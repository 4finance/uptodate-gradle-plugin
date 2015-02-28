package com.ofg.uptodate.finder

import com.ofg.uptodate.LoggerProxy
import groovy.transform.PackageScope
import groovy.util.logging.Slf4j
import groovyx.net.http.HTTPBuilder

import java.util.concurrent.Future

import static com.ofg.uptodate.UrlEspaceUtils.escape

@Slf4j
@PackageScope
class MavenLatestDependenciesProvider implements LatestDependenciesProvider {
    
    private final LoggerProxy loggerProxy

    MavenLatestDependenciesProvider(LoggerProxy loggerProxy) {
        this.loggerProxy = loggerProxy
    }
    
    @Override
    List<Dependency> findLatest(List<Dependency> dependencies, FinderConfiguration finderConfiguration) {
        int httpPoolSize = Math.min(dependencies.size(), finderConfiguration.httpConnectionSettings.poolSize)
        HTTPBuilder httpBuilder = new HTTPBuilderProvider(finderConfiguration.httpConnectionSettings).withPoolSize(httpPoolSize).get()
        Closure latestFromMavenGetter = getLatestFromMavenCentralRepo.curry(httpBuilder, finderConfiguration.excludedVersionPatterns)
        return dependencies.collect(latestFromMavenGetter).collect{it.get()}.grep(getOnlyNewer).collect {it[1]}
    }

    private Closure<Future> getLatestFromMavenCentralRepo = {HTTPBuilder httpBuilder, List<String> versionToExcludePatterns, Dependency dependency ->
        String listVersionsForGroupAndArtifactQuery = "q=${escape("g:\"$dependency.group\"")}+AND+${escape("a:\"$dependency.name\"")}&core=gav&rows10&wt=json".toString()
        httpBuilder.get(queryString: listVersionsForGroupAndArtifactQuery) { resp, json ->
            if(!json) {
                return []
            }
            DependencyVersion firstNonExcludedVersion = json.response.docs.findAll { doc ->
                versionToExcludePatterns.every {
                    !doc.v.matches(it)
                }
            }.collect { new DependencyVersion(it.v)}.max()
            return [dependency, new Dependency(dependency, firstNonExcludedVersion)]
        }
    }

    private Closure<Boolean> getOnlyNewer = { List<Dependency> dependenciesToCompare ->
        loggerProxy.debug(log, "Maven Central Dependencies to get only newer $dependenciesToCompare")
        boolean dependenciesToCompareEmpty = dependenciesToCompare.empty
        if (dependenciesToCompareEmpty) {
            loggerProxy.debug(log, "Dependencies to compare are empty - sth went wrong so no newer version was found")
            return false
        }
        boolean foundInRepoVersionHasNullVersion = dependenciesToCompare[1].version == null
        if (foundInRepoVersionHasNullVersion) {
            loggerProxy.debug(log, "The retrieved dependency has null version value thus no newer version was found")
            return false
        }
        boolean fetchedDependencyHasGreaterVersion = dependenciesToCompare[1].version > dependenciesToCompare[0].version
        loggerProxy.debug(log, "Fetched dependency has greater version than the current one [$fetchedDependencyHasGreaterVersion]")
        return  fetchedDependencyHasGreaterVersion
    }
}
