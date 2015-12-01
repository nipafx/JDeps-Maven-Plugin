package org.codefx.mvn.jdeps.mojo;

import org.codefx.mvn.jdeps.result.FailBuildResultOutputStrategy;
import org.codefx.mvn.jdeps.result.LogResultOutputStrategy;
import org.codefx.mvn.jdeps.result.ResultOutputStrategy;
import org.codefx.mvn.jdeps.result.RuleOutputFormat;
import org.codefx.mvn.jdeps.result.RuleOutputStrategy;
import org.codefx.mvn.jdeps.result.RuleOutputStrategy.Writer;
import org.codefx.mvn.jdeps.result.ViolationsToRuleTransformer;
import org.codefx.mvn.jdeps.tool.LineWriter;
import org.codefx.mvn.jdeps.tool.LineWriter.IfFileExists;
import org.codefx.mvn.jdeps.tool.LineWriter.StaticContent;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static java.util.Objects.requireNonNull;
import static org.codefx.mvn.jdeps.mojo.MojoLogging.logger;

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
		return new RuleOutputStrategy(
				ViolationsToRuleTransformer::transform,
				format.getToLinesTransformer(outputFormatStaticContent),
				createLineWriter(outputFormatStaticContent));
	}

	private Writer createLineWriter(StaticContent outputFormatStaticContent) {
		Path file = getFile(filePath);
		LineWriter lineWriter = new LineWriter(file, IfFileExists.APPEND_NEW_CONTENT, outputFormatStaticContent);
		return lines -> {
			logger().debug(String.format("Starting to write rules to '%s' ...", file));
			lineWriter.write(lines);
			logger().info(String.format("Rules were written to '%s'.", file));
		};
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
