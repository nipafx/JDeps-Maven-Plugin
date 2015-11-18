package org.codefx.maven.plugin.jdeps.dependency;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Ordering;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static java.lang.Integer.min;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.joining;

/**
 * A violation is a dependency of a class on another class which is marked as JDK-internal API by jdeps.
 * <p>
 * It consists of a {@link Type} which depends on one or more {@link InternalType}s.
 */
public final class Violation implements Comparable<Violation> {

	private final Type dependent;
	private final ImmutableList<InternalType> sortedInternalDependencies;

	/**
	 * @throws IllegalStateException
	 * 		if the list of internal dependencies is empty
	 */
	private Violation(Type dependent, Collection<InternalType> internalDependencies) {
		this.dependent = requireNonNull(dependent, "The argument 'dependent' must not be null.");

		requireNonNull(internalDependencies, "The argument 'internalDependencies' must not be null.");
		if (internalDependencies.size() == 0)
			throw new IllegalArgumentException(
					"A violation must contain at least one internal dependency.");
		sortedInternalDependencies = sorted(internalDependencies);
	}

	private static ImmutableList<InternalType> sorted(Iterable<InternalType> dependencies) {
		boolean dependenciesAreOrdered = Ordering.natural().isOrdered(dependencies);
		if (dependenciesAreOrdered)
			if (dependencies instanceof ImmutableList)
				return (ImmutableList<InternalType>) dependencies;
			else
				return ImmutableList.copyOf(dependencies);
		else
			return Ordering.natural().immutableSortedCopy(dependencies);
	}

	/**
	 * Builds a new violation.
	 *
	 * @param dependent
	 * 		the dependent which contains the violating dependency
	 * @param internalDependencies
	 * 		the types the dependent depends upon
	 *
	 * @return a {@link ViolationBuilder}
	 *
	 * @throws IllegalStateException
	 * 		if the list of internal dependencies is empty
	 */
	public static Violation buildFor(Type dependent, Collection<InternalType> internalDependencies) {
		return new Violation(dependent, internalDependencies);
	}

	/**
	 * Starts building a new violation.
	 *
	 * @param dependent
	 * 		the dependent which contains the violating dependency
	 *
	 * @return a {@link ViolationBuilder}
	 */
	public static ViolationBuilder buildForDependent(Type dependent) {
		return new ViolationBuilder(dependent);
	}

	/**
	 * @return the dependent which contains the dependencies on internal types
	 */
	public Type getDependent() {
		return dependent;
	}

	/**
	 * @return the internal types upon which {@link #getDependent()} depends
	 */
	public ImmutableList<InternalType> getInternalDependencies() {
		return sortedInternalDependencies;
	}

	// #begin COMPARETO, EQUALS, HASHCODE, TOSTRING

	@Override
	public int compareTo(Violation other) {
		if (this == other)
			return 0;

		int comparisonByDependents = dependent.compareTo(other.dependent);
		if (comparisonByDependents != 0)
			return comparisonByDependents;

		// check the elements in both lists
		int maxIndex = min(sortedInternalDependencies.size(), other.sortedInternalDependencies.size());
		for (int i = 0; i < maxIndex; i++) {
			int comparisonByDependencies =
					sortedInternalDependencies.get(i).compareTo(other.sortedInternalDependencies.get(i));
			if (comparisonByDependencies != 0)
				return comparisonByDependencies;
		}
		// both lists contain the same elements up to the length of the shorter one; make the shorter one smaller
		return sortedInternalDependencies.size() - other.sortedInternalDependencies.size();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;

		Violation other = (Violation) obj;
		return Objects.equals(dependent, other.dependent)
				&& Objects.equals(sortedInternalDependencies, other.sortedInternalDependencies);
	}

	@Override
	public int hashCode() {
		return Objects.hash(dependent, sortedInternalDependencies);
	}

	@Override
	public String toString() {
		String dependencies = sortedInternalDependencies
				.stream()
				.map(Object::toString)
				.collect(joining(", ", "{", "}"));
		return dependent + " -> " + dependencies;
	}

	/**
	 * @return a string representation of a violation which spans multiple lines
	 */
	public String toMultiLineString() {
		String dependentLine = ".\t" + dependent + "\n";
		String dependencyLineStart = ".\t\t -> ";
		return dependentLine +
				sortedInternalDependencies.stream()
						.map(Object::toString)
						.collect(joining("\n" + dependencyLineStart, dependencyLineStart, ""));
	}

	// #end COMPARETO, EQUALS, HASHCODE, TOSTRING

	// #begin BUILDER

	/**
	 * Allows to build a {@link Violation} (which is immutable) by successively adding dependecies.
	 */
	public static class ViolationBuilder {

		private final Type dependent;

		private final List<InternalType> internalDependencies;

		private ViolationBuilder(Type dependent) {
			this.dependent = requireNonNull(dependent, "The argument 'dependent' must not be null.");
			this.internalDependencies = new ArrayList<>();
		}

		/**
		 * Adds the specified {@link InternalType} as a dependency.
		 *
		 * @param dependency
		 * 		an internal dependent
		 *
		 * @return this builder
		 */
		public ViolationBuilder addDependency(InternalType dependency) {
			requireNonNull(dependency, "The argument 'dependency' must not be null.");

			internalDependencies.add(dependency);
			return this;
		}

		/**
		 * Adds the specified {@link InternalType}s as dependencies.
		 *
		 * @param dependencies
		 * 		a variable number of internal types
		 *
		 * @return this builder
		 */
		public ViolationBuilder addDependencies(InternalType... dependencies) {
			requireNonNull(dependencies, "The argument 'dependencies' must not be null.");

			Collections.addAll(internalDependencies, dependencies);
			return this;
		}

		/**
		 * Adds the specified {@link InternalType}s as dependencies.
		 *
		 * @param dependencies
		 * 		an iterable of internal types
		 *
		 * @return this builder
		 */
		public ViolationBuilder addDependencies(Iterable<InternalType> dependencies) {
			requireNonNull(dependencies, "The argument 'dependencies' must not be null.");

			dependencies.forEach(internalDependencies::add);
			return this;
		}

		/**
		 * @return a new {@link Violation}
		 *
		 * @throws IllegalStateException
		 * 		if the list of internal dependencies is empty
		 */
		public Violation build() {
			try {
				return new Violation(dependent, internalDependencies);
			} catch (IllegalArgumentException ex) {
				String message = "The violation could not be built because it contains no internal dependencies. " +
						"Maybe the violation block ended prematurely?";
				throw new IllegalStateException(message, ex);
			}
		}

	}

	// #end BUILDER

}
