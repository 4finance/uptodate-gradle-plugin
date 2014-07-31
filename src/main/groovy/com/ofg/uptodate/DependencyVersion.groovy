package com.ofg.uptodate

class DependencyVersion implements Comparable<DependencyVersion> {

    private static Version NULL_VERSION = new Version('', false)

    final String unparsedVersion
    final Version major
    final Version minor
    final Version rev
    final Version build

    DependencyVersion(String unparsedVersion) {
        this.unparsedVersion = unparsedVersion
        String[] splitVersion = unparsedVersion.split('\\.')
        major = new Version(splitVersion[0], splitVersion[0].isNumber())
        minor = getParsedRevision(splitVersion[1])
        rev = splitVersion.length > 2 ? getParsedRevision(splitVersion[2]) : NULL_VERSION
        build = splitVersion.length > 3 ? getParsedRevision(splitVersion[3]) : NULL_VERSION
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
        return (isCurrentRevNullVersion() && other.rev.finalVersion) ? -1 : 1
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

    static class Version implements Comparable<Version> {
        final String versionNumber
        final boolean finalVersion

        private Version(String versionNumber, boolean finalVersion) {
            this.versionNumber = versionNumber
            this.finalVersion = finalVersion
        }

        @Override
        int compareTo(Version o) {
            if (this.finalVersion && !o.finalVersion) {
                return 1
            } else if (!this.finalVersion && o.finalVersion) {
                return -1
            }
            if (this.versionNumber.isNumber() && o.versionNumber.isNumber()) {
                return this.versionNumber.toInteger() <=> o.versionNumber.toInteger()
            }
            boolean bothNotNumbers = !this.versionNumber.isNumber() && !o.versionNumber.isNumber()
            if (bothNotNumbers) {
                return this.versionNumber <=> o.versionNumber
            }
            return this.versionNumber.isNumber() ?  1 : -1
        }
    }

    @Override
    String toString() {
        return unparsedVersion
    }
}
