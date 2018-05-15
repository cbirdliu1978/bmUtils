package com.bmtech.utils.bmfs;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import com.bmtech.utils.bmfs.MFileWriter.MFileOutputStream;
import com.bmtech.utils.bmfs.util.AssureInputStream.AssureFailException;
import com.bmtech.utils.bmfs.util.MFileFormatErrorException;
import com.bmtech.utils.bmfs.util.ReadProtocol;
import com.bmtech.utils.bmfs.util.WriteProtocol;
import com.bmtech.utils.io.FileBasedLock;
import com.bmtech.utils.io.InputStreamCombin;
import com.bmtech.utils.io.LineReader;
import com.bmtech.utils.io.LineWriter;
import com.bmtech.utils.log.LogHelper;

public class MDir {
	private static Map<File, MDir> dirMap = new ConcurrentHashMap<File, MDir>();
	public static final String mdirUriPrefix = "mdir://";
	private static Thread shutdown;

	private static void besureClose(MDir mDir) {
		dirMap.remove(mDir.getLocalDir());
		mDir.closeDir();
	}

	private synchronized static void registrShutDownHook() {
		if (shutdown != null) {
			shutdown = new Thread() {
				@Override
				public void run() {
					for (MDir dir : dirMap.values()) {
						if (dir.isWrite) {
							dir.closeDir();
						}
					}
				}

			};
			Runtime.getRuntime().addShutdownHook(shutdown);
		}
	}

	public synchronized void close() {
		MDir.closeMDir(this);
	}

	public synchronized static void closeMDir(MDir mdir) {
		if (mdir == null) {
			return;
		}
		mdir.closeDir();
		MDir toRemove = dirMap.remove(mdir.getLocalDir());
		if (toRemove == null) {
			LogHelper.log.debug("can not remove dir %s, not registed", mdir.getLocalDir());
		} else {
			if (toRemove != mdir) {
				LogHelper.log.debug("not save dir for %s, %s is %s", mdir.getLocalDir(), mdir, toRemove);
			}
		}
	}

	public static MDir open(File dir) throws IOException {
		return makeMDir(dir, false);
	}

	public static MDir open4Write(File dir) throws IOException {
		return makeMDir(dir, true);
	}

	public static MDir makeMDir(File dirPath, boolean write) throws IOException {
		MDir dir = new MDir(dirPath, write);
		dirMap.put(dirPath, dir);
		if (dir.isWrite) {
			registrShutDownHook();
		}
		return dir;
	}

	class MFileAlreadyExistsException extends IOException {
		private static final long serialVersionUID = 1L;

		MFileAlreadyExistsException(String clause) {
			super(clause);
		}
	}

	public static enum AccessPriv {
		READ, WRITE
	};

	private final File localDir;
	private final File delFile;
	private final File indexFile;
	private static final int FILE_BUCKET_SIZE = 64 * 1024;
	private static final int FILE_BUCKET_NUMBER = 1024;
	private final FileBasedLock fLock;
	private final MFile[][] files = new MFile[FILE_BUCKET_NUMBER][];
	private final Map<String, MFile> nameMap = new HashMap<String, MFile>();
	private final LineWriter delWriter;
	private final WriteProtocol dataOutStream;
	private final FileOutputStream indexWriter;
	private final AtomicInteger fsIdSeq = new AtomicInteger();
	private final File dataFile;
	private static final byte[] magic = new byte[] { 0x4d, 0x44, 0x49, 0x52 };
	private static final byte entryPrefix = magic[0];
	private final byte[] writeBuffer;
	private static final int preHeadLen = MFile.marginLen + 4;
	private long currentLength = 0;

	private boolean isWriteClose = false;

	private int[] locateFsId(int fsid) {
		return new int[] { fsid / FILE_BUCKET_SIZE, fsid % FILE_BUCKET_SIZE };
	}

	final LogHelper log;
	final boolean isWrite;

	private MDir(File dir) throws IOException {
		this(dir, true);
	}

	private MDir(File dir, boolean write) throws IOException {
		this.isWrite = write;
		this.localDir = dir;
		if (!getLocalDir().exists()) {
			getLocalDir().mkdirs();
		}
		log = new LogHelper("Mdir:" + dir.getName());
		dataFile = new File(getLocalDir(), "mfArc.data");
		if (!dataFile.exists()) {
			if (!dataFile.createNewFile()) {
				throw new IOException("can not create delete dir " + dataFile);
			}
		}
		this.currentLength = this.dataFile.length();
		indexFile = new File(getLocalDir(), "mfArc.index");
		if (!indexFile.exists()) {
			if (!indexFile.createNewFile()) {
				throw new IOException("can not create index dir " + indexFile);
			}
		}

		delFile = new File(getLocalDir(), "mfArc.delete");
		if (!delFile.exists()) {
			if (!delFile.createNewFile()) {
				throw new IOException("can not create delete dir " + delFile);
			}
		}
		if (write) {
			fLock = new FileBasedLock(new File(getLocalDir(), "fLock"));
			fLock.tryLock();
			indexWriter = new FileOutputStream(this.indexFile, true);
			dataOutStream = new WriteProtocol(new FileOutputStream(this.dataFile, true));
			delWriter = new LineWriter(delFile, true);
			this.writeBuffer = new byte[64 * 1024];

		} else {
			indexWriter = null;
			dataOutStream = null;
			fLock = null;
			delWriter = null;
			this.writeBuffer = null;
		}

		besureIndexFile();
		log.debug("loading deleted ");
		Set<Integer> deleted = loadDeleted();
		log.debug("loading files");
		long fileShouldLen = loadFiles(deleted);
		log.debug("load ok! now size " + this.size());
		if (fileShouldLen != this.currentLength) {
			if (this.canWrite()) {
				boolean autoRepair = !"false".equals(System.getProperty("MdirAutoRepair"));
				if (autoRepair) {
					List<MFile> mfList = this.getMFiles();
					log.fatal("try repairing %s", this);
					for (MFile mfile : mfList) {
						if (mfile.getOffset() + mfile.getLength() + preHeadLen > fileShouldLen) {
							log.fatal("deleting %s", mfile);
							try {
								this.delete(mfile);
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}

				} else {
					throw new MFileFormatErrorException(
							"filelen not match! mdir corrupt! should be repair. requre " + fileShouldLen + ", real is "
									+ this.currentLength + ". miss " + (fileShouldLen - this.currentLength) + "bytes");
				}
			} else {
				log.fatal(
						"file size not match! maybe corrupt. Now in read model, ignore repair. YOU MAY GET 'MFileFormatErrorException' while reading mdir");
			}
		}

		log.debug("init finished");
	}

	public synchronized void fsyn() throws IOException {
		if (dataOutStream != null && !isWriteClose) {
			this.dataOutStream.flush();
			this.delWriter.flush();
		}
	}

	void besureIndexLocationExists(int locate[]) {
		if (files[locate[0]] == null) {// be sure locate exists
			files[locate[0]] = new MFile[FILE_BUCKET_SIZE];
		}
	}

	public MFile getFileById(int fsid) {
		MFile[] arr = this.files[fsid / FILE_BUCKET_SIZE];
		if (arr == null)
			return null;
		return arr[fsid % FILE_BUCKET_SIZE];
	}

	synchronized int locateId(String name) throws IOException {
		if (this.nameMap.containsKey(name)) {
			throw new MFileAlreadyExistsException("has already exists file " + name);
		}
		// find the chunk
		int ret = fsIdSeq.addAndGet(1);
		int locate[] = this.locateFsId(ret);
		if (locate[0] >= files.length) {
			throw new IOException("can not locate new FileId " + ret);
		}
		besureIndexLocationExists(locate);
		assert files[locate[0]][locate[1]] == null;
		this.nameMap.put(name, null);
		return ret;
	}

	private synchronized void closeDir() {
		try {
			this.fsyn();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		if (this.defaultWriter != null) {
			this.delWriter.close();
		}
		if (dataOutStream != null) {
			this.delWriter.close();
			try {
				this.indexWriter.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			try {
				this.dataOutStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				this.fLock.releaseLock();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		this.isWriteClose = true;
	}

	@Override
	public void finalize() {
		MDir.besureClose(this);
	}

	private void besureIndexFile() throws IOException {
		if (!this.indexFile.exists()) {
			this.indexFile.createNewFile();
		}
		if (this.indexFile.length() == 0) {
			FileOutputStream fos = new FileOutputStream(this.indexFile);
			fos.write(magic);
			fos.close();
		}
		if (indexFile.length() < 4) {
			throw new IOException("MDir magic is error, too short!");
		}
		checkMagic();
	}

	private void checkMagic() throws IOException {
		this.openIndex().close();
	}

	private FileInputStream openIndex() throws IOException {
		FileInputStream fis = new FileInputStream(indexFile);
		ReadProtocol proc = new ReadProtocol(fis);
		for (byte b : magic) {
			byte b2 = proc.readByte();
			if (b != b2) {
				throw new IOException("MDir magic is error");
			}
		}
		return fis;
	}

	private long loadFiles(Set<Integer> deleted) throws IOException {
		FileInputStream fis = this.openIndex();
		int maxId = 0;
		long maxLen = 0;
		try {
			MFileIndexIterator itr = new MFileIndexIterator(fis);
			while (itr.hasNext()) {
				MFile file = itr.next();
				long fileLen = file.getOffset() + file.getLength() + MFile.marginLen + 4;
				if (fileLen > maxLen)
					maxLen = fileLen;
				try {
					if (deleted.contains(file.fsId)) {
						continue;
					}
					this.mountIndex(file, true);
				} finally {
					maxId = maxId > file.fsId ? maxId : file.fsId;
				}
			}
			this.fsIdSeq.set(maxId);
		} finally {
			fis.close();
		}
		return maxLen;
	}

	private Set<Integer> loadDeleted() throws IOException {
		LineReader lr = new LineReader(this.delFile);
		try {
			HashSet<Integer> set = new HashSet<Integer>();
			while (lr.hasNext()) {
				String line = lr.next();
				line = line.trim();
				if (line.length() == 0) {
					continue;
				}
				if (!line.endsWith("`")) {
					throw new IOException("delFile corupt!");
				}
				line = line.substring(0, line.length() - 1);
				int x = Integer.parseInt(line);
				set.add(x);
			}
			return set;
		} finally {
			lr.close();
		}

	}

	@Override
	public String toString() {
		return mdirUriPrefix + getLocalDir().getName() + "/";
	}

	public boolean canWrite() {
		return fLock != null;
	}

	private void checkWriteAccess() throws IOException {
		if (!canWrite()) {
			throw new IOException("no priv writing to " + this);
		}
	}

	boolean isMount(MFile mfile) {
		if (null == mfile)
			return false;
		int locate[] = this.locateFsId(mfile.fsId);
		return isMount(mfile, locate);
	}

	boolean isMount(MFile mfile, int locate[]) {
		if (files[locate[0]] == null) {
			return false;
		}
		if (mfile == files[locate[0]][locate[1]] && files[locate[0]][locate[1]] != null) {
			return true;
		}
		return false;
	}

	synchronized void mount(MFile mfile, InputStream ips) throws IOException {
		checkWriteAccess();
		if (this.isMount(mfile)) {
			throw new IOException("already mount file " + mfile);
		}
		byte[] b = mfile.marginBytes();
		this.dataOutStream.write(b);
		this.dataOutStream.writeI32(mfile.fsId);
		int len = 0;
		while (true) {
			int x = ips.read(this.writeBuffer);
			if (x == -1) {
				break;
			}
			this.dataOutStream.write(this.writeBuffer, 0, x);
			len += x;
		}
		dataOutStream.flush();
		mfile.setOffset(this.currentLength);
		mfile.setLength(len);
		this.currentLength += len + preHeadLen;

		this.writeIndex(mfile);
		mountIndex(mfile, true);
	}

	private void mountIndex(MFile mfile, boolean forceCheckSameFileNameExists) throws IOException {

		int locate[] = this.locateFsId(mfile.fsId);
		if (isMount(mfile, locate)) {
			return;
		}
		assert files[locate[0]][locate[1]] == null;
		if (forceCheckSameFileNameExists) {
			assert this.nameMap.get(mfile.getName()) == null;
		}
		this.besureIndexLocationExists(locate);
		this.files[locate[0]][locate[1]] = mfile;
		this.nameMap.put(mfile.getName(), mfile);
	}

	public synchronized boolean delete(MFile mf) throws IOException {
		if (mf == null)
			return false;
		checkWriteAccess();
		int[] locate = this.locateFsId(mf.fsId);
		this.files[locate[0]][locate[1]] = null;
		MFile tmp = this.nameMap.remove(mf.getName());
		if (tmp == null) {
			return false;
		} else {
			delWriter.writeLine(mf.fsId + "`");
			delWriter.flush();
			return true;
		}
	}

	public synchronized boolean deleteFile(String fileName) throws IOException {
		MFile mfile = this.getMFileByName(fileName);
		return this.delete(mfile);

	}

	public synchronized MFile getMFileByName(String name) {
		return this.nameMap.get(name);
	}

	final MFile readMFileIndex(InputStream ips) throws IOException {
		ReadProtocol proc = new ReadProtocol(ips);
		try {
			if (entryPrefix != proc.readByte()) {
				throw new IOException("bad format Exception! entryPrifix error! ");
			}
		} catch (AssureFailException e) {
			return null;
		}
		byte flag = proc.readByte();
		short verify = proc.readI16();
		int fsId = proc.readI32();
		long createTime = proc.readI64();
		long offset = proc.readI64();
		long length = proc.readI64();
		String str = proc.readString();
		MFile file = new MFile(this, str, fsId, flag, createTime);
		file.setOffset(offset);
		file.setLength(length);
		if (file.veryfyPend() != verify) {
			throw new IOException("can not verify mfile when read!");
		}
		return file;

	}

	private synchronized final void writeIndex(MFile mfile) throws IOException {
		WriteProtocol proc = new WriteProtocol(indexWriter);
		proc.writeByte(entryPrefix);
		proc.writeByte(mfile.flagByte());
		proc.writeI16(mfile.veryfyPend());
		proc.writeI32(mfile.fsId);
		proc.writeI64(mfile.getCreateTime());
		proc.writeI64(mfile.getOffset());
		proc.writeI64(mfile.getLength());
		proc.writeString(mfile.getName());
		indexWriter.flush();
	}

	public synchronized int size() {
		return this.nameMap.size();
	}

	public synchronized MFile createMFileIfPossible(String name) throws IOException {
		if (name == null) {
			name = "fsIdSeq." + (this.fsIdSeq.get() + 1);
		}
		try {
			return new MFile(name, this);
		} catch (MFileAlreadyExistsException e) {
			return null;
		}
	}

	public MFileReaderIterator openReader() throws IOException {
		return new MFileReaderIterator(this);
	}

	public MFileWriter openWriter() {
		return new MFileWriter(this);
	}

	private MFileWriter defaultWriter;

	public synchronized MFileWriter getDefaultWriter() {
		if (this.defaultWriter == null) {
			this.defaultWriter = this.openWriter();
		}
		return this.defaultWriter;
	}

	private MFile deleteAndCreateNewOne(MFile mFile) throws IOException {
		this.delete(mFile);
		return this.createMFileIfPossible(mFile.getName());
	}

	private MFile deleteAndCreateNewOne(String fileName) throws IOException {
		MFile mf = this.getMFileByName(fileName);
		if (mf == null) {
			mf = this.createMFileIfPossible(fileName);
		} else {
			mf = deleteAndCreateNewOne(mf);
		}
		return mf;
	}

	public synchronized MFile replaceFile(String fileName, MFileWriter writer, InputStream ips) throws IOException {
		MFile mf = deleteAndCreateNewOne(fileName);
		return addFile(mf, writer, ips);
	}

	public synchronized MFile addFile(String fileName, MFileWriter writer, InputStream ips) throws IOException {
		MFile mFile = this.createMFileIfPossible(fileName);
		if (mFile == null) {
			throw new IOException("mfile alread exists:" + fileName);
		}
		return this.addFile(mFile, writer, ips);
	}

	public synchronized MFile replaceFile(MFile mFile, InputStream ips) throws IOException {
		mFile = this.deleteAndCreateNewOne(mFile);
		return this.addFile(mFile, ips);
	}

	public synchronized MFile addFile(MFile mFile, InputStream ips) throws IOException {
		MFileWriter writer = this.getDefaultWriter();
		return this.addFile(mFile, writer, ips);
	}

	public synchronized MFile replaceFile(MFile mFile, byte[] bs) throws IOException {
		mFile = this.deleteAndCreateNewOne(mFile);
		return addFile(mFile, bs);
	}

	public synchronized MFile addFile(MFile mFile, byte[] bs) throws IOException {
		MFileWriter writer = this.getDefaultWriter();
		return this.addFile(mFile, writer, new ByteArrayInputStream(bs));
	}

	public synchronized MFile replaceFile(MFile mFile, MFileWriter writer, InputStream ips) throws IOException {
		mFile = this.deleteAndCreateNewOne(mFile);
		return this.addFile(mFile, writer, ips);
	}

	public synchronized MFile addFile(MFile mFile, MFileWriter writer, InputStream ips) throws IOException {
		boolean gzipOut = false;

		MFileOutputStream mFileOutputStream;
		if (gzipOut) {
			mFileOutputStream = writer.openMFileGzip(mFile);
		} else {
			mFileOutputStream = writer.openMFile(mFile);
		}
		try {
			mFileOutputStream.write(ips);
		} finally {
			mFileOutputStream.mountAndClose();
		}
		return mFile;
	}

	public synchronized MFile addFile(String fileName, InputStream ips) throws IOException {
		return this.addFile(fileName, this.getDefaultWriter(), ips);
	}

	public synchronized MFile replaceFile(String fileName, InputStream ips) throws IOException {
		MFile mf = this.deleteAndCreateNewOne(fileName);
		return this.addFile(mf, ips);
	}

	public synchronized MFile addFile(byte[] bs) throws IOException {
		return this.addFile((String) null, bs);
	}

	public synchronized MFile addFile(String fileName, byte[] bs) throws IOException {
		return this.addFile(fileName, this.getDefaultWriter(), new ByteArrayInputStream(bs));
	}

	public synchronized MFile replaceFile(String fileName, byte[] bs) throws IOException {
		MFile mf = this.deleteAndCreateNewOne(fileName);
		return this.addFile(mf, bs);
	}

	public synchronized MFile replaceFile(File currentFile) throws IOException {
		String name = currentFile.getName();
		MFile mf = this.getMFileByName(name);
		if (mf != null) {
			this.delete(mf);
		}
		return addFile(currentFile);
	}

	public synchronized MFile addFile(File currentFile) throws IOException {
		FileInputStream fis = new FileInputStream(currentFile);
		try {
			return this.addFile(currentFile.getName(), fis);
		} finally {
			fis.close();
		}
	}

	class MFileIndexIterator implements Iterator<MFile> {

		private MFile now;
		private final InputStream ips;

		public MFileIndexIterator(InputStream ips) throws IOException {
			this.ips = ips;
			now = readMFileIndex(ips);
		}

		@Override
		public boolean hasNext() {
			return now != null;
		}

		@Override
		public MFile next() {
			MFile ret = now;
			try {
				now = readMFileIndex(ips);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
			return ret;
		}

		@Override
		public void remove() {
			throw new RuntimeException("remove() not support!");
		}
	}

	/**
	 * if mfile is not mount, let its name reuseable
	 * 
	 * @param mfile
	 */
	public synchronized void abort(MFile mfile) {
		if (this.nameMap.get(mfile.getName()) == null) {
			this.nameMap.remove(mfile.getName());
		}
	}

	public synchronized List<MFile> listFiles() {
		return this.getMFiles();
	}

	public synchronized List<MFile> getMFiles() {

		Collection<MFile> collection = this.nameMap.values();
		List<MFile> lst = new ArrayList<MFile>(collection.size());
		lst.addAll(collection);
		Collections.sort(lst, new Comparator<MFile>() {

			@Override
			public int compare(MFile o1, MFile o2) {
				return o1.fsId - o2.fsId;
			}
		});
		return lst;

	}

	public File getLocalDir() {
		return localDir;
	}

	public String filePath(MFile mFile) {
		return this.toString() + mFile.getName();
	}

	public synchronized MFile addFile(InputStreamCombin ips) throws IOException {
		return this.addFile((String) null, ips);
	}

	public File getDataFile() {
		return dataFile;
	}

	public static void main(String[] args) throws IOException {
		MDir mdir = MDir.open4Write(new File("/old/test/mdir"));
		System.out.println(mdir.size());
		MFile mf = mdir.getMFileByName("mdir");
		System.out.println(mf.baseInfo());
		File f = new File("/WiFi_Log.txt");
		mdir.replaceFile(f);
		mdir.replaceFile(f);
		mdir.replaceFile(f);
		mdir.replaceFile(f);
	}

	public void addFile(String txt) throws IOException {
		this.addFile(null, txt);
	}

	public void addFile(String name, String txt) throws IOException {
		InputStream ips = new ByteArrayInputStream(txt.getBytes());
		this.addFile(name, ips);
	}

}
