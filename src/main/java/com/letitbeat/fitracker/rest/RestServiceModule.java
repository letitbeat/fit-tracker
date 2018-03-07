package com.letitbeat.fitracker.rest;

import com.google.inject.AbstractModule;

public class RestServiceModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(ExerciseService.class).to(ExerciseServiceImpl.class);
		bind(ObjectMapperProvider.class);
		bind(RestExceptionMapper.class);
	}
}
