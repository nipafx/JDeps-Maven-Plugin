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

	private final Function<Result, Stream<DependencyRule>> toRuleTransformer;
	private final Function<DependencyRule, Stream<String>> toLinesTransformer;
	private final Writer writer;

	/**
	 * Creates a new output strategy, relying on the specified functions to do most of the work.
	 *
	 * @param toRuleTransformer
	 * 		transforms a {@link Result} to a stream of {@link DependencyRule}s
	 * @param toLinesTransformer
	 * 		transforms dependency rules to lines
	 * @param writer
	 * 		writes lines to a file
	 */
	public RuleOutputStrategy(
			Function<Result, Stream<DependencyRule>> toRuleTransformer,
			Function<DependencyRule, Stream<String>> toLinesTransformer,
			Writer writer) {
		this.toRuleTransformer =
				requireNonNull(toRuleTransformer, "The argument 'toRuleTransformer' must not be null.");
		this.toLinesTransformer =
				requireNonNull(toLinesTransformer, "The argument 'toLinesTransformer' must not be null.");
		this.writer = requireNonNull(writer, "The argument 'writer' must not be null.");
	}

	@Override
	public void output(Result result) throws MojoFailureException {
		Stream<String> lines = getDependencyRuleLines(result);
		writeDependencyRuleLines(lines);
	}

	private Stream<String> getDependencyRuleLines(Result result) {
		return toRuleTransformer
				.apply(result)
				.sorted(comparing(DependencyRule::getDependent).thenComparing(DependencyRule::getSeverity))
				.flatMap(toLinesTransformer);
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
