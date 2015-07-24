package org.codefx.maven.plugin.jdeps.rules;

import org.codefx.maven.plugin.jdeps.dependency.InternalType;
import org.codefx.maven.plugin.jdeps.dependency.Type;
import org.codefx.maven.plugin.jdeps.dependency.Violation;
import org.junit.Before;
import org.junit.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Tests {@link ViolationFilter}.
 */
@SuppressWarnings("javadoc")
public class ViolationFilterTest {

	// the violation which is used to test the filter

	private final Type dependent = Type.of("org.codefx.lab", "SomeType");
	private final InternalType cache = InternalType.of("sun.misc", "Cache", "", "");
	private final InternalType unsafe = InternalType.of("sun.misc", "Unsafe", "", "");
	private final Violation violation = Violation
			.forDependent(dependent)
			.addDependency(cache)
			.addDependency(unsafe)
			.build();

	// the mocked dependency judge and the tested rule filter

	private DependencyJudge dependencyJudge;
	private ViolationFilter violationFilter;

	@Before
	public void createFilter() {
		dependencyJudge = mock(DependencyJudge.class);
		violationFilter = new ViolationFilter(dependencyJudge);
	}

	@SuppressWarnings("unused")
	@Test(expected = NullPointerException.class)
	public void create_dependencyRuleTreeNull_throwsNullPointerException() throws Exception {
		new ViolationFilter(null);
	}

	@Test(expected = NullPointerException.class)
	public void filter_violationNull_throwsNullPointerException() throws Exception {
		violationFilter.filter(null);
	}

	@Test
	public void filter_allDependenciesForbidden_violationUnchanged() throws Exception {
		when(dependencyJudge.forbiddenDependency(any(), any())).thenReturn(true);

		Optional<Violation> filteredViolation = violationFilter.filter(violation);

		assertThat(filteredViolation).contains(violation);
	}

	@Test
	public void filter_oneDependencyAllowed_violationContainsOtherDependency() throws Exception {
		when(dependencyJudge.forbiddenDependency(any(), any())).thenReturn(true);
		when(dependencyJudge.forbiddenDependency(dependent, unsafe)).thenReturn(false);

		Optional<Violation> filteredViolation = violationFilter.filter(violation);

		assertThat(filteredViolation).isPresent();
		assertThat(filteredViolation.get().getDependent()).isEqualTo(dependent);
		assertThat(filteredViolation.get().getInternalDependencies()).containsOnly(cache);
	}

	@Test
	public void filter_allDependenciesAllowed_emptyViolation() throws Exception {
		when(dependencyJudge.forbiddenDependency(any(), any())).thenReturn(false);

		Optional<Violation> filteredViolation = violationFilter.filter(violation);

		assertThat(filteredViolation).isEmpty();
	}

}
