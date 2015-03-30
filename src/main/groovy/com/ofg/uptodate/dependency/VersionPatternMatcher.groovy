package com.ofg.uptodate.dependency

class VersionPatternMatcher {

    private final String version

    VersionPatternMatcher(String version) {
        this.version = version
    }

    boolean matchesNoneOf(List<String> patterns) {
        return !patterns.any {
            version.matches(it)
        }
    }
}
