package org.codefx.maven.plugin.jdeps.mojo;

import org.codefx.maven.plugin.jdeps.rules.DependencyRule;
import org.codefx.maven.plugin.jdeps.rules.Severity;
import org.codehaus.plexus.classworlds.launcher.ConfigurationException;

import static java.lang.String.format;
import static java.util.Objects.requireNonNull;

/**
 * A dependency rule {@code (Dependent -> Dependency: Severity)} defined in full XML.
 * <p>
 * This class is only used as a vehicle for the Mojo's configuration parameters. Note that it requires a parameterless
 * constructor and that changing its name or the name of its fields would break existing configurations.
 */
class XmlRule {

	private String dependent;
	private String dependency;
	private Severity severity;

	/**
	 * Default constructor for use by Maven's parameter injection.
	 */
	public XmlRule() {
		// no code required
	}

	/**
	 * Creates a new XML rule {@code dependent -> dependency: severity}.
	 */
	XmlRule(String dependent, String dependency, Severity severity) {
		this.dependent = requireNonNull(dependent, "The argument 'dependent' must not be null.");
		this.dependency = requireNonNull(dependency, "The argument 'dependency' must not be null.");
		this.severity = requireNonNull(severity, "The argument 'severity' must not be null.");
	}

	/**
	 * Creates a new XML rule from the specified dependency rule.
	 *
	 * @param dependencyRule
	 * 		the rule to create an XML rule from
	 */
	XmlRule(DependencyRule dependencyRule) {
		dependent = dependencyRule.getDependent();
		dependency = dependencyRule.getDependency();
		severity = dependencyRule.getSeverity();
	}

	/**
	 * @return this rule as a {@link DependencyRule}
	 *
	 * @throws ConfigurationException
	 * 		if creating the {@code DependencyRule} fails
	 */
	public DependencyRule asDependencyRule() throws ConfigurationException {
		try {
			return DependencyRule.of(dependent, dependency, severity);
		} catch (IllegalArgumentException ex) {
			throw new ConfigurationException(ex.getMessage());
		}
	}

	public String getDependent() {
		return dependent;
	}

	public String getDependency() {
		return dependency;
	}

	public Severity getSeverity() {
		return severity;
	}

	@Override
	public String toString() {
		return format("(%s -> %s: %s)", dependent, dependency, severity);
	}

	/**
	 * Creates an XML string representing this rule.
	 *
	 * @param linePrefix
	 * 		the prefix to use for each line, possibly existing indentation
	 * @param indent
	 * 		the string used to indent inner XML, possible {@code "\t"}
	 *
	 * @return this rule as an XML string
	 */
	public String toXmlString(String linePrefix, String indent) {
		return ""
				+ linePrefix + "<xmlRule>\n"
				+ linePrefix + indent + "<dependent>" + dependent + "</dependent>\n"
				+ linePrefix + indent + "<dependency>" + dependency + "</dependency>\n"
				+ linePrefix + indent + "<severity>" + severity + "</severity>\n"
				+ linePrefix + "</xmlRule>\n";
	}

}
