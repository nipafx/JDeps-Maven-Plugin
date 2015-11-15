package org.codefx.maven.plugin.jdeps.mojo;

import org.codefx.maven.plugin.jdeps.result.Result;
import org.codefx.maven.plugin.jdeps.result.SystemOutResultOutputStrategy;
import org.codefx.maven.plugin.jdeps.rules.PackageInclusion;
import org.codefx.maven.plugin.jdeps.rules.Severity;
import org.junit.Ignore;
import org.junit.Test;

import java.io.File;
import java.util.Collections;

/**
 * Tests {@link JdkInternalsExecutionService}.
 * <p>
 * This is no regular unit/integration test as it relies on correct configuration to pass. If this test fails simply
 * {@link Ignore} it or change the constants to reflect the situation on your system.
 */
//@Ignore
@SuppressWarnings("javadoc")
public class JdkInternalsExecutionServiceTest {

	private static final String PATH_TO_SCANNED_FOLDER = "/home/nipa/Code/MavenLab/target";

	@Test
	public void execute_pathsExist_returnsViolations() throws Exception {
		Result result = JdkInternalsExecutionService.execute(
				new File(PATH_TO_SCANNED_FOLDER),
				new DependencyRulesConfiguration(
						PackageInclusion.HIERARCHICAL, Severity.WARN,
						Collections.emptyList(), Collections.emptyList()));
		new SystemOutResultOutputStrategy().output(result);
	}

}
