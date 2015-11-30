package org.codefx.maven.plugin.jdeps.result;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

import static java.lang.String.format;
import static java.nio.file.StandardOpenOption.APPEND;
import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.WRITE;
import static java.util.Objects.requireNonNull;

/**
 * Appends dependency rules to a file.
 */
public class RuleWriter {

	private final Path outputFile;

	public RuleWriter(Path outputFile) {
		this.outputFile = requireNonNull(outputFile, "The argument 'outputFile' must not be null.");
	}

	public void write(Stream<String> lines) throws IOException {
		try (BufferedWriter writer = openFile()) {
			lines.forEachOrdered(line -> writeToFile(writer, line));
		} catch (IllegalStateException ex) {
			// 'IOException's are rethrown as 'IllegalStateExceptions'
			if (ex.getCause() instanceof IOException)
				throwWriteFailedException((IOException) ex.getCause());
		} catch (IOException ex) {
			throwWriteFailedException(ex);
		}
	}

	private BufferedWriter openFile() throws IOException {
		// create a new file or append the existing file; open with write access
		return Files.newBufferedWriter(outputFile, CREATE, APPEND, WRITE);
	}

	private static void writeToFile(BufferedWriter writer, String line) {
		try {
			writer.append(line);
			writer.newLine();
		} catch (IOException ex) {
			throw new IllegalStateException(ex);
		}
	}

	private void throwWriteFailedException(IOException ex) throws IOException {
		String message = format("Writing rules to '%s' failed.", outputFile);
		throw new IOException(message, ex);
	}

}
