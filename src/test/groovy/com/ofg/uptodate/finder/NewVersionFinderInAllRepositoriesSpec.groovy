package com.ofg.uptodate.finder

import com.ofg.uptodate.LoggerProxy
import com.ofg.uptodate.dependency.Dependency
import spock.lang.Specification

import static com.ofg.uptodate.finder.NewVersionFinderInAllRepositories.NEW_VERSIONS_MESSAGE_HEAD

class NewVersionFinderInAllRepositoriesSpec extends Specification {

    static final Dependency HIBERNATE = new Dependency('org.hibernate', 'hibernate-core', '4.3.6.Final')
    static final Dependency JUNIT = new Dependency('junit', 'junit', '4.11')

    LoggerProxy logger = Mock()
    NewVersionFinderInAllRepositories finder = new NewVersionFinderInAllRepositories(logger, [])

    def 'should print versions in lexical order'() {
        given:
            List<Dependency> updates = [HIBERNATE, JUNIT]
        when:
            finder.printDependencies(updates as Set)
        then:
            1 * logger.lifecycle(_, "$NEW_VERSIONS_MESSAGE_HEAD\n" +
                    "'junit:junit:4.11'\n" +
                    "'org.hibernate:hibernate-core:4.3.6.Final'")
    }
}
