package de.variantsync.core.ast;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LineBasedParser {

	public LineBasedParser() {}

	public AST<LineGrammar, String> parseDirectory(Path path) throws IOException {
		AST<LineGrammar, String> result = null;
		if (Files.exists(path)) {
			if (Files.isDirectory(path)) {
				result = new AST<>(LineGrammar.Directory, path.getFileName().toString());

				try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(path)) {
					// DirectoryStream has no specific order. Thus, the entries are added to an ArrayList which can be sorted.
					final List<Path> list = new ArrayList<>();
					for (final Path entry : directoryStream) {
						list.add(entry);
					}
					Collections.sort(list);
					for (final Path entry : list) {
						result.addChild(parseDirectory(entry));
					}

				}
			} else {
				if (!isBinaryFile(path)) {
					result = new AST<>(LineGrammar.TextFile, path.getFileName().toString());
					final List<String> fileStream = Files.readAllLines(path);
					for (final String line : fileStream) {
						result.addChild(new AST<>(LineGrammar.Line, line));
					}
				} else {
					result = new AST<>(LineGrammar.BinaryFile, path.getFileName().toString());
				}
			}
		} else {
			throw new FileNotFoundException();
		}
		return result;
	}

	private static boolean isBinaryFile(Path file) throws IOException {
		if (Files.exists(file)) {
			final String type = Files.probeContentType(file);
			if (type == null) {
				// type couldn't be determined => assume binary
				return true;
			} else if (type.startsWith("text")) {
				// non-binary
				return false;
			} else {
				// type isn't text => assume binary
				return true;
			}
		} else {
			throw new FileNotFoundException();
		}
	}
}
