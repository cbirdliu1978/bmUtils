package com.bmtech.datamine.data;

import com.bmtech.datamine.data.mday.MinDay;

public class StockDiffer {
	static class FDiff {
		Double abs;
		Double rel;

		private final FieldType field;

		FDiff(Double abs, Double rel, FieldType field) {
			this.abs = abs;
			this.rel = rel;
			this.field = field;
		}

		private double value(MinDay day) {
			return day.get(field);

		}

		public boolean accept(MinDay from, MinDay to) {
			return acc(value(from), value(to));
		}

		boolean acc(double from, double to) {
			double diff = Math.abs(from - to);
			boolean absMatch = false, relMatch = false;
			if (abs != null) {
				if (abs < diff)
					absMatch = false;
				else
					absMatch = true;
			}
			if (rel != null) {
				double diffPerc = diff / from;
				if (diffPerc > rel) {
					relMatch = false;
				} else {
					relMatch = true;
				}
			}
			return absMatch || relMatch;
		}

	}

	public static StockDiffer differ = new StockDiffer();

	private double relMax = 0.005;
	FDiff[] differs = new FDiff[] { new FDiff(0.03, relMax, FieldType.OPEN), new FDiff(0.03, relMax, FieldType.HIGH),
			new FDiff(0.03, relMax, FieldType.LOW), new FDiff(0.03, relMax, FieldType.CLOSE), new FDiff(null, 0.05, FieldType.VOLUMN) };

	public boolean diff(MinDay from, MinDay to) {
		int matchNum = 0;
		if (from == null || to == null) {
			System.out.println("ERROR");
			return false;
		} else {
			for (FDiff diff : differs) {
				boolean acc = diff.accept(from, to);
				if (acc) {
					matchNum++;
				}
			}
			return matchNum >= 3;
		}
	}

}
