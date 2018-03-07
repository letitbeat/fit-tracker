package com.letitbeat.fitracker.rest;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

import com.google.inject.Inject;
import com.letitbeat.fitracker.AbstractIntegrationTest;
import com.letitbeat.fitracker.TestClientService;
import com.letitbeat.fitracker.jpa.domain.Enums;
import com.letitbeat.fitracker.jpa.domain.Enums.ExerciseType;
import com.letitbeat.fitracker.jpa.domain.Exercise;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import org.joda.time.DateTime;
import org.junit.Test;


public class StatsBasicTest extends AbstractIntegrationTest {

  @Inject
  private TestClientService testClientService;

  @Test
  public void testGetEmptyStats() {
    Map<ExerciseType, Long> stats = testClientService.getStats(1L);

    assertNotNull(stats);
    for (ExerciseType type: ExerciseType.values()) {
      assertThat(stats.get(type), is(0L));
    }

  }

  @Test
  public void testGetUserStats() {
    final long userId = 30L;

    final Exercise exerciseToInsert = new Exercise();
    exerciseToInsert.setDescription("Coding Task");
    exerciseToInsert.setDuration(130);
    exerciseToInsert.setDistance(0);
    exerciseToInsert.setCalories(500);
    exerciseToInsert.setStartTime(Calendar.getInstance().getTime());
    exerciseToInsert.setType(Enums.ExerciseType.OTHER);
    exerciseToInsert.setUserId(userId);

    final Exercise persistedExercise = testClientService.createExercise(exerciseToInsert);
    assertNotNull(persistedExercise);
    assertNotNull(persistedExercise.getId());

    final DateTime dt = new DateTime(Calendar.getInstance().getTime());
    final Date date2 = dt.minusDays(1).toDate();

    final Exercise exerciseToInsert2 = new Exercise();
    exerciseToInsert2.setDescription("Coding Task between interval");
    exerciseToInsert2.setDuration(130);
    exerciseToInsert2.setDistance(0);
    exerciseToInsert2.setCalories(500);
    exerciseToInsert2.setStartTime(date2);
    exerciseToInsert2.setType(ExerciseType.CYCLING);
    exerciseToInsert2.setUserId(userId);

    final Exercise persistedExercise2 = testClientService.createExercise(exerciseToInsert2);
    assertNotNull(persistedExercise2);
    assertNotNull(persistedExercise2.getId());

    Map<ExerciseType, Long> statsByUser = testClientService.getStats(userId);

    assertNotNull(statsByUser);
    assertThat(statsByUser.get(ExerciseType.OTHER), is(1L));
    assertThat(statsByUser.get(ExerciseType.CYCLING), is(1L));
    assertThat(statsByUser.get(ExerciseType.CIRCUIT_TRAINING), is(0L));
    assertThat(statsByUser.get(ExerciseType.RUNNING), is(0L));

  }

  @Test
  public void testGetStatsRepeatedExercise() {
    final long userId = 35L;

    final Exercise exerciseToInsert = new Exercise();
    exerciseToInsert.setDescription("Coding Task");
    exerciseToInsert.setDuration(130);
    exerciseToInsert.setDistance(0);
    exerciseToInsert.setCalories(500);
    exerciseToInsert.setStartTime(Calendar.getInstance().getTime());
    exerciseToInsert.setType(ExerciseType.RUNNING);
    exerciseToInsert.setUserId(userId);

    final Exercise persistedExercise = testClientService.createExercise(exerciseToInsert);
    assertNotNull(persistedExercise);
    assertNotNull(persistedExercise.getId());

    final DateTime dt = new DateTime(Calendar.getInstance().getTime());
    final Date date2 = dt.minusDays(1).toDate();

    final Exercise exerciseToInsert2 = new Exercise();
    exerciseToInsert2.setDescription("Coding Task between interval");
    exerciseToInsert2.setDuration(130);
    exerciseToInsert2.setDistance(0);
    exerciseToInsert2.setCalories(500);
    exerciseToInsert2.setStartTime(date2);
    exerciseToInsert2.setType(ExerciseType.RUNNING);
    exerciseToInsert2.setUserId(userId);

    final Exercise persistedExercise2 = testClientService.createExercise(exerciseToInsert2);
    assertNotNull(persistedExercise2);
    assertNotNull(persistedExercise2.getId());

    Map<ExerciseType, Long> statsByUser = testClientService.getStats(userId);

    assertNotNull(statsByUser);
    assertThat(statsByUser.get(ExerciseType.OTHER), is(0L));
    assertThat(statsByUser.get(ExerciseType.CYCLING), is(0L));
    assertThat(statsByUser.get(ExerciseType.CIRCUIT_TRAINING), is(0L));
    assertThat(statsByUser.get(ExerciseType.RUNNING), is(2L));
  }
}
