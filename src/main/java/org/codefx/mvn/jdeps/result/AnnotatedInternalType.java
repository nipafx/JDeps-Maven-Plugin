package org.codefx.mvn.jdeps.result;

import org.codefx.mvn.jdeps.dependency.InternalType;
import org.codefx.mvn.jdeps.rules.Severity;

import static java.util.Objects.requireNonNull;

/**
 * An internal type which is annotated with a severity.
 * <p>
 * Note that the same internal type (e.g. "sun.misc.Unsafe") might be annotated with different severities depending on
 * which class depends on it. Maybe {@code com.foo.Bar -> sun.misc.Unsafe} is {@link Severity#INFORM INFORM} but
 * {@code com.foo.Baz -> sun.misc.Unsafe} is {@link Severity#WARN}.
 */
final class AnnotatedInternalType {

	private final InternalType type;
	private final Severity severity;

	private AnnotatedInternalType(InternalType type, Severity severity) {
		this.type = requireNonNull(type, "The argument 'type' must not be null.");
		this.severity = requireNonNull(severity, "The argument 'severity' must not be null.");
	}

	/**
	 * Returns an internal type annotated with the specified severity.
	 *
	 * @param type
	 * 		the internal type to annotate
	 * @param severity
	 * 		the severity
	 *
	 * @return an annotated internal type
	 */
	public static AnnotatedInternalType of(InternalType type, Severity severity) {
		return new AnnotatedInternalType(type, severity);
	}

	public InternalType getType() {
		return type;
	}

	public Severity getSeverity() {
		return severity;
	}

}
