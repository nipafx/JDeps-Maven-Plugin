package org.codefx.maven.plugin.jdeps.rules;

import org.codefx.maven.plugin.jdeps.dependency.Type;

/**
 * Judges the severity of individual dependencies.
 */
public interface DependencyJudge {

	/**
	 * Indicates whether the specified dependency is forbidden or not.
	 *
	 * @param dependent
	 *            the type which depends on the the other type
	 * @param dependency
	 *            the type upon which the {@code dependent} depends
	 * @return the severity of the dependency {@code dependent -> dependency}
	 */
	default Severity judgeSeverity(Type dependent, Type dependency) {
		return judgeSeverity(dependent.getFullyQualifiedName(), dependency.getFullyQualifiedName());
	}

	/**
	 * Indicates whether the specified dependency is forbidden or not.
	 *
	 * @param dependentName
	 *            fully qualified name of the type or package which depends on the the other
	 * @param dependencyName
	 *            fully qualified name of the type or package upon which the {@code dependent} depends
	 * @return the severity of the dependency {@code dependent -> dependency}
	 */
	Severity judgeSeverity(String dependentName, String dependencyName);

}
