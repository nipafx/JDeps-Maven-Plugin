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
	 * @throws CommandLineException
	 * 		if the jdeps executable could not be found, running the tool failed or it returned with an error
	 */
	public static Result execute(File scannedFolder, DependencyRulesConfiguration dependencyRulesConfiguration)
			throws CommandLineException, ConfigurationException {

		ResultBuilder resultBuilder = createResultBuilder(dependencyRulesConfiguration);
		createJdkInternalsExecutor(scannedFolder, resultBuilder).execute();
		return resultBuilder.build();
	}

	private static ResultBuilder createResultBuilder(DependencyRulesConfiguration dependencyRulesConfiguration)
			throws ConfigurationException {
		DependencyJudge dependencyJudge = dependencyRulesConfiguration.createJudge();
		return new ResultBuilder(dependencyJudge);
	}

	private static JdkInternalsExecutor createJdkInternalsExecutor(File scannedFolder, ResultBuilder resultBuilder)
			throws CommandLineException {
		Path jDepsExecutable = findJDepsExecutable();
		Path folderToScan = Paths.get(scannedFolder.toURI());
		ViolationParser violationParser = new ViolationParser(resultBuilder::addViolation);
		return new JdkInternalsExecutor(jDepsExecutable, folderToScan, violationParser::parseLine);
	}

	private static Path findJDepsExecutable() throws CommandLineException {
		JDepsSearch jDepsSearch = new ComposedJDepsSearch();
		return jDepsSearch.search().orElseThrow(() -> new CommandLineException("Could not locate JDeps executable."));
	}

}
