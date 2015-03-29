package com.ofg.uptodate.finder.dependency

class VersionPatternMatcher {

    private final String text

    VersionPatternMatcher(String text) {
        this.text = text
    }

    boolean notMatchesAny(List<String> excludedPatterns) {
        return excludedPatterns.every {
            !text.matches(it)
        }
    }
}
