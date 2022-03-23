# -----------------------------------------------------
# -----------------------------------------------------
# STAGE 1 - BUILD
# Maven build
FROM maven:3.8.4-jdk-11 as build

RUN mkdir -p /app/src
COPY ./src /app/src
COPY pom.xml /app
WORKDIR /app
RUN mvn clean install -Dmaven.test.skip=true


# -----------------------------------------------------
# -----------------------------------------------------
# STAGE 2 - BUILD
# EXECUTABLE JAR + Java environment
FROM adoptopenjdk/openjdk11:jre-11.0.6_10-alpine

# copy built jar from previous stage - intermediate results are discarded in image
RUN mkdir -p /app/jar
COPY --from=build /app/target/planit-aurin-matsim-*.jar /app/jar/ 

COPY atrc/run_workflow_from_params.sh .

# specify default command
CMD ["sh", "./run_workflow_from_params.sh"]

