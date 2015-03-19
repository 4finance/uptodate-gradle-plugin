package com.ofg.uptodate.finder

import spock.lang.Specification
import spock.lang.Unroll

class DependencyVersionSpec extends Specification {

    @Unroll
    def 'should resolve [#higherVersion] as higher version than [#lowerVersion]'() {
        given:
            DependencyVersion higherDependencyVersion = new DependencyVersion(higherVersion)
            DependencyVersion lowerDependencyVersion = new DependencyVersion(lowerVersion)
        expect:
            higherDependencyVersion > lowerDependencyVersion
        where:
            higherVersion     | lowerVersion
            '2.0.0'           | '1.0.0'
            '1.1.0'           | '1.0.0'
            '1.1.1'           | '1.1.0'
            '1.1'             | '1.0'
            '1.1'             | '1.1-beta'
            '1.3'             | '1.3.RC'
            '1.3'             | '1.3.Beta'
            '3.3.2'           | '3.3'
            '1.1.1.1'         | '1.1.1.0'
            '1.1.1.1'         | '1.1.1.1-beta'
            '1.1.2'           | '1.1.1.1'
            '1.1.2'           | '1.1.2-beta'
            '0.7-groovy-2.0'  | '0.7-groovy-1.8'
            'CD-001'          | 'CD-000'
            '0.9.9-RC1'       | '0.9.8'
            '0.9.9.RC1'       | '0.9.8'
            '2.2.1-b03'       | '2.2'
            '1.3.14'          | '1.3.4-1'
            '0.7-groovy-11.0' | '0.7-groovy-2.0'
            '3.1.0'           | '3.1-b09'
            '4.3.6.Final'     | '4.2.9.Final'
    }
}
