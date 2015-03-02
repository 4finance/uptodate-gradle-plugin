package com.ofg.uptodate.finder.jcenter
import com.ofg.uptodate.LoggerProxy
import com.ofg.uptodate.finder.Dependency
import com.ofg.uptodate.finder.DependencyVersion
import com.ofg.uptodate.finder.FinderConfiguration
import com.ofg.uptodate.finder.util.HTTPBuilderProvider
import com.ofg.uptodate.finder.LatestDependenciesProvider
import com.ofg.uptodate.finder.util.StringMatcher
import groovy.transform.PackageScope
import groovy.util.logging.Slf4j
import groovy.util.slurpersupport.NodeChild
import groovyx.net.http.HTTPBuilder

import java.util.concurrent.Future

import static com.ofg.uptodate.finder.util.HTTPBuilderProvider.FailureHandlers.logOnlyFailureHandler

@Slf4j
@PackageScope
class JCenterLatestDependenciesProvider implements LatestDependenciesProvider {
    
    private final LoggerProxy loggerProxy

    JCenterLatestDependenciesProvider(LoggerProxy loggerProxy) {
        this.loggerProxy = loggerProxy
    }
    
    @Override
    List<Dependency> findLatest(List<Dependency> dependencies, FinderConfiguration finderConfiguration) {
        HTTPBuilder httpBuilder = new HTTPBuilderProvider(finderConfiguration.httpConnectionSettings).getWithPoolSizeFor(dependencies)
        Closure latestFromJCenterGetter = getLatestFromJCenterRepo.curry(httpBuilder, finderConfiguration.excludedVersionPatterns)
        return dependencies.collect(latestFromJCenterGetter).collect{it.get()}
    }

    public Closure<Future> getLatestFromJCenterRepo = {HTTPBuilder httpBuilder, List<String> versionToExcludePatterns, Dependency dependency ->
        httpBuilder.handler.failure = logOnlyFailureHandler(loggerProxy, log, dependency.name)
        httpBuilder.get(path: "/${dependency.group.split('\\.').join('/')}/${dependency.name}/maven-metadata.xml") { resp, xml ->
            if(!xml) {
                return []
            }
            return [dependency, new Dependency(dependency, getLatestDependencyVersion(xml.versioning.release.text(), xml, versionToExcludePatterns))]
        } as Future
    }

    private DependencyVersion getLatestDependencyVersion(String releaseVersion, NodeChild xml, List<String> versionToExcludePatterns) {
        if (new StringMatcher(releaseVersion).notMatchesAny(versionToExcludePatterns)) {
            return new DependencyVersion(releaseVersion)
        }
        return xml.versioning.versions.version.findAll { NodeChild version ->
            new StringMatcher(version.text()).notMatchesAny(versionToExcludePatterns)
        }.collect {
            NodeChild version -> new DependencyVersion(version.text())
        }.max()
    }
}
