package com.ofg.uptodate.finder
import com.ofg.uptodate.LoggerProxy
import com.ofg.uptodate.dependency.Dependency
import groovy.util.logging.Slf4j

import java.util.concurrent.Future

@Slf4j
class NewerDependencyProvider {
    
    private final LoggerProxy loggerProxy
    private final Closure<Future> latestVersionsCollector

    NewerDependencyProvider(Closure<Future> latestVersionsCollector, LoggerProxy loggerProxy) {
        this.loggerProxy = loggerProxy
        this.latestVersionsCollector = latestVersionsCollector
    }
    
    List<Dependency> getFor(List<Dependency> dependencies) {
        return dependencies.collect(latestVersionsCollector).collect { it.get() }.grep(getOnlyNewer).collect { it[1] }
    }

    private Closure<Boolean> getOnlyNewer = { List<Dependency> dependenciesToCompare ->
        loggerProxy.debug(log, "Dependencies to get only newer: $dependenciesToCompare")
        if (dependenciesToCompare.empty) {
            loggerProxy.debug(log, "Dependencies to compare are empty - sth went wrong so no newer version was found")
            return false
        }
        if (!dependenciesToCompare[1].version) {
            loggerProxy.debug(log, "The retrieved dependency has null version value thus no newer version was found")
            return false
        }
        boolean fetchedDependencyHasGreaterVersion = dependenciesToCompare[1].version > dependenciesToCompare[0].version
        loggerProxy.debug(log, "Fetched dependency has greater version than the current one [$fetchedDependencyHasGreaterVersion]")
        return fetchedDependencyHasGreaterVersion
    }
}
