package io.fourfinanceit.uptodate.finder.http

import io.fourfinanceit.uptodate.UptodatePluginExtension
import spock.lang.Specification

class ProxySettingsResolverSpec extends Specification {

    private static final String SYS_PROP_HTTP_PROXY_HOSTNAME = 'http-proxy-sys-prop'
    private static final int SYS_PROP_HTTP_PROXY_PORT = 15000
    private static final String SYS_PROP_HTTPS_PROXY_HOSTNAME = 'https-proxy-sys-prop'
    private static final int SYS_PROP_HTTPS_PROXY_PORT = 15100
    private static final String PLUGIN_CONFIG_PROXY_HOSTNAME = 'http-proxy-plugin-config'
    private static final int PLUGIN_CONFIG_PROXY_PORT = 15200
    private static final String PLUGIN_CONFIG_PROXY_SCHEME = 'http'

    def "'http.*' system properties should have the highest priority when resolving proxy configuration"() {
        given: "proxy is configured via 'http,*' system properties"
            String prevHttpProxyHost = System.setProperty('http.proxyHost', SYS_PROP_HTTP_PROXY_HOSTNAME)
            String prevHttpProxyPort = System.setProperty('http.proxyPort', SYS_PROP_HTTP_PROXY_PORT.toString())
        and: "proxy is configured via 'https,*' system properties"
            String prevHttpsProxyHost = System.setProperty('https.proxyHost', SYS_PROP_HTTPS_PROXY_HOSTNAME)
            String prevHttpsProxyPort = System.setProperty('https.proxyPort', SYS_PROP_HTTPS_PROXY_PORT.toString())
        and: "proxy is configured via plugin configuration"
            UptodatePluginExtension uptodatePluginExtension = uptodatePluginExtensionWithProxyConfiguredViaPluginExtension()
        expect:
            proxySettingsFor(uptodatePluginExtension) == propsHttpProxySettings()
        cleanup:
            System.setProperty('http.proxyHost', prevHttpProxyHost ?: '')
            System.setProperty('http.proxyPort', prevHttpProxyPort ?: '')
            System.setProperty('https.proxyHost', prevHttpsProxyHost ?: '')
            System.setProperty('https.proxyPort', prevHttpsProxyPort ?: '')
    }

    def "'https.*' system properties should have the highest priority when resolving proxy configuration and 'http.*' properties are not present"() {
        given: "proxy is configured via 'https,*' system properties"
            String prevHttpsProxyHost = System.setProperty('https.proxyHost', SYS_PROP_HTTPS_PROXY_HOSTNAME)
            String prevHttpsProxyPort = System.setProperty('https.proxyPort', SYS_PROP_HTTPS_PROXY_PORT.toString())
        and: "proxy is configured via plugin configuration"
            UptodatePluginExtension uptodatePluginExtension = uptodatePluginExtensionWithProxyConfiguredViaPluginExtension()
        expect:
            proxySettingsFor(uptodatePluginExtension) == propsHttpsProxySettings()
        cleanup:
            System.setProperty('https.proxyHost', prevHttpsProxyHost ?: '')
            System.setProperty('https.proxyPort', prevHttpsProxyPort ?: '')
    }

    def "plugin configuration should have the highest priority when resolving proxy configuration and proxy system properties are not present"() {
        given: "proxy is configured via plugin configuration"
            UptodatePluginExtension uptodatePluginExtension = uptodatePluginExtensionWithProxyConfiguredViaPluginExtension()
        expect:
            proxySettingsFor(uptodatePluginExtension) == pluginProxySettings()
    }

    def "should return null when no proxy settings found"() {
        expect:
            proxySettingsFor(new UptodatePluginExtension()) == null
    }

    private UptodatePluginExtension uptodatePluginExtensionWithProxyConfiguredViaPluginExtension() {
        return new UptodatePluginExtension(
                proxyHostname: PLUGIN_CONFIG_PROXY_HOSTNAME,
                proxyPort: PLUGIN_CONFIG_PROXY_PORT,
                proxyScheme: PLUGIN_CONFIG_PROXY_SCHEME
        )
    }

    private ProxySettings proxySettingsFor(UptodatePluginExtension uptodatePluginExtension) {
        return new ProxySettingsResolver(uptodatePluginExtension).resolve()
    }

    private ProxySettings propsHttpProxySettings() {
        return new ProxySettings(hostname: SYS_PROP_HTTP_PROXY_HOSTNAME, port: SYS_PROP_HTTP_PROXY_PORT, scheme: 'http')
    }

    private ProxySettings propsHttpsProxySettings() {
        return new ProxySettings(hostname: SYS_PROP_HTTPS_PROXY_HOSTNAME, port: SYS_PROP_HTTPS_PROXY_PORT, scheme: 'https')
    }

    private ProxySettings pluginProxySettings() {
        return new ProxySettings(
                hostname: PLUGIN_CONFIG_PROXY_HOSTNAME,
                port: PLUGIN_CONFIG_PROXY_PORT,
                scheme: PLUGIN_CONFIG_PROXY_SCHEME)
    }
}
