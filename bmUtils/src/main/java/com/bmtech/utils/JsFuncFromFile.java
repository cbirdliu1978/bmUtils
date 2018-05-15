package com.bmtech.utils;

import java.io.File;
import java.io.IOException;

import javax.script.ScriptException;
import javax.script.SimpleScriptContext;

import com.bmtech.utils.log.LogHelper;

public class JsFuncFromFile implements JsExpressionItf {
	private JsFunc func;
	private File file;
	private long lastModified = 0;
	private LogHelper log;

	public JsFuncFromFile(File file) throws IOException, ScriptException {
		this(file, 2);
	}

	public JsFuncFromFile(File file, int checkFileItvSecond) throws IOException, ScriptException {
		this.file = file;
		log = new LogHelper("JsFuncFromFile-sec." + checkFileItvSecond);
		this.initFunc();
		updateFuncThread(checkFileItvSecond);
	}

	private void updateFuncThread(int checkFileItvSecond) {
		if (checkFileItvSecond > 0) {
			Thread t = new Thread() {
				@Override
				public void run() {
					while (true) {
						try {
							Thread.sleep(checkFileItvSecond * 1000);
							if (needUpdate()) {
								log.debug("need update jsFunc from file %s", file);
								initFunc();
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}

			};
			t.setDaemon(true);
			t.start();
		}
	}

	private synchronized void initFunc() throws IOException, ScriptException {
		if (this.needUpdate()) {
			log.info("updating new js function");
			SimpleScriptContext ctt = null;
			if (this.func != null) {
				ctt = this.func.getCtt();
			}
			this.func = new JsFunc(file, ctt);
			this.lastModified = file.lastModified();
		}
	}

	@Override
	public Object eval() throws ScriptException {
		return this.func.eval();
	}

	@Override
	public boolean accept(Object... args) throws ScriptException {
		return this.func.accept(args);
	}

	@Override
	public Object eval(Object... args) throws ScriptException {
		return this.func.eval(args);
	}

	@Override
	public Object eval(String jsVarName, Object value) throws ScriptException {
		return this.func.eval(jsVarName, value);
	}

	private boolean needUpdate() {
		return lastModified != file.lastModified();
	}
}
