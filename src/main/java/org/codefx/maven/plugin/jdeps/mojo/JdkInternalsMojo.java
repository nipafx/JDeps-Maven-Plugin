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
import java.util.Collections;
import java.util.List;

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

	@Parameter
	private List<XmlRule> xmlDependencyRules;

	@Parameter
	private List<String> arrowDependencyRules;

	@Parameter(defaultValue = "${project.build.outputDirectory}", readonly = true)
	private File buildOutputDirectory;

	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		MojoLogging.registerLogger(this::getLog);
		executePlugin();
		MojoLogging.unregisterLogger();
	}

	private void executePlugin() throws MojoExecutionException, MojoFailureException {
		Result result = executeJDeps();
		new MojoResultOutputStrategy().output(result);
	}

	private Result executeJDeps() throws MojoExecutionException {
		try {
			return JdkInternalsExecutionService.execute(
					buildOutputDirectory,
					new DependencyRulesConfiguration(
							emptyListIfNull(xmlDependencyRules),
							emptyListIfNull(arrowDependencyRules))
			);
		} catch (CommandLineException ex) {
			throw new MojoExecutionException("Executing 'jdeps -jdkinternals' failed.", ex);
		} catch (ConfigurationException ex) {
			throw new MojoExecutionException("Parsing the configuration failed.", ex);
		}
	}

	private static <E> List<E> emptyListIfNull(List<E> list) {
		return list == null ? Collections.emptyList() : list;
	}

}
