package org.codefx.maven.plugin.jdeps.mojo;

import org.codefx.maven.plugin.jdeps.mojo.RuleOutputFormat.StaticContent;
import org.codefx.maven.plugin.jdeps.result.MojoOutputStrategy;
import org.codefx.maven.plugin.jdeps.result.ResultOutputStrategy;
import org.codefx.maven.plugin.jdeps.result.RuleOutputStrategy;
import org.codefx.maven.plugin.jdeps.result.RuleWriter;
import org.codefx.maven.plugin.jdeps.result.ViolationsToRuleTransformer;
import org.codefx.maven.plugin.jdeps.rules.DependencyRule;

import java.nio.file.Paths;
import java.util.function.Function;
import java.util.stream.Stream;

import static java.util.Objects.requireNonNull;

class OutputConfiguration {

	private static final String DEFAULT_INDENT = "\t";

	private final boolean outputRulesForViolations;
	private final RuleOutputFormat outputFormat;
	private final String outputPath;

	public OutputConfiguration(
			boolean outputRulesForViolations, RuleOutputFormat outputFormat, String outputPath) {
		this.outputRulesForViolations =
				requireNonNull(outputRulesForViolations, "The argument 'outputRulesForViolations' must not be null.");
		this.outputFormat = requireNonNull(outputFormat, "The argument 'outputFormat' must not be null.");
		this.outputPath = requireNonNull(outputPath, "The argument 'outputPath' must not be null.");
	}

	public ResultOutputStrategy createOutputStrategy() {
		if (outputRulesForViolations)
			return createRuleOutputStrategy();
		else
			return createMojoOutputStrategy();
	}

	private ResultOutputStrategy createRuleOutputStrategy() {
		StaticContent outputFormatStaticContent = outputFormat.getStaticContent(DEFAULT_INDENT);

		Function<DependencyRule, Stream<String>> toLinesTransformer =
				outputFormat.getToLinesTransformer(outputFormatStaticContent);
		RuleWriter writer = new RuleWriter(Paths.get(outputPath), outputFormatStaticContent);

		return new RuleOutputStrategy(ViolationsToRuleTransformer::transform, toLinesTransformer, writer::write);
	}

	private ResultOutputStrategy createMojoOutputStrategy() {
		return new MojoOutputStrategy();
	}

}
