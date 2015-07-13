package io.fourfinanceit.uptodate.dependency

import spock.lang.Specification

class DependencyEqualsSpec extends Specification {
    Dependency junit4_11 = new Dependency('junit', 'junit', '4.11')
    Dependency junit4_8 = new Dependency('junit', 'junit', '4.8')

    def 'dependencies with same group, name and version should be considered equal'() {
        expect:
            new Dependency('junit', 'junit', '4.11') == junit4_11
    }

    def 'distinct dependencies should not be considered equal'() {
        expect:
            junit4_11 != junit4_8
    }
}
