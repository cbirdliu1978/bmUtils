package com.bmtech.utils;

import java.io.File;
import java.io.IOException;

import javax.script.ScriptException;
import javax.script.SimpleScriptContext;

import com.bmtech.utils.io.FileGet;

/**
 * this is a bridge between jvm and nashorn js for light weight interactive. the
 * parameter for construction is a raw function innered script (as
 * _the_expresion_inputted), and this class will automaticall wrapp it as <b>
 * <br>
 * <br>
 * var __js_FuncInjected_ =function (){
 * 
 * <br>
 * //wrapped expression here<br>
 * &nbsp; _the_expresion_inputted
 * 
 * 
 * <br>
 * }<br>
 * <br>
 * // call the wrap function<br>
 * __js_FuncInjected_(); </b>
 * 
 * <br>
 * <br>
 * 
 * @author qq948
 *
 */
public class JsFunc extends JsExpression {

	public JsFunc(File file, SimpleScriptContext ctt) throws IOException, ScriptException {
		this(FileGet.getStr(file), ctt);
	}

	public JsFunc(File file) throws IOException, ScriptException {
		this(FileGet.getStr(file));
	}

	public JsFunc(String exp) throws ScriptException {
		this(exp, null);
	}

	public JsFunc(String exp, SimpleScriptContext ctt) throws ScriptException {
		super("var __js_FuncInjected_ =function (){\n \n" + besurehasReturnStatement(exp)
				+ ";\n }\n __js_FuncInjected_();", ctt, false);
	}

	public void load(Class<?> clazz) throws ScriptException {
		String txt = "importPackage(" + clazz.getPackage().getName() + ")";
		this.engine.eval(txt);

	}

	public String jsFunction() {
		return this.getExp();
	}

	private static String besurehasReturnStatement(String str) throws ScriptException {
		str = str.trim();
		String[] strs = str.split("\n");
		if (strs.length > 0) {

			if (strs[strs.length - 1].contains("return")) {
				return str;
			} else {
				if (strs.length == 1) {
					return "return " + str;
				}
			}
		}
		throw new ScriptException("no return statement found in last line");
	}
}
