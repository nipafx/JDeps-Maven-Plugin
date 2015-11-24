package org.codefx.maven.plugin.jdeps.result;

import org.apache.maven.plugin.MojoFailureException;
import org.codefx.maven.plugin.jdeps.dependency.InternalType;
import org.codefx.maven.plugin.jdeps.dependency.Type;
import org.codefx.maven.plugin.jdeps.dependency.Violation;
import org.codefx.maven.plugin.jdeps.dependency.Violation.ViolationBuilder;
import org.codefx.maven.plugin.jdeps.result.Result;
import org.codefx.maven.plugin.jdeps.result.RuleOutputStrategy;
import org.codefx.maven.plugin.jdeps.rules.DependencyRule;
import org.codefx.maven.plugin.jdeps.rules.Severity;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Tests {@link RuleOutputStrategy}.
 */
public class RuleOutputStrategyTest {

	private RuleOutputStrategy output;
	private Result result;
	private List<DependencyRule> createdRules;

	@Before
	@SuppressWarnings("unchecked")
	public void setUp() {
		createdRules = new ArrayList<>();
		output = new RuleOutputStrategy(createdRules::add);
		result = mock(Result.class);
	}

	@Test(expected = NullPointerException.class)
	public void create_ruleOutputNull_throwsException() {
		new RuleOutputStrategy(null);
	}

	@Test(expected = NullPointerException.class)
	public void output_resultNull_throwsException() throws MojoFailureException {
		output.output(null);
	}

	@Test
	public void output_resultEmpty_outputEmpty() throws Exception {
		when(result.violationsWithSeverity(any()))
				.then(ignored -> Stream.empty());

		output.output(result);

		assertThat(createdRules).isEmpty();
	}

	@Test
	public void output_oneViolationPerSeverity_outputAllViolations() throws Exception {
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

		output.output(result);

		assertThat(createdRules).containsOnly(
				DependencyRule.of("org.Ignore", "sun.Ignore", Severity.IGNORE),
				DependencyRule.of("org.Summarize", "sun.Summarize", Severity.SUMMARIZE),
				DependencyRule.of("org.Inform", "sun.Inform", Severity.INFORM),
				DependencyRule.of("org.Warn", "sun.Warn", Severity.WARN),
				DependencyRule.of("org.Fail", "sun.Fail", Severity.FAIL)
		);
	}

	@Test
	public void output_oneViolationWithManyDependencies_outputContainsOneRulePerDependency() throws Exception {
		when(result.violationsWithSeverity(any()))
				.then(ignored -> Stream.empty());
		when(result.violationsWithSeverity(Severity.IGNORE))
				.thenReturn(Stream.of(violation("org.A", "sun.X", "sun.Y", "sun.Z")));

		output.output(result);

		assertThat(createdRules).containsOnly(
				DependencyRule.of("org.A", "sun.X", Severity.IGNORE),
				DependencyRule.of("org.A", "sun.Y", Severity.IGNORE),
				DependencyRule.of("org.A", "sun.Z", Severity.IGNORE)
		);
	}

	@Test
	public void output_manyViolations_outputSortedByDependantName() throws Exception {
		when(result.violationsWithSeverity(any()))
				.then(ignored -> Stream.empty());
		when(result.violationsWithSeverity(Severity.IGNORE))
				.thenReturn(Stream.of(
						violation("org.C", "sun.X"),
						violation("org.A", "sun.Z"),
						violation("org.B", "sun.Y")
				));

		output.output(result);

		assertThat(createdRules).containsExactly(
				DependencyRule.of("org.A", "sun.Z", Severity.IGNORE),
				DependencyRule.of("org.B", "sun.Y", Severity.IGNORE),
				DependencyRule.of("org.C", "sun.X", Severity.IGNORE)
		);
	}

	/**
	 * @param violation
	 * 		a variable number of strings, which will be parsed as
	 * 		{@code [dependentName, dependencyName, dependencyName, ... ]}
	 *
	 * @return a violation
	 */
	private static Violation violation(String... violation) {
		ViolationBuilder violationBuilder = Violation.buildForDependent(Type.of(violation[0]));
		Arrays.stream(violation)
				// the first element is the dependent, which was already used above
				.skip(1)
				// 'InternalType.of' requires the fully qualified name to be split into package and class name;
				// to not write such code here, create a 'Type' from the fully qualified name, first
				.map(Type::of)
				.map(type -> InternalType.of(type.getPackageName(), type.getClassName(), "", ""))
				.forEachOrdered(violationBuilder::addDependency);
		return violationBuilder.build();
	}

}
