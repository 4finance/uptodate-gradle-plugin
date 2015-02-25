package com.ofg.uptodate.finder
import com.ofg.uptodate.LoggerProxy
import com.ofg.uptodate.UptodatePluginExtension
import groovy.util.logging.Slf4j
import groovyx.net.http.AsyncHTTPBuilder
import groovyx.net.http.HTTPBuilder

import java.util.concurrent.Future

import static com.ofg.uptodate.UrlEspaceUtils.escape

@Slf4j
class MavenNewVersionFinder implements NewVersionFinder {

    public static final String MAVEN_CENTRAL_REPO_URL = "http://search.maven.org/solrsearch/select"
    private final String mavenUrl
    private final int maxHttpConnectionsPoolSize
    private final int connectionTimeout
    private final List<String> versionToExcludePatterns
    private final boolean ignoreMaven
    private final LoggerProxy loggerProxy
    private final String proxyHost
    private final int proxyPort
    private final String proxyScheme

    MavenNewVersionFinder(LoggerProxy loggerProxy, UptodatePluginExtension uptodatePluginExtension) {
        mavenUrl = uptodatePluginExtension.mavenRepo
        maxHttpConnectionsPoolSize = uptodatePluginExtension.simultaneousHttpConnections
        connectionTimeout = uptodatePluginExtension.connectionTimeout
        versionToExcludePatterns = uptodatePluginExtension.excludedVersionPatterns
        ignoreMaven = uptodatePluginExtension.ignoreMavenCentral
        proxyHost = uptodatePluginExtension.proxyHostname
        proxyPort = uptodatePluginExtension.proxyPort
        proxyScheme = uptodatePluginExtension.proxyScheme
        this.loggerProxy = loggerProxy
    }

    @Override
    List<Dependency> findNewer(List<Dependency> dependencies) {
        if (ignoreMaven) {
            return []
        } else if(dependencies.empty) {
            return []
        }
        List<Dependency> newerDependencies = findNewerInMavenCentralRepo(dependencies)
        loggerProxy.debug(log, "Newer dependencies found in Maven Central $newerDependencies")
        return newerDependencies
    }

    private List<Dependency> findNewerInMavenCentralRepo(List<Dependency> dependencies) {
        int httpPoolSize = Math.min(dependencies.size(), maxHttpConnectionsPoolSize)
        HTTPBuilder httpBuilder = new AsyncHTTPBuilder(timeout: connectionTimeout, poolSize: httpPoolSize, uri: mavenUrl)
        setProxyIfApplicable(httpBuilder)
        Closure latestFromMavenGetter = getLatestFromMavenCentralRepo.curry(httpBuilder, versionToExcludePatterns)
        return dependencies.collect(latestFromMavenGetter).collect{it.get()}.grep(getOnlyNewer).collect {it[1]}
    }

    private setProxyIfApplicable(HTTPBuilder httpBuilder) {
        if (proxyHost) {
            httpBuilder.setProxy(proxyHost, proxyPort, proxyScheme)
        }
    }

    private final Closure<Future> getLatestFromMavenCentralRepo = {HTTPBuilder httpBuilder, List<String> versionToExcludePatterns, Dependency dependency ->
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

    private final Closure<Boolean> getOnlyNewer = { List<Dependency> dependenciesToCompare ->
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
