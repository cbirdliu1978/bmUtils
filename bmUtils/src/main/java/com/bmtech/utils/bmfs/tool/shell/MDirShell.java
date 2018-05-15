package com.bmtech.utils.bmfs.tool.shell;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.bmtech.utils.Charsets;
import com.bmtech.utils.Consoler;
import com.bmtech.utils.Misc;
import com.bmtech.utils.ZipUnzip;
import com.bmtech.utils.bmfs.MDir;
import com.bmtech.utils.bmfs.MFile;
import com.bmtech.utils.bmfs.MFileReader;
import com.bmtech.utils.io.LineWriter;

public class MDirShell implements Runnable {

	public final Map<String, DebugCmd> cmdMap = new HashMap<String, DebugCmd>();

	private boolean quit = false;
	private LineWriter historyWirter;
	protected final SimpleDateFormat sdf;
	MDir mdir;

	public MDirShell(String path) throws IOException {
		this(new File(path));
	}

	public MDirShell() throws IOException {
		this(Consoler.readString("mdir path"));
	}

	public MDirShell(File mdirPath) throws IOException {
		this(MDir.makeMDir(mdirPath, false));
	}

	public MDirShell(MDir mdir) throws IOException {
		this.sdf = new SimpleDateFormat("yyyy-MM-dd.HH:mm:ss.SSS");
		this.mdir = mdir;
		this.putCmd();
	}

	private void reload(File dir, boolean writeMode) throws IOException {
		if (dir.exists() && dir.isDirectory()) {
			MDir old = mdir;
			old.close();

			if (writeMode) {
				mdir = MDir.open4Write(dir);
			} else {
				mdir = MDir.open(dir);
			}

			Consoler.println("open in ‘%s’ model for dir %s", writeMode ? "Write" : "read", dir);
		} else {
			System.out.println("not a directory: " + dir);
		}
	}

	protected void putCmd() {
		this.putCmd("del", new DebugCmd("delete [fileName]", this) {
			@Override
			public Object run(String paras[]) throws IOException {
				if (paras.length != 1) {
					System.out.println("expect 1 param, but get " + paras.length);
					return "false";
				}
				String fileName = paras[0];
				return mdir.deleteFile(fileName);
			}
		});
		this.putCmd("quit", new DebugCmd("退出Shell", this) {
			@Override
			public Object run(String paras[]) {
				quit = true;
				return quit;
			}
		});
		this.putCmd("size", new DebugCmd("文件数", this) {
			@Override
			public Object run(String paras[]) {
				int size = mdir.size();
				System.out.println("files number " + size);
				return size;
			}
		});
		this.putCmd("list", new DebugCmd("list [offset] [len]", this) {
			@Override
			public Object run(String paras[]) {
				int minMargin = 20;
				int offset = 0;
				int len = -10;
				if (paras.length == 1) {
					len = Misc.parseInt(paras[0], minMargin);
				} else if (paras.length == 2) {
					offset = Misc.parseInt(paras[0], 0);
					if (offset < 0)
						offset = 0;
					len = Misc.parseInt(paras[1], minMargin);

				}
				List<MFile> files = mdir.getMFiles();

				int from, to;
				if (len >= 0) {
					from = offset;
					to = offset + len;
				} else {
					if (offset == 0)
						offset = files.size();

					from = offset + len;
					to = offset;
				}
				from = from < 0 ? 0 : from;
				to = to > files.size() ? files.size() : to;

				for (int x = from; x < to; x++) {
					System.out.println(" " + (x + 1) + "	" + files.get(x).baseInfo());
				}
				return files;
			}
		});
		DebugCmd getId = new DebugCmd("fsid [charset] fileName", this) {

			@Override
			public Object run(String[] paras) throws Exception {
				return getFsIdFile(paras, false);
			}
		};
		// this.putCmd("fsid", getId);
		this.putCmd("id", getId);
		DebugCmd getIdUnzip = new DebugCmd("fsid [charset] fileName", this) {

			@Override
			public Object run(String[] paras) throws Exception {
				return getFsIdFile(paras, true);
			}
		};
		this.putCmd("fsidUnzip", getIdUnzip);
		this.putCmd("idUnzip", getIdUnzip);

		this.putCmd("get", new DebugCmd("get [charset] fileName", this) {

			@Override
			public Object run(String[] paras) throws Exception {
				return getFile(paras, false);

			}
		});
		this.putCmd("like", new DebugCmd("like regExp", this) {

			@Override
			public Object run(String[] paras) throws Exception {
				if (paras.length < 1) {
					System.out.println("use as like regEx");
					return null;
				}
				Pattern p = Pattern.compile(paras[0]);
				System.out.println("search name like " + p);
				List<MFile> lst = new ArrayList<>();
				mdir.getMFiles().forEach((mFile) -> {
					Matcher m = p.matcher(mFile.getName());
					if (m.find()) {
						System.out.println(" " + mFile.baseInfo());
						lst.add(mFile);
					}

				});

				return lst;

			}
		});
		this.putCmd("dir", new DebugCmd("print local dir", this) {

			@Override
			public Object run(String[] paras) throws Exception {
				Consoler.println("current dir : %s", mdir.getLocalDir());
				return mdir.getLocalDir();
			}
		});
		this.putCmd("getUnzip", new DebugCmd("getUnzip [charset] fileName", this) {

			@Override
			public Object run(String[] paras) throws Exception {
				return getFile(paras, true);

			}

		});
		this.putCmd("reload", new DebugCmd(
				"reload [-w] [mdirPath]\n if -w use write priv,[mdirPath] the path to load, default using current file\n use command dir to get detail",
				this) {

			@Override
			public Object run(String[] paras) throws Exception {

				if (paras.length == 2) {
					boolean write = false;
					boolean paraOk = true;
					if (paras[0].equalsIgnoreCase("-w")) {
						write = true;
					} else if (paras[0].equalsIgnoreCase("-r")) {
						write = false;
					} else {
						System.out.println("unknown mode " + paras[0]);
						paraOk = false;
					}
					if (paraOk) {
						File dir = new File(paras[1]);
						reload(dir, write);
						return true;
					}
					return false;
				} else if (paras.length == 1) {
					File dir = new File(paras[0]);
					reload(dir, false);
					return true;
				} else if (paras.length == 0) {
					File dir = mdir.getLocalDir();
					reload(dir, false);
					return true;
				} else {
					System.out.println("expect 1 or 2 paras, but get " + paras.length);
					return false;
				}

			}

		});

		this.putCmd("help", new DebugCmd("usage", this) {
			@Override
			public Object run(String paras[]) {

				if (paras.length == 0) {
					for (Entry<String, DebugCmd> e : this.env.cmdMap.entrySet()) {
						System.out.println("\n\t" + e.getKey() + ":");
						DebugCmd cmd = env.getCommand(e.getKey());
						System.out.println("\t\t" + cmd.help());

					}
				} else {
					String cmd = paras[0];
					DebugCmd cmd2 = this.env.getCommand(cmd);
					if (cmd != null) {
						System.out.println("\n\t" + cmd + ":");
						System.out.println("\t\t" + cmd2.help());
					} else {
						System.out.println("没有找到命令:" + cmd);
					}
				}
				return true;
			}
		});
	}

	protected DebugCmd getCommand(String key) {
		return this.cmdMap.get(key.toLowerCase());
	}

	protected void putCmd(String strs, DebugCmd r) {
		putCmd(new String[] { strs }, r);
	}

	protected void putCmd(String[] strs, DebugCmd r) {
		for (String str : strs) {
			cmdMap.put(str.trim().toLowerCase(), r);
		}
	}

	protected void putCmd(DebugCmd r, String... strs) {
		putCmd(strs, r);
	}

	public Object getFsIdFile(String paras[], boolean unzip) throws Exception {
		String fileId = null;
		Charset cs = null;
		if (paras.length < 1) {
			System.out.println("没有指定fsId");
			return null;
		} else if (paras.length == 1) {
			fileId = paras[0];

		} else {
			try {
				cs = Charset.forName(paras[0]);
			} catch (Exception e) {
				System.out.println("unkown charset " + paras[0]);
				;
			}
			fileId = paras[1];
		}
		int fsId = Misc.parseInt(fileId, -1);
		if (fsId == -1) {
			throw new Exception("not a number " + fileId);
		}
		return this.doFsId(fsId, unzip, cs);
	}

	public Object getFile(String paras[], boolean unzip) throws IOException {
		String fileName = null;
		Charset cs = null;
		if (paras.length < 1) {
			System.out.println("没有指定文件名");
			return null;
		} else if (paras.length == 1) {
			fileName = paras[0];

		} else {
			try {
				cs = Charset.forName(paras[0]);
			} catch (Exception e) {
				System.out.println("unkown charset " + paras[0]);
				;
			}
			fileName = paras[1];
		}
		return doFile(fileName, unzip, cs);
	}

	String doFile(MFile mf, boolean unzip, Charset cs) throws IOException {
		Consoler.println("fileName %s, fsId %s, %sBytes, createTime %s", mf.getName(), mf.fsId, mf.getLength(),
				sdf.format(mf.getCreateTime()));
		if (!Consoler.confirm("printDetail?", true)) {
			return "";
		}
		Consoler.print("content:");
		MFileReader reader = mf.openReader();
		byte[] bs;
		try {
			bs = reader.getBytes();

		} finally {
			reader.close();
		}

		if (unzip) {
			bs = ZipUnzip.unGzip(bs);
		}
		if (cs == null) {
			cs = Charsets.UTF8_CS;
		}
		String txt = new String(bs, cs);
		System.out.println("file is : " + mf.baseInfo());
		System.out.println("character number " + txt.length());
		System.out.println();
		System.out.println(txt);
		System.out.println();
		return txt;

	}

	String doFsId(int fsId, boolean unzip, Charset cs) throws IOException {
		MFile mf = mdir.getFileById(fsId);
		if (mf == null) {
			System.out.println("no file has fsId '" + fsId + "'");
			return null;
		} else {
			return doFile(mf, unzip, cs);
		}
	}

	String doFile(String fileName, boolean unzip, Charset cs) throws IOException {
		MFile mf = mdir.getMFileByName(fileName);
		if (mf == null) {
			System.out.println("没有找到文件'" + fileName + "'");
			return null;
		} else {
			return doFile(mf, unzip, cs);
		}
	}

	@Override
	public void run() {
		int lineNo = 0;

		while (true) {
			if (quit) {
				break;
			}
			++lineNo;
			String cmdStr = Consoler.readString("\nmShell").trim();
			if (cmdStr.length() == 0) {
				continue;
			}
			String[] cmds = cmdStr.split("\n");
			for (String cmd : cmds) {
				cmd(cmd, lineNo);
			}
		}
		dieMessage();

	}

	public void cmd(String cmdStr) {
		cmd(cmdStr, 0);
	}

	public void cmd(String cmd, int lineNo) {

		cmd = cmd.trim();
		if (this.getHistoryWirter() != null) {
			try {
				String time = sdf.format(System.currentTimeMillis());
				this.getHistoryWirter().writeLine("//\tdebug-" + lineNo + " " + time);
				this.getHistoryWirter().writeLine(cmd);
				this.getHistoryWirter().flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		try {
			execute(cmd, lineNo);
		} catch (Exception exc) {
			{
				System.out.println(exc.getLocalizedMessage());
				exc.printStackTrace();

			}
			// return null;
		}

	}

	protected void dieMessage() {
		System.out.println("quit debug");

	}

	private Object execute(String cmd, int line) throws Exception {
		int pos = cmd.indexOf(" ");
		int posTab = cmd.indexOf("\t");
		if (posTab != -1) {
			if (pos == -1) {
				pos = posTab;
			} else {
				if (pos > posTab) {
					pos = posTab;
				}
			}
		}
		String prvCmd = cmd;
		String paras[] = new String[0];
		if (pos != -1) {
			prvCmd = cmd.substring(0, pos);
			paras = cmd.substring(pos).trim().replace('\t', ' ').replaceAll(" +", " ").split(" ");
		}
		DebugCmd r = cmdMap.get(prvCmd.toLowerCase().trim());
		if (r == null) {
			System.out.println("unknown command '" + prvCmd + "'");
			return null;
		} else {
			return r.run(paras);
		}
	}

	public void setHistoryWirter(LineWriter historyWirter) {
		this.historyWirter = historyWirter;
	}

	public LineWriter getHistoryWirter() {
		return historyWirter;
	}

	public static void main(String[] args) throws IOException {
		String pArg = null;
		if (args.length > 0)
			pArg = args[0];
		String path = Consoler.readString("mdir path", pArg);

		File dir = new File(path);
		if (!dir.exists()) {
			System.out.println("not exists " + dir.getCanonicalPath());
			return;
		}
		MDir mdir = MDir.makeMDir(dir, false);
		MDirShell shell = new MDirShell(mdir);
		shell.run();
	}
}
