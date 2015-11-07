package org.codefx.maven.plugin.jdeps.result;

import com.google.common.collect.ImmutableList;
import org.codefx.maven.plugin.jdeps.dependency.Violation;

public class ResultBuilder {

	private final ImmutableList.Builder<Violation> violations = ImmutableList.builder();

	public void addViolation(Violation violation) {
		violations.add(violation);
	}

	public Result build() {
		return new Result(violations.build());
	}

}
