package org.codefx.mvn.jdeps.mojo;

import org.codefx.mvn.jdeps.result.FailBuildResultOutputStrategy;
import org.codefx.mvn.jdeps.result.LogResultOutputStrategy;
import org.codefx.mvn.jdeps.result.ResultOutputStrategy;
import org.codefx.mvn.jdeps.result.RuleOutputFormat;
import org.codefx.mvn.jdeps.result.RuleOutputStrategy;
import org.codefx.mvn.jdeps.result.ViolationsToRuleTransformer;
import org.codefx.mvn.jdeps.rules.DependencyRule;
import org.codefx.mvn.jdeps.tool.LineWriter;
import org.codefx.mvn.jdeps.tool.LineWriter.IfFileExists;
import org.codefx.mvn.jdeps.tool.LineWriter.StaticContent;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.Function;
import java.util.stream.Stream;

import static java.util.Objects.requireNonNull;

class OutputConfiguration {

	private static final String DEFAULT_FILE_NAME = "dependency_rules.xml";
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
		LogResultOutputStrategy logResult = new LogResultOutputStrategy();
		ResultOutputStrategy outputRulesOrFailBuild =
				outputRules ? createRuleOutputStrategy() : createFailingStrategy();

		// always log the result before doing anything else
		return result -> {
			logResult.output(result);
			outputRulesOrFailBuild.output(result);
		};

	}

	private ResultOutputStrategy createRuleOutputStrategy() {
		StaticContent outputFormatStaticContent = format.getStaticContent(DEFAULT_INDENT);

		Function<DependencyRule, Stream<String>> toLinesTransformer =
				format.getToLinesTransformer(outputFormatStaticContent);
		LineWriter writer = new LineWriter(
				getFile(filePath), IfFileExists.APPEND_NEW_CONTENT, outputFormatStaticContent);

		return new RuleOutputStrategy(ViolationsToRuleTransformer::transform, toLinesTransformer, writer::write);
	}

	private static Path getFile(String path) {
		Path outputFile = Paths.get(path);
		if (Files.isDirectory(outputFile))
			return outputFile.resolve(DEFAULT_FILE_NAME);
		else
			return outputFile;
	}

	private ResultOutputStrategy createFailingStrategy() {
		return new FailBuildResultOutputStrategy();
	}

}
