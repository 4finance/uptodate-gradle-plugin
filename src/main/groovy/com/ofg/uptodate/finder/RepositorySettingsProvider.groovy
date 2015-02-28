package com.ofg.uptodate.finder

import com.ofg.uptodate.UptodatePluginExtension

interface RepositorySettingsProvider {

    RepositorySettings getFrom(UptodatePluginExtension uptodatePluginExtension)
}
