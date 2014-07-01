package com.ofg.uptodate

class UptodatePluginExtension {
    String mavenRepo = MavenNewVersionFinder.MAVEN_CENTRAL_REPO_URL
    final IncludedExcludedConfigurationsHolder configurations = new IncludedExcludedConfigurationsHolder()

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
