package com.ofg.uptodate.reporting
import com.ofg.uptodate.LoggerProxy
import com.ofg.uptodate.UptodatePluginExtension
import com.ofg.uptodate.dependency.Dependency
import spock.lang.Specification

class BuildBreakingSpec extends Specification {

    private static final Dependency HIBERNATE = new Dependency('org.hibernate', 'hibernate-core', '4.3.6.Final')
    private static final Dependency JUNIT = new Dependency('junit', 'junit', '4.11')
    private static final String PROJECT_NAME = 'ProjectName'

    LoggerProxy logger = Mock()

    def 'should not break the build if build breaker is turned off'() {
        given:
            UptodatePluginExtension uptodatePluginExtension = extensionWithoutBuildBreaker()
            NewVersionProcessor reporter = new NewVersionProcessor(logger, PROJECT_NAME, uptodatePluginExtension)
            List<Dependency> updates = [HIBERNATE, JUNIT]
        when:
            reporter.reportUpdates(updates as Set)
        then:
            noExceptionThrown()
    }

    def 'should break the build if build breaker is turned on and no filtering is applied'() {
        given:
            UptodatePluginExtension uptodatePluginExtension = extensionWithBuildBreaker()
            NewVersionProcessor reporter = new NewVersionProcessor(logger, PROJECT_NAME, uptodatePluginExtension)
            List<Dependency> updates = [HIBERNATE, JUNIT]
        when:
            reporter.reportUpdates(updates as Set)
        then:
            thrown(NewDependencyVersionsFoundException)
    }

    def 'should break the build if build breaker is turned on and package filtering is applied'() {
        given:
            UptodatePluginExtension uptodatePluginExtension = extensionWithBuildBreakerWithPackageFiltering()
            NewVersionProcessor reporter = new NewVersionProcessor(logger, PROJECT_NAME, uptodatePluginExtension)
            List<Dependency> updates = [HIBERNATE, JUNIT]
        when:
            reporter.reportUpdates(updates as Set)
        then:
            thrown(NewDependencyVersionsFoundException)
    }

    def 'should not break the build if build breaker is turned on, package filtering is applied and found dependency is excluded'() {
        given:
            UptodatePluginExtension uptodatePluginExtension = extensionWithBuildBreakerWithPackageIncludsionAndExclusion()
            NewVersionProcessor reporter = new NewVersionProcessor(logger, PROJECT_NAME, uptodatePluginExtension)
            List<Dependency> updates = [HIBERNATE, JUNIT]
        when:
            reporter.reportUpdates(updates as Set)
        then:
            noExceptionThrown()
    }

    UptodatePluginExtension extensionWithoutBuildBreaker() {
        UptodatePluginExtension uptodatePluginExtension = new UptodatePluginExtension()
        uptodatePluginExtension.breakTheBuild {
            enabled = false
        }
        return uptodatePluginExtension
    }

    UptodatePluginExtension extensionWithBuildBreaker() {
        UptodatePluginExtension uptodatePluginExtension = new UptodatePluginExtension()
        uptodatePluginExtension.breakTheBuild {
            enabled = true
        }
        return uptodatePluginExtension
    }

    UptodatePluginExtension extensionWithBuildBreakerWithPackageFiltering() {
        UptodatePluginExtension uptodatePluginExtension = new UptodatePluginExtension()
        uptodatePluginExtension.breakTheBuild {
            enabled = true
            includePatterns('org.hibernate')

        }
        return uptodatePluginExtension
    }

    UptodatePluginExtension extensionWithBuildBreakerWithPackageIncludsionAndExclusion() {
        UptodatePluginExtension uptodatePluginExtension = new UptodatePluginExtension()
        uptodatePluginExtension.breakTheBuild {
            enabled = true
            excludedPatterns('hibernate-core', 'junit')

        }
        return uptodatePluginExtension
    }

}
