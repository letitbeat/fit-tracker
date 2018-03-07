package com.letitbeat.fitracker.jpa.dao;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.persist.Transactional;
import com.letitbeat.fitracker.jpa.domain.Exercise;
import com.letitbeat.fitracker.jpa.domain.Enums;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.TemporalType;
import org.joda.time.DateTime;

@Transactional
public class ExerciseDaoImpl extends AbstractBaseDao<Exercise> implements ExerciseDao {

	@Inject
	ExerciseDaoImpl(final Provider<EntityManager> entityManagerProvider) {
		super(entityManagerProvider, Exercise.class);
	}

	@Nonnull
	@Override
	public List<Exercise> findByDescription(@Nullable String description) {
		if (description == null) {
			return Collections.emptyList();
		}

		description = description.toLowerCase();

		try {
			return getEntityManager()
					.createQuery("SELECT e FROM Exercise e WHERE LOWER(e.description) = :description")
					.setParameter("description", description)
					.getResultList();
		} catch (NoResultException e) {
			return Collections.emptyList();
		}
	}

	@Nonnull
	@Override
	public List<Exercise> findByDate(@Nonnull Long userId, @Nonnull Date fromDate) {

		String queryString = "SELECT e "
				+ "FROM Exercise e "
				+ "WHERE userId = :userId AND startTime >= :startTime";

		Query query = getEntityManager()
				.createQuery(queryString)
				.setParameter("userId", userId)
				.setParameter("startTime", fromDate);

		try {
			return query.getResultList();
		} catch (NoResultException e) {
			return Collections.emptyList();
		}
	}

	@Nonnull
	@Override
	public List<Exercise> findByTypeAndDate(@Nonnull Long userId,
			@Nullable Enums.ExerciseType exerciseType,
			@Nullable Date date) {

		String queryString = "SELECT e FROM Exercise e WHERE userId = :userId ";

		if (exerciseType != null) {
			queryString += "AND type = :type ";
		}

		if (date != null) {
			queryString += "AND startTime BETWEEN :startTime AND :endTime ";
		}

		Query query = getEntityManager()
				.createQuery(queryString)
				.setParameter("userId", userId);

		if (exerciseType != null) {
			query.setParameter("type", exerciseType);
		}

		if (date != null) {
			DateTime startTime = new DateTime(date.getTime());
			DateTime endTime = startTime.plusDays(1);

			query.setParameter("startTime", startTime.toDate(), TemporalType.DATE);
			query.setParameter("endTime", endTime.toDate(), TemporalType.DATE);
		}

		try {
			return query.getResultList();
		} catch (NoResultException e) {
			return Collections.emptyList();
		}
	}

}
