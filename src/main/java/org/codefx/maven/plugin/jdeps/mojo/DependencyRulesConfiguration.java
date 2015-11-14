package org.codefx.maven.plugin.jdeps.mojo;

import org.codefx.maven.plugin.jdeps.rules.DependencyJudge;
import org.codefx.maven.plugin.jdeps.rules.DependencyJudgeBuilder;
import org.codefx.maven.plugin.jdeps.rules.DependencyRule;
import org.codefx.maven.plugin.jdeps.rules.Severity;
import org.codefx.maven.plugin.jdeps.rules.SimpleDependencyJudge;
import org.codefx.maven.plugin.jdeps.rules.TypeNameHierarchyMapDependencyJudge
		.TypeNameHierarchyMapDependencyJudgeBuilder;
import org.codehaus.plexus.classworlds.launcher.ConfigurationException;

import java.util.List;

import static java.util.Objects.requireNonNull;
import static org.codefx.maven.plugin.jdeps.mojo.MojoLogging.logger;

/**
 * Captures the MOJO configuration that pertains the dependency rules and {@link #createJudge() creates} the
 * according {@link DependencyJudge}.
 */
class DependencyRulesConfiguration {

	private final Severity defaultSeverity;
	private final List<XmlRule> xml;
	private final List<String> arrow;

	public DependencyRulesConfiguration(Severity defaultSeverity, List<XmlRule> xml, List<String> arrow) {
		this.defaultSeverity = requireNonNull(defaultSeverity, "The argument 'defaultSeverity' must not be null.");
		this.xml = requireNonNull(xml, "The argument 'xml' must not be null.");
		this.arrow = requireNonNull(arrow, "The argument 'arrow' must not be null.");
	}

	/**
	 * @return the {@link DependencyJudge} matching the configuration
	 */
	public DependencyJudge createJudge() throws ConfigurationException {
		if (xml.isEmpty() && arrow.isEmpty())
			return new SimpleDependencyJudge(defaultSeverity);

		DependencyJudgeBuilder dependencyJudgeBuilder =
				new TypeNameHierarchyMapDependencyJudgeBuilder().withDefaultSeverity(defaultSeverity);

		addXmlRulesToBuilder(xml, dependencyJudgeBuilder);
		addArrowRulesToBuilder(arrow, dependencyJudgeBuilder);

		return dependencyJudgeBuilder.build();
	}

	static void addXmlRulesToBuilder(List<XmlRule> xmlRules, DependencyJudgeBuilder dependencyJudgeBuilder)
			throws ConfigurationException {
		logStartAddingRules(xmlRules, "XML");

		for (XmlRule rule : xmlRules) {
			DependencyRule dependencyRule = rule.asDependencyRule();
			dependencyJudgeBuilder.addDependency(dependencyRule);
			logAddedRule(dependencyRule);
		}

		logDoneAddingRules(xmlRules, "XML");
	}

	static void addArrowRulesToBuilder(List<String> arrowRules, DependencyJudgeBuilder dependencyJudgeBuilder)
			throws ConfigurationException {
		logStartAddingRules(arrowRules, "arrow");

		for (String arrowRule : arrowRules) {
			ArrowRuleParser
					.parseRules(arrowRule)
					.forEach(rule -> {
						dependencyJudgeBuilder.addDependency(rule);
						logAddedRule(rule);
					});
		}

		logDoneAddingRules(arrowRules, "arrow");
	}

	private static void logStartAddingRules(List<?> rules, String ruleName) {
		if (!rules.isEmpty())
			logger().debug("Adding configured " + ruleName + " rules:");
	}

	private static void logAddedRule(DependencyRule dependencyRule) {
		logger().debug("\t" + dependencyRule);
	}

	private static void logDoneAddingRules(List<?> rules, String ruleName) {
		if (rules.isEmpty())
			logger().info("No " + ruleName + " rules configured.");
		else
			logger().info(rules.size() + " " + ruleName + " rules configured and added.");
	}

}
