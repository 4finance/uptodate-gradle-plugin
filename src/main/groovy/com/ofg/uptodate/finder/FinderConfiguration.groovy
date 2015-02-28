package com.ofg.uptodate.finder

import groovy.transform.PackageScope

@PackageScope
class FinderConfiguration {

    boolean ignore
    HttpConnectionSettings httpConnectionSettings
    List<String> excludedVersionPatterns
}
