package org.codefx.mvn.jdeps.result;

import org.codefx.mvn.jdeps.dependency.Violation;
import org.codefx.mvn.jdeps.tool.PairCollector.Pair;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static java.lang.String.format;
import static java.util.stream.Collectors.reducing;
import static java.util.stream.Collectors.summingInt;
import static org.codefx.mvn.jdeps.mojo.MojoLogging.logger;
import static org.codefx.mvn.jdeps.tool.PairCollector.pairing;

/**
 * A {@link ResultOutputStrategy} that uses the Mojos facilities to log violations.
 */
public class LogResultOutputStrategy implements ResultOutputStrategy {

	static final String MESSAGE_ABOUT_JDEPS =
			"JDeps reported dependencies on JDK-internal APIs. ";
	private static final String MESSAGE_NO_DEPENDENCIES =
			"JDeps reported no dependencies on JDK-internal APIs.";

	private static final String MESSAGE_SUMMARIZE_DEPENDENCIES =
			MESSAGE_ABOUT_JDEPS + "Configured for SUMMARY are %1$s.";
	private static final String MESSAGE_INFORM_DEPENDENCIES =
			MESSAGE_ABOUT_JDEPS + "Configured to INFORM are %1$s:";
	private static final String MESSAGE_WARN_DEPENDENCIES =
			MESSAGE_ABOUT_JDEPS + "Configured to WARN are %1$s:";
	private static final String MESSAGE_FAIL_DEPENDENCIES =
			MESSAGE_ABOUT_JDEPS + "Configured to FAIL are %1$s:";

	@Override
	public void output(Result result) {
		logger().debug("Printing analysis results...");

		int violationsCount = logNumberOfViolationsToSummarize(result);
		violationsCount += logViolationsToInform(result);
		violationsCount += logViolationsToWarn(result);
		violationsCount += logViolationsToFail(result);

		if (violationsCount == 0)
			logZeroDependencies(message -> logger().info(message));
	}

	private int logNumberOfViolationsToSummarize(Result result) {
		return countAndIfViolationsExist(
				result.violationsToSummarize(),
				(count, ignoredViolationDetails) -> logger().info(format(MESSAGE_SUMMARIZE_DEPENDENCIES, count)));
	}

	private int logViolationsToInform(Result result) {
		return logViolations(
				result.violationsToInform(), MESSAGE_INFORM_DEPENDENCIES, message -> logger().info(message));
	}

	private int logViolationsToWarn(Result result) {
		return logViolations(
				result.violationsToWarn(), MESSAGE_WARN_DEPENDENCIES, message -> logger().warn(message));
	}

	private int logViolationsToFail(Result result) {
		return logViolations(
				result.violationsToFail(), MESSAGE_FAIL_DEPENDENCIES, message -> logger().error(message));
	}

	private int logViolations(Stream<Violation> violations, String messageFormat, Consumer<String> log) {
		return countAndIfViolationsExist(
				violations,
				(count, violationLines) -> logMessage(messageFormat, count, violationLines, log));
	}

	private void logMessage(
			String messageFormat, int violationsCount, Stream<String> violationLines, Consumer<String> log) {
		log.accept(format(messageFormat, violationsCount));
		violationLines.forEach(log);
	}

	private int countAndIfViolationsExist(
			Stream<Violation> violations, BiConsumer<Integer, Stream<String>> handleViolations) {
		Pair<Integer, Stream<String>> countAndMessage = violations
				.collect(pairing(
						summingInt(violation -> violation.getInternalDependencies().size()),
						reducing(Stream.of(), Violation::toLines, Stream::concat)));
		if (countAndMessage.first == 0)
			return 0;

		handleViolations.accept(countAndMessage.first, countAndMessage.second);
		return countAndMessage.first;
	}

	private void logZeroDependencies(Consumer<String> log) {
		log.accept(MESSAGE_NO_DEPENDENCIES);
	}

}
