#--------------------------#
# APP-TYPE: CONFIG         #
#--------------------------#
# see ../../docker.README.md for details

# Base Alpine Linux based image with OpenJDK 11 only
FROM adoptopenjdk/openjdk11:jre-11.0.6_10-alpine

# PLANit version
ENV VERSION 0.0.1a1

# user specific environmental variables to override:
#---------------------------------------------------
ENV OUTPUT ""
ENV MODES ""
ENV CRS ""
ENV NETWORK ""
ENV NETWORK_CRS ""

# cleaning the network is not supported here because it has no impact on the configuration file

ENV PLANS ""
ENV PLANS_CRS ""

# down sampling of a plans file is not supported here because it has no impact on the configuration file

ENV ACTIVITY_CONFIG ""
ENV STARTTIME ""
ENV ENDTIME ""
ENV FLOWCAP_FACTOR ""
ENV STORAGECAP_FACTOR ""
ENV ITERATIONS_MAX ""
#---------------------------------------------------

RUN mkdir -p /app/jar
RUN mkdir  /input
RUN mkdir  /output

# copy application JAR (with libraries inside) assuming image is built from root of repo
COPY ./target/planit-aurin-matsim-${VERSION}.jar /app/jar

# run the executable jar with --type config
CMD ["sh", "-c", "java -jar /app/jar/planit-aurin-matsim-${VERSION}.jar \
  --type config \
  --output ${OUTPUT} \
  --modes ${MODES} \
  --crs ${CRS} \
  --network ${NETWORK} \
  --network_crs ${NETWORK_CRS} \
  --plans ${PLANS}\
  --plans_crs ${PLANS_CRS}\
  --activity_config ${ACTIVITY_CONFIG}\
  --starttime ${STARTTIME}\
  --endtime ${ENDTIME}\
  --flowcap_factor ${FLOWCAP_FACTOR}\
  --storagecap_factor ${STORAGECAP_FACTOR}\
  --iterations_max ${ITERATIONS_MAX}\
  "]

       