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
 
# Copied resources

The docker image for docker/simulation also  by default copies in the Melbourne test resources used in the unit tests. The files are mounted in 

`/app/test/resources/Melbourne` 

these resources can be used to run some simple tests on the created docker image without the need to specify any volumes or mounts. 

# Build a docker image

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

# Inspect file structure of built image

To inspect the file structure of an image (not container), export it to a tar file and then inspect the tar. An image cannot be inspected without running it otherwise.

```
docker image save matsim-<type>-wrapper:latest > ./image.tar
```

# Running image without volumes

## Default-config

Below an example of running the image with no command line options set.

To create the used default config template used by the wrapper in XML form for manual adjustments (with default output). This creates the default configuration file, but since this is a container, the created file is lost when the container is removed.

```
docker rm  matsim-defaultconfig-wrapper
docker run matsim-defaultconfig-wrapper:latest
```

> See volues examples on how to extract created files from a run

## Matsim-simulation

Below an example of how to run a MATSim simulation in Docker, analogous to the car based Melbourne oriented unit test. Note that here we use the directly copied resources to `/app/test/resources` that are by default available in the image. For true external files one needs to use volumes instead. Also results are lost when the container dies in this example.

```
docker rm matsim-simulation-wrapper
docker run --name matsim-simulation-wrapper -e MODES=car_sim -e CRS=epsg:3112 -e NETWORK="/app/test/resources/Melbourne/car_simple_melbourne_network_cleaned.xml" -e NETWORK_CRS=epsg:3112 -e PLANS="/app/test/resources/Melbourne/plans_victoria_car.xml" -e PLANS_CRS=epsg:3112 -e ACTIVITY_CONFIG="/app/test/resources/Melbourne/activity_config.xml" -e LINK_STATS=1,1 -e ITERATIONS_MAX=2 matsim-simulation-wrapper:latest
```

> See volumes examples on how to extract created files from a run

# Running image with volumes

When using volumes we conform to the following convention and assumptions:

* Your environment has two fixed persistent directories: VM_INPUT and VM_OUTPUT where  the container reads and outputs data. Often Docker would be run from a virtual machine (VM) hence this naming convention. 
* It is expected that these two directories are available in the working directory from which the run command is invoked. If not this script needs to be altered to reflect these changes
* Before Docker Container runs, the input files should be present in the VM_INPUT directory.
* The docker container runs and creates the output files in VM_OUTPUT directory

Below example scripts of how to run while using volumes

## Default-config

Example where result is written to `/output` which in turn is mapped by the volume

```
docker rm  matsim-defaultconfig-wrapper
docker run --name matsim-defaultconfig-wrapper  -e OUTPUT=/output -v ${PWD}/VM_OUTPUT:/output/:rw  matsim-defaultconfig-wrapper:latest
```

## Matsim-simulation created image

Example where result is written to `/output` which in turn is mapped by the volume. Input is passed on via volume `/input` as well. Here the assumption is that all input files of the Melbourne test resources have been copied prior to running the image to the `./VM_INPUT/` dir.

```
docker rm matsim-simulation-wrapper
docker run --name matsim-simulation-wrapper -e MODES=car_sim -e CRS=epsg:3112 -e NETWORK="/input/car_simple_melbourne_network_cleaned.xml" -e NETWORK_CRS=epsg:3112 -e PLANS="/input/plans_victoria_car.xml" -e PLANS_CRS=epsg:3112 -e ACTIVITY_CONFIG="/input/activity_config.xml" -e LINK_STATS=1,1 -e ITERATIONS_MAX=2 -e OUTPUT=/output -v ${PWD}/VM_INPUT:/input/:rw  -v ${PWD}/VM_OUTPUT:/output/:rw --rm matsim-simulation-wrapper:latest
```

> It is important to note that in case the inputs are to be chosen by an end-user via some interface that hides the actual Docker calls, the process that converts these inputs to the below should ensure that these files are copied to the right locations and that the environment variables passed in to the run command that refer to files, e.g. INPUT=, OUTPUT=, etc., are constructed on the fly by combining the volume directories and file names where needed.

# Resources

A comprehensive yet easy to comprehend tutorial regarding creating docker images and containers can be found on

* https://www.youtube.com/watch?v=3c-iBn73dDE and the blogpost on 
* https://codefresh.io/docker-tutorial/java_docker_pipeline/

Something specific to the openjdk 11 and alpine image can be found on this post (mainly regarding the setup of the virtual file system)

* https://stackoverflow.com/questions/53669151/java-11-application-as-lightweight-docker-image