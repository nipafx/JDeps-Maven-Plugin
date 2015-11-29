package org.codefx.maven.plugin.jdeps;

import org.codefx.maven.plugin.jdeps.dependency.InternalType;
import org.codefx.maven.plugin.jdeps.dependency.Type;
import org.codefx.maven.plugin.jdeps.dependency.Violation;
import org.codefx.maven.plugin.jdeps.dependency.Violation.ViolationBuilder;

import java.util.Arrays;

/**
 * Factory methods that can be shared across different tests.
 */
public class Factory {

	/**
	 * @param dependent
	 * 		the fully qualified name of the dependent
	 * @param dependencies
	 * 		a variable number of dependencies
	 *
	 * @return a violation
	 */
	public static Violation violation(String dependent, String... dependencies) {
		ViolationBuilder violationBuilder = Violation.buildForDependent(Type.of(dependent));
		Arrays.stream(dependencies)
				// 'InternalType.of' requires the fully qualified name to be split into package and class name;
				// to not write such code here, create a 'Type' from the fully qualified name, first
				.map(Type::of)
				.map(type -> InternalType.of(type.getPackageName(), type.getClassName(), "", ""))
				.forEachOrdered(violationBuilder::addDependency);
		return violationBuilder.build();
	}

	/**
	 * @return the violation in {@code OnActions}
	 */
	public static Violation onActionsViolation() {
		return violation(
				"org.codefx.maven.plugin.testproject.OnActions",
				"sun.security.action.GetBooleanAction", "sun.security.action.GetIntegerAction");

	}

	/**
	 * @return the violation in {@code OnBASE64}
	 */
	public static Violation onBASE64Violation() {
		return violation(
				"org.codefx.maven.plugin.testproject.OnBASE64",
				"sun.misc.BASE64Decoder", "sun.misc.BASE64Encoder");

	}

	/**
	 * @return the violation in {@code OnUnsafe}
	 */
	public static Violation onUnsafeViolation() {
		return violation("org.codefx.maven.plugin.testproject.OnUnsafe", "sun.misc.Unsafe");
	}

}
