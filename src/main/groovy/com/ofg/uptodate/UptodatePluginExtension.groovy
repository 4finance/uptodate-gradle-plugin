package com.ofg.uptodate

class UptodatePluginExtension {
    String mavenRepo = MavenNewVersionFinder.MAVEN_CENTRAL_REPO_URL
    final IncludedExcludedConfigurationsHolder configurations = new IncludedExcludedConfigurationsHolder()
    int connectionTimeout = 5000
    List<String> versionToExcludePatterns = ['(?i).*[-.]alpha.*', '(?i).*[-.]beta.*', '(?i).*?RC\\d*', '(?i).*?CR\\d*']

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
