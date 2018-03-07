package com.letitbeat.fitracker.jpa.domain;

public final class Enums {

	public enum ExerciseType {
		RUNNING(2),
		CYCLING(2),
		SWIMMING(3),
		ROWING(2),
		WALKING(1),
		CIRCUIT_TRAINING(4),
		STRENGTH_TRAINING(3),
		FITNESS_COURSE(2),
		SPORTS(3),
		OTHER(1);

		private int multiplier;

		ExerciseType(int multiplier){
			this.multiplier = multiplier;
		}

		public int getMultiplier(){
			return this.multiplier;
		}
	}
}
