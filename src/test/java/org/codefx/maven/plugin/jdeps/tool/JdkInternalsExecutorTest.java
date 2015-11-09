package org.codefx.maven.plugin.jdeps.tool;

import org.junit.Ignore;
import org.junit.Test;

import java.nio.file.Paths;

/**
 * Tests {@link JdkInternalsExecutor}.
 * <p>
 * This is no regular unit/integration test as it relies on correct configuration to pass. If this test fails simply
 * {@link Ignore} it or change the constants to reflect the situation on your system.
 */
//@Ignore
@SuppressWarnings("javadoc")
public class JdkInternalsExecutorTest {

	private static final String PATH_TO_JDEPS = "/opt/java/jdk8/bin/jdeps";
	private static final String PATH_TO_SCANNED_FOLDER = "/home/nipa/Code/MavenLab/target";

	@Test
	public void execute_pathsExist_printsJDepsOutput() throws Exception {
		System.out.println("\n# " + getClass().getSimpleName().toUpperCase());

		JdkInternalsExecutor executor = new JdkInternalsExecutor(
				Paths.get(PATH_TO_JDEPS), Paths.get(PATH_TO_SCANNED_FOLDER), System.out::println);
		executor.execute();
	}

}
