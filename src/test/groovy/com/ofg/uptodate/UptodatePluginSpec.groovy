package com.ofg.uptodate

import com.github.dreamhead.moco.HttpServer
import com.github.dreamhead.moco.Setting
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import spock.lang.Specification
import com.github.dreamhead.moco.Runnable as MocoRunnable

import static com.github.dreamhead.moco.Moco.*
import static com.github.dreamhead.moco.Runner.running
import static com.ofg.uptodate.UptodatePlugin.getTASK_NAME

class UptodatePluginSpec extends Specification {
    static final String COMPILE_CONFIGURATION = 'compile'
    static final String TEST_COMPILE_CONFIGURATION = 'testCompile'
    static final int MOCK_HTTP_SERVER_PORT = 12306

    Project project = ProjectBuilder.builder().build()
    LoggerProxy loggerProxy = Mock()
    UptodatePlugin plugin = new UptodatePlugin(loggerProxy)
    HttpServer server = httpserver(MOCK_HTTP_SERVER_PORT)

    def setup() {
        def compileConfiguration = project.configurations.create(COMPILE_CONFIGURATION)
        project.configurations.create(TEST_COMPILE_CONFIGURATION) {extendsFrom compileConfiguration}
        plugin.apply(project)
        project.extensions.uptodate.mavenRepo = "http://localhost:${MOCK_HTTP_SERVER_PORT}"
    }

    def "should list all dependencies, that have newer versions in Maven Central"() {
        given:
            artifactMetadataRequestResponse('org.hibernate:hibernate-core', Jsons.HIBERNATE_RESPONSE)
            artifactMetadataRequestResponse('junit:junit', Jsons.JUNIT_RESPONSE)
        and:
            project.dependencies.add(COMPILE_CONFIGURATION, 'org.hibernate:hibernate-core:4.2.9.Final')
            project.dependencies.add(TEST_COMPILE_CONFIGURATION, 'junit:junit:4.11')
        when:
            executeUptodateTask()
        then:
            1 * loggerProxy.warn(_, "New versions available in Maven Central:\n" +
                    "'org.hibernate:hibernate-core:4.3.5.Final'")
    }

    def "should not fail or display phantom versions for dependencies not from Maven Central"() {
        given:
            artifactMetadataRequestResponse('net.gvmtool:gvm-sdk', Jsons.NOT_FOUND_GVM_RESPONSE)
        and:
            project.dependencies.add(COMPILE_CONFIGURATION, 'net.gvmtool:gvm-sdk:0.5.5')
        when:
            executeUptodateTask()
        then:
            0 * loggerProxy.warn(_, _)
    }

    def "should not display header on warn but only message on info when no new dependencies are found"() {
        given:
            artifactMetadataRequestResponse('junit:junit', Jsons.JUNIT_RESPONSE)
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
            artifactMetadataRequestResponse('org.hibernate:hibernate-core', Jsons.HIBERNATE_RESPONSE)
            artifactMetadataRequestResponse('junit:junit', Jsons.JUNIT_RESPONSE)
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

    private Setting artifactMetadataRequestResponse(String artifact, String response) {
        server.request(eq(query('q'),"id:\"$artifact\"")).response(header("content-type", "application/json"), with(text(response)))
    }

    private void executeUptodateTask() {
        running(server, { project.tasks.getByName(TASK_NAME).execute() } as MocoRunnable)
    }
}
