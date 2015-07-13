package io.fourfinanceit.uptodate.finder

import groovy.transform.Immutable
import groovy.transform.PackageScope

@Immutable
class RepositorySettings {

    String repoUrl
    boolean ignoreRepo
}
