package com.ofg.uptodate.finder

import com.ofg.uptodate.LoggerProxy
import com.ofg.uptodate.UptodatePluginExtension
import groovy.util.logging.Slf4j
import groovy.util.slurpersupport.NodeChild
import groovyx.net.http.AsyncHTTPBuilder
import groovyx.net.http.HTTPBuilder

import java.util.concurrent.Future

@Slf4j
class JCenterNewVersionFinder implements NewVersionFinder {
    
    public static final String JCENTER_REPO_URL = "http://jcenter.bintray.com/"
    private final int maxHttpConnectionsPoolSize
    private final int connectionTimeout
    private final List<String> versionToExcludePatterns
    private final boolean ignoreJCenter
    private final String jCenterRepo
    private final LoggerProxy loggerProxy
    private final String proxyHost
    private final int proxyPort
    private final String proxyScheme

    JCenterNewVersionFinder(LoggerProxy loggerProxy, UptodatePluginExtension uptodatePluginExtension) {
        maxHttpConnectionsPoolSize = uptodatePluginExtension.simultaneousHttpConnections
        connectionTimeout = uptodatePluginExtension.connectionTimeout
        versionToExcludePatterns = uptodatePluginExtension.excludedVersionPatterns
        ignoreJCenter = uptodatePluginExtension.ignoreJCenter
        jCenterRepo = uptodatePluginExtension.jCenterRepo
        proxyHost = uptodatePluginExtension.proxyHostname
        proxyPort = uptodatePluginExtension.proxyPort
        proxyScheme = uptodatePluginExtension.proxyScheme
        this.loggerProxy = loggerProxy
    }

    @Override
    List<Dependency> findNewer(List<Dependency> dependencies) {
        if (ignoreJCenter) {
            return []
        } else if (dependencies.empty) {
            return []
        }
        List<Dependency> newerDependencies = findNewerInJCenter(dependencies)
        loggerProxy.debug(log, "Newer dependencies found in JCenter $newerDependencies")
        return newerDependencies
    }
    
    private List<Dependency> findNewerInJCenter(List<Dependency> dependencies) {
        int httpPoolSize = Math.min(dependencies.size(), maxHttpConnectionsPoolSize)        
        HTTPBuilder httpBuilder = new AsyncHTTPBuilder(timeout: connectionTimeout, poolSize: httpPoolSize, uri: jCenterRepo)
        if (proxyHost) {
            httpBuilder.setProxy(proxyHost, proxyPort, proxyScheme)
        }
        Closure latestFromMavenGetter = getLatestFromJCenterRepo.curry(httpBuilder, versionToExcludePatterns, loggerProxy)
        return dependencies.collect(latestFromMavenGetter).collect{ it.get() }.grep(getOnlyNewer).collect { it[1] }
    }

    public final Closure<Future> getLatestFromJCenterRepo = {HTTPBuilder httpBuilder, List<String> versionToExcludePatterns, LoggerProxy loggerProxy, Dependency dependency ->
        appendFailureHandling(httpBuilder, loggerProxy, dependency.name)
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

    private void appendFailureHandling(HTTPBuilder httpBuilder, LoggerProxy loggerProxy, String dependencyName) {
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
