package com.ofg.uptodate.finder.jcenter

import com.ofg.uptodate.finder.HttpProxyServerProvider
import com.ofg.uptodate.finder.NewFinderSpec
import groovy.text.SimpleTemplateEngine
import org.codehaus.groovy.runtime.StackTraceUtils
import spock.lang.Unroll

import static com.ofg.uptodate.VersionPatterns.*
import static com.ofg.uptodate.Xmls.*
import static com.ofg.uptodate.finder.NewVersionFinderInAllRepositories.NO_NEW_VERSIONS_MESSAGE

@Mixin([JCenterReponseProvider, HttpProxyServerProvider])
class JCenterNewVersionFinderSpec extends NewFinderSpec {
    
    def setup() {
        project.extensions.uptodate.jCenterRepo = "http://localhost:${MOCK_HTTP_SERVER_PORT}/"
        project.extensions.uptodate.ignoreMavenCentral = true
    }

    @Override
    protected void artifactMetadataRequestResponse(String group, String name, String response) {
        stubInteractionForJcenter(group, name, response)
    }

    private void artifactMetadataRequestResponseThroughProxy(String group, String name, String response) {
        stubProxyInteractionForJcenter(group, name, response)
    }

    private void runningBehindHttpProxy() {
        startHttpProxyServer()
    }

    def "should list all dependencies, that have newer versions"() {
        given:
            artifactMetadataRequestResponse('org.hibernate', 'hibernate-core', HIBERNATE_CORE_META_DATA)
            artifactMetadataRequestResponse('junit' ,'junit', JUNIT_META_DATA)
        and:
            project.dependencies.add(COMPILE_CONFIGURATION, 'org.hibernate:hibernate-core:4.2.9.Final')
            project.dependencies.add(TEST_COMPILE_CONFIGURATION, 'junit:junit:4.11')
        when:
            executeUptodateTask()
        then:
            1 * loggerProxy.lifecycle(_, "New versions available:\n" +
                "'org.hibernate:hibernate-core:4.3.6.Final'")
    }

    def "should not fail or display phantom versions for dependencies not from Jcenter"() {
        given:
            artifactMetadataRequestResponse(400)
        and:
            project.dependencies.add(COMPILE_CONFIGURATION, 'net.gvmtool:gvm-sdk:0.5.5')
        when:
            executeUptodateTask()
        then:
            1 * loggerProxy.lifecycle(_, NO_NEW_VERSIONS_MESSAGE)
    }

    @Unroll
    def "should not fail or display excluded versions for dependencies found in external repo"() {
        given:
            artifactMetadataRequestResponse('org.hibernate', 'hibernate-core', new SimpleTemplateEngine().createTemplate(RESPONSE_TEMPLATE).make([artifactVersion: artifactVersion]).toString())
            project.extensions.uptodate.setExcludedVersionPatterns(ALPHA, BETA, RC, CR, SNAPSHOT)
        and:
            project.dependencies.add(COMPILE_CONFIGURATION, 'org.hibernate:hibernate-core:0.5.5')
        when:
            executeUptodateTask()
        then:
            1 * loggerProxy.lifecycle(_, NO_NEW_VERSIONS_MESSAGE)
        where:
            artifactVersion << ['2.2.2-BETA', '2.2.Beta3', '2.2.2-alpha', '2.2.Alpha-3', '1.3.RC', '1.3.CR1', '1.0.0-SNAPSHOT']
    }

    def 'should inform when no new dependencies are found'() {
        given:
            artifactMetadataRequestResponse('junit', 'junit', JUNIT_META_DATA)
        and:
            project.dependencies.add(TEST_COMPILE_CONFIGURATION, 'junit:junit:4.11')
        when:
            executeUptodateTask()
        then:
            1 * loggerProxy.lifecycle(_, NO_NEW_VERSIONS_MESSAGE)
    }

    def 'should not list dependencies from excluded configurations'() {
        given:
            artifactMetadataRequestResponse('org.hibernate', 'hibernate-core', HIBERNATE_CORE_META_DATA)
            artifactMetadataRequestResponse('junit', 'junit', JUNIT_META_DATA)
        and:
            project.dependencies.add(COMPILE_CONFIGURATION, 'org.hibernate:hibernate-core:4.2.9.Final')
            project.dependencies.add(TEST_COMPILE_CONFIGURATION, 'junit:junit:4.8')
        and:
            project.extensions.uptodate.excludeConfigurations(COMPILE_CONFIGURATION)
        when:
            executeUptodateTask()
        then:
            1 * loggerProxy.lifecycle(_, "New versions available:\n" +
                "'junit:junit:4.11'")
    }

    def 'should fail to find any updates due to timeout'() {
        given:
            stubResponseWithADelayOf(500)
            project.dependencies.add(TEST_COMPILE_CONFIGURATION, 'junit:junit:4.8')
        and:
            project.extensions.uptodate.connectionTimeout = 100
        when:
            executeUptodateTask()
        then:
            Throwable thrownException = thrown()
            StackTraceUtils.extractRootCause(thrownException).class == SocketTimeoutException
    }

    def 'should list all dependencies that have newer versions if running behind a proxy server configured via plugin'() {
        given:
            runningBehindHttpProxy()
        and:
            artifactMetadataRequestResponseThroughProxy('org.hibernate', 'hibernate-core', HIBERNATE_CORE_META_DATA)
            artifactMetadataRequestResponseThroughProxy('junit' ,'junit', JUNIT_META_DATA)
        and:
            project.dependencies.add(COMPILE_CONFIGURATION, 'org.hibernate:hibernate-core:4.2.9.Final')
            project.dependencies.add(TEST_COMPILE_CONFIGURATION, 'junit:junit:4.11')
        and:
            project.extensions.uptodate.with {
                proxyHostname = MOCK_HTTP_PROXY_SERVER_HOST
                proxyPort = MOCK_HTTP_PROXY_SERVER_PORT
                proxyScheme = 'http'
            }
        when:
            executeUptodateTask()
        then:
            1 * loggerProxy.lifecycle(_, "New versions available:\n" +
                "'org.hibernate:hibernate-core:4.3.6.Final'")
        cleanup:
            shutdownHttpProxyServer()
    }

    def 'should list all dependencies that have newer versions if running behind a proxy server configured via system properties'() {
        given:
            runningBehindHttpProxy()
        and:
            artifactMetadataRequestResponseThroughProxy('org.hibernate', 'hibernate-core', HIBERNATE_CORE_META_DATA)
            artifactMetadataRequestResponseThroughProxy('junit' ,'junit', JUNIT_META_DATA)
        and:
            project.dependencies.add(COMPILE_CONFIGURATION, 'org.hibernate:hibernate-core:4.2.9.Final')
            project.dependencies.add(TEST_COMPILE_CONFIGURATION, 'junit:junit:4.11')
        and:
            String previousProxyHostname = System.setProperty('http.proxyHost', MOCK_HTTP_PROXY_SERVER_HOST)
            String previousProxyPort = System.setProperty('http.proxyPort', MOCK_HTTP_PROXY_SERVER_PORT.toString())
        when:
            executeUptodateTask()
        then:
            1 * loggerProxy.lifecycle(_, "New versions available:\n" +
                "'org.hibernate:hibernate-core:4.3.6.Final'")
        cleanup:
            shutdownHttpProxyServer()
            System.setProperty('http.proxyHost', previousProxyHostname ?: '')
            System.setProperty('http.proxyPort', previousProxyPort ?: '')
    }
}
