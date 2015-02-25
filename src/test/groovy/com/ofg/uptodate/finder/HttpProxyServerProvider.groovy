package com.ofg.uptodate.finder
import com.github.tomakehurst.wiremock.client.MappingBuilder
import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder
import com.github.tomakehurst.wiremock.client.WireMock
import com.ofg.uptodate.http.HttpMockServer
import groovy.transform.PackageScope

@PackageScope
class HttpProxyServerProvider {
    
    public static final int MOCK_HTTP_PROXY_SERVER_PORT = 12406
    
    HttpMockServer httpProxyServer
    WireMock wireMockProxy
    
    void startHttpProxyServer() {
        httpProxyServer = new HttpMockServer(MOCK_HTTP_PROXY_SERVER_PORT)
        httpProxyServer.start()
        wireMockProxy = new WireMock('localhost', httpProxyServer.port())
        wireMockProxy.resetMappings()
    }
    
    void shutdownHttpProxyServer() {
        httpProxyServer.shutdownServer()
    }

    void stubProxyInteraction(MappingBuilder mapping, ResponseDefinitionBuilder response) {
        wireMockProxy.register(mapping.willReturn(response))
    }
}
