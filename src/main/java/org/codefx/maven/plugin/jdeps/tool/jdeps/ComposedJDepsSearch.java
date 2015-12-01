package org.codefx.maven.plugin.jdeps.tool.jdeps;

import java.nio.file.Path;
import java.util.Optional;

/**
 * Tries all available searches to locate JDeps.
 */
public class ComposedJDepsSearch implements JDepsSearch {

	private final JavaHomeSystemPropertyJDepsSearch systemPropertySearch;
	private final JavaHomeEnvironmentVariableJDepsSearch environmentVariableSearch;

	/**
	 * Creates a new search.
	 */
	public ComposedJDepsSearch() {
		systemPropertySearch = new JavaHomeSystemPropertyJDepsSearch();
		environmentVariableSearch = new JavaHomeEnvironmentVariableJDepsSearch();
	}

	@Override
	public Optional<Path> search() {
		Optional<Path> viaSystemProperty = systemPropertySearch.search();
		if (viaSystemProperty.isPresent())
			return viaSystemProperty;

		Optional<Path> viaEnvironmentVariable = environmentVariableSearch.search();
		if (viaEnvironmentVariable.isPresent())
			return viaEnvironmentVariable;

		return Optional.empty();
	}

}
