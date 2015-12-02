package org.codefx.mvn.jdeps.result;

import com.google.common.collect.ImmutableList;
import org.codefx.mvn.jdeps.rules.Arrow;
import org.codefx.mvn.jdeps.rules.ArrowRuleParser;
import org.codefx.mvn.jdeps.rules.DependencyRule;
import org.codefx.mvn.jdeps.rules.XmlRule;
import org.codefx.mvn.jdeps.tool.LineWriter.StaticContent;

import java.util.function.Function;
import java.util.stream.Stream;

import static java.lang.String.format;
import static java.util.Objects.requireNonNull;

/**
 * Enumerates the ways in which rules can be output.
 */
public enum RuleOutputFormat {

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
	 * @param indent
	 * 		the string used to indent inner XML, possibly {@code "\t"}
	 *
	 * @return the static content for this output format
	 */
	public StaticContent getStaticContent(String indent) {
		requireNonNull(indent, "The argument 'indent' must not be null.");
		if (!indent.trim().isEmpty())
			throw new IllegalArgumentException("The argument 'indent' must only consist of whitespace.");

		switch (this) {
			case ARROW:
				// intended fall through: both cases have the same prolog and epilog
			case ON:
				return new StaticContent(
						ImmutableList.of("<arrowDependencyRules>", indent + "<arrowRules>"),
						ImmutableList.of(indent + "</arrowRules>", "</arrowDependencyRules>"),
						indent + indent);
			case XML:
				return new StaticContent(
						ImmutableList.of("<xmlDependencyRules>"),
						ImmutableList.of("</xmlDependencyRules>"),
						indent);
			default:
				throw new IllegalArgumentException(format("Unknown format '%s'.", this));
		}
	}

	/**
	 * Creates a function which transforms dependency rules to parsable lines that can be written to a file.
	 *
	 * @param staticContent
	 * 		the static content used for this format
	 *
	 * @return a function mapping a dependency rule to a stream of lines
	 */
	public Function<DependencyRule, Stream<String>> getToLinesTransformer(StaticContent staticContent) {
		requireNonNull(staticContent, "The argument 'staticContent' must not be null.");

		switch (this) {
			case ARROW:
				return rule -> Stream.of(ArrowRuleParser.ruleToArrowString(Arrow.ARROW, rule));
			case ON:
				return rule -> Stream.of(ArrowRuleParser.ruleToArrowString(Arrow.ON, rule));
			case XML:
				return rule -> new XmlRule(rule).toXmlLines(staticContent.indent);
			default:
				throw new IllegalArgumentException(format("Unknown format '%s'.", this));
		}
	}

}
