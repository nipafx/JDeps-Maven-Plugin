package org.codefx.maven.plugin.jdeps.rules;

import org.codehaus.plexus.classworlds.launcher.ConfigurationException;

import java.util.stream.Stream;

import static java.lang.String.format;
import static java.util.Objects.requireNonNull;

/**
 * A dependency rule {@code (Dependent -> Dependency: Severity)} defined in full XML.
 * <p>
 * This class is used as a vehicle for the Mojo's configuration parameters. Note that it must be {@code public},
 * requires a parameterless constructor and that changing its name or the name of its fields would break existing
 * configurations.
 */
public class XmlRule {

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
	public XmlRule(String dependent, String dependency, Severity severity) {
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
	public XmlRule(DependencyRule dependencyRule) {
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
	 * @param indent
	 * 		the string used to indent inner XML, possible {@code "\t"}
	 *
	 * @return this rule as an XML string
	 */
	public Stream<String> toXmlLines(String indent) {
		requireNonNull(indent, "The argument 'indent' must not be null.");
		if (!indent.trim().isEmpty())
			throw new IllegalArgumentException("The argument 'indent' must only consist of whitespace.");

		return Stream.of(
				"<xmlRule>",
				indent + "<dependent>" + dependent + "</dependent>",
				indent + "<dependency>" + dependency + "</dependency>",
				indent + "<severity>" + severity + "</severity>",
				"</xmlRule>");
	}

}
