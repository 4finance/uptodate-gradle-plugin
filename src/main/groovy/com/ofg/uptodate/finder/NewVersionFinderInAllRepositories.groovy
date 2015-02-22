package com.ofg.uptodate.finder

import com.ofg.uptodate.LoggerProxy
import groovy.util.logging.Slf4j

@Slf4j
class NewVersionFinderInAllRepositories {

    public static final String NEW_VERSIONS_MESSAGE_HEAD = 'New versions available:\n'
    public static final String NO_NEW_VERSIONS_MESSAGE = 'No new versions are available.'

    private final List<NewVersionFinder> newVersionFinders
    private final LoggerProxy loggerProxy
    
    NewVersionFinderInAllRepositories(LoggerProxy loggerProxy, List<NewVersionFinder> newVersionFinders) {
        this.newVersionFinders = newVersionFinders
        this.loggerProxy = loggerProxy
    }

    Set<Dependency> findNewer(List<Dependency> dependencies) {
        List<Dependency> newestVersions = []
        newVersionFinders.each {
            newestVersions << it.findNewer(dependencies)            
        }
        Set<Dependency> uniqueNewestVersions = newestVersions.flatten().unique(byGroupAndName()).toSet()
        loggerProxy.debug(log, "Unique newest versions found $uniqueNewestVersions")
        return uniqueNewestVersions
    }

    Comparator<Dependency> byGroupAndName() {
        return { Dependency dependency, Dependency other ->
            if (dependency.hasSameGroupAndNameAs(other)) {
                loggerProxy.debug(log, "Two dependencies have same group and name. First [$dependency], second [$other]")
                int thisVersionLowerOrEqual = dependency.version <= other.version ? 0 : 1
                loggerProxy.debug(log, "First dep lower or equal than second [${thisVersionLowerOrEqual == 0}]")
                return thisVersionLowerOrEqual
            }
            return -1
        } as Comparator<Dependency>
    }

    void printDependencies(Set<Dependency> dependenciesWithNewVersions) {
        if (dependenciesWithNewVersions.isEmpty()) {
            loggerProxy.info(log, NO_NEW_VERSIONS_MESSAGE)
        } else {
            loggerProxy.warn(log, NEW_VERSIONS_MESSAGE_HEAD + dependenciesWithNewVersions.join('\n'))
        }    
    }
}
