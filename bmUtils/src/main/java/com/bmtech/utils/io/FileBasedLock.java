package com.bmtech.utils.io;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;

import com.bmtech.utils.log.LogHelper;


/**
 * file lock to prevent reentrant
 * @author Fisher@Beiming
 *
 */
public class FileBasedLock{
	public final File file;
	FileLock fLock;
	FileChannel fileChannel;
	RandomAccessFile raFile;
	public FileBasedLock(File fileToLock) throws IOException{
		this.file = fileToLock;
		if(!file.exists()){
			file.createNewFile();
		}
	}
	/**
	 * try to lock on this 
	 * @return
	 * @throws IOException 
	 */
	public synchronized void tryLock() throws IOException{
		if(!file.canWrite()) {
			throw new IOException("can not write file: " + file.getCanonicalFile());
		}

		raFile = new RandomAccessFile(file, "rw");

		fileChannel = raFile.getChannel();
		if(fileChannel == null){
			throw new IOException("can not lock file becouse can not get channel for " + file);
		}
		fLock = fileChannel.tryLock();
		if(fLock == null){
			throw new IOException("can not lock file becouse can not get flock for " + file);
		}
	}
	public synchronized void releaseLock() throws IOException{
		if(fLock != null){
			if(fLock.isValid()){
				fLock.release();
			}
			fLock = null;
		}
		if(fileChannel != null){
			if(fileChannel.isOpen()){
				fileChannel.close();
			}
			fileChannel = null;
		}
	}
	public void finalize(){
		try {
			if(fLock != null){
				LogHelper.log.warn("file lock is released by VM : %s", file.getName());
			}
			this.releaseLock();
		} catch (IOException e) {
			LogHelper.log.error(e, "when finalize FileLock for %s", file);
		}
	}
	public void block() {
		while(true) {
			try {
				Thread.sleep(60 * 60  * 1000);
			} catch (InterruptedException e) {
				LogHelper.log.error(e, "when block FileLock for %s", file);
			}
		}
	}
	public File getFile() {
		return file;
	}
	/**
	 * not allow root '/' ?
	 * @param dir
	 * @return
	 * @throws IOException
	 */
	public static FileBasedLock tryLockDirAndGetTheLockObject(File dir) throws IOException{
		if(!dir.isDirectory()){
			throw new IOException("can ONLY lock dir! arg error:" + dir);
		}
		File par = dir.getParentFile();
		File fileToLock = new File(par, dir.getName().replace('/', '.').replace('\\', '.') + ".fblock");
		FileBasedLock lock = new FileBasedLock(fileToLock);
		lock.tryLock();
		return lock;
	}
}
