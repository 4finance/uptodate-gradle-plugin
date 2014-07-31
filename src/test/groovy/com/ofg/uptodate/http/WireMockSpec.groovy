package com.ofg.uptodate.http

import com.github.tomakehurst.wiremock.client.MappingBuilder
import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder
import com.github.tomakehurst.wiremock.client.WireMock
import spock.lang.Specification

class WireMockSpec extends Specification {

    HttpMockServer httpMockServer
    protected WireMock wireMock

    void setup() {
        httpMockServer = new HttpMockServer(getHttpServerPort())
        httpMockServer.start()
        wireMock = new WireMock('localhost', httpMockServer.port())
        wireMock.resetMappings()
    }

    void cleanup() {
        httpMockServer.shutdownServer()
    }

    protected Integer getHttpServerPort() {
        return HttpMockServer.DEFAULT_PORT
    }

    protected void stubInteraction(MappingBuilder mapping, ResponseDefinitionBuilder response) {
        wireMock.register(mapping.willReturn(response))
    }

}
