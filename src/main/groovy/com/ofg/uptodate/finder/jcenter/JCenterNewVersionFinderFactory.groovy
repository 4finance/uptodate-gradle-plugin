package com.ofg.uptodate.finder.jcenter

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
import groovy.util.slurpersupport.NodeChild
import groovyx.net.http.HTTPBuilder

import java.util.concurrent.Future

import static com.ofg.uptodate.finder.http.HTTPBuilderProvider.FailureHandlers.logOnlyFailureHandler

@PackageScope
@Slf4j
class JCenterNewVersionFinderFactory implements NewVersionFinderFactory {

    public static final String JCENTER_REPO_URL = "http://jcenter.bintray.com/"

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
        httpBuilder.get(path: "/${dependency.group.split('\\.').join('/')}/${dependency.name}/maven-metadata.xml") { resp, xml ->
            if (!xml) {
                return []
            }
            return [dependency, new Dependency(dependency, getLatestDependencyVersion(xml.versioning.release.text(), xml, versionToExcludePatterns))]
        } as Future
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
