package com.bmtech.utils.io.diskMerge;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import com.bmtech.utils.Misc;
import com.bmtech.utils.log.LogHelper;

public class MRTool {
	static File tmpBase = null;

	private static File getTmpBaseDir() throws IOException {
		if (tmpBase != null) {
			return tmpBase;
		}
		String str = System.getProperty("mr-tmpDir");
		File tmpBase;
		if (str == null) {
			tmpBase = new File("./mr-tmp/" + new SimpleDateFormat("yyyy-MM-dd_HH_mm_ss").format(System.currentTimeMillis()));
		} else {
			tmpBase = new File(str);
		}
		if (!tmpBase.exists()) {
			if (!tmpBase.mkdirs()) {
				throw new IOException("can not create file " + tmpBase);
			}
		}
		Misc.del(tmpBase.listFiles());
		MRTool.tmpBase = tmpBase;
		return tmpBase;
	}

	public static File getTmpFile(String suffix) throws IOException {
		File tmpBase = getTmpBaseDir();
		while (true) {
			int vi = Misc.randInt(0, 1000000000);
			File ret = new File(tmpBase, "mrtmp-" + vi + suffix);
			if (ret.exists()) {
				continue;
			} else {
				if (!ret.createNewFile()) {
					throw new IOException("can not create file " + tmpBase);
				}
				ret.deleteOnExit();
				return ret;
			}
		}
	}

	public static void combin(File toFile, RecordFactory fac, File... mlst) throws Exception {
		MOut mout = fac.getWriter(toFile);
		try {
			for (File f : mlst) {
				PeekableQueue rr = fac.getReader(f);
				try {
					while (rr.peek() != null) {
						MRecord mr = rr.take();
						mout.offer(mr);
					}
				} finally {
					rr.close();
				}
			}
		} finally {
			mout.close();
		}
	}

	public static void mergeTo(File out, RecordFactory fac, int mergeFactor, File... mlst) throws Exception {
		MOut mout = fac.getWriter(out);
		try {
			mergeTo(mout, fac, mergeFactor, mlst);
		} finally {
			mout.close();
		}
	}

	private static void mergeTo(MOut mout, RecordFactory fac, int mergeFactor, File... mlst) throws Exception {

		if (mlst.length == 0) {
			return;
		}
		int grp = mlst.length / mergeFactor;
		if (0 != mlst.length % mergeFactor) {
			grp++;
		}
		if (grp > 1) {
			File tmpFiles[] = new File[grp];
			try {
				for (int x = 0; x < grp; x++) {
					File tmpFile = getTmpFile(".mrg");
					tmpFiles[x] = tmpFile;

					int start = x * mergeFactor;
					int end = start + mergeFactor;
					if (end > mlst.length) {
						end = mlst.length;
					}
					File tmplst[] = new File[end - start];
					System.arraycopy(mlst, start, tmplst, 0, end - start);

					directMergeTo(tmpFile, fac, tmplst);

				}
				mergeTo(mout, fac, mergeFactor, tmpFiles);
			} finally {
				for (File x : tmpFiles) {
					if (x != null) {
						x.delete();
					}
				}
			}

		} else {
			directMergeTo(mout, fac, mlst);
		}

	}

	public static void directMergeTo(File out, RecordFactory fac, File... files) throws Exception {
		MOut mout = fac.getWriter(out);
		try {
			directMergeTo(mout, fac, files);
		} finally {
			mout.close();
		}
	}

	private static void directMergeTo(MOut mout, RecordFactory fac, File... files) throws Exception {

		PeekableQueue[] mlst = new PeekableQueue[files.length];
		for (int x = 0; x < mlst.length; x++) {

			mlst[x] = fac.getReader(files[x]);
		}
		Comparator<MRecord> cmp = fac.getComparator();
		try {
			while (true) {
				MRecord mm = null, tmp;
				for (PeekableQueue mq : mlst) {
					tmp = mq.peek();
					if (tmp == null)
						continue;

					if (mm == null) {
						mm = tmp;
					} else {
						int ret = cmp.compare(mm, tmp);
						if (ret > 0) {
							mm = tmp;
						}
					}
				}
				if (mm == null) {
					break;
				}
				for (PeekableQueue mq : mlst) {
					tmp = mq.peek();
					if (tmp == null)
						continue;
					if (cmp.compare(mm, tmp) == 0) {
						mq.take();
						mout.offer(tmp);
					}
				}
			}
		} finally {
			mout.close();
			for (int x = 0; x < mlst.length; x++) {
				mlst[x].close();
			}
		}

	}

	public static int reduce(File toFile, File fromFile, RecordFactory fac, MReduceSelect cmp) throws Exception {
		MOut mout = fac.getWriter(toFile);
		try {
			PeekableQueue q = fac.getReader(fromFile);
			try {
				return reduce(mout, q, cmp);
			} finally {
				q.close();
			}
		} finally {
			mout.close();
		}
	}

	public static Iterator<List<MRecord>> reduceIterator(final File fromFile, final RecordFactory fac) throws Exception {

		Iterator<List<MRecord>> itr = new Iterator<List<MRecord>>() {
			List<MRecord> ret;
			final Comparator<MRecord> cmp = fac.getComparator();
			PeekableQueue q = fac.getReader(fromFile);

			@Override
			public boolean hasNext() {
				try {
					ret = new ArrayList<MRecord>();
					MRecord tmpl = q.peek();
					if (tmpl == null)
						return false;
					while (true) {
						MRecord crt = q.peek();
						if (crt == null)
							break;
						if (tmpl == crt || cmp.compare(tmpl, crt) == 0) {
							ret.add(crt);
							q.take();
						} else {
							break;
						}
					}
					return ret.size() > 0;
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}

			@Override
			public List<MRecord> next() {
				return ret;
			}

			@Override
			public void finalize() {
				this.q.close();
			}
		};
		return itr;
	}

	/**
	 * 
	 * @param cmp
	 * @param q
	 * @param toFile
	 *            the file reduce to
	 * @throws Exception
	 */
	private static int reduce(MOut mout, PeekableQueue q, MReduceSelect cmp) throws Exception {

		MRecord last = null;
		int ret = 0;
		try {
			while (true) {
				MRecord crt = q.take();
				if (crt == null) {
					if (last != null) {
						mout.offer(last);
						ret++;
					}
					break;
				}
				if (last == null) {
					last = crt;
				} else {
					if (cmp.equals(last, crt)) {
						last = cmp.select(last, crt);
					} else {
						mout.offer(last);
						last = crt;
						ret++;
					}
				}
			}
		} finally {
			mout.close();
		}
		return ret;
	}

	public static void sortFile(File toFile, File fromFile, RecordFactory fac, int bufferSize) throws Exception {
		if (fac.getComparator() == null) {
			throw new Exception("comparator not set for " + fac);
		}
		PeekableQueue q = fac.getReader(fromFile);
		try {
			MOut out = fac.getWriter(toFile);// new MFileOut(toFile);
			try {
				sortFile(out, q, fac, bufferSize);
			} finally {
				out.close();
			}
		} finally {
			q.close();
		}
	}

	/**
	 * 
	 * @param c
	 *            MRecord's comparator
	 * @param q
	 *            peekableQueue
	 * @param toFile
	 *            sort result
	 * @param bufferSize
	 * @param recordClass
	 *            record
	 * @throws Exception
	 */
	static final LogHelper log = new LogHelper("MRTool");

	private static void sortFile(MOut toFile, PeekableQueue q, RecordFactory fac, int bufferSize) throws Exception {
		MRecord crt = null;
		ArrayList<MRecord> lst = new ArrayList<MRecord>();
		ArrayList<File> fls = new ArrayList<File>();
		try {
			while (true) {
				crt = q.take();
				if (crt == null) {
					if (lst.size() > 0) {

						File tmpFile = getTmpFile(".srt");
						log.debug("sort with tmp-file %s, now has tmp-file %s", tmpFile.getAbsolutePath(), fls.size());
						Collections.sort(lst, fac.getComparator());
						fls.add(tmpFile);
						writeLstToFile(lst, tmpFile, fac);
						log.debug("sort ok for  tmp-file %s ", tmpFile.getAbsolutePath());

					}
					break;
				}
				lst.add(crt);
				if (lst.size() >= bufferSize) {
					Collections.sort(lst, fac.getComparator());
					File tmpFile = getTmpFile(".srt");
					fls.add(tmpFile);
					writeLstToFile(lst, tmpFile, fac);

					lst.clear();
				}
			}

			File[] fs = new File[fls.size()];
			fls.toArray(fs);
			MRTool.mergeTo(toFile, fac, 32, fs);
		} finally {
			for (File f : fls) {
				f.delete();
			}
		}
	}

	private static void writeLstToFile(ArrayList<MRecord> lst, File file, RecordFactory fac) throws Exception {
		MOut mo = fac.getWriter(file);
		for (MRecord me : lst) {
			mo.offer(me);
		}
		mo.close();
	}

	public static void diff(File toFile, File from, File cmpFile, RecordFactory fac) throws Exception {
		PeekableQueue qFrom = fac.getReader(from);
		PeekableQueue qToCompare = fac.getReader(cmpFile);
		Comparator<? super MRecord> cmp = fac.getComparator();
		MOut out = fac.getWriter(toFile);
		try {
			boolean qcmpIsEnd = false;
			while (true) {
				MRecord su = qFrom.take();
				if (su == null) {
					break;
				}
				if (qcmpIsEnd) {
					out.offer(su);
				} else {
					boolean find = false;
					while (true) {
						MRecord suTmp = qToCompare.peek();
						if (suTmp == null) {
							qcmpIsEnd = true;
							break;
						} else {
							int v = cmp.compare(su, suTmp);
							if (v == 0) {
								find = true;
								break;
							} else if (v < 0) {
								break;
							} else {
								qToCompare.take();
							}
						}
					}
					if (!find) {
						out.offer(su);
					}
				}
			}
		} finally {
			out.close();
			qFrom.close();
			qToCompare.close();
		}
	}

}
