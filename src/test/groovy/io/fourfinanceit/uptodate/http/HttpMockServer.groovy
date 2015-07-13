package io.fourfinanceit.uptodate.http

import com.github.tomakehurst.wiremock.WireMockServer

class HttpMockServer extends WireMockServer {

    static final int DEFAULT_PORT = 8030

    HttpMockServer(int port) {
        super(port)
    }

    HttpMockServer() {
        super(DEFAULT_PORT)
    }
    
    void shutdownServer() {
        if (isRunning()) {
            stop()
        }
        shutdown()
    }
}
