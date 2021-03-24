package de.tubs.variantsync.core.managers.persistence;

import java.util.regex.Pattern;

public class XMLFormatHelper {

	// the xml pattern should start with an optional standard xml-starting line (e.g. <?xml charSequenceOfArbitraryLength?>)
	// followed by an xml tag starting with the content string
	public static Pattern createContentRegex(String content) {
		return Pattern.compile("\\A\\s*(<[?]xml\\s.*[?]>\\s*)?<" + content + "[\\s>]");
	}
}
