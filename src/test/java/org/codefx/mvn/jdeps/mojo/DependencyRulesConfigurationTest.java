package org.codefx.mvn.jdeps.mojo;

import org.codefx.mvn.jdeps.rules.DependencyJudgeBuilder;
import org.codefx.mvn.jdeps.rules.DependencyRule;
import org.codefx.mvn.jdeps.rules.Severity;
import org.codefx.mvn.jdeps.rules.XmlRule;
import org.codehaus.plexus.classworlds.launcher.ConfigurationException;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;

/**
 * Tests {@link DependencyRulesConfiguration}.
 */
public class DependencyRulesConfigurationTest {

	private DependencyJudgeBuilder dependencyJudgeBuilder;

	@Before
	public void createDependencyJudgeBuilder() {
		dependencyJudgeBuilder = mock(DependencyJudgeBuilder.class);
	}

	// #begin XML RULES

	@Test
	public void addXmlRulesToBuilder_invalidRule_ruleNotAddedToBuilder() {
		// see 'DependencyRuleTest' for more details of how rules can be invalid
		XmlRule invalidRule = new XmlRule("", "", Severity.FAIL);

		try {
			DependencyRulesConfiguration.addXmlRulesToBuilder(singletonList(invalidRule), dependencyJudgeBuilder);
			fail();
		} catch (ConfigurationException ex) {
			assertThat(ex).hasMessageContaining("The rule ( -> : FAIL)");
		}

		verifyZeroInteractions(dependencyJudgeBuilder);
	}

	@Test
	public void addXmlRulesToBuilder_validRule_ruleAddedToBuilder() throws Exception {
		XmlRule validXmlRule = new XmlRule("com.foo.bar", "sun.misc.Unsafe", Severity.FAIL);
		DependencyRule validRule = validXmlRule.asDependencyRule();

		DependencyRulesConfiguration.addXmlRulesToBuilder(singletonList(validXmlRule), dependencyJudgeBuilder);
		verify(dependencyJudgeBuilder).addDependency(validRule);
		verifyNoMoreInteractions(dependencyJudgeBuilder);
	}

	@Test
	public void addXmlRulesToBuilder_validRules_rulesAddedToBuilder() throws Exception {
		List<XmlRule> xmlRules = Arrays.asList(
				new XmlRule("com.foo.bar", "sun.misc.Unsafe", Severity.FAIL),
				new XmlRule("com.foo", "sun.misc.Unsafe", Severity.WARN),
				new XmlRule("com", "sun.misc.Unsafe", Severity.INFORM));

		DependencyRulesConfiguration.addXmlRulesToBuilder(xmlRules, dependencyJudgeBuilder);
		verify(dependencyJudgeBuilder).addDependency(xmlRules.get(0).asDependencyRule());
		verify(dependencyJudgeBuilder).addDependency(xmlRules.get(1).asDependencyRule());
		verify(dependencyJudgeBuilder).addDependency(xmlRules.get(2).asDependencyRule());
		verifyNoMoreInteractions(dependencyJudgeBuilder);
	}

	// #end XML RULES

	// #begin ARROW RULES

	@Test
	public void addArrowRulesToBuilder_invalidRule_ruleNotAddedToBuilder() {
		// see 'DependencyRuleTest' for more details of how rules can be invalid
		String invalidRule = "INVALID RULE";

		try {
			DependencyRulesConfiguration.addArrowRulesToBuilder(singletonList(invalidRule), dependencyJudgeBuilder);
			fail();
		} catch (ConfigurationException ex) {
			assertThat(ex).hasMessageContaining("The line 'INVALID RULE' defines no valid rule.");
		}

		verifyZeroInteractions(dependencyJudgeBuilder);
	}

	@Test
	public void addArrowRulesToBuilder_validRule_ruleAddedToBuilder() throws Exception {
		String validArrowRule = "com.foo.bar -> sun.misc.Unsafe: FAIL";
		DependencyRule validRule = DependencyRule.of("com.foo.bar", "sun.misc.Unsafe", Severity.FAIL);

		DependencyRulesConfiguration.addArrowRulesToBuilder(singletonList(validArrowRule), dependencyJudgeBuilder);
		verify(dependencyJudgeBuilder).addDependency(validRule);
		verifyNoMoreInteractions(dependencyJudgeBuilder);
	}

	@Test
	public void addArrowRulesToBuilder_validRules_rulesAddedToBuilder() throws Exception {
		List<String> arrowRules = Arrays.asList(
				"com.foo.bar -> sun.misc.Unsafe: FAIL",
				"com.foo -> sun.misc.Unsafe: WARN",
				"com -> sun.misc.Unsafe: INFORM");
		List<DependencyRule> rules = Arrays.asList(
				DependencyRule.of("com.foo.bar", "sun.misc.Unsafe", Severity.FAIL),
				DependencyRule.of("com.foo", "sun.misc.Unsafe", Severity.WARN),
				DependencyRule.of("com", "sun.misc.Unsafe", Severity.INFORM));

		DependencyRulesConfiguration.addArrowRulesToBuilder(arrowRules, dependencyJudgeBuilder);
		verify(dependencyJudgeBuilder).addDependency(rules.get(0));
		verify(dependencyJudgeBuilder).addDependency(rules.get(1));
		verify(dependencyJudgeBuilder).addDependency(rules.get(2));
		verifyNoMoreInteractions(dependencyJudgeBuilder);
	}

	// #end ARROW RULES

}
