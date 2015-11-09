package org.codefx.maven.plugin.jdeps.rules;

import org.codefx.maven.plugin.jdeps.dependency.InternalType;
import org.codefx.maven.plugin.jdeps.dependency.Type;

/**
 * A {@link DependencyJudge} which judges all dependencies as {@link Severity#FAIL}.
 */
public class AllFailDependencyJudge implements DependencyJudge {

	@Override
	public Severity judgeSeverity(Type dependent, InternalType dependency) {
		return Severity.FAIL;
	}

}
