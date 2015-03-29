package com.ofg.uptodate.finder

import com.ofg.uptodate.UptodatePluginExtension
import com.ofg.uptodate.finder.dependency.Dependency

interface NewVersionFinderFactory {

    NewVersionFinder create(UptodatePluginExtension uptodatePluginExtension, List<Dependency> dependencies)
}
