package com.ofg.uptodate

import groovyx.net.http.AsyncHTTPBuilder
import groovyx.net.http.HTTPBuilder

import java.util.concurrent.Future

class MavenNewVersionFinder implements NewVersionFinder {

    @Override
    List<Dependency> findNewer(List<Dependency> dependencies) {
        HTTPBuilder httpBuilder = new AsyncHTTPBuilder(poolSize: dependencies.size(), uri: "http://search.maven.org/solrsearch/select")
        Closure latestFromMavenGetter = getLatestFromMavenCentralRepo.curry(httpBuilder)
        return dependencies.collect(latestFromMavenGetter).collect{it.get()}.grep(getOnlyNewer).collect {it[1]}
    }

    public static final Closure<Future> getLatestFromMavenCentralRepo = {HTTPBuilder httpBuilder, Dependency dependency ->
        httpBuilder.get(query: [q: "id:\"$dependency.group:$dependency.name\"", wt: "json"]) { resp, json ->
            [dependency, new Dependency(dependency, json.response.docs.first().latestVersion)]
        }
    }
    public static final Closure<Boolean> getOnlyNewer = { List<Dependency> dependenciesToCompare ->
        dependenciesToCompare[0].version != dependenciesToCompare[1].version
    }
}
