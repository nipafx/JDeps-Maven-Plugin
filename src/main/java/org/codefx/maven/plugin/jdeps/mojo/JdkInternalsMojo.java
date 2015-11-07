package org.codefx.maven.plugin.jdeps.mojo;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.codefx.maven.plugin.jdeps.result.MojoResultOutputStrategy;
import org.codefx.maven.plugin.jdeps.result.Result;
import org.codehaus.plexus.util.cli.CommandLineException;

import java.io.File;

import static org.apache.maven.plugins.annotations.LifecyclePhase.VERIFY;
import static org.apache.maven.plugins.annotations.ResolutionScope.COMPILE;

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
		Result result = executeJDeps();
		new MojoResultOutputStrategy(getLog()).output(result);
	}

	private Result executeJDeps() throws MojoFailureException {
		try {
			return JdkInternalsExecutionService.execute(outputDirectory);
		} catch (CommandLineException ex) {
			throw new MojoFailureException("Executing 'jdeps -jdkinternals' failed.", ex);
		}
	}

}
