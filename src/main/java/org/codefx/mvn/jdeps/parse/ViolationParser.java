package org.codefx.mvn.jdeps.parse;

import org.codefx.mvn.jdeps.dependency.InternalType;
import org.codefx.mvn.jdeps.dependency.Type;
import org.codefx.mvn.jdeps.dependency.Violation;
import org.codefx.mvn.jdeps.dependency.Violation.ViolationBuilder;
import org.codefx.mvn.jdeps.mojo.MojoLogging;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.String.format;

/**
 * Parses violation blocks from the JDeps output line by line and hands created {@link Violation}s to a
 * {@link Consumer} that can further process it.
 */
public class ViolationParser {

	public static final String MESSAGE_MARKER_JDEPS_LINE = "[d]";
	public static final String MESSAGE_MARKER_UNKNOWN_LINE = "[ ]";
	private static final String MESSAGE_PARSED_LINE = " %s %s";

	/**
	 * Pattern to match the reported type line, e.g.
	 *
	 * <pre>
	 * 	   org.codefx.lab.App (...)
	 * </pre>
	 */
	private static final Pattern REPORTED_TYPE_PATTERN = Pattern.compile(""
			+ "\\s+" // leading spaces
			+ "([a-zA-Z_][\\.\\w]*)" // qualified class name (simplified), e.g. "sun.misc.Unsafe"
			+ "\\s+" // spaces to separate class name
			+ ".*");

	private final InternalTypeLineParser internalTypeLineParser;
	private final Consumer<Violation> violationConsumer;
	private LineParserState lineParser;

	/**
	 * Creates a new parser.
	 *
	 * @param violationConsumer
	 * 		the {@link Consumer} to which parsed {@link Violation}s are handed over
	 */
	public ViolationParser(Consumer<Violation> violationConsumer) {
		this(new InternalTypeLineParser(), violationConsumer);
	}

	/**
	 * Creates a new parser.
	 *
	 * @param internalTypeLineParser
	 * 		used to parse individual internal dependencies
	 * @param violationConsumer
	 * 		the {@link Consumer} to which parsed {@link Violation}s are handed over
	 */
	public ViolationParser(InternalTypeLineParser internalTypeLineParser, Consumer<Violation> violationConsumer) {
		Objects.requireNonNull(internalTypeLineParser, "The argument 'internalTypeLineParser' must not be null.");
		Objects.requireNonNull(violationConsumer, "The argument 'violationConsumer' must not be null.");

		this.internalTypeLineParser = internalTypeLineParser;
		this.violationConsumer = violationConsumer;
		this.lineParser = new NoBlock();
	}

	// #begin PARSE SUPPORT

	/**
	 * Parses the specified line.
	 * <p>
	 * As soon as a new {@link Violation} is created it is handed to the {@link Consumer} specified during
	 * construction.
	 *
	 * @param line
	 * 		the line to parse
	 */
	public void parseLine(String line) {
		Objects.requireNonNull(line, "The argument 'line' must not be null.");
		lineParser = lineParser.parseLine(line);
		lineParser.logLine(line);
	}

	/**
	 * Informs the parser that parsing is done (for now).
	 * <p>
	 * If a violation is currently being created, calling this method will build it.
	 */
	public void finish() {
		lineParser = lineParser.parseLine("");
	}

	private LineParserState determineWhetherNewBlockStarted(String line) {
		Optional<String> asFirstBlockLine = parseAsFirstBlockLine(line);
		if (asFirstBlockLine.isPresent())
			return new BlockBegan(asFirstBlockLine.get());
		else
			return new NoBlock();
	}

	private static Optional<String> parseAsFirstBlockLine(String line) {
		Matcher firstLineMatcher = REPORTED_TYPE_PATTERN.matcher(line);
		boolean isFirstLine = firstLineMatcher.matches();
		if (isFirstLine)
			return Optional.of(firstLineMatcher.group(1));
		else
			return Optional.empty();
	}

	// #end PARSE SUPPORT

	// #begin PARSE STATE MACHINE

	private interface LineParserState {

		LineParserState parseLine(String line);

		void logLine(String line);
	}

	/**
	 * There is currently no violations block.
	 * <p>
	 * The next line may start a new block if it is a {@link #REPORTED_TYPE_PATTERN REPORTED_TYPE}.
	 */
	private class NoBlock implements LineParserState {

		@Override
		public LineParserState parseLine(String line) {
			return determineWhetherNewBlockStarted(line);
		}

		@Override
		public void logLine(String line) {
			MojoLogging.logger().debug(format(MESSAGE_PARSED_LINE, MESSAGE_MARKER_UNKNOWN_LINE, line));
		}

	}

	/**
	 * A block began and a violation is being build.
	 * <p>
	 * The block can either be continued with an internal dependency or may end. If it ends:
	 * <ul>
	 * <li>the violation which is currently being build is finished and handed to the {@link #violationConsumer}
	 * <li>a new block might start with the next line ~> transition to new {@link BlockBegan}
	 * <li>some other lines might occur ~> transition to {@link NoBlock}
	 * </ul>
	 */
	private class BlockBegan implements LineParserState {

		private final ViolationBuilder violationBuilder;

		public BlockBegan(String fullyQualifiedClassName) {
			assert fullyQualifiedClassName != null : "The argument 'fullyQualifiedClassName' must not be null.";

			Type dependent = Type.of(fullyQualifiedClassName);
			violationBuilder = Violation.buildForDependent(dependent);
		}

		@Override
		public LineParserState parseLine(String line) {
			assert line != null : "The argument 'line' must not be null.";

			boolean lineCouldBeProcessed = processLine(line);
			return computeNextState(line, lineCouldBeProcessed);
		}

		private boolean processLine(String line) {
			Optional<InternalType> parsedInternalType = internalTypeLineParser.parseLine(line);
			parsedInternalType.ifPresent(violationBuilder::addDependency);
			return parsedInternalType.isPresent();
		}

		private LineParserState computeNextState(String line, boolean lineCouldBeProcessed) {
			if (lineCouldBeProcessed)
				return this;
			else {
				finishViolation();
				return determineWhetherNewBlockStarted(line);
			}
		}

		private void finishViolation() {
			Violation violation = violationBuilder.build();
			violationConsumer.accept(violation);
		}

		@Override
		public void logLine(String line) {
			MojoLogging.logger().debug(format(MESSAGE_PARSED_LINE, MESSAGE_MARKER_JDEPS_LINE, line));
		}

	}

	// #end PARSE STATE MACHINE
}
