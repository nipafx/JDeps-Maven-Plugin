package org.codefx.maven.plugin.jdeps.rules;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link TypeNameHierarchy}.
 */
public class TypeNameHierarchyTest {

	@Test(expected = NullPointerException.class)
	public void forFullyQualifiedName_nameNull_throwsException() {
		TypeNameHierarchy.forFullyQualifiedName(null, PackageInclusion.HIERARCHICAL);
	}

	@Test(expected = NullPointerException.class)
	public void forFullyQualifiedName_packageInclusionNull_throwsException() {
		TypeNameHierarchy.forFullyQualifiedName("some.Class", null);
	}

	// #begin HIERARCHICAL PACKAGE INCLUSION

	@Test
	public void iterateHierarchical_oneLevelType_correctNames() {
		TypeNameHierarchy hierarchy = TypeNameHierarchy
				.forFullyQualifiedName("some.Class", PackageInclusion.HIERARCHICAL);
		assertThat(hierarchy).containsExactly("some.Class", "some");
	}

	@Test
	public void iterateHierarchical_twoLevelType_correctNames() {
		TypeNameHierarchy hierarchy = TypeNameHierarchy
				.forFullyQualifiedName("some.deep.Class", PackageInclusion.HIERARCHICAL);
		assertThat(hierarchy).containsExactly("some.deep.Class", "some.deep", "some");
	}

	@Test
	public void iterateHierarchical_threeLevelType_correctNames() {
		TypeNameHierarchy hierarchy = TypeNameHierarchy
				.forFullyQualifiedName("some.very.deep.Class", PackageInclusion.HIERARCHICAL);
		assertThat(hierarchy).containsExactly("some.very.deep.Class", "some.very.deep", "some.very", "some");
	}

	@Test
	public void iterateHierarchical_threeLevelInnerType_correctNames() {
		TypeNameHierarchy hierarchy = TypeNameHierarchy
				.forFullyQualifiedName("some.very.deep.Inner.Class", PackageInclusion.HIERARCHICAL);
		assertThat(hierarchy).containsExactly(
				"some.very.deep.Inner.Class", "some.very.deep.Inner", "some.very.deep", "some.very", "some");
	}

	// #begin HIERARCHICAL PACKAGE INCLUSION

	// #begin FLAT PACKAGE INCLUSION

	// edge cases

	@Test
	public void iterateFlat_noPackagePrefix_correctNames() {
		TypeNameHierarchy hierarchy = TypeNameHierarchy
				.forFullyQualifiedName("TopLevel.Class", PackageInclusion.FLAT);
		assertThat(hierarchy).containsExactly("TopLevel.Class", "TopLevel");
	}

	// regular cases

	@Test
	public void iterateFlat_oneLevelType_correctNames() {
		TypeNameHierarchy hierarchy = TypeNameHierarchy
				.forFullyQualifiedName("some.Class", PackageInclusion.FLAT);
		assertThat(hierarchy).containsExactly("some.Class", "some");
	}

	@Test
	public void iterateFlat_twoLevelType_correctNames() {
		TypeNameHierarchy hierarchy = TypeNameHierarchy
				.forFullyQualifiedName("some.deep.Class", PackageInclusion.FLAT);
		assertThat(hierarchy).containsExactly("some.deep.Class", "some.deep");
	}

	@Test
	public void iterateFlat_threeLevelType_correctNames() {
		TypeNameHierarchy hierarchy = TypeNameHierarchy
				.forFullyQualifiedName("some.very.deep.Class", PackageInclusion.FLAT);
		assertThat(hierarchy).containsExactly("some.very.deep.Class", "some.very.deep");
	}

	@Test
	public void iterateFlat_threeLevelInnerType_correctNames() {
		TypeNameHierarchy hierarchy = TypeNameHierarchy
				.forFullyQualifiedName("some.very.deep.Inner.Class", PackageInclusion.FLAT);
		assertThat(hierarchy).containsExactly(
				"some.very.deep.Inner.Class", "some.very.deep.Inner", "some.very.deep");
	}

	// #end FLAT PACKAGE INCLUSION
}
