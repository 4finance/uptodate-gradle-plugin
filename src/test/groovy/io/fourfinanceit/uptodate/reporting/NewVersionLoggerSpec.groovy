package io.fourfinanceit.uptodate.reporting

import io.fourfinanceit.uptodate.LoggerProxy
import io.fourfinanceit.uptodate.dependency.Dependency
import spock.lang.Specification

import static NewVersionLogger.NEW_VERSIONS_AVAILABLE
import static NewVersionLogger.NEW_VERSIONS_MESSAGE_HEADER
import static NewVersionLogger.NO_NEW_VERSIONS_AVAILABLE
import static NewVersionLogger.NO_NEW_VERSIONS_MESSAGE

class NewVersionLoggerSpec extends Specification {

    private static final Dependency HIBERNATE = new Dependency('org.hibernate', 'hibernate-core', '4.3.6.Final')
    private static final Dependency JUNIT = new Dependency('junit', 'junit', '4.11')
    private static final String PROJECT_NAME = 'ProjectName'

    LoggerProxy logger = Mock()

    def 'should report updates in lexical order'() {
        given:
            NewVersionLogger reporter = new NewVersionLogger(logger, PROJECT_NAME, false)
            List<Dependency> updates = [HIBERNATE, JUNIT]
        when:
            reporter.reportUpdates(updates as Set)
        then:
            1 * logger.lifecycle(_, NEW_VERSIONS_MESSAGE_HEADER +
                                    "'junit:junit:4.11'\n" +
                                    "'org.hibernate:hibernate-core:4.3.6.Final'")
    }

    def 'should not print project name by default'() {
        given:
            NewVersionLogger reporter = new NewVersionLogger(logger, PROJECT_NAME, false)
        when:
            reporter.reportUpdates(dependencies as Set)
        then:
            1 * logger.lifecycle(_, message)
        where:
            dependencies | message
            [JUNIT]      | "$NEW_VERSIONS_MESSAGE_HEADER'junit:junit:4.11'"
            []           | NO_NEW_VERSIONS_MESSAGE
    }

    def 'should allow to print project name'() {
        given:
            NewVersionLogger reporter = new NewVersionLogger(logger, PROJECT_NAME, true)
        when:
            reporter.reportUpdates(dependencies as Set)
        then:
            1 * logger.lifecycle(_, message)
        where:
            dependencies | message
            [JUNIT]      | "$NEW_VERSIONS_AVAILABLE for $PROJECT_NAME:\n'junit:junit:4.11'"
            []           | "$NO_NEW_VERSIONS_AVAILABLE for $PROJECT_NAME."
    }
}
