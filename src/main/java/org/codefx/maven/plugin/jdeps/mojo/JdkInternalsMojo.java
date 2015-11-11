package org.codefx.maven.plugin.jdeps.mojo;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.codefx.maven.plugin.jdeps.result.MojoResultOutputStrategy;
import org.codefx.maven.plugin.jdeps.result.Result;
import org.codehaus.plexus.classworlds.launcher.ConfigurationException;
import org.codehaus.plexus.util.cli.CommandLineException;

import java.io.File;
import java.util.Optional;

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

	/**
	 * Indicates which dependencies are allowed and which are forbidden.
	 */
	@Parameter(defaultValue = "", property = "jdkinternals.rules")
	private String dependencyRules;

	@Parameter(defaultValue = "${project.build.outputDirectory}", readonly = true)
	private File buildOutputDirectory;

	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		Result result = executeJDeps();
		new MojoResultOutputStrategy(getLog()).output(result);
	}

	private Result executeJDeps() throws MojoExecutionException {
		try {
			return JdkInternalsExecutionService.execute(
					buildOutputDirectory,
					new DependencyRulesConfiguration(Optional.ofNullable(dependencyRules))
			);
		} catch (CommandLineException ex) {
			throw new MojoExecutionException("Executing 'jdeps -jdkinternals' failed.", ex);
		} catch (ConfigurationException ex) {
			throw new MojoExecutionException("Parsing the configuration failed.", ex);
		}
	}

}
