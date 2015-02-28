package com.ofg.uptodate.finder

import groovy.transform.PackageScope

@PackageScope
class HttpConnectionSettings {
    
    String url
    ProxySettings proxySettings
    int poolSize
    int timeout
}
