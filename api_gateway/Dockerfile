# the first stage of our build will use gradle parent image
FROM gradle AS GRADLE_BUILD
# copy src code to the container
COPY . /usr/src/java-code/
WORKDIR /usr/src/java-code
# package our application code
RUN gradle build bootJar -x test

# the second stage of build will use open jdk
FROM openjdk:17
WORKDIR /usr/src/java-app
# copy only the artifacts we need from the first stage and discard the rest
COPY --from=GRADLE_BUILD /usr/src/java-code/build/libs/api_gateway-0.0.1-SNAPSHOT.jar ./api_gateway.jar
# set the startup command to execute the jar
CMD ["java", "-jar", "api_gateway.jar"]