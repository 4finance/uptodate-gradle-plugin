package com.ofg.uptodate
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import spock.lang.Specification

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
            project.dependencies.add(COMPILE_CONFIGURATION, 'junit:junit:4.11')
            project.dependencies.add(COMPILE_CONFIGURATION, 'org.mockito:mockito-core:1.8.5')

        when:
            project.tasks.getByName(TASK_NAME).execute()

        then:
            1 * loggerProxy.warn(_, {
                it == "New versions available in maven central:\n" +
                    "$COMPILE_CONFIGURATION 'org.mockito:mockito-core:1.9.5'"
            })
    }
}
