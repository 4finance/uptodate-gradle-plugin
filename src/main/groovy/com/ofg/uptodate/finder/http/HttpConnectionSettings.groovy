package com.ofg.uptodate.finder.http

import groovy.transform.Immutable

@Immutable
class HttpConnectionSettings {

    String url
    ProxySettings proxySettings
    int poolSize
    int timeout
}
