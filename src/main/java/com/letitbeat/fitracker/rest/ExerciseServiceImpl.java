package com.letitbeat.fitracker.rest;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.letitbeat.fitracker.jpa.dao.ExerciseDao;
import com.letitbeat.fitracker.jpa.domain.Exercise;
import com.letitbeat.fitracker.jpa.domain.Enums;
import com.letitbeat.fitracker.jpa.domain.Enums.ExerciseType;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response.Status;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class ExerciseServiceImpl implements ExerciseService {

	private static final Logger log = LoggerFactory.getLogger(ExerciseServiceImpl.class);

	private final ExerciseDao exerciseDao;

	@Inject
	ExerciseServiceImpl(final ExerciseDao exerciseDao) {
		this.exerciseDao = exerciseDao;
	}

	@Nonnull
	@Override
	public Exercise getExerciseById(@Nonnull final Long exerciseId) {
		log.debug("Get exercise by id.");

		final Exercise exercise = exerciseDao.findById(exerciseId);
		if (exercise == null) {
			throw new NotFoundException("Exercise with id = " + exerciseId + " could not be found.");
		}

		return exercise;
	}

	@Nonnull
	@Override
	public List<Exercise> getExerciseByDescription(@Nullable final String description) {
		log.debug("Get exercise by description.");

		return exerciseDao.findByDescription(description);
	}

	@Nonnull
	@Override
	public Exercise createExercise(@Nonnull final Exercise exercise) {
		log.debug("Create exercise");

		validateInput(exercise);

		validateTimespan(exercise);

		return exerciseDao.create(exercise);
	}

	@Nonnull
	@Override
	public Exercise updateExercise(@Nonnull final Exercise exercise) {
		log.debug("Update exercise");

		validateInput(exercise);

		final Exercise persistedExercise = exerciseDao.findById(exercise.getId());

		if(persistedExercise == null) {
			throw new NotFoundException("Exercise with id = " + exercise.getId() + " could not be found.");
		}

		if (!exercise.getType().equals(persistedExercise.getType())) {
			throw new IllegalArgumentException(
					"Provided exercise type " + exercise.getType() + " do not match, expected: "
							+ persistedExercise.getType());
		}

		return exerciseDao.update(exercise);
	}

	@Override
	public void deleteExercise(@Nonnull final Long exerciseId) {
		log.debug(String.format("Delete exercise id: %d", exerciseId));

		final Exercise exercise = exerciseDao.findById(exerciseId);
		if (exercise == null) {
			throw new NotFoundException("Exercise with id = " + exerciseId + " could not be found.");
		}

		exerciseDao.deleteById(exerciseId);
	}

	@Nonnull
	@Override
	public List<Exercise> getExerciseByTypeAndDate(
			@Nonnull final Long userId,
			@Nullable final Enums.ExerciseType exerciseType,
			@Nullable final String dateString) {
		log.debug(
				String.format("Get exercise for userId: %d type: %s and date: %s", userId, exerciseType,
						dateString));

		Date dateParameter = null;
		if (dateString != null) {
			try {
				dateParameter = DateUtils.parseDate(dateString, "yyyy-MM-dd");
			} catch (ParseException e) {
				log.error(e.getMessage());
				throw new IllegalArgumentException(
						"Date must be in the format yyyy-MM-dd, provided: " + dateString);
			}
		}

		return exerciseDao.findByTypeAndDate(userId, exerciseType, dateParameter);
	}

	@Nonnull
	@Override
	public List<Long> getRanking(@Nonnull final List<Long> userIds) {

		log.debug(String.format("Get ranking for users: %s", userIds));

		if (userIds == null || userIds.isEmpty()) {
			return Collections.emptyList();
		}

		Map<Long, Double> scoreByUser = new HashMap<>();

		for (Long userId : userIds) {
			Double score = getUserScore(userId);
			if (score > 0) {
				scoreByUser.put(userId, score);
			}
		}

		return scoreByUser.entrySet().stream()
				.sorted(Map.Entry.<Long, Double>comparingByValue().reversed())
				.map(Map.Entry::getKey)
				.collect(Collectors.toList());
	}

	@Nonnull
	@Override
	public Map<ExerciseType, Long> getStats(@Nonnull final Long userId) {

		log.debug(String.format("Get user stats for user: %s", userId));

		final Date initDate = subtractDays(new Date(), 30);
		final List<Exercise> userExercises = exerciseDao.findByDate(userId, initDate);

		EnumMap<ExerciseType, Long> stats = userExercises.stream()
				.collect(Collectors.groupingBy(Exercise::getType, () -> new EnumMap<>(ExerciseType.class),
						Collectors.counting()));

		EnumSet.allOf(ExerciseType.class).forEach(e -> stats.putIfAbsent(e, 0L));

		return stats;
	}

	/**
	 * Performs a calculation of the user points based on the exercises a given user has completed in
	 * the last 4 weeks.
	 *
	 * The points are calculated as follows: - A user gets points for each exercise he has completed
	 * in the past 4 weeks. - A user gets one point per minute of the duration of the exercise plus
	 * the burnt kilo calories. - Each time a user performs the same type of exercise again it is
	 * worth 10% less (Make sure to look at the newest exercises first).
	 *
	 * Example: A user ran 4 times in the past 4 weeks (let's say once per week). This weeks run is
	 * worth 100%. The oldest run is worth only 70% of the calculated points for the exercise.
	 *
	 * - Each exercise type has a multiplication factor for the point calculation.
	 *
	 * @param userId the user id to calculate the exercise earned points.
	 * @return the score calculated for the given user
	 */
	private Double getUserScore(final Long userId) {

		final Date initDate = subtractDays(new Date(), 30);
		final List<Exercise> userExercises = exerciseDao.findByDate(userId, initDate);
		final Map<ExerciseType, Double> types = new EnumMap<>(ExerciseType.class);

		return userExercises.stream()
				.sorted((a, b) -> b.getStartTime().compareTo(a.getStartTime()))          // reverse order
				.mapToDouble(e -> {
							double points = (double)(e.getDuration() / 60) + e.getCalories();  // duration to mins
							double weight = types.getOrDefault(e.getType(), 1.0);
							types.put(e.getType(), weight - 0.1);
							return points * e.getType().getMultiplier() * weight;
						}
				).sum();
	}

	/**
	 * Validates the exercise fields, all fields must be required, if not an exception is thrown
	 * indicating the missing field.
	 *
	 * @param exercise the exercise to check its fields.
	 */
	private void validateInput(@Nonnull final Exercise exercise) {

		try {
			Objects.requireNonNull(exercise.getUserId(), "Exercise User Id must not be null");
			Objects.requireNonNull(exercise.getCalories(), "Exercise calories must not be null");
			Objects.requireNonNull(exercise.getDistance(), "Exercise distance must not be null");
			Objects.requireNonNull(exercise.getDuration(), "Exercise duration must not be null");
			Objects.requireNonNull(exercise.getStartTime(), "Exercise start time must not be null");
			Objects.requireNonNull(exercise.getType(), "Exercise type must not be null");
			Objects.requireNonNull(exercise.getDescription(), "Exercise description must not be null");

			if (!exercise.getDescription().matches("[a-zA-Z ]*")) {
				throw new IllegalArgumentException(
						"Exercise description only alphanumeric and spaces allowed");
			}
		} catch (NullPointerException e) {
			throw new IllegalArgumentException(e.getMessage(), e);
		} catch (IllegalArgumentException e) {
			throw e;
		}

	}

	/**
	 * Checks if there is another exercise for the given user taking place at the same start time
	 * or during the exercise duration. If that is the case, will throw an exception indicating
	 * that a conflict occurs.
	 *
	 * @param exercise the exercise to check.
	 * @throws RuntimeException if there is a conflict with the exercise start time.
	 */
	private void validateTimespan(@Nonnull final Exercise exercise) {

		Objects.requireNonNull(exercise, "Exercise must not be null");

		final Date exerciseStartTime = exercise.getStartTime();
		final List<Exercise> exercises = exerciseDao
				.findByDate(exercise.getUserId(), truncateTime(exerciseStartTime));

		for (Exercise persistedExercise : exercises) {

			long startTime = persistedExercise.getStartTime().getTime();
			long endTime = startTime + persistedExercise.getDuration() * 1000; // duration is in seconds

			if (exerciseStartTime.equals(persistedExercise.getStartTime())
					|| (exerciseStartTime.after(persistedExercise.getStartTime())
					&& exerciseStartTime.before(new Date(endTime)))) {
				throw new WebApplicationException(
						"There is already an exercise taking place for the given start time: " +
								exerciseStartTime.toString(), Status.CONFLICT);
			}
		}
	}

	/**
	 * Removes the time part for a given date
	 *
	 * For example if you have the date 07-06-2017 12:15:23, this method will truncate the time
	 * and return 07-06-2017 00:00:00.
	 *
	 * @param date the date to work with
	 * @return the date with the time truncated.
	 */
	private Date truncateTime(Date date) {
		return DateUtils.truncate(date, Calendar.DATE);
	}

	/**
	 * Subtracts from a given date the number of given days.
	 *
	 * @param date the date to subtract the days
	 * @param days the number of days to subtract from date
	 * @return the new date with the subtracted days.
	 */
	private Date subtractDays(final Date date, final int days) {
		GregorianCalendar cal = new GregorianCalendar();
		cal.setTime(date);
		cal.add(Calendar.DATE, -days);

		return cal.getTime();
	}
}
