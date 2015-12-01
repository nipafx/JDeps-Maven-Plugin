package org.codefx.mvn.jdeps.rules;

/**
 * Defines how packages are interpreted; see {@link DependencyJudge} for details.
 */
public enum PackageInclusion {

	/**
	 * Packages are interpreted as officially specified, i.e. one can never contain another.
	 */
	FLAT,

	/**
	 * Packages are interpreted like folders, i.e. packages can contain each other.
	 */
	HIERARCHICAL

}
