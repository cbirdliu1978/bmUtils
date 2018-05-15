package com.bmtech.utils.restoreable;

public abstract class RItem {
	private String iName;

	public RItem(String name) {
		this.iName = name;
	}

	public String getItemName() {
		return iName;
	}

	public abstract void execute() throws Exception;

	public static RItem get(RItem itm) {
		return itm;
	}

}
