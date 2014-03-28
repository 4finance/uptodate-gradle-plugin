package com.ofg.uptodate

interface NewVersionFinder {
    List<Dependency> findNewer(List<Dependency> dependencies)
}