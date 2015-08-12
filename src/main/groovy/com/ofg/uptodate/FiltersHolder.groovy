package com.ofg.uptodate

class FiltersHolder {
	final IncludedExcludedPatternsHolder patternsHolder = new IncludedExcludedPatternsHolder()

	/**
	 * String names of configurations to include for checking
	 * @param configurations
	 */
	void include(String... configurations) {
		this.patternsHolder.included.addAll(configurations)
	}

	void exclude(String... configurations) {
		this.patternsHolder.excluded.addAll(configurations)
	}

	static class IncludedExcludedPatternsHolder {
		final Set<String> included = []
		final Set<String> excluded = []
	}

	Set<String> getIncluded() {
		return patternsHolder.included
	}

	Set<String> getExcluded() {
		return patternsHolder.excluded
	}
}
