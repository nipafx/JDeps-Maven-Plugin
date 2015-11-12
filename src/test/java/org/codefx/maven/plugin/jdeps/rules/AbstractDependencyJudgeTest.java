package org.codefx.maven.plugin.jdeps.rules;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Abstract superclass for tests of hierarchical {@link DependencyJudge} implementations.
 */
public abstract class AbstractDependencyJudgeTest {

	// #begin BUILDER

	@Test(expected = NullPointerException.class)
	public void addDependency_ruleNull_throwsException() {
		builder().addDependency(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void addDependency_alreadyDefinedWithDifferentSeverity_throwsException() {
		builder().
				withDefaultSeverity(Severity.FAIL)
				.addDependency(DependencyRule.of("com.foo.bar", "sun.misc.Unsafe", Severity.INFORM))
				.addDependency(DependencyRule.of("com.foo.bar", "sun.misc.Unsafe", Severity.WARN));
	}

	@Test
	public void addDependency_alreadyDefinedWithSameSeverity_throwsException() {
		builder().
				withDefaultSeverity(Severity.FAIL)
				.addDependency(DependencyRule.of("com.foo.bar", "sun.misc.Unsafe", Severity.WARN))
				.addDependency(DependencyRule.of("com.foo.bar", "sun.misc.Unsafe", Severity.WARN));
	}

	@Test(expected = IllegalStateException.class)
	public void build_alreadyCalled_throwsException() {
		DependencyJudgeBuilder builder = builder().withDefaultSeverity(Severity.FAIL);
		builder.build();
		builder.build();
	}

	// #end BUILDER

	// #begin JUDGE

	@Test
	public void judgeSeverity_noRules_defaultValue() {
		for (Severity defaultSeverity : Severity.values()) {
			DependencyJudge judge = builder().
					withDefaultSeverity(defaultSeverity).build();
			// class -> class
			assertThat(judge.judgeSeverity("com.foo.Bar", "sun.misc.Unsafe")).isSameAs(defaultSeverity);
			// class -> package
			assertThat(judge.judgeSeverity("com.foo.Bar", "sun.misc")).isSameAs(defaultSeverity);
			assertThat(judge.judgeSeverity("com.foo.Baz", "sun")).isSameAs(defaultSeverity);
			// package -> class
			assertThat(judge.judgeSeverity("com.foo", "sun.misc.Unsafe")).isSameAs(defaultSeverity);
			assertThat(judge.judgeSeverity("com", "sun.misc.Unsafe")).isSameAs(defaultSeverity);
			// package -> package
			assertThat(judge.judgeSeverity("com.foo", "sun.misc")).isSameAs(defaultSeverity);
			assertThat(judge.judgeSeverity("com.foo", "sun.misc")).isSameAs(defaultSeverity);
		}
	}

	@Test
	public void judgeSeverity_exactMatch_definedDefaultSeverity() {
		DependencyJudge judge = builder().
				withDefaultSeverity(Severity.INFORM)
				// this rule should be applied
				.addDependency("com.foo.Bar", "sun.misc.Unsafe", Severity.FAIL)
				.build();

		Severity severity = judge.judgeSeverity("com.foo.Bar", "sun.misc.Unsafe");
		assertThat(severity).isSameAs(Severity.FAIL);
	}

	@Test
	public void judgeSeverity_dependentOccursInMoreSpecialRule_ruleIsNotApplied() {
		DependencyJudge judge = builder().
				withDefaultSeverity(Severity.INFORM)
				// the rule defined for "com.foo.Bar" MUST NOT be applied to "com.foo"
				.addDependency("com.foo.Bar", "sun.misc.Unsafe", Severity.FAIL)
				.build();

		Severity severity = judge.judgeSeverity("com.foo", "sun.misc.Unsafe");
		assertThat(severity).isSameAs(Severity.INFORM);
	}

	@Test
	public void judgeSeverity_dependentOccursInSamePackageRule_ruleIsNotApplied() {
		DependencyJudge judge = builder().
				withDefaultSeverity(Severity.INFORM)
				// the rule defined for "com.foo.Baz" MUST NOT be applied to "com.foo.Bar"
				.addDependency("com.foo.Baz", "sun.misc.Unsafe", Severity.FAIL)
				.build();

		Severity severity = judge.judgeSeverity("com.foo", "sun.misc.Unsafe");
		assertThat(severity).isSameAs(Severity.INFORM);
	}

	@Test
	public void judgeSeverity_dependentCoveredByMoreGeneralRule_ruleIsApplied() {
		DependencyJudge judge = builder().
				withDefaultSeverity(Severity.INFORM)
				// this rule should be applied
				.addDependency("com.foo", "sun.misc.Unsafe", Severity.FAIL)
				.build();

		Severity severity = judge.judgeSeverity("com.foo.Bar", "sun.misc.Unsafe");
		assertThat(severity).isSameAs(Severity.FAIL);
	}

	@Test
	public void judgeSeverity_dependencyOccursInMoreSpecialRule_ruleIsNotApplied() {
		DependencyJudge judge = builder().
				withDefaultSeverity(Severity.INFORM)
				// this rule defined for "sun.misc.Unsafe" MUST NOT be applied to "sun.misc"
				.addDependency("com.foo.Bar", "sun.misc.Unsafe", Severity.FAIL)
				.build();

		Severity severity = judge.judgeSeverity("com.foo.Bar", "sun.misc");
		assertThat(severity).isSameAs(Severity.INFORM);
	}

	@Test
	public void judgeSeverity_dependencyCoveredByMoreGeneralRule_ruleIsApplied() {
		DependencyJudge judge = builder().
				withDefaultSeverity(Severity.INFORM)
				// this rule should be applied
				.addDependency("com.foo.Bar", "sun.misc", Severity.FAIL)
				.build();

		Severity severity = judge.judgeSeverity("com.foo.Bar", "sun.misc.Unsafe");
		assertThat(severity).isSameAs(Severity.FAIL);
	}

	@Test
	public void judgeSeverity_bothCoveredByMoreGeneralRule_ruleIsApplied() {
		DependencyJudge judge = builder().
				withDefaultSeverity(Severity.INFORM)
				// this rule should be applied
				.addDependency("com.foo", "sun.misc", Severity.FAIL)
				.build();

		Severity severity = judge.judgeSeverity("com.foo.Bar", "sun.misc.Unsafe");
		assertThat(severity).isSameAs(Severity.FAIL);
	}

	@Test
	public void judgeSeverity_dependentCoveredByTwoRules_mostSpecialRuleIsApplied() {
		DependencyJudge judge = builder().
				withDefaultSeverity(Severity.INFORM)
				.addDependency("com", "sun.misc", Severity.WARN)
				// this rule should be applied
				.addDependency("com.foo", "sun.misc", Severity.FAIL)
				.addDependency("com.foo.misc.Bar", "sun.misc", Severity.WARN)
				.build();

		Severity severity = judge.judgeSeverity("com.foo.misc", "sun.misc.Unsafe");
		assertThat(severity).isSameAs(Severity.FAIL);
	}

	@Test
	public void judgeSeverity_dependencyCoveredByTwoRules_mostSpecialRuleIsApplied() {
		DependencyJudge judge = builder().
				withDefaultSeverity(Severity.INFORM)
				.addDependency("com.foo.Bar", "sun", Severity.WARN)
				// this rule should be applied
				.addDependency("com.foo.Bar", "sun.misc", Severity.FAIL)
				.addDependency("com.foo.Bar", "sun.misc.util.Unsafe", Severity.WARN)
				.build();

		Severity severity = judge.judgeSeverity("com.foo.Bar", "sun.misc.util");
		assertThat(severity).isSameAs(Severity.FAIL);
	}

	@Test
	public void judgeSeverity_bothCoveredByTwoRules_mostSpecialRuleIsApplied() {
		DependencyJudge judge = builder().
				withDefaultSeverity(Severity.INFORM)
				.addDependency("com", "sun.misc", Severity.WARN)
				.addDependency("com.foo", "sun", Severity.WARN)
				// this rule should be applied
				.addDependency("com.foo", "sun.misc", Severity.FAIL)
				.addDependency("com.foo", "sun.misc.util.Unsafe", Severity.WARN)
				.addDependency("com.foo.misc.Bar", "sun.misc", Severity.WARN)
				.build();

		Severity severity = judge.judgeSeverity("com.foo.misc", "sun.misc.util");
		assertThat(severity).isSameAs(Severity.FAIL);
	}

	@Test
	public void judgeSeverity_bestMatchingDependentHasNoRuleForDependency_moreGeneralRuleIsApplied() {
		DependencyJudge judge = builder().
				withDefaultSeverity(Severity.INFORM)
				// this rule should be applied
				.addDependency("com.foo", "sun.misc", Severity.FAIL)
				// this rule matches the dependant but has no rule for the dependency, so it should not be applied
				.addDependency("com.foo.Bar", "sun.misc.Unsafe", Severity.WARN)
				.build();

		Severity severity = judge.judgeSeverity("com.foo.Bar", "sun.misc");
		assertThat(severity).isSameAs(Severity.FAIL);

		// for demonstration purposes, check that the second rule does apply given a matching dependency
		assertThat(judge.judgeSeverity("com.foo.Bar", "sun.misc.Unsafe")).isSameAs(Severity.WARN);
	}

	// #end JUDGE

	/**
	 * @return the builder for the {@code DependencyJudgeBuilder} tested by this class
	 */
	protected abstract DependencyJudgeBuilder builder();

}
