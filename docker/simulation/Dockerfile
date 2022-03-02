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
ENV MODES ""
ENV CRS ""
ENV NETWORK ""
ENV NETWORK_CRS ""
ENV NETWORK_CLEAN ""
ENV PLANS ""
ENV PLANS_CRS ""
ENV PLANS_SAMPLE ""
ENV ACTIVITY_CONFIG ""
ENV STARTTIME ""
ENV ENDTIME ""
ENV FLOWCAP_FACTOR ""
ENV STORAGECAP_FACTOR ""
ENV LINK_STATS ""
ENV ITERATIONS_MAX ""
ENV PT_STOPS_CSV ""
ENV OUTPUT ""
#---------------------------------------------------

RUN mkdir -p /app/jar
RUN mkdir  /input
RUN mkdir  /output

# copy built jar from previous stage - intermediate results are discarded in image
COPY --from=build /app/target/planit-aurin-matsim-*.jar /app/jar 

# copy test resources 
RUN mkdir -p /app/test/resources/Melbourne
COPY ./src/test/resources/Melbourne/* /app/test/resources/Melbourne

# run the executable jar with --type config
CMD ["sh", "-c", "java -jar /app/jar/planit-aurin-matsim-${VERSION}.jar \
  --type simulation \
  --modes ${MODES} \
  --crs ${CRS} \
  --network ${NETWORK} \
  --network_crs ${NETWORK_CRS} \
  --network_clean ${NETWORK_CLEAN} \
  --plans ${PLANS}\
  --plans_crs ${PLANS_CRS}\
  --plans_sample ${PLANS_SAMPLE}\
  --activity_config ${ACTIVITY_CONFIG}\
  --starttime ${STARTTIME}\
  --endtime ${ENDTIME}\
  --flowcap_factor ${FLOWCAP_FACTOR}\
  --storagecap_factor ${STORAGECAP_FACTOR}\
  --link_stats ${LINK_STATS} \
  --iterations_max ${ITERATIONS_MAX}\
  --pt_stops_csv ${PT_STOPS_CSV} \
  --output ${OUTPUT} \
  "]

       