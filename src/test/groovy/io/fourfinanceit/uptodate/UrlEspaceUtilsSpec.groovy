package io.fourfinanceit.uptodate

import spock.lang.Specification

class UrlEspaceUtilsSpec extends Specification {

    def 'should escape text to application/x-www-form-urlencoded format'() {
        given:
            String unespacedText = 'g:"junit"&a:"junit"'
        expect:
            'g%3A%22junit%22%26a%3A%22junit%22' == UrlEspaceUtils.escape(unespacedText)
    }
}
