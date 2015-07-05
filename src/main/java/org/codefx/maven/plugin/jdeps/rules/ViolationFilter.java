package org.codefx.maven.plugin.jdeps.rules;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.codefx.maven.plugin.jdeps.dependency.InternalType;
import org.codefx.maven.plugin.jdeps.dependency.Type;
import org.codefx.maven.plugin.jdeps.dependency.Violation;

/**
 * Uses a {@link DependencyFilter} to filter violations, i.e. remove non-forbidden dependencies from violations or, if
 * all dependencies are allowed, remove the entire violation.
 */
public class ViolationFilter {

	private final DependencyFilter dependencyFilter;

	/**
	 * Creates a new violation filter which uses the specified dependency filter.
	 *
	 * @param dependencyFilter
	 *            used to filter each violation's individual dependencies
	 */
	public ViolationFilter(DependencyFilter dependencyFilter) {
		Objects.requireNonNull(dependencyFilter, "The argument 'dependencyFilter' must not be null.");
		this.dependencyFilter = dependencyFilter;
	}

	/**
	 * Filters the specified violation.
	 *
	 * @param violation
	 *            the violation to filter
	 * @return an {@link Optional} with a violation that only contains non-forbidden dependencies; if none are
	 *         forbidden, an {@link Optional#empty() empty} {@code Optional}
	 */
	public Optional<Violation> filter(Violation violation) {
		Objects.requireNonNull(violation, "The argument 'violation' must not be null.");

		List<InternalType> forbiddenDependencies = extractForbiddenDependencies(violation);
		return createFilteredViolation(violation.getType(), forbiddenDependencies);
	}

	private List<InternalType> extractForbiddenDependencies(Violation violation) {
		Type dependentType = violation.getType();
		List<InternalType> forbiddenDependencies = violation.getInternalDependencies().stream()
				.filter(dependency -> dependencyFilter.forbiddenDependency(dependentType, dependency))
				.collect(Collectors.toList());
		return forbiddenDependencies;
	}

	private static Optional<Violation> createFilteredViolation(
			Type dependentType, List<InternalType> forbiddenDependencies) {
		boolean allDependenciesAllowed = forbiddenDependencies.isEmpty();
		if (allDependenciesAllowed)
			// if all dependencies are allowed, there is no violation
			return Optional.empty();
		else {
			Violation filteredViolation = Violation
					.forType(dependentType)
					.addDependencies(forbiddenDependencies)
					.build();
			return Optional.of(filteredViolation);
		}
	}

}
