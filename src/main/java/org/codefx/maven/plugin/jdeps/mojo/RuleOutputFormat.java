package org.codefx.maven.plugin.jdeps.mojo;

import org.codefx.maven.plugin.jdeps.rules.DependencyRule;

import java.util.function.Function;
import java.util.stream.Stream;

import static java.lang.String.format;
import static java.util.Objects.requireNonNull;

/**
 * Enumerates the ways in which rules can be output.
 */
enum RuleOutputFormat {

	/**
	 * Full XML; see {@link XmlRule}
	 */
	XML,

	/**
	 * Single line rule {@code dependent -> dependency: severity}; see {@link ArrowRuleParser}.
	 */
	ARROW,

	/**
	 * Single line rule {@code dependent on dependency: severity}; see {@link ArrowRuleParser}.
	 */
	ON;

	/**
	 * Creates a function which transforms dependency rules to parsable lines that can be written to a file.
	 *
	 * @param linePrefix
	 * 		the prefix to use for each line, possibly existing indentation
	 * @param indent
	 * 		the string used to indent inner XML, possible {@code "\t"}
	 *
	 * @return a function mapping a dependency rule to a stream of lines
	 */
	public Function<DependencyRule, Stream<String>> getToLinesTransformer(String linePrefix, String indent) {
		requireNonNull(linePrefix, "The argument 'linePrefix' must not be null.");
		if (!linePrefix.trim().isEmpty())
			throw new IllegalArgumentException("The argument 'linePrefix' must only consist of whitespace.");
		requireNonNull(indent, "The argument 'indent' must not be null.");
		if (!indent.trim().isEmpty())
			throw new IllegalArgumentException("The argument 'indent' must only consist of whitespace.");

		switch (this) {
			case ARROW:
				return rule -> Stream.of(ArrowRuleParser.ruleToArrowString(linePrefix, Arrow.ARROW, rule));
			case ON:
				return rule -> Stream.of(ArrowRuleParser.ruleToArrowString(linePrefix, Arrow.ON, rule));
			case XML:
				return rule -> new XmlRule(rule).toXmlLines(linePrefix, indent);
			default:
				throw new IllegalArgumentException(format("Unknown format '%s'.", this));
		}
	}

}
