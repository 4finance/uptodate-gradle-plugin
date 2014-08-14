package com.ofg.uptodate.finder
import static com.github.tomakehurst.wiremock.client.WireMock.*
import static com.ofg.uptodate.UrlEspaceUtils.escape

class MavenReponseProvider {

    public static final String ROOT_PATH = "/\\?"
    public static final String SOLR_AND = '\\+AND\\+'
    
    void stubInteractionForMavenCentral(String group, String name, String response) {
        stubInteraction(get(urlMatching( "^.*${ROOT_PATH}q=${escape("g:\"$group\"")}$SOLR_AND${escape("a:\"$name\"")}.*")), aResponse().withStatus(200).withHeader("Content-Type", "application/json").withBody(response))
    }
}
