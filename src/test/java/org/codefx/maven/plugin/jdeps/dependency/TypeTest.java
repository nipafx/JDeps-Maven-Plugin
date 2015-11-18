package org.codefx.maven.plugin.jdeps.dependency;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link Type}.
 */
public class TypeTest {

	@Test(expected = NullPointerException.class)
	public void of_packageNameNull_throwsException() {
		Type.of(null, "Class");
	}

	@Test(expected = IllegalArgumentException.class)
	public void of_packageNameEmpty_throwsException() {
		Type.of("", "Class");
	}

	@Test(expected = NullPointerException.class)
	public void of_classNameNull_throwsException() {
		Type.of("java.lang", null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void of_classNameEmpty_throwsException() {
		Type.of("java.lang", "");
	}

	@Test(expected = NullPointerException.class)
	public void of_fullNameNull_throwsException() {
		Type.of(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void of_fullNameEmpty_throwsException() {
		Type.of("");
	}

	@Test(expected = IllegalArgumentException.class)
	public void of_fullNameWithoutDot_throwsException() {
		Type.of("ClassName");
	}

	@Test
	public void getPackageName_createdFromPackageAndClassName_returnsCorrectName() {
		Type type = Type.of("java.lang", "Class");
		assertThat(type.getPackageName()).isEqualTo("java.lang");
	}

	@Test
	public void getClassName_createdFromPackageAndClassName_returnsCorrectName() {
		Type type = Type.of("java.lang", "Class");
		assertThat(type.getClassName()).isEqualTo("Class");
	}

	@Test
	public void getFullName_createdFromPackageAndClassName_returnsCorrectName() {
		Type type = Type.of("java.lang", "Class");
		assertThat(type.getFullyQualifiedName()).isEqualTo("java.lang.Class");
	}

	@Test
	public void getPackageName_createdFromFullName_returnsCorrectName() {
		Type type = Type.of("java.lang.Class");
		assertThat(type.getPackageName()).isEqualTo("java.lang");
	}

	@Test
	public void getClassName_createdFromFullName_returnsCorrectName() {
		Type type = Type.of("java.lang.Class");
		assertThat(type.getClassName()).isEqualTo("Class");
	}

	@Test
	public void getFullName_createdFromFullName_returnsCorrectName() {
		Type type = Type.of("java.lang.Class");
		assertThat(type.getFullyQualifiedName()).isEqualTo("java.lang.Class");
	}

	@Test
	public void compareTo_differentPackageNames_orderedByPackageName() throws Exception {
		// note how if the types were sorted by their simple name, 'Optional' would be smaller than 'String'
		Type smaller = Type.of("java.lang", "String");
		Type greater = Type.of("java.util", "Optional");

		assertThat(smaller.compareTo(greater)).isNegative();
		assertThat(greater.compareTo(smaller)).isPositive();
	}

	@Test
	public void compareTo_samePackageButDifferentClassNames_orderedByClassName() throws Exception {
		Type smaller = Type.of("java.lang", "Object");
		Type greater = Type.of("java.lang", "String");

		assertThat(smaller.compareTo(greater)).isNegative();
		assertThat(greater.compareTo(smaller)).isPositive();
	}

	@Test
	public void compareTo_sameFullyQualifiedName_orderedSame() throws Exception {
		Type one = Type.of("java.lang", "Object");
		Type other = Type.of("java.lang", "Object");

		assertThat(one.compareTo(other)).isZero();
		assertThat(other.compareTo(one)).isZero();
	}

}
