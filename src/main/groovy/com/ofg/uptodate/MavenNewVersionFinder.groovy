package com.ofg.uptodate

import groovyx.net.http.AsyncHTTPBuilder
import groovyx.net.http.HTTPBuilder

import java.util.concurrent.Future

import static com.ofg.uptodate.UrlEspaceUtils.escape

class MavenNewVersionFinder implements NewVersionFinder {

    static final String MAVEN_CENTRAL_REPO_URL = "http://search.maven.org/solrsearch/select"
    private final String mavenUrl
    private final int maxHttpConnectionsPoolSize
    private final int connectionTimeout
    private final List<String> versionToExcludePatterns

    MavenNewVersionFinder(UptodatePluginExtension uptodatePluginExtension) {
        mavenUrl = uptodatePluginExtension.mavenRepo
        maxHttpConnectionsPoolSize = uptodatePluginExtension.simultaneousHttpConnections
        connectionTimeout = uptodatePluginExtension.connectionTimeout
        versionToExcludePatterns = uptodatePluginExtension.versionToExcludePatterns
    }

    @Override
    List<Dependency> findNewer(List<Dependency> dependencies) {
        return dependencies.isEmpty() ? [] : findNewerInMavenCentralRepo(dependencies)
    }

    private List<Dependency> findNewerInMavenCentralRepo(List<Dependency> dependencies) {
        int httpPoolSize = Math.min(dependencies.size(), maxHttpConnectionsPoolSize)
        HTTPBuilder httpBuilder = new AsyncHTTPBuilder(timeout: connectionTimeout, poolSize: httpPoolSize, uri: mavenUrl)
        Closure latestFromMavenGetter = getLatestFromMavenCentralRepo.curry(httpBuilder, versionToExcludePatterns)
        return dependencies.collect(latestFromMavenGetter).collect{it.get()}.grep(getOnlyNewer).collect {it[1]}
    }

    public static final Closure<Future> getLatestFromMavenCentralRepo = {HTTPBuilder httpBuilder, List<String> versionToExcludePatterns, Dependency dependency ->
        String listVersionsForGroupAndArtifactQuery = "q=${escape("g:\"$dependency.group\"")}+AND+${escape("a:\"$dependency.name\"")}&core=gav&rows10&wt=json".toString()
        httpBuilder.get(queryString: listVersionsForGroupAndArtifactQuery) { resp, json ->
            DependencyVersion firstNonExcludedVersion = json.response.docs.findAll { doc ->
                versionToExcludePatterns.every {
                    !doc.v.matches(it)
                }
            }.collect { new DependencyVersion(it.v)}.max()
            return [dependency, new Dependency(dependency, firstNonExcludedVersion)]
        }
    }

    public static final Closure<Boolean> getOnlyNewer = { List<Dependency> dependenciesToCompare ->
        dependenciesToCompare[1].version != null && dependenciesToCompare[1].version > dependenciesToCompare[0].version
    }


}
