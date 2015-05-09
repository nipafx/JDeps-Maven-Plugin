package org.codefx.maven.plugin.jdeps.search;

import java.nio.file.Path;
import java.util.Optional;

import org.apache.commons.lang3.SystemUtils;

/**
 * Tries to locate jdeps via the system property "java.home".
 */
final class JavaHomeSystemPropertyJDepsSearch implements JDepsSearch {

	private final SearchJDepsInJdk searchJDepsInJdk;

	/**
	 * Creates a new search.
	 */
	public JavaHomeSystemPropertyJDepsSearch() {
		this(new SearchJDepsInJdk());
	}

	/**
	 * Creates a new search which uses the specified service to locate JDeps in the JDK folder.
	 * 
	 * @param searchJDepsInJdk
	 *            used to locate JDeps in the JDK folder
	 */
	public JavaHomeSystemPropertyJDepsSearch(SearchJDepsInJdk searchJDepsInJdk) {
		this.searchJDepsInJdk = searchJDepsInJdk;
	}

	@Override
	public Optional<Path> search() {
		// "java.home" points to "jdk/jre" and jdeps can be found in "jdk/bin" (if this is run with a JDK)
		Path javaHome = SystemUtils.getJavaHome().toPath();
		Path jdkHome = javaHome.getParent();

		return searchJDepsInJdk.search(jdkHome);
	}

}
