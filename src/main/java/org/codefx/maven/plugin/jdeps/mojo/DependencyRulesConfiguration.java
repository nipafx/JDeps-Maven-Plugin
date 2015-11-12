package org.codefx.maven.plugin.jdeps.mojo;

import org.codefx.maven.plugin.jdeps.rules.DependencyJudge;
import org.codefx.maven.plugin.jdeps.rules.DependencyJudgeBuilder;
import org.codefx.maven.plugin.jdeps.rules.Severity;
import org.codefx.maven.plugin.jdeps.rules.TypeNameHierarchyMapDependencyJudge
		.TypeNameHierarchyMapDependencyJudgeBuilder;
import org.codehaus.plexus.classworlds.launcher.ConfigurationException;

import java.util.List;

import static java.util.Objects.requireNonNull;

/**
 * Captures the MOJO configuration that pertains the dependency rules and {@link #createJudge() creates} the
 * according {@link DependencyJudge}.
 */
class DependencyRulesConfiguration {

	private final List<XmlRule> xml;
	private final List<String> arrow;

	public DependencyRulesConfiguration(List<XmlRule> xml, List<String> arrow) {
		this.xml = requireNonNull(xml, "The argument 'xml' must not be null.");
		this.arrow = requireNonNull(arrow, "The argument 'arrow' must not be null.");
	}

	/**
	 * @return the {@link DependencyJudge} matching the configuration
	 */
	public DependencyJudge createJudge() throws ConfigurationException {
		DependencyJudgeBuilder dependencyJudgeBuilder =
				new TypeNameHierarchyMapDependencyJudgeBuilder().withDefaultSeverity(Severity.FAIL);

		addXmlRulesToBuilder(xml, dependencyJudgeBuilder);
		addArrowRulesToBuilder(arrow, dependencyJudgeBuilder);

		return dependencyJudgeBuilder.build();
	}

	static void addXmlRulesToBuilder(List<XmlRule> xml, DependencyJudgeBuilder dependencyJudgeBuilder)
			throws ConfigurationException {
		for (XmlRule rule : xml) {
			dependencyJudgeBuilder.addDependency(rule.asDependencyRule());
		}
	}

	static void addArrowRulesToBuilder(List<String> arrowRules, DependencyJudgeBuilder dependencyJudgeBuilder)
			throws ConfigurationException {
		for (String arrowRule : arrowRules)
			ArrowRuleParser.parseRules(arrowRule).forEach(dependencyJudgeBuilder::addDependency);
	}

}
