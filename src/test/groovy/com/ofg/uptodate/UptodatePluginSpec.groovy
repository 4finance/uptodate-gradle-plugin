package com.ofg.uptodate

import com.github.dreamhead.moco.HttpServer
import com.github.dreamhead.moco.ResponseHandler
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import spock.lang.Specification

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

    def "should list all dependencies, that have newer versions in maven central"() {
        given:
            server.request(eq(query('q'),'id:"junit:junit"')).response(jsonContentType(), with(text(Jsons.JUNIT_RESPONSE)))
            server.request(eq(query('q'),'id:"org.hibernate:hibernate-core"')).response(jsonContentType(), with(text(Jsons.HIBERNATE_RESPONSE)))
        and:
            project.dependencies.add(COMPILE_CONFIGURATION, 'org.hibernate:hibernate-core:4.2.9.Final')
            project.dependencies.add(TEST_COMPILE_CONFIGURATION, 'junit:junit:4.11')
        when:
            running(server, new com.github.dreamhead.moco.Runnable() {
                @Override
                void run() throws Exception {
                    project.tasks.getByName(TASK_NAME).execute()
                }
            })
        then:
            1 * loggerProxy.warn(_, {
                it == "New versions available in maven central:\n" +
                    "'org.hibernate:hibernate-core:4.3.5.Final'"
            })
    }

    def "should not fail or display phantom versions for dependencies not from Maven Central"() {
        given:
            server.request(eq(query('q'),'id:"net.gvmtool:gvm-sdk"')).response(jsonContentType(), with(text(Jsons.NOT_FOUND_GVM_RESPONSE)))
        and:
            project.dependencies.add(COMPILE_CONFIGURATION, 'net.gvmtool:gvm-sdk:0.5.5')
        when:
            running(server, new com.github.dreamhead.moco.Runnable() {
                @Override
                void run() throws Exception {
                    project.tasks.getByName(TASK_NAME).execute()
                }
            })
        then:
            0 * loggerProxy.warn(_, _)
    }

    def "should not display header on warn but only message on info when no new dependencies are found"() {
        given:
            server.request(eq(query('q'),'id:"junit:junit"')).response(jsonContentType(), with(text(Jsons.JUNIT_RESPONSE)))
        and:
            project.dependencies.add(TEST_COMPILE_CONFIGURATION, 'junit:junit:4.11')
        when:
            running(server, new com.github.dreamhead.moco.Runnable() {
                @Override
                void run() throws Exception {
                    project.tasks.getByName(TASK_NAME).execute()
                }
            })
        then:
            0 * loggerProxy.warn(_, _)
            1 * loggerProxy.info(_, {
                it == "No new versions are available in maven central."
            })
    }

    private ResponseHandler jsonContentType() {
        header("content-type", "application/json")
    }
}
