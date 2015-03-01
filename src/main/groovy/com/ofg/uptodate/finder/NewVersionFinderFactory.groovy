package com.ofg.uptodate.finder

import com.ofg.uptodate.UptodatePluginExtension

interface NewVersionFinderFactory {

    NewVersionFinder build(UptodatePluginExtension uptodatePluginExtension)
}
