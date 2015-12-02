package org.codefx.mvn.jdeps.testproject;

import sun.misc.Unsafe;

public class OnUnsafe {

	public static void main(String[] args) {
		Unsafe unsafe = null;
		unsafe.addressSize();
	}

}
