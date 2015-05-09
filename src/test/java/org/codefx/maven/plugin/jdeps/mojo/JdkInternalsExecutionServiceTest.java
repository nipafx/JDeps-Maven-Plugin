package org.codefx.maven.plugin.jdeps.mojo;

import java.io.File;

import org.codefx.maven.plugin.jdeps.dependency.Violation;
import org.junit.Ignore;
import org.junit.Test;

import com.google.common.collect.ImmutableList;

/**
 * Tests {@link JdkInternalsExecutionService}.
 * <p>
 * This is no regular unit/integration test as it relies on correct configuration to pass. If this test fails simply
 * {@link Ignore} it or change the constants to reflect the situation on your system.
 */
//@Ignore
@SuppressWarnings("javadoc")
public class JdkInternalsExecutionServiceTest {

	private static final String PATH_TO_SCANNED_FOLDER = "D:\\Code\\MavenLab\\target\\";

	@Test
	public void execute_pathsExist_returnsViolations() throws Exception {
		ImmutableList<Violation> violations = JdkInternalsExecutionService.execute(new File(PATH_TO_SCANNED_FOLDER));

		violations.forEach(System.out::println);
	}

}
