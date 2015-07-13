package io.fourfinanceit.uptodate

import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.gradle.testfixtures.ProjectBuilder
import spock.lang.Specification

class ConfigurationFilterSpec extends Specification {
    static final  String COMPILE_CONFIGURATION_NAME = 'compile'
    static final  String TEST_COMPILE_CONFIGURATION_NAME = 'testCompile'

    UptodatePluginExtension ext = new UptodatePluginExtension()
    Project project = ProjectBuilder.builder().build()
    Configuration compileConfiguration = project.configurations.create(COMPILE_CONFIGURATION_NAME)
    Configuration testCompileConfiguration = project.configurations.create(TEST_COMPILE_CONFIGURATION_NAME)
    ConfigurationFilter configurationFilter = new ConfigurationFilter(project)

    def 'should allow to filter out configurations by name'() {
        given:
            ext.excludeConfigurations(TEST_COMPILE_CONFIGURATION_NAME)
        expect:
            getConfigurations() == configurations(compileConfiguration)
    }

    def 'should allow to get only configurations with matching names'() {
        given:
            ext.includeConfigurations(COMPILE_CONFIGURATION_NAME)
        expect:
            getConfigurations() == configurations(compileConfiguration)
    }

    def 'excludes should take precedence over includes'() {
        given:
            ext.includeConfigurations(COMPILE_CONFIGURATION_NAME, TEST_COMPILE_CONFIGURATION_NAME)
            ext.excludeConfigurations(TEST_COMPILE_CONFIGURATION_NAME)
        expect:
            getConfigurations() == configurations(compileConfiguration)
    }

    def 'should return all configurations when no configurations were included nor excluded'() {
        expect:
            getConfigurations() == configurations(compileConfiguration, testCompileConfiguration)
    }

    private Set<Configuration> getConfigurations() {
        return configurationFilter.getConfigurations(ext.configurations)
    }

    private Set<Configuration> configurations(Configuration... dependencyConfigurations) {
        return dependencyConfigurations as Set
    }
}
