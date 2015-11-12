package org.codefx.maven.plugin.jdeps.mojo;

import org.apache.maven.plugin.logging.Log;
import org.codefx.maven.plugin.jdeps.rules.DependencyJudge;
import org.codefx.maven.plugin.jdeps.rules.DependencyJudgeBuilder;
import org.codefx.maven.plugin.jdeps.rules.Severity;
import org.codefx.maven.plugin.jdeps.rules.TypeNameHierarchyMapDependencyJudge
		.TypeNameHierarchyMapDependencyJudgeBuilder;
import org.codehaus.plexus.classworlds.launcher.ConfigurationException;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Objects.requireNonNull;

/**
 * Captures the MOJO configuration that pertains the dependency rules and {@link #createJudge() creates} the
 * according {@link DependencyJudge}.
 */
class DependencyRulesConfiguration {

	private final Log log;

	private final List<Rule> verbose;
	private final List<String> arrow;

	public DependencyRulesConfiguration(Log log, List<Rule> verbose, List<String> arrow) {
		this.log = requireNonNull(log, "The argument 'log' must not be null.");
		this.verbose = requireNonNull(verbose, "The argument 'verbose' must not be null.");
		this.arrow = requireNonNull(arrow, "The argument 'arrow' must not be null.");
	}

	/**
	 * @return the {@link DependencyJudge} matching the configuration
	 */
	public DependencyJudge createJudge() throws ConfigurationException {
		if (log.isInfoEnabled()) {
			log.info("Verbose rules:\n" + rulesToString(verbose));
			log.info("Arrow rules:\n" + rulesToString(arrow));
		}

		DependencyJudgeBuilder dependencyJudgeBuilder =
				new TypeNameHierarchyMapDependencyJudgeBuilder().withDefaultSeverity(Severity.FAIL);
		addVerboseRulesToBuilder(verbose, dependencyJudgeBuilder);

		return dependencyJudgeBuilder.build();
	}

	private static void addVerboseRulesToBuilder(List<Rule> verbose, DependencyJudgeBuilder dependencyJudgeBuilder)
			throws ConfigurationException {
		for (Rule rule : verbose) {
			rule.checkValidity();
			dependencyJudgeBuilder.addDependency(rule.getDependent(), rule.getDependency(), rule.getSeverity());
		}
	}

	private static <E> String rulesToString(Collection<E> rules) {
		return rules.stream()
				.map(Object::toString)
				.collect(Collectors.joining("\n\t", "\t", "\n"));
	}

}
