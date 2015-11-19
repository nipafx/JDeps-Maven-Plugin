package org.codefx.maven.plugin.jdeps.mojo;

import java.util.Arrays;

import static java.util.stream.Collectors.joining;

/**
 * The different visualisations of an arrow in arrow rules like {@code dependent -> dependency: severity}.
 */
public enum Arrow {

	ARROW("->"),
	ON("on");

	public static final String REGULAR_EXPRESSION_MATCHER = Arrays
			.stream(values())
			.map(Arrow::text)
			.collect(joining("|", "(", ")"));

	private final String text;

	Arrow(String text) {
		this.text = text;
	}

	public String text() {
		return text;
	}

}

