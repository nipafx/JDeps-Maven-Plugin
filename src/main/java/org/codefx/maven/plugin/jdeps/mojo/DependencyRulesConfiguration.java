package org.codefx.maven.plugin.jdeps.mojo;

import org.codefx.maven.plugin.jdeps.rules.AllFailDependencyJudge;
import org.codefx.maven.plugin.jdeps.rules.DependencyJudge;
import org.codefx.maven.plugin.jdeps.rules.DependencyJudgeBuilder;
import org.codefx.maven.plugin.jdeps.rules.TypeNameHierarchyMapDependencyJudge.TypeNameMapDependencyJudgeBuilder;

import java.util.Optional;

import static java.util.Objects.requireNonNull;

/**
 * Captures the MOJO configuration that pertains the dependency rules and {@link #createJudge() creates} the
 * according {@link DependencyJudge}.
 */
class DependencyRulesConfiguration {

	private final Optional<String> dependencyRulesAsArrowString;

	/**
	 * Creates a new configuration.
	 *
	 * @param dependencyRulesAsArrowString
	 * 		the configured dependency rules as a "multiline arrow string"
	 */
	public DependencyRulesConfiguration(Optional<String> dependencyRulesAsArrowString) {
		this.dependencyRulesAsArrowString = requireNonNull(dependencyRulesAsArrowString,
				"The argument 'dependencyRulesAsArrowString' must not be null.");
	}

	/**
	 * @return the {@link DependencyJudge} matching the configuration
	 */
	public DependencyJudge createJudge() {
		if (dependencyRulesAsArrowString.isPresent())
			return BuildFromArrowString.build(
					new TypeNameMapDependencyJudgeBuilder(),
					dependencyRulesAsArrowString.get());

		return new AllFailDependencyJudge();
	}

	/**
	 * Interprets a "multiline arrow string" and uses a builder to create a judge from it.
	 */
	final static class BuildFromArrowString {

		private final DependencyJudgeBuilder builder;
		private final String arrowString;

		/**
		 * Creates a new builder.
		 *
		 * @param builder
		 * 		the builder to use
		 * @param arrowString
		 * 		the "multiline arrow string" to interpret
		 */
		BuildFromArrowString(DependencyJudgeBuilder builder, String arrowString) {
			this.builder = builder;
			this.arrowString = arrowString;
		}

		/**
		 * @return a judge created according to the specified "multiline arrow string"
		 */
		DependencyJudge build() {
			// TODO: create correct judge
			return new AllFailDependencyJudge();
		}

		/**
		 * @param builder
		 * 		the builder to use
		 * @param arrowString
		 * 		the "multiline arrow string" to interpret
		 *
		 * @return a judge created with the builder according to the "multiline arrow string"
		 */
		static DependencyJudge build(DependencyJudgeBuilder builder, String arrowString) {
			return new BuildFromArrowString(builder, arrowString).build();
		}

	}

}
