package org.codefx.maven.plugin.jdeps.rules;

/**
 * The severity of a dependency as defined by the configuration.
 */
public enum Severity {

	/**
	 * Ignore individual dependencies.
	 */
	IGNORE,

	/**
	 * Summarize these dependencies.
	 */
	SUMMARIZE,

	/**
	 * Inform about individual dependencies.
	 */
	INFORM,

	/**
	 * Warn about individual dependencies.
	 */
	WARN,

	/**
	 * Fail the build for such dependencies.
	 */
	FAIL

}
