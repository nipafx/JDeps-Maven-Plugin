package org.codefx.maven.plugin.jdeps.find;

import java.nio.file.Path;
import java.util.Optional;

import org.junit.Test;

/**
 * Tests {@link JavaHomeSystemPropertyJDepsSearch}.
 */
public class JavaHomeSystemPropertyJDepsSearchTest {

	/*
	 * A real test is not possible without mocking the file system and system properties.
	 */

	@Test
	@SuppressWarnings("javadoc")
	public void search_stateUnknown_throwNoException() throws Exception {
		Optional<Path> jDeps = new JavaHomeSystemPropertyJDepsSearch().search();
		// it is generally unknown whether this search can find jdeps so we can not assert anything
		System.out.println(jDeps);
	}

}
