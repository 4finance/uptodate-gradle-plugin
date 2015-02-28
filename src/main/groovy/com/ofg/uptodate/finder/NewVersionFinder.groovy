package com.ofg.uptodate.finder

import com.ofg.uptodate.LoggerProxy
import com.ofg.uptodate.UptodatePluginExtension
import groovy.util.logging.Slf4j

@Slf4j
class NewVersionFinder {

    private final LatestDependenciesProvider latestDependenciesProvider
    private final FinderConfiguration finderConfiguration
    private final LoggerProxy loggerProxy

    private NewVersionFinder(LoggerProxy loggerProxy,
                             RepositorySettingsProvider repositorySettingsProvider,
                             LatestDependenciesProvider latestDependenciesProvider,
                             UptodatePluginExtension uptodatePluginExtension) {

        RepositorySettings repositorySettings = repositorySettingsProvider.getFrom(uptodatePluginExtension)
        finderConfiguration = new FinderConfiguration(
                ignore: repositorySettings.ignoreRepo,
                httpConnectionSettings: new HttpConnectionSettings(
                        url: repositorySettings.repoUrl,
                        proxySettings: new ProxySettings(
                                hostname: uptodatePluginExtension.proxyHostname,
                                port: uptodatePluginExtension.proxyPort,
                                scheme: uptodatePluginExtension.proxyScheme
                        ),
                        poolSize: uptodatePluginExtension.simultaneousHttpConnections,
                        timeout: uptodatePluginExtension.connectionTimeout
                ),
                excludedVersionPatterns: uptodatePluginExtension.excludedVersionPatterns
        )
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

    static NewVersionFinder mavenNewVersionFinder(LoggerProxy loggerProxy, UptodatePluginExtension uptodatePluginExtension) {
        return new NewVersionFinder(loggerProxy, new MavenRepositorySettingsProvider(), new MavenLatestDependenciesProvider(loggerProxy), uptodatePluginExtension);
    }

    static NewVersionFinder jCenterNewVersionFinder(LoggerProxy loggerProxy, UptodatePluginExtension uptodatePluginExtension) {
        return new NewVersionFinder(loggerProxy, new JCenterRepositorySettingsProvider(), new JCenterLatestDependenciesProvider(loggerProxy), uptodatePluginExtension);
    }
}
