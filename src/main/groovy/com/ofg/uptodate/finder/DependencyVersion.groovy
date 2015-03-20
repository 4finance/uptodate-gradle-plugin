package com.ofg.uptodate.finder

import groovy.transform.EqualsAndHashCode
import org.gradle.mvn3.org.apache.maven.artifact.versioning.ArtifactVersion
import org.gradle.mvn3.org.apache.maven.artifact.versioning.DefaultArtifactVersion

@EqualsAndHashCode
class DependencyVersion implements Comparable<DependencyVersion> {

    final String unparsedVersion
    final ArtifactVersion artifactVersion

    DependencyVersion(String unparsedVersion) {
        this.unparsedVersion = unparsedVersion
        this.artifactVersion = new DefaultArtifactVersion(unparsedVersion)
    }

    @Override
    int compareTo(DependencyVersion other) {
        return this.artifactVersion <=> other.artifactVersion
    }

    @Override
    String toString() {
        return unparsedVersion
    }
}
