package com.ofg.uptodate.finder

import com.ofg.uptodate.UptodatePluginExtension
import groovy.transform.PackageScope

@PackageScope
class MavenRepositorySettingsProvider implements RepositorySettingsProvider {

    public static final String MAVEN_CENTRAL_REPO_URL = "http://search.maven.org/solrsearch/select"
    
    @Override
    RepositorySettings getFrom(UptodatePluginExtension uptodatePluginExtension) {
        return new RepositorySettings(
                repoUrl: uptodatePluginExtension.mavenRepo,
                ignoreRepo: uptodatePluginExtension.ignoreMavenCentral
        )
    }
}
