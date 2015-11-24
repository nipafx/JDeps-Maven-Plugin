package org.codefx.maven.plugin.jdeps.mojo;

import org.apache.commons.lang3.NotImplementedException;
import org.codefx.maven.plugin.jdeps.result.MojoOutputStrategy;
import org.codefx.maven.plugin.jdeps.result.ResultOutputStrategy;

import static java.util.Objects.requireNonNull;

class OutputConfiguration {

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
			return createMojoOutputstrategy();
	}

	private ResultOutputStrategy createRuleOutputStrategy() {
		throw new NotImplementedException("Not yet implemented!");
	}

	private ResultOutputStrategy createMojoOutputstrategy() {
		return new MojoOutputStrategy();
	}

}
