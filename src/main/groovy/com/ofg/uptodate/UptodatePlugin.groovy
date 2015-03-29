package com.ofg.uptodate

import com.ofg.uptodate.finder.dependency.Dependency
import com.ofg.uptodate.finder.jcenter.JCenterNewVersionFinderFactory
import com.ofg.uptodate.finder.maven.MavenNewVersionFinderFactory
import com.ofg.uptodate.finder.NewVersionFinderInAllRepositories
import groovy.util.logging.Slf4j
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.artifacts.Configuration

import javax.inject.Inject

@Slf4j
class UptodatePlugin implements Plugin<Project> {
    public static final String TASK_NAME = 'uptodate'
    public static final String GRADLE_BINTRAY_JCENTER_REPO_NAME = 'BintrayJCenter'
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
        UptodatePluginExtension uptodatePluginExtension = project.extensions.uptodate
        Task createdTask = project.task(TASK_NAME) << { Task task ->
            printMissingJCenterRepoIfApplicable(uptodatePluginExtension, project)
            List<Dependency> dependencies = getDependencies(project)
            if (dependencies) {
                NewVersionFinderInAllRepositories newVersionFinder = new NewVersionFinderInAllRepositories(loggerProxy,
                        [new MavenNewVersionFinderFactory(loggerProxy).create(uptodatePluginExtension, dependencies),
                         new JCenterNewVersionFinderFactory(loggerProxy).create(uptodatePluginExtension, dependencies)])
                Set<Dependency> dependenciesWithNewVersions = newVersionFinder.findNewer(dependencies)
                newVersionFinder.printDependencies(dependenciesWithNewVersions)
            } else {
                loggerProxy.info(log, 'No dependencies found in project configuration.')
            }
        }
        createdTask.group = "Dependencies"
        createdTask.description = "Checks your dependencies against provided repositories (defaults to Maven Central and JCenter)"
    }

    private void printMissingJCenterRepoIfApplicable(UptodatePluginExtension uptodatePluginExtension, Project project) {
        if (uptodatePluginExtension.showMissingJCenterMessage && !jCenterRepositoryIsPresent(project)) {
            loggerProxy.info(log, '''JCenter repository is not found in the configured repositories.
                                     You may consider setting it up as follows:
                                                
                                     repositories {
                                         jcenter()
                                     }                                    
                                  ''')
        }
    }

    private boolean jCenterRepositoryIsPresent(Project project) {
        return project.repositories.find {
            it.name == GRADLE_BINTRAY_JCENTER_REPO_NAME
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
            conf.dependencies.findAll { dep -> dep.name && dep.group && dep.version }.collect { dep ->
                log.debug("Collecting dependency with group: [$dep.group] name: [$dep.name] and version: [$dep.version]")
                new Dependency(dep.group, dep.name, dep.version)
            }
        }.flatten().unique()
    }
}
