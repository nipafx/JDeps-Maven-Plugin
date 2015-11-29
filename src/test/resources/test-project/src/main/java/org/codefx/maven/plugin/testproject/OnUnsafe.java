package org.codefx.maven.plugin.testproject;

import sun.misc.Unsafe;

public class OnUnsafe {

	public static void main(String[] args) {
		Unsafe unsafe = null;
		unsafe.addressSize();
	}

}
