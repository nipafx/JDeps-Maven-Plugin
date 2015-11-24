package org.codefx.maven.plugin.jdeps.mojo;

import com.google.common.collect.ImmutableList;
import org.codefx.maven.plugin.jdeps.rules.DependencyRule;
import org.codehaus.plexus.classworlds.launcher.ConfigurationException;

import java.util.Optional;
import java.util.function.IntFunction;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static java.lang.String.format;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.joining;

/**
 * Parses rules of the form
 * {@code com.foo.Bar -> sun.misc.Unsafe: WARN} and {@code com.fo.Bar on sun.misc.Unsafe: WARN}.
 */
class ArrowRuleParser {

	private static final String ERROR_MESSAGE_LINE_INVALID_RULE = "The line '%s' defines no valid rule.";
	private static final String ERROR_MESSAGE_MULTIPLE_RULES =
			"The line '%s' defines multiple rules. Please separate rules by a newline.";

	/**
	 * Pattern to separate a string spanning multiple lines.
	 */
	private static final Pattern LINE_PATTERN = Pattern.compile("^", Pattern.MULTILINE);

	/**
	 * Pattern to match a rule of the form {@code dependent -> dependency: severity}.
	 */
	private static final Pattern ARROW_RULE_PATTERN = Pattern.compile(
			"\\s*(\\S*)\\s*" + Arrow.REGULAR_EXPRESSION_MATCHER + "\\s*(\\S*)\\s*:\\s*(\\S*)\\s*");

	/**
	 * Format for {@code dependent -> dependency: severity}, where {@code dependent}, {@code dependency}, the arrow,
	 * and {@code severity} are provided as arguments to {@link String#format(String, Object...) String.format}.
	 */
	private static final String ARROW_RULE_FORMAT = "%s %s %s: %s";

	/**
	 * Parses the rules in the specified string and returns a list of the created {@link DependencyRule}s.
	 *
	 * @param rules
	 * 		a string that defines dependency rules; individual rules must be separated by a newline; empty lines are
	 * 		allowed
	 *
	 * @return a list of parsed rules
	 *
	 * @throws ConfigurationException
	 * 		if a non-empty line could not be parsed
	 */
	public static ImmutableList<DependencyRule> parseRules(String rules) throws ConfigurationException {
		requireNonNull(rules, "The argument 'rules' must not be null.");
		if (rules.trim().isEmpty())
			return ImmutableList.of();

		ImmutableList.Builder<DependencyRule> ruleList = ImmutableList.builder();
		for (String ruleLine : LINE_PATTERN.split(rules)) {
			parseRuleLine(ruleLine).ifPresent(ruleList::add);
		}
		return ruleList.build();
	}

	private static Optional<DependencyRule> parseRuleLine(String ruleLine) throws ConfigurationException {
		if (ruleLine.trim().isEmpty())
			return Optional.empty();

		Matcher ruleMatcher = ARROW_RULE_PATTERN.matcher(ruleLine);
		final Optional<DependencyRule> rule;
		if (ruleMatcher.find())
			rule = Optional.of(extractRuleFromCurrentMatch(ruleMatcher::group));
		else
			throw new ConfigurationException(format(ERROR_MESSAGE_LINE_INVALID_RULE, ruleLine.trim()));

		// disallow two rules in the same line
		if (ruleMatcher.find())
			throw new ConfigurationException(format(ERROR_MESSAGE_MULTIPLE_RULES, ruleLine.trim()));

		return rule;
	}

	private static DependencyRule extractRuleFromCurrentMatch(IntFunction<String> getGroup)
			throws ConfigurationException {
		try {
			return DependencyRule.of(getGroup.apply(1), getGroup.apply(3), getGroup.apply(4));
		} catch (IllegalArgumentException ex) {
			throw new ConfigurationException(ex.getMessage());
		}
	}

	/**
	 * Formats the specified dependency rules as arrow strings that can later be parsed by this parser.
	 *
	 * @param linePrefix
	 * 		the prefix to use for every line; must only consist of whitespace
	 * @param arrow
	 * 		the arrow text to use
	 * @param dependencyRules
	 * 		the rules to convert to string
	 *
	 * @return a multiline arrow rule string
	 */
	public static String rulesToArrowStrings(
			String linePrefix, Arrow arrow, Stream<DependencyRule> dependencyRules) {
		requireNonNull(linePrefix, "The argument 'linePrefix' must not be null.");
		if (!linePrefix.trim().isEmpty())
			throw new IllegalArgumentException("The argument 'linePrefix' must only consist of whitespace.");
		requireNonNull(arrow, "The argument 'arrow' must not be null.");
		requireNonNull(dependencyRules, "The argument 'dependencyRules' must not be null.");

		return dependencyRules
				.map(rule -> ruleToArrowString(linePrefix, arrow, rule))
				.collect(joining("\n"));
	}

	private static String ruleToArrowString(
			String linePrefix, Arrow arrow, DependencyRule rule) {
		return linePrefix + format(
				ARROW_RULE_FORMAT, rule.getDependent(), arrow.text(), rule.getDependency(), rule.getSeverity());
	}

}
