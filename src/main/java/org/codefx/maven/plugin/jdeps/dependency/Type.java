package org.codefx.maven.plugin.jdeps.dependency;

import java.util.Comparator;
import java.util.Objects;

import static java.util.Objects.requireNonNull;

/**
 * A simple textual representation of a type consisting of the package and the class name.
 */
public class Type implements Comparable<Type> {

	private static final Comparator<Type> COMPARATOR = Comparator
			.comparing(Type::getPackageName)
			.thenComparing(Type::getClassName);

	private final String packageName;
	private final String className;

	/**
	 * Creates a new type.
	 *
	 * @param packageName
	 * 		the name of the package containing the type (dotted)
	 * @param className
	 * 		the name of the type's class (dotted)
	 */
	protected Type(String packageName, String className) {
		this.packageName = requireNonNull(packageName, "The argument 'packageName' must not be null.");
		this.className = requireNonNull(className, "The argument 'className' must not be null.");

		if (packageName.isEmpty())
			throw new IllegalArgumentException("The argument 'packageName' must not be empty.");
		if (className.isEmpty())
			throw new IllegalArgumentException("The argument 'className' must not be empty.");
	}

	/**
	 * Returns a type for the specified package and class name.
	 *
	 * @param packageName
	 * 		the name of the package containing the type (dotted)
	 * @param className
	 * 		the name of the type's class (dotted)
	 *
	 * @return a type
	 */
	public static Type of(String packageName, String className) {
		return new Type(packageName, className);
	}

	/**
	 * Returns a type for the specified fully qualified class name.
	 *
	 * @param qualifiedClassName
	 * 		the fully qualified name of the type's class (dotted)
	 *
	 * @return a type
	 */
	public static Type of(String qualifiedClassName) {
		requireNonNull(qualifiedClassName, "The argument 'qualifiedClassName' must not be null.");
		if (qualifiedClassName.isEmpty())
			throw new IllegalArgumentException("The argument 'qualifiedClassName' must not be empty.");

		int lastDotIndex = qualifiedClassName.lastIndexOf('.');
		if (lastDotIndex == -1)
			throw new IllegalArgumentException(
					"The argument 'qualifiedClassName' must be a fully qualified class name "
							+ "with at least one dot ('.').");

		String packageName = qualifiedClassName.substring(0, lastDotIndex);
		String className = qualifiedClassName.substring(lastDotIndex + 1);
		return new Type(packageName, className);
	}

	/**
	 * @return the dotted name of this type's package
	 */
	public String getPackageName() {
		return packageName;
	}

	/**
	 * @return this type's class name
	 */
	public String getClassName() {
		return className;
	}

	/**
	 * @return this type's fully qualified name, i.e.
	 * {@link #getPackageName() packageName}.{@link #getClassName() className}
	 */
	public String getFullyQualifiedName() {
		return packageName + "." + className;
	}

	// #begin COMPARETO / EQUALS / HASHCODE / TOSTRING

	@Override
	public final int compareTo(Type other) {
		if (this == other)
			return 0;

		return COMPARATOR.compare(this, other);
	}

	@Override
	public final boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof Type))
			return false;

		Type other = (Type) obj;
		return Objects.equals(this.packageName, other.packageName)
				&& Objects.equals(this.className, other.className);
	}

	@Override
	public final int hashCode() {
		return Objects.hash(packageName, className);
	}

	@Override
	public String toString() {
		return packageName + "." + className;
	}

	// #end COMPARETO / EQUALS / HASHCODE / TOSTRING

}
