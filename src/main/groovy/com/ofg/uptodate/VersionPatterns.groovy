package com.ofg.uptodate

class VersionPatterns {

    public static final String ALPHA = caseInsensitive('.*[-.]alpha-?\\d*$')
    public static final String BETA = caseInsensitive('.*[-.]beta-?\\d*$')
    public static final String RC = caseInsensitive('.*[-.]RC-?\\d*$')
    public static final String CR = caseInsensitive('.*[-.]CR-?\\d*$')
    public static final String SNAPSHOT = caseInsensitive('.*-SNAPSHOT$')
    public static final String MILESTONE = caseInsensitive('.*M[0-9]+$')
    public static final String RELEASE = caseInsensitive('^R[0-9]+$')
    public static final List<String> ALL_PATTERNS = [ALPHA, BETA, RC, CR, SNAPSHOT, MILESTONE, RELEASE]

    public static String caseInsensitive(String pattern) {
        return "(?i)$pattern"
    }
}
