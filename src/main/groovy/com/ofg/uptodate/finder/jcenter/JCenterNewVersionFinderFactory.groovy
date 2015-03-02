package com.ofg.uptodate.finder.jcenter

import com.ofg.uptodate.LoggerProxy
import com.ofg.uptodate.UptodatePluginExtension
import com.ofg.uptodate.finder.FinderConfiguration
import com.ofg.uptodate.finder.NewVersionFinder
import com.ofg.uptodate.finder.NewVersionFinderFactory
import com.ofg.uptodate.finder.RepositorySettings
import groovy.transform.PackageScope

@PackageScope
class JCenterNewVersionFinderFactory implements NewVersionFinderFactory {

    public static final String JCENTER_REPO_URL = "http://jcenter.bintray.com/"

    final LoggerProxy loggerProxy

    JCenterNewVersionFinderFactory(LoggerProxy loggerProxy) {
        this.loggerProxy = loggerProxy
    }

    @Override
    NewVersionFinder create(UptodatePluginExtension uptodatePluginExtension) {
        return new NewVersionFinder(
                loggerProxy,
                new JCenterLatestDependenciesProvider(loggerProxy),
                new FinderConfiguration(new RepositorySettings(repoUrl: uptodatePluginExtension.jCenterRepo, ignoreRepo: uptodatePluginExtension.ignoreJCenter), uptodatePluginExtension))
    }
}
