
uptodate-gradle-plugin
======================

### Current build status
version 0.0.1 (2014/05/14)
[![Build Status](https://travis-ci.org/4finance/uptodate-gradle-plugin.svg?branch=master)](https://travis-ci.org/4finance/uptodate-gradle-plugin)


Gradle plugin that tells you what libs have new versions on Maven Central

### Add dependency to jcenter and to the plugin
```
buildscript {
    repositories {	
	      jcenter()
    }
    dependencies {
        classpath("com.ofg:uptodate-gradle-plugin:0.0.1")
    }
}
```

### Running the plugin
```
gradle uptodate
```

Example of output:
```
:clean
:addHashFile
:compileJava
:compileGroovy
:processResources
:classes
:jar
:bootRepackage
:assemble
:compileTestJava
:compileTestGroovy
:processTestResources
:testClasses
:test
:check
:uptodate
New versions available in maven central:
'com.fasterxml.jackson.core:jackson-databind:2.4.0-rc3'
'org.jacoco:org.jacoco.agent:0.7.1.201405082137'
:build
```

### Configuring plugin in build.gradle

```
uptodate {
    mavenRepo = "address to your custom repo (defaults to maven central repo)"
}
```

### Attaching uptodate plugin to your build
```
build.dependsOn 'uptodate'
```
