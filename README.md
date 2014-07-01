
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
New versions available in maven central:
'com.fasterxml.jackson.core:jackson-databind:2.4.0-rc3'
'org.jacoco:org.jacoco.agent:0.7.1.201405082137'
:build
```

### How to install it?

#### Step 1: Add dependency to jcenter and to the plugin
```
buildscript {
    repositories {	
        jcenter()
    }
    dependencies {
        classpath('com.ofg:uptodate-gradle-plugin:0.0.3')
    }
}
```

#### Step 2: Add the plugin to your build (gradle.build)
```
apply plugin: 'uptodate'
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

### How to add private maven repos?

If you have your own maven repo (most companies do), that just add it to plugin configuration and it will verify newer versions there as well.

So in your build.gradle, add:

```
uptodate {
    mavenRepo = "address to your custom repo solrsearch (defaults to maven central repo solrsearch)"
}
```

### How to run the plugin on every build?

You can run the plugin automatically on every build, just by adding to your build.gradle

```
build.dependsOn 'uptodate'
```

But be warned, this will slow down the build by a few seconds (required to hit maven remote repo with http), so it is not suggested unless you don't care about build time.


### Current build status

[![Build Status](https://travis-ci.org/4finance/uptodate-gradle-plugin.svg?branch=master)](https://travis-ci.org/4finance/uptodate-gradle-plugin)


### Changelog

To see what has changed in recent versions of Uptodate plugin see the [CHANGELOG](CHANGELOG.md) 
