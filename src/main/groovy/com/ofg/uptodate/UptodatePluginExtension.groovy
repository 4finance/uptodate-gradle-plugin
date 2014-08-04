package com.ofg.uptodate

import static com.ofg.uptodate.UptodatePluginExtension.VersionPatterns.*

class UptodatePluginExtension {
    String mavenRepo = MavenNewVersionFinder.MAVEN_CENTRAL_REPO_URL
    final IncludedExcludedConfigurationsHolder configurations = new IncludedExcludedConfigurationsHolder()
    int simultaneousHttpConnections = 8
    int connectionTimeout = 5000
    List<String> versionToExcludePatterns = [ALPHA, BETA, RC, CR, SNAPSHOT]

    void connectionTimeout(int connectionTimeout) {
        this.connectionTimeout = connectionTimeout
    }

    void versionToExcludePatterns(String... versionsToExclude) {
        this.versionToExcludePatterns = Arrays.asList(versionsToExclude)
    }

    void includeConfigurations(String... configurations) {
        this.configurations.included.addAll(configurations)
    }

    void excludeConfigurations(String... configurations) {
        this.configurations.excluded.addAll(configurations)
    }

    static class IncludedExcludedConfigurationsHolder {
        final Set<String> included = []
        final Set<String> excluded = []
    }

    static class VersionPatterns {
        static final String ALPHA = caseInsensitive('.*[-.]alpha-?\\d*$')
        static final String BETA = caseInsensitive('.*[-.]beta-?\\d*$')
        static final String RC = caseInsensitive('.*[-.]RC-?\\d*$')
        static final String CR = caseInsensitive('.*[-.]CR-?\\d*$')
        static final String SNAPSHOT = caseInsensitive('.*-SNAPSHOT$')

        static String caseInsensitive(String pattern) {
            return "(?i)$pattern"
        }
    }
}
