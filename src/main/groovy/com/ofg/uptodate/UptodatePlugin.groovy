package com.ofg.uptodate

import groovy.util.logging.Slf4j
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.artifacts.Configuration

import javax.inject.Inject

@Slf4j
class UptodatePlugin implements Plugin<Project> {
    static final String TASK_NAME = 'uptodate'
    static final String NEW_VERSIONS_MESSAGE_HEAD = 'New versions available in Maven Central:\n'
    static final String NO_NEW_VERSIONS_MESSAGE = 'No new versions are available in Maven Central.'
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
        project.extensions.create(TASK_NAME, UptodatePluginExtension)
        project.task(TASK_NAME) << { Task task ->
            NewVersionFinder newVersionFinder = new MavenNewVersionFinder(project.extensions.uptodate)
            List<Dependency> dependencies = getDependencies(project)
            List<Dependency> dependenciesWithNewVersions = newVersionFinder.findNewer(dependencies)
            if (dependenciesWithNewVersions.isEmpty()) {
                loggerProxy.info(task.logger, NO_NEW_VERSIONS_MESSAGE)
            } else {
                loggerProxy.warn(task.logger, NEW_VERSIONS_MESSAGE_HEAD + dependenciesWithNewVersions.join('\n'))
            }
        }
    }

    private List<Dependency> getDependencies(Project project) {
        ConfigurationFilter configurationFilter = new ConfigurationFilter(project)
        Set<Configuration> configurations = configurationFilter.getConfigurations(project.extensions.uptodate.configurations)
        return getDependencies(configurations)
    }

    private List<Dependency> getDependencies(Set<Configuration> configurations) {
        log.debug("Getting dependencies for configurations [$configurations]")
        return configurations.collectNested { conf ->            
            conf.dependencies.collect { dep ->
                log.debug("Collecting dependency with group: [$dep.group] name: [$dep.name] and version: [$dep.version]")    
                new Dependency(dep.group, dep.name, dep.version) 
            }
        }.flatten().unique()
    }
}
