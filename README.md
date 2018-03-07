## Technologies used in the project

* Oracle/Open JDK 1.7 or 1.8 (must be pre-installed).
* [Gradle](http://gradle.org/) for build automation.
* [Guice](https://github.com/google/guice) for dependency injection.
* [JPA/Hibernate](http://hibernate.org/orm/) for persistence.
* [Jersey](https://jersey.java.net/) for REST endpoints.
* [Swagger UI](https://github.com/swagger-api/swagger-ui) for manual API testing and API documentation.
* [JUnit](http://junit.org/junit4/) for unit testing.
* [Mockito](http://mockito.org/) for mocked testing.
* [Rest Assured](https://github.com/rest-assured/rest-assured) for integration testing.

## How to Build and Run the Project

This project uses the [Gradle](https://gradle.org) build system, you can build the project locally just by typing the
following in the console:

```
./gradlew build
```

On Windows use `gradlew.bat` instead of `./gradlew`.

The output of the Gradle build is located in the  `build/` directory.

To run the project deploy the war-File in the `build/libs` directory to the application server of your choice.
Alternatively, you can use the Gradle Jetty plugin:

```
./gradlew jettyRun
```

### Swagger

Browse to the application root for API documentation:

[http://localhost:8080/](http://localhost:8080)

All REST endpoints can be tested locally with the Swagger UI frontend.

## How to Extend the Project

If you want to import the project in an IDE such as Eclipse or IntelliJ IDEA then Gradle provides a way to generate all
the necessary project files.

Generate Eclipse project:
```
./gradlew cleanEclipse eclipse
```

Generate IntelliJ IDEA project:
```
./gradlew cleanIdea idea
```

Alternatively, with IntelliJ IDEA you can also import the project from the Gradle model,
just follow [this guide](https://www.jetbrains.com/help/idea/2016.1/importing-project-from-gradle-model.html).

## Functionalities included

1. Exercise CRUD:
    - Persist new exercises:
        - Insert new exercises.
        - Update exercise.
        - All exercise fields should be mandatory for inserting (except the id) and updating.
        - Use an appropriate HTTP status code if the input is invalid.
        - Check if the provided description has a valid syntax (only alphnum + spaces)
          by using a simple regular expression.
        - During exercise persisting it should be checked that there
          is no other exercise already present for the user id, in the
          period (start + duration) where the new exercise will take place.
          If this is the case return an HTTP status code `Conflict` with appropriate
          error message.
        - While updating an exercise, the user id and type shouldn't change.
    - Delete an existing exercise by a given exercise id.
    - List all existing exercises for a given user id.
        - Add the ability to filter them by type and/or by date.
    
2. Included REST end point in the ExerciseService with the following:
        - Rank a list of user ids by the users' points:
        - the points are calculated as follows:
            - A user gets points for each exercise he has completed in the past 4 weeks.
            - A user gets one point per minute of the duration of the exercise plus the burnt kilo calories.
            - Each time a user performs the same type of exercise again it is worth 10% less (Make sure to look at the newest exercises first).
                Example: A user ran 4 times in the past 4 weeks (let's say once per week).
                This weeks run is worth 100%. The oldest run is worth only 70% of the calculated points for the exercise.
            - An exercise can't count for less than 0 points.
        - Each exercise type has a multiplication factor for the point calculation:
            - RUNNING - 2
            - CYCLING - 2
            - SWIMMING - 3
            - ROWING - 2
            - WALKING - 1
            - CIRCUIT_TRAINING - 4
            - STRENGTH_TRAINING - 3
            - FITNESS_COURSE - 2
            - SPORTS - 3
            - OTHER - 1
        - It returns an ordered list of user ids according to the users' points in descending order.
    
3. REST endpoint in the ExerciseService which returns a map containing the times a user has 
    completed each exercise based on the exercise type in the past 4 weeks. If there is an exercise the user 
    has not done, it will return 0 for that type as the number of times.      
