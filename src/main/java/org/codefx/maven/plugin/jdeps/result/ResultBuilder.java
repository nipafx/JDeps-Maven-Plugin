package org.codefx.maven.plugin.jdeps.result;

import com.google.common.collect.ImmutableList;
import org.codefx.maven.plugin.jdeps.dependency.InternalType;
import org.codefx.maven.plugin.jdeps.dependency.Type;
import org.codefx.maven.plugin.jdeps.dependency.Violation;
import org.codefx.maven.plugin.jdeps.rules.DependencyJudge;
import org.codefx.maven.plugin.jdeps.rules.Severity;

import static java.util.Objects.requireNonNull;

/**
 * Builds a result, judging the violation's severities with a {@link DependencyJudge} specified during construction.
 * <p>
 * Builder instances can be reused; it is safe to call {@link #build()} multiple times to build multiple lists in
 * series. Each new list contains all the elements of the ones created before it.
 */
public class ResultBuilder {

	private final DependencyJudge judge;
	private final ImmutableList.Builder<AnnotatedViolation> violations;

	/**
	 * Creates a new result builder.
	 *
	 * @param judge
	 * 		the dependency judge to use
	 */
	public ResultBuilder(DependencyJudge judge) {
		this.judge = requireNonNull(judge, "The argument 'judge' must not be null.");
		violations = ImmutableList.builder();
	}

	/**
	 * Adds the specified violation to the result currently being built.
	 *
	 * @param violation
	 * 		the violation to add
	 */
	public ResultBuilder addViolation(Violation violation) {
		Type dependent = violation.getDependent();
		ImmutableList.Builder<AnnotatedInternalType> internalDependencies = ImmutableList.builder();
		violation
				.getInternalDependencies().stream()
				.map(dependency -> annotateWithSeverity(dependent, dependency))
				.forEach(internalDependencies::add);
		violations.add(AnnotatedViolation.of(dependent, internalDependencies.build()));

		return this;
	}

	private AnnotatedInternalType annotateWithSeverity(Type dependent, InternalType dependency) {
		Severity severity = judge.judgeSeverity(dependent, dependency);
		return AnnotatedInternalType.of(dependency, severity);
	}

	/**
	 * Builds a new result.
	 * <p>
	 * Can be called repeatedly, each call creating a new result containing all the violations added since this builder
	 * was created.
	 *
	 * @return a new result
	 */
	public Result build() {
		return new Result(violations.build());
	}

}
