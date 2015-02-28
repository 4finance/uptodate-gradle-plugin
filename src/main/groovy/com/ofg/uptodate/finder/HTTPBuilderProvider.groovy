package com.ofg.uptodate.finder

import groovyx.net.http.AsyncHTTPBuilder
import groovyx.net.http.HTTPBuilder

class HTTPBuilderProvider {

    private final HttpConnectionSettings httpConnectionSettings
    private int poolSize

    HTTPBuilderProvider(HttpConnectionSettings httpConnectionSettings) {
        this.httpConnectionSettings = httpConnectionSettings
    }

    HTTPBuilderProvider withPoolSize(int poolSize) {
        this.poolSize = poolSize
        return this
    }

    HTTPBuilder get() {
        HTTPBuilder httpBuilder = new AsyncHTTPBuilder(
                uri: httpConnectionSettings.url,
                timeout: httpConnectionSettings.timeout,
                poolSize: poolSize
        )
        return configureProxySettingsIfApplicable(httpBuilder)
    }

    private HTTPBuilder configureProxySettingsIfApplicable(HTTPBuilder httpBuilder) {
        httpConnectionSettings.proxySettings.with {
            if (hostname) {
                httpBuilder.setProxy(hostname, port, scheme)
            }
        }
        return httpBuilder
    }
}
