package org.codefx.maven.plugin.jdeps.rules;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Abstract superclass for tests of hierarchical {@link DependencyJudge} implementations.
 */
public abstract class AbstractHierarchicalDependencyJudgeTest extends AbstractDependencyJudgeTest {

	@Test
	public void judgeSeverity_dependentCoveredByRuleForSuperPackage_ruleIsApplied() {
		DependencyJudge judge = builder().
				withDefaultSeverity(Severity.INFORM)
				.addDependency("com", "sun.misc.Unsafe", Severity.FAIL)
				.build();

		Severity severity = judge.judgeSeverity("com.foo.Bar", "sun.misc.Unsafe");
		assertThat(severity).isSameAs(Severity.FAIL);
	}

	@Test
	public void judgeSeverity_dependencyCoveredByRuleForSuperPackage_ruleIsApplied() {
		DependencyJudge judge = builder().
				withDefaultSeverity(Severity.INFORM)
				.addDependency("com.foo.Bar", "sun", Severity.FAIL)
				.build();

		Severity severity = judge.judgeSeverity("com.foo.Bar", "sun.misc.Unsafe");
		assertThat(severity).isSameAs(Severity.FAIL);
	}

	@Test
	public void judgeSeverity_bothCoveredByRuleForSuperPackage_ruleIsApplied() {
		DependencyJudge judge = builder().
				withDefaultSeverity(Severity.INFORM)
				.addDependency("com", "sun", Severity.FAIL)
				.build();

		Severity severity = judge.judgeSeverity("com.foo.Bar", "sun.misc.Unsafe");
		assertThat(severity).isSameAs(Severity.FAIL);
	}

}
