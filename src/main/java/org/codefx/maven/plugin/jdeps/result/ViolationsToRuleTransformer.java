package org.codefx.maven.plugin.jdeps.result;

import org.codefx.maven.plugin.jdeps.dependency.Violation;
import org.codefx.maven.plugin.jdeps.rules.DependencyRule;
import org.codefx.maven.plugin.jdeps.rules.Severity;

import java.util.stream.Stream;

import static java.util.Objects.requireNonNull;

/**
 * Converts a {@link Result}'s {@link Violation}s into {@link DependencyRule}s.
 */
public class ViolationsToRuleTransformer {

	/**
	 *
	 * @param result the result containing the violations
	 * @return a stream of {@link DependencyRule}s
	 */
	public static Stream<DependencyRule> transform(Result result) {
		requireNonNull(result, "The argument 'result' must not be null.");
		return Severity.stream()
				.flatMap(severity -> dependencyRulesForSeverity(result, severity));
	}

	private static Stream<DependencyRule> dependencyRulesForSeverity(Result result, Severity severity) {
		return result
				.violationsWithSeverity(severity)
				.flatMap(violation -> dependencyRulesForViolation(severity, violation));
	}

	private static Stream<DependencyRule> dependencyRulesForViolation(Severity severity, Violation violation) {
		return violation
				.getInternalDependencies().stream()
				.map(dependency ->
						DependencyRule.of(
								violation.getDependent().getFullyQualifiedName(),
								dependency.getFullyQualifiedName(),
								severity));
	}

}
