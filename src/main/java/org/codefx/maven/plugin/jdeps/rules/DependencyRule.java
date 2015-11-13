package org.codefx.maven.plugin.jdeps.rules;

import java.util.Objects;

import static java.lang.String.format;

public final class DependencyRule {

	private static final String ERROR_MESSAGE_MISSING_TYPE = "The rule %s defines no %s.";
	private static final String ERROR_MESSAGE_MISSING_SEVERITY = "The rule %s defines no severity.";

	private static final String ERROR_MESSAGE_NAME_PART_EMPTY =
			"In the rule %s the name '%s' contains one empty part. Make sure it has no superfluous dots.";
	private static final String ERROR_MESSAGE_NAME_PART_STARTS_INVALID =
			"In the rule %s a part of the name '%s' starts with the invalid character '%s'.";
	private static final String ERROR_MESSAGE_NAME_PART_CONTAINS_INVALID =
			"In the rule %s the name '%s' contains the invalid character '%s'.";

	private static final String ERROR_MESSAGE_INVALID_SEVERITY = "The rule %s defines an invalid severity.";

	private final String dependent;
	private final String dependency;
	private final Severity severity;

	private DependencyRule(String dependent, String dependency, Severity severity) {
		this.dependent = dependent;
		this.dependency = dependency;
		this.severity = severity;
	}

	public static DependencyRule of(String dependent, String dependency, String severity) {
		Severity asSeverity = parseSeverity(dependent, dependency, severity);
		return of(dependent, dependency, asSeverity);
	}

	private static Severity parseSeverity(String dependent, String dependency, String severity) {
		if (severity == null)
			throw new IllegalArgumentException(
					format(ERROR_MESSAGE_MISSING_SEVERITY, toString(dependent, dependency, (String) null)));
		try {
			return Severity.valueOf(severity);
		} catch (IllegalArgumentException ex) {
			throw new IllegalArgumentException(
					format(ERROR_MESSAGE_INVALID_SEVERITY, toString(dependent, dependency, severity)));
		}
	}

	public static DependencyRule of(String dependent, String dependency, Severity severity) {
		checkName(dependent, toString(dependent, dependency, severity), "dependent");
		checkName(dependency, toString(dependent, dependency, severity), "dependency");
		if (severity == null)
			throw new IllegalArgumentException(
					format(ERROR_MESSAGE_MISSING_SEVERITY, toString(dependent, dependency, (String) null)));

		return new DependencyRule(dependent, dependency, severity);
	}

	/*
	 * If only 'checkValidity()' were accessible, we would have to have the same checks for the dependent and the
	 * dependency. To prevent this useless repetition 'DependencyRule' exposes 'checkName'.
	 */

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
	 * @throws IllegalArgumentException
	 * 		if the name is invalid
	 */
	static void checkName(String name, String ruleAsString, String role) throws IllegalArgumentException {
		if (name == null || name.isEmpty())
			throw new IllegalArgumentException(format(ERROR_MESSAGE_MISSING_TYPE, ruleAsString, role));

		for (String namePart : name.split("\\.")) {
			if (namePart == null || namePart.isEmpty())
				throw new IllegalArgumentException(
						format(ERROR_MESSAGE_NAME_PART_EMPTY, ruleAsString, name));
			if (!Character.isJavaIdentifierStart(namePart.charAt(0)))
				throw new IllegalArgumentException(
						format(ERROR_MESSAGE_NAME_PART_STARTS_INVALID, ruleAsString, name, namePart.charAt(0)));
			for (int i = 1; i < namePart.length(); i++)
				if (!Character.isJavaIdentifierPart(namePart.charAt(i)))
					throw new IllegalArgumentException(
							format(ERROR_MESSAGE_NAME_PART_CONTAINS_INVALID, ruleAsString, name, namePart.charAt(i)));
		}

		// split does not include trailing empty strings so make an extra check for string ending with "."
		if (name.endsWith("."))
			throw new IllegalArgumentException(
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
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof DependencyRule))
			return false;
		DependencyRule that = (DependencyRule) o;
		return Objects.equals(dependent, that.dependent)
				&& Objects.equals(dependency, that.dependency);
	}

	@Override
	public int hashCode() {
		return Objects.hash(dependent, dependency);
	}

	@Override
	public String toString() {
		return toString(dependent, dependency, severity);
	}

	private static String toString(String dependent, String dependency, Severity severity) {
		return toString(dependent, dependency, severity == null ? "null" : severity.toString());
	}

	private static String toString(String dependent, String dependency, String severity) {
		return format("(%s -> %s: %s)", dependent, dependency, severity);
	}

}
