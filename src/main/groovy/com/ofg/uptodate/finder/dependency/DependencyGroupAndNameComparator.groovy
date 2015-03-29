package com.ofg.uptodate.finder.dependency

class DependencyGroupAndNameComparator implements Comparator<Dependency> {

    @Override
    int compare(Dependency dep1, Dependency dep2) {
        int groupComparison = dep1.group <=> dep2.group
        if (groupComparison != 0) {
            return groupComparison
        } else {
            return dep1.name <=> dep2.name
        }
    }
}
