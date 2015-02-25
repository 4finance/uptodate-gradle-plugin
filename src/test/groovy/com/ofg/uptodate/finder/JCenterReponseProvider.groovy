package com.ofg.uptodate.finder
import static com.github.tomakehurst.wiremock.client.WireMock.*

class JCenterReponseProvider {
    void stubInteractionForJcenter(String group, String name, String response) {
        stubInteraction(get(urlMatching("^.*${group.split('\\.').join('/')}/${name}/maven-metadata.xml")), aResponse().withHeader('content-type', 'application/xml').withStatus(200).withBody(response))
    }

    void stubProxyInteractionForJcenter(String group, String name, String response) {
        stubProxyInteraction(get(urlMatching("^.*${group.split('\\.').join('/')}/${name}/maven-metadata.xml")), aResponse().withHeader('content-type', 'application/xml').withStatus(200).withBody(response))
    }
}
