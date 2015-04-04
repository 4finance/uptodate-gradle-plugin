package com.ofg.uptodate.finder.maven

import com.ofg.uptodate.UptodatePluginExtension
import com.ofg.uptodate.dependency.Dependency
import com.ofg.uptodate.dependency.Version
import com.ofg.uptodate.dependency.VersionPatternMatcher
import com.ofg.uptodate.finder.NewVersionFinder
import com.ofg.uptodate.finder.NewVersionFinderFactory
import com.ofg.uptodate.finder.http.HTTPBuilderProvider
import com.ofg.uptodate.finder.FinderConfiguration
import com.ofg.uptodate.finder.RepositorySettings
import groovy.transform.PackageScope
import groovy.util.logging.Slf4j
import groovyx.net.http.HTTPBuilder

import java.util.concurrent.Future

import static com.ofg.uptodate.UrlEspaceUtils.escape
import static com.ofg.uptodate.finder.http.HTTPBuilderProvider.FailureHandlers.logOnlyFailureHandler

@PackageScope
@Slf4j
class MavenNewVersionFinderFactory implements NewVersionFinderFactory {

    public static final String MAVEN_CENTRAL_REPO_URL = "https://search.maven.org/solrsearch/select"

    @Override
    NewVersionFinder create(UptodatePluginExtension uptodatePluginExtension, List<Dependency> dependencies) {
        FinderConfiguration finderConfiguration = new FinderConfiguration(
                new RepositorySettings(repoUrl: uptodatePluginExtension.mavenRepo, ignoreRepo: uptodatePluginExtension.ignoreMavenCentral),
                uptodatePluginExtension,
                dependencies.size())
        return new NewVersionFinder(
                mavenLatestVersionsCollector(finderConfiguration),
                finderConfiguration)
    }

    private Closure<Future> mavenLatestVersionsCollector(FinderConfiguration finderConfiguration) {
        HTTPBuilder httpBuilder = new HTTPBuilderProvider(finderConfiguration.httpConnectionSettings).get()
        return getLatestFromMavenCentralRepo.curry(httpBuilder, finderConfiguration.excludedVersionPatterns)
    }

    private Closure<Future> getLatestFromMavenCentralRepo = { HTTPBuilder httpBuilder, List<String> versionToExcludePatterns, Dependency dependency ->
        httpBuilder.handler.failure = logOnlyFailureHandler(log, dependency.name)
        httpBuilder.get(queryString: "q=${escape("g:\"$dependency.group\"")}+AND+${escape("a:\"$dependency.name\"")}&core=gav&rows10&wt=json".toString()) { resp, json ->
            if (!json) {
                return []
            }
            Version latestNonExcludedVersion = json.response.docs.findAll { doc ->
                new VersionPatternMatcher(doc.v).matchesNoneOf(versionToExcludePatterns)
            }.collect { new Version(it.v) }.max()
            return [dependency, new Dependency(dependency, latestNonExcludedVersion)]
        }
    }
}
