package org.codefx.maven.plugin.jdeps.result;

import org.apache.maven.plugin.MojoFailureException;
import org.codefx.maven.plugin.jdeps.rules.DependencyRule;
import org.codefx.maven.plugin.jdeps.rules.Severity;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.codefx.maven.plugin.jdeps.Factory.violation;
import static org.codefx.maven.plugin.jdeps.result.ViolationsToRuleTransformer.transform;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Tests {@link ViolationsToRuleTransformer}.
 */
public class ViolationsToRuleTransformerTest {

	private Result result;

	@Before
	public void setUp() {
		result = mock(Result.class);
	}

	@Test(expected = NullPointerException.class)
	public void transform_resultNull_throwsException() throws MojoFailureException {
		transform(null);
	}

	@Test
	public void transform_resultEmpty_outputEmpty() throws Exception {
		when(result.violationsWithSeverity(any()))
				.then(ignored -> Stream.empty());

		List<DependencyRule> rules = transform(result).collect(toList());

		assertThat(rules).isEmpty();
	}

	@Test
	public void transform_oneViolationPerSeverity_outputAllViolations() throws Exception {
		when(result.violationsWithSeverity(Severity.IGNORE))
				.thenReturn(Stream.of(violation("org.Ignore", "sun.Ignore")));
		when(result.violationsWithSeverity(Severity.SUMMARIZE))
				.thenReturn(Stream.of(violation("org.Summarize", "sun.Summarize")));
		when(result.violationsWithSeverity(Severity.INFORM))
				.thenReturn(Stream.of(violation("org.Inform", "sun.Inform")));
		when(result.violationsWithSeverity(Severity.WARN))
				.thenReturn(Stream.of(violation("org.Warn", "sun.Warn")));
		when(result.violationsWithSeverity(Severity.FAIL))
				.thenReturn(Stream.of(violation("org.Fail", "sun.Fail")));

		List<DependencyRule> rules = transform(result).collect(toList());

		assertThat(rules).containsOnly(
				DependencyRule.of("org.Ignore", "sun.Ignore", Severity.IGNORE),
				DependencyRule.of("org.Summarize", "sun.Summarize", Severity.SUMMARIZE),
				DependencyRule.of("org.Inform", "sun.Inform", Severity.INFORM),
				DependencyRule.of("org.Warn", "sun.Warn", Severity.WARN),
				DependencyRule.of("org.Fail", "sun.Fail", Severity.FAIL)
		);
	}

	@Test
	public void transform_oneViolationWithManyDependencies_outputContainsOneRulePerDependency() throws Exception {
		when(result.violationsWithSeverity(any()))
				.then(ignored -> Stream.empty());
		when(result.violationsWithSeverity(Severity.IGNORE))
				.thenReturn(Stream.of(violation("org.A", "sun.X", "sun.Y", "sun.Z")));

		List<DependencyRule> rules = transform(result).collect(toList());

		assertThat(rules).containsOnly(
				DependencyRule.of("org.A", "sun.X", Severity.IGNORE),
				DependencyRule.of("org.A", "sun.Y", Severity.IGNORE),
				DependencyRule.of("org.A", "sun.Z", Severity.IGNORE)
		);
	}

}
