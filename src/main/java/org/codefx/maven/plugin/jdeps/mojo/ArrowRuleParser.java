package org.codefx.maven.plugin.jdeps.mojo;

import com.google.common.collect.ImmutableList;
import org.codefx.maven.plugin.jdeps.rules.DependencyRule;
import org.codehaus.plexus.classworlds.launcher.ConfigurationException;

import java.util.Optional;
import java.util.function.IntFunction;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.String.format;
import static java.util.Objects.requireNonNull;

/**
 * Parses rules of the form
 * {@code com.foo.Bar -> sun.misc.Unsafe: WARN} and {@code com.fo.Bar on sun.misc.Unsafe: WARN}.
 */
public class ArrowRuleParser {

	private static final String ERROR_MESSAGE_LINE_INVALID_RULE = "The line '%s' defines no valid rule.";
	private static final String ERROR_MESSAGE_MULTIPLE_RULES =
			"The line '%s' defines multiple rules. Please separate rules by a newline.";

	private static final Pattern LINE_PATTERN = Pattern.compile("^", Pattern.MULTILINE);
	private static final Pattern ARROW_RULE_PATTERN = Pattern.compile(
			"\\s*(\\S*)\\s*(->|on)\\s*(\\S*)\\s*:\\s*(\\S*)\\s*");

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
			throw new ConfigurationException(format(ERROR_MESSAGE_LINE_INVALID_RULE, ruleLine));

		if (ruleMatcher.find())
			throw new ConfigurationException(format(ERROR_MESSAGE_MULTIPLE_RULES, ruleLine));

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

}
