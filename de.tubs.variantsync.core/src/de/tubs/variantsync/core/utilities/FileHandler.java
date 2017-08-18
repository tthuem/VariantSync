package de.tubs.variantsync.core.utilities;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;

public class FileHandler {
	
	/**
	 * Reads TXT-file and adds each line to list of string elements.
	 *
	 * @param inputStream
	 *            input file
	 * @return list of file content
	 * @throws IOException
	 *             file could not be read
	 */
	public static List<String> readFile(InputStream inputStream) {
		List<String> fileLines = new LinkedList<String>();
		BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
		String line = "";
		try {
			while ((line = reader.readLine()) != null) {
				fileLines.add(line);
			}
		} catch (IOException e) {
			
		} finally {
			try {
				reader.close();
			} catch (NullPointerException | IOException e) {
				LogOperations.logError("BufferedReader could not be closed.", e);
			}
		}
		return fileLines;
	}
	
	/**
	 * Reads content from file using buffered reader. Adds each line in file to
	 * List<String>.
	 *
	 * @param in
	 *            buffered Reader for file
	 * @param charset
	 * @return list with file content
	 * @throws FileOperationException
	 */
	public static List<String> readFile(InputStream in, String charset) {
		List<String> fileContent = new LinkedList<String>();
		String line = "";
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(in, charset));
			while ((line = reader.readLine()) != null) {
				fileContent.add(line);
			}
		} catch (IOException e) {
			
		} finally {
			try {
				reader.close();
			} catch (NullPointerException | IOException e) {
				LogOperations.logError("BufferedReader could not be closed.", e);
			}
		}
		return fileContent;
	}
	
}
