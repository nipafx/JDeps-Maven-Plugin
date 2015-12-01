package org.codefx.mvn.jdeps.result;

import com.google.common.collect.ImmutableList;
import org.codefx.mvn.jdeps.dependency.InternalType;
import org.codefx.mvn.jdeps.dependency.Type;
import org.codefx.mvn.jdeps.dependency.Violation;
import org.codefx.mvn.jdeps.rules.Severity;
import org.junit.Test;

import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests {@link AnnotatedViolation}.
 */
public class AnnotatedViolationTest {

	private static final Type DEPENDENT = Type.of("com.foo.Bar");

	private static final InternalType ENCODER = InternalType.of("sun.misc", "BASE64Encoder", "", "");
	private static final InternalType DECODER = InternalType.of("sun.misc", "BASE64Decoder", "", "");
	private static final InternalType UNSAFE = InternalType.of("sun.misc", "Unsafe", "", "");

	private static final AnnotatedInternalType INFORMED_ENCODER = AnnotatedInternalType.of(ENCODER, Severity.INFORM);
	private static final AnnotatedInternalType WARNED_DECODER = AnnotatedInternalType.of(DECODER, Severity.WARN);
	private static final AnnotatedInternalType FAILED_UNSAFE = AnnotatedInternalType.of(UNSAFE, Severity.FAIL);

	private static final ImmutableList<AnnotatedInternalType> ALL_ANNOTATED_TYPES =
			ImmutableList.of(INFORMED_ENCODER, WARNED_DECODER, FAILED_UNSAFE);

	@Test(expected = NullPointerException.class)
	public void of_dependentNull_throwsException() {
		AnnotatedViolation.of(null, ALL_ANNOTATED_TYPES);
	}

	@Test(expected = NullPointerException.class)
	public void of_dependenciesNull_throwsException() {
		AnnotatedViolation.of(DEPENDENT, null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void of_dependenciesEmpty_throwsException() {
		AnnotatedViolation.of(DEPENDENT, ImmutableList.of());
	}

	@Test
	public void only_dependentOfViolations_returnsDependentSpecifiedDuringConstruction() throws Exception {
		AnnotatedViolation annotatedViolation = AnnotatedViolation.of(DEPENDENT, ALL_ANNOTATED_TYPES);
		streamViolationsForAllSeverities(annotatedViolation)
				.map(Violation::getDependent)
				.forEach(dependent -> assertThat(dependent).isEqualTo(DEPENDENT));
	}

	private static Stream<Violation> streamViolationsForAllSeverities(AnnotatedViolation violation) {
		return Arrays
				.stream(Severity.values())
				.map(violation::only)
				.filter(Optional::isPresent)
				.map(Optional::get);
	}

	@Test
	public void only_severityExistsNot_returnsEmptyOptional() throws Exception {
		assertThat(violationWithDependencies(INFORMED_ENCODER, WARNED_DECODER, FAILED_UNSAFE)
				.only(Severity.IGNORE))
				.isEmpty();
		assertThat(violationWithDependencies(WARNED_DECODER, FAILED_UNSAFE)
				.only(Severity.INFORM))
				.isEmpty();
		assertThat(violationWithDependencies(INFORMED_ENCODER, FAILED_UNSAFE)
				.only(Severity.WARN))
				.isEmpty();
		assertThat(violationWithDependencies(INFORMED_ENCODER, WARNED_DECODER)
				.only(Severity.FAIL))
				.isEmpty();
	}

	@Test
	public void only_severityExists_returnsExactlyInternalDependenciesForSeverity() throws Exception {
		assertThat(violationWithDependencies(INFORMED_ENCODER, WARNED_DECODER, FAILED_UNSAFE)
				.only(Severity.INFORM).get()
				.getInternalDependencies())
				.containsOnly(ENCODER);
		assertThat(violationWithDependencies(INFORMED_ENCODER, WARNED_DECODER, FAILED_UNSAFE)
				.only(Severity.WARN).get()
				.getInternalDependencies())
				.containsOnly(DECODER);
		assertThat(violationWithDependencies(INFORMED_ENCODER, WARNED_DECODER, FAILED_UNSAFE)
				.only(Severity.FAIL).get()
				.getInternalDependencies())
				.containsOnly(UNSAFE);
	}

	@Test
	public void except_noOtherSeverityExists_returnsEmptyOptional() throws Exception {
		assertThat(violationWithDependencies(INFORMED_ENCODER)
				.except(Severity.IGNORE, Severity.INFORM))
				.isEmpty();
		assertThat(violationWithDependencies(WARNED_DECODER)
				.except(Severity.IGNORE, Severity.WARN))
				.isEmpty();
		assertThat(violationWithDependencies(FAILED_UNSAFE)
				.except(Severity.IGNORE, Severity.FAIL))
				.isEmpty();
		assertThat(violationWithDependencies(INFORMED_ENCODER, WARNED_DECODER)
				.except(Severity.IGNORE, Severity.INFORM, Severity.WARN))
				.isEmpty();
	}

	@Test
	public void except_otherSeveritiesExist_returnsExactlyInternalDependenciesForOtherSeverities() throws Exception {
		assertThat(violationWithDependencies(INFORMED_ENCODER, WARNED_DECODER, FAILED_UNSAFE)
				.except(Severity.INFORM).get()
				.getInternalDependencies())
				.containsOnly(DECODER, UNSAFE);
		assertThat(violationWithDependencies(INFORMED_ENCODER, WARNED_DECODER, FAILED_UNSAFE)
				.except(Severity.WARN).get()
				.getInternalDependencies())
				.containsOnly(ENCODER, UNSAFE);
		assertThat(violationWithDependencies(INFORMED_ENCODER, WARNED_DECODER, FAILED_UNSAFE)
				.except(Severity.FAIL).get()
				.getInternalDependencies())
				.containsOnly(ENCODER,  DECODER);
	}

	private static AnnotatedViolation violationWithDependencies(AnnotatedInternalType... dependencies) {
		return AnnotatedViolation.of(DEPENDENT, ImmutableList.copyOf(dependencies));
	}

}
