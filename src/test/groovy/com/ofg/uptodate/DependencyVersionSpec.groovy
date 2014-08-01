package com.ofg.uptodate

import spock.lang.Specification

class DependencyVersionSpec extends Specification {

    def 'should resolve which version is greater'() {
        given:
            DependencyVersion higherDependencyVersion = new DependencyVersion(higherVersion)
            DependencyVersion lowerDependencyVersion = new DependencyVersion(lowerVersion)
        expect:
            higherDependencyVersion >lowerDependencyVersion
        where:
            higherVersion    | lowerVersion
            '2.0.0'          | '1.0.0'
            '1.1.0'          | '1.0.0'
            '1.1.1'          | '1.1.0'
            '1.1'            | '1.0'
            '1.1'            | '1.1-beta'
            '1.3'            | '1.3.RC'
            '1.3'            | '1.3.Beta'
            '3.3.2'          | '3.3'
            '1.1.1.1'        | '1.1.1.0'
            '1.1.1.1'        | '1.1.1.1-beta'
            '1.1.2'          | '1.1.1.1'
            '1.1.2'          | '1.1.2-beta'
            '0.7-groovy-2.0' | '0.7-groovy-1.8'
            'CD-001'         | 'CD-000'
    }
}
