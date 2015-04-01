package com.ofg.uptodate.finder

import com.ofg.uptodate.dependency.Dependency
import groovy.util.logging.Slf4j

import java.util.concurrent.Future

@Slf4j
class NewVersionFinder {

    private final Closure<Future> latestVersionsCollector
    private final FinderConfiguration finderConfiguration

    NewVersionFinder(Closure<Future> latestVersionsCollector, FinderConfiguration finderConfiguration) {
        this.latestVersionsCollector = latestVersionsCollector
        this.finderConfiguration = finderConfiguration
    }

    List<Dependency> findNewer(List<Dependency> dependencies) {
        if (finderConfiguration.ignore) {
            return []
        }

        List<Dependency> newerDependencies = new NewerDependencyProvider(latestVersionsCollector).getFor(dependencies)
        log.debug("Newer dependencies found in $finderConfiguration.httpConnectionSettings.url: $newerDependencies")
        return newerDependencies
    }
}
