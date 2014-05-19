package com.ofg.uptodate

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task

import javax.inject.Inject

class UptodatePlugin implements Plugin<Project> {
    static final String TASK_NAME = 'uptodate'
    static final String NEW_VERSIONS_MESSAGE_HEAD = 'New versions available in maven central:\n'
    static final String NO_NEW_VERSIONS_MESSAGE = 'No new versions are available in maven central.'
    private final LoggerProxy loggerProxy

    @Inject
    UptodatePlugin() {
        loggerProxy = new LoggerProxy()
    }

    UptodatePlugin(LoggerProxy loggerProxy) {
        this.loggerProxy = loggerProxy
    }

    @Override
    void apply(Project project) {
        project.extensions.create(TASK_NAME, UptodatePluginConfiguration)

        project.task(TASK_NAME) << { Task task ->
            NewVersionFinder newVersionFinder = new MavenNewVersionFinder(project.extensions.uptodate.mavenRepo)
            List<Dependency> dependencies = getDependencies(project)
            List<Dependency> dependenciesWithNewVersions = newVersionFinder.findNewer(dependencies)
            if (dependenciesWithNewVersions.isEmpty()) {
                loggerProxy.info(task.getLogger(), NO_NEW_VERSIONS_MESSAGE)
            } else {
                loggerProxy.warn(task.getLogger(), NEW_VERSIONS_MESSAGE_HEAD + dependenciesWithNewVersions.join('\n'))
            }
        }
    }

    private List<Dependency> getDependencies(Project project) {
        return project.configurations.collectNested { conf ->
            conf.allDependencies.collect { dep -> new Dependency(dep.group, dep.name, dep.version) }
        }.flatten().unique()
    }
}
