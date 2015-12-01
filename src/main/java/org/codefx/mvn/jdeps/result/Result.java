package org.codefx.mvn.jdeps.result;

import com.google.common.collect.ImmutableList;
import org.codefx.mvn.jdeps.dependency.Violation;
import org.codefx.mvn.jdeps.rules.Severity;

import java.util.Optional;
import java.util.stream.Stream;

import static java.util.Objects.requireNonNull;

/**
 * The result of running JDeps.
 * <p>
 * The violations are made available with a number streams, one for each severity.
 */
public class Result {

	private final ImmutableList<AnnotatedViolation> violations;

	Result(ImmutableList<AnnotatedViolation> violations) {
		this.violations = requireNonNull(violations, "The argument 'violations' must not be null.");
	}

	/**
	 * @param severity
	 * 		the severity to filter by
	 *
	 * @return a stream of violations with the specified severity
	 */
	public Stream<Violation> violationsWithSeverity(Severity severity) {
		return violations.stream()
				.map(violation -> violation.only(severity))
				.filter(Optional::isPresent)
				.map(Optional::get);
	}

	/**
	 * @return a stream of the violations that are configured to be be ignored
	 */
	public Stream<Violation> violationsToIgnore() {
		return violationsWithSeverity(Severity.IGNORE);
	}

	/**
	 * @return a stream of the violations that are configured to be be summarized
	 */
	public Stream<Violation> violationsToSummarize() {
		return violationsWithSeverity(Severity.SUMMARIZE);
	}

	/**
	 * @return a stream of the violations that are configured to be be informed about
	 */
	public Stream<Violation> violationsToInform() {
		return violationsWithSeverity(Severity.INFORM);
	}

	/**
	 * @return a stream of the violations that are configured to be warned about
	 */
	public Stream<Violation> violationsToWarn() {
		return violationsWithSeverity(Severity.WARN);
	}

	/**
	 * @return a stream of the violations that are configured to fail the build
	 */
	public Stream<Violation> violationsToFail() {
		return violationsWithSeverity(Severity.FAIL);
	}

}
