package org.codefx.mvn.jdeps.result;

import com.google.common.collect.Sets;
import org.apache.maven.plugin.MojoFailureException;
import org.codefx.mvn.jdeps.dependency.Violation;
import org.codefx.mvn.jdeps.mojo.MojoLogging;

import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Stream;

import static java.text.MessageFormat.format;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.summingInt;

/**
 * A {@link ResultOutputStrategy} that uses the Mojos facilities to report violations, i.e. the logger and exceptions.
 */
public class MojoOutputStrategy implements ResultOutputStrategy {

	private static final String MESSAGE_SUMMARIZE_DEPENDENCIES =
			"JDeps reported {0} dependencies on JDK-internal APIs that are configured to be ignored.";
	private static final String MESSAGE_INFORM_DEPENDENCIES =
			"JDeps reported {0} dependencies on JDK-internal APIs that are configured to be logged:\n{1}";
	private static final String MESSAGE_WARN_DEPENDENCIES =
			"JDeps reported {0} dependencies on JDK-internal APIs that are configured to be warned about:\n{1}";
	private static final String MESSAGE_FAIL_DEPENDENCIES =
			"JDeps reported {0} dependencies on JDK-internal APIs that are configured to fail the build:\n{1}";
	private static final String MESSAGE_NO_DEPENDENCIES =
			"JDeps reported no dependencies on JDK-internal APIs.";

	@Override
	public void output(Result result) throws MojoFailureException {
		MojoLogging.logger().debug("Printing analysis results...");

		int violationsCount = logNumberOfViolationsToSummarize(result);
		violationsCount += logViolationsToInform(result);
		violationsCount += logViolationsToWarn(result);
		// for better readability violations to fail are both logged and thrown as an exception
		violationsCount += logViolationsToFail(result);
		throwExceptionForViolationsToFail(result);

		// note that the previous line might have thrown an exception in which case this is never executed
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
				result.violationsToInform(), MESSAGE_INFORM_DEPENDENCIES, message -> MojoLogging.logger().info(message));
	}

	private int logViolationsToWarn(Result result) {
		return logViolations(
				result.violationsToWarn(), MESSAGE_WARN_DEPENDENCIES, message -> MojoLogging.logger().warn(message));
	}

	private int logViolationsToFail(Result result) {
		return logViolations(
				result.violationsToFail(), MESSAGE_FAIL_DEPENDENCIES, message -> MojoLogging.logger().error(message));
	}

	private int throwExceptionForViolationsToFail(Result result) throws MojoFailureException {
		return countAndIfViolationsExist(
				result.violationsToFail(),
				(count, list) -> {
					throw new MojoFailureException(format(MESSAGE_FAIL_DEPENDENCIES, count, list));
				});
	}

	private int logViolations(Stream<Violation> violations, String messageFormat, Consumer<String> log) {
		return countAndIfViolationsExist(
				violations,
				(count, list) -> log.accept(format(messageFormat, count, list)));
	}

	private <E extends Exception> int countAndIfViolationsExist(
			Stream<Violation> violations, HandleViolations<E> handleViolations) throws E {
		Pair<Integer, String> countAndMessage = violations
				.collect(
						PairCollector.pairing(
								summingInt(violation -> violation.getInternalDependencies().size()),
								mapping(Violation::toMultiLineString, joining("\n"))));
		if (countAndMessage.first == 0)
			return 0;

		handleViolations.handle(countAndMessage.first, countAndMessage.second);
		return countAndMessage.first;
	}

	private void logZeroDependencies(Consumer<String> log) {
		log.accept(MESSAGE_NO_DEPENDENCIES);
	}

	@FunctionalInterface
	private interface HandleViolations<E extends Exception> {

		void handle(int count, String message) throws E;

	}

	/*
	 * TODO: The following should be polished, tested and placed somewhere else.
	 */

	private static class Pair<A, B> {

		final A first;
		final B second;

		Pair(A first, B second) {
			this.first = first;
			this.second = second;
		}
	}

	/**
	 * Uses two collectors on a stream of pairs to create a pair of collection results.
	 *
	 * @param <T>
	 * 		the type of input elements to the reduction operation
	 * @param <A>
	 * 		the result type of the first reduction operation
	 * @param <B>
	 * 		the result type of the second reduction operation
	 * @param <CA>
	 * 		the mutable accumulation type of the first reduction operation
	 * @param <CB>
	 * 		the mutable accumulation type of the second reduction operation
	 */
	private static class PairCollector<T, A, B, CA, CB> implements Collector<T, Pair<CA, CB>, Pair<A, B>> {

		private final Collector<T, CA, A> firstCollector;
		private final Collector<T, CB, B> secondCollector;

		private PairCollector(Collector<T, CA, A> firstCollector, Collector<T, CB, B> secondCollector) {
			this.firstCollector = firstCollector;
			this.secondCollector = secondCollector;
		}

		public static <T, A, B, CA, CB> Collector<T, ?, Pair<A, B>> pairing(
				Collector<T, CA, A> firstCollector, Collector<T, CB, B> secondCollector) {
			return new PairCollector<>(firstCollector, secondCollector);
		}

		@Override
		public Supplier<Pair<CA, CB>> supplier() {
			return () -> new Pair<>(firstCollector.supplier().get(), secondCollector.supplier().get());
		}

		@Override
		public BiConsumer<Pair<CA, CB>, T> accumulator() {
			return (containers, newValue) -> {
				firstCollector.accumulator().accept(containers.first, newValue);
				secondCollector.accumulator().accept(containers.second, newValue);
			};
		}

		@Override
		public BinaryOperator<Pair<CA, CB>> combiner() {
			return (containers, otherContainers) -> {
				CA firstNewContainer = firstCollector.combiner().apply(containers.first, otherContainers.first);
				CB secondNewContainer = secondCollector.combiner().apply(containers.second, otherContainers.second);
				return new Pair<>(firstNewContainer, secondNewContainer);
			};
		}

		@Override
		public Function<Pair<CA, CB>, Pair<A, B>> finisher() {
			return containers -> {
				A firstResult = firstCollector.finisher().apply(containers.first);
				B secondResult = secondCollector.finisher().apply(containers.second);
				return new Pair<>(firstResult, secondResult);
			};
		}

		@Override
		public Set<Characteristics> characteristics() {
			return Sets.intersection(firstCollector.characteristics(), secondCollector.characteristics());
		}

	}

}
