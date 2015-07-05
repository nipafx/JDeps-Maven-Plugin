package org.codefx.maven.plugin.jdeps.dependency;

import static java.util.stream.Collectors.joining;

import java.util.Objects;

import com.google.common.collect.ImmutableList;

/**
 * A violation is a dependency of a class on another class which is marked as JDK-internal API by jdeps.
 * <p>
 * It consists of a {@link Type} which depends on one or more {@link InternalType}s.
 */
public class Violation {

	private final Type type;
	private final ImmutableList<InternalType> internalDependencies;

	private Violation(Type type, ImmutableList<InternalType> internalDependencies) {
		Objects.requireNonNull(type, "The argument 'type' must not be null.");
		Objects.requireNonNull(
				internalDependencies, "The argument 'internalDependencies' must not be null.");
		if (internalDependencies.size() == 0)
			throw new IllegalArgumentException(
					"A violation must contain at least one internal dependency.");

		this.type = type;
		this.internalDependencies = internalDependencies;
	}

	/**
	 * Starts building a new violation.
	 *
	 * @param type
	 *            the type which contains the violating dependency
	 * @return a {@link ViolationBuilder}
	 */
	public static ViolationBuilder forType(Type type) {
		return new ViolationBuilder(type);
	}

	/**
	 * @return the type which contains the dependencies on internal types
	 */
	public Type getType() {
		return type;
	}

	/**
	 * @return the internal types upon which {@link #getType()} depends
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
		return Objects.equals(type, other.type)
				&& Objects.equals(internalDependencies, other.internalDependencies);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Objects.hashCode(type);
		result = prime * result + Objects.hashCode(internalDependencies);
		return result;
	}

	@Override
	public String toString() {
		String dependencies = internalDependencies
				.stream()
				.map(Object::toString)
				.collect(joining(", ", "{", "}"));
		return type + " -> " + dependencies;
	}

	/**
	 * @return a string representation of a violation which spans multiple lines
	 */
	public String toMultiLineString() {
		String typeLine = ".\t" + type + "\n";
		String dependencyLineStart = ".\t\t -> ";
		return typeLine
				+ internalDependencies.stream()
						.map(Object::toString)
						.collect(joining("\n" + dependencyLineStart, dependencyLineStart, ""));
	}

	// #end EQUALS, HASHCODE, TOSTRING

	// #begin BUILDER

	/**
	 * Allows to build a {@link Violation} (which is immutable) by successively adding dependecies.
	 */
	public static class ViolationBuilder {

		private final Type type;

		private final ImmutableList.Builder<InternalType> internalDependenciesBuilder;

		private ViolationBuilder(Type type) {
			this.type = type;
			this.internalDependenciesBuilder = ImmutableList.builder();
		}

		/**
		 * Adds the specified {@link InternalType} as a dependency.
		 *
		 * @param dependency
		 *            an internal type
		 */
		public void addDependency(InternalType dependency) {
			internalDependenciesBuilder.add(dependency);
		}

		/**
		 * @return a new {@link Violation}
		 */
		public Violation build() {
			return new Violation(type, internalDependenciesBuilder.build());
		}

	}

	// #end BUILDER

}
