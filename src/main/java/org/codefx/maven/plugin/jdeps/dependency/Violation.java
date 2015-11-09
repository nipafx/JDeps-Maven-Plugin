package org.codefx.maven.plugin.jdeps.dependency;

import com.google.common.collect.ImmutableList;

import java.util.Objects;

import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.joining;

/**
 * A violation is a dependency of a class on another class which is marked as JDK-internal API by jdeps.
 * <p>
 * It consists of a {@link Type} which depends on one or more {@link InternalType}s.
 */
public class Violation {

	private final Type dependent;
	private final ImmutableList<InternalType> internalDependencies;

	/**
	 * @throws IllegalStateException
	 * 		if the list of internal dependencies is empty
	 */
	private Violation(Type dependent, ImmutableList<InternalType> internalDependencies) {
		this.dependent = requireNonNull(dependent, "The argument 'dependent' must not be null.");
		this.internalDependencies =
				requireNonNull(internalDependencies, "The argument 'internalDependencies' must not be null.");

		if (internalDependencies.size() == 0)
			throw new IllegalArgumentException(
					"A violation must contain at least one internal dependency.");
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
	public static Violation buildFor(Type dependent, ImmutableList<InternalType> internalDependencies) {
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
		return internalDependencies;
	}

	// #begin EQUALS, HASHCODE, TOSTRING

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
				&& Objects.equals(internalDependencies, other.internalDependencies);
	}

	@Override
	public int hashCode() {
		return Objects.hash(dependent, internalDependencies);
	}

	@Override
	public String toString() {
		String dependencies = internalDependencies
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
				internalDependencies.stream()
						.map(Object::toString)
						.collect(joining("\n" + dependencyLineStart, dependencyLineStart, ""));
	}

	// #end EQUALS, HASHCODE, TOSTRING

	// #begin BUILDER

	/**
	 * Allows to build a {@link Violation} (which is immutable) by successively adding dependecies.
	 */
	public static class ViolationBuilder {

		private final Type dependent;

		private final ImmutableList.Builder<InternalType> internalDependenciesBuilder;

		private ViolationBuilder(Type dependent) {
			this.dependent = requireNonNull(dependent, "The argument 'dependent' must not be null.");
			this.internalDependenciesBuilder = ImmutableList.builder();
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

			internalDependenciesBuilder.add(dependency);
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

			internalDependenciesBuilder.addAll(dependencies);
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
				return new Violation(dependent, internalDependenciesBuilder.build());
			} catch (IllegalArgumentException ex) {
				String message = "The violation could not be built because it contains no internal dependencies. " +
						"Maybe the violation block ended prematurely?";
				throw new IllegalStateException(message, ex);
			}
		}

	}

	// #end BUILDER

}
