package com.letitbeat.fitracker.jpa;

import com.google.inject.Inject;
import com.google.inject.persist.PersistService;
import com.google.inject.persist.Transactional;

@Transactional
class JpaPersistenceInitializer {

	@Inject
	public JpaPersistenceInitializer(final PersistService persistenceService) {
		persistenceService.start();
	}
}
