package com.ofg.uptodate.finder
import com.ofg.uptodate.LoggerProxy
import groovy.transform.PackageScope
import groovy.util.logging.Slf4j
import groovy.util.slurpersupport.NodeChild
import groovyx.net.http.HTTPBuilder

import java.util.concurrent.Future

@Slf4j
@PackageScope
class JCenterLatestDependenciesProvider implements LatestDependenciesProvider {
    
    private final LoggerProxy loggerProxy

    JCenterLatestDependenciesProvider(LoggerProxy loggerProxy) {
        this.loggerProxy = loggerProxy
    }
    
    @Override
    List<Dependency> findLatest(List<Dependency> dependencies, FinderConfiguration finderConfiguration) {
        int httpPoolSize = Math.min(dependencies.size(), finderConfiguration.httpConnectionSettings.poolSize)
        HTTPBuilder httpBuilder = new HTTPBuilderProvider(finderConfiguration.httpConnectionSettings).withPoolSize(httpPoolSize).get()
        Closure latestFromJCenterGetter = getLatestFromJCenterRepo.curry(httpBuilder, finderConfiguration.excludedVersionPatterns)
        return dependencies.collect(latestFromJCenterGetter).collect{it.get()}.grep(getOnlyNewer).collect {it[1]}
    }

    public Closure<Future> getLatestFromJCenterRepo = {HTTPBuilder httpBuilder, List<String> versionToExcludePatterns, Dependency dependency ->
        appendFailureHandling(httpBuilder, dependency.name)
        httpBuilder.get(path: "/${dependency.group.split('\\.').join('/')}/${dependency.name}/maven-metadata.xml") { resp, xml ->
            if(!xml) {
                return []
            }
            def releaseVersionNode = xml.versioning.release
            String releaseVersion = releaseVersionNode.text()
            DependencyVersion getFirstNonExcludedVersion = getLatestDependencyVersion(releaseVersion, xml, versionToExcludePatterns)
            return [dependency, new Dependency(dependency, getFirstNonExcludedVersion)]
        } as Future
    }

    private void appendFailureHandling(HTTPBuilder httpBuilder, String dependencyName) {
        httpBuilder.handler.failure = { resp ->
            loggerProxy.debug(log, "Error with status [$resp.status] occurred while trying to download dependency [$dependencyName]")
            return []
        }
    }

    private DependencyVersion getLatestDependencyVersion(String releaseVersion, NodeChild xml, List<String> versionToExcludePatterns) {
        if (versionNotMatchesExcludes(versionToExcludePatterns, releaseVersion)) {
            return new DependencyVersion(releaseVersion)
        }
        return xml.versioning.versions.version.findAll { NodeChild version ->
            versionNotMatchesExcludes(versionToExcludePatterns, version.text())
        }.collect {
            NodeChild version -> new DependencyVersion(version.text())
        }.max()

    }

    private boolean versionNotMatchesExcludes(List<String> versionToExcludePatterns, String version) {
        return versionToExcludePatterns.every {
            !version.matches(it)
        }
    }

    private final Closure<Boolean> getOnlyNewer = { List<Dependency> dependenciesToCompare ->
        !dependenciesToCompare.empty && dependenciesToCompare[1].version != null && dependenciesToCompare[1].version > dependenciesToCompare[0].version
    }
}
