package com.ofg.uptodate.finder
import com.ofg.uptodate.LoggerProxy
import com.ofg.uptodate.UptodatePlugin
import com.ofg.uptodate.http.WireMockSpec
import org.apache.http.HttpStatus
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder

import static com.github.tomakehurst.wiremock.client.WireMock.*
import static com.ofg.uptodate.UptodatePlugin.TASK_NAME

abstract class NewFinderSpec extends WireMockSpec {

    public static final String COMPILE_CONFIGURATION = 'compile'
    public static final String TEST_COMPILE_CONFIGURATION = 'testCompile'
    public static final int MOCK_HTTP_SERVER_PORT = 12306   

    Project project = ProjectBuilder.builder().build()
    LoggerProxy loggerProxy = Mock()
    UptodatePlugin plugin = new UptodatePlugin(loggerProxy)

    def setup() {
        def compileConfiguration = project.configurations.create(COMPILE_CONFIGURATION)
        project.configurations.create(TEST_COMPILE_CONFIGURATION) {extendsFrom compileConfiguration}
        plugin.apply(project)
        project.extensions.uptodate.showMissingJCenterMessage = false
        artifactMetadataRequestResponse(200)
    }

    @Override
    protected Integer getHttpServerPort() {
        return MOCK_HTTP_SERVER_PORT
    }
  
    protected void artifactMetadataRequestResponse(String group, String name, String response) {
        artifactMetadataRequestResponse(HttpStatus.SC_OK)
    }

    protected void artifactMetadataRequestResponse(int status) {
        stubInteraction(get(urlMatching('.*')), aResponse().withStatus(status))
    }

    protected void executeUptodateTask() {
        project.tasks.getByName(TASK_NAME).execute()
    }

    protected void stubResponseWithADelayOf(int delayInMs) {
        stubInteraction(get(urlMatching('/.*')), aResponse().withFixedDelay(delayInMs))
    }
    
}
