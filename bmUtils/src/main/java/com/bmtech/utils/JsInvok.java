package com.bmtech.utils;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.script.Invocable;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

public class JsInvok {
	static List<String> dftPackages = new ArrayList<String>();
	static String[] dftLoad = new String[] { "com.bmtech", "com.bmtech.utils", "com.bmtech.utils.bmfs",
			"com.bmtech.utils.bmfs.tool", "com.bmtech.utils.bmfs.util", "com.bmtech.utils.c2j",
			"com.bmtech.utils.c2j.cTypes", "com.bmtech.utils.charDet", "com.bmtech.utils.counter",
			"com.bmtech.utils.http", "com.bmtech.utils.http.fileDump", "com.bmtech.utils.http.itrCrawl",
			"com.bmtech.utils.io", "com.bmtech.utils.io.diskMerge", "com.bmtech.utils.log", "com.bmtech.utils.rds",
			"com.bmtech.utils.rds.convert", "com.bmtech.utils.ruledSegment", "com.bmtech.utils.ruledSegment.affix",
			"com.bmtech.utils.security", "com.bmtech.utils.segment", "com.bmtech.utils.superTrimer",
			"com.bmtech.utils.systemWatcher", "com.bmtech.utils.systemWatcher.innerAction", "com.bmtech.utils.tcp",
			"com.bmtech.util.nashorn", "com.bmtech.nashorn.fsql.expression", "com.bmtech.nashorn.fsql.expression.ast",
			"com.bmtech.utils.bmfs.tool.shell", "java.lang", "java.io", "java.nio", "java.util", "java.net",
			"java.util.concurrent", "java.sql", "java.nio.charset", "java.text", "java.util.concurrent.atomic",
			"java.util.regex", "java.util.Map", };
	static {
		for (String x : dftLoad) {
			dftPackages.add(x);
		}
	}

	private static List<String> getPackagesNames() {
		Package[] pkg = Package.getPackages();
		List<String> packageNames = new ArrayList<>();
		packageNames.addAll(dftPackages);
		for (Package p : pkg) {
			String name = p.getName();
			for (String prf : dftPackages) {
				if (name.startsWith(prf)) {
					packageNames.add(name);
				}
			}
		}
		return packageNames;
	}

	public static void initEngineWithCompatAndImport(ScriptEngine engine) throws ScriptException {

		engine.eval("load('nashorn:mozilla_compat.js');");
		StringBuilder sb = new StringBuilder();
		sb.append("importPackage(");
		String x = getPackagesNames().toString();
		sb.append(x.substring(1, x.length() - 1));
		sb.append(");");
		String imp = sb.toString();
		// LogHelper.log.debug("js default imports %s", imp);
		engine.eval(imp);
	}

	public static final String defaultFunction = "javaCall";

	public static Object invoke(File jsFile, Object... args) throws Exception {
		return invoke(jsFile, defaultFunction, args);
	}

	public static ScriptEngine loadScriptEngine(File jsFile) throws ScriptException, IOException {
		ScriptEngineManager manager = new ScriptEngineManager();
		ScriptEngine engine = manager.getEngineByName("nashorn");

		FileReader reader = new FileReader(jsFile); // 执行指定脚本
		engine.eval(reader);
		reader.close();
		return engine;
	}

	public static Object invoke(File jsFile, String functionName, Object... args)
			throws ScriptException, IOException, NoSuchMethodException {
		ScriptEngine engine = loadScriptEngine(jsFile);
		Invocable invoke = (Invocable) engine; // 调用merge方法，并传入两个参数
		Object obj = invoke.invokeFunction(functionName, args);
		return obj;
	}

	public static ScriptEngine getEngine(boolean initImportPackage) throws ScriptException {
		return getEngine(null, initImportPackage);
	}

	public static ScriptEngine getEngine(ScriptContext context, boolean initImportPackage) throws ScriptException {
		ScriptEngineManager manager = new ScriptEngineManager();
		ScriptEngine engine = manager.getEngineByName("nashorn");
		if (context != null)
			engine.setContext(context);
		if (initImportPackage)
			JsInvok.initEngineWithCompatAndImport(engine);
		return engine;
	}
}
