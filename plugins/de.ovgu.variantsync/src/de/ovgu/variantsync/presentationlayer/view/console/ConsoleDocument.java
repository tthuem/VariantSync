package de.ovgu.variantsync.presentationlayer.view.console;

/**
 * 
 * @author Lei Luo
 *
 */
public class ConsoleDocument {
	private String[] lines;
	private int writeIndex = 0;
	private int readIndex = 0;
	private static final int BUFFER_SIZE = 300;

	public void clear() {
		lines = null;
		writeIndex = 0;
		readIndex = 0;
	}

	public void appendConsoleLine(String line) {
		if (lines == null) {
			lines = new String[BUFFER_SIZE];
		}
		lines[writeIndex] = line;
		if (++writeIndex >= BUFFER_SIZE) {
			writeIndex = 0;
		}
		if (writeIndex == readIndex && ++readIndex >= BUFFER_SIZE) {
			readIndex = 0;
		}
	}

	public String[] getLines() {
		if (isEmpty()) {
			return new String[0];
		}
		String[] docLines = new String[readIndex > writeIndex ? BUFFER_SIZE
				: writeIndex];
		int index = readIndex;
		for (int i = 0; i < docLines.length; i++) {
			docLines[i] = lines[index++];
			if (index >= BUFFER_SIZE) {
				index = 0;
			}
		}
		return docLines;
	}

	public boolean isEmpty() {
		return writeIndex == readIndex;
	}
}
