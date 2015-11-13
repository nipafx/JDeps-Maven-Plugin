package org.codefx.maven.plugin.jdeps.parse;

import org.codefx.maven.plugin.jdeps.dependency.InternalType;
import org.codefx.maven.plugin.jdeps.dependency.Type;
import org.codefx.maven.plugin.jdeps.dependency.Violation;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;

/**
 * Tests the class {@link ViolationParser}.
 */
public class ViolationParserTest {

	private ViolationParser parser;

	private Consumer<Violation> violationVerifier;

	@Before
	public void setup() {
		violationVerifier = mock(Consumer.class);
		parser = new ViolationParser(violationVerifier);
	}

	// isInternalTypeLine

	@Test(expected = NullPointerException.class)
	public void parseLine_nullLine_throwsNullPointerException() throws Exception {
		parser.parseLine(null);
	}

    @Test
    public void isViolation_emptyLine_createsNoViolation() throws Exception {
        parser.parseLine("");

        verifyZeroInteractions(violationVerifier);
    }

    @Test(expected =  IllegalStateException.class)
    public void isViolation_onlyDependent_throwsIllegalStateException() throws Exception {
        String onlyDependent = "   org.codefx.lab.App (target)\n";

        parseBlock(parser, onlyDependent);
    }

    @Test
	public void parseLine_matchingBlock$1_createsViolation() throws Exception {
		String matchingBlock = ""
				+ "   org.codefx.lab.App (target)\n"
				+ "	      -> sun.misc.BASE64Decoder                             JDK internal API (rt.jar)\n"
				+ "	      -> sun.misc.Unsafe                                    JDK internal API (rt.jar)\n";
		ArgumentCaptor<Violation> violationCaptor = ArgumentCaptor.forClass(Violation.class);

		parseBlock(parser, matchingBlock);

		verify(violationVerifier).accept(violationCaptor.capture());
		Violation violation = violationCaptor.getValue();
		assertThat(violation.getDependent()).isEqualTo(Type.of("org.codefx.lab", "App"));
		assertThat(violation.getInternalDependencies())
				.hasSize(2)
				.contains(InternalType.of("sun.misc", "BASE64Decoder", "JDK internal API", "rt.jar"))
				.contains(InternalType.of("sun.misc", "Unsafe", "JDK internal API", "rt.jar"));
	}

    @Test
    public void parseLine_matchingBlockWithCapitalLettersInDependendent_createsViolation() throws Exception {
        String matchingBlockWithCapitalLettersInDependent = ""
                + "   Org.codefx.lAb.App (target)\n"
                + "	      -> sun.misc.BASE64Decoder                             JDK internal API (rt.jar)\n"
                + "	      -> sun.misc.Unsafe                                    JDK internal API (rt.jar)\n";
        ArgumentCaptor<Violation> violationCaptor = ArgumentCaptor.forClass(Violation.class);

        parseBlock(parser, matchingBlockWithCapitalLettersInDependent);

        verify(violationVerifier).accept(violationCaptor.capture());
        Violation violation = violationCaptor.getValue();
        assertThat(violation.getDependent()).isEqualTo(Type.of("Org.codefx.lAb", "App"));
    }

    @Test
    public void parseLine_matchingBlockWithCapitalLettersInDependencies_createsViolation() throws Exception {
        String matchingBlockWithCapitalLettersInDependencies = ""
                + "   org.codefx.lab.App (target)\n"
                + "	      -> Sun.misc.BASE64Decoder                             JDK internal API (rt.jar)\n"
                + "	      -> sun.miSc.Unsafe                                    JDK internal API (rt.jar)\n";
        ArgumentCaptor<Violation> violationCaptor = ArgumentCaptor.forClass(Violation.class);

        parseBlock(parser, matchingBlockWithCapitalLettersInDependencies);

        verify(violationVerifier).accept(violationCaptor.capture());
        Violation violation = violationCaptor.getValue();
        assertThat(violation.getInternalDependencies())
                .hasSize(2)
                .contains(InternalType.of("Sun.misc", "BASE64Decoder", "JDK internal API", "rt.jar"))
                .contains(InternalType.of("sun.miSc", "Unsafe", "JDK internal API", "rt.jar"));
    }

    private static void parseBlock(ViolationParser parser, String block) {
		new BufferedReader(new StringReader(block))
				.lines()
				.forEach(parser::parseLine);
		parser.finish();
	}

}
