package com.ofg.uptodate.finder.maven

import com.ofg.uptodate.finder.HttpProxyServerProvider
import com.ofg.uptodate.finder.NewFinderSpec
import groovy.text.SimpleTemplateEngine
import org.codehaus.groovy.runtime.StackTraceUtils

import static com.ofg.uptodate.Jsons.*
import static com.ofg.uptodate.VersionPatterns.*

@Mixin([MavenReponseProvider, HttpProxyServerProvider])
class MavenNewVersionFinderSpec extends NewFinderSpec {

    def setup() {
        project.extensions.uptodate.mavenRepo = "http://localhost:${MOCK_HTTP_SERVER_PORT}/"
        project.extensions.uptodate.ignoreJCenter = true
    }

    protected void artifactMetadataRequestResponse(String group, String name, String response) {
        stubInteractionForMavenCentral(group, name, response)
    }
    
    private void artifactMetadataRequestResponseThroughProxy(String group, String name, String response) {
        stubProxyInteractionForMavenCentral(group, name, response)
    }
    
    private void runningBehindHttpProxy() {
        startHttpProxyServer()
    }
    
    @Override
    protected Integer getHttpServerPort() {
        return MOCK_HTTP_SERVER_PORT
    }

    def "should list all dependencies, that have newer versions"() {
        given:
            artifactMetadataRequestResponse('org.hibernate', 'hibernate-core', HIBERNATE_RESPONSE)
            artifactMetadataRequestResponse('junit' ,'junit', JUNIT_RESPONSE)
        and:
            project.dependencies.add(COMPILE_CONFIGURATION, 'org.hibernate:hibernate-core:4.2.9.Final')
            project.dependencies.add(TEST_COMPILE_CONFIGURATION, 'junit:junit:4.11')
        when:
            executeUptodateTask()
        then:
            1 * loggerProxy.warn(_, "New versions available:\n" +
                    "'org.hibernate:hibernate-core:4.3.6.Final'")
    }

    def "should not fail or display phantom versions for dependencies not from Maven Central"() {
        given:
            artifactMetadataRequestResponse('net.gvmtool', 'gvm-sdk', NOT_FOUND_GVM_RESPONSE)
        and:
            project.dependencies.add(COMPILE_CONFIGURATION, 'net.gvmtool:gvm-sdk:0.5.5')
        when:
            executeUptodateTask()
        then:
            0 * loggerProxy.warn(_, _)
    }

    def "should not fail or display excluded versions for dependencies found in external repo"() {
        given:
            artifactMetadataRequestResponse('org.hibernate', 'hibernate-core', new SimpleTemplateEngine().createTemplate(RESPONSE_TEMPLATE).make([artifactVersion: artifactVersion]).toString())
            project.extensions.uptodate.versionToExcludePatterns = [ALPHA, BETA, RC, CR, SNAPSHOT]
        and:
            project.dependencies.add(COMPILE_CONFIGURATION, 'org.hibernate:hibernate-core:0.5.5')
        when:
            executeUptodateTask()
        then:
            0 * loggerProxy.warn(_, _)
        where:
            artifactVersion << ['2.2.2-BETA', '2.2.Beta3', '2.2.2-alpha', '2.2.Alpha-3', '1.3.RC', '1.3.CR1', '1.0.0-SNAPSHOT']
    }

    def "should not display header on warn but only message on info when no new dependencies are found"() {
        given:
            artifactMetadataRequestResponse('junit', 'junit', JUNIT_RESPONSE)
        and:
            project.dependencies.add(TEST_COMPILE_CONFIGURATION, 'junit:junit:4.11')
        when:
            executeUptodateTask()
        then:
            0 * loggerProxy.warn(_, _)
            1 * loggerProxy.info(_, "No new versions are available.")
    }

    def 'should not list dependencies from excluded configurations'() {
        given:
            artifactMetadataRequestResponse('org.hibernate', 'hibernate-core', HIBERNATE_RESPONSE)
            artifactMetadataRequestResponse('junit', 'junit', JUNIT_RESPONSE)
        and:
            project.dependencies.add(COMPILE_CONFIGURATION, 'org.hibernate:hibernate-core:4.2.9.Final')
            project.dependencies.add(TEST_COMPILE_CONFIGURATION, 'junit:junit:4.8')
        and:
            project.extensions.uptodate.excludeConfigurations(COMPILE_CONFIGURATION)
        when:
            executeUptodateTask()
        then:
            1 * loggerProxy.warn(_, "New versions available:\n" +
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

    def 'should not list any newer versions if the current version seems greater than the final version taken from Maven Central'() {
        given:
            artifactMetadataRequestResponse('javax.servlet.jsp','jsp-api', JSP_API_RESPONE)
        and:
            project.dependencies.add(COMPILE_CONFIGURATION, 'javax.servlet.jsp:jsp-api:2.2.1-b03')
        when:
            executeUptodateTask()
        then:
            0 * loggerProxy.warn(_, _)
            1 * loggerProxy.info(_, "No new versions are available.")
    }

    def 'should list all dependencies that have newer versions if running behind a proxy server configured via plugin'() {
        given:
            runningBehindHttpProxy()
        and:
            artifactMetadataRequestResponseThroughProxy('org.hibernate', 'hibernate-core', HIBERNATE_RESPONSE)
            artifactMetadataRequestResponseThroughProxy('junit' ,'junit', JUNIT_RESPONSE)
        and:
            project.dependencies.add(COMPILE_CONFIGURATION, 'org.hibernate:hibernate-core:4.2.9.Final')
            project.dependencies.add(TEST_COMPILE_CONFIGURATION, 'junit:junit:4.11')
        and:
            project.extensions.uptodate.with {
                proxyHostname = 'localhost'
                proxyPort = MOCK_HTTP_PROXY_SERVER_PORT
                proxyScheme = 'http'
            }
        when:
            executeUptodateTask()
        then:
            1 * loggerProxy.warn(_, "New versions available:\n" +
                "'org.hibernate:hibernate-core:4.3.6.Final'")
        cleanup:
            shutdownHttpProxyServer()
    }

}
