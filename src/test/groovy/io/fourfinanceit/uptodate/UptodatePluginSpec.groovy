package io.fourfinanceit.uptodate

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import spock.lang.Specification

import static UptodatePlugin.TASK_NAME

class UptodatePluginSpec extends Specification {

    Project project = ProjectBuilder.builder().build()
    LoggerProxy loggerProxy = Mock()
    UptodatePlugin plugin = new UptodatePlugin(loggerProxy)

    def setup() {
        plugin.apply(project)        
    }

    def 'should print info how to add jcenter if flag is present and jcenter is not'() {
        given:
            project.extensions.uptodate.showMissingJCenterMessage = true
            project.extensions.uptodate.ignoreMavenCentral = true
            project.extensions.uptodate.ignoreJCenter = true
        when:
            executeUptodateTask()
        then:
            1 * loggerProxy.info(_, { it.contains('JCenter repository is not found')})
    }

    protected void executeUptodateTask() {
        project.tasks.getByName(TASK_NAME).execute()
    }
    
}
