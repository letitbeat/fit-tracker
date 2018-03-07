package com.letitbeat.fitracker.rest;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import com.google.inject.Inject;
import com.letitbeat.fitracker.TestClientService;
import com.letitbeat.fitracker.jpa.domain.Exercise;
import com.letitbeat.fitracker.AbstractIntegrationTest;
import com.letitbeat.fitracker.jpa.domain.Enums;
import java.util.List;
import javax.ws.rs.WebApplicationException;
import org.junit.Test;

public class ExerciseBasicTest extends AbstractIntegrationTest {

	@Inject
	private TestClientService testClientService;

	@Test(expected = RuntimeException.class)
	public void testNotValidExerciseId() {
		testClientService.getExercise(-1L);
	}

	@Test
	public void testInsert() {
		final long userId = 10L;
		final String date = "2016-06-01T14:23:35";

		final Exercise exerciseToInsert = new Exercise();
		exerciseToInsert.setDescription("Task");
		exerciseToInsert.setDuration(14400);
		exerciseToInsert.setDistance(0);
		exerciseToInsert.setCalories(500);
		exerciseToInsert.setStartTime(convertDate(date));
		exerciseToInsert.setType(Enums.ExerciseType.OTHER);
		exerciseToInsert.setUserId(userId);

		final Exercise persistedExercise = testClientService.createExercise(exerciseToInsert);
		assertNotNull(persistedExercise);
		assertNotNull(persistedExercise.getId());
		assertThat(persistedExercise.getDescription(), is("Task"));
		assertThat(persistedExercise.getDuration(), is(14400));
		assertThat(persistedExercise.getType(), is(Enums.ExerciseType.OTHER));
		assertThat(persistedExercise.getCalories(), is(500));
		assertThat(persistedExercise.getDistance(), is(0));
		assertThat(persistedExercise.getUserId(), is(userId));
		assertDate(persistedExercise.getStartTime(), convertDate(date));

		final Exercise selectedExercise = testClientService.getExercise(persistedExercise.getId());
		assertNotNull(selectedExercise);
		assertThat(selectedExercise.getId(), is(persistedExercise.getId()));
		assertThat(selectedExercise.getDescription(), is("Task"));
		assertThat(selectedExercise.getDuration(), is(14400));
		assertThat(selectedExercise.getType(), is(Enums.ExerciseType.OTHER));
		assertThat(selectedExercise.getCalories(), is(500));
		assertThat(selectedExercise.getDistance(), is(0));
		assertThat(selectedExercise.getUserId(), is(userId));
		assertDate(selectedExercise.getStartTime(), convertDate(date));
	}

	@Test
	public void testUpdate() {
		final long userId = 11L;
		final String date = "2016-06-02T17:03:15";

		final Exercise exerciseToInsert = new Exercise();
		exerciseToInsert.setDescription("Swim");
		exerciseToInsert.setDuration(14400);
		exerciseToInsert.setDistance(0);
		exerciseToInsert.setCalories(500);
		exerciseToInsert.setStartTime(convertDate(date));
		exerciseToInsert.setType(Enums.ExerciseType.OTHER);
		exerciseToInsert.setUserId(userId);

		final Exercise persistedExercise = testClientService.createExercise(exerciseToInsert);
		assertNotNull(persistedExercise);
		assertNotNull(persistedExercise.getId());

		final Exercise exerciseToUpdate = new Exercise();
		exerciseToUpdate.setId(persistedExercise.getId());
		exerciseToUpdate.setDescription("Swim");
		exerciseToUpdate.setDuration(7200);
		exerciseToUpdate.setDistance(1500);
		exerciseToUpdate.setCalories(700);
		exerciseToUpdate.setStartTime(convertDate(date));
		exerciseToUpdate.setType(Enums.ExerciseType.OTHER);
		exerciseToUpdate.setUserId(userId);

		final Exercise updatedExercise = testClientService.updateExercise(exerciseToUpdate);
		assertNotNull(updatedExercise);
		assertThat(updatedExercise.getId(), is(persistedExercise.getId()));
		assertThat(updatedExercise.getDescription(), is("Swim"));
		assertThat(updatedExercise.getDuration(), is(7200));
		assertThat(updatedExercise.getType(), is(Enums.ExerciseType.OTHER));
		assertThat(updatedExercise.getCalories(), is(700));
		assertThat(updatedExercise.getDistance(), is(1500));
		assertThat(updatedExercise.getUserId(), is(userId));
		assertDate(updatedExercise.getStartTime(), convertDate(date));

		final Exercise selectedExercise = testClientService.getExercise(persistedExercise.getId());
		assertNotNull(selectedExercise);
		assertThat(selectedExercise.getId(), is(persistedExercise.getId()));
		assertThat(selectedExercise.getDescription(), is("Swim"));
		assertThat(selectedExercise.getDuration(), is(7200));
		assertThat(selectedExercise.getType(), is(Enums.ExerciseType.OTHER));
		assertThat(selectedExercise.getCalories(), is(700));
		assertThat(selectedExercise.getDistance(), is(1500));
		assertThat(selectedExercise.getUserId(), is(userId));
		assertDate(selectedExercise.getStartTime(), convertDate(date));
	}

	@Test
	public void testDelete() {
		final long userId = 12L;
		final String date = "2016-06-15T12:00:00";

		final Exercise exerciseToInsert = new Exercise();
		exerciseToInsert.setDescription("Coding Task");
		exerciseToInsert.setDuration(14400);
		exerciseToInsert.setDistance(0);
		exerciseToInsert.setCalories(500);
		exerciseToInsert.setStartTime(convertDate(date));
		exerciseToInsert.setType(Enums.ExerciseType.OTHER);
		exerciseToInsert.setUserId(userId);

		final Exercise persistedExercise = testClientService.createExercise(exerciseToInsert);
		assertNotNull(persistedExercise);
		assertNotNull(persistedExercise.getId());

		testClientService.deleteExercise(persistedExercise.getId());

		try {
			testClientService.getExercise(persistedExercise.getId());
			fail("'testClientService.getExercise(persistedExercise.getId())' should have thrown an exception.");
		} catch (RuntimeException e) {
		}
	}

	@Test
	public void testSelectByDescription() {
		final long userId = 13L;
		final String date = "2016-06-01T14:23:35";

		final Exercise exerciseToInsert = new Exercise();
		exerciseToInsert.setDescription("Coding Task testSelectByDescription");
		exerciseToInsert.setDuration(14400);
		exerciseToInsert.setDistance(0);
		exerciseToInsert.setCalories(500);
		exerciseToInsert.setStartTime(convertDate(date));
		exerciseToInsert.setType(Enums.ExerciseType.OTHER);
		exerciseToInsert.setUserId(userId);

		final Exercise persistedExercise = testClientService.createExercise(exerciseToInsert);
		assertNotNull(persistedExercise);
		assertNotNull(persistedExercise.getId());

		final List<Exercise> selectedExercises = testClientService.getExerciseByDescription("Coding Task testSelectByDescription");
		assertNotNull(selectedExercises);

		assertThat("timestamp", selectedExercises.size(), greaterThanOrEqualTo(1));
		assertThat(selectedExercises, contains(hasProperty("id", is(persistedExercise.getId()))));
	}

	@Test
	public void testSelectByTypeAndDate() {
		final long userId = 14L;
		final String date = "2016-06-19T11:00:00";

		final Exercise exerciseToInsert = new Exercise();
		exerciseToInsert.setDescription("Coding Task");
		exerciseToInsert.setDuration(14400);
		exerciseToInsert.setDistance(0);
		exerciseToInsert.setCalories(500);
		exerciseToInsert.setStartTime(convertDate(date));
		exerciseToInsert.setType(Enums.ExerciseType.OTHER);
		exerciseToInsert.setUserId(userId);

		final Exercise persistedExercise = testClientService.createExercise(exerciseToInsert);
		assertNotNull(persistedExercise);
		assertNotNull(persistedExercise.getId());

		final List<Exercise> selectedExercises = testClientService.getExercises(userId, Enums.ExerciseType.OTHER, "2016-06-19");
		assertNotNull(selectedExercises);
		assertThat(selectedExercises.size(), is(1));

		final Exercise selectedExercise = selectedExercises.get(0);
		assertThat(selectedExercise.getId(), is(persistedExercise.getId()));
		assertThat(selectedExercise.getDescription(), is("Coding Task"));
		assertThat(selectedExercise.getDuration(), is(14400));
		assertThat(selectedExercise.getType(), is(Enums.ExerciseType.OTHER));
		assertThat(selectedExercise.getCalories(), is(500));
		assertThat(selectedExercise.getDistance(), is(0));
		assertThat(selectedExercise.getUserId(), is(userId));
		assertDate(selectedExercise.getStartTime(), convertDate(date));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testNotValidInput() {
		final long userId = 14L;
		final String date = "2016-06-19T11:00:00";

		final Exercise exerciseToInsert = new Exercise();
		exerciseToInsert.setDuration(14400);
		exerciseToInsert.setDistance(0);
		exerciseToInsert.setCalories(500);
		exerciseToInsert.setStartTime(convertDate(date));
		exerciseToInsert.setType(Enums.ExerciseType.OTHER);
		exerciseToInsert.setUserId(userId);

		testClientService.createExercise(exerciseToInsert);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testNotValidDescription() {
		final long userId = 14L;
		final String date = "2016-06-19T11:00:00";

		final Exercise exerciseToInsert = new Exercise();
		exerciseToInsert.setDescription("*** Coding Task **");
		exerciseToInsert.setDuration(14400);
		exerciseToInsert.setDistance(0);
		exerciseToInsert.setCalories(500);
		exerciseToInsert.setStartTime(convertDate(date));
		exerciseToInsert.setType(Enums.ExerciseType.OTHER);
		exerciseToInsert.setUserId(userId);

		testClientService.createExercise(exerciseToInsert);
	}

	@Test(expected = WebApplicationException.class)
	public void testStartTimeConflict() {
		final long userId = 22L;
		final String date = "2017-06-19T11:00:00";
		final String date2 = "2017-06-19T12:00:00";

		final Exercise exerciseToInsert = new Exercise();
		exerciseToInsert.setDescription("Task");
		exerciseToInsert.setDuration(14400);
		exerciseToInsert.setDistance(0);
		exerciseToInsert.setCalories(500);
		exerciseToInsert.setStartTime(convertDate(date));
		exerciseToInsert.setType(Enums.ExerciseType.OTHER);
		exerciseToInsert.setUserId(userId);

		final Exercise persistedExercise = testClientService.createExercise(exerciseToInsert);
		assertNotNull(persistedExercise);
		assertNotNull(persistedExercise.getId());

		final Exercise exerciseToInsert2 = new Exercise();
		exerciseToInsert2.setDescription("Coding Task between interval");
		exerciseToInsert2.setDuration(14400);
		exerciseToInsert2.setDistance(0);
		exerciseToInsert2.setCalories(500);
		exerciseToInsert2.setStartTime(convertDate(date2));
		exerciseToInsert2.setType(Enums.ExerciseType.OTHER);
		exerciseToInsert2.setUserId(userId);

		testClientService.createExercise(exerciseToInsert2);
	}

}
