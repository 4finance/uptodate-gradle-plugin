package com.ofg.uptodate

import spock.lang.Specification

class DependencySpec extends Specification {
    static final Dependency JUNIT_4_11 = new Dependency('junit', 'junit', '4.11')
    static final Dependency JUNIT_4_8 = new Dependency('junit', 'junit', '4.8')

    def 'dependencies with same group, name and version should be considered equal'() {
        expect:
        new Dependency('junit', 'junit', '4.11') == JUNIT_4_11
    }

    def 'distinct dependencies should not be considered equal'() {
        expect:
        JUNIT_4_11 != JUNIT_4_8
    }
}
