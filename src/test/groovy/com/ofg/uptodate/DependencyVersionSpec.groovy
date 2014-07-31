package com.ofg.uptodate

import spock.lang.Specification

class DependencyVersionSpec extends Specification {

    def 'should resolve which version is greater'() {
        given:
            DependencyVersion dependencyVersion1 = new DependencyVersion(versionString1)
            DependencyVersion dependencyVersion2 = new DependencyVersion(versionString2)
        expect:
            (dependencyVersion1 <=> dependencyVersion2) == comparisonResult
        where:
            versionString1   | versionString2   || comparisonResult
            '2.0.0'          | '1.0.0'          || 1
            '1.1.0'          | '1.0.0'          || 1
            '1.1.1'          | '1.1.0'          || 1
            '1.1'            | '1.0'            || 1
            '1.1'            | '1.1-beta'       || 1
            '1.3'            | '1.3.RC'         || 1
            '3.3.2'          | '3.3'            || 1
            '1.1.1.1'        | '1.1.1.0'        || 1
            '1.1.1.1'        | '1.1.1.1-beta'   || 1
            '1.1.2'          | '1.1.1.1'        || 1
            '1.1.2'          | '1.1.2-beta'     || 1
            '0.7-groovy-2.0' | '0.7-groovy-1.8' || 1
    }
}
