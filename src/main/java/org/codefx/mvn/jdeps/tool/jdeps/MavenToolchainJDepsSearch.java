package org.codefx.mvn.jdeps.tool.jdeps;

import org.apache.maven.toolchain.Toolchain;

import java.nio.file.Path;
import java.util.Objects;
import java.util.Optional;

/**
 * Tries to locate JDeps in the JDK specified to the Maven Toolchain.
 */
class MavenToolchainJDepsSearch implements JDepsSearch {

	private final Toolchain toolchain;

	/**
	 * Creates a new search using the specified toolchain.
	 * 
	 * @param toolchain
	 *            the toolchain which will be used to locate JDeps
	 */
	public MavenToolchainJDepsSearch(Toolchain toolchain) {
		Objects.requireNonNull(toolchain, "The argument 'toolchain' must not be null.");
		this.toolchain = toolchain;
	}

	@Override
	public Optional<Path> search() {
		return Optional.empty();
	}

}
