package com.ofg.uptodate.reporting

import com.ofg.uptodate.LoggerProxy
import com.ofg.uptodate.UptodatePluginExtension
import com.ofg.uptodate.dependency.Dependency
import spock.lang.Specification

import static NewVersionProcessor.NEW_VERSIONS_AVAILABLE
import static NewVersionProcessor.NEW_VERSIONS_MESSAGE_HEADER
import static NewVersionProcessor.NO_NEW_VERSIONS_AVAILABLE
import static NewVersionProcessor.NO_NEW_VERSIONS_MESSAGE

class NewVersionLoggerSpec extends Specification {

    private static final Dependency HIBERNATE = new Dependency('org.hibernate', 'hibernate-core', '4.3.6.Final')
    private static final Dependency JUNIT = new Dependency('junit', 'junit', '4.11')
    private static final String PROJECT_NAME = 'ProjectName'
    private static final UptodatePluginExtension NO_REPORT_EXTENSIONS = new UptodatePluginExtension(reportProjectName: false)

    LoggerProxy logger = Mock()

    def 'should report updates in lexical order'() {
        given:
            NewVersionProcessor reporter = new NewVersionProcessor(logger, PROJECT_NAME, NO_REPORT_EXTENSIONS)
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
            NewVersionProcessor reporter = new NewVersionProcessor(logger, PROJECT_NAME, NO_REPORT_EXTENSIONS)
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
            NewVersionProcessor reporter = new NewVersionProcessor(logger, PROJECT_NAME, new UptodatePluginExtension(reportProjectName: true))
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
