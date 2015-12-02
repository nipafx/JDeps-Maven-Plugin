# JDeps Mvn

###### The Only Maven Plugin That Understands JDeps

---

This plugin is all about [JDeps](https://docs.oracle.com/javase/8/docs/technotes/tools/unix/jdeps.html) and particularly about [JDK-internal APIs](https://wiki.openjdk.java.net/display/JDK8/Java+Dependency+Analysis+Tool).
With Java 9 ([scheduled for 03/2017](http://mail.openjdk.java.net/pipermail/jdk9-dev/2015-December/003149.html)) such APIs [will become unavailable](http://blog.codefx.org/java/dev/how-java-9-and-project-jigsaw-may-break-your-code/), which may break a project.
Redemption comes in four easy steps:

1. identify your projects' problematic dependencies
2. create a plan to move away from them
3. prevent relapses
4. do the same for your dependencies

This plugin helps with :one: and :three:, :two: shouldn't be too hard but :four: might be.

Running `jdeps -jdkinternals` against the compiled classes it will **discover dependencies** on internal APIs.
But it not only runs JDeps, it also understands its output, which allows a **convenient and detailed configuration** of when exactly the **build should break**.
This enables a **self-paced migration** away from problematic dependencies with immediate **failure on relapses**.

## Quick Start

To get a first impression how you're doing JDK-internal-wise simply run _JDeps Mvn_:

```bash
mvn clean compile org.codefx.mvn:jdeps-maven-plugin:jdkinternals
```

This will log every dependency _jdeps_ reports to the console (on level `WARN`).

Adding the following to your pom yields the same result as above but on every run:

```xml
<plugin>
	<groupId>org.codefx.mvn</groupId>
	<artifactId>jdeps-maven-plugin</artifactId>
	<version>0.2</version>
	<executions>
		<execution>
			<configuration>
				<!-- define known and acceptable dependencies here (see below) -->
			</configuration>
			<goals>
				<goal>jdkinternals</goal>
			</goals>
		</execution>
	</executions>
</plugin>
```

To explicitly run this plugin you can do one of these:

```bash
mvn clean compile jdeps:jdkinternals # just this plugin
mvn verify # everything up to the phase in which this plugin runs
```

## More Documentation :anger:

But we're all too good in ignoring log messages so check out the [walkthrough](https://github.com/CodeFX-org/JDeps-Maven-Plugin/wiki/Walkthrough) and the rest of the [wiki](https://github.com/CodeFX-org/JDeps-Maven-Plugin/wiki) for information on how to advance to the next level.

## Contact

Nicolai Parlog <br>
CodeFX

Web: http://codefx.org <br>
Twitter: https://twitter.com/nipafx<br>
Mail: nipa@codefx.org <br>
PGP-Key: http://keys.gnupg.net/pks/lookup?op=vindex&search=0xA47A795BA5BF8326 <br>
