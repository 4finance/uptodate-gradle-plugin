package io.fourfinanceit.uptodate.finder.jcenter

import com.github.tomakehurst.wiremock.client.MappingBuilder
import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder

import static com.github.tomakehurst.wiremock.client.WireMock.*

class JCenterResponseProvider {
    
    void stubInteractionForJcenter(String group, String name, String response, String contentType = 'application/xml') {
        stubInteraction(jcenterMapping(group, name), jcenterResponse(response, contentType))
    }

    void stubProxyInteractionForJcenter(String group, String name, String response, String contentType = 'application/xml') {
        stubProxyInteraction(jcenterMapping(group, name), jcenterResponse(response, contentType))
    }
    
    private static MappingBuilder jcenterMapping(String group, String name) {
        return get(urlMatching("^.*${group.split('\\.').join('/')}/${name}/maven-metadata.xml"))
    }

    private static ResponseDefinitionBuilder jcenterResponse(String response, String contentType) {
        return aResponse().withHeader('content-type', contentType).withStatus(200).withBody(response)
    }
}
