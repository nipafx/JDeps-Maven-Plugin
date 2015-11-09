package org.codefx.maven.plugin.jdeps.rules;

import org.codefx.maven.plugin.jdeps.dependency.Type;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link TypeNameHierarchy}.
 */
public class TypeNameHierarchyTest {

	@Test(expected = NullPointerException.class)
	public void forType_typeNull_throwsNullPointerException() {
		TypeNameHierarchy.forType(null);
	}

	@Test
	public void iterate_oneLevelType_correctNames() {
		TypeNameHierarchy hierarchy = TypeNameHierarchy.forType(Type.of("some.Class"));
		assertThat(hierarchy).containsExactly("some.Class", "some");
	}

	@Test
	public void iterate_twoLevelType_correctNames() {
		TypeNameHierarchy hierarchy = TypeNameHierarchy.forType(Type.of("some.deep.Class"));
		assertThat(hierarchy).containsExactly("some.deep.Class", "some.deep", "some");
	}

	@Test
	public void iterate_threeLevelType_correctNames() {
		TypeNameHierarchy hierarchy = TypeNameHierarchy.forType(Type.of("some.very.deep.Class"));
		assertThat(hierarchy).containsExactly("some.very.deep.Class", "some.very.deep", "some.very", "some");
	}

}
