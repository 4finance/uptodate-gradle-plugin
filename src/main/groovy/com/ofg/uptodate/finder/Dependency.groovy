package com.ofg.uptodate.finder

import groovy.transform.EqualsAndHashCode

@EqualsAndHashCode
class Dependency {
    final String group
    final String name
    final DependencyVersion version

    Dependency(String group, String name, String version) {
        this.group = group
        this.name = name
        this.version = new DependencyVersion(version)
    }

    Dependency(Dependency dependency, DependencyVersion version) {
        name = dependency.name
        group = dependency.group
        this.version = version
    }
    
    boolean hasSameGroupAndNameAs(Dependency other) {
        return this.group == other.group && this.name == other.name
    }

    @Override
    public String toString() {
        return "'$group:$name:$version'"
    }
}
