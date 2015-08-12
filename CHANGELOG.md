1.6.0
-----
New features:
* [#16](https://github.com/4finance/uptodate-gradle-plugin/issues/16) Fail build if new versions of specified packages are found

1.5.2
-----
Bug fixes:
* [#51](https://github.com/4finance/uptodate-gradle-plugin/pull/52) Added support for Gradle 2.5

1.5.1
-----
New features:
* [#36](https://github.com/4finance/uptodate-gradle-plugin/pull/36) Add possibility to report project name in update header
* [#42](https://github.com/4finance/uptodate-gradle-plugin/pull/46) Workaround for wrong content type returned from jCenter

1.5.0
-----
Breaking changes:
* Restore logging on `warn` log level

1.4.0
-----
New features:
* [#32](https://github.com/4finance/uptodate-gradle-plugin/pull/32) Dependency updates are reported in lexical order

Bug fixes:
* [#30](https://github.com/4finance/uptodate-gradle-plugin/issues/30) AllRepositoriesnewVersionFinderSpec test failing in IntelliJ IDEA

Breaking changes:
* [#33](https://github.com/4finance/uptodate-gradle-plugin/pull/33) Dependency updates are logged on `lifecycle` log level instead of `warn`

1.3.1
-----
Bug fixes:
* [#15](https://github.com/4finance/uptodate-gradle-plugin/issues/25) Fixed dependency version comparison

1.3.0
-----
[Changes](https://github.com/4finance/uptodate-gradle-plugin/issues?q=is%3Aissue+is%3Aclosed+milestone%3A1.3.0)

1.2.0
-----
New features:
* [#18](https://github.com/4finance/uptodate-gradle-plugin/issues/18) Support Proxy configuration
* Moving towards Github release notes and changelog - this file will be deprecated

1.1.1
-----
Bug fixes:
* [#13](https://github.com/4finance/uptodate-gradle-plugin/issues/13) Two new versions found in project

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

Breaking changes:
* renamed plugin from `uptodate` to `com.ofg.uptodate`

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
