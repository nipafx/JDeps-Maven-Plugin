package org.codefx.maven.plugin.jdeps.result;

import org.apache.maven.plugin.MojoExecutionException;

public interface ResultOutputStrategy {

	void output(Result result) throws MojoExecutionException;

}
