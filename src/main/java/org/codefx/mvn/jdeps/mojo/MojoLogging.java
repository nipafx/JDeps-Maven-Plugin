package org.codefx.mvn.jdeps.mojo;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugin.logging.SystemStreamLog;

import java.util.Optional;
import java.util.function.Supplier;

/**
 * Evil global-state that makes the {@link JdkInternalsMojo mojo}'s logger available to all the plugin's classes.
 * <p>
 * This class manages a function that can return a logger because {@link AbstractMojo#getLog()} (the method that will
 * likely be used to access the logger) states: "simply call this method directly whenever you need the logger".
 */
public class MojoLogging {

	private static final Log fallbackSystemStreamLog = new SystemStreamLog();

	private static Optional<Supplier<Log>> getLogger = Optional.empty();

	/**
	 * @return the currently registered logger
	 */
	public static Log logger() {
		return getLogger
				.orElse(() -> fallbackSystemStreamLog)
				.get();
	}

	/**
	 * Registers the specified function to access the logger from here on.
	 *
	 * @param getLogger
	 * 		a supplier for the logger
	 */
	static void registerLogger(Supplier<Log> getLogger) {
		MojoLogging.getLogger = Optional.of(getLogger);
	}

	/**
	 * Unregisters the last registered logger.
	 * <p>
	 * Until another logger is {@link #registerLogger(Supplier) registered}, a {@link SystemStreamLog} will be used.
	 */
	static void unregisterLogger() {
		getLogger = Optional.empty();
	}

}
