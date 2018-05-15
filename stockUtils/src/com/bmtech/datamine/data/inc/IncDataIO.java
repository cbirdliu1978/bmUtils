package com.bmtech.datamine.data.inc;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.bmtech.datamine.Statics;
import com.bmtech.datamine.Stock;
import com.bmtech.datamine.data.DataIntergrationVerifier;
import com.bmtech.datamine.data.DataType;
import com.bmtech.datamine.data.mday.MinDay;
import com.bmtech.utils.ForEach;
import com.bmtech.utils.Misc;
import com.bmtech.utils.io.LineWriter;
import com.bmtech.utils.log.L;

public class IncDataIO extends IncHolder {

	public static final File dataIncBaseDir = new File("/ext/inc/");
	static {
		try {
			Misc.besureDirExists(dataIncBaseDir);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private File incFile;
	private boolean canWriter = false;

	public IncDataIO(DataType dt, Stock stk) throws Exception {
		this(dt, stk, false);
	}

	public IncDataIO(DataType dataType, Stock stk, boolean writeMode) throws Exception {
		super(dataType, stk);
		File dir = new File(dataIncBaseDir, stk.getCode().toLowerCase());
		incFile = new File(dir, dataType.name() + ".txt");

		Misc.besureFileExists(incFile);

		List<MinDay> lst = loadFromIncFile(dataType, stk);
		this.merge(lst);
		this.canWriter = writeMode;
	}

	private void add(MinDay toAdd, List<MinDay> list) {
		// check dataType match
		boolean isMatch = DataIntergrationVerifier.isDataTypeMatch(toAdd, this.getDataType());
		if (!isMatch) {
			throw new RuntimeException("not expected DataType, this is " + this.getDataType() + ", but real got " + toAdd);
		}

		// check order
		if (list.size() > 0) {
			MinDay last = list.get(list.size() - 1);
			boolean orderOk = DataIntergrationVerifier.isOrderOk(toAdd, last);
			if (!orderOk) {
				throw new RuntimeException("order mismatch! last is " + last + ", toAdd is " + toAdd);
			}
		}
		list.add(toAdd);

	}

	private List<MinDay> loadFromIncFile(DataType dataType, Stock stk) throws IOException {
		List<MinDay> list = new ArrayList<>();
		if (dataType == DataType.min05) {
			int barSec = dataType.orgBarRangeSeconds();
			ForEach.forEachLine(this.incFile, (line, lineI) -> {
				MinDay mday = MinDay.parseMinDay(line, barSec, stk);
				add(mday, list);
			});
		} else {
			ForEach.forEachLine(this.incFile, (line, lineI) -> {
				MinDay mday = MinDay.parseDayLine(line, stk);
				add(mday, list);
			});
		}
		return list;
	}

	public void save() throws IOException {
		save(null);
	}

	public void save(MinDay bigThan) throws IOException {
		if (!this.canWriter)
			throw new RuntimeException("can not write in read mode");

		List<MinDay> list = getList(bigThan);
		int size = list.size();
		L.iInfo("save data size %s for stock %s, dataType %s, last is %s", size, this.stock, this.dataType,
				size == 0 ? null : list.get(list.size() - 1));

		if (size == 0)
			return;
		if (list.get(size - 1).getEndTime() != Statics.endMarketTime) {
			throw new IOException("end time is not endMarketTime " + list.get(size - 1));
		}
		LineWriter lw = new LineWriter(this.incFile, false);
		try {
			for (int x = 0; x < size; x++) {
				MinDay mday = list.get(x);
				String str;
				if (this.getDataType() == DataType.min05) {
					str = MinDay.toMinuteLine(mday);
				} else {
					str = MinDay.toDayLine(mday);
				}
				lw.writeLine(str);
			}
		} finally {
			lw.close();
		}
	}

	public void setCanWrite() {
		this.canWriter = true;
	}

}
