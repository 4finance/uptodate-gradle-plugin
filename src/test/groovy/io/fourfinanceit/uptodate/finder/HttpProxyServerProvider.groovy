package io.fourfinanceit.uptodate.finder
import com.github.tomakehurst.wiremock.client.MappingBuilder
import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder
import com.github.tomakehurst.wiremock.client.WireMock
import io.fourfinanceit.uptodate.http.HttpMockServer

class HttpProxyServerProvider {
    
    public static final int MOCK_HTTP_PROXY_SERVER_PORT = 12406
    public static final String MOCK_HTTP_PROXY_SERVER_HOST = 'localhost'

    HttpMockServer httpProxyServer
    WireMock wireMockProxy
    
    void startHttpProxyServer() {
        httpProxyServer = new HttpMockServer(MOCK_HTTP_PROXY_SERVER_PORT)
        httpProxyServer.start()
        wireMockProxy = new WireMock(MOCK_HTTP_PROXY_SERVER_HOST, httpProxyServer.port())
        wireMockProxy.resetMappings()
    }
    
    void shutdownHttpProxyServer() {
        httpProxyServer.shutdownServer()
    }

    void stubProxyInteraction(MappingBuilder mapping, ResponseDefinitionBuilder response) {
        wireMockProxy.register(mapping.willReturn(response))
    }
}
