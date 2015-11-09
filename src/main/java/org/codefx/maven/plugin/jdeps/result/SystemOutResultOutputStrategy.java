package org.codefx.maven.plugin.jdeps.result;

import org.apache.maven.plugin.MojoExecutionException;
import org.codefx.maven.plugin.jdeps.dependency.Violation;

import java.util.stream.Stream;

/**
 * A {@link ResultOutputStrategy} which simply prints to {@link System#out}.
 */
public class SystemOutResultOutputStrategy implements ResultOutputStrategy {

	@Override
	public void output(Result result) throws MojoExecutionException {
		output("IGNORE", result.violationsToIgnore());
		output("INFORM", result.violationsToInform());
		output("WARN:", result.violationsToWarn());
		output("FAIL:", result.violationsToFail());
	}

	private static void output(String header, Stream<Violation> violations) {
		System.out.println("\n\n" + header + ":");
		violations
				.map(Violation::toMultiLineString)
				.forEach(System.out::println);
	}

}
