package com.ofg.uptodate.finder

import groovy.transform.Immutable
import groovy.transform.PackageScope

@PackageScope
@Immutable
class HttpConnectionSettings {
    
    String url
    ProxySettings proxySettings
    int poolSize
    int timeout
}
