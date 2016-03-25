package com.ofg.uptodate.functional

import nebula.test.IntegrationSpec
import nebula.test.functional.ExecutionResult

class BasicFunctionalSpec extends IntegrationSpec {

    void setup() {
        fork = true
    }

    def "should 1"() {
        given:
            writeHelloWorld('test.uptodate.basic1')
            buildFile << """
                //TODO: Why http-builder dependency is not added automatically (as compile time production dependency)?
                buildscript {
                    repositories {
                        jcenter()
                    }
                    dependencies {
                        classpath 'org.codehaus.groovy.modules.http-builder:http-builder:0.7.2'
                    }
                }
                repositories {
                    jcenter()
                }
                apply plugin: 'com.ofg.uptodate'
                apply plugin: 'java'
                dependencies {
                    testCompile 'junit:junit:4.11'
                }
            """.stripIndent()
        when:
            ExecutionResult result = runTasksSuccessfully('uptodate')
        then:
            println result.standardOutput
            result.standardOutput.contains('New versions available:')
            result.standardOutput.contains("'junit:junit:")
    }
}