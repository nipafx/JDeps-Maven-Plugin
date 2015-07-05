package org.codefx.maven.plugin.jdeps.rules;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.codefx.maven.plugin.jdeps.dependency.InternalType;
import org.codefx.maven.plugin.jdeps.dependency.Type;
import org.codefx.maven.plugin.jdeps.dependency.Violation;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests {@link ViolationFilter}.
 */
@SuppressWarnings("javadoc")
public class ViolationFilterTest {

	// the violation which is used to test the filter

	private final Type dependentType = Type.of("org.codefx.lab", "SomeType");
	private final InternalType cache = InternalType.of("sun.misc", "Cache", "", "");
	private final InternalType unsafe = InternalType.of("sun.misc", "Unsafe", "", "");
	private final Violation violation = Violation
			.forType(dependentType)
			.addDependency(cache)
			.addDependency(unsafe)
			.build();

	// the mocked rule tree and the tested rule filter

	private DependencyFilter dependencyFilter;
	private ViolationFilter violationFilter;

	@Before
	public void createFilter() {
		dependencyFilter = mock(DependencyFilter.class);
		violationFilter = new ViolationFilter(dependencyFilter);
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
	public void filter_emptyRuleTree_violationUnchanged() throws Exception {
		when(dependencyFilter.forbiddenDependency(any(), any())).thenReturn(true);

		Optional<Violation> filteredViolation = violationFilter.filter(violation);

		assertThat(filteredViolation).contains(violation);
	}

	@Test
	public void filter_ruleTreeRemovesOneDependency_violationContainsOtherDependency() throws Exception {
		when(dependencyFilter.forbiddenDependency(any(), any())).thenReturn(true);
		when(dependencyFilter.forbiddenDependency(dependentType, unsafe)).thenReturn(false);

		Optional<Violation> filteredViolation = violationFilter.filter(violation);

		assertThat(filteredViolation).isPresent();
		assertThat(filteredViolation.get().getType()).isEqualTo(dependentType);
		assertThat(filteredViolation.get().getInternalDependencies()).containsOnly(cache);
	}

	@Test
	public void filter_ruleTreeRemovesAllDependencies_emptyViolation() throws Exception {
		when(dependencyFilter.forbiddenDependency(any(), any())).thenReturn(false);

		Optional<Violation> filteredViolation = violationFilter.filter(violation);

		assertThat(filteredViolation).isEmpty();
	}

}
