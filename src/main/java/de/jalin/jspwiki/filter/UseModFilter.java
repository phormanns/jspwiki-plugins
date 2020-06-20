package de.jalin.jspwiki.filter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.Properties;
import java.util.StringTokenizer;

import org.apache.wiki.WikiContext;
import org.apache.wiki.api.core.Engine;
import org.apache.wiki.api.exceptions.FilterException;
import org.apache.wiki.api.filters.PageFilter;

public class UseModFilter implements PageFilter {

	private String lookForward = null;

	@Override
	public void initialize(Engine engine, Properties properties) throws FilterException {
		// skip initalization
	}

	public String preTranslate(final WikiContext wikiContext, final String content) throws FilterException {
		try {
			final BufferedReader reader = new BufferedReader(new StringReader(content));
			String currentLine = readLine(reader);
			final StringBuffer translationBuffer = new StringBuffer();
			while (currentLine != null) {
				currentLine = translateSingleLine(reader, currentLine);
				translationBuffer.append(currentLine);
				translationBuffer.append("\n");
				currentLine = readLine(reader);
			}
			return new String(translationBuffer);
		} catch (Exception e) {
			return "%%error\nFehler im Filter: " + e.getMessage() + "\n%%\n";
		}
	}

	private String translateSingleLine(final BufferedReader reader, String currentLine) throws IOException {
		if (currentLine.contains("[{")) {
			final StringBuffer pluginBuffer = new StringBuffer(currentLine);
			while (!currentLine.contains("}]")) {
				currentLine = readLine(reader);
				pluginBuffer.append("\n");
				pluginBuffer.append(currentLine);
			}
			return pluginBuffer.toString();
		}
		if (currentLine.contains("{{{") || currentLine.contains("<pre>") || currentLine.startsWith(" ")) {
			currentLine = translatePreformatted(reader, currentLine);
		} else {
			if (currentLine.startsWith(":")) {
				currentLine = translateIndent(reader, currentLine);
			}
			if (currentLine.startsWith("=")) {
				currentLine = translateTitle(reader, currentLine);
			}
			if (currentLine.startsWith("#")) {
				currentLine = translateItemize(reader, currentLine);
			}
			if (currentLine.startsWith("*")) {
				currentLine = translateItemize(reader, currentLine);
			}
			currentLine = translateInlineMarkup(reader, currentLine);
		}
		return currentLine;
	}

	private String translateItemize(final BufferedReader reader, String currentLine) throws IOException {
		String nextLine = readLine(reader);
		if (nextLine != null && nextLine.length() > 0) {
			char firstChar = nextLine.charAt(0);
			while (nextLine != null && nextLine.trim().length() > 0 && firstChar != ' ' && firstChar != '!'
					&& firstChar != '=' && firstChar != '*' && firstChar != '#' && firstChar != ':') {
				currentLine += " " + nextLine;
				nextLine = readLine(reader);
				if (nextLine != null && nextLine.length() > 0) {
					firstChar = nextLine.charAt(0);
				}
			}
			if (nextLine != null) {
				lookForward = nextLine;
			}
		}
		return currentLine;
	}

	private String translateInlineMarkup(final BufferedReader reader, String currentLine) throws IOException {
		if (currentLine.contains("'''''")) {
			currentLine = currentLine.replaceFirst("'''''", "__''");
			currentLine = currentLine.replaceFirst("'''''", "''__");
		}
		if (currentLine.contains("'''")) {
			currentLine = currentLine.replaceAll("'''", "__");
		}
		if (currentLine.contains("http://")) {
			currentLine = translateLink(reader, currentLine, "http://");
		}
		if (currentLine.contains("https://")) {
			currentLine = translateLink(reader, currentLine, "https://");
		}
		return currentLine;
	}

	private String translateLink(final BufferedReader reader, String currentLine, String protocol) throws IOException {
		if (currentLine.contains("[" + protocol)) {
			int startIndex = currentLine.indexOf("[" + protocol);
			String translated = currentLine.substring(0, startIndex + 1);
			String part = currentLine.substring(startIndex + 1);
			String nextLine = readLine(reader);
			while (!part.contains("]") && nextLine != null) {
				currentLine += " " + nextLine;
				part += " " + nextLine;
				nextLine = readLine(reader);
			}
			if (nextLine != null) {
				lookForward = nextLine;
			}
			StringTokenizer tokenizer = new StringTokenizer(part, " ]|,;\n");
			String link = tokenizer.nextToken();
			if (part.length() > link.length()) {
				int nextChar = part.charAt(link.length());
				if (nextChar == ']' || nextChar == '|') {
					return currentLine;
				}
			}
			int endIndex = startIndex + part.indexOf("]") + 1;
			String text = currentLine.substring(startIndex + link.length() + 2, endIndex);
			return translated + text + "|" + link + currentLine.substring(endIndex);
		} else {
			int startIndex = currentLine.indexOf(protocol);
			String translated = currentLine.substring(0, startIndex);
			String part = currentLine.substring(startIndex);
			StringTokenizer tokenizer = new StringTokenizer(part, " ]|;\n");
			String link = tokenizer.nextToken();
			if (part.length() > link.length()) {
				int nextChar = part.charAt(link.length());
				if (nextChar == ']' || nextChar == '|') {
					return currentLine;
				}
			}
			return translated + "[" + link + "]" + part.substring(link.length());
		}
	}

	private String translateIndent(final BufferedReader reader, String currentLine) throws IOException {
		int level = 0;
		while (currentLine.startsWith(":")) {
			level++;
			currentLine = currentLine.substring(1);
		}
		String nextLine = readLine(reader);
		if (nextLine != null && nextLine.length() > 0) {
			char firstChar = nextLine.charAt(0);
			while (nextLine != null && firstChar != ' ' && firstChar != '-' && firstChar != '!' && firstChar != '='
					&& firstChar != '*' && firstChar != '#' && firstChar != ':') {
				currentLine += "\n" + nextLine;
				nextLine = readLine(reader);
				if (nextLine != null && nextLine.length() > 0) {
					firstChar = nextLine.charAt(0);
				}
			}
			if (nextLine != null) {
				lookForward = nextLine;
			}
		}
		currentLine = "%%(margin-bottom: 12pt; margin-top: 12pt; margin-left: " + (level * 24) + "pt)\n" + currentLine
				+ "\n%%";
		return currentLine;
	}

	private String readLine(final BufferedReader reader) throws IOException {
		if (lookForward != null) {
			String tmp = lookForward;
			lookForward = null;
			return tmp;
		}
		return reader.readLine();
	}

	private String translatePreformatted(final BufferedReader reader, String currentLine) throws IOException {
		if (currentLine.startsWith(" ")) {
			if (currentLine.trim().length() == 0) {
				currentLine = "";
			} else {
				currentLine = "{{{\n" + currentLine;
				String nextLine = readLine(reader);
				while (nextLine != null && nextLine.trim().length() > 0) {
					if (nextLine.startsWith(" ")) {
						currentLine += "\n";
					} else {
						currentLine += " ";
					}
					currentLine += nextLine;
					nextLine = readLine(reader);
				}
				if (nextLine != null)
					lookForward = nextLine;
				currentLine += "\n}}}";
			}
		} else {
			if (currentLine.contains("{{{")) {
				int startIndex = currentLine.indexOf("{{{");
				String firstPart = currentLine.substring(0, startIndex);
				currentLine = translateInlineMarkup(reader, firstPart) + currentLine.substring(startIndex);
				String nextLine = readLine(reader);
				while (!currentLine.contains("}}}") && nextLine != null) {
					currentLine += "\n" + nextLine;
					nextLine = readLine(reader);
				}
				if (!currentLine.contains("}}}")) {
					currentLine += "\n}}}";
				}
				if (nextLine != null)
					lookForward = nextLine;
				int endIndex = currentLine.indexOf("}}}");
				String lastPart = currentLine.substring(endIndex + 3);
				currentLine = currentLine.substring(0, endIndex + 3) + translateInlineMarkup(reader, lastPart);
			} else {
				currentLine = currentLine.replaceFirst("<pre>", "{{{");
				int startIndex = currentLine.indexOf("{{{");
				String firstPart = currentLine.substring(0, startIndex);
				currentLine = translateInlineMarkup(reader, firstPart) + currentLine.substring(startIndex);
				String nextLine = readLine(reader);
				while (!currentLine.contains("</pre>") && nextLine != null) {
					currentLine += "\n" + nextLine;
					nextLine = readLine(reader);
				}
				if (nextLine != null)
					lookForward = nextLine;
				if (currentLine.contains("</pre>")) {
					currentLine = currentLine.replaceFirst("</pre>", "}}}");
				} else {
					currentLine += "\n}}}";
				}
				int endIndex = currentLine.indexOf("}}}");
				String lastPart = currentLine.substring(endIndex + 3);
				currentLine = currentLine.substring(0, endIndex + 3) + translateInlineMarkup(reader, lastPart);
			}
		}
		return currentLine;
	}

	private String translateTitle(final BufferedReader reader, String currentLine) throws IOException {
		String nextLine = null;
		while (!currentLine.trim().endsWith("=")) {
			nextLine = readLine(reader);
			if (nextLine != null) {
				currentLine += " " + nextLine;
			} else {
				break;
			}
		}
		currentLine = currentLine.trim();
		if (currentLine.startsWith("====") && currentLine.endsWith("====")) {
			currentLine = "__" + currentLine.substring(4, currentLine.length() - 4) + "__";
		}
		if (!currentLine.startsWith("====") && currentLine.startsWith("===") && currentLine.endsWith("===")) {
			currentLine = "!" + currentLine.substring(3, currentLine.length() - 3);
		}
		if (!currentLine.startsWith("===") && currentLine.startsWith("==") && currentLine.endsWith("==")) {
			currentLine = "!!" + currentLine.substring(2, currentLine.length() - 2);
		}
		if (!currentLine.startsWith("==") && currentLine.startsWith("=") && currentLine.endsWith("=")) {
			currentLine = "!!!" + currentLine.substring(1, currentLine.length() - 1);
		}
		return currentLine;
	}

}
