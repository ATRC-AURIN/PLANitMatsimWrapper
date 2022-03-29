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

# install bash and dos2unix
RUN apk add --no-cache bash dos2unix

# copy built jar from previous stage - intermediate results are discarded in image
RUN mkdir -p /app/jar
COPY --from=build /app/target/planit-aurin-matsim-*.jar /app/jar/ 

COPY atrc/atrc_entrypoint.sh .

# run dos2unix on the entrypoint script to prevent incorrect
# EOL characters being added if building image on windows
RUN dos2unix /atrc_entrypoint.sh

# specify default command
CMD ["sh", "./atrc_entrypoint.sh"]

