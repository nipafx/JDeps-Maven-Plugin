package org.codefx.maven.plugin.jdeps.tool.jdeps;

import com.google.common.io.Resources;
import org.junit.Ignore;
import org.junit.Test;

import java.nio.file.Path;
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

	private static final Path PATH_TO_JDEPS = Paths.get("/opt/java/jdk8/bin/jdeps");
	private static final Path PATH_TO_SCANNED_FOLDER;

	static {
		Path testProjectPom = Paths.get(Resources.getResource("test-project/pom.xml").getPath());
		PATH_TO_SCANNED_FOLDER = testProjectPom.resolveSibling("target").resolve("classes");
	}

	@Test
	public void execute_pathsExist_printsJDepsOutput() throws Exception {
		// print this class' name as a header for the following JDeps output
		System.out.println("\n# " + getClass().getSimpleName().toUpperCase() + "\n");

		JdkInternalsExecutor executor = new JdkInternalsExecutor(PATH_TO_JDEPS, PATH_TO_SCANNED_FOLDER, System.out::println);
		executor.execute();
	}

}
