package org.codefx.maven.plugin.jdeps.mojo;

import org.codefx.maven.plugin.jdeps.rules.Severity;
import org.codehaus.plexus.classworlds.launcher.ConfigurationException;

import static java.lang.String.format;

public class Rule {

	private static final String ERROR_MESSAGE_MISSING_TYPE = "The rule %s defines no %s.";
	private static final String ERROR_MESSAGE_MISSING_SEVERITY = "The rule %s defines no severity.";

	private static final String ERROR_MESSAGE_NAME_PART_EMPTY =
			"In the rule %s the name '%s' contains one empty part. Make sure it has no superfluous dots.";
	private static final String ERROR_MESSAGE_NAME_PART_STARTS_INVALID =
			"In the rule %s a part of the name '%s' starts with the invalid character '%s'.";
	private static final String ERROR_MESSAGE_NAME_PART_CONTAINS_INVALID =
			"In the rule %s the name '%s' contains the invalid character '%s'.";

	private String dependent;
	private String dependency;
	private Severity severity;

	public Rule() {
		// default constructor for use by Maven's parameter injection
	}

	public Rule(String dependent, String dependency, Severity severity) {
		this.dependent = dependent;
		this.dependency = dependency;
		this.severity = severity;
	}

	public void checkValidity() throws ConfigurationException {
		checkName(dependent, toString(), "dependent");
		checkName(dependency, toString(), "dependency");
		if (severity == null)
			throw new ConfigurationException(format(ERROR_MESSAGE_MISSING_SEVERITY, this));
	}

	/**
	 * Checks whether the specified name is a valid Java identifier for a package or class.
	 *
	 * @param name
	 * 		the name to check
	 * @param ruleAsString
	 * 		a textual representation of the rule so that it can be used in the error output
	 * @param role
	 * 		the role of the type called {@code name}, i.e. dependent or dependency
	 *
	 * @throws ConfigurationException
	 * 		if the name is invalid
	 */
	static void checkName(String name, String ruleAsString, String role) throws ConfigurationException {
		if (name == null || name.isEmpty())
			throw new ConfigurationException(format(ERROR_MESSAGE_MISSING_TYPE, ruleAsString, role));

		for (String namePart : name.split("\\.")) {
			if (namePart == null || namePart.isEmpty())
				throw new ConfigurationException(
						format(ERROR_MESSAGE_NAME_PART_EMPTY, ruleAsString, name));
			if (!Character.isJavaIdentifierStart(namePart.charAt(0)))
				throw new ConfigurationException(
						format(ERROR_MESSAGE_NAME_PART_STARTS_INVALID, ruleAsString, name, namePart.charAt(0)));
			for (int i = 1; i < namePart.length(); i++)
				if (!Character.isJavaIdentifierPart(namePart.charAt(i)))
					throw new ConfigurationException(
							format(ERROR_MESSAGE_NAME_PART_CONTAINS_INVALID, ruleAsString, name, namePart.charAt(i)));
		}

		// split does not include trailing empty strings so make an extra check for string ending with "."
		if (name.endsWith("."))
			throw new ConfigurationException(
					format(ERROR_MESSAGE_NAME_PART_EMPTY, ruleAsString, name));
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
