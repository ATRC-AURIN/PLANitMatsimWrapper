# Docker information

In this file we keep track how to use docker with this Java repository. Since this repository supports multiple ways of using MATSim (not all necessarily running a simulation), we provide multiple Docker images, one for each of the *--type* command line switches that represent the different uses of this repository.  

Given that Docker prefers to have the image built using a file named Dockerfile, we place each unique Dockerfile in its own directory under the root/docker dir, namely:

* /docker/simulation/Dockerfile
* /docker/config/Dockerfile
* /docker/default_config/Dockerfile

The first would equate to setting the command line switch to *--type simulation*, the second to *--type config*, the third to *--type default-config* as per the README.md.

# Creating the docker image for MATSim Wrapper

* We use the Dockerfile in the root dir to create the image.  
* We use Alpine as the Linux distro since it is small
* There is no official jdk-11 and Alpine Docker image, so we use the one from adoptopenjdk, i.e., adoptopenjdk/openjdk11:jre-11.0.6_10-alpine

Restrictions to date:
  
* The jar is not built as part of the docker image creation, it needs to be build before the docker image can be created. We then copy the locally created fat jar (from the maven clean install on pom.xml) as the only file added to the openjdk-alpine image.

# Environmental variables

The docker image has the exact same environmental variables as the Java wrapper has (see README.md), except that all variables are capitalised, i.e., *--network* translates to *NETWORK*, which in turn requires the *-e* switch to signal to docker it is an environmental variable.

In addition one more variable is present:

* *VERSION*, with default to the latest released version of this repository, so normally there is no need to use this variable
 

# Cheatsheet of Docker commands to use this repo:

**build the docker image**

From chosen app type we must invoke the right docker file. While normally we would run this from within the directory where the docker file is located, we cannot do this here because we require access to the built jar file. this jar file is located in a parent directory relative to the where the docker files live. In Docker it is NOT allowed to copy files outside of the context, i.e., upstream of the docker file root dir. To avoid this problem, we instead invoke the build from the root of the repo (which does have access downstream to the jar file via /target/*) and then specify the dockerfile explicitly as being the one required to generate the image for the type we are interested in via the *-f* switch

For example build **--type default** image via

```
docker build -t matsim-defaultconfig-wrapper:latest -f ./docker/default_config/Dockerfile .
```

and the **--type config** image via

```
docker build -t matsim-config-wrapper:latest -f ./docker/config/Dockerfile .
```

and the **--type simulation** image via 

```
docker build -t matsim-simulation-wrapper:latest -f ./docker/simulation/Dockerfile . 
```

**inspect file structure of built image**

To inspect the file structure of an image (not container), export it to a tar file and then inspect the tar. An image cannot be inspected without running it otherwise.

```
docker image save matsim-<type>-wrapper:latest > ./image.tar
```

**running the created image **

Below an example of running the image with none/some of the command line options set.

To create the used default config template used by the wrapper in XML form for manual adjustements (with default output)

```
docker run matsim-defaultconfig-wrapper:latest
```

# Resources

A comprehensive yet easy to comprehend tutorial regarding creating docker images and containers can be found on

* https://www.youtube.com/watch?v=3c-iBn73dDE and the blogpost on 
* https://codefresh.io/docker-tutorial/java_docker_pipeline/

Something specific to the openjdk 11 and alpine image can be found on this post (mainly regarding the setup of the virtual file system)

* https://stackoverflow.com/questions/53669151/java-11-application-as-lightweight-docker-image