package com.ofg.uptodate

import org.slf4j.Logger

class LoggerProxy {
    void warn(Logger logger, String warning) {
        logger.warn(warning)
    }
    void info(Logger logger, String warning) {
        logger.info(warning)
    }
    void debug(Logger logger, String warning) {
        logger.debug(warning)
    }
}
