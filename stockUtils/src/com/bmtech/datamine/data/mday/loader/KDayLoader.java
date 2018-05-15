package com.bmtech.datamine.data.mday.loader;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import com.bmtech.datamine.data.mday.MinDay;

public abstract class KDayLoader implements Iterator<MinDay> {
	public abstract List<MinDay> getDayList() throws IOException;

}
