package org.codefx.maven.plugin.jdeps.mojo;

import org.codefx.maven.plugin.jdeps.parse.ViolationParser;
import org.codefx.maven.plugin.jdeps.result.Result;
import org.codefx.maven.plugin.jdeps.result.ResultBuilder;
import org.codefx.maven.plugin.jdeps.rules.DependencyJudge;
import org.codefx.maven.plugin.jdeps.tool.ComposedJDepsSearch;
import org.codefx.maven.plugin.jdeps.tool.JDepsSearch;
import org.codefx.maven.plugin.jdeps.tool.JdkInternalsExecutor;
import org.codehaus.plexus.classworlds.launcher.ConfigurationException;
import org.codehaus.plexus.util.cli.CommandLineException;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Orchestrates all bits and pieces which are needed to run "jdeps -jdkInternals" and parse the output.
 */
class JdkInternalsExecutionService {

	/**
	 * Executes jdeps.
	 *
	 * @param scannedFolder
	 * 		the folder to be scanned by JDeps
	 * @param dependencyRulesConfiguration
	 * 		the configuration for the dependency rules
	 *
	 * @return the result of the JDeps run
	 *
	 * @throws CommandLineException
	 * 		if the jdeps executable could not be found, running the tool failed or it returned with an error
	 */
	public static Result execute(
			File scannedFolder, DependencyRulesConfiguration dependencyRulesConfiguration)
			throws CommandLineException, ConfigurationException {
		Path jDepsExecutable = findJDepsExecutable();

		ResultBuilder resultBuilder = createResultBuilder(dependencyRulesConfiguration);
		ViolationParser violationParser = new ViolationParser(resultBuilder::addViolation);
		JdkInternalsExecutor executor = new JdkInternalsExecutor(
				jDepsExecutable, Paths.get(scannedFolder.toURI()), violationParser::parseLine);

		executor.execute();

		return resultBuilder.build();
	}

	private static Path findJDepsExecutable() throws CommandLineException {
		JDepsSearch jDepsSearch = new ComposedJDepsSearch();
		return jDepsSearch.search().orElseThrow(() -> new CommandLineException("Could not locate JDeps executable."));
	}

	private static ResultBuilder createResultBuilder(DependencyRulesConfiguration dependencyRulesConfiguration)
			throws ConfigurationException {
		DependencyJudge dependencyJudge = dependencyRulesConfiguration.createJudge();
		return new ResultBuilder(dependencyJudge);
	}

}
