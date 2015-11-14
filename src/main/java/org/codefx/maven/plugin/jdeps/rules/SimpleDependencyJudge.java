package org.codefx.maven.plugin.jdeps.rules;

import static java.util.Objects.requireNonNull;

/**
 * A {@link DependencyJudge} that judges all dependencies with the severity specified during construction.
 */
public class SimpleDependencyJudge implements DependencyJudge {

	private final Severity severity;

	/**
	 * Creates a new instance that judges all dependencies with the specified severity
	 *
	 * @param severity
	 * 		the severity to use for all sependencies
	 */
	public SimpleDependencyJudge(Severity severity) {
		this.severity = requireNonNull(severity, "The argument 'severity' must not be null.");
	}

	@Override
	public Severity judgeSeverity(String dependentName, String dependencyName) {
		return severity;
	}

}
