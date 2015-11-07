package org.codefx.maven.plugin.jdeps.result;

import com.google.common.collect.ImmutableList;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.codefx.maven.plugin.jdeps.dependency.Violation;

import java.util.Objects;

import static java.util.stream.Collectors.joining;

public class MojoResultOutputStrategy implements ResultOutputStrategy {

	private final Log log;

	public MojoResultOutputStrategy(Log log) {
		this.log = Objects.requireNonNull(log, "The argument 'log' must not be null.");
	}

	@Override
	public void output(Result result) throws MojoExecutionException {
		if (result.violations.isEmpty())
			logZeroDependencies(log);
		else
			failBuild(result.violations);
	}

	private void logZeroDependencies(Log log) {
		log.info("JDeps reported no dependencies on JDK-internal APIs.");
	}

	private static void failBuild(ImmutableList<Violation> violations) throws MojoExecutionException {
		String message = violations.stream()
				.map(Violation::toMultiLineString)
				.collect(joining("\n", "\nSome classes contain dependencies on JDK-internal API:\n", "\n\n"));
		throw new MojoExecutionException(message);
	}


}
