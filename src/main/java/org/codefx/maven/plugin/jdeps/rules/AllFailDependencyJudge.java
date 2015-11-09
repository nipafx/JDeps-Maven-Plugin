package org.codefx.maven.plugin.jdeps.rules;

/**
 * A {@link DependencyJudge} which judges all dependencies as {@link Severity#FAIL}.
 */
public class AllFailDependencyJudge implements DependencyJudge {

	@Override
	public Severity judgeSeverity(String dependentName, String dependencyName) {
		return Severity.FAIL;
	}

}
