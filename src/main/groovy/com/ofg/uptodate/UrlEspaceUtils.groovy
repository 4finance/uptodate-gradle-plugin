package com.ofg.uptodate

import static java.net.URLEncoder.encode

class UrlEspaceUtils {

    public static final String URL_ENCODING = 'UTF-8'

    public static String escape(String paramValue) {
        return encode(paramValue, URL_ENCODING)
    }

}
