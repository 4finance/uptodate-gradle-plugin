package com.ofg.uptodate.reporting

import com.ofg.uptodate.UptodatePluginExtension
import com.ofg.uptodate.dependency.Dependency
import groovy.util.logging.Slf4j

@Slf4j
class BuildBreaker {

	private static final Set<String> INCLUDE_ALL = ['.*'] as Set<String>

	private final UptodatePluginExtension.BuildBreakerConfiguration buildBreakerConfiguration

	BuildBreaker(UptodatePluginExtension.BuildBreakerConfiguration buildBreakerConfiguration) {
		this.buildBreakerConfiguration = buildBreakerConfiguration
	}

	void breakTheBuildIfNecessary(List<Dependency> sortedUpdates) {
		if (!buildBreakerConfiguration.enabled) {
			log.debug('Build breaking is disabled')
			return
		}
		log.debug("Build breaking is enabled")
		List<Dependency> matchingDependencies = findAllDependenciesMatchingPatterns(sortedUpdates)
		log.debug("Found the following matching dependencies $matchingDependencies")
		breakTheBuildIfMatchingDepsWereFound(matchingDependencies)
	}

	private List<Dependency> findAllDependenciesMatchingPatterns(List<Dependency> sortedUpdates) {
		return sortedUpdates.findAll { Dependency dependency ->
			getPatternsForInclusionOrAllAsDefault().any { nameOrGroupMatchesRegex(dependency, it) } &&
					!buildBreakerConfiguration.filters.excluded.any { nameOrGroupMatchesRegex(dependency, it) }
		}
	}

	private Set<String> getPatternsForInclusionOrAllAsDefault() {
		Set<String> patternsForInclusion = buildBreakerConfiguration.filters.included
		return patternsForInclusion.empty ? INCLUDE_ALL : patternsForInclusion
	}

	private void breakTheBuildIfMatchingDepsWereFound(List<Dependency> matchingDependencies) {
		if (!matchingDependencies.empty) {
			throw new NewDependencyVersionsFoundException("For the following dependencies $matchingDependencies new versions have been found. Breaking the build")
		}
	}

	private static boolean nameOrGroupMatchesRegex(Dependency dependency, String regex) {
		return dependency.group.matches(regex) || dependency.name.matches(regex)
	}
}
