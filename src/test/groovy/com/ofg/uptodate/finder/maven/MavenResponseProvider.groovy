package com.ofg.uptodate.finder.maven
import com.github.tomakehurst.wiremock.client.MappingBuilder
import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder

import static com.github.tomakehurst.wiremock.client.WireMock.*
import static com.ofg.uptodate.UrlEspaceUtils.escape

class MavenResponseProvider {

    public static final String ROOT_PATH = "/\\?"
    public static final String SOLR_AND = '\\+AND\\+'
    
    void stubInteractionForMavenCentral(String group, String name, String response) {
        stubInteraction(mavenMapping(group, name), mavenResponse(response))
    }

    void stubProxyInteractionForMavenCentral(String group, String name, String response) {
        stubProxyInteraction(mavenMapping(group, name), mavenResponse(response))
    }
    
    private static MappingBuilder mavenMapping(String group, String name) {
        return get(urlMatching( "^.*${ROOT_PATH}q=${escape("g:\"$group\"")}$SOLR_AND${escape("a:\"$name\"")}.*"))
    }
    
    private static ResponseDefinitionBuilder mavenResponse(String response){
        return aResponse().withStatus(200).withHeader("Content-Type", "application/json").withBody(response)   
    }
}
