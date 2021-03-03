package de.variantsync.core.ast;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class LineBasedParser {

	public LineBasedParser() {}

	public AST<LineGrammar, String> parseDirectory(Path folder) throws IOException {
		AST<LineGrammar, String> result;
		if (Files.isDirectory(folder)) {
			result = new AST<>(LineGrammar.Directory, folder.getFileName().toString());
			final DirectoryStream<Path> directoryStream = Files.newDirectoryStream(folder);
			for (final Path entry : directoryStream) {
				result.addChild(parseDirectory(entry));
			}
		} else {
			if (!isBinaryFile(folder)) {
				result = new AST<>(LineGrammar.TextFile, folder.getFileName().toString());
				final List<String> fileStream = Files.readAllLines(folder);
				for (final String line : fileStream) {
					result.addChild(new AST<>(LineGrammar.Line, line));
				}
			} else {
				result = new AST<>(LineGrammar.BinaryFile, folder.getFileName().toString());
			}
		}
		return result;
	}

	private static boolean isBinaryFile(Path file) {
		try {
			final String type = Files.probeContentType(file);
			if (type == null) {
				// type couldn't be determined => assume binary
				return true;
			} else if (type.startsWith("text")) {
				// non-binary
				return false;
			}
		} catch (final IOException e) {
			e.printStackTrace();
		}
		// type isn't text => assume binary
		return true;
	}
}
