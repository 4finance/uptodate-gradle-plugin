package com.ofg.uptodate.finder.util

class StringMatcher {

    String text

    StringMatcher(String text) {
        this.text = text
    }

    boolean notMatchesAny(List<String> excludedPatterns) {
        return excludedPatterns.every {
            !text.matches(it)
        }
    }
}
