package org.codefx.maven.plugin.jdeps.result;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.codefx.maven.plugin.jdeps.dependency.InternalType;
import org.codefx.maven.plugin.jdeps.dependency.Type;
import org.codefx.maven.plugin.jdeps.dependency.Violation;
import org.codefx.maven.plugin.jdeps.rules.Severity;

import java.util.Optional;

import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.toList;

/**
 * A violation whose dependencies are annotated with their severity.
 */
class AnnotatedViolation {

	private final Type dependent;
	private ImmutableMap<Severity, ImmutableList<InternalType>> internalDependencies;

	public AnnotatedViolation(Type dependent, ImmutableList<AnnotatedInternalType> internalDependencies) {
		this.dependent = requireNonNull(dependent, "The argument 'dependent' must not be null.");
		requireNonNull(internalDependencies, "The argument 'internalDependencies' must not be null.");
		if (internalDependencies.size() == 0)
			throw new IllegalArgumentException(
					"A violation must contain at least one internal dependency.");

		this.internalDependencies = internalDependencies.stream().collect(
				collectingAndThen(
						groupingBy(AnnotatedInternalType::getSeverity,
								collectingAndThen(mapping(
										AnnotatedInternalType::getType,
										toList()),
										ImmutableList::copyOf)),
						ImmutableMap::copyOf));
	}

	/**
	 * Returns a violation which contains only the internal dependencies with the specified severity.
	 * <p>
	 * If no internal dependencies for the specified severity exist, {@link Optional#empty()} is returned.
	 *
	 * @param severity
	 * 		the severity to filter internal dependencies by
	 *
	 * @return a violation or {@link Optional#empty() empty} if no internal dependencies with the specified severity
	 * exist
	 */
	public Optional<Violation> only(Severity severity) {
		return Optional.ofNullable(internalDependencies.get(severity))
				.map(dependencies -> Violation.buildFor(dependent, internalDependencies.get(severity)));
	}

}
