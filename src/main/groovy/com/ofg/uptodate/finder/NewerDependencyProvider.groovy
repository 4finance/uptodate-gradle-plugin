package com.ofg.uptodate.finder

import com.ofg.uptodate.dependency.Dependency
import groovy.util.logging.Slf4j

import java.util.concurrent.Future

@Slf4j
class NewerDependencyProvider {
    
    private final Closure<Future> latestVersionsCollector

    NewerDependencyProvider(Closure<Future> latestVersionsCollector) {
        this.latestVersionsCollector = latestVersionsCollector
    }
    
    List<Dependency> getFor(List<Dependency> dependencies) {
        return dependencies.collect(latestVersionsCollector).collect { it.get() }.grep(getOnlyNewer).collect { it[1] }
    }

    private Closure<Boolean> getOnlyNewer = { List<Dependency> dependenciesToCompare ->
        log.debug("Dependencies to get only newer: $dependenciesToCompare")
        if (dependenciesToCompare.empty) {
            log.debug('Dependencies to compare are empty - sth went wrong so no newer version was found')
            return false
        }
        if (!dependenciesToCompare[1].version) {
            log.debug('The retrieved dependency has null version value thus no newer version was found')
            return false
        }
        boolean fetchedDependencyHasGreaterVersion = dependenciesToCompare[1].version > dependenciesToCompare[0].version
        log.debug("Fetched dependency has greater version than the current one [$fetchedDependencyHasGreaterVersion]")
        return fetchedDependencyHasGreaterVersion
    }
}
