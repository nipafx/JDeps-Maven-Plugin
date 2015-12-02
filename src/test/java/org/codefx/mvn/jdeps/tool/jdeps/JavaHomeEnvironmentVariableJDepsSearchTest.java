package org.codefx.mvn.jdeps.tool.jdeps;

import org.junit.Test;

import java.nio.file.Path;
import java.util.Optional;

/**
 * Tests {@link JavaHomeEnvironmentVariableJDepsSearch}.
 */
public class JavaHomeEnvironmentVariableJDepsSearchTest {

	/*
	 * A real test is not possible without mocking the file system and system properties.
	 */

	@Test
	@SuppressWarnings("javadoc")
	public void search_stateUnknown_throwNoException() throws Exception {
		Optional<Path> jDeps = new JavaHomeEnvironmentVariableJDepsSearch().search();
		// it is generally unknown whether this search can find jdeps so we can not assert anything
		System.out.println(jDeps);
	}

}
