package org.codefx.maven.plugin.jdeps.result;

import org.apache.maven.plugin.MojoFailureException;
import org.codefx.maven.plugin.jdeps.rules.DependencyRule;

import java.io.IOException;
import java.util.function.Function;
import java.util.stream.Stream;

import static java.util.Comparator.comparing;
import static java.util.Objects.requireNonNull;

/**
 * Interprets a result's violations as dependency rules and writes them to a file.
 */
public class RuleOutputStrategy implements ResultOutputStrategy {

	private final Function<Result, Stream<DependencyRule>> getRulesFromResult;
	private final Function<DependencyRule, Stream<String>> convertRuleToLines;
	private final Writer writer;

	/**
	 * Creates a new output strategy, relying on the specified functions to do most of the work.
	 *
	 * @param getRulesFromResult
	 * 		transforms a {@link Result} to a stream of {@link DependencyRule}s
	 * @param convertRuleToLines
	 * 		transforms dependency rules to lines
	 * @param writer
	 * 		writes lines to a file
	 */
	public RuleOutputStrategy(
			Function<Result, Stream<DependencyRule>> getRulesFromResult,
			Function<DependencyRule, Stream<String>> convertRuleToLines,
			Writer writer) {
		this.getRulesFromResult =
				requireNonNull(getRulesFromResult, "The argument 'getRulesFromResult' must not be null.");
		this.convertRuleToLines =
				requireNonNull(convertRuleToLines, "The argument 'convertRuleToLines' must not be null.");
		this.writer = requireNonNull(writer, "The argument 'writer' must not be null.");
	}

	@Override
	public void output(Result result) throws MojoFailureException {
		Stream<String> lines = getDependencyRuleLines(result);
		writeDependencyRuleLines(lines);
	}

	private Stream<String> getDependencyRuleLines(Result result) {
		return getRulesFromResult
				.apply(result)
				.sorted(comparing(DependencyRule::getDependent).thenComparing(DependencyRule::getSeverity))
				.flatMap(convertRuleToLines);
	}

	private void writeDependencyRuleLines(Stream<String> dependencyRuleLines) throws MojoFailureException {
		try {
			writer.write(dependencyRuleLines);
		} catch (IOException ex) {
			throw new MojoFailureException(ex.getMessage(), ex.getCause());
		}
	}

	/**
	 * Writes a stream of lines to a file.
	 */
	public interface Writer {

		void write(Stream<String> lines) throws IOException;

	}

}
