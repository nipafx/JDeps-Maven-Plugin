package org.codefx.maven.plugin.jdeps.tool;

import java.nio.file.Paths;

import org.junit.Ignore;
import org.junit.Test;

/**
 * Tests {@link JdkInternalsExecutor}.
 * <p>
 * This is no regular unit/integration test as it relies on correct configuration to pass. If this test fails simply
 * {@link Ignore} it or change the constants to reflect the situation on your system.
 */
//@Ignore
@SuppressWarnings("javadoc")
public class JdkInternalsExecutorTest {

	private static final String PATH_TO_JDEPS = "C:\\Program Files\\Java\\jdk8\\bin\\jdeps.exe";

	private static final String PATH_TO_SCANNED_FOLDER = "D:\\Code\\MavenLab\\target\\";

	@Test
	public void execute_pathsExist_printsJDepsOutput() throws Exception {
		JdkInternalsExecutor executor = new JdkInternalsExecutor(
				Paths.get(PATH_TO_JDEPS), Paths.get(PATH_TO_SCANNED_FOLDER), System.out::println);
		executor.execute();
	}

}
