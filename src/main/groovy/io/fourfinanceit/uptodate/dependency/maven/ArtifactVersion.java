package io.fourfinanceit.uptodate.dependency.maven;

/**
 * Repackaged from Gradle's org.gradle.mvn3.org.apache.maven.artifact.versioning package
 * for compatibility's sake
 */
public interface ArtifactVersion extends Comparable<ArtifactVersion> {
    int getMajorVersion();

    int getMinorVersion();

    int getIncrementalVersion();

    int getBuildNumber();

    String getQualifier();

    void parseVersion(String var1);
}
