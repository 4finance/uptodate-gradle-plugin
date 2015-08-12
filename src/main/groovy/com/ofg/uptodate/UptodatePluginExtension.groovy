package com.ofg.uptodate

import com.ofg.uptodate.finder.jcenter.JCenterNewVersionFinderFactory
import com.ofg.uptodate.finder.maven.MavenNewVersionFinderFactory

class UptodatePluginExtension {
    
    /**
     * Set to true if you want project name to be printed in updates report header
     */
    boolean reportProjectName = false

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
    String jCenterRepo = JCenterNewVersionFinderFactory.JCENTER_REPO_URL

    /**
    * Link to Maven Central repo (or nay other that follows their convention)
    */
    String mavenRepo = MavenNewVersionFinderFactory.MAVEN_CENTRAL_REPO_URL
    
    /**
     * Number of maximal http connections to external repos
     */
    int simultaneousHttpConnections = 8

    /**
     * Limit in milliseconds for connections
     */
    int connectionTimeout = 5000

    /**
     * Proxy server hostname
     */
    String proxyHostname

    /**
     * Proxy server port. Defaults to -1, meaning the default port corresponding to proxyScheme
     */
    int proxyPort = -1

    /**
     * The name of the scheme to be used for connecting to the proxy server (usually 'http' or https', defaults to 'http') 
     */
    String proxyScheme = "http"
    
    /**
     * Patterns for versions that will not be considered as updates
     */
    final List<String> excludedVersionPatterns = VersionPatterns.ALL_PATTERNS

    void addExcludedVersionPatterns(String... patternsToExclude) {
        excludedVersionPatterns.addAll(patternsToExclude)
    }

    void setExcludedVersionPatterns(String... patternsToExclude) {
        setExcludedVersionPatterns(Arrays.asList(patternsToExclude))
    }

    void setExcludedVersionPatterns(List<String> patternsToExclude) {
        excludedVersionPatterns.clear()
        excludedVersionPatterns.addAll(patternsToExclude)
    }

    final FiltersHolder configurations = new FiltersHolder()

    /**
     * String names of configurations to include for checking
     * @param configurations
     */
    void includeConfigurations(String... configurations) {
        this.configurations.include(configurations)
    }

    /**
     * String names of configurations to exclude from checking
     * @param configurations
     */
    void excludeConfigurations(String... configurations) {
        this.configurations.exclude(configurations)
    }

    BuildBreakerConfiguration buildBreaker = new BuildBreakerConfiguration()

    void breakTheBuild(Closure closure) {
        closure.delegate = buildBreaker
        closure.call()
    }

    static class BuildBreakerConfiguration {

        /**
         * Define if you want to break the build if new versions were found
         */
        boolean enabled = false

        FiltersHolder filters = new FiltersHolder()

        /**
         * String names of patterns for either group or dependency name to include for checking
         * @param patterns
         */
        void includePatterns(String... patterns) {
            this.filters.include(patterns)
        }

        /**
         * String names of patterns for either group or dependency name to exclude for checking
         * @param patterns
         */
        void excludedPatterns(String... patterns) {
            this.filters.exclude(patterns)
        }

    }
}
