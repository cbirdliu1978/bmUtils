/*
 * Copyright (c) 2002-2016, the original author or authors.
 *
 * This software is distributable under the BSD license. See the terms of the
 * BSD license in the documentation provided with this software.
 *
 * http://www.opensource.org/licenses/bsd-license.php
 */
package com.bmtech.utils;

import static jline.internal.Preconditions.checkNotNull;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.List;

import jline.Terminal;
import jline.console.ConsoleReader;
import jline.console.completer.Completer;
import jline.console.history.History;
import jline.console.history.MemoryHistory;

public class ConsolerLineReader {
	private static final ConsolerLineReader instance = new ConsolerLineReader();
	private int promptMax = 10;
	private int retrieveMax = 500;
	private final ConsoleReader reader;
	PrintWriter out;

	private ConsolerLineReader() {
		try {
			reader = new ConsoleReader();
			// reader.setExpandEvents(expand);
			reader.setHandleUserInterrupt(true);
			reader.setPrompt("");
			Terminal term = reader.getTerminal();

			term.setEchoEnabled(false);
			List<Completer> completors = new LinkedList<Completer>();

			MemoryHistory mhis = (MemoryHistory) (reader.getHistory());
			completors.add(new HistoryCompleter(mhis));

			for (Completer c : completors) {
				reader.addCompleter(c);
			}

			mhis.setIgnoreDuplicates(true);

			term.setEchoEnabled(false);
			out = new PrintWriter(reader.getOutput());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

	}

	public History getHistory() {
		return reader.getHistory();
	}

	public class HistoryCompleter implements Completer {

		History history;

		public HistoryCompleter(History history) {
			this.history = history;
		}

		@Override
		public int complete(String buffer, final int cursor, final List<CharSequence> candidates) {
			checkNotNull(candidates);
			int size = history.size();
			int readNum = retrieveMax > size ? size : retrieveMax;
			int prpNum = promptMax > size ? size : promptMax;
			buffer = buffer == null ? "" : buffer.trim();
			if (buffer.length() == 0) {
				// candidates.add("$");
				for (int x = 0; x < prpNum; x++) {
					candidates.add(history.get(x));
				}
			} else {
				int end = size - readNum;
				for (int x = size - 1; x >= 0 && x >= end; x--) {
					String v = history.get(x).toString();
					// System.out.println("his" + v);
					if (v != null) {
						if (v.startsWith(buffer)) {
							candidates.add(v);
							if (candidates.size() >= promptMax)
								break;
						}
					}
				}
			}
			return candidates.isEmpty() ? -1 : 0;
		}
	}

	public String readLine() throws IOException {
		String line = reader.readLine();
		if (line == null)
			line = "";
		return line;
	}

	public int getPromptMax() {
		return this.promptMax;
	}

	public void setPromptMax(int promptMax) {
		this.promptMax = promptMax;
	}

	public int getRetrieveMax() {
		return this.retrieveMax;
	}

	public void setRetrieveMax(int retrieveMax) {
		this.retrieveMax = retrieveMax;
	}

	public static ConsolerLineReader getInstance() {
		return instance;
	}

	public static void main(String[] args) throws IOException {
		ConsolerLineReader e2 = new ConsolerLineReader();
		String line;
		PrintWriter out = new PrintWriter(e2.reader.getOutput());

		boolean color = true;
		while ((line = e2.readLine()) != null) {
			if (color) {
				out.println("\u001B[33m======>\u001B[0m\"" + line + "\"");

			} else {
				out.println("======>\"" + line + "\"");
			}
			out.flush();

		}
	}

	public ConsoleReader getReader() {
		return this.reader;
	}

	public void println(String str) throws IOException {
		out.println(str);
		out.flush();
	}

	public void print(String str) throws IOException {
		out.print(str);
		out.flush();
	}
}
