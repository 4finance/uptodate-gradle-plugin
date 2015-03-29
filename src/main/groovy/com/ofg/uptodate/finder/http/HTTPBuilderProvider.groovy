package com.ofg.uptodate.finder.http

import com.ofg.uptodate.LoggerProxy
import com.ofg.uptodate.finder.dependency.Dependency
import groovyx.net.http.AsyncHTTPBuilder
import groovyx.net.http.HTTPBuilder
import org.slf4j.Logger

class HTTPBuilderProvider {

    private final HttpConnectionSettings httpConnectionSettings

    HTTPBuilderProvider(HttpConnectionSettings httpConnectionSettings) {
        this.httpConnectionSettings = httpConnectionSettings
    }

    HTTPBuilder get() {
        HTTPBuilder httpBuilder = new AsyncHTTPBuilder(
                uri: httpConnectionSettings.url,
                timeout: httpConnectionSettings.timeout,
                poolSize: httpConnectionSettings.poolSize
        )
        return configureProxySettingsIfApplicable(httpBuilder)
    }

    private HTTPBuilder configureProxySettingsIfApplicable(HTTPBuilder httpBuilder) {
        httpConnectionSettings.proxySettings?.with {
            httpBuilder.setProxy(hostname, port, scheme)
        }
        return httpBuilder
    }

    static class FailureHandlers {

        static Closure<List<Dependency>> logOnlyFailureHandler(LoggerProxy loggerProxy, Logger log, String dependencyName) {
            { resp ->
                loggerProxy.debug(log, "Error with status [$resp.status] occurred while trying to download dependency [$dependencyName]")
                return []
            }
        }
    }
}
