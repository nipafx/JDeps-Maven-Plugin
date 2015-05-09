package org.codefx.maven.plugin.jdeps.search;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;

/**
 * Tries to locate jdeps via the environment variable "JAVA_HOME".
 */
class JavaHomeEnvironmentVariableJDepsSearch implements JDepsSearch {

	private final SearchJDepsInJdk searchJDepsInJdk;

	/**
	 * Creates a new search.
	 */
	public JavaHomeEnvironmentVariableJDepsSearch() {
		this(new SearchJDepsInJdk());
	}

	/**
	 * Creates a new search which uses the specified service to locate JDeps in the JDK folder.
	 *
	 * @param searchJDepsInJdk
	 *            used to locate JDeps in the JDK folder
	 */
	public JavaHomeEnvironmentVariableJDepsSearch(SearchJDepsInJdk searchJDepsInJdk) {
		this.searchJDepsInJdk = searchJDepsInJdk;
	}

	@Override
	public Optional<Path> search() {
		Optional<Path> javaHome = getJavaHome();
		if (!javaHome.isPresent())
			return Optional.empty();

		// assume that "JAVA_HOME" points to a JDK (and not to a JRE);
		return searchJDepsInJdk.search(javaHome.get());
	}

	private static Optional<Path> getJavaHome() {
		try {
			String javaHome = System.getenv("JAVA_HOME");
			if (StringUtils.isEmpty(javaHome))
				return Optional.empty();
			return Optional.of(Paths.get(javaHome));
		} catch (SecurityException ex) {
			return Optional.empty();
		}
	}

}
