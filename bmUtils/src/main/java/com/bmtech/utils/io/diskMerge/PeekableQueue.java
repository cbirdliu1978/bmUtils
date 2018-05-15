package com.bmtech.utils.io.diskMerge;

public interface PeekableQueue {
	/**
	 * peek if there is record still can read
	 * 
	 * @return
	 * @throws Exception
	 */
	public MRecord peek() throws Exception;

	/**
	 * get the record and move cursor to next record.(then, peek# will return
	 * next new one )
	 * 
	 * @return
	 * @throws Exception
	 */
	public MRecord take() throws Exception;

	/**
	 * close the queue
	 */
	public void close();

	/**
	 * get the number record has readed(not include the current peekable one)
	 * 
	 * @return
	 */
	public int getReadedRecordNumber();

}
