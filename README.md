# JDeps Maven Plugin

This plugin aims at including [JDeps](https://docs.oracle.com/javase/8/docs/technotes/tools/unix/jdeps.html) in a Maven build. The primary goal is to break the build if a dependency on [JDK-internal API](https://wiki.openjdk.java.net/display/JDK8/Java+Dependency+Analysis+Tool) is discovered.

The secondary goal is to teach myself how to create a Maven plugin. So this is very much a work in progress... 

## Other Such Plugins

There are (at least) two Maven plugins which allow to use JDeps.

* [Maven JDeps plugin by Philippe Marschall](https://github.com/marschall/jdeps-maven-plugin): currently only allows you to run jdeps without any consequences for the build
* [official Apache Maven JDeps plugin](http://maven.apache.org/plugins-archives/maven-jdeps-plugin-LATEST/maven-jdeps-plugin/): seems to be aimed at breaking the build when discovering internal dependencies but is still in development and does currently not work

(Keep in mind that the author's and the reader's "currently" do not point to the same date.)
