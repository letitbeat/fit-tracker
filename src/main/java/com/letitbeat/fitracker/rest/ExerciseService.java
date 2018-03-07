package com.letitbeat.fitracker.rest;

import com.letitbeat.fitracker.jpa.domain.Exercise;
import com.letitbeat.fitracker.jpa.domain.Enums;
import com.letitbeat.fitracker.jpa.domain.Enums.ExerciseType;
import io.swagger.annotations.Api;
import java.util.List;
import java.util.Map;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

@Path("/api/v1/exercise")
@Api(value = "Exercise Service")
public interface ExerciseService {

	/**
	 * Get the exercise for a given exerciseId.
	 *
	 * @param exerciseId
	 *            id to search
	 * @return the exercise for the given exerciseId
	 */
	@GET
	@Path("/{exerciseId}")
	@Nonnull
	@Produces(MediaType.APPLICATION_JSON)
	Exercise getExerciseById(@Nonnull @PathParam("exerciseId") Long exerciseId);

	/**
	 * Get the exercises with the given description.
	 *
	 * @param description
	 *            description to search
	 * @return the exercises for the given description
	 */
	@GET
	@Path("/")
	@Nonnull
	@Produces(MediaType.APPLICATION_JSON)
	List<Exercise> getExerciseByDescription(@Nullable @QueryParam("description") String description);

	/**
	 * Persists a given exercise.
	 *
	 * @param exercise to persist
	 * @return the persisted exercise with created id
	 * @throws RuntimeException if there is an error please throw an appropriate exception here
	 */
	@POST
	@Path("/")
	@Nonnull
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	Exercise createExercise(@Nonnull Exercise exercise);

	/**
	 * Updates an existing exercise.
	 *
	 * @param exercise to update
	 * @return the updated exercise
	 * @throws RuntimeException if there is an error please throw an appropriate exception here
	 */
	@PUT
	@Path("/")
	@Nonnull
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	Exercise updateExercise(@Nonnull Exercise exercise);

	/**
	 * Deletes an exercise with the given exerciseId.
	 *
	 * @param exerciseId id to delete
	 * @throws RuntimeException if there is an error please throw an appropriate exception here
	 */
	@DELETE
	@Path("/{exerciseId}")
	@Produces(MediaType.APPLICATION_JSON)
	void deleteExercise(@Nonnull @PathParam("exerciseId") Long exerciseId);

	/**
	 * Returns a list of exercises for a specific user and some filter items (type + date).
	 *
	 * @param userId who did the exercise
	 * @param exerciseType filter: type of the exercise
	 * @param date filter: date ("yyyy-MM-dd") of the exercise
	 * @return a list of exercises
	 * @throws RuntimeException if there is an error please throw an appropriate exception here
	 */
	@GET
	@Path("/user/{userId}")
	@Nonnull
	@Produces(MediaType.APPLICATION_JSON)
	List<Exercise> getExerciseByTypeAndDate(@Nonnull @PathParam("userId") Long userId,
			@Nullable @QueryParam("type") Enums.ExerciseType exerciseType,
			@Nullable @QueryParam("date") String date);

	/**
	 * Calculate the ranking for the given user ids. The Calculation based on the exercises the user
	 * has done. The first in the list is the user with the highest score.
	 *
	 * @param userIds list of user ids
	 * @return the user list orders by there calculated exercise points
	 *
	 */
	@GET
	@Path("/ranking")
	@Nonnull
	@Produces(MediaType.APPLICATION_JSON)
	List<Long> getRanking(@Nonnull @QueryParam("userIds") List<Long> userIds);

	/**
	 * Returns a map containing the times a user has completed each exercise based on the type,
	 * in the past 4 weeks. If there is an exercise the user has not done, it will
	 * return 0 as the number of times for that type.
	 *
	 * @param userId the user who did the exercise
	 * @return a map containing the type and the number of times each exercise was completed in the
	 * past 4 weeks.
	 */
	@GET
	@Path("/user/{userId}/stats")
	@Nonnull
	@Produces(MediaType.APPLICATION_JSON)
	Map<ExerciseType, Long> getStats(@Nonnull  @PathParam("userId") Long userId);
}
