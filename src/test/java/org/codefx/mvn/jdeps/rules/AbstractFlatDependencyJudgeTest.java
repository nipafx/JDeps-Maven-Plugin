package org.codefx.mvn.jdeps.rules;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Abstract superclass for tests of hierarchical {@link DependencyJudge} implementations.
 */
public abstract class AbstractFlatDependencyJudgeTest extends AbstractDependencyJudgeTest {

	@Test
	public void judgeSeverity_dependentCoveredByRuleForSuperPackage_ruleIsNotApplied() {
		DependencyJudge judge = builder().
				withDefaultSeverity(Severity.INFORM)
				.addDependency("com", "sun.misc.Unsafe", Severity.FAIL)
				.build();

		Severity severity = judge.judgeSeverity("com.foo.Bar", "sun.misc.Unsafe");
		assertThat(severity).isSameAs(Severity.INFORM);
	}

	@Test
	public void judgeSeverity_dependencyCoveredByRuleForSuperPackage_ruleIsNotApplied() {
		DependencyJudge judge = builder().
				withDefaultSeverity(Severity.INFORM)
				.addDependency("com.foo.Bar", "sun", Severity.FAIL)
				.build();

		Severity severity = judge.judgeSeverity("com.foo.Bar", "sun.misc.Unsafe");
		assertThat(severity).isSameAs(Severity.INFORM);
	}

	@Test
	public void judgeSeverity_bothCoveredByRuleForSuperPackage_ruleIsNotApplied() {
		DependencyJudge judge = builder().
				withDefaultSeverity(Severity.INFORM)
				.addDependency("com", "sun", Severity.FAIL)
				.build();

		Severity severity = judge.judgeSeverity("com.foo.Bar", "sun.misc.Unsafe");
		assertThat(severity).isSameAs(Severity.INFORM);
	}

}
