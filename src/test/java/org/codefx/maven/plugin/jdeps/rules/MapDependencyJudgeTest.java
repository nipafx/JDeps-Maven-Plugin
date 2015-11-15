package org.codefx.maven.plugin.jdeps.rules;

import org.codefx.maven.plugin.jdeps.rules.MapDependencyJudge.MapDependencyJudgeBuilder;
import org.codefx.maven.plugin.jdeps.rules.MapDependencyJudgeTest.AsFlat;
import org.codefx.maven.plugin.jdeps.rules.MapDependencyJudgeTest.AsHierarchical;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

/**
 * Tests for {@link MapDependencyJudge}.
 */
@RunWith(Suite.class)
@SuiteClasses({AsFlat.class, AsHierarchical.class})
public class MapDependencyJudgeTest {

	public static class AsFlat extends AbstractFlatDependencyJudgeTest {

		@Override
		protected DependencyJudgeBuilder builder() {
			return new MapDependencyJudgeBuilder().withInclusion(PackageInclusion.FLAT);
		}

	}

	public static class AsHierarchical extends AbstractHierarchicalDependencyJudgeTest {

		@Override
		protected DependencyJudgeBuilder builder() {
			return new MapDependencyJudgeBuilder().withInclusion(PackageInclusion.HIERARCHICAL);
		}

	}

}
