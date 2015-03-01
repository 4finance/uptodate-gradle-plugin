package com.ofg.uptodate.finder

import com.ofg.uptodate.LoggerProxy
import com.ofg.uptodate.UptodatePluginExtension
import groovy.util.logging.Slf4j

@Slf4j
class NewVersionFinder {

    private final LatestDependenciesProvider latestDependenciesProvider
    private final FinderConfiguration finderConfiguration
    private final LoggerProxy loggerProxy

    NewVersionFinder(LoggerProxy loggerProxy,
                     RepositorySettingsProvider repositorySettingsProvider,
                     LatestDependenciesProvider latestDependenciesProvider,
                     UptodatePluginExtension uptodatePluginExtension) {

        finderConfiguration = new FinderConfiguration(repositorySettingsProvider.getFrom(uptodatePluginExtension), uptodatePluginExtension)
        this.latestDependenciesProvider = latestDependenciesProvider
        this.loggerProxy = loggerProxy
    }

    List<Dependency> findNewer(List<Dependency> dependencies) {
        if (finderConfiguration.ignore) {
            return []
        } else if (dependencies.empty) {
            return []
        }

        List<Dependency> newerDependencies = latestDependenciesProvider.findLatest(dependencies, finderConfiguration)
        loggerProxy.debug(log, "Newer dependencies found in $finderConfiguration.httpConnectionSettings.url $newerDependencies")
        return newerDependencies
    }
}
