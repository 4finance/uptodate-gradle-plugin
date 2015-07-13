package io.fourfinanceit.uptodate.finder.jcenter

import io.fourfinanceit.uptodate.UptodatePluginExtension
import io.fourfinanceit.uptodate.dependency.VersionPatternMatcher
import io.fourfinanceit.uptodate.finder.NewVersionFinder
import io.fourfinanceit.uptodate.finder.NewVersionFinderFactory
import io.fourfinanceit.uptodate.finder.FinderConfiguration
import groovy.util.logging.Slf4j
import groovy.util.slurpersupport.NodeChild
import groovyx.net.http.HTTPBuilder
import io.fourfinanceit.uptodate.dependency.Dependency
import io.fourfinanceit.uptodate.dependency.Version
import io.fourfinanceit.uptodate.finder.RepositorySettings
import io.fourfinanceit.uptodate.finder.http.HTTPBuilderProvider

import java.util.concurrent.Future

import static io.fourfinanceit.uptodate.finder.http.HTTPBuilderProvider.FailureHandlers.logOnlyFailureHandler

@Slf4j
class JCenterNewVersionFinderFactory implements NewVersionFinderFactory {

    public static final String JCENTER_REPO_URL = "https://jcenter.bintray.com/"

    @Override
    NewVersionFinder create(UptodatePluginExtension uptodatePluginExtension, List<Dependency> dependencies) {
        FinderConfiguration finderConfiguration = new FinderConfiguration(
                new RepositorySettings(repoUrl: uptodatePluginExtension.jCenterRepo, ignoreRepo: uptodatePluginExtension.ignoreJCenter),
                uptodatePluginExtension,
                dependencies.size())
        return new NewVersionFinder(
                jCenterLatestVersionsCollector(finderConfiguration),
                finderConfiguration)
    }

    private Closure<Future> jCenterLatestVersionsCollector(FinderConfiguration finderConfiguration) {
        HTTPBuilder httpBuilder = new HTTPBuilderProvider(finderConfiguration.httpConnectionSettings).get()
        return getLatestFromJCenterRepo.curry(httpBuilder, finderConfiguration.excludedVersionPatterns)
    }

    private Closure<Future> getLatestFromJCenterRepo = { HTTPBuilder httpBuilder, List<String> versionToExcludePatterns, Dependency dependency ->
        httpBuilder.handler.failure = logOnlyFailureHandler(log, dependency.name)
        handleApplicationUnknownContentTypeAsXml(httpBuilder)
        httpBuilder.get(path: "/${dependency.group.split('\\.').join('/')}/${dependency.name}/maven-metadata.xml") { resp, xml ->
            if (!xml) {
                return []
            }
            return [dependency, new Dependency(dependency, getLatestDependencyVersion(xml.versioning.release.text(), xml, versionToExcludePatterns))]
        } as Future
    }

    //application/unknown is returned from jCenter when using CloudFront - #42
    private void handleApplicationUnknownContentTypeAsXml(HTTPBuilder httpBuilder) {
        httpBuilder.parser.'application/unknown' = httpBuilder.parser.'application/xml'
    }

    private Version getLatestDependencyVersion(String releaseVersion, NodeChild xml, List<String> versionToExcludePatterns) {
        if (new VersionPatternMatcher(releaseVersion).matchesNoneOf(versionToExcludePatterns)) {
            return new Version(releaseVersion)
        }
        return xml.versioning.versions.version.findAll { NodeChild version ->
            new VersionPatternMatcher(version.text()).matchesNoneOf(versionToExcludePatterns)
        }.collect {
            NodeChild version -> new Version(version.text())
        }.max()
    }
}
