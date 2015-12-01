package org.codefx.mvn.jdeps.result;

import org.codefx.mvn.jdeps.dependency.Violation;
import org.codefx.mvn.jdeps.mojo.MojoLogging;
import org.codefx.mvn.jdeps.tool.PairCollector;
import org.codefx.mvn.jdeps.tool.PairCollector.Pair;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static java.lang.String.format;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.summingInt;

/**
 * A {@link ResultOutputStrategy} that uses the Mojos facilities to log violations.
 */
public class LogResultOutputStrategy implements ResultOutputStrategy {

	private static final String MESSAGE_ABOUT_JDEPS =
			"JDeps reported dependencies on JDK-internal APIs. ";
	private static final String MESSAGE_NO_DEPENDENCIES =
			"JDeps reported no dependencies on JDK-internal APIs.";

	private static final String MESSAGE_SUMMARIZE_DEPENDENCIES =
			MESSAGE_ABOUT_JDEPS + "Configured for SUMMARY are %1$s.";
	private static final String MESSAGE_INFORM_DEPENDENCIES =
			MESSAGE_ABOUT_JDEPS + "Configured to INFORM are %1$s:\n%2$s";
	private static final String MESSAGE_WARN_DEPENDENCIES =
			MESSAGE_ABOUT_JDEPS + "Configured to WARN are %1$s:\n%2$s";
	static final String MESSAGE_FAIL_DEPENDENCIES =
			MESSAGE_ABOUT_JDEPS + "Configured to FAIL are %1$s:\n%2$s";

	@Override
	public void output(Result result) {
		MojoLogging.logger().debug("Printing analysis results...");

		int violationsCount = logNumberOfViolationsToSummarize(result);
		violationsCount += logViolationsToInform(result);
		violationsCount += logViolationsToWarn(result);
		violationsCount += logViolationsToFail(result);

		if (violationsCount == 0)
			logZeroDependencies(message -> MojoLogging.logger().info(message));
	}

	private int logNumberOfViolationsToSummarize(Result result) {
		return countAndIfViolationsExist(
				result.violationsToSummarize(),
				(count, ignoredList) -> MojoLogging.logger().info(format(MESSAGE_SUMMARIZE_DEPENDENCIES, count)));
	}

	private int logViolationsToInform(Result result) {
		return logViolations(
				result.violationsToInform(), MESSAGE_INFORM_DEPENDENCIES,
				message -> MojoLogging.logger().info(message));
	}

	private int logViolationsToWarn(Result result) {
		return logViolations(
				result.violationsToWarn(), MESSAGE_WARN_DEPENDENCIES, message -> MojoLogging.logger().warn(message));
	}

	private int logViolationsToFail(Result result) {
		return logViolations(
				result.violationsToFail(), MESSAGE_FAIL_DEPENDENCIES, message -> MojoLogging.logger().error(message));
	}

	private int logViolations(Stream<Violation> violations, String messageFormat, Consumer<String> log) {
		return countAndIfViolationsExist(
				violations,
				(count, list) -> log.accept(format(messageFormat, count, list)));
	}

	private int countAndIfViolationsExist(
			Stream<Violation> violations, BiConsumer<Integer, String> handleViolations) {
		Pair<Integer, String> countAndMessage = violations
				.collect(
						PairCollector.pairing(
								summingInt(violation -> violation.getInternalDependencies().size()),
								mapping(Violation::toMultiLineString, joining("\n"))));
		if (countAndMessage.first == 0)
			return 0;

		handleViolations.accept(countAndMessage.first, countAndMessage.second);
		return countAndMessage.first;
	}

	private void logZeroDependencies(Consumer<String> log) {
		log.accept(MESSAGE_NO_DEPENDENCIES);
	}

}
