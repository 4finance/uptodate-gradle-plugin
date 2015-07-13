package io.fourfinanceit.uptodate.finder.maven

import io.fourfinanceit.uptodate.dependency.VersionPatternMatcher
import groovy.util.logging.Slf4j
import groovyx.net.http.HTTPBuilder
import io.fourfinanceit.uptodate.UptodatePluginExtension
import io.fourfinanceit.uptodate.dependency.Dependency
import io.fourfinanceit.uptodate.dependency.Version
import io.fourfinanceit.uptodate.finder.FinderConfiguration
import io.fourfinanceit.uptodate.finder.NewVersionFinder
import io.fourfinanceit.uptodate.finder.NewVersionFinderFactory
import io.fourfinanceit.uptodate.finder.RepositorySettings
import io.fourfinanceit.uptodate.finder.http.HTTPBuilderProvider

import java.util.concurrent.Future

import static io.fourfinanceit.uptodate.UrlEspaceUtils.escape
import static io.fourfinanceit.uptodate.finder.http.HTTPBuilderProvider.FailureHandlers.logOnlyFailureHandler

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
        httpBuilder.get(queryString: buildQueryString(dependency)) { resp, json ->
            if (!json) {
                return []
            }
            Version latestNonExcludedVersion = json.response.docs.findAll { doc ->
                new VersionPatternMatcher(doc.v).matchesNoneOf(versionToExcludePatterns)
            }.collect { new Version(it.v) }.max()
            return [dependency, new Dependency(dependency, latestNonExcludedVersion)]
        }
    }

    private String buildQueryString(Dependency dependency) {
        String groupCriteria = escape("g:\"$dependency.group\"")
        String artifactCriteria = escape("a:\"$dependency.name\"")
        return "q=${groupCriteria}+AND+${artifactCriteria}&core=gav&rows10&wt=json"
    }
}
