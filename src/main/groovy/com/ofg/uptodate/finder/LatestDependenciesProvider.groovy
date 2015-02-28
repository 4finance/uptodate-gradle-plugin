package com.ofg.uptodate.finder

interface LatestDependenciesProvider {
    
    List<Dependency> findLatest(List<Dependency> dependencies, FinderConfiguration finderConfiguration)
}
