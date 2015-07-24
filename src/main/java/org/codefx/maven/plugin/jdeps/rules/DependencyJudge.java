package org.codefx.maven.plugin.jdeps.rules;

import org.codefx.maven.plugin.jdeps.dependency.Type;

/**
 * Judges whether individual dependencies are forbidden or not.
 */
public interface DependencyJudge {

	/**
	 * Indicates whether the specified dependency is forbidden or not.
	 *
	 * @param dependent
	 *            the type which depends on the the other type
	 * @param dependency
	 *            the type upon which the {@code dependent} depends
	 * @return true if the dependency {@code dependent -> dependency} is forbidden; otherwise false
	 */
	boolean forbiddenDependency(Type dependent, Type dependency);

}
