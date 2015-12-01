package org.codefx.mvn.jdeps.parse;

import org.codefx.mvn.jdeps.dependency.InternalType;

import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Parses a single line of JDeps output to an {@link InternalType}.
 * <p>
 * Such lines must generally be of the following form:
 *
 * <pre>
 *       -&gt; package.name.ClassName     category (source)
 * </pre>
 */
class InternalTypeLineParser {

	private static final Pattern JDEPS_DEPENDENCY_PATTERN = Pattern.compile(""
			+ "\\s+->\\s+" // leading spaces and arrow, e.g. "      -> "
			+ "([a-zA-Z_][\\.\\w]*)" // qualified class name (simplified), e.g. "sun.misc.Unsafe"
			+ "\\s+" // spaces to fill up the column
			+ "(\\w[\\w\\s]*\\w*)" // category, e.g. "JDK Internal API"
			+ "\\s" // space between category and source
			+ "\\(([\\w\\.]*)\\)" // source, e.g. "(jt.jar)"
			+ ".*");

	/**
	 * Indicates whether the specified line is an {@link InternalType}.
	 *
	 * @param line
	 *            the line to check
	 * @return true if the line can be parsed to an {@link InternalType}
	 */
	public boolean isInternalTypeLine(String line) {
		Objects.requireNonNull(line, "The argument 'line' must not be null.");

		return JDEPS_DEPENDENCY_PATTERN.matcher(line).matches();
	}

	/**
	 * Tries to parse the specified line to an {@link InternalType}.
	 *
	 * @param line
	 *            the line to parse
	 * @return an {@link InternalType} if the line could be parsed; otherwise an empty {@link Optional}
	 */
	public Optional<InternalType> parseLine(String line) {
		Objects.requireNonNull(line, "The argument 'line' must not be null.");

		Matcher lineMatcher = JDEPS_DEPENDENCY_PATTERN.matcher(line);
		if (!lineMatcher.matches() || lineMatcher.groupCount() != 3)
			return Optional.empty();

		String fullyQualifiedClassName = lineMatcher.group(1);
		String category = lineMatcher.group(2);
		String source = lineMatcher.group(3);

		InternalType type = InternalType.of(
				extractPackageName(fullyQualifiedClassName),
				extractClassName(fullyQualifiedClassName),
				category,
				source);
		return Optional.of(type);
	}

	private static String extractPackageName(String fullyQualifiedClassName) {
		int indexOfLastPoint = fullyQualifiedClassName.lastIndexOf('.');
		return fullyQualifiedClassName.substring(0, indexOfLastPoint);
	}

	private static String extractClassName(String fullyQualifiedClassName) {
		int indexOfLastPoint = fullyQualifiedClassName.lastIndexOf('.');
		return fullyQualifiedClassName.substring(indexOfLastPoint + 1);
	}

}
