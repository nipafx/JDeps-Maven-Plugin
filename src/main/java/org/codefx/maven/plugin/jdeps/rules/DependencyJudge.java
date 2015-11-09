package org.codefx.maven.plugin.jdeps.rules;

import org.codefx.maven.plugin.jdeps.dependency.InternalType;
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
	Severity judgeSeverity(Type dependent, InternalType dependency);

}
