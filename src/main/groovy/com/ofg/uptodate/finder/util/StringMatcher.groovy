package com.ofg.uptodate.finder.util

class StringMatcher {

    private final String text

    StringMatcher(String text) {
        this.text = text
    }

    boolean notMatchesAny(List<String> excludedPatterns) {
        return excludedPatterns.every {
            !text.matches(it)
        }
    }
}
