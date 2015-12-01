package org.codefx.mvn.jdeps.rules;

import java.util.Arrays;
import java.util.stream.Stream;

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
	FAIL;

	/**
	 * @return a stream of all severities
	 */
	public static Stream<Severity> stream() {
		return Arrays.stream(values());
	}

}
