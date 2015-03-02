package com.ofg.uptodate.finder

import com.ofg.uptodate.UptodatePluginExtension

interface NewVersionFinderFactory {

    NewVersionFinder create(UptodatePluginExtension uptodatePluginExtension)
}
