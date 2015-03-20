package com.ofg.uptodate.finder

import com.ofg.uptodate.finder.jcenter.JCenterReponseProvider
import com.ofg.uptodate.finder.maven.MavenReponseProvider

import static com.ofg.uptodate.Jsons.JUNIT_RESPONSE
import static com.ofg.uptodate.Jsons.OLD_HIBERNATE_RESPONSE
import static com.ofg.uptodate.Xmls.HIBERNATE_CORE_META_DATA
import static com.ofg.uptodate.Xmls.OLD_JUNIT_META_DATA

@Mixin([MavenReponseProvider, JCenterReponseProvider])
class AllRepositoriesnewVersionFinderSpec extends NewFinderSpec {
   
    def setup() {
        project.extensions.uptodate.mavenRepo = "http://localhost:${MOCK_HTTP_SERVER_PORT}/maven/"
        project.extensions.uptodate.jCenterRepo = "http://localhost:${MOCK_HTTP_SERVER_PORT}/jcenter/" 
    }
    
    @Override
    protected Integer getHttpServerPort() {
        return MOCK_HTTP_SERVER_PORT
    }

    def "should list all dependencies, that have newer versions"() {
        given:
            stubInteractionForJcenter('org.hibernate', 'hibernate-core', HIBERNATE_CORE_META_DATA)
            stubInteractionForJcenter('junit', 'junit', OLD_JUNIT_META_DATA)
            stubInteractionForMavenCentral('org.hibernate' ,'hibernate-core', OLD_HIBERNATE_RESPONSE)
            stubInteractionForMavenCentral('junit' ,'junit', JUNIT_RESPONSE)
        and:
            project.dependencies.add(COMPILE_CONFIGURATION, 'org.hibernate:hibernate-core:4.2.9.Final')
            project.dependencies.add(TEST_COMPILE_CONFIGURATION, 'junit:junit:4.10')
        when:
            executeUptodateTask()
        then:
            1 * loggerProxy.warn(_, "New versions available:\n" +
                    "'junit:junit:4.11'\n" +
                    "'org.hibernate:hibernate-core:4.3.6.Final'")
    }    

}
