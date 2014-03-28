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
    Project project = ProjectBuilder.builder().build()
    LoggerProxy loggerProxy = Mock()
    UptodatePlugin plugin = new UptodatePlugin(loggerProxy)

    def setup() {
        project.configurations.create(COMPILE_CONFIGURATION)
        plugin.apply(project)
    }

    def "should list all dependencies, that have newer versions in maven central"() {
        given:
            HttpServer server = httpserver(12306)
            server.request(eq(query('q'),'id:"junit:junit"')).response(jsonContentType(), with(text(Jsons.JUNIT_RESPONSE)))
            server.request(eq(query('q'),'id:"org.mockito:mockito-core"')).response(jsonContentType(), with(text(Jsons.MOCKITO_RESPONSE)))
        and:
            project.dependencies.add(COMPILE_CONFIGURATION, 'junit:junit:4.11')
            project.dependencies.add(COMPILE_CONFIGURATION, 'org.mockito:mockito-core:1.8.5')
            project.extensions.uptodate.mavenRepo = "http://localhost:12306"
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
                    "$COMPILE_CONFIGURATION 'org.mockito:mockito-core:1.9.5'"
            })
    }

    private ResponseHandler jsonContentType() {
        header("content-type", "application/json")
    }
}
