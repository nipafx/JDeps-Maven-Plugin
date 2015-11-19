package org.codefx.maven.plugin.jdeps.mojo;

import com.google.common.collect.ImmutableList;
import org.codefx.maven.plugin.jdeps.rules.DependencyRule;
import org.codefx.maven.plugin.jdeps.rules.Severity;
import org.codehaus.plexus.classworlds.launcher.ConfigurationException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static java.lang.String.format;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.codefx.maven.plugin.jdeps.mojo.ArrowRuleParser.parseRules;

/**
 * Tests {@link ArrowRuleParser}.
 */
public class ArrowRuleParserTest {

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	// #begin PARSE - EDGE CASES

	@Test(expected = NullPointerException.class)
	public void parseRules_rulesNull_throwsException() throws Exception {
		parseRules(null);
	}

	@Test
	public void parseRules_rulesEmpty_emptyList() throws Exception {
		ImmutableList<DependencyRule> rules = parseRules("");
		assertThat(rules).isEmpty();
	}

	// #end PARSE - EDGE CASES

	// #begin PARSE - INVALID CASES

	/*
	 * In general see 'DependencyRuleTest' for the different ways in which rules can be invalid
	 */

	@Test
	public void parseRules_invalidRule_throwsException() throws Exception {
		thrown.expect(ConfigurationException.class);
		thrown.expectMessage("The line 'fooBar' defines no valid rule.");

		parseRules("fooBar");
	}

	@Test
	public void parseRules_invalidArrows_throwsException() throws Exception {
		assertThatThrownBy(
				() -> parseRules("com.foo.Bar ~> sun.misc.Unsafe: WARN"))
				.isInstanceOf(ConfigurationException.class)
				.hasMessageContaining("The line 'com.foo.Bar ~> sun.misc.Unsafe: WARN' defines no valid rule.");

		assertThatThrownBy(
				() -> parseRules("com.foo.Bar to sun.misc.Unsafe: WARN"))
				.isInstanceOf(ConfigurationException.class)
				.hasMessageContaining("The line 'com.foo.Bar to sun.misc.Unsafe: WARN' defines no valid rule.");
	}

	@Test
	public void parseRules_invalidRuleInSecondLine_throwsException() throws Exception {
		thrown.expect(ConfigurationException.class);
		thrown.expectMessage("The line 'fooBar' defines no valid rule.");

		parseRules("com.foo.Bar -> sun.misc.Unsafe: WARN\n"
				+ "fooBar");
	}

	@Test
	public void parseRules_invalidRuleInOneOfManyLines_throwsException() throws Exception {
		thrown.expect(ConfigurationException.class);
		thrown.expectMessage("The line 'fooBar' defines no valid rule.");

		parseRules("com.foo.Bar -> sun.misc.Unsafe: WARN\n"
				+ "fooBar\n"
				+ "com.foo.Bar -> sun.misc: WARN\n"
				+ "com.foo.Bar -> sun: WARN\n");
	}

	@Test
	public void parseRules_twoRulesInOneLine_throwsException() throws Exception {
		String line = "com.foo.Bar -> sun.misc.Unsafe: WARN com.foo.Bar -> sun.misc.Unsafe: WARN";

		thrown.expect(ConfigurationException.class);
		thrown.expectMessage(format("The line '%s' defines multiple rules.", line));

		parseRules(line);
	}

	// #end PARSE - INVALID CASES

	// #begin PARSE - VALID CASES

	@Test
	public void parseRules_singleLineValidArrowRule_returnsRule() throws Exception {
		ImmutableList<DependencyRule> rules = parseRules("com.foo.Bar -> sun.misc.Unsafe: WARN");

		assertThat(rules).hasSize(1);
		assertThat(rules.get(0)).isEqualTo(DependencyRule.of("com.foo.Bar", "sun.misc.Unsafe", Severity.WARN));
	}

	@Test
	public void parseRules_singleLineValidArrowRuleWithSpaces_returnsRule() throws Exception {
		ImmutableList<DependencyRule> rules = parseRules("   com.foo.Bar   ->    sun.misc.Unsafe  :    WARN   ");

		assertThat(rules).hasSize(1);
		assertThat(rules.get(0)).isEqualTo(DependencyRule.of("com.foo.Bar", "sun.misc.Unsafe", Severity.WARN));
	}

	@Test
	public void parseRules_multipleLinesValidArrowRule_returnsRules() throws Exception {
		// note the empty lines and the various kinds of line breaks
		ImmutableList<DependencyRule> rules = parseRules("\n"
				+ "com.foo.Bar -> sun.misc.Unsafe: INFORM\r\n"
				+ "\n\r\n\r"
				+ "com.foo.Bar -> sun.misc.Unsafe: WARN\r"
				+ "com.foo.Bar -> sun.misc.Unsafe: FAIL"
		);

		assertThat(rules).hasSize(3);
		assertThat(rules.get(0)).isEqualTo(DependencyRule.of("com.foo.Bar", "sun.misc.Unsafe", Severity.INFORM));
		assertThat(rules.get(1)).isEqualTo(DependencyRule.of("com.foo.Bar", "sun.misc.Unsafe", Severity.WARN));
		assertThat(rules.get(2)).isEqualTo(DependencyRule.of("com.foo.Bar", "sun.misc.Unsafe", Severity.FAIL));
	}

	@Test
	public void parseRules_singleLineValidOnRule_returnsRule() throws Exception {
		ImmutableList<DependencyRule> rules = parseRules("com.foo.Bar on sun.misc.Unsafe: WARN");

		assertThat(rules).hasSize(1);
		assertThat(rules.get(0)).isEqualTo(DependencyRule.of("com.foo.Bar", "sun.misc.Unsafe", Severity.WARN));
	}

	// #end PARSE - VALID CASES

	// #begin TO STRING

	// TODO: tests for 'rulesToArrowStrings'

	// #end TO STRING

}
