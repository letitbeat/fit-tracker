package com.letitbeat.fitracker.jpa.dao;

import com.letitbeat.fitracker.jpa.domain.Exercise;
import com.letitbeat.fitracker.jpa.domain.Enums;
import java.util.Date;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface ExerciseDao extends BaseDao<Exercise> {

	/**
	 * Returns a list of exercises with the given description
	 *
	 * @param description of the exercise
	 * @return filters list of exercise
	 */
	@Nonnull
	List<Exercise> findByDescription(@Nullable String description);

	/**
	 * Returns a list of exercises for a specific user and from specific date.
	 *
	 * @param userId who did the exercise
	 * @param fromDate filter: from the date ("yyyy-MM-dd") of the exercises
	 * @return list of exercises
	 */
	@Nonnull
	List<Exercise> findByDate(@Nonnull Long userId, @Nonnull Date fromDate);

	/**
	 * Returns a list of exercises for a specific user and some filter items (type + date).
	 *
	 * @param userId who did the exercise
	 * @param exerciseType filter: type of the exercise
	 * @param date filter: date ("yyyy-MM-dd") of the exercise
	 * @return a list of exercises
	 */
	@Nonnull
	List<Exercise> findByTypeAndDate(@Nonnull Long userId, @Nullable Enums.ExerciseType exerciseType,
			@Nullable Date date);
}
