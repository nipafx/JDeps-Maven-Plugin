package org.codefx.maven.plugin.jdeps.mojo;

import org.codefx.maven.plugin.jdeps.rules.DependencyRule;
import org.codefx.maven.plugin.jdeps.rules.Severity;
import org.codehaus.plexus.classworlds.launcher.ConfigurationException;

import static java.lang.String.format;

/**
 * A dependency rule {@code (Dependent -> Dependency: Severity)} defined in full XML.
 * <p>
 * This class is only used as a vehicle for the Mojo's configuration parameters. Note that it requires a parameterless
 * constructor and that changing its name or the name of its fields would break existing configurations.
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
	 * Constructor for tests.
	 */
	XmlRule(String dependent, String dependency, Severity severity) {
		this.dependent = dependent;
		this.dependency = dependency;
		this.severity = severity;
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

}
