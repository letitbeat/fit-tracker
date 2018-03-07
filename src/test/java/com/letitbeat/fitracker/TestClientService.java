package com.letitbeat.fitracker;

import com.google.inject.Inject;
import com.letitbeat.fitracker.jpa.domain.Enums;
import com.letitbeat.fitracker.jpa.domain.Enums.ExerciseType;
import com.letitbeat.fitracker.jpa.domain.Exercise;
import com.letitbeat.fitracker.rest.ExerciseService;
import java.util.List;
import java.util.Map;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class TestClientService {

	private final ExerciseService exerciseService;

	@Inject
	public TestClientService(final ExerciseService exerciseService) {
		this.exerciseService = exerciseService;
	}

	/**
	 * Get the exercise for a given exerciseId.
	 *
	 * @param exerciseId
	 *            id to search
	 * @return the exercise for the given exerciseId
	 * @throws RuntimeException
	 *             if there is an error please throw an appropriate exception here
	 */
	@Nonnull
	public Exercise getExercise(@Nullable final Long exerciseId) {
		return exerciseService.getExerciseById(exerciseId);
	}

	/**
	 * Get the exercises with the given description.
	 *
	 * @param description description to search
	 * @return the exercises for the given description
	 * @throws RuntimeException if there is an error please throw an appropriate exception here
	 */
	@Nonnull
	public List<Exercise> getExerciseByDescription(@Nullable final String description) {
		return exerciseService.getExerciseByDescription(description);
	}

	/**
	 * Persists a given exercise.
	 *
	 * @param exercise to persist
	 * @return the persisted exercise with created id
	 * @throws RuntimeException if there is an error please throw an appropriate exception here
	 */
	@Nonnull
	public Exercise createExercise(@Nullable final Exercise exercise) {
		return exerciseService.createExercise(exercise);
	}

	/**
	 * Updates an existing exercise.
	 *
	 * @param exercise to update
	 * @return the updated exercise
	 * @throws RuntimeException if there is an error please throw an appropriate exception here
	 */
	@Nonnull
	public Exercise updateExercise(@Nullable final Exercise exercise) {
		return exerciseService.updateExercise(exercise);
	}

	/**
	 * Deletes an exercise with the given exerciseId.
	 *
	 * @param exerciseId id to delete
	 * @throws RuntimeException if there is an error please throw an appropriate exception here
	 */
	public void deleteExercise(@Nullable final Long exerciseId) {
		exerciseService.deleteExercise(exerciseId);
	}

	/**
	 * Returns a list of exercises for a specific user and some filter items (type + date).
	 *
	 * @param userId who did the exercise
	 * @param exerciseType filter: type of the exercise
	 * @param date filter: date ("yyyy-MM-dd") of the exercise
	 * @return a list of exercises
	 * @throws RuntimeException if there is an error please throw an appropriate exception here
	 */
	@Nonnull
	public List<Exercise> getExercises(@Nullable final Long userId,
			@Nullable final Enums.ExerciseType exerciseType,
			@Nullable final String date) {
		return exerciseService.getExerciseByTypeAndDate(userId, exerciseType, date);
	}

	/**
	 * Calculate the ranking for the given user ids. The Calculation based on the exercises the user
	 * has done. The first in the list is the user with the highest score.
	 *
	 * @param userIds list of user ids
	 * @return the user list orders by there calculated exercise points
	 * @throws RuntimeException if there is an error please throw an appropriate exception here
	 */
	@Nonnull
	public List<Long> getRanking(@Nullable final List<Long> userIds) {
		return exerciseService.getRanking(userIds);
	}

	/**
	 * Returns a map containing the times a user has completed each exercise based on the type,
	 * in the past 4 weeks. If there is an exercise the user
	 * has not done, it will return 0 as the number of times.
	 *
	 * @param userId the user who did the exercise
	 * @return a map containing the type and the number of times each exercise was completed in the
	 * past 4 weeks.
	 */
	@Nonnull
	public Map<ExerciseType, Long> getStats(@Nonnull Long userId) {
		return exerciseService.getStats(userId);
	}
}
