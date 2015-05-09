package org.codefx.maven.plugin.jdeps.find;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

import org.apache.commons.lang3.SystemUtils;

/**
 * Tries to locate JDeps inside the JDK folder.
 * <p>
 * This class does not implement {@link JDepsSearch} because it can not function on its own and requires input (namely
 * the JDK folder).
 */
public class SearchJDepsInJdk {

	/**
	 * @param jdkHome
	 *            the path to the JDK in which JDeps will be searched for; does not have to be a valid directory (i.e.
	 *            could be a non-existent path or a file)
	 * @return the path to JDeps if it could be found (i.e. a file with the correct name exists); otherwise an empty
	 *         {@link Optional}
	 */
	public Optional<Path> search(Path jdkHome) {
		Path jdkBin = jdkHome.resolve("bin");
		Path jdeps = jdkBin.resolve(jdepsFileName());

		return Files.isRegularFile(jdeps)
				? Optional.of(jdeps)
				: Optional.empty();
	}

	private static String jdepsFileName() {
		String jdepsExecutable = "jdeps" + (SystemUtils.IS_OS_WINDOWS ? ".exe" : "");
		return jdepsExecutable;
	}

}
