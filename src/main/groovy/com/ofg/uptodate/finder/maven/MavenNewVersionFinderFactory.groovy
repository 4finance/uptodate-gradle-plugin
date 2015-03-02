package com.ofg.uptodate.finder.maven

import com.ofg.uptodate.LoggerProxy
import com.ofg.uptodate.UptodatePluginExtension
import com.ofg.uptodate.finder.FinderConfiguration
import com.ofg.uptodate.finder.NewVersionFinder
import com.ofg.uptodate.finder.NewVersionFinderFactory
import com.ofg.uptodate.finder.RepositorySettings
import groovy.transform.PackageScope

@PackageScope
class MavenNewVersionFinderFactory implements NewVersionFinderFactory {

    public static final String MAVEN_CENTRAL_REPO_URL = "http://search.maven.org/solrsearch/select"

    final LoggerProxy loggerProxy

    MavenNewVersionFinderFactory(LoggerProxy loggerProxy) {
        this.loggerProxy = loggerProxy
    }

    @Override
    NewVersionFinder create(UptodatePluginExtension uptodatePluginExtension) {
        return new NewVersionFinder(
                loggerProxy, 
                new MavenLatestDependenciesProvider(loggerProxy),
                new FinderConfiguration(new RepositorySettings(repoUrl: uptodatePluginExtension.mavenRepo, ignoreRepo: uptodatePluginExtension.ignoreMavenCentral), uptodatePluginExtension))
    }
}
