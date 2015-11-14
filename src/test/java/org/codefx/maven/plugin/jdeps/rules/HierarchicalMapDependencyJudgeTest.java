package org.codefx.maven.plugin.jdeps.rules;

import org.codefx.maven.plugin.jdeps.rules.HierarchicalMapDependencyJudge.HierarchicalMapDependencyJudgeBuilder;

/**
 * Tests for {@link HierarchicalMapDependencyJudge}.
 */
public class HierarchicalMapDependencyJudgeTest extends AbstractHierarchicalDependencyJudgeTest {

	@Override
	protected DependencyJudgeBuilder builder() {
		return new HierarchicalMapDependencyJudgeBuilder();
	}

}
