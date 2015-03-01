package com.ofg.uptodate.finder

import com.ofg.uptodate.LoggerProxy
import com.ofg.uptodate.UptodatePluginExtension
import groovy.transform.PackageScope

@PackageScope
class MavenNewVersionFinderFactory implements NewVersionFinderFactory {

    final LoggerProxy loggerProxy

    MavenNewVersionFinderFactory(LoggerProxy loggerProxy) {
        this.loggerProxy = loggerProxy
    }

    @Override
    NewVersionFinder build(UptodatePluginExtension uptodatePluginExtension) {
        return new NewVersionFinder(loggerProxy, new MavenRepositorySettingsProvider(), new MavenLatestDependenciesProvider(loggerProxy), uptodatePluginExtension)
    }
}
