package org.codefx.maven.plugin.jdeps.mojo;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.codefx.maven.plugin.jdeps.dependency.Violation;
import org.codefx.maven.plugin.jdeps.parse.ViolationParser;
import org.codefx.maven.plugin.jdeps.tool.ComposedJDepsSearch;
import org.codefx.maven.plugin.jdeps.tool.JDepsSearch;
import org.codefx.maven.plugin.jdeps.tool.JdkInternalsExecutor;
import org.codehaus.plexus.util.cli.CommandLineException;

import com.google.common.collect.ImmutableList;

/**
 * Orchestrates all bits and pieces which are needed to run "jdeps -jdkInternals" and parse the output.
 */
public class JdkInternalsExecutionService {

	/**
	 * Executes jdeps.
	 * 
	 * @param scannedFolder
	 *            the folder to be scanned
	 * @return a list of all violations
	 * @throws CommandLineException
	 *             if the jdeps executable could not be found, running the tool failed or it returned with an error
	 */
	public static ImmutableList<Violation> execute(File scannedFolder) throws CommandLineException {
		Path jDepsExecutable = findJDepsExecutable();
		ImmutableList.Builder<Violation> violationListBuilder = ImmutableList.builder();
		ViolationParser violationParser = new ViolationParser(violationListBuilder::add);
		JdkInternalsExecutor executor = new JdkInternalsExecutor(
				jDepsExecutable, Paths.get(scannedFolder.toURI()), violationParser::parseLine);

		executor.execute();

		return violationListBuilder.build();
	}

	private static Path findJDepsExecutable() throws CommandLineException {
		JDepsSearch jDepsSearch = new ComposedJDepsSearch();
		return jDepsSearch.search().orElseThrow(() -> new CommandLineException("Could not locate jdeps executable."));
	}

}
