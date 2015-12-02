package org.codefx.mvn.jdeps.rules;

import org.codefx.mvn.jdeps.dependency.Type;

/**
 * Judges the severity of individual dependencies according to predefined rules.
 *
 * <h2>Rules</h2>
 * Rules are specified during construction and generally take the form {@code (dependant -> dependency; severity)}.
 * The {@code dependent} as well as the {@code dependency} can be either a type or a package.
 * <p>
 * Given a pair of types, a rule is said to <em>match</em> if the first type belongs to its dependent and the second
 * type belongs to its dependency. "Belongs to" has a different meaning depending on whether the judge is "flat" or
 * "hierarchical" - see <em>Hierarchical vs. Flat</em> for details and examples - but a type always "belongs to" itself
 * and the package that contains it.
 * <p>
 * The <em>best match</em> for a pair of types is the rule that has the most specific dependant that contains the first
 * type and out of those partial matches defines the most specific dependency that contains the second.
 * <p>
 * Calling the judge with a pair of types returns either the severity specified by the best matching rule or, if no
 * rule matches, the default severity specified during construction.
 *
 * <h3>Example</h3>
 * Given the rules {@code A (com.foo -> sun.misc.Unsafe; WARN)}, {@code B (com.foo -> java.lang; WARN)}, and
 * {@code C (com.foo.Bar -> sun.misc; WARN)}:
 * <ul>
 * <li>for {@code com.foo.Foo -> sun.misc.Unsafe} the only match will be {@code A} because {@code Foo} does not belong
 * to {@code C}'s {@code Bar} and {@code Unsafe} does not belong to {@code B}'s {@code java.lang}
 * <li>for {@code com.foo.Bar -> sun.misc.Unsafe} the best match will be {@code C} because it has the most
 * specific dependent of the three rules and its dependency still matches {@code Unsafe}
 * <li>for {@code com.foo.Bar -> java.lang.String} the best match will be {@code B} because even though {@code C}
 * defines a more specific dependant (an exact match to be precise) it <em>does not</em> define a matching dependency
 * so it does not match as a whole
 * </ul>
 *
 * <h2>Hierarchical vs. Flat</h2>
 *
 * <h3>Flat Judge</h3>
 * A {@link PackageInclusion#FLAT flat} judge adheres to the official interpretation of Java packages, which are <em>not</em> hierarchical.
 * In this case there is no relation between, e.g., the packages "com.foo" and "com.foo.bar".
 * <p>
 * Rules defined for a package will only match the contained types, including their inner types. Rules defined for
 * types will match these types and the inner types.
 *
 * <h4>Example</h4>
 * Given the rule {@code (com.foo.Bar -> sun.misc.Unsafe; WARN)}:
 * <ul>
 * <li>{@code com.foo.Bar -> sun.misc.Unsafe} will match
 * <li>{@code com.foo.Bar.InnerClass -> sun.misc.Unsafe} will match because {@code InnerClass} belongs to {@code Bar}
 * <li>{@code com.foo.Baz -> sun.misc.Unsafe} will not match because {@code Baz} is not {@code Bar}
 * <li>{@code com.foo.Bar -> sun.misc.BASE64Decoder} will not match because {@code BASE64Decoder} is not {@code Unsafe}
 * </ul>
 * Given the rule {@code (com.foo -> sun.misc.Unsafe; WARN)}:
 * <ul>
 * <li>{@code com.foo.Bar -> sun.misc.Unsafe} will match because {@code Bar} is in the correct package
 * <li>{@code com.foo.Bar.InnerClass -> sun.misc.Unsafe} will match because {@code InnerClass} belongs to {@code Bar},
 * which is in the correct package
 * <li>{@code com.foo.Baz -> sun.misc.Unsafe} will match because {@code Baz} is in the correct package
 * <li>{@code com.foo.Bar -> sun.misc.BASE64Decoder} will not match because {@code BASE64Decoder} is not {@code Unsafe}
 * <li>{@code com.foo.bar.Bar -> sun.misc.Unsafe} will not match because for a flat judge {@code com.foo.bar} does not
 * belong to {@code com.foo}
 * </ul>
 *
 * <h3>Hierarchical Judge</h3>
 * A {@link PackageInclusion#HIERARCHICAL hierarchical} judge will interpret package names similar to folders and thus create a relation where packages can
 * contain other packages, e.g. {@code sun} contains {@code sun.misc}.
 *
 * <h4>Example</h4>
 * Given the rule {@code (com.foo.Bar -> sun.misc.Unsafe; WARN)}:
 * <ul>
 * <li>{@code com.foo.Bar -> sun.misc.Unsafe} will match
 * <li>{@code com.foo.Bar.InnerClass -> sun.misc.Unsafe} will match because {@code InnerClass} belongs to {@code Bar}
 * <li>{@code com.foo.Baz -> sun.misc.Unsafe} will not match because {@code Baz} is not {@code Bar}
 * <li>{@code com.foo.Bar -> sun.misc.BASE64Decoder} will not match because {@code BASE64Decoder} is not {@code Unsafe}
 * </ul>
 * Given the rule {@code (com.foo -> sun.misc.Unsafe; WARN)}:
 * <ul>
 * <li>{@code com.foo.Bar -> sun.misc.Unsafe} will match because {@code Bar} is in the correct package
 * <li>{@code com.foo.Bar.InnerClass -> sun.misc.Unsafe} will match because {@code InnerClass} belongs to {@code Bar},
 * which is in the correct package
 * <li>{@code com.foo.Baz -> sun.misc.Unsafe} will match because {@code Baz} is in the correct package
 * <li>{@code com.foo.Bar -> sun.misc.BASE64Decoder} will not match because {@code BASE64Decoder} is not {@code Unsafe}
 * <li>{@code com.foo.bar.Bar -> sun.misc.Unsafe} will match because for a hierarchical judge {@code com.foo.bar}
 * belongs to {@code com.foo}
 * </ul>
 */
public interface DependencyJudge {

	/**
	 * Indicates the severity of the specified dependency {@code dependent -> dependency}.
	 *
	 * @param dependent
	 * 		the type which depends on the the other type
	 * @param dependency
	 * 		the type upon which the {@code dependent} depends
	 *
	 * @return the severity of the dependency
	 */
	default Severity judgeSeverity(Type dependent, Type dependency) {
		return judgeSeverity(dependent.getFullyQualifiedName(), dependency.getFullyQualifiedName());
	}

	/**
	 * Indicates the severity of the specified dependency.
	 *
	 * @param dependentName
	 * 		fully qualified name of the type or package which depends on the the other
	 * @param dependencyName
	 * 		fully qualified name of the type or package upon which the {@code dependent} depends
	 *
	 * @return the severity of the dependency
	 */
	Severity judgeSeverity(String dependentName, String dependencyName);

}
