package org.codefx.mvn.jdeps.result;

import org.apache.maven.plugin.MojoFailureException;
import org.codefx.mvn.jdeps.dependency.Violation;
import org.codefx.mvn.jdeps.tool.PairCollector;
import org.codefx.mvn.jdeps.tool.PairCollector.Pair;

import static java.lang.String.format;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.summingInt;

/**
 * A {@link ResultOutputStrategy} that fails the build if the result contains violations that are configured to do so.
 */
public class FailBuildResultOutputStrategy implements ResultOutputStrategy {

	@Override
	public void output(Result result) throws MojoFailureException {
		Pair<Integer, String> countAndMessage = result
				.violationsToFail()
				.collect(
						PairCollector.pairing(
								summingInt(violation -> violation.getInternalDependencies().size()),
								mapping(Violation::toMultiLineString, joining("\n"))));

		if (countAndMessage.first > 0)
			throw new MojoFailureException(format(
					LogResultOutputStrategy.MESSAGE_FAIL_DEPENDENCIES, countAndMessage.first, countAndMessage.second));
	}

}
