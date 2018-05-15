package com.bmtech.utils.io.diskMerge;

public interface MReduceSelect {
	/**
	 * check if me1 equals with me2
	 * @param me1
	 * @param me2
	 * @return
	 */
	public boolean equals (MRecord me1, MRecord me2);
	
	/**
	 * get the prefered object if me1 equals with me2
	 * @param me1
	 * @param me2
	 * @return
	 */
	public MRecord select(MRecord me1, MRecord me2);
}
