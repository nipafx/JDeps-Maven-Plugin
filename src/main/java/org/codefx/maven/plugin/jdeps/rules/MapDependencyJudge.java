package org.codefx.maven.plugin.jdeps.rules;

import com.google.common.collect.Iterables;
import com.google.common.collect.Iterators;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static java.text.MessageFormat.format;
import static java.util.Objects.requireNonNull;

/**
 * A {@link DependencyJudge} based on a bimap {@code (dependency, dependant) -> severity} and using
 * {@link TypeNameHierarchy}-s to identify the best match.
 */
public class MapDependencyJudge implements DependencyJudge {

	private final PackageInclusion packageInclusion;
	private final Severity defaultSeverity;
	private final Map<String, Map<String, Severity>> dependencies;

	private MapDependencyJudge(
			PackageInclusion packageInclusion,
			Severity defaultSeverity,
			Map<String, Map<String, Severity>> dependencies) {
		this.packageInclusion = requireNonNull(packageInclusion, "The argument 'packageInclusion' must not be null.");
		this.defaultSeverity = requireNonNull(defaultSeverity, "The argument 'defaultSeverity' must not be null.");
		this.dependencies = requireNonNull(dependencies, "The argument 'dependencies' must not be null.");
	}

	@Override
	public Severity judgeSeverity(String dependentName, String dependencyName) {
		// the order of the two loops is crucial;
		// checking all dependency names before continuing with the next dependent name yields the desired behavior of
		// finding the best matching dependent that defines a rule for the dependency
		for (String dependentNamePart : namesFor(dependentName))
			for (String dependencyNamePart : namesFor(dependencyName)) {
				Optional<Severity> severity = tryGetSeverityFor(dependentNamePart, dependencyNamePart);
				if (severity.isPresent())
					return severity.get();
			}

		return defaultSeverity;
	}

	private Optional<Severity> tryGetSeverityFor(String dependent, String dependency) {
		return Optional.ofNullable(dependencies.get(dependent))
				.map(mapForDependent -> mapForDependent.get(dependency));
	}

	private Iterable<String> namesFor(String dependentName) {
		Iterable<String> typeNameHierarchy = TypeNameHierarchy.forFullyQualifiedName(dependentName, packageInclusion);
		Iterable<String> wildcard = () -> Iterators.singletonIterator(DependencyRule.ALL_TYPES_WILDCARD);
		return Iterables.concat(typeNameHierarchy, wildcard);
	}

	public static class MapDependencyJudgeBuilder implements DependencyJudgeBuilder {

		private PackageInclusion packageInclusion;
		private Severity defaultSeverity;
		private final Map<String, Map<String, Severity>> dependencies;
		private boolean alreadyBuilt;

		public MapDependencyJudgeBuilder() {
			// set default values
			packageInclusion = PackageInclusion.FLAT;
			defaultSeverity = Severity.FAIL;
			dependencies = new HashMap<>();

			alreadyBuilt = false;
		}

		@Override
		public DependencyJudgeBuilder withInclusion(PackageInclusion packageInclusion) {
			this.packageInclusion =
					requireNonNull(packageInclusion, "The argument 'packageInclusion' must not be null.");
			return this;
		}

		@Override
		public DependencyJudgeBuilder withDefaultSeverity(Severity defaultSeverity) {
			this.defaultSeverity = requireNonNull(defaultSeverity, "The argument 'defaultSeverity' must not be null.");
			return this;
		}

		@Override
		public DependencyJudgeBuilder addDependency(DependencyRule rule) {
			requireNonNull(rule, "The argument 'ruleName' must not be null.");

			Map<String, Severity> mapForDependent =
					dependencies.computeIfAbsent(rule.getDependent(), ignored -> new HashMap<>());
			Severity previousSeverity = mapForDependent.put(rule.getDependency(), rule.getSeverity());

			if (previousSeverity != null && previousSeverity != rule.getSeverity()) {
				String message = format(
						"The dependency '{0} -> {1}' is defined with multiple severitues {2} and {3}.",
						rule.getDependent(), rule.getDependency(), previousSeverity, rule.getSeverity());
				throw new IllegalArgumentException(message);
			}

			return this;
		}

		public DependencyJudge build() {
			if (alreadyBuilt)
				throw new IllegalStateException("A builder can only be used once.");
			alreadyBuilt = true;
			return new MapDependencyJudge(packageInclusion, defaultSeverity, dependencies);
		}

	}

}
