package org.codefx.maven.plugin.jdeps.mojo;

import org.assertj.core.api.Condition;
import org.codefx.maven.plugin.jdeps.rules.Severity;
import org.junit.Test;

import java.util.List;

import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;

public class XmlRuleTest {

	private XmlRule rule = new XmlRule("dependent", "depenendency", Severity.FAIL);

	@Test(expected = NullPointerException.class)
	public void toXmlLines_linePrefixNull_throwsException() {
		rule.toXmlLines(null, "");
	}

	@Test(expected = IllegalArgumentException.class)
	public void toXmlLines_linePrefixNotEmpty_throwsException() {
		rule.toXmlLines("x", "");
	}

	@Test(expected = NullPointerException.class)
	public void toXmlLines_indentNull_throwsException() {
		rule.toXmlLines("", null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void toXmlLines_indentNotEmpty_throwsException() {
		rule.toXmlLines("", "x");
	}

	@Test
	public void toXmlLines_validLinePrefix_linePrefixIsUsed() throws Exception {
		List<String> lines = rule.toXmlLines("\t", "").collect(toList());

		Condition<String> startingWithLinePrefix =
				new Condition<>(line -> line.startsWith("\t"), "Start with line prefix.");
		assertThat(lines).are(startingWithLinePrefix);
	}

	@Test
	public void toXmlLines_validIndent_indentIsUsed() throws Exception {
		List<String> lines = rule.toXmlLines("", "\t").collect(toList());

		Condition<String> startingWithIndentUnlessOuterTagLines = new Condition<>(
				line -> line.startsWith("\t") || line.equals("<xmlRule>") || line.equals("</xmlRule>"),
				"Start with indent.");
		assertThat(lines).are(startingWithIndentUnlessOuterTagLines);
	}

	@Test
	public void toXmlLines_validRule_containsAllInformation() throws Exception {
		XmlRule rule = new XmlRule("DEPENDENT", "DEPENDENCY", Severity.FAIL);
		List<String> lines = rule.toXmlLines("", "").collect(toList());

		assertThat(lines).containsExactly(
				"<xmlRule>",
				"<dependent>DEPENDENT</dependent>",
				"<dependency>DEPENDENCY</dependency>",
				"<severity>FAIL</severity>",
				"</xmlRule>");
	}

}
