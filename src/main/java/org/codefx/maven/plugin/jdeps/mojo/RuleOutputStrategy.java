package org.codefx.maven.plugin.jdeps.mojo;

import org.apache.maven.plugin.MojoFailureException;
import org.codefx.maven.plugin.jdeps.dependency.Violation;
import org.codefx.maven.plugin.jdeps.result.Result;
import org.codefx.maven.plugin.jdeps.result.ResultOutputStrategy;
import org.codefx.maven.plugin.jdeps.rules.DependencyRule;
import org.codefx.maven.plugin.jdeps.rules.Severity;

import java.util.function.Consumer;
import java.util.stream.Stream;

import static java.util.Comparator.comparing;
import static java.util.Objects.requireNonNull;

/**
 * Converts a {@link Result}'s {@link Violation}s into {@link DependencyRule}s and passes them into the consumer
 * specified during construction.
 */
public class RuleOutputStrategy implements ResultOutputStrategy {

	private final Consumer<DependencyRule> ruleOutput;

	/**
	 * Creates a new rule output using the specified consumer as a sink for the generated rules.
	 *
	 * @param ruleOutput
	 * 		the output for the generated rules
	 */
	public RuleOutputStrategy(Consumer<DependencyRule> ruleOutput) {
		this.ruleOutput = requireNonNull(ruleOutput, "The argument 'ruleOutput' must not be null.");
	}

	@Override
	public void output(Result result) throws MojoFailureException {
		requireNonNull(result, "The argument 'result' must not be null.");
		Severity.stream()
				.flatMap(severity -> dependencyRulesForSeverity(result, severity))
				.sorted(comparing(DependencyRule::getDependent)
						.thenComparing(DependencyRule::getSeverity))
				.forEachOrdered(ruleOutput);
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
