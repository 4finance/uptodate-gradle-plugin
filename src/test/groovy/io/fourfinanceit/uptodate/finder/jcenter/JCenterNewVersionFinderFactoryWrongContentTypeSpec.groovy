package io.fourfinanceit.uptodate.finder.jcenter

import io.fourfinanceit.uptodate.finder.NewFinderSpec

import static io.fourfinanceit.uptodate.Xmls.HIBERNATE_CORE_META_DATA
import static io.fourfinanceit.uptodate.Xmls.JUNIT_META_DATA

@Mixin([JCenterResponseProvider])
class JCenterNewVersionFinderFactoryWrongContentTypeSpec extends NewFinderSpec {

    def setup() {
        project.extensions.uptodate.jCenterRepo = "http://localhost:${MOCK_HTTP_SERVER_PORT}/"
        project.extensions.uptodate.ignoreMavenCentral = true
    }

    @Override
    protected void artifactMetadataRequestResponse(String group, String name, String response) {
        stubInteractionForJcenter(group, name, response, "application/unknown")
    }

    def "should list new dependencies even when jcenter returned application/unknown content type"() {
        given:
            artifactMetadataRequestResponse('org.hibernate', 'hibernate-core', HIBERNATE_CORE_META_DATA)
            artifactMetadataRequestResponse('junit' ,'junit', JUNIT_META_DATA)
        and:
            project.dependencies.add(COMPILE_CONFIGURATION, 'org.hibernate:hibernate-core:4.2.9.Final')
            project.dependencies.add(TEST_COMPILE_CONFIGURATION, 'junit:junit:4.11')
        when:
            executeUptodateTask()
        then:
            1 * loggerProxy.lifecycle(_, "New versions available:\n" +
                "'org.hibernate:hibernate-core:4.3.6.Final'")
    }
}
