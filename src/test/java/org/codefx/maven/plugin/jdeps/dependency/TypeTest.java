package org.codefx.maven.plugin.jdeps.dependency;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

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

}
