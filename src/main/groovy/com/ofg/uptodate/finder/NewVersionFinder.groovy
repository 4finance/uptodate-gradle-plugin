package com.ofg.uptodate.finder

import com.ofg.uptodate.LoggerProxy
import com.ofg.uptodate.dependency.Dependency
import groovy.util.logging.Slf4j

import java.util.concurrent.Future

@Slf4j
class NewVersionFinder {

    private final LoggerProxy loggerProxy
    private final Closure<Future> latestVersionsCollector
    private final FinderConfiguration finderConfiguration

    NewVersionFinder(LoggerProxy loggerProxy, Closure<Future> latestVersionsCollector, FinderConfiguration finderConfiguration) {
        this.loggerProxy = loggerProxy
        this.latestVersionsCollector = latestVersionsCollector
        this.finderConfiguration = finderConfiguration
    }

    List<Dependency> findNewer(List<Dependency> dependencies) {
        if (finderConfiguration.ignore) {
            return []
        }

        List<Dependency> newerDependencies = new NewerDependencyProvider(latestVersionsCollector, loggerProxy).getFor(dependencies)
        loggerProxy.debug(log, "Newer dependencies found in $finderConfiguration.httpConnectionSettings.url: $newerDependencies")
        return newerDependencies
    }
}
