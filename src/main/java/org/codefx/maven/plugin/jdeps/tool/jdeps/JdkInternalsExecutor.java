package org.codefx.maven.plugin.jdeps.tool.jdeps;

import org.codefx.maven.plugin.jdeps.parse.ViolationParser;
import org.codehaus.plexus.util.cli.CommandLineException;
import org.codehaus.plexus.util.cli.CommandLineUtils;
import org.codehaus.plexus.util.cli.CommandLineUtils.StringStreamConsumer;
import org.codehaus.plexus.util.cli.Commandline;

import java.io.BufferedReader;
import java.io.StringReader;
import java.nio.file.Path;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static java.lang.String.format;
import static org.codefx.maven.plugin.jdeps.mojo.MojoLogging.logger;

/**
 * Executes "jdeps -jdkinternals".
 */
public class JdkInternalsExecutor {

	private final Path jDepsExecutable;
	private final Path pathToCheckedFiles;
	private final Consumer<String> jDepsOutputConsumer;

	/**
	 * Creates a new executor.
	 *
	 * @param jDepsExecutable
	 *            path to the JDeps executable
	 * @param folderToScan
	 *            the path to the folder which jdeps will scan
	 * @param jDepsOutputConsumer
	 *            consumer of jdeps' output (line by line)
	 */
	public JdkInternalsExecutor(Path jDepsExecutable, Path folderToScan, Consumer<String> jDepsOutputConsumer) {
		Objects.requireNonNull(jDepsExecutable, "The argument 'jDepsExecutable' must not be null.");
		Objects.requireNonNull(folderToScan, "The argument 'pathToCheckedFiles' must not be null.");
		Objects.requireNonNull(jDepsOutputConsumer, "The argument 'jDepsOutputConsumer' must not be null.");

		this.jDepsExecutable = jDepsExecutable;
		this.pathToCheckedFiles = folderToScan;
		this.jDepsOutputConsumer = jDepsOutputConsumer;
	}

	// #begin EXECUTE JDEPS

	/**
	 * Executes jdeps.
	 *
	 * @throws CommandLineException
	 *             if running jdeps failed or the tool returned with an error
	 */
	public void execute() throws CommandLineException {
		Commandline jDepsCommand = createJDepsCommand(jDepsExecutable);
		execute(jDepsCommand);
	}

	private Commandline createJDepsCommand(Path jDepsExecutable) {
		Commandline jDepsCommand = new Commandline();
		jDepsCommand.setExecutable(jDepsExecutable.toAbsolutePath().toString());
		jDepsCommand.createArg().setValue("-jdkinternals");
		jDepsCommand.createArg().setFile(pathToCheckedFiles.toFile());
		return jDepsCommand;
	}

	private void execute(Commandline jDepsCommand) throws CommandLineException {
		StringStreamConsumer errorConsoleConsumer = new StringStreamConsumer();

		logger().debug(format("Running JDeps: %s", jDepsCommand));
		logger().debug(String.format(
				"(JDeps output is forwarded here. "
						+ "Lines are marked: %s = recognized as dependency; %s = not recognized.)",
				ViolationParser.MESSAGE_MARKER_JDEPS_LINE,
				ViolationParser.MESSAGE_MARKER_UNKNOWN_LINE));

		int exitCode = CommandLineUtils.executeCommandLine(
				jDepsCommand, jDepsOutputConsumer::accept, errorConsoleConsumer);

		logger().debug(format("JDeps completed with exit code %d.", exitCode));

		if (exitCode != 0)
			throwCommandLineException(jDepsCommand, exitCode, errorConsoleConsumer.getOutput());
	}

	private static void throwCommandLineException(Commandline jDepsCommand, int exitCode, String errorOutput)
			throws CommandLineException {
		StringBuilder message = new StringBuilder("JDeps returned with exit code '" + exitCode + "'.\n");
		message.append("\t Executed command: "
				+ CommandLineUtils.toString(jDepsCommand.getCommandline()).replaceAll("'", ""));
		message.append("\t Error output:\n");
		streamLines(errorOutput).forEachOrdered(errorLine -> message.append("\t\t " + errorLine + "\n"));

		throw new CommandLineException(message.toString());
	}

	private static Stream<String> streamLines(String lines) {
		return new BufferedReader(new StringReader(lines)).lines();
	}

	// #end EXECUTE JDEPS

}
