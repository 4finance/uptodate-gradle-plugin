1.1.0
-----
New features:
* adding new excluded version patterns to already excluded ones

Changes to plugin configuration DSL:
* removed `versionToExcludePatterns` method and property - use `addExcludedVersionPatterns` and `setExcludedVersionPatterns` methods instead
* removed `connectionTimeout` method - use `connectionTimeout` property instead

1.0.0
-----
New features:
* verification of versions against JCenter
* plugin available at http://plugins.gradle.org - see http://plugins.gradle.org/plugin/com.ofg.uptodate

0.0.6
-----
New features:
* max simultaneous HTTP connections configuration
* exclude SNAPSHOT versions

Bug fixes:
* fixed checking version against gradleApi()

0.0.5
-----
Bug fixes:
* fixed a bug related to passing no minor version (for example 'CD-000')

0.0.4
-----
New features:
* connection timeout
* dependencies version exclude patterns

Bug fixes:
* [Issue #5 - Dependencies with version different than the latest available in Maven Central are treated as outdated](https://github.com/4finance/uptodate-gradle-plugin/issues/5)

0.0.3
-----
New features:
* configuration filter

0.0.2
-----
Bug fixes:
* [Issue #1 - uptodate task fails with 'Cannot access first() element from an empty List'](https://github.com/4finance/uptodate-gradle-plugin/issues/1)

0.0.1
-----
Initial release