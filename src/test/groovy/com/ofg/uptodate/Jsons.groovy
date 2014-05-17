package com.ofg.uptodate

class Jsons {
    static final String JUNIT_RESPONSE = """
                                            {"responseHeader": {
                                                "status": 0,
                                                "QTime": 0,
                                                "params": {
                                                    "spellcheck": "true",
                                                    "fl": "id,g,a,latestVersion,p,ec,repositoryId,text,timestamp,versionCount",
                                                    "sort": "score desc,timestamp desc,g asc,a asc",
                                                    "indent": "off",
                                                    "q": "id:\\"junit:junit\\"",
                                                    "spellcheck.count": "5",
                                                    "wt": "json",
                                                    "version": "2.2"
                                                }
                                            }, "response": {
                                                "numFound": 1,
                                                "start": 0,
                                                "docs": [
                                                    {
                                                        "id": "junit:junit",
                                                        "g": "junit",
                                                        "a": "junit",
                                                        "latestVersion": "4.11",
                                                        "repositoryId": "central",
                                                        "p": "jar",
                                                        "timestamp": 1352920907000,
                                                        "versionCount": 20,
                                                        "text": ["junit", "junit", "-sources.jar", "-javadoc.jar", ".jar", ".pom"],
                                                        "ec": ["-sources.jar", "-javadoc.jar", ".jar", ".pom"]
                                                    }
                                                ]
                                            }, "spellcheck": {
                                                "suggestions": []
                                            }}
                                            """
    static final String HIBERNATE_RESPONSE = """
                                            {"responseHeader": {
                                                "status": 0,
                                                "QTime": 0,
                                                "params": {
                                                    "spellcheck": "true",
                                                    "fl": "id,g,a,latestVersion,p,ec,repositoryId,text,timestamp,versionCount",
                                                    "sort": "score desc,timestamp desc,g asc,a asc",
                                                    "indent": "off",
                                                    "q": "id:\\"org.hibernate:hibernate-core\\"",
                                                    "spellcheck.count": "5",
                                                    "wt": "json",
                                                    "version": "2.2"
                                                }
                                            }, "response": {
                                                "numFound": 1,
                                                "start": 0,
                                                "docs": [
                                                    {
                                                        "id": "org.hibernate:hibernate-core",
                                                        "g": "org.hibernate",
                                                        "a": "hibernate-core",
                                                        "latestVersion": "4.3.5.Final",
                                                        "repositoryId": "central",
                                                        "p": "jar",
                                                        "timestamp": 1396450371000,
                                                        "versionCount": 98,
                                                        "text": ["org.hibernate","hibernate-core","-sources.jar",".jar",".pom"],
                                                        "ec": ["-sources.jar", ".jar", ".pom"]
                                                    }
                                                ]
                                            }, "spellcheck": {
                                                "suggestions": []
                                            }}
                                            """

    static final String NOT_FOUND_GVM_RESPONSE = """
                                        {
                                            "responseHeader": {
                                                "status": 0,
                                                "QTime": 1,
                                                "params": {
                                                    "spellcheck": "true",
                                                    "fl": "id,g,a,latestVersion,p,ec,repositoryId,text,timestamp,versionCount",
                                                    "sort": "score desc,timestamp desc,g asc,a asc",
                                                    "indent": "off",
                                                    "q": "id:\\"net.gvmtool:gvm-sdk\\"",
                                                    "spellcheck.count": "5",
                                                    "wt": "json",
                                                    "version": "2.2"
                                                }
                                            },
                                            "response": {
                                                "numFound": 0,
                                                "start": 0,
                                                "docs": [ ]
                                            },
                                            "spellcheck": {
                                                "suggestions": [
                                                    "gvm",
                                                    {
                                                        "numFound": 5,
                                                        "startOffset": 16,
                                                        "endOffset": 23,
                                                        "suggestion": [
                                                            "jvm",
                                                            "gem",
                                                            "gsm",
                                                            "gcm",
                                                            "gvt"
                                                        ]
                                                    }
                                                ]
                                            }
                                        }
                                        """
}
