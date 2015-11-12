package org.codefx.maven.plugin.jdeps.mojo;

import org.codefx.maven.plugin.jdeps.rules.DependencyRule;
import org.codefx.maven.plugin.jdeps.rules.Severity;
import org.codehaus.plexus.classworlds.launcher.ConfigurationException;

import static java.lang.String.format;

public class XmlRule {

	private String dependent;
	private String dependency;
	private Severity severity;

	public XmlRule() {
		// default constructor for use by Maven's parameter injection
	}

	public XmlRule(String dependent, String dependency, Severity severity) {
		this.dependent = dependent;
		this.dependency = dependency;
		this.severity = severity;
	}

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
