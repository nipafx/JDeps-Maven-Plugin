package org.codefx.maven.plugin.jdeps.result;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.codefx.maven.plugin.jdeps.dependency.InternalType;
import org.codefx.maven.plugin.jdeps.dependency.Type;
import org.codefx.maven.plugin.jdeps.dependency.Violation;
import org.codefx.maven.plugin.jdeps.rules.Severity;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;

import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.toList;

/**
 * A violation whose dependencies are annotated with their severity.
 */
final class AnnotatedViolation {

	private final Type dependent;
	private ImmutableMap<Severity, ImmutableList<InternalType>> internalDependencies;

	private AnnotatedViolation(
			Type dependent,
			ImmutableMap<Severity, ImmutableList<InternalType>> internalDependencies) {
		this.dependent = dependent;
		this.internalDependencies = internalDependencies;
	}

	public static AnnotatedViolation of(Type dependent, ImmutableList<AnnotatedInternalType> internalDependencies) {
		requireNonNull(dependent, "The argument 'dependent' must not be null.");
		requireNonNull(internalDependencies, "The argument 'internalDependencies' must not be null.");
		if (internalDependencies.size() == 0)
			throw new IllegalArgumentException(
					"A violation must contain at least one internal dependency.");

		ImmutableMap<Severity, ImmutableList<InternalType>> internalDependenciesMap =
				internalDependencies.stream().collect(
						collectingAndThen(
								groupingBy(AnnotatedInternalType::getSeverity,
										collectingAndThen(mapping(
												AnnotatedInternalType::getType,
												toList()),
												ImmutableList::copyOf)),
								ImmutableMap::copyOf));
		return new AnnotatedViolation(dependent, internalDependenciesMap);
	}

	/**
	 * Returns a violation that contains only the internal dependencies with the specified severities.
	 * <p>
	 * If no internal dependencies for the specified severities exist, {@link Optional#empty()} is returned.
	 *
	 * @param severities
	 * 		the severities to filterBy internal dependencies by
	 *
	 * @return a violation or {@link Optional#empty() empty} if no internal dependencies with the specified severity
	 * exist
	 */
	public Optional<Violation> only(Severity... severities) {
		List<Severity> severitiesToSelect = Arrays.asList(severities);
		return filterBy(severitiesToSelect::contains);
	}

	/**
	 * Returns a violation that contains all internal dependencies except the ones with one of the specified
	 * severities.
	 *
	 * @return a violation or {@link Optional#empty() empty} if no internal dependencies with the non-excluded
	 * severities exist
	 */
	public Optional<Violation> except(Severity... severities) {
		List<Severity> severitiesToIgnore = Arrays.asList(severities);
		return filterBy(severity -> !severitiesToIgnore.contains(severity));
	}

	private Optional<Violation> filterBy(Predicate<Severity> filterBySeverity) {
		List<InternalType> dependencies = Severity.stream()
				.filter(filterBySeverity)
				.map(internalDependencies::get)
				.filter(Objects::nonNull)
				.flatMap(ImmutableList::stream)
				.collect(toList());

		if (dependencies.isEmpty())
			return Optional.empty();
		else
			return Optional.of(Violation.buildFor(dependent, dependencies));
	}

}
