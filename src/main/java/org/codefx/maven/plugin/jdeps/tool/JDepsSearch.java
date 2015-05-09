package org.codefx.maven.plugin.jdeps.tool;

import java.nio.file.Path;
import java.util.Optional;

/**
 * Tries to locate the <a href="https://wiki.openjdk.java.net/display/JDK8/Java+Dependency+Analysis+Tool">Java
 * Dependency Analysis Tool (jdeps)</a>.
 */
public interface JDepsSearch {

	/**
	 * @return the path to the tool or an empty {@link Optional} if it was not found
	 */
	Optional<Path> search();

}
