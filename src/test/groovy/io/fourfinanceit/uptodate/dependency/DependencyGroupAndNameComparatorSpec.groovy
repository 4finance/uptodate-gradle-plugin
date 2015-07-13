package io.fourfinanceit.uptodate.dependency

import spock.lang.Specification

class DependencyGroupAndNameComparatorSpec extends Specification {

    static final int FIRST_IS_GREATER = 1
    static final int SECOND_IS_GREATER = -1
    static final int ARE_EQUAL = 0
    static final String VALID_VERSION = '1.0'

    DependencyGroupAndNameComparator comparator = new DependencyGroupAndNameComparator()

    def 'should compare dependency group and name in lexical order'() {
        given:
            Dependency dependency1 = new Dependency(group1, name1, VALID_VERSION)
            Dependency dependency2 = new Dependency(group2, name2, VALID_VERSION)
        expect:
            comparator.compare(dependency1, dependency2) == comparisonResult
        where:
            group1          | name1            | group2          | name2                 || comparisonResult
            'org.hibernate' | 'hibernate-core' | 'junit'         | 'junit'               || FIRST_IS_GREATER
            'junit'         | 'junit'          | 'org.hibernate' | 'hibernate-core'      || SECOND_IS_GREATER
            'org.hibernate' | 'hibernate-core' | 'org.hibernate' | 'hibernate-validator' || SECOND_IS_GREATER
            'org.hibernate' | 'hibernate-core' | 'org.hibernate' | 'hibernate-core'      || ARE_EQUAL
    }
}
