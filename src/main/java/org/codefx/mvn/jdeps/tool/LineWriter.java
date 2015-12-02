package org.codefx.mvn.jdeps.tool;

import com.google.common.collect.ImmutableList;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.stream.Stream;

import static java.lang.String.format;
import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.WRITE;
import static java.util.Objects.requireNonNull;

/**
 * Writes a stream of lines to a file.
 */
public class LineWriter {

	private final Path outputFile;
	private final IfFileExists ifFileExists;
	private final StaticContent staticContent;

	/**
	 * Creates a new writer.
	 *
	 * @param outputFile
	 * 		the file to write to
	 * @param ifFileExists
	 * 		what to do if the file already exists
	 * @param staticContent
	 * 		the content to wrap the written lines in
	 */
	public LineWriter(Path outputFile, IfFileExists ifFileExists, StaticContent staticContent) {
		this.outputFile = requireNonNull(outputFile, "The argument 'outputFile' must not be null.");
		this.ifFileExists = requireNonNull(ifFileExists, "The argument 'ifFileExists' must not be null.");
		this.staticContent = requireNonNull(staticContent, "The argument 'staticContent' must not be null.");
	}

	/**
	 * Writes the specified lines to the file.
	 *
	 * @param lines
	 * 		the lines to write
	 *
	 * @throws IOException
	 * 		if writing fails
	 */
	public void write(Stream<String> lines) throws IOException {
		try (BufferedWriter writer = openFile()) {
			staticContent.prolog.forEach(line -> writeToFile(writer, line));
			lines.forEachOrdered(line -> writeToFile(writer, staticContent.indent + line));
			staticContent.epilog.forEach(line -> writeToFile(writer, line));
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
		return Files.newBufferedWriter(outputFile, CREATE, ifFileExists.openOption(), WRITE);
	}

	private void writeToFile(BufferedWriter writer, String line) {
		try {
			writer.append(line);
			writer.newLine();
		} catch (IOException ex) {
			throw new IllegalStateException(ex);
		}
	}

	private void throwWriteFailedException(IOException ex) throws IOException {
		String message = format("Writing to '%s' failed.", outputFile);
		throw new IOException(message, ex);
	}

	/**
	 * Defines some static content the writer will write to the file.
	 */
	public static class StaticContent {

		public final ImmutableList<String> prolog;
		public final ImmutableList<String> epilog;
		public final String indent;

		/**
		 * @param prolog
		 * 		the written lines must start with these lines
		 * @param epilog
		 * 		the written lines must end with these lines
		 * @param indent
		 * 		this indent is to be added before each line from the stream; it must only consist of whitespace
		 */
		public StaticContent(ImmutableList<String> prolog, ImmutableList<String> epilog, String indent) {
			this.prolog = requireNonNull(prolog, "The argument 'prolog' must not be null.");
			this.epilog = requireNonNull(epilog, "The argument 'epilog' must not be null.");
			this.indent = requireNonNull(indent, "The argument 'indent' must not be null.");
			if (!indent.trim().isEmpty())
				throw new IllegalArgumentException("The argument 'indent' must only consist of whitespace.");
		}
	}

	/**
	 * Defines the writers behavior if the file already exists.
	 */
	public enum IfFileExists {

		/**
		 * Remove all existing content.
		 */
		REMOVE_EXISTING_CONTENT,

		/**
		 * Append the new content.
		 */
		APPEND_NEW_CONTENT;

		public StandardOpenOption openOption() {
			switch (this) {
				case REMOVE_EXISTING_CONTENT:
					return StandardOpenOption.TRUNCATE_EXISTING;
				case APPEND_NEW_CONTENT:
					return StandardOpenOption.APPEND;
				default:
					throw new IllegalArgumentException(format("Unknown IfFileExists.%s.", this));
			}
		}
	}

}
