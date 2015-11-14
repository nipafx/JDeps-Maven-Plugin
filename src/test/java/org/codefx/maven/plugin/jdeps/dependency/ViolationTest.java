package org.codefx.maven.plugin.jdeps.dependency;

import com.google.common.collect.ImmutableList;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests {@link Violation}.
 */
public class ViolationTest {

	private static final Type DEPENDENT = Type.of("com.foo.Bar");

	private static final ImmutableList<InternalType> INTERNAL_DEPENDENCIES =
			ImmutableList.of(
					InternalType.of("sun.misc", "Unsafe", "", ""),
					InternalType.of("sun.misc", "BASE64Decoder", "", ""),
					InternalType.of("sun.misc", "BASE64Encoder", "", "")
			);

	// #begin BUILD FOR

	@Test(expected = NullPointerException.class)
	public void buildFor_dependentNull_throwsException() {
		Violation.buildFor(null, INTERNAL_DEPENDENCIES);
	}

	@Test(expected = NullPointerException.class)
	public void buildFor_dependenciesNull_throwsException() {
		Violation.buildFor(DEPENDENT, null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void buildFor_dependenciesEmpty_throwsException() {
		Violation.buildFor(DEPENDENT, ImmutableList.of());
	}

	@Test
	public void buildFor_dependent_violationReturnsDependent() throws Exception {
		Violation violation = Violation.buildFor(DEPENDENT, INTERNAL_DEPENDENCIES);
		assertThat(violation.getDependent()).isEqualTo(DEPENDENT);
	}

	@Test
	public void buildFor_dependent_violationReturnsInternalDependencies() throws Exception {
		Violation violation = Violation.buildFor(DEPENDENT, INTERNAL_DEPENDENCIES);
		assertThat(violation.getInternalDependencies()).containsExactlyElementsOf(INTERNAL_DEPENDENCIES);
	}

	// #end BUILD FOR

	// #begin BUILD FOR DEPENDENT

	@Test(expected = NullPointerException.class)
	public void buildForDependent_dependentNull_throwsException() {
		Violation.buildForDependent(null);
	}

	@Test
	public void buildForDependent_buildViolation_returnsDependent() {
		Violation violation = Violation
				.buildForDependent(DEPENDENT)
				.addDependencies(INTERNAL_DEPENDENCIES)
				.build();
		assertThat(violation.getDependent()).isEqualTo(DEPENDENT);
	}

	@Test(expected = NullPointerException.class)
	public void addDependency_dependencyNull_throwsException() {
		Violation.buildForDependent(DEPENDENT).addDependency(null);
	}

	@Test
	public void addDependency_buildViolation_returnsAddedDependency() throws Exception {
		Violation violation = Violation
				.buildForDependent(DEPENDENT)
				.addDependency(INTERNAL_DEPENDENCIES.get(0))
				.build();
		assertThat(violation.getInternalDependencies()).containsExactly(INTERNAL_DEPENDENCIES.get(0));
	}

	@Test(expected = NullPointerException.class)
	public void addDependencies_dependencyNull_throwsException() {
		Violation.buildForDependent(DEPENDENT).addDependencies(null);
	}

	@Test
	public void addDependencies_buildViolation_returnsAddedDependencies() throws Exception {
		Violation violation = Violation
				.buildForDependent(DEPENDENT)
				.addDependencies(INTERNAL_DEPENDENCIES)
				.build();
		assertThat(violation.getInternalDependencies()).containsExactlyElementsOf(INTERNAL_DEPENDENCIES);
	}

	// #end BUILD FOR DEPENDENT

}
