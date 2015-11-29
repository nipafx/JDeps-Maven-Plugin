package org.codefx.maven.plugin.jdeps.mojo;

import com.google.common.io.Resources;
import org.codefx.maven.plugin.jdeps.dependency.Violation;
import org.codefx.maven.plugin.jdeps.result.Result;
import org.codefx.maven.plugin.jdeps.rules.PackageInclusion;
import org.codefx.maven.plugin.jdeps.rules.Severity;
import org.junit.Test;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;

import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.codefx.maven.plugin.jdeps.Factory.onActionsViolation;
import static org.codefx.maven.plugin.jdeps.Factory.onBASE64Violation;
import static org.codefx.maven.plugin.jdeps.Factory.onUnsafeViolation;

/**
 * Integration tests of {@link JdkInternalsExecutionService}.
 * <p>
 * This test can only pass if {@code test(resources/test-project/target/classes} contains compiled classes.
 */
public class JdkInternalsExecutionServiceTest {

	private static final Path PATH_TO_SCANNED_FOLDER;

	static {
		Path testProjectPom = Paths.get(Resources.getResource("test-project/pom.xml").getPath());
		PATH_TO_SCANNED_FOLDER = testProjectPom.resolveSibling("target").resolve("classes");
	}

	@Test
	public void execute_internalDependenciesExist_returnsViolations() throws Exception {
		// print this class' name as a header for the following JDeps output
		System.out.println("\n# " + getClass().getSimpleName().toUpperCase() + "\n");

		Result result = JdkInternalsExecutionService.execute(
				PATH_TO_SCANNED_FOLDER,
				new DependencyRulesConfiguration(
						Severity.WARN, PackageInclusion.HIERARCHICAL,
						Collections.emptyList(), Collections.emptyList()));

		assertThat(violations(result, Severity.IGNORE)).isEmpty();
		assertThat(violations(result, Severity.SUMMARIZE)).isEmpty();
		assertThat(violations(result, Severity.INFORM)).isEmpty();
		assertThat(violations(result, Severity.WARN)).containsOnly(
				onActionsViolation(),
				onBASE64Violation(),
				onUnsafeViolation()
		);
		assertThat(violations(result, Severity.FAIL)).isEmpty();
	}

	private static List<Violation> violations(Result result, Severity severity) {
		return result.violationsWithSeverity(severity).collect(toList());
	}

}
