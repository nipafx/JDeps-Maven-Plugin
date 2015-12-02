package org.codefx.mvn.jdeps.parse;

import org.codefx.mvn.jdeps.dependency.InternalType;
import org.junit.Before;
import org.junit.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Tests the class {@link InternalTypeLineParser}.
 */
public class InternalTypeLineParserTest {

	private InternalTypeLineParser parser;

	@Before
	public void setup() {
		parser = new InternalTypeLineParser();
	}

	// isInternalTypeLine

	@Test(expected = NullPointerException.class)
	public void isInternalTypeLine_nullLine_throwsException() throws Exception {
		parser.isInternalTypeLine(null);
	}

	@Test
	public void isInternalTypeLine_emptyLine_returnsFalse() throws Exception {
		boolean isInternalTypeLine = parser.isInternalTypeLine("");

		assertFalse(isInternalTypeLine);
	}

	@Test
	public void isInternalTypeLine_matchingLine$1_returnsTrue() throws Exception {
		String example = "      -> sun.misc.BASE64Decoder                             JDK internal API (rt.jar)";
		boolean isInternalTypeLine = parser.isInternalTypeLine(example);

		assertTrue(isInternalTypeLine);
	}

    @Test
    public void isInternalTypeLine_matchingLine$2_returnsTrue() throws Exception {
        String example = "      -> sun.misc.Unsafe                             JDK internal API (rt.jar)";
        boolean isInternalTypeLine = parser.isInternalTypeLine(example);

        assertTrue(isInternalTypeLine);
    }

    @Test
    public void isInternalTypeLine_matchingLineStartingWithCapitalLetters_returnsTrue() throws Exception {
        String example = "      -> Sun.misc.Unsafe                             JDK internal API (rt.jar)";
        boolean isInternalTypeLine = parser.isInternalTypeLine(example);

        assertTrue(isInternalTypeLine);
    }

    // parseLine

	@Test(expected = NullPointerException.class)
	public void parseLine_nullLine_throwsException() throws Exception {
		parser.parseLine(null);
	}

	@Test
	public void parseLine_emptyLine_returnsFalse() throws Exception {
		Optional<InternalType> type = parser.parseLine("");

		assertFalse(type.isPresent());
	}

	@Test
	public void parseLine_matchingLine$1_returnsTrue() throws Exception {
		String example = "      -> sun.misc.BASE64Decoder                             JDK internal API (rt.jar)";
		Optional<InternalType> type = parser.parseLine(example);

		assertThat(type)
				.isPresent()
				.contains(InternalType.of("sun.misc", "BASE64Decoder", "JDK internal API", "rt.jar"));
	}

    @Test
    public void parseLine_matchingLine$2_returnsTrue() throws Exception {
        String example = "      -> sun.misc.Unsafe                             JDK internal API (rt.jar)";
        Optional<InternalType> type = parser.parseLine(example);

        assertThat(type)
                .isPresent()
                .contains(InternalType.of("sun.misc", "Unsafe", "JDK internal API", "rt.jar"));
    }

    @Test
    public void parseLine_matchingLineStartingWithCapitalLetters_returnsTrue() throws Exception {
        String example = "      -> Sun.misc.Unsafe                             JDK internal API (rt.jar)";
        Optional<InternalType> type = parser.parseLine(example);

        assertThat(type)
                .isPresent()
                .contains(InternalType.of("Sun.misc", "Unsafe", "JDK internal API", "rt.jar"));
    }

}
