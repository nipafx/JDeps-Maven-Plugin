package org.codefx.maven.plugin.jdeps.result;

import com.google.common.collect.ImmutableList;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.codefx.maven.plugin.jdeps.dependency.Violation;

import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.joining;

public class Result {

	final ImmutableList<Violation> violations;

	Result(ImmutableList<Violation> violations) {
		this.violations = requireNonNull(violations, "The argument 'violations' must not be null.");
	}

}
