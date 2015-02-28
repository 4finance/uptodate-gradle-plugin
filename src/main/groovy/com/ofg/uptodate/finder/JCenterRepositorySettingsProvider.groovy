package com.ofg.uptodate.finder
import com.ofg.uptodate.UptodatePluginExtension
import groovy.transform.PackageScope

@PackageScope
class JCenterRepositorySettingsProvider implements RepositorySettingsProvider {

    public static final String JCENTER_REPO_URL = "http://jcenter.bintray.com/"
    
    @Override
    RepositorySettings getFrom(UptodatePluginExtension uptodatePluginExtension) {
        return new RepositorySettings(
                repoUrl: uptodatePluginExtension.jCenterRepo,
                ignoreRepo: uptodatePluginExtension.ignoreJCenter
        )
    }
}
