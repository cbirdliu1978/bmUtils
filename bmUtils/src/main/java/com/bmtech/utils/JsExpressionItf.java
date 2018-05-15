package com.bmtech.utils;

import javax.script.ScriptException;

public interface JsExpressionItf {

	public Object eval() throws ScriptException;

	public boolean accept(Object... args) throws ScriptException;

	public Object eval(Object... args) throws ScriptException;

	public Object eval(String jsVarName, Object value) throws ScriptException;

}
