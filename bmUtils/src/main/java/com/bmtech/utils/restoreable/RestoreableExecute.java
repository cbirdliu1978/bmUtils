package com.bmtech.utils.restoreable;

import java.util.Iterator;

import com.bmtech.utils.bmfs.MDir;

public class RestoreableExecute extends RestoreableExecuteAbstract {

	public RestoreableExecute(ExcludeChecker checker, Iterator<RItem> feeder) {
		this(checker, feeder, 1);
	}

	public RestoreableExecute(ExcludeChecker checker, Iterator<RItem> feeder, int threadNum) {
		super(checker, feeder, threadNum);
	}

	public RestoreableExecute(String name, Iterator<RItem> feeder) throws Exception {
		super(new DiskSynStringChecker(name), feeder, 1);
	}

	public RestoreableExecute(String name, Iterator<RItem> feeder, int threadNum) throws Exception {
		super(new DiskSynStringChecker(name), feeder, threadNum);
	}

	public RestoreableExecute(MDir mdir, Iterator<RItem> feeder) throws Exception {
		super(new MDirChecker(mdir), feeder, 1);
	}

	public RestoreableExecute(MDir mdir, Iterator<RItem> feeder, int threadNum) throws Exception {
		super(new MDirChecker(mdir), feeder, threadNum);
	}

}
