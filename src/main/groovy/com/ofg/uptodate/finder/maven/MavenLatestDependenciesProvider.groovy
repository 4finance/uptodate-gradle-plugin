package com.ofg.uptodate.finder.maven

import com.ofg.uptodate.LoggerProxy
import com.ofg.uptodate.finder.Dependency
import com.ofg.uptodate.finder.DependencyVersion
import com.ofg.uptodate.finder.FinderConfiguration
import com.ofg.uptodate.finder.util.HTTPBuilderProvider
import com.ofg.uptodate.finder.LatestDependenciesProvider
import com.ofg.uptodate.finder.util.StringMatcher
import groovy.transform.PackageScope
import groovy.util.logging.Slf4j
import groovyx.net.http.HTTPBuilder

import java.util.concurrent.Future

import static com.ofg.uptodate.UrlEspaceUtils.escape
import static com.ofg.uptodate.finder.util.HTTPBuilderProvider.FailureHandlers.logOnlyFailureHandler

@Slf4j
@PackageScope
class MavenLatestDependenciesProvider implements LatestDependenciesProvider {

    private final LoggerProxy loggerProxy

    MavenLatestDependenciesProvider(LoggerProxy loggerProxy) {
        this.loggerProxy = loggerProxy
    }

    @Override
    List<Dependency> findLatest(List<Dependency> dependencies, FinderConfiguration finderConfiguration) {
        HTTPBuilder httpBuilder = new HTTPBuilderProvider(finderConfiguration.httpConnectionSettings).getWithPoolSizeFor(dependencies)
        Closure latestFromMavenGetter = getLatestFromMavenCentralRepo.curry(httpBuilder, finderConfiguration.excludedVersionPatterns)
        return dependencies.collect(latestFromMavenGetter).collect { it.get() }
    }

    private Closure<Future> getLatestFromMavenCentralRepo = { HTTPBuilder httpBuilder, List<String> versionToExcludePatterns, Dependency dependency ->
        httpBuilder.handler.failure = logOnlyFailureHandler(loggerProxy, log, dependency.name)
        httpBuilder.get(queryString: "q=${escape("g:\"$dependency.group\"")}+AND+${escape("a:\"$dependency.name\"")}&core=gav&rows10&wt=json".toString()) { resp, json ->
            if (!json) {
                return []
            }
            DependencyVersion latestNonExcludedVersion = json.response.docs.findAll { doc ->
                new StringMatcher(doc.v).notMatchesAny(versionToExcludePatterns)
            }.collect { new DependencyVersion(it.v) }.max()
            return [dependency, new Dependency(dependency, latestNonExcludedVersion)]
        }
    }
}
