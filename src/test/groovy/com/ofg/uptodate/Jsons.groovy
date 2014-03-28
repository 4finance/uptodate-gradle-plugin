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
    static final String MOCKITO_RESPONSE = """
                                            {"responseHeader": {
                                                "status": 0,
                                                "QTime": 0,
                                                "params": {
                                                    "spellcheck": "true",
                                                    "fl": "id,g,a,latestVersion,p,ec,repositoryId,text,timestamp,versionCount",
                                                    "sort": "score desc,timestamp desc,g asc,a asc",
                                                    "indent": "off",
                                                    "q": "id:\\"org.mockito:mockito-core\\"",
                                                    "spellcheck.count": "5",
                                                    "wt": "json",
                                                    "rows": "20",
                                                    "version": "2.2"
                                                }
                                            }, "response": {
                                                "numFound": 1,
                                                "start": 0,
                                                "docs": [
                                                    {
                                                        "id": "org.mockito:mockito-core",
                                                        "g": "org.mockito",
                                                        "a": "mockito-core",
                                                        "latestVersion": "1.9.5",
                                                        "repositoryId": "central",
                                                        "p": "jar",
                                                        "timestamp": 1350209209000,
                                                        "versionCount": 17,
                                                        "text": ["org.mockito", "mockito-core", "-sources.jar", "-javadoc.jar", ".jar", ".pom"],
                                                        "ec": ["-sources.jar", "-javadoc.jar", ".jar", ".pom"]
                                                    }
                                                ]
                                            }, "spellcheck": {
                                                "suggestions": []
                                            }}
                                            """
}
