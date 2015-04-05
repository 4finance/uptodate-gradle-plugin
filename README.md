[![Build Status](https://travis-ci.org/4finance/uptodate-gradle-plugin.svg?branch=master)](https://travis-ci.org/4finance/uptodate-gradle-plugin) [![Coverage Status](http://img.shields.io/coveralls/4finance/uptodate-gradle-plugin/master.svg)](https://coveralls.io/r/4finance/uptodate-gradle-plugin)
[ ![Download](https://api.bintray.com/packages/4finance/uptodate-gradle-plugin/uptodate-gradle-plugin/images/download.svg) ](https://bintray.com/4finance/uptodate-gradle-plugin/uptodate-gradle-plugin/_latestVersion)
[![Gitter](https://badges.gitter.im/Join%20Chat.svg)](https://gitter.im/4finance/uptodate-gradle-plugin?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge)

uptodate-gradle-plugin
======================

Gradle plugin that tells you what libs have new versions on Maven Central, so when you come back to a project, you know what you can update.

### How it works?

You run the plugin:

```
gradle uptodate
```

And you get for example this:

```
New versions available:
'com.google.guava:guava:17.0'
'org.hibernate:hibernate-entitymanager:4.3.5.Final'
'org.hibernate:hibernate-core:4.3.5.Final'
```

### How to install it?

#### Step 1: Add dependency to JCenter and to the plugin
```
buildscript {
    repositories {	
        jcenter()
    }
    dependencies {
        classpath 'com.ofg:uptodate-gradle-plugin:1.5.0'
    }
}
```

#### Step 2: Add the plugin to your build (gradle.build)
```
apply plugin: 'com.ofg.uptodate'
```

And now you can run the plugin with
```
gradle uptodate
```

### How to configure which configurations should be checked for updates?

By default all project configurations are checked for updates but you can exclude any of them.
```
uptodate {
    excludeConfigurations 'providedCompile', 'providedRuntime'
}
```

If you want to check only specific configurations it's also possible, simply include those configurations.
```
uptodate {
    includeConfigurations 'compile'
}
```

Please note that excludes take precedence over includes.

### How to exclude non-final versions from reported updates?

You can also provide patterns of versions that you would like to exclude.
There are also some patterns (like BETA, RC, etc.) defined in com.ofg.uptodate.UptodatePluginExtension.VersionPatterns

By default following patterns are excluded: ALPHA, BETA, RC, CR, SNAPSHOT, MILESTONE, RELEASE( i.e. r08)

```
import static com.ofg.uptodate.VersionPatterns.*

uptodate {
    setExcludedVersionPatterns ALPHA, BETA, '.*-demo-?\\d*$'
}
```

You can also add your own patterns to the already excluded version patterns.

```
uptodate {
    addExcludedVersionPatterns '.*-demo-?\\d*$'
}
```

### How to tune HTTP connections?

You can change connection timeout (5000 ms by default) and/or limit simultaneous HTTP connections (8 connections by default) by connectionTimeout and simultaneousHttpConnections properties respectively.

```
uptodate {
    connectionTimeout 10000
    simultaneousHttpConnections  4
}
```

### How to run the plugin if you are behind an HTTP proxy?

The proxy settings can be configured by using one of the following approaches:

#### 1. Via System properties (recommended)

Make sure to have the following properties configured when running the plugin:

```
http.proxyHost=localhost
http.proxyPort=15000
https.proxyHost=localhost
https.proxyPort=15000
```

#### 2. Via plugin configuration

Add the proxyHostname, proxyPort (defaults to -1, which is the default port of the scheme) and proxyScheme (defaults to 'http') properties to the plugin configuration.

```
uptodate {
    proxyHostname 'localhost'
    proxyPort 15000
    proxyScheme 'http'
}
```

### How to print project names along with information about new (or lack of) dependency updates?

By default (sub)project names are not printed so that you can easily operate on uptodate task output (e.g.: `sort | uniq`) but you can include project names in update headers by:

```
uptodate {
    reportProjectName true
}
```

### How to run the plugin on every build?

You can run the plugin automatically on every build, just by adding to your build.gradle

```
build.dependsOn 'uptodate'
```

But be warned, this will slow down the build by a few seconds (required to hit maven remote repo with http), so it is not suggested unless you don't care about build time.

### Changelog

To see what has changed in recent versions of Uptodate plugin see the [CHANGELOG](CHANGELOG.md) 
