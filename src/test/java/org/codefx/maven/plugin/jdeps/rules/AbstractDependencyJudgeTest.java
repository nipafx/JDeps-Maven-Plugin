package org.codefx.maven.plugin.jdeps.rules;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Abstract superclass for tests of {@link DependencyJudge} implementations.
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
	public void addDependency_alreadyDefinedWithSameSeverity_throwsNoException() {
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
	public void judgeSeverity_exactMatch_definedSeverity() {
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
				// the rule defined for "com.foo.Bar" MUST NOT be applied to "com.foo.Baz"
				.addDependency("com.foo.Bar", "sun.misc.Unsafe", Severity.FAIL)
				.build();

		Severity severity = judge.judgeSeverity("com.foo.Baz", "sun.misc.Unsafe");
		assertThat(severity).isSameAs(Severity.INFORM);
	}

	@Test
	public void judgeSeverity_dependentCoveredByMoreGeneralRuleForContainingPackage_ruleIsApplied() {
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
	public void judgeSeverity_dependencyOccursInSamePackageRule_ruleIsNotApplied() {
		DependencyJudge judge = builder().
				withDefaultSeverity(Severity.INFORM)
				// this rule defined for "sun.misc.Unsafe" MUST NOT be applied to "sun.misc.BASE64Encoder"
				.addDependency("com.foo.Bar", "sun.misc.Unsafe", Severity.FAIL)
				.build();

		Severity severity = judge.judgeSeverity("com.foo.Bar", "sun.misc.BASE64Encoder");
		assertThat(severity).isSameAs(Severity.INFORM);
	}

	@Test
	public void judgeSeverity_dependencyCoveredByMoreGeneralRuleForContainingPackage_ruleIsApplied() {
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
				.addDependency("com.foo", "sun.misc", Severity.WARN)
				.addDependency("com.foo.Bar", "sun.misc", Severity.FAIL)
				.addDependency("com.foo.Bar.Inner", "sun.misc", Severity.WARN)
				.build();

		Severity severity = judge.judgeSeverity("com.foo.Bar", "sun.misc.Unsafe");
		assertThat(severity).isSameAs(Severity.FAIL);
	}

	@Test
	public void judgeSeverity_dependencyCoveredByTwoRules_mostSpecialRuleIsApplied() {
		DependencyJudge judge = builder().
				withDefaultSeverity(Severity.INFORM)
				.addDependency("com.foo.Bar", "sun.misc", Severity.WARN)
				.addDependency("com.foo.Bar", "sun.misc.Unsafe", Severity.FAIL)
				.addDependency("com.foo.Bar", "sun.misc.Unsafe.Inner", Severity.WARN)
				.build();

		Severity severity = judge.judgeSeverity("com.foo.Bar", "sun.misc.Unsafe");
		assertThat(severity).isSameAs(Severity.FAIL);
	}

	@Test
	public void judgeSeverity_bothCoveredByTwoRules_mostSpecialRuleIsApplied() {
		DependencyJudge judge = builder().
				withDefaultSeverity(Severity.INFORM)
				.addDependency("com.foo", "sun.misc.Unsafe", Severity.WARN)
				.addDependency("com.foo.Bar", "sun.misc", Severity.WARN)
				.addDependency("com.foo.Bar", "sun.misc.Unsafe", Severity.FAIL)
				.addDependency("com.foo.Bar.Inner", "sun.misc.Unsafe", Severity.WARN)
				.addDependency("com.foo.Bar", "sun.misc.Unsafe.Inner", Severity.WARN)
				.build();

		Severity severity = judge.judgeSeverity("com.foo.Bar", "sun.misc.Unsafe");
		assertThat(severity).isSameAs(Severity.FAIL);
	}

	@Test
	public void judgeSeverity_bestMatchingDependentHasNoRuleForDependency_moreGeneralRuleIsApplied() {
		DependencyJudge judge = builder().
				withDefaultSeverity(Severity.INFORM)
				// this rule should be applied
				.addDependency("com.foo", "sun.misc", Severity.FAIL)
				// this rule matches the dependant but has no rule for the dependency, so it should not be applied
				.addDependency("com.foo.Bar", "sun.misc.Unsafe.Inner", Severity.WARN)
				.build();

		Severity severity = judge.judgeSeverity("com.foo.Bar", "sun.misc.Unsafe");
		assertThat(severity).isSameAs(Severity.FAIL);

		// for demonstration purposes, check that the second rule does apply given a matching dependency
		assertThat(judge.judgeSeverity("com.foo.Bar", "sun.misc.Unsafe.Inner")).isSameAs(Severity.WARN);
	}

	@Test
	public void judgeSeverity_dependentCoveredByWildcardRule_ruleIsApplied() {
		DependencyJudge judge = builder().
				withDefaultSeverity(Severity.INFORM)
				.addDependency(DependencyRule.ALL_TYPES_WILDCARD, "sun.misc.Unsafe", Severity.FAIL)
				.build();

		assertThat(judge.judgeSeverity("com.foo.Bar", "sun.misc.Unsafe")).isSameAs(Severity.FAIL);
		assertThat(judge.judgeSeverity("net.Foo", "sun.misc.Unsafe")).isSameAs(Severity.FAIL);
		assertThat(judge.judgeSeverity("org", "sun.misc.Unsafe")).isSameAs(Severity.FAIL);
	}

	@Test
	public void judgeSeverity_dependencyCoveredByWildcardRule_ruleIsApplied() {
		DependencyJudge judge = builder().
				withDefaultSeverity(Severity.INFORM)
				.addDependency("com.foo.Bar", DependencyRule.ALL_TYPES_WILDCARD, Severity.FAIL)
				.build();

		assertThat(judge.judgeSeverity("com.foo.Bar", "sun.misc.Unsafe")).isSameAs(Severity.FAIL);
		assertThat(judge.judgeSeverity("com.foo.Bar", "sun.misc.BASE64Encoder")).isSameAs(Severity.FAIL);
		assertThat(judge.judgeSeverity("com.foo.Bar", "sun.")).isSameAs(Severity.FAIL);
	}

	@Test
	public void judgeSeverity_bothDependenciesCoveredByWildcardRule_ruleIsApplied() {
		DependencyJudge judge = builder().
				withDefaultSeverity(Severity.INFORM)
				.addDependency(DependencyRule.ALL_TYPES_WILDCARD, DependencyRule.ALL_TYPES_WILDCARD, Severity.FAIL)
				.build();

		assertThat(judge.judgeSeverity("com.foo.Bar", "sun.misc.Unsafe")).isSameAs(Severity.FAIL);
		assertThat(judge.judgeSeverity("com.foo.Bar", "sun.")).isSameAs(Severity.FAIL);
		assertThat(judge.judgeSeverity("com", "sun.misc.BASE64Encoder")).isSameAs(Severity.FAIL);
	}

	// #end JUDGE

	/**
	 * @return the builder for the {@code DependencyJudgeBuilder} tested by this class
	 */
	protected abstract DependencyJudgeBuilder builder();

}
