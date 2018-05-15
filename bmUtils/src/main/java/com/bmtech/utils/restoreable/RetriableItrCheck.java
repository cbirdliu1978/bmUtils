package com.bmtech.utils.restoreable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.bmtech.utils.Misc;
import com.bmtech.utils.Runnor;
import com.bmtech.utils.log.LogHelper;

public class RetriableItrCheck implements Iterator<RItem>, ExcludeChecker {
	public static interface ItemFactory {
		public Runnor make(String name);
	}

	private ExcludeChecker check = new MemoryChecker();
	private List<String> itemNames = new ArrayList<>();
	private int maxRoundNum = Integer.MAX_VALUE;
	private int nowRound = 0;
	private int roundSkipNumber = 0;
	private int roundNowCheckIndex = 0;

	private ItemFactory fac;

	public RetriableItrCheck(List<String> itemNames) {
		this.itemNames.addAll(itemNames);
	}

	public int getLeftItemNum() {
		return this.itemNames.size() - getCheck().hasDoneNum();
	}

	public Set<String> letfItems() {
		Set<String> set = new HashSet<>();
		set.addAll(this.itemNames);
		set.removeAll(getCheck().hasDoneSet());
		return set;
	}

	private Runnor makeItem(String name) {
		if (fac == null) {
			throw new RuntimeException("ItemFactory not set yet!");
		}
		return fac.make(name);
	}

	@Override
	public boolean hasNext() {
		if (roundNowCheckIndex >= itemNames.size()) {
			if (roundSkipNumber >= itemNames.size()) {
				return false;
			}
			nowRound++;
			if (nowRound >= this.maxRoundNum)
				return false;

			roundSkipNumber = 0;
			roundNowCheckIndex = 0;
			return hasNext();
		} else {
			for (; roundNowCheckIndex < this.itemNames.size(); roundNowCheckIndex++) {
				String str = this.itemNames.get(this.roundNowCheckIndex);
				if (!this.getCheck().hasDone(str)) {
					return true;
				}
				roundSkipNumber++;
			}
			return hasNext();
		}
	}

	@Override
	public RItem next() {
		String name = this.itemNames.get(this.roundNowCheckIndex);
		roundNowCheckIndex++;// consume
		Runnor r = this.makeItem(name);
		return new RItem(name) {

			@Override
			public void execute() throws Exception {
				synchronized (name) {
					if (!getCheck().hasDone(name)) {
						r.run();
					}
				}
			}
		};
	}

	public int getMaxRoundNum() {
		return maxRoundNum;
	}

	public void setMaxRoundNum(int maxRoundNum) {
		this.maxRoundNum = maxRoundNum;
	}

	@Override
	public boolean hasDone(String name) {
		return getCheck().hasDone(name);
	}

	@Override
	public void registerFinished(String name) {
		getCheck().registerFinished(name);
	}

	public ItemFactory getFac() {
		return fac;
	}

	public void setFac(ItemFactory fac) {
		this.fac = fac;
	}

	public static RestoreableExecute getExecutor(RetriableItrCheck itr, ItemFactory fac) {
		itr.setFac(fac);
		RestoreableExecute re = new RestoreableExecute(itr, itr);
		return re;
	}

	public static RestoreableExecute getExecutor(List<String> lst, ItemFactory fac) {
		RetriableItrCheck itr = new RetriableItrCheck(lst);
		itr.setFac(fac);
		RestoreableExecute re = new RestoreableExecute(itr, itr);
		return re;
	}

	public static void main(String[] args) throws Exception {
		List<String> lst = new ArrayList<>();

		for (int x = 0; x < 10000; x++)
			lst.add("" + x);

		RestoreableExecute re = getExecutor(lst, (String name) -> {
			return new Runnor() {
				@Override
				public void run() {
					LogHelper.iInfo("try doing " + name + "i");
					if (Misc.randInt(0, 6) == 5)
						throw new RuntimeException("failExe doing " + name + "i");
					LogHelper.iInfo("ok doing " + name + "i");
				}
			};
		});
		re.setThreadNum(3);
		re.execute();
	}

	public ExcludeChecker getCheck() {
		return check;
	}

	@Override
	public Set<String> hasDoneSet() {
		return this.check.hasDoneSet();
	}

	@Override
	public int hasDoneNum() {
		return check.hasDoneNum();
	}

	public void setCheck(ExcludeChecker check) {
		this.check = check;
	}

}