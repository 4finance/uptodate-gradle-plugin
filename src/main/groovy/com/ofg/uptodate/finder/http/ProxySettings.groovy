package com.ofg.uptodate.finder.http

import groovy.transform.Immutable
import groovy.transform.PackageScope

@Immutable
@PackageScope
class ProxySettings {

    String hostname
    int port
    String scheme
}
