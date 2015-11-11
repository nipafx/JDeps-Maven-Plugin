package org.codefx.maven.plugin.jdeps.result;

import org.apache.maven.plugin.MojoFailureException;

public interface ResultOutputStrategy {

	void output(Result result) throws MojoFailureException;

}
