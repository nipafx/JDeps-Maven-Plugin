package org.codefx.maven.plugin.jdeps.rules;

import com.google.common.collect.ImmutableList;

import java.util.Iterator;

import static java.lang.Integer.max;
import static java.lang.String.format;
import static java.lang.String.join;
import static java.util.Objects.requireNonNull;

/**
 * Splits a type's or package's name into a an iteration of successivley more general names.
 * <p>
 * Depending on the {@link PackageInclusion} specified during construction, this might include only the package
 * containing the type or all "super packages".
 * <p>
 * E.g.: "java.lang.String" to { "java.lang.String", "java.lang", "java" } or to { "java.lang.String", "java.lang" }.
 */
final class TypeNameHierarchy implements Iterable<String> {

	private final ImmutableList<String> hierarchy;

	private TypeNameHierarchy(ImmutableList<String> hierarchy) {
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
	 * @param fullName
	 * 		the fully qualified name of the type or package for which the hierarchy will be created
	 * @param packageInclusion
	 * 		determines which packages to include in the resulting hierarchy; for {@link PackageInclusion#FLAT FLAT}
	 * 		this will only be the package containing the type; for
	 * 		{@link PackageInclusion#HIERARCHICAL HIERARCHICAL} it will be all "super packages"
	 *
	 * @return a type name hierarchy for the specified name including the specified packages
	 */
	public static TypeNameHierarchy forFullyQualifiedName(String fullName, PackageInclusion packageInclusion) {
		requireNonNull(fullName, "The argument 'fullName' must not be null.");
		requireNonNull(packageInclusion, "The argument 'packageInclusion' must not be null.");

		String[] nameParts = fullName.split("\\.");
		int indexOfTopmostNamePart = indexOfTopmostNamePart(nameParts, packageInclusion);
		String[] hierarchy = new String[nameParts.length - indexOfTopmostNamePart];

		String partialName = "";
		for (int i = 0; i < nameParts.length; i++) {
			// append next name part
			if (i == 0)
				partialName = nameParts[0];
			else
				partialName += "." + nameParts[i];
			// add name to hierarchy if contains topmost package
			if (i >= indexOfTopmostNamePart) {
				// the index is counted from the back because we create the less specific names first
				// but want the more specific names to appear at the front of the array
				int indexInHierarchy = nameParts.length - 1 - i;
				hierarchy[indexInHierarchy] = partialName;
			}
		}

		return new TypeNameHierarchy(ImmutableList.copyOf(hierarchy));
	}

	private static int indexOfTopmostNamePart(String[] nameParts, PackageInclusion packageInclusion) {
		switch (packageInclusion) {
			case FLAT:
				return indexOfContainingPackage(nameParts);
			case HIERARCHICAL:
				return 0;
			default:
				throw new IllegalArgumentException(format("Unknown inclusion '%s'.", packageInclusion));
		}
	}

	private static int indexOfContainingPackage(String[] nameParts) {
		for (int i = 0; i < nameParts.length; i++) {
			char firstLetter = getFirstLetter(nameParts, i);
			boolean foundTypeName = Character.isUpperCase(firstLetter);
			if (foundTypeName)
				return max(i - 1, 0);
		}
		return 0;
	}

	private static char getFirstLetter(String[] nameParts, int index) {
		String namePart = nameParts[index];
		if (namePart.isEmpty())
			throw new IllegalArgumentException(
					format("The name %scontained an empty name.", join(".", nameParts)));
		return namePart.charAt(0);
	}

	@Override
	public Iterator<String> iterator() {
		return hierarchy.iterator();
	}

}
