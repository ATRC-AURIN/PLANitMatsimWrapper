#--------------------------#
# APP-TYPE: DEFAULT_CONFIG #
#--------------------------#
# see ../../docker.README.md for details

# Base Alpine Linux based image with OpenJDK 11 only
FROM adoptopenjdk/openjdk11:jre-11.0.6_10-alpine

# PLANit version
ENV VERSION 0.0.1a1
ENV OUTPUT ""

RUN mkdir /opt/jar

# copy application JAR (with libraries inside) assuming image is built from root of repo
COPY ./target/planit-aurin-matsim-${VERSION}.jar /opt/jar

# run the executable jar with --type default_config
CMD ["sh", "-c", "java -jar /opt/jar/planit-aurin-matsim-${VERSION}.jar --type default_config --output ${OUTPUT}"]

       