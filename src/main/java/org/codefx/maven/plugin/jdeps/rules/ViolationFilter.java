package org.codefx.maven.plugin.jdeps.rules;

import org.codefx.maven.plugin.jdeps.dependency.InternalType;
import org.codefx.maven.plugin.jdeps.dependency.Type;
import org.codefx.maven.plugin.jdeps.dependency.Violation;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Uses a {@link DependencyJudge} to filter violations, i.e. remove non-forbidden dependencies from violations or, if
 * all dependencies are allowed, remove the entire violation.
 */
public class ViolationFilter {

	private final DependencyJudge dependencyJudge;

	/**
	 * Creates a new violation filter which uses the specified dependency filter.
	 *
	 * @param dependencyJudge
	 *            used to filter each violation's individual dependencies
	 */
	public ViolationFilter(DependencyJudge dependencyJudge) {
		Objects.requireNonNull(dependencyJudge, "The argument 'dependencyJudge' must not be null.");
		this.dependencyJudge = dependencyJudge;
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
		return createFilteredViolation(violation.getDependent(), forbiddenDependencies);
	}

	private List<InternalType> extractForbiddenDependencies(Violation violation) {
		Type dependent = violation.getDependent();
        return violation.getInternalDependencies().stream()
                .filter(dependency -> dependencyJudge.forbiddenDependency(dependent, dependency))
                .collect(Collectors.toList());
	}

	private static Optional<Violation> createFilteredViolation(Type dependent, List<InternalType> forbiddenDependencies) {
		boolean allDependenciesAllowed = forbiddenDependencies.isEmpty();
		if (allDependenciesAllowed)
			// if all dependencies are allowed, there is no violation
			return Optional.empty();
		else {
			Violation filteredViolation = Violation
					.forDependent(dependent)
					.addDependencies(forbiddenDependencies)
					.build();
			return Optional.of(filteredViolation);
		}
	}

}
