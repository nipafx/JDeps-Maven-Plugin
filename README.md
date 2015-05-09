# JDeps Maven Plugin

This Maven plugin employs [JDeps](https://docs.oracle.com/javase/8/docs/technotes/tools/unix/jdeps.html) to fail a build when dependencies on [JDK-internal APIs](https://wiki.openjdk.java.net/display/JDK8/Java+Dependency+Analysis+Tool) are discovered.

With Java 9 ([scheduled for 09/2016](http://mail.openjdk.java.net/pipermail/jdk9-dev/2015-May/002172.html)) such APIs [will become unavailable](http://blog.codefx.org/java/dev/how-java-9-and-project-jigsaw-may-break-your-code/), which may break a project. This plugin can help to avoid that. Running `jdeps -jdkinternals` against the compiled classes it will discover dependencies on internal APIs and break the build if any are found. This makes it easier to transition to Java 9.

The next important step is to implement [*#1 Allow to Ignore Known Dependencies*](https://github.com/CodeFX-org/JDeps-Maven-Plugin/issues/1). It would allow to specify a list of known dependencies which will not break the build - thus enabling a step by step transition while preventing undetected relapses.

## Documentation

Add this to the project's `pom.xml`:

```xml
<groupId>org.codefx.maven.plugin</groupId>
<artifactId>jdeps-maven-plugin</artifactId>
<version>0.1</version>
<executions>
	<execution>
		<goals>
			<goal>jdkinternals</goal>
		</goals>
	</execution>
</executions>
```

The plugin is fairly simple and currently neither requires nor allows any further configuration.

It will run during the `verify` [phase](https://maven.apache.org/guides/introduction/introduction-to-the-lifecycle.html), so it can be started with `mvn verify` (or above). It can be run directly with  `mvn jdeps:jdkinternals`.


## Other Such Plugins

There are (at least) two Maven plugins which allow to use JDeps.

* [Maven JDeps plugin by Philippe Marschall](https://github.com/marschall/jdeps-maven-plugin): currently only allows you to run jdeps without any consequences for the build
* [official Apache Maven JDeps plugin](http://maven.apache.org/plugins-archives/maven-jdeps-plugin-LATEST/maven-jdeps-plugin/): seems to be aimed at breaking the build when discovering internal dependencies but is still in development and does currently not work

(Keep in mind that the author's and the reader's "currently" do not point to the same date.)
