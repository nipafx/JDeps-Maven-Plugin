package org.codefx.mvn.jdeps.result;

import org.apache.maven.plugin.MojoFailureException;
import org.codefx.mvn.jdeps.dependency.Violation;
import org.codefx.mvn.jdeps.tool.PairCollector.Pair;

import java.util.stream.Stream;

import static java.lang.String.format;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.reducing;
import static java.util.stream.Collectors.summingInt;
import static org.codefx.mvn.jdeps.tool.PairCollector.pairing;

/**
 * A {@link ResultOutputStrategy} that fails the build if the result contains violations that are configured to do so.
 */
public class FailBuildResultOutputStrategy implements ResultOutputStrategy {

	static final String MESSAGE_FAIL_DEPENDENCIES =
			LogResultOutputStrategy.MESSAGE_ABOUT_JDEPS + "\nConfigured to FAIL are %1$s:\n%2$s";

	@Override
	public void output(Result result) throws MojoFailureException {
		Pair<Integer, Stream<String>> countAndMessage = result
				.violationsToFail()
				.collect(pairing(
						summingInt(violation -> violation.getInternalDependencies().size()),
						reducing(Stream.of(), Violation::toLines, Stream::concat)));

		if (countAndMessage.first > 0)
			throw new MojoFailureException(format(
					MESSAGE_FAIL_DEPENDENCIES,
					countAndMessage.first,
					// whitespace at the lines' beginnings are apparently removed by Maven so prefix with a dot
					countAndMessage.second.map(line -> "." + line).collect(joining("\n"))));
	}

}
