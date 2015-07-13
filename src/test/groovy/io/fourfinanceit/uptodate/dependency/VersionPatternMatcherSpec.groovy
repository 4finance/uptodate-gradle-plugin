package io.fourfinanceit.uptodate.dependency

import spock.lang.Specification

import static io.fourfinanceit.uptodate.VersionPatterns.*

class VersionPatternMatcherSpec extends Specification {

    VersionPatternMatcher matcher = new VersionPatternMatcher('RC-1')

    def 'should detect if version matches any pattern'() {
        expect:
            matcher.matchesNoneOf([ALPHA, RC, BETA])
    }

    def 'should detect if version does not match any pattern'() {
        expect:
            matcher.matchesNoneOf([ALPHA, BETA])
    }
}
