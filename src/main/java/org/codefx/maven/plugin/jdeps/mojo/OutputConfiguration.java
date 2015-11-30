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

	private final boolean outputRules;
	private final RuleOutputFormat format;
	private final String filePath;

	public OutputConfiguration(
			boolean outputRules, RuleOutputFormat format, String filePath) {
		this.outputRules = requireNonNull(outputRules, "The argument 'outputRules' must not be null.");
		this.format = requireNonNull(format, "The argument 'format' must not be null.");
		this.filePath = requireNonNull(filePath, "The argument 'filePath' must not be null.");
	}

	public ResultOutputStrategy createOutputStrategy() {
		if (outputRules)
			return createRuleOutputStrategy();
		else
			return createMojoOutputStrategy();
	}

	private ResultOutputStrategy createRuleOutputStrategy() {
		StaticContent outputFormatStaticContent = format.getStaticContent(DEFAULT_INDENT);

		Function<DependencyRule, Stream<String>> toLinesTransformer =
				format.getToLinesTransformer(outputFormatStaticContent);
		RuleWriter writer = new RuleWriter(Paths.get(filePath), outputFormatStaticContent);

		return new RuleOutputStrategy(ViolationsToRuleTransformer::transform, toLinesTransformer, writer::write);
	}

	private ResultOutputStrategy createMojoOutputStrategy() {
		return new MojoOutputStrategy();
	}

}
