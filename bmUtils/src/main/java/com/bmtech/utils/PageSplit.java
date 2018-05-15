package com.bmtech.utils;

import java.util.ArrayList;
import java.util.List;

public class PageSplit {
	private int itemNumberPerPage = 10;
	private int itemStartIndex = 0, itemEndIndex = 0;
	private int startPageNum = 1;
	private int endPageNum = 1;
	private final int currentPage;
	private int totalPageNum = 0;
	private final int totalItem;

	public PageSplit(int totalItem, int itemPerPage, int pageIndex) {
		this(totalItem, itemPerPage, pageIndex, 10);
	}

	public PageSplit(int totalItem, int itemPerPage, int pageIndex,
			int showNumber) {
		this.totalItem = totalItem;
		this.itemNumberPerPage = itemPerPage;
		if (pageIndex < 1)
			pageIndex = 1;
		currentPage = pageIndex;

		this.itemStartIndex = (pageIndex - 1) * this.itemNumberPerPage;
		if (itemStartIndex > totalItem) {
			itemStartIndex = totalItem;
			itemEndIndex = totalItem;
		} else {
			itemEndIndex = itemStartIndex + this.itemNumberPerPage;
			if (totalItem < pageIndex * this.itemNumberPerPage)
				this.itemEndIndex = totalItem;

			totalPageNum = totalItem / itemPerPage;
			if (totalItem % itemPerPage != 0)
				totalPageNum++;

			int leftMargin = showNumber / 2;
			int rightMargin = leftMargin;
			if (leftMargin + rightMargin + 1 > showNumber) {
				if (leftMargin > 0) {
					leftMargin--;
				}
			}

			startPageNum = pageIndex - leftMargin;
			endPageNum = pageIndex + rightMargin;
			if (startPageNum <= 0) {
				endPageNum = 1 + endPageNum - startPageNum;
				startPageNum = 1;
			}
			if (endPageNum > totalPageNum)
				endPageNum = totalPageNum;
		}
	}

	public int getPageStart() {
		return this.startPageNum;
	}

	public int getPageEnd() {
		return this.endPageNum;
	}

	public int getPrePage() {
		if (this.startPageNum > 1)
			return (this.startPageNum - 1);
		return (1);
	}

	public int getNextPage() {
		if (this.endPageNum > this.currentPage)
			return (this.currentPage + 1);
		return (this.endPageNum);
	}

	public int getTotalPageNum() {
		return this.totalPageNum;
	}

	public List<Integer> getPageIndexes() {
		List<Integer> ret = new ArrayList<Integer>();
		for (int x = this.getPageStart(); x <= this.getPageEnd(); x++) {
			ret.add(x);
		}
		return ret;
	}

	public int getTotalItem() {
		return totalItem;
	}

	public int getItemStartIndex() {
		return itemStartIndex;
	}

	public int getItemEndIndex() {
		return itemEndIndex;
	}

	@Override
	public String toString() {
		return "PageSplit [itemNumberPerPage=" + itemNumberPerPage
				+ ", itemStartIndex=" + itemStartIndex + ", itemEndIndex="
				+ itemEndIndex + ", startPageNum=" + startPageNum
				+ ", endPageNum=" + endPageNum + ", currentPage=" + currentPage
				+ ", totalPageNum=" + totalPageNum + ", totalItem=" + totalItem
				+ "]";
	}

	public void setItemEndIndex(int itemEndIndex) {
		this.itemEndIndex = itemEndIndex;
	}

	public int getItemNumberPerPage() {
		return itemNumberPerPage;
	}

	public int getCurrentPage() {
		return currentPage;
	}

	public int getEndPageNum() {
		return endPageNum;
	}

	public void setEndPageNum(int endPageNum) {
		this.endPageNum = endPageNum;
	}

	public int getStartPageNum() {
		return startPageNum;
	}

	public void setStartPageNum(int startPageNum) {
		this.startPageNum = startPageNum;
	}

}
