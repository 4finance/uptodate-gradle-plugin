package com.ofg.uptodate

import static com.ofg.uptodate.VersionPatterns.*

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
}
