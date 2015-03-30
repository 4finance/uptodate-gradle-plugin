package com.ofg.uptodate

import org.slf4j.Logger

class LoggerProxy {

    void lifecycle(Logger logger, String message) {
        logger.lifecycle(message)
    }

    void info(Logger logger, String message) {
        logger.info(message)
    }

    void debug(Logger logger, String message) {
        logger.debug(message)
    }
}
