package com.ofg.uptodate.finder

import com.ofg.uptodate.LoggerProxy
import com.ofg.uptodate.UptodatePluginExtension
import groovy.transform.PackageScope

@PackageScope
class JCenterNewVersionFinderFactory implements NewVersionFinderFactory {

    final LoggerProxy loggerProxy

    JCenterNewVersionFinderFactory(LoggerProxy loggerProxy) {
        this.loggerProxy = loggerProxy
    }

    @Override
    NewVersionFinder build(UptodatePluginExtension uptodatePluginExtension) {
        return new NewVersionFinder(loggerProxy, new JCenterRepositorySettingsProvider(), new JCenterLatestDependenciesProvider(loggerProxy), uptodatePluginExtension);
    }
}
