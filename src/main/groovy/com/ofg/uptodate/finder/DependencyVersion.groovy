package com.ofg.uptodate.finder

import com.ofg.uptodate.VersionPatterns
import groovy.transform.EqualsAndHashCode

@EqualsAndHashCode
class DependencyVersion implements Comparable<DependencyVersion> {

    private static Version NULL_VERSION = new Version('', false)
    private static final int THIS_VERSION_LOWER = -1
    private static final int THIS_VERSION_HIGHER = 1

    final String unparsedVersion
    final Version major
    final Version minor
    final Version rev
    final Version build

    DependencyVersion(String unparsedVersion) {
        this.unparsedVersion = unparsedVersion
        String[] splitVersion = unparsedVersion.split('\\.')
        major = new Version(splitVersion[0], splitVersion[0].isNumber())
        minor = minorVersionExists(splitVersion) ? getParsedRevision(splitVersion[1]) : NULL_VERSION
        rev = revVersionExists(splitVersion) ? getParsedRevision(splitVersion[2]) : NULL_VERSION
        build = buildVersionExists(splitVersion) ? getParsedRevision(splitVersion[3]) : NULL_VERSION
    }

    private boolean buildVersionExists(String[] splitVersion) {
        splitVersion.length > 3
    }

    private boolean revVersionExists(String[] splitVersion) {
        splitVersion.length > 2
    }

    private boolean minorVersionExists(String[] splitVersion) {
        splitVersion.length > 1
    }

    private static Version getParsedRevision(String splitVersion) {
        return new Version(splitVersion, splitVersion.isNumber())
    }

    @Override
    int compareTo(DependencyVersion other) {
        int versionsComparison = this.major <=> other.major
        if (versionsComparison != 0) {
            return versionsComparison
        }
        versionsComparison = this.minor <=> other.minor
        if (versionsComparison != 0) {
            return versionsComparison
        }
        versionsComparison = compareRevisions(other)
        if (versionsComparison != 0) {
            return versionsComparison
        }
        return this.build <=> other.build
    }

    private int compareRevisions(DependencyVersion other) {
        if (bothHaveRevisionFilled(other)) {
            return compareRevisionsWith(other)
        } else if (neitherHasRevisionFilled(other)) {
            return 0
        }
        // this: 1.3, other 1.3.RC
        return (isCurrentRevNullVersion() && other.rev.finalVersion) ?
                THIS_VERSION_LOWER : revisionMostLikelyGreaterThanNullVersion(other)
    }

    private boolean isCurrentRevNullVersion() {
        return this.rev == NULL_VERSION
    }

    private int compareRevisionsWith(DependencyVersion other) {
        return this.rev <=> other.rev
    }

    private boolean bothHaveRevisionFilled(DependencyVersion other) {
        return this.rev != NULL_VERSION && other.rev != NULL_VERSION
    }

    private boolean neitherHasRevisionFilled(DependencyVersion other) {
        return this.rev == NULL_VERSION && other.rev == NULL_VERSION
    }

    // this 2.2, other 2.2.1-b03
    private int revisionMostLikelyGreaterThanNullVersion(DependencyVersion other) {
        return other.rev.versionNumber.matches('^[0-9]+.*$') ? THIS_VERSION_LOWER : THIS_VERSION_HIGHER
    }

    @EqualsAndHashCode
    static class Version implements Comparable<Version> {
        final String versionNumber
        final boolean finalVersion

        private Version(String versionNumber, boolean finalVersion) {
            this.versionNumber = versionNumber
            this.finalVersion = finalVersion
        }

        @Override
        int compareTo(Version o) {
            Version extractedVersion = tryToExtractVersion(this.versionNumber)
            Version extractedOtherVersion = tryToExtractVersion(o.versionNumber)
            if(extractedVersion.versionNumber == extractedOtherVersion.versionNumber) {
                if (extractedVersion.finalVersion && !extractedOtherVersion.finalVersion) {
                    return 1
                } else if (!extractedVersion.finalVersion && extractedOtherVersion.finalVersion) {
                    return -1
                }
                return 0
            }
            if (extractedVersion.isNumber() && extractedOtherVersion.isNumber()) {
                return extractedVersion.toInteger() <=> extractedOtherVersion.toInteger()
            }
            return extractedVersion.versionNumber <=> extractedOtherVersion.versionNumber
        }

        private Version tryToExtractVersion(String version) {
            String matchedPattern = VersionPatterns.ALL_PATTERNS.find { version.matches(it) }
            if (!matchedPattern) {
                return new Version(version, version.isNumber())
            }
            String versionNumber = version.split('[-|.]')[0]
            return new Version(versionNumber, false)
        }

        boolean isNumber() {
            return versionNumber.isNumber()
        }

        Integer toInteger() {
            return versionNumber.toInteger()
        }
    }

    @Override
    String toString() {
        return unparsedVersion
    }
}
