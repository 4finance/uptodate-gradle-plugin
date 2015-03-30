package com.ofg.uptodate.dependency

import groovy.transform.EqualsAndHashCode
import org.gradle.mvn3.org.apache.maven.artifact.versioning.ArtifactVersion
import org.gradle.mvn3.org.apache.maven.artifact.versioning.DefaultArtifactVersion

@EqualsAndHashCode
class Version implements Comparable<Version> {

    private final String unparsedVersion
    final ArtifactVersion artifactVersion

    Version(String unparsedVersion) {
        this.unparsedVersion = unparsedVersion
        this.artifactVersion = new DefaultArtifactVersion(unparsedVersion)
    }

    @Override
    int compareTo(Version other) {
        return this.artifactVersion <=> other.artifactVersion
    }

    @Override
    String toString() {
        return unparsedVersion
    }
}
