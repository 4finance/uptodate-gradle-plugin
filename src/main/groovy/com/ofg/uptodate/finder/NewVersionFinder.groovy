package com.ofg.uptodate.finder

import com.ofg.uptodate.LoggerProxy
import groovy.util.logging.Slf4j

@Slf4j
class NewVersionFinder {

    private final LoggerProxy loggerProxy
    private final LatestDependenciesProvider latestDependenciesProvider
    private final FinderConfiguration finderConfiguration

    NewVersionFinder(LoggerProxy loggerProxy, LatestDependenciesProvider latestDependenciesProvider, FinderConfiguration finderConfiguration) {
        this.loggerProxy = loggerProxy
        this.latestDependenciesProvider = latestDependenciesProvider
        this.finderConfiguration = finderConfiguration
    }

    List<Dependency> findNewer(List<Dependency> dependencies) {
        if (finderConfiguration.ignore || dependencies.empty) {
            return []
        }

        List<Dependency> newerDependencies = latestDependenciesProvider.findLatest(dependencies, finderConfiguration).grep(getOnlyNewer).collect {it[1]}
        loggerProxy.debug(log, "Newer dependencies found in $finderConfiguration.httpConnectionSettings.url $newerDependencies")
        return newerDependencies
    }

    private Closure<Boolean> getOnlyNewer = { List<Dependency> dependenciesToCompare ->
        loggerProxy.debug(log, "${finderConfiguration.httpConnectionSettings.url} - Dependencies to get only newer $dependenciesToCompare")
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
