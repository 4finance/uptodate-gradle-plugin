package com.ofg.uptodate
import com.ofg.uptodate.finder.JCenterNewVersionFinder
import com.ofg.uptodate.finder.MavenNewVersionFinder

class UptodatePluginExtension {
    
    /**
     * Set to true if you don't want to search for your dependency in Maven Central
     */
    boolean ignoreMavenCentral = false

    /**
     * Set to true if you don't want to search for your dependency in JCenter 
     */
    boolean ignoreJCenter = false

    /**
     * Switch to show whether you have missing JCenter dependency in your repositories 
     */
    boolean showMissingJCenterMessage = true

    /**
     * Link to JCenter repo (or nay other that follows their convention)
     */
    String jCenterRepo = JCenterNewVersionFinder.JCENTER_REPO_URL

    /**
    * Link to Maven Central repo (or nay other that follows their convention)
    */
    String mavenRepo = MavenNewVersionFinder.MAVEN_CENTRAL_REPO_URL
    
    /**
     * Number of maximal http connections to external repos
     */
    int simultaneousHttpConnections = 8

    /**
     * Limit in milliseconds for connections
     */
    int connectionTimeout = 5000

    /**
     * Patterns for versions that will not be considered as updates
     */
    final List<String> excludedVersionPatterns = VersionPatterns.ALL_PATTERNS

    void addExcludedVersionPatterns(String... patternsToExclude) {
        excludedVersionPatterns.addAll(patternsToExclude)
    }

    void setExcludedVersionPatterns(String... patternsToExclude) {
        excludedVersionPatterns.clear()
        excludedVersionPatterns.addAll(patternsToExclude)
    }

    final IncludedExcludedConfigurationsHolder configurations = new IncludedExcludedConfigurationsHolder()

    /**
     * String names of configurations to include for checking
     * @param configurations
     */
    void includeConfigurations(String... configurations) {
        this.configurations.included.addAll(configurations)
    }

    /**
     * String names of configurations to exclude from checking
     * @param configurations
     */
    void excludeConfigurations(String... configurations) {
        this.configurations.excluded.addAll(configurations)
    }

    static class IncludedExcludedConfigurationsHolder {
        final Set<String> included = []
        final Set<String> excluded = []
    }
}
