package org.codefx.maven.plugin.jdeps.rules;

import java.util.HashMap;
import java.util.Map;

import static java.text.MessageFormat.format;
import static java.util.Objects.requireNonNull;

/**
 * A {@link DependencyJudge} based on a bimap {@code (dependency, dependant) -> severity} and using
 * {@link TypeNameHierarchy}-s to identify the best match.
 */
public class TypeNameHierarchyMapDependencyJudge implements DependencyJudge {

	private final Severity defaultSeverity;
	private final Map<String, Map<String, Severity>> dependencies;

	private TypeNameHierarchyMapDependencyJudge(
			Severity defaultSeverity, Map<String, Map<String, Severity>> dependencies) {
		this.defaultSeverity = requireNonNull(defaultSeverity, "The argument 'defaultSeverity' must not be null.");
		this.dependencies = requireNonNull(dependencies, "The argument 'dependencies' must not be null.");
	}

	@Override
	public Severity judgeSeverity(String dependentName, String dependencyName) {
		TypeNameHierarchy dependentNameHierarchy = TypeNameHierarchy.forFullyQualifiedName(dependentName);
		TypeNameHierarchy dependencyNameHierarchy = TypeNameHierarchy.forFullyQualifiedName(dependencyName);

		for (String dependentNamePart : dependentNameHierarchy)
			for (String dependencyNamePart : dependencyNameHierarchy) {
				Map<String, Severity> mapForDependent = dependencies.get(dependentNamePart);
				if (mapForDependent != null) {
					Severity severity = mapForDependent.get(dependencyNamePart);
					if (severity != null)
						return severity;
				}
			}

		return defaultSeverity;
	}

	public static class TypeNameHierarchyMapDependencyJudgeBuilder implements DependencyJudgeBuilder {

		private Severity defaultSeverity;
		private final Map<String, Map<String, Severity>> dependencies;
		private boolean alreadyBuilt;

		public TypeNameHierarchyMapDependencyJudgeBuilder() {
			defaultSeverity = Severity.FAIL;
			dependencies = new HashMap<>();
			alreadyBuilt = false;
		}

		@Override
		public DependencyJudgeBuilder withDefaultSeverity(Severity defaultSeverity) {
			this.defaultSeverity = requireNonNull(defaultSeverity, "The argument 'defaultSeverity' must not be null.");
			return this;
		}

		@Override
		public DependencyJudgeBuilder addDependency(String dependentName, String dependencyName, Severity severity) {
			requireNonNull(dependentName, "The argument 'dependentName' must not be null.");
			requireNonNull(dependencyName, "The argument 'dependencyName' must not be null.");
			requireNonNull(severity, "The argument 'severity' must not be null.");

			Map<String, Severity> mapForDependent =
					dependencies.computeIfAbsent(dependentName, ignored -> new HashMap<>());
			Severity previousSeverity = mapForDependent.put(dependencyName, severity);

			if (previousSeverity != null) {
				String message = format(
						"The dependencyName '{0} -> {1}' already had severity '{2}', "
								+ "which was now overriden with '{3}'.",
						dependentName, dependencyName, previousSeverity, severity);
				throw new IllegalArgumentException(message);
			}

			return this;
		}

		public DependencyJudge build() {
			if (alreadyBuilt)
				throw new IllegalStateException("A builder can only be used once.");
			alreadyBuilt = true;
			return new TypeNameHierarchyMapDependencyJudge(defaultSeverity, dependencies);
		}

	}

}
