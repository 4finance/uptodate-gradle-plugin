package com.ofg.uptodate

import groovy.transform.PackageScope 

@PackageScope
class Dependency {
    private final String configuration
    private final String group
    private final String name
    private final String version

    Dependency(String configuration, String group, String name, String version) {
        this.configuration = configuration
        this.group = group
        this.name = name
        this.version = version
    }

    Dependency(Dependency dependency, String version) {
        name = dependency.name
        group = dependency.group
        configuration = dependency.configuration
        this.version = version
    }

    @Override
    public String toString() {
        return "$configuration '$group:$name:$version'"
    }

    String getConfiguration() {
        return configuration
    }

    String getGroup() {
        return group
    }

    String getName() {
        return name
    }

    String getVersion() {
        return version
    }
}
