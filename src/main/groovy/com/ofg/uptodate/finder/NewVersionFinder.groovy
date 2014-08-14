package com.ofg.uptodate.finder

interface NewVersionFinder {
    List<Dependency> findNewer(List<Dependency> dependencies)
}