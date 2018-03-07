package com.letitbeat.fitracker.rest;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

import com.google.inject.Inject;
import com.letitbeat.fitracker.TestClientService;
import com.letitbeat.fitracker.AbstractIntegrationTest;
import com.letitbeat.fitracker.jpa.domain.Enums;
import com.letitbeat.fitracker.jpa.domain.Exercise;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import org.joda.time.DateTime;
import org.junit.Test;

public class RankingBasicTest extends AbstractIntegrationTest {

	@Inject
	private TestClientService testClientService;

	@Test
	public void testRankingList() {
		final long userId1 = 20L;
		final long userId2 = 21L;

		final Exercise exercise1ToInsert = new Exercise();
		exercise1ToInsert.setDescription("Task");
		exercise1ToInsert.setDuration(14400);
		exercise1ToInsert.setDistance(0);
		exercise1ToInsert.setCalories(500);
		exercise1ToInsert.setStartTime(Calendar.getInstance().getTime());
		exercise1ToInsert.setType(Enums.ExerciseType.OTHER);
		exercise1ToInsert.setUserId(userId1);

		final Exercise persistedExercise1 = testClientService.createExercise(exercise1ToInsert);
		assertNotNull(persistedExercise1);
		assertNotNull(persistedExercise1.getId());

		final Exercise exercise2ToInsert = new Exercise();
		exercise2ToInsert.setDescription("Heavy Task");
		exercise2ToInsert.setDuration(7200);
		exercise2ToInsert.setDistance(1500);
		exercise2ToInsert.setCalories(700);
		exercise2ToInsert.setStartTime(Calendar.getInstance().getTime());
		exercise2ToInsert.setType(Enums.ExerciseType.OTHER);
		exercise2ToInsert.setUserId(userId2);

		final Exercise persistedExercise2 = testClientService.createExercise(exercise2ToInsert);
		assertNotNull(persistedExercise2);
		assertNotNull(persistedExercise2.getId());

		final List<Long> ranking = testClientService.getRanking(Arrays.asList(userId1, userId2));
		assertNotNull(ranking);
		assertThat(ranking.size(), is(2));
		assertThat(ranking.get(0), is(userId2));
		assertThat(ranking.get(1), is(userId1));
	}

	@Test
	public void testRankingListEmpty() {
		final List<Long> rankingList = testClientService.getRanking(Arrays.asList(1L, 2L, 3L));
		assertNotNull(rankingList);
		assertThat(rankingList.size(), is(0));
	}

	@Test
	public void testRankingListRepeatedType() {
		final long userId1 = 22L;
		final long userId2 = 23L;

		final Exercise exercise1ToInsert = new Exercise();
		exercise1ToInsert.setDescription("Task");
		exercise1ToInsert.setDuration(14400);
		exercise1ToInsert.setDistance(0);
		exercise1ToInsert.setCalories(500);
		exercise1ToInsert.setStartTime(Calendar.getInstance().getTime());
		exercise1ToInsert.setType(Enums.ExerciseType.OTHER);
		exercise1ToInsert.setUserId(userId1);

		final Exercise persistedExercise1 = testClientService.createExercise(exercise1ToInsert);
		assertNotNull(persistedExercise1);
		assertNotNull(persistedExercise1.getId());

		final DateTime dt = new DateTime(Calendar.getInstance().getTime());
		final Date date2 = dt.minusDays(1).toDate();

		final Exercise exercise2ToInsert = new Exercise();
		exercise2ToInsert.setDescription("Task");
		exercise2ToInsert.setDuration(14400);
		exercise2ToInsert.setDistance(0);
		exercise2ToInsert.setCalories(500);
		exercise2ToInsert.setStartTime(date2);
		exercise2ToInsert.setType(Enums.ExerciseType.OTHER);
		exercise2ToInsert.setUserId(userId1);

		final Exercise persistedExercise2 = testClientService.createExercise(exercise2ToInsert);
		assertNotNull(persistedExercise2);
		assertNotNull(persistedExercise2.getId());

		final Exercise exercise3ToInsert = new Exercise();
		exercise3ToInsert.setDescription("Pushup");
		exercise3ToInsert.setDuration(7200);
		exercise3ToInsert.setDistance(1500);
		exercise3ToInsert.setCalories(700);
		exercise3ToInsert.setStartTime(Calendar.getInstance().getTime());
		exercise3ToInsert.setType(Enums.ExerciseType.OTHER);
		exercise3ToInsert.setUserId(userId2);

		final Exercise persistedExercise3 = testClientService.createExercise(exercise3ToInsert);
		assertNotNull(persistedExercise3);
		assertNotNull(persistedExercise3.getId());

		final List<Long> ranking = testClientService.getRanking(Arrays.asList(userId1, userId2));
		assertNotNull(ranking);
		assertThat(ranking.size(), is(2));
		assertThat(ranking.get(0), is(userId1));
		assertThat(ranking.get(1), is(userId2));
	}

}
