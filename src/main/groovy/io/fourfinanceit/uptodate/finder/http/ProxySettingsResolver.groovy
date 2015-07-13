package io.fourfinanceit.uptodate.finder.http

import io.fourfinanceit.uptodate.UptodatePluginExtension

class ProxySettingsResolver {

    private final UptodatePluginExtension uptodatePluginExtension

    private static final int DEFAULT_PROXY_PORT = -1

    ProxySettingsResolver(UptodatePluginExtension uptodatePluginExtension) {
        this.uptodatePluginExtension = uptodatePluginExtension
    }

    ProxySettings resolve() {
        return [proxyConfigFromHttpSystemProperties(), proxyConfigFromHttpsSystemProperties(), proxyConfigFromPluginConfiguration(uptodatePluginExtension)].find { it.hostname }
    }

    private ProxySettings proxyConfigFromHttpSystemProperties() {
        return proxyConfigFromSystemPropertiesForScheme('http')
    }

    private ProxySettings proxyConfigFromHttpsSystemProperties() {
        return proxyConfigFromSystemPropertiesForScheme('https')
    }

    private ProxySettings proxyConfigFromPluginConfiguration(UptodatePluginExtension uptodatePluginExtension) {
        return new ProxySettings(
                hostname: uptodatePluginExtension.proxyHostname,
                port: uptodatePluginExtension.proxyPort,
                scheme: uptodatePluginExtension.proxyScheme
        )
    }
    
    private ProxySettings proxyConfigFromSystemPropertiesForScheme(String scheme) {
        String proxyPort = System.getProperty("${scheme}.proxyPort")
        return new ProxySettings(
                hostname: System.getProperty("${scheme}.proxyHost"),
                port: proxyPort ? proxyPort.toInteger() : DEFAULT_PROXY_PORT,
                scheme: "${scheme}"
        )
    }
}
