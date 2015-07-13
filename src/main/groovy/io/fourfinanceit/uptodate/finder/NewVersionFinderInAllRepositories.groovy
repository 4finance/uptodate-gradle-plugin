package io.fourfinanceit.uptodate.finder

import io.fourfinanceit.uptodate.dependency.Dependency
import groovy.util.logging.Slf4j
import io.fourfinanceit.uptodate.LoggerProxy

@Slf4j
class NewVersionFinderInAllRepositories {

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
}
