package com.bmtech.utils.restoreable;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import com.bmtech.utils.ForEach;
import com.bmtech.utils.Misc;
import com.bmtech.utils.io.LineWriter;
import com.bmtech.utils.log.LogHelper;

public class DiskSynStringChecker implements ExcludeChecker {

	private String name;
	protected Set<String> hasDone = new HashSet<>();
	private File myFile;
	final LineWriter lw;

	public DiskSynStringChecker(String name) throws Exception {
		this.name = name.trim();
		myFile = makeFile();
		ForEach.forEachLine(myFile, (line, lineNo) -> {
			line = line.trim();
			if (!line.startsWith("#")) {
				hasDone.add(line);
			}
		});

		lw = new LineWriter(myFile, true);
	}

	private File makeFile() throws IOException {
		File rein = new File("./RestoreableExecute.save/" + Misc.toFileNameFormat(name, '-'));
		Misc.besureDirExists(rein.getParentFile());
		Misc.besureFileExists(rein);
		return rein;
	}

	@Override
	public synchronized boolean hasDone(String itemName) {
		if (hasDone.contains(itemName)) {
			return true;
		}
		return false;
	}

	@Override
	public synchronized void registerFinished(String name) {
		hasDone.add(name);
		try {
			saveToFile(name);
		} catch (IOException e) {
			LogHelper.log.error("can not save item with name%s", name);
		}

	}

	private synchronized void saveToFile(String name) throws IOException {
		lw.writeLine(name);
		lw.flush();
	}

	public void reset() {
		this.hasDone.clear();
	}

	@Override
	public Set<String> hasDoneSet() {
		return this.hasDone;
	}

	@Override
	public int hasDoneNum() {
		return hasDone.size();
	}
}
