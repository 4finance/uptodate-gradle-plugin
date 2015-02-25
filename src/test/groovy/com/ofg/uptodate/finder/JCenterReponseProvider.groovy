package com.ofg.uptodate.finder

import com.github.tomakehurst.wiremock.client.MappingBuilder
import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder

import static com.github.tomakehurst.wiremock.client.WireMock.*

class JCenterReponseProvider {
    
    void stubInteractionForJcenter(String group, String name, String response) {
        stubInteraction(jcenterMapping(group, name), jcenterResponse(response))
    }

    void stubProxyInteractionForJcenter(String group, String name, String response) {
        stubProxyInteraction(jcenterMapping(group, name), jcenterResponse(response))
    }
    
    private static MappingBuilder jcenterMapping(String group, String name) {
        return get(urlMatching("^.*${group.split('\\.').join('/')}/${name}/maven-metadata.xml"))
    }

    private static ResponseDefinitionBuilder jcenterResponse(String response) {
        return aResponse().withHeader('content-type', 'application/xml').withStatus(200).withBody(response)
    }
}
