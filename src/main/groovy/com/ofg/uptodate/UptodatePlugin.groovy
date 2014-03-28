package com.ofg.uptodate

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task

class UptodatePlugin implements Plugin<Project> {
    static final String TASK_NAME = 'uptodate'
    static final String NEW_VERSIONS_MESSAGE_HEAD = 'New versions available in maven central:\n'
    private final LoggerProxy loggerProxy

    UptodatePlugin() {
        loggerProxy = new LoggerProxy()
    }

    UptodatePlugin(LoggerProxy loggerProxy) {
        this.loggerProxy = loggerProxy
    }

    @Override
    void apply(Project project) {
        project.extensions.create("uptodate", UptodatePluginConfiguration)

        project.task(TASK_NAME) << { Task task ->
            NewVersionFinder newVersionFinder = new MavenNewVersionFinder(project.extensions.uptodate.mavenRepo)
            List<Dependency> dependencies = getDependencies(project)
            List<Dependency> dependenciesWithNewVersions = newVersionFinder.findNewer(dependencies)
            loggerProxy.warn(task.getLogger(), NEW_VERSIONS_MESSAGE_HEAD + dependenciesWithNewVersions.join('\n'))
        }
    }

    private List<Dependency> getDependencies(Project project) {
        return project.configurations.collectNested { conf ->
            conf.allDependencies.collect { dep ->
                new Dependency(conf.name, dep.group, dep.name, dep.version)
        }}.flatten()
    }


}
