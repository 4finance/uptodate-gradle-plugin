package com.ofg.uptodate.reporting

import com.ofg.uptodate.LoggerProxy
import com.ofg.uptodate.UptodatePluginExtension
import com.ofg.uptodate.dependency.Dependency
import com.ofg.uptodate.dependency.DependencyGroupAndNameComparator
import groovy.util.logging.Slf4j

@Slf4j
class NewVersionProcessor {

    private static final boolean DO_NOT_MUTATE_ORIGINAL_COLLECTION = false
    private static final String NEW_VERSIONS_AVAILABLE = 'New versions available'
    private static final String NO_NEW_VERSIONS_AVAILABLE = 'No new versions available'

    public static final String NEW_VERSIONS_MESSAGE_HEADER = "${NEW_VERSIONS_AVAILABLE}:\n"
    public static final String NO_NEW_VERSIONS_MESSAGE = "${NO_NEW_VERSIONS_AVAILABLE}."

    private final LoggerProxy logger
    private final String projectName
    private final boolean reportProjectName
    private final BuildBreaker buildBreaker

    NewVersionProcessor(LoggerProxy logger, String projectName, UptodatePluginExtension uptodatePluginExtension) {
        this.logger = logger
        this.projectName = projectName
        this.reportProjectName = uptodatePluginExtension.reportProjectName
        this.buildBreaker = new BuildBreaker(uptodatePluginExtension.buildBreaker)
    }

    void reportUpdates(Set<Dependency> newVersions) {
        if (newVersions.isEmpty()) {
            logger.lifecycle(log, noNewVersionsMessage())
        } else {
            List<Dependency> sortedUpdates = newVersions.sort(DO_NOT_MUTATE_ORIGINAL_COLLECTION, new DependencyGroupAndNameComparator())
            logger.lifecycle(log, newVersionsReport(sortedUpdates))
            buildBreaker.breakTheBuildIfNecessary(sortedUpdates)
        }
    }

    private String newVersionsReport(newVersions) {
        return "${newVersionsMessageHeader()}${newVersions.join('\n')}"
    }

    private String newVersionsMessageHeader() {
        return reportProjectName ? "$NEW_VERSIONS_AVAILABLE for $projectName:\n" : NEW_VERSIONS_MESSAGE_HEADER
    }

    private String noNewVersionsMessage() {
        return reportProjectName ? "$NO_NEW_VERSIONS_AVAILABLE for $projectName." : NO_NEW_VERSIONS_MESSAGE
    }
}
