package com.ofg.uptodate

import groovy.transform.EqualsAndHashCode
import groovy.transform.PackageScope

@PackageScope
@EqualsAndHashCode(includeFields = true)
class Dependency {
    private final String group
    private final String name
    private final String version

    Dependency(String group, String name, String version) {
        this.group = group
        this.name = name
        this.version = version
    }

    Dependency(Dependency dependency, String version) {
        name = dependency.name
        group = dependency.group
        this.version = version
    }

    @Override
    public String toString() {
        return "'$group:$name:$version'"
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
