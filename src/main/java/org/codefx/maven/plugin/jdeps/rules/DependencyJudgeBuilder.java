package org.codefx.maven.plugin.jdeps.rules;

/**
 * Builds a {@link DependencyJudge}.
 */
public interface DependencyJudgeBuilder {

	/**
	 * Sets the specified severity as the default severity used by the created judge.
	 *
	 * @param defaultSeverity
	 * 		the default severity to set
	 *
	 * @return this builder
	 */
	DependencyJudgeBuilder withDefaultSeverity(Severity defaultSeverity);

	/**
	 * Adds the specified dependency rule to the created judge.
	 *
	 * @param dependentName
	 * 		fully qualified name of the type or package which depends on the the other
	 * @param dependencyName
	 * 		fully qualified name of the type or package upon which the {@code dependent} depends
	 * @param severity
	 * 		the severity of the dependency {@code dependent -> dependency}
	 *
	 * @return this builder
	 */
	default DependencyJudgeBuilder addDependency(String dependentName, String dependencyName, Severity severity) {
		return addDependency(DependencyRule.of(dependentName, dependencyName, severity));
	}

	/**
	 * Adds the specified dependency rule to the created judge.
	 *
	 * @param rule
	 * 		the rule to add
	 *
	 * @return this builder
	 */
	DependencyJudgeBuilder addDependency(DependencyRule rule);

	/**
	 * @return a new dependency judge
	 *
	 * @throws IllegalStateException
	 * 		if called more than once
	 */
	DependencyJudge build();

}
