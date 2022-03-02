#--------------------------#
# APP-TYPE: CONFIG         #
#--------------------------#
# see ../../docker.README.md for details

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

# PLANit version
ENV VERSION 0.0.1a1

# user specific environmental variables to override:
#---------------------------------------------------
ENV OUTPUT ""
#---------------------------------------------------

RUN mkdir -p /app/jar
RUN mkdir  /input
RUN mkdir  /output

# copy built jar from previous stage - intermediate results are discarded in image
COPY --from=build /app/target/planit-aurin-matsim-*.jar /app/jar 

# run the executable jar with --type default_config
CMD ["sh", "-c", "java -jar /app/jar/planit-aurin-matsim-${VERSION}.jar \
  --type default_config \
  --output ${OUTPUT}\
  "]

       