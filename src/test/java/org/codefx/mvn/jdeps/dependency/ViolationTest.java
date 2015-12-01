package org.codefx.mvn.jdeps.dependency;

import com.google.common.collect.ImmutableList;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests {@link Violation}.
 */
public class ViolationTest {

	private static final Type DEPENDENT = Type.of("com.foo.Bar");

	private static final ImmutableList<InternalType> DEPENDENCIES =
			ImmutableList.of(
					InternalType.of("sun.misc", "Unsafe", "", ""),
					InternalType.of("sun.misc", "BASE64Decoder", "", ""),
					InternalType.of("sun.misc", "BASE64Encoder", "", "")
			);

	private static final ImmutableList<InternalType> SORTED_DEPENDENCIES =
			ImmutableList.of(
					InternalType.of("sun.misc", "BASE64Decoder", "", ""),
					InternalType.of("sun.misc", "BASE64Encoder", "", ""),
					InternalType.of("sun.misc", "Unsafe", "", "")
					);

	// #begin BUILD FOR

	@Test(expected = NullPointerException.class)
	public void buildFor_dependentNull_throwsException() {
		Violation.buildFor(null, DEPENDENCIES);
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
		Violation violation = Violation.buildFor(DEPENDENT, DEPENDENCIES);
		assertThat(violation.getDependent()).isEqualTo(DEPENDENT);
	}

	@Test
	public void buildFor_dependencies_violationReturnsInternalDependenciesInSortedOrder() throws Exception {
		Violation violation = Violation.buildFor(DEPENDENT, DEPENDENCIES);
		assertThat(violation.getInternalDependencies()).containsExactlyElementsOf(SORTED_DEPENDENCIES);
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
				.addDependencies(DEPENDENCIES)
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
				.addDependency(DEPENDENCIES.get(0))
				.build();
		assertThat(violation.getInternalDependencies()).containsExactly(DEPENDENCIES.get(0));
	}

	@Test(expected = NullPointerException.class)
	public void addDependencies_dependencyNull_throwsException() {
		Violation.buildForDependent(DEPENDENT).addDependencies((Iterable<InternalType>) null);
	}

	@Test
	public void addDependencies_buildViolation_returnsAddedDependenciesInSortedOrder() throws Exception {
		Violation violation = Violation
				.buildForDependent(DEPENDENT)
				.addDependencies(DEPENDENCIES)
				.build();
		assertThat(violation.getInternalDependencies()).containsExactlyElementsOf(SORTED_DEPENDENCIES);
	}

	// #end BUILD FOR DEPENDENT

	@Test
	public void compareTo_differentDependents_orderedByDependents() throws Exception {
		Violation smaller = Violation
				.buildForDependent(Type.of("java.lang.Object"))
				.addDependencies(DEPENDENCIES)
				.build();
		Violation greater = Violation
				.buildForDependent(Type.of("java.lang.String"))
				.addDependencies(DEPENDENCIES)
				.build();

		assertThat(smaller.compareTo(greater)).isNegative();
		assertThat(greater.compareTo(smaller)).isPositive();
	}

	@Test
	public void compareTo_sameDependents_differentDependencies_orderedByDependencies() throws Exception {
		Violation smaller = Violation
				.buildForDependent(DEPENDENT)
				.addDependencies(
						InternalType.of("sun.misc", "BASE64Decoder", "", ""),
						InternalType.of("sun.misc", "Unsafe", "", ""))
				.build();
		Violation greater = Violation
				.buildForDependent(DEPENDENT)
				.addDependencies(
						InternalType.of("sun.misc", "BASE64Encoder", "", ""),
						InternalType.of("sun.misc", "Unsafe", "", ""))
				.build();

		assertThat(smaller.compareTo(greater)).isNegative();
		assertThat(greater.compareTo(smaller)).isPositive();
	}

	@Test
	public void compareTo_sameDependents_subsetDependencies_orderedByDependencies() throws Exception {
		Violation smaller = Violation
				.buildForDependent(DEPENDENT)
				.addDependencies(
						InternalType.of("sun.misc", "BASE64Decoder", "", ""),
						InternalType.of("sun.misc", "BASE64Encoder", "", ""))
				.build();
		Violation greater = Violation
				.buildForDependent(DEPENDENT)
				.addDependencies(
						InternalType.of("sun.misc", "BASE64Decoder", "", ""),
						InternalType.of("sun.misc", "BASE64Encoder", "", ""),
						InternalType.of("sun.misc", "Unsafe", "", ""))
				.build();

		assertThat(smaller.compareTo(greater)).isNegative();
		assertThat(greater.compareTo(smaller)).isPositive();
	}

	@Test
	public void compareTo_sameDependents_sameDependencies_orderedSame() throws Exception {
		Violation one = Violation
				.buildForDependent(DEPENDENT)
				.addDependencies(DEPENDENCIES)
				.build();
		Violation other = Violation
				.buildForDependent(DEPENDENT)
				.addDependencies(DEPENDENCIES)
				.build();

		assertThat(one.compareTo(other)).isZero();
		assertThat(other.compareTo(one)).isZero();
	}

}
