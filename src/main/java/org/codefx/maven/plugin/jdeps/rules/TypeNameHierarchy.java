package org.codefx.maven.plugin.jdeps.rules;

import org.codefx.maven.plugin.jdeps.dependency.Type;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import static java.util.Objects.requireNonNull;

/**
 * Splits a type's or package's name into a an iteration of successivley more general package names.
 * <p>
 * E.g.: "java.lang.String" -> { "java.lang.String", "java.lang", "java" }
 */
final class TypeNameHierarchy implements Iterable<String> {

	private final List<String> hierarchy;

	private TypeNameHierarchy(List<String> hierarchy) {
		this.hierarchy =
				requireNonNull(hierarchy, "The argument 'hierarchy' must not be null.");
		for (int i = 1; i < hierarchy.size(); i++) {
			String shorterPrefix = hierarchy.get(i) + ".";
			String longerPrefix = hierarchy.get(i - 1);
			if (!longerPrefix.startsWith(shorterPrefix))
				throw new IllegalArgumentException();
		}
	}

	/**
	 * @param type
	 * 		the type for which the hierarchy will be created
	 *
	 * @return a type name hierarchy for the specified type
	 */
	public static TypeNameHierarchy forType(Type type) {
		requireNonNull(type, "The argument 'type' must not be null.");
		return forFullyQualifiedName(type.getFullyQualifiedName());
	}

	/**
	 * @param fullName
	 * 		the fully qualified name of the type or package for which the hierarchy will be created
	 *
	 * @return a type name hierarchy for the specified name
	 */
	public static TypeNameHierarchy forFullyQualifiedName(String fullName) {
		requireNonNull(fullName, "The argument 'fullName' must not be null.");

		String[] nameParts = fullName.split("\\.");
		String[] hierarchy = new String[nameParts.length];

		hierarchy[0] = fullName;
		String partialName = "";
		for (int i = 0; i < nameParts.length; i++) {
			if (i == 0)
				partialName = nameParts[0];
			else
				partialName += "." + nameParts[i];
			int hierarchyIndex = hierarchy.length - 1 - i;
			hierarchy[hierarchyIndex] = partialName;
		}

		return new TypeNameHierarchy(Arrays.asList(hierarchy));
	}

	@Override
	public Iterator<String> iterator() {
		return hierarchy.iterator();
	}

}
