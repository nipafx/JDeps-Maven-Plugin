package org.codefx.maven.plugin.jdeps.mojo;

import static java.lang.String.format;

/**
 * Enumerates the ways in which rules can be output.
 */
enum RuleOutputFormat {

	XML,
	ARROW,
	ON;

	/**
	 * @return the {@link Arrow} for this format, if one is associated with it
	 */
	public Arrow asArrow() {
		switch (this) {
			case ARROW:
				return Arrow.ARROW;
			case ON:
				return Arrow.ON;
			case XML:
				throw new IllegalArgumentException(format("'%s' is no text format.", this));
			default:
				throw new IllegalArgumentException(format("Unknown format '%s'.", this));
		}
	}

}
