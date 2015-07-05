package org.codefx.maven.plugin.jdeps.mojo;

import static java.util.stream.Collectors.joining;
import static org.apache.maven.plugins.annotations.LifecyclePhase.VERIFY;
import static org.apache.maven.plugins.annotations.ResolutionScope.COMPILE;

import java.io.File;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.codefx.maven.plugin.jdeps.dependency.Violation;
import org.codehaus.plexus.util.cli.CommandLineException;

import com.google.common.collect.ImmutableList;

/**
 * Runs "jdeps -jdkinternals" and breaks the build if the tool reports dependencies on JDK internal API.
 */
@Mojo(name = "jdkinternals",
		threadSafe = true,
		requiresProject = true,
		defaultPhase = VERIFY,
		requiresDependencyResolution = COMPILE)
public class JdkInternalsMojo extends AbstractMojo {

	@Parameter(defaultValue = "${project.build.outputDirectory}", readonly = true)
	private File outputDirectory;

	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		ImmutableList<Violation> violations = executeJDeps();
		evaluateViolations(violations);
	}

	private ImmutableList<Violation> executeJDeps() throws MojoFailureException {
		try {
			return JdkInternalsExecutionService.execute(outputDirectory);
		} catch (CommandLineException ex) {
			throw new MojoFailureException("Executing 'jdeps -jdkinternals' failed.", ex);
		}
	}

	private void evaluateViolations(ImmutableList<Violation> violations) throws MojoExecutionException {
		if (violations.isEmpty())
			logZeroDependencies();
		else
			failBuild(violations);
	}

	private void logZeroDependencies() {
		getLog().info("JDeps reported no dependencies on JDK-internal APIs.");
	}

	private static void failBuild(ImmutableList<Violation> violations) throws MojoExecutionException {
		String message = violations.stream()
				.map(Violation::toMultiLineString)
				.collect(joining("\n", "\nSome classes contain dependencies on JDK-internal API:\n", "\n\n"));
		throw new MojoExecutionException(message);
	}

}
