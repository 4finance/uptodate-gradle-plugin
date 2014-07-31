package com.ofg.uptodate

import com.ofg.uptodate.http.WireMockSpec
import org.codehaus.groovy.runtime.StackTraceUtils
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder

import static com.github.tomakehurst.wiremock.client.WireMock.*
import static com.ofg.uptodate.Jsons.HIBERNATE_RESPONSE
import static com.ofg.uptodate.Jsons.JUNIT_RESPONSE
import static com.ofg.uptodate.Jsons.NOT_FOUND_GVM_RESPONSE
import static com.ofg.uptodate.UptodatePlugin.getTASK_NAME
import static com.ofg.uptodate.UrlEspaceUtils.escape

class UptodatePluginSpec extends WireMockSpec {
    static final String COMPILE_CONFIGURATION = 'compile'
    static final String TEST_COMPILE_CONFIGURATION = 'testCompile'
    static final int MOCK_HTTP_SERVER_PORT = 12306

    static final String ROOT_PATH = "/\\?"
    static final String SOLR_AND = '\\+AND\\+'

    Project project = ProjectBuilder.builder().build()
    LoggerProxy loggerProxy = Mock()
    UptodatePlugin plugin = new UptodatePlugin(loggerProxy)

    def setup() {
        def compileConfiguration = project.configurations.create(COMPILE_CONFIGURATION)
        project.configurations.create(TEST_COMPILE_CONFIGURATION) {extendsFrom compileConfiguration}
        plugin.apply(project)
        project.extensions.uptodate.mavenRepo = "http://localhost:${MOCK_HTTP_SERVER_PORT}/"
    }

    @Override
    protected Integer getHttpServerPort() {
        return MOCK_HTTP_SERVER_PORT
    }

    def "should list all dependencies, that have newer versions in Maven Central"() {
        given:
            artifactMetadataRequestResponse('org.hibernate', 'hibernate-core', HIBERNATE_RESPONSE)
            artifactMetadataRequestResponse('junit' ,'junit', JUNIT_RESPONSE)
        and:
            project.dependencies.add(COMPILE_CONFIGURATION, 'org.hibernate:hibernate-core:4.2.9.Final')
            project.dependencies.add(TEST_COMPILE_CONFIGURATION, 'junit:junit:4.11')
        when:
            executeUptodateTask()
        then:
            1 * loggerProxy.warn(_, "New versions available in Maven Central:\n" +
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

    def "should not display header on warn but only message on info when no new dependencies are found"() {
        given:
            artifactMetadataRequestResponse('junit', 'junit', JUNIT_RESPONSE)
        and:
            project.dependencies.add(TEST_COMPILE_CONFIGURATION, 'junit:junit:4.11')
        when:
            executeUptodateTask()
        then:
            0 * loggerProxy.warn(_, _)
            1 * loggerProxy.info(_, "No new versions are available in Maven Central.")
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
            1 * loggerProxy.warn(_, "New versions available in Maven Central:\n" +
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

    private void artifactMetadataRequestResponse(String group, String name, String response) {
        stubInteraction(get(urlMatching( "${ROOT_PATH}q=${escape("g:\"$group\"")}$SOLR_AND${escape("a:\"$name\"")}.*")), aResponse().withStatus(200).withHeader("Content-Type", "application/json").withBody(response))
    }

    private void executeUptodateTask() {
        project.tasks.getByName(TASK_NAME).execute()
    }

    private void stubResponseWithADelayOf(int delayInMs) {
        stubInteraction(get(urlMatching('/.*')), aResponse().withFixedDelay(delayInMs))
    }

}
