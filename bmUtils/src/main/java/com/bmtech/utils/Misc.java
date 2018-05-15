package com.bmtech.utils;

import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import com.bmtech.utils.io.FileGet;
import com.bmtech.utils.log.BmtLogger;
import com.bmtech.utils.log.L;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Misc {
	public static String bytesToStr(byte[] bs) {
		StringBuilder sb = new StringBuilder();
		for (byte b : bs) {
			sb.append((char) ((b & 0x0f) + 65));
			sb.append((char) (((b & 0xf0) >> 4) + 65));
		}
		return sb.toString();
	}

	public static boolean isBlank(String str) {
		return str == null || str.length() == 0;
	}

	public static byte[] strToBytes(String a) {

		int len = a.length() / 2;
		byte[] b = new byte[len];
		int pos;
		for (int i = 0; i < len; i++) {
			pos = i * 2;
			b[i] = (byte) (a.charAt(pos) - 65 + ((a.charAt(pos + 1) - 65) << 4));
		}
		return b;
	}

	public static boolean isWinSys() {
		Properties prop = System.getProperties();

		String os = prop.getProperty("os.name");
		if (os != null) {
			return os.toLowerCase().startsWith("windows");
		}
		return false;
	}



	public static String substring(String str, String prefix, String sufix) {
		return getSubString(str, prefix, sufix);
	}

	/**
	 * 
	 * @param str
	 * @param prefix
	 *            perfix, null be viewed as from the begining
	 * @param sufix
	 *            suffix, null be view as to the end
	 * @return null returned if nothing found
	 */
	public static String getSubString(String str, String prefix, String sufix) {
		if (str == null)
			return null;
		int st, ed, stLen;
		if (prefix == null) {
			st = 0;
			stLen = 0;
		} else {
			st = str.indexOf(prefix);
			stLen = prefix.length();
		}
		if (st == -1)
			return null;

		if (sufix == null)
			ed = str.length();
		else
			ed = str.indexOf(sufix, st + stLen);
		if (ed == -1)
			return null;
		return str.substring(st + stLen, ed);
	}

	public static ArrayList<String> subs(String str, char prefix, char suffix) {
		ArrayList<String> lst = new ArrayList<String>();
		int pos = 0, end;
		while (true) {
			pos = str.indexOf(prefix, pos);
			if (pos == -1)
				break;
			pos += 1;
			end = str.indexOf(suffix, pos);
			if (end == -1)
				break;
			lst.add(str.substring(pos, end));
			pos = end + 1;
		}
		return lst;
	}

	public static double atod(String str) {
		int start = -1;
		int st = 0;
		if (str == null)
			throw new NumberFormatException(str);
		for (; st < str.length(); st++) {
			char c = str.charAt(st);
			if (c >= '0' && c <= '9') {
				break;
			}
		}
		if (st >= str.length())
			throw new NumberFormatException(str);
		start = st;
		st += 1;
		for (; st < str.length(); st++) {
			char c = str.charAt(st);
			if (c >= '0' && c <= '9') {
				continue;
			} else
				break;
		}
		int ed = st;

		if (st < str.length()) {
			if (str.charAt(st) == '.') {
				st++;
				for (; st < str.length(); st++) {
					char c = str.charAt(st);
					if (c >= '0' && c <= '9') {
						continue;
					} else
						break;
				}
			}
		}
		if (st - ed > 1) {
			ed = st;
		}
		return Double.parseDouble(str.substring(start, ed));
	}

	/**
	 * works just like c api atoi, search for good formatted slice and convert to int
	 * 
	 * @param str
	 * @return
	 */

	public static int atoi(String str) throws NumberFormatException {
		int start = -1;
		int st = 0;
		if (str == null)
			throw new NumberFormatException(str);
		for (; st < str.length(); st++) {
			char c = str.charAt(st);
			if (c >= '0' && c <= '9') {
				break;
			}
		}
		if (st >= str.length())
			throw new NumberFormatException(str);
		start = st;
		st += 1;
		for (; st < str.length(); st++) {
			char c = str.charAt(st);
			if (c >= '0' && c <= '9') {
				continue;
			} else
				break;
		}
		return Integer.parseInt(str.substring(start, st));
	}

	static String fbdFileName_rewriteTo[] = { "CON_", "PRN_", "AUX_", "NUL_", "COM1_", "COM2_", "COM3_", "COM4_", "COM5_", "COM6_", "COM7_",
			"COM8_", "COM9_", "LPT1_", "LPT2_", "LPT3_", "LPT4_", "LPT5_", "LPT6_", "LPT7_", "LPT8_", "LPT9_", "con_", "prn_", "aux_",
			"nul_", "com1_", "com2_", "com3_", "com4_", "com5_", "com6_", "com7_", "com8_", "com9_", "lpt1_", "lpt2_", "lpt3_", "lpt4_",
			"lpt5_", "lpt6_", "lpt7_", "lpt8_", "lpt9_", };
	static String fbdFileName[] = { "CON", "PRN", "AUX", "NUL", "COM1", "COM2", "COM3", "COM4", "COM5", "COM6", "COM7", "COM8", "COM9",
			"LPT1", "LPT2", "LPT3", "LPT4", "LPT5", "LPT6", "LPT7", "LPT8", "LPT9", "con", "prn", "aux", "nul", "com1", "com2", "com3",
			"com4", "com5", "com6", "com7", "com8", "com9", "lpt1", "lpt2", "lpt3", "lpt4", "lpt5", "lpt6", "lpt7", "lpt8", "lpt9", };

	/**
	 * format org to a validate file name
	 * 
	 * @param org
	 * @return
	 */
	public static String toFileNameFormat(String org) {
		return formatFileName(org);
	}

	public static String formatFileName(String org) {
		return toFileNameFormat(org, '~');
	}

	public static String formatFileName(String org, char c) {
		return toFileNameFormat(org, c);
	}

	public static String toFileNameFormat(String org, char rep) {
		if (org == null || org.length() == 0) {
			return System.currentTimeMillis() + "";
		}
		char cs[] = org.toCharArray();
		char c;
		int len = cs.length;
		for (int i = 0; i < len; i++) {
			c = cs[i];
			if (c == '\\' || c == '/' || c == ':' || c == '*' || c == '?' || c == '"' || c == '|' || c == '<' || c == '>') {
				cs[i] = rep;
			}
		}
		String ret = new String(cs);
		for (int x = 0; x < fbdFileName.length; x++) {
			ret = ret.replace(fbdFileName[x], fbdFileName_rewriteTo[x]);
		}
		return ret;
	}

	public static void shutDownMachine(int seconds) throws IOException {
		Runtime rt = Runtime.getRuntime();
		String cmd = "c:\\windows\\system32\\shutdown   -s   -t  " + seconds;
		rt.exec(cmd);
	}

	public static byte[] read(InputStream ips) throws IOException {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		byte[] buf = new byte[4096];

		while (true) {
			int tRead = ips.read(buf, 0, buf.length);
			if (tRead == -1) {
				break;
			}
			bos.write(buf, 0, tRead);
		}

		return bos.toByteArray();
	}

	/**
	 * try to fill the bs, return the bytes readed
	 * 
	 * @param ips
	 * @param bs
	 * @return
	 * @throws IOException
	 */
	public static int tryFillBuffer(InputStream ips, byte[] bs) throws IOException {
		return tryFillBuffer(ips, bs, 0, bs.length);
	}

	public static int tryFillBuffer(InputStream ips, byte[] bs, int offset, int len) throws IOException {
		int readed = 0;

		while (true) {
			int tRead = ips.read(bs, readed + offset, len - readed);
			if (tRead == -1) {
				if (readed != 0)
					break;
				readed = -1;
				break;
			}
			readed += tRead;
			if (readed >= len)
				break;
		}

		return readed;
	}

	public static File createSameDir(File f, String suffix) {
		File ret = new File(f.getParentFile(), f.getName() + "." + suffix);
		ret.mkdirs();
		return ret;
	}

	public static File createSameFile(File f, String suffix) throws IOException {
		return createSameFile(f, suffix, false);
	}

	public long creationTime(File file) throws IOException {
		BasicFileAttributes view = Files.getFileAttributeView(file.toPath(), BasicFileAttributeView.class).readAttributes();
		return view.creationTime().toMillis();
	}

	public static File createSameFile(File f, String suffix, boolean createFile) throws IOException {
		File ret = new File(f.getParentFile(), f.getName() + "." + suffix);
		ret.getParentFile().mkdirs();
		if (createFile && !ret.exists()) {
			ret.createNewFile();
		}
		return ret;
	}

	public static String timeStr() {
		return timeStr(System.currentTimeMillis());
	}

	public static String timeStr(long time) {
		return new SimpleDateFormat("yyyy-MM-dd.HH.mm.ss.SSS").format(time);
	}

	public static void del(File file) {
		if (file.isDirectory()) {
			BmtLogger.instance().log("remove %s...", file);
			File[] fs = file.listFiles();
			for (int i = 0; i < fs.length; i++) {
				del(fs[i]);
			}
			file.delete();
		} else
			file.delete();
	}

	public static int randInt(int from, int to) {
		int itv = to - from;
		if (itv == 0)
			return from;
		int it = (int) Math.floor((Math.random() * (itv + 1)));
		return (from + it);
	}

	public static void del(File[] listFiles) {
		for (File f : listFiles) {
			Misc.del(f);
		}

	}

	public static void sleep(int ms) {
		try {
			Thread.sleep(ms);
		} catch (Exception e) {
			throw new RuntimeException(e);// e.printStackTrace();
		}
	}

	public static void copyFile(File from, File to) throws IOException {
		byte[] buf = new byte[64 * 1024];
		FileInputStream fis = new FileInputStream(from);
		try {
			if (!to.exists()) {
				to.createNewFile();
			} else {
				if (to.isDirectory()) {
					throw new IOException("can not copy to a directory:" + to);
				}
			}
			FileOutputStream fos = new FileOutputStream(to);
			try {

				while (true) {
					int read = fis.read(buf);
					if (read < 0) {
						break;
					}
					fos.write(buf, 0, read);
				}
			} finally {
				fos.close();
			}
		} finally {
			fis.close();
		}
	}

	public static File besureDirExists(String dirStr) throws IOException {
		return besureDirExists(new File(dirStr));
	}

	public static File besureFileExists(File file) throws IOException {
		if (!file.exists()) {
			besureDirExists(file.getParentFile());
			try {
				file.createNewFile();
			} catch (Exception e) {
				throw new IOException("can not create file " + file, e);
			}
		}
		return file;
	}

	public static File besureDirExists(File dir) throws IOException {
		if (!dir.exists()) {
			try {
				dir.mkdirs();
			} catch (Exception e) {
				throw new IOException("can not create dir " + dir, e);
			}
		}
		if (!dir.exists() || dir.isFile()) {
			throw new IOException("can not create dir:" + dir.getAbsolutePath());
		}
		return dir;
	}

	public static void saveToDisk(File toSave, String txt) throws IOException {
		saveToDisk(toSave, txt.getBytes());
	}

	public static void saveToDisk(File toSave, byte[] txt) throws IOException {
		FileOutputStream fos = new FileOutputStream(toSave);
		try {
			fos.write(txt);
		} finally {
			fos.close();
		}
	}

	public static String toString(Object o) {
		MRToStringStyle style = new MRToStringStyle();
		StringBuffer sb = new StringBuffer();
		style.append(sb, "", o, true);
		return sb.substring(1);
	}

	public static String toJson(Object obj) {
		return new String(toJsonBytes(obj), Charsets.UTF8_CS);
	}

	public static byte[] toJsonBytes(Object obj) {

		try {
			ObjectMapper m = new ObjectMapper();
			ByteArrayOutputStream stm = new ByteArrayOutputStream();
			JsonGenerator gen = m.getFactory().createGenerator(stm);
			gen.writeObject(obj);
			gen.close();
			return stm.toByteArray();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

	}

	public static <T> T parseJson(String json, TypeReference<T> t) throws JsonParseException, JsonMappingException, IOException {
		ObjectMapper mapper = new ObjectMapper();
		T ee = mapper.readValue(json, t);
		return ee;
	}

	public static <T> T parseJson(String json, Class<T> t) throws JsonParseException, JsonMappingException, IOException {
		ObjectMapper mapper = new ObjectMapper();
		T ee = mapper.readValue(json, t);
		return ee;
	}

	public static <T> T parseJson(byte[] json, TypeReference<T> t) throws JsonParseException, JsonMappingException, IOException {
		ObjectMapper mapper = new ObjectMapper();
		T ee = mapper.readValue(json, t);
		return ee;
	}

	public static <T> T parseJson(byte[] json, Class<T> t) throws JsonParseException, JsonMappingException, IOException {
		ObjectMapper mapper = new ObjectMapper();
		T ee = mapper.readValue(json, t);
		return ee;
	}

	public static int parseInt(String num, int dft) {
		try {
			return Integer.parseInt(num);
		} catch (Exception e) {
			return dft;
		}
	}

	public static long parseLong(String num, long dft) {
		try {
			return Integer.parseInt(num);
		} catch (Exception e) {
			return dft;
		}
	}

	public static double parseDouble(String num, double dft) {
		try {
			return Double.parseDouble(num);
		} catch (Exception e) {
			return dft;
		}
	}

	public static int doubleCompare(double d1, double d2, int percis) {
		return (int) (d1 * percis + 0.5) - (int) (d2 * percis + 0.5);
	}

	public static double round(double d, int i) {
		return Math.round(d * i) / (double) i;
	}

	public static Object reflect(Object o, String field) throws NoSuchFieldException, Exception {
		Field fld = o.getClass().getDeclaredField(field);
		fld.setAccessible(true);
		return fld.get(o);
	}

	public static Field[] getClassField(Class<?> clazz) throws Exception {
		Map<String, Field> map = new HashMap<>();
		Class<?> i = clazz;
		while (i != null && i != Object.class) {
			Field[] fx = i.getDeclaredFields();
			for (Field f : fx) {
				if (map.containsKey(f.getName()))
					continue;
				f.setAccessible(true);

				map.put(f.getName(), f);
			}
			i = i.getSuperclass();
		}
		Field fs[] = new Field[map.size()];
		map.values().toArray(fs);
		return fs;
	}

	public static Field getClassField(Class<?> clazz, String fieldName) throws Exception {

		Class<?> i = clazz;
		while (i != null && i != Object.class) {
			Field[] fx = i.getDeclaredFields();
			for (Field f : fx) {
				if (!f.getName().equals(fieldName))
					continue;
				f.setAccessible(true);

				return f;
			}
			i = i.getSuperclass();
		}

		throw new RuntimeException("not found field " + fieldName + " in " + clazz);

	}

	public static Object getFieldValue(Object obj, String fieldName) throws Exception {

		return getClassField(obj.getClass(), fieldName).get(obj);
	}

	public static double div(double fenzi, double fenmu) {
		return fenzi / fenmu;
	}

	public static Set<String> packages(String jarFilePath) throws IOException {
		return packages(new File(jarFilePath));
	}

	public static Set<String> packages(File jarsFile) throws IOException {

		JarFile jarFile = new JarFile(jarsFile);

		Iterator<JarEntry> itr = jarFile.stream().iterator();
		Set<String> set = new HashSet<String>();
		while (itr.hasNext()) {
			JarEntry entry = itr.next();
			String name = entry.getName();
			if (name.startsWith("META-INF"))
				continue;
			if (name.endsWith("/"))
				continue;
			int pos = name.lastIndexOf("/");
			if (pos == -1) {
				continue;
			}
			String dir = name.substring(0, pos).replace('/', '.');
			set.add(dir);

		}
		jarFile.close();
		return set;
	}

	public static void setClipText(String tempText) {
		StringSelection editText = new StringSelection(tempText);
		Toolkit.getDefaultToolkit().getSystemClipboard().setContents(editText, null);
	}

	public static String getClipText() {
		Transferable t = Toolkit.getDefaultToolkit().getSystemClipboard().getContents(null);
		try {
			if (t != null && t.isDataFlavorSupported(DataFlavor.stringFlavor)) {
				String x = ((String) t.getTransferData(DataFlavor.stringFlavor));
				if (x == null)
					x = "";
				return x;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return "";
	}

	public byte[] readBytes(File file) throws IOException {
		return FileGet.getBytes(file);
	}

	public String readString(File file) throws IOException {
		return FileGet.getStr(file);
	}

	public static int nowMinute() {
		return Calendar.getInstance().get(Calendar.MINUTE);
	}

	public static int nowHour() {
		return Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
	}

	public static int nowSeconds() {
		return Calendar.getInstance().get(Calendar.SECOND);
	}

	public static int nowDay() {
		String s = new SimpleDateFormat("yyyyMMdd").format(System.currentTimeMillis());
		return Integer.parseInt(s);
	}

	public static int time(long time) {
		return (int) (time % (24 * 60 * 60 * 1000));
	}

	public static Thread startThread(RunnableExceptionable r) {
		Thread t = new Thread() {
			@Override
			public void run() {

				try {
					r.run();
				} catch (Exception e) {
					L.f(e, "thread interrupt! got exception");
				}
			}
		};
		t.setDaemon(true);
		t.start();
		return t;
	}

	/**
	 * start a endless daemon thread, {@link DaemonLoopThread}
	 * 
	 * @param r
	 * @return
	 */
	public static DaemonLoopThread startDaemonLoopThread(RunnableExceptionable r) {
		DaemonLoopThread dh = new DaemonLoopThread() {
			@Override
			protected boolean singleRound() throws Exception {
				r.run();
				return false;
			}
		};
		dh.start();
		return dh;
	}

	public interface RunnableExceptionable {
		public void run() throws Exception;

	}

	public static void main(String[] args) {
		int from = 1, to = 9;
		int arr[] = new int[9];
		for (int x = 0; x < 10000; x++) {
			int v = Misc.randInt(from, to);
			arr[v - 1]++;
		}
		for (int x = 0; x < arr.length; x++) {
			System.out.println((x + 1) + " " + arr[x]);
		}
	}

	public static final char[] hexChar = new char[] { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };

	public static char int2HexChar(int v) {
		if (v >= 0 && v <= 9) {
			return (char) ('0' + v);
		} else
			return (char) ('A' + (v - 10));
	}

	public static short hexChar2Int(char c) {
		if (c >= '0' && c <= '9') {
			return (short) (c - '0');
		}
		if (c >= 'A' && c <= 'F')
			return (short) (10 + c - 'A');

		throw new RuntimeException("unknown value define " + c + "( ascii value " + (int) c + ")");

	}

	public static void waitFinish(ExecutorService exe) throws java.lang.InterruptedException {
		exe.shutdown();
		while (true) {
			exe.awaitTermination(Integer.MAX_VALUE, TimeUnit.DAYS);
			if (exe.isTerminated()) {
				break;
			}
		}

	}

	public static boolean prob(double d) {
		return Math.random() < d;
	}

	public static final String format(String why, Object... args) {
		if (args == null || args.length == 0) {
			return why;
		}
		try {
			return String.format(why, args);
		} catch (Exception e) {
			e.printStackTrace();
			StringBuilder sb = new StringBuilder();
			sb.append(why);
			for (Object x : args) {
				sb.append(", ");
				sb.append(x);
			}
			return sb.toString();
		}
	}

	public static void throwNewRuntimeException(Throwable e) throws RuntimeException {
		throw new RuntimeException(e);
	}

	public static void throwNewRuntimeException(String format, Object... args) throws RuntimeException {
		String line = format(format, args);
		throw new RuntimeException(line);
	}

	public static void submitWithException(ExecutorService exe, Runnable r) {
		Misc.submit(exe, new RunnableExceptionable() {

			@Override
			public void run() throws Exception {
				r.run();
			}

		});
	}

	public static void submit(ExecutorService exe, RunnableExceptionable r) {
		exe.submit(new Runnable() {
			@Override
			public void run() {
				try {
					r.run();
				} catch (Throwable e) {
					e.printStackTrace(System.out);
					L.f(e, "for exe %s, runnable = %s", exe, r);
				}
			}
		});

	}

	public static void block() {
		while (true) {
			Misc.sleep(Integer.MAX_VALUE);
		}
	}

	public static double freeMemoryMB() {
		return Misc.toMB(Runtime.getRuntime().freeMemory());
	}

	/**
	 * Returns the total amount of memory in the Java virtual machine
	 * 
	 * @return
	 */
	public static double totalMemoryMB() {
		return Misc.toMB(Runtime.getRuntime().totalMemory());
	}

	public static double maxMemoryMB() {
		return Misc.toMB(Runtime.getRuntime().maxMemory());
	}

	public static double stillCanUseMemoryMB() {
		return toMB(Runtime.getRuntime().maxMemory() - Runtime.getRuntime().totalMemory() + Runtime.getRuntime().freeMemory());
	}

	public static double toMB(long var) {
		return Misc.round((double) var / 1024 / 1024, 100);
	}

	public static String memoryInfo() {
		return ("maxMemory " + maxMemoryMB() + "MB\t") + ("totalMemory " + totalMemoryMB() + "MB\t")
				+ ("freeMemory " + freeMemoryMB() + "MB\tstill can use " + Misc.stillCanUseMemoryMB() + "MB");

	}

	public static double toDouble(Object eval) {
		if (eval instanceof Integer) {
			return (int) eval;
		} else if (eval instanceof Long) {
			return (Long) eval;
		} else if (eval instanceof Double) {
			return (double) eval;
		} else if (eval instanceof Float) {
			return (Float) eval;
		} else if (eval instanceof Short) {
			return (Short) eval;
		} else
			throw new RuntimeException("can not convert to number : " + eval);
	}

	public static String printErrorAsString(Throwable e) {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		e.printStackTrace(new PrintStream(bos));
		return new String(bos.toByteArray());
	}

	public static List<File> listFileWithoutHidden(File dir) {
		if (!dir.isDirectory()) {
			throw new RuntimeException("not a directory " + dir);
		}
		File[] fs = dir.listFiles();
		List<File> lst = new ArrayList<>(fs.length);
		for (File f : fs) {
			if (f.getName().startsWith("."))
				continue;
			else
				lst.add(f);
		}
		lst.sort((a, b) -> {
			return a.getName().compareTo(b.getName());
		});
		return lst;
	}

}