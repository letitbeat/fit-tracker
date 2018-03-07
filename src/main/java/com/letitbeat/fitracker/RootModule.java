package com.letitbeat.fitracker;

import com.google.inject.AbstractModule;

import com.letitbeat.fitracker.jpa.JpaModule;
import com.letitbeat.fitracker.rest.RestServiceModule;

public class RootModule extends AbstractModule {

	@Override
	protected void configure() {
		install(new JpaModule());
		install(new RestServiceModule());

		bind(TestData.class).asEagerSingleton();
	}
}
