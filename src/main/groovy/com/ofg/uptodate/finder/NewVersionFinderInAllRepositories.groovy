package com.ofg.uptodate.finder

import com.ofg.uptodate.LoggerProxy
import com.ofg.uptodate.dependency.Dependency
import com.ofg.uptodate.dependency.DependencyGroupAndNameComparator
import groovy.util.logging.Slf4j

@Slf4j
class NewVersionFinderInAllRepositories {

    public static final String NEW_VERSIONS_MESSAGE_HEAD = 'New versions available:'
    public static final String NO_NEW_VERSIONS_MESSAGE = 'No new versions are available.'
    private static final boolean DO_NOT_MUTATE_ORIGINAL_COLLECTION = false

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
        log.debug("Unique newest versions found $uniqueNewestVersions")
        return uniqueNewestVersions
    }

    Comparator<Dependency> byGroupAndName() {
        return { Dependency dependency, Dependency other ->
            if (dependency.hasSameGroupAndNameAs(other)) {
                log.debug("Two dependencies have same group and name. First [$dependency], second [$other]")
                int thisVersionLowerOrEqual = dependency.version <= other.version ? 0 : 1
                log.debug("First dep lower or equal than second [${thisVersionLowerOrEqual == 0}]")
                return thisVersionLowerOrEqual
            }
            return -1
        } as Comparator<Dependency>
    }

    void printDependencies(Set<Dependency> dependenciesWithNewVersions) {
        if (dependenciesWithNewVersions.isEmpty()) {
            loggerProxy.lifecycle(log, NO_NEW_VERSIONS_MESSAGE)
        } else {
            List<Dependency> sortedUpdates = dependenciesWithNewVersions.sort(DO_NOT_MUTATE_ORIGINAL_COLLECTION, new DependencyGroupAndNameComparator())
            loggerProxy.lifecycle(log, "$NEW_VERSIONS_MESSAGE_HEAD\n${sortedUpdates.join('\n')}")
        }    
    }
}
