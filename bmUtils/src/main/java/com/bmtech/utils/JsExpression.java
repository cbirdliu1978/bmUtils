package com.bmtech.utils;

import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptException;
import javax.script.SimpleScriptContext;

public class JsExpression implements JsExpressionItf {
	/**
	 * importPackage( java.lang, java.io,java.nio, java.util, java.net, java.util.concurrent, java.sql, java.nio.charset, java.text,
	 * java.util.concurrent.atomic, java.util.regex, java.util.Map);
	 * 
	 * @return
	 */

	protected ScriptEngine engine;

	private final String exp;
	private SimpleScriptContext ctt;
	private int maxDoNum = 500 * 10000;
	private int crtDo = 0;
	private boolean initImportPackage;

	public JsExpression(String exp, SimpleScriptContext ctt, boolean initImportPackage) throws ScriptException {
		this.exp = exp;
		this.initImportPackage = initImportPackage;
		if (ctt == null) {
			ctt = new SimpleScriptContext();
		}
		this.ctt = ctt;
		this.reInitEngine();
	}

	public JsExpression(String exp, boolean initImportPackage) throws ScriptException {
		this(exp, null, initImportPackage);
	}

	public synchronized void setValue(String jsVarName, Object value) {
		getCtt().setAttribute(jsVarName, value, ScriptContext.ENGINE_SCOPE);
	}

	@Override
	public synchronized Object eval() throws ScriptException {
		crtDo++;
		if (crtDo > this.maxDoNum) {
			crtDo = 0;
			reInitEngine();
		}
		return engine.eval(this.exp);
	}

	@Override
	public boolean accept(Object... args) throws ScriptException {
		return (boolean) eval(args);
	}

	@Override
	public synchronized Object eval(Object... args) throws ScriptException {
		for (int x = 0; x < args.length; x++) {
			if (x == 0)
				setValue("$", args[x]);
			setValue("$" + (1 + x), args[x]);
		}
		return eval();
	}

	@Override
	public synchronized Object eval(String jsVarName, Object value) throws ScriptException {
		setValue(jsVarName, value);
		return eval();
	}

	private synchronized void reInitEngine() throws ScriptException {
		this.engine = null;
		this.engine = JsInvok.getEngine(this.getCtt(), initImportPackage);
	}

	@Override
	public String toString() {
		return exp;
	}

	public String getExp() {
		return exp;
	}

	public SimpleScriptContext getCtt() {
		return ctt;
	}

	public void debug() {
		while (true) {
			String line = Consoler.readLine("debug:");
			if (line.equals("//q") || line.equals("quit")) {

				break;
			}
			try {
				Object ret = this.eval(line);
				System.out.println(ret);
			} catch (ScriptException e) {
				e.printStackTrace();
			}
		}
	}

}
