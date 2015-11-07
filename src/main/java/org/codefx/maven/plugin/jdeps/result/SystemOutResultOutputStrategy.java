package org.codefx.maven.plugin.jdeps.result;

import org.apache.maven.plugin.MojoExecutionException;

public class SystemOutResultOutputStrategy implements ResultOutputStrategy {

	@Override
	public void output(Result result) throws MojoExecutionException {
		result.violations.forEach(System.out::println);
	}
}
