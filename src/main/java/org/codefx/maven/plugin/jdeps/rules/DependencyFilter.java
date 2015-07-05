package org.codefx.maven.plugin.jdeps.rules;

import org.codefx.maven.plugin.jdeps.dependency.Type;

/**
 * Filters individual dependencies by indicating whether they are forbidden or not.
 */
public interface DependencyFilter {

	/**
	 * Indicates whether the specified dependency is forbidden or not.
	 *
	 * @param dependent
	 *            the type which depends on the the other type
	 * @param dependency
	 *            the type upon which the {@code dependent} depends
	 * @return true if the dependency {@code dependent -> dependency} is forbidden; otherwise false
	 */
	public boolean forbiddenDependency(Type dependent, Type dependency);

}
