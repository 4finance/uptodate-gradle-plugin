package io.fourfinanceit.uptodate.finder

import io.fourfinanceit.uptodate.UptodatePluginExtension
import io.fourfinanceit.uptodate.dependency.Dependency

interface NewVersionFinderFactory {

    NewVersionFinder create(UptodatePluginExtension uptodatePluginExtension, List<Dependency> dependencies)
}
