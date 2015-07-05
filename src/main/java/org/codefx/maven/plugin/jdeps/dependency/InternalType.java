package org.codefx.maven.plugin.jdeps.dependency;

import java.util.Objects;

/**
 * A type which is considered JDK-internal API by JDeps.
 * <p>
 * Besides the type's package and class name, an internal type also contains the category (e.g. "JDK internal API") and
 * source which contains the type (e.g. "rt.jar") as reported by JDeps.
 */
public final class InternalType extends Type {

	private final String category;
	private final String source;

	private InternalType(String packageName, String className, String category, String source) {
		super(packageName, className);
		Objects.requireNonNull(category, "The argument 'category' must not be null.");
		Objects.requireNonNull(source, "The argument 'source' must not be null.");

		this.category = category;
		this.source = source;
	}

	/**
	 * Returns an internal type for the specified arguments.
	 *
	 * @param packageName
	 *            the name of the package containing the type
	 * @param className
	 *            the name of the type's class
	 * @param category
	 *            the category as reported by JDeps (e.g. "JDK internal API")
	 * @param source
	 *            the source as reported by JDeps (e.g. "rt.jar")
	 * @return an internal type
	 */
	public static InternalType of(
			String packageName,
			String className,
			String category,
			String source) {
		return new InternalType(packageName, className, category, source);
	}

	/**
	 * @return the category of this internal dependency as reported by JDeps (e.g. "JDK internal API")
	 */
	public String getCategory() {
		return category;
	}

	/**
	 * @return the source of this internal dependency as reported by JDeps (e.g. "rt.jar")
	 */
	public String getSource() {
		return source;
	}

	@Override
	public String toString() {
		return super.toString() + " [" + category + ", " + source + "]";
	}

}
