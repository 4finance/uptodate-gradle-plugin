package com.ofg.uptodate.finder
import com.ofg.uptodate.UptodatePluginExtension
import groovy.transform.PackageScope

@PackageScope
class FinderConfiguration {

    final boolean ignore
    final HttpConnectionSettings httpConnectionSettings
    final List<String> excludedVersionPatterns

    FinderConfiguration(RepositorySettings repositorySettings,
                        UptodatePluginExtension uptodatePluginExtension) {

        ignore = repositorySettings.ignoreRepo
        httpConnectionSettings = new HttpConnectionSettings(
                url: repositorySettings.repoUrl,
                proxySettings: new ProxySettings(
                        hostname: uptodatePluginExtension.proxyHostname,
                        port: uptodatePluginExtension.proxyPort,
                        scheme: uptodatePluginExtension.proxyScheme
                ),
                poolSize: uptodatePluginExtension.simultaneousHttpConnections,
                timeout: uptodatePluginExtension.connectionTimeout
        )
        excludedVersionPatterns = uptodatePluginExtension.excludedVersionPatterns
    }
}
