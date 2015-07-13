package com.ofg.uptodate.dependency.maven;

import java.util.StringTokenizer;

/**
 * Repackaged from Gradle's org.gradle.mvn3.org.apache.maven.artifact.versioning package
 * for compatibility's sake
 */
public class DefaultArtifactVersion implements ArtifactVersion {
    private Integer majorVersion;
    private Integer minorVersion;
    private Integer incrementalVersion;
    private Integer buildNumber;
    private String qualifier;
    private ComparableVersion comparable;

    public DefaultArtifactVersion(String version) {
        this.parseVersion(version);
    }

    public int hashCode() {
        return 11 + this.comparable.hashCode();
    }

    public boolean equals(Object other) {
        return this == other?true:(!(other instanceof ArtifactVersion)?false:this.compareTo((ArtifactVersion)other) == 0);
    }

    public int compareTo(ArtifactVersion otherVersion) {
        return otherVersion instanceof DefaultArtifactVersion?this.comparable.compareTo(((DefaultArtifactVersion)otherVersion).comparable):this.compareTo((ArtifactVersion)(new DefaultArtifactVersion(otherVersion.toString())));
    }

    public int getMajorVersion() {
        return this.majorVersion != null?this.majorVersion.intValue():0;
    }

    public int getMinorVersion() {
        return this.minorVersion != null?this.minorVersion.intValue():0;
    }

    public int getIncrementalVersion() {
        return this.incrementalVersion != null?this.incrementalVersion.intValue():0;
    }

    public int getBuildNumber() {
        return this.buildNumber != null?this.buildNumber.intValue():0;
    }

    public String getQualifier() {
        return this.qualifier;
    }

    public final void parseVersion(String version) {
        this.comparable = new ComparableVersion(version);
        int index = version.indexOf("-");
        String part2 = null;
        String part1;
        if(index < 0) {
            part1 = version;
        } else {
            part1 = version.substring(0, index);
            part2 = version.substring(index + 1);
        }

        if(part2 != null) {
            try {
                if(part2.length() != 1 && part2.startsWith("0")) {
                    this.qualifier = part2;
                } else {
                    this.buildNumber = Integer.valueOf(part2);
                }
            } catch (NumberFormatException var10) {
                this.qualifier = part2;
            }
        }

        if(part1.indexOf(".") < 0 && !part1.startsWith("0")) {
            try {
                this.majorVersion = Integer.valueOf(part1);
            } catch (NumberFormatException var8) {
                this.qualifier = version;
                this.buildNumber = null;
            }
        } else {
            boolean fallback = false;
            StringTokenizer tok = new StringTokenizer(part1, ".");

            try {
                this.majorVersion = getNextIntegerToken(tok);
                if(tok.hasMoreTokens()) {
                    this.minorVersion = getNextIntegerToken(tok);
                }

                if(tok.hasMoreTokens()) {
                    this.incrementalVersion = getNextIntegerToken(tok);
                }

                if(tok.hasMoreTokens()) {
                    fallback = true;
                }

                if(part1.indexOf("..") >= 0 || part1.startsWith(".") || part1.endsWith(".")) {
                    fallback = true;
                }
            } catch (NumberFormatException var9) {
                fallback = true;
            }

            if(fallback) {
                this.qualifier = version;
                this.majorVersion = null;
                this.minorVersion = null;
                this.incrementalVersion = null;
                this.buildNumber = null;
            }
        }

    }

    private static Integer getNextIntegerToken(StringTokenizer tok) {
        String s = tok.nextToken();
        if(s.length() > 1 && s.startsWith("0")) {
            throw new NumberFormatException("Number part has a leading 0: \'" + s + "\'");
        } else {
            return Integer.valueOf(s);
        }
    }

    public String toString() {
        StringBuilder buf = new StringBuilder();
        if(this.majorVersion != null) {
            buf.append(this.majorVersion);
        }

        if(this.minorVersion != null) {
            buf.append(".");
            buf.append(this.minorVersion);
        }

        if(this.incrementalVersion != null) {
            buf.append(".");
            buf.append(this.incrementalVersion);
        }

        if(this.buildNumber != null) {
            buf.append("-");
            buf.append(this.buildNumber);
        } else if(this.qualifier != null) {
            if(buf.length() > 0) {
                buf.append("-");
            }

            buf.append(this.qualifier);
        }

        return buf.toString();
    }
}