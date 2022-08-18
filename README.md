# PLANitOsmMatsimWrapper

![Master Branch](https://github.com/AURIN-OFFICE/PLANitMatsimWrapper/actions/workflows/maven_master.yml/badge.svg?branch=master)
![Develop Branch](https://github.com/AURIN-OFFICE/PLANitMatsimWrapper/actions/workflows/maven_develop.yml/badge.svg?branch=develop)

Wrapper for Aurin services to access exposed functionality of PLANit MATSim simulations.

The purpose of this wrapper is twofold:

* Allow novice users to run a MATSim simulation with minimal need for configuration (network, activities, default settings)
* Allow more experienced users to provide custom configuration settings to run more complex MATSim simulations in the cloud

To service both needs this wrapper exposes two main features:

* Generate MATSim configuration files
    * based on the wrapper's predefined settings
    * based on MATSim default settings (full config generator)
* Run MATSim simulations 
    * based on wrapper's predefined settings without a MATSim config file
    * based on user provided MATSim config file

This approach allows a user to not bother with dealing with any MATSim config file and instead rely on the out-of-the-box predefined settings of the wrapper, or alternatively, let the wrapper generate a config file based on its predefined settings, adjust this file to suit the user's needs, and then conduct a simulation, or provide a completely custom configuration. The latter requires the most in-depth knowledge of MATSim whereas the first option requires little to no knowledge of MATSim.

> This repository has been implemented by the University of Sydney for the ATRC project. The ATRC is a project lead by the Australian Urban Research Infrastructure Network (AURIN) and is supported by the Australian Research Data Commons (ARDC). AURIN and the ARDC are funded by the National Collaborative Research Infrastructure Strategy (NCRIS).  
ATRC Investment: https://doi.org/10.47486/PL104  
ATRC RAiD: https://hdl.handle.net/102.100.100/102.100.100/399880 

## Getting started

The simplest way to use this wrapper is to simply build this project via its pom.xml. Before performing a maven clean install. This will gather all dependencies and compile wrapper. The pom.xml is configured to generate an executable jar in the target output dir. After successfully building this project the executable jar can be found under path/to/PLANitAurinMatsimWrapper/target/planit-aurin-matsim_version_.jar. It is this jar that can be run from the command line. Below you will find an example on how to conduct a simple conversion based on an OSM URL with a bounding box for a small area in Germany

```
java -jar planit-aurin-matsim-<version>.jar --type simulation --modes car_sim --crs epsg:3112 --network "..\src\test\resources\Melbourne\car_simple_melbourne_network_cleaned.xml" --network_crs epsg:3112 --plans "..\src\test\resources\Melbourne\plans_victoria.xml" --plans_crs epsg:3112 --activity_config "..\src\test\resources\Melbourne\activity_config.xml" --iterations_max 2
```

Below a list of the available command line options that are currently exposed. The PLANit OSM parser has many more options than currently made available. If you wish to use those, then we suggest not using this wrapper but instead directly utilise the PLANit platform instead.

Below you will find a list of the available command line options that are currently exposed. The PLANit MATSim wrapper (and MATSim itself for that matter) has many more options than currently made available through command line options. If you wish to use those, then we suggest generating a (template based) configuration file instead (see other test examples in the repository) and adjust it to your needs. Then, use your own configuration file for running the simulation instead of using the command line switches.

## Command line options

The following command line options are available which should be provided such that the key is preceded with a double hyphen (--) and the value follows directly (if any) with any number of spaces in between (no hyphens), e.g., --<key> <value>:

We distinguish between three key types of functionality that are exposed, namely:
 * (i)    Run a simple MATSim simulation using basic command line options (as shown above) 
 * (ii)    Generate a MATSim configuration file to adjust offline before using it for...
 * (iii)    Run a MATSim simulation using custom MATSim configuration file
 
 Option (ii) can be used to generate a completely vanilla config file based on MATSim's defaults, or generate one based on this wrapper's default template. The following command line switch is mandatory and is reponsible for choosing either to run a simulation ((i) or (iii)) or generate a config file ((ii)). 
   
  * **--type**    *Format: options: [simulation, config, default_config]*. Default: none.
 
When choosing *default_config*, all other command line settings are ignored except for the --output option on where to store the result configuration file, it generates the full default based MATSim configuration file.

When choosing *config*, the configurable command line options set by the user are included in the generated config file as well as the defaults that otherwise would be applied by this wrapper's simulation runs. If no settings are specified, it generates the same setting as *default_config* would. In other words when using *default_config* it provides the user with a template with all default options explicitly listed, whereas *config* provides a more tailored configuration file based on the command line modifications provided.

When choosing --type *simulation*, one can either decide to adopt a pre-generated MATSim config file to configure the simulation, or, alternatively use the exposed command line configuration options directly. In case a config file is used, only the following command line options are important:

 *  **--config**    *Format: <path to config file>*. Default: none. This configuration file should be complete unless the *--override_config* is also used.</li>
 * **--override_config**    *Format: <path to additional config file>.* Default: none. Options in this configuration override or supplement the ones in *--config*. Optional.

 We note that when adopting a config file based simulation all other command line options (see below) are ignored since the custom configuration file takes precedence. In this case.

When configuring the simulation via command line options directly, not using a custom config file, the following options are made available. In this situation, MATSim adopts its default simulator qsim. 
 
 * **--modes**    *Format: options [car_sim, car_sim_pt_teleport, car_pt_sim].* Default car_sim.
 * **--crs**      *Format: *"epsg:<xyz>"*. Default: WGS84 (EPSG:4326). Indicates the coordinate reference system to use in MATSim internally
 * **--network**    *Format: <path to the network file>*. Default: cwd such that *"./network.xml"*
 * **--network_crs**     *Format: "epsg:<xyz>"*. Default: unchanged. Coordinate reference system of the network file, converted to *--crs* in simulation if different
 * **--network_clean**    *Format: options: [yes, no].* Default: no. When yes, apply a network clean operation on memory model of network before simulating, persists result under original network input location when possible. Can be used to remove unreachable links if needed
 * **--plans**    *Format: <path to the activities file>*. Default: the cwd such that *"./plans.xml"*
 * **--plans_crs**    *Format: "epsg:<xyz>*. Default: unchanged. Coordinate reference system of the plans file, converted to *--crs* in simulation if different
 * **--plans_sample**    *Format: between 0 and 1.* Default: 1. Sample of the population plans applied in simulation. When in config mode, downsampled plan is persisted as well
 * **--activity_config**    *Format: <path to activity config file>*. Defining activity types portion in MATSim config file format (plancalcscore section only) compatible with the plans file
 * **--starttime**    *Format: "hh:mm:ss".* Default:00:00:00. Start time of the simulation in, ignore activities in the plans file before this time.
 * **--endtime**    *Format: "hh:mm:ss".* Default:00:00:00. End time of the simulation in "hh:mm:ss" format, ignore activities in the plans file after this time
 * **--flowcap_factor**    *Format:* between 0 and 1. Default 1. Scale link flow capacity. Use icw down sampling of population plans to remain consistent
 * **--storagecap_factor**    *Format: between 0 and 1.* Default 1. Scale link storage capacity. Use icw down sampling of population plans to remain consistent
 * **--iterations_max**    *Format: positive number.* Default: none. Maximum number of iterations the simulation will run before terminating. Mandatory
 * **--link_stats** *Format: average interval integer, interval integer.* Default: 5,10. Defines over how many iterations to average the link statistics (first) and the interval to which these statistics are to be persisted (second). Second value must be larger or equal than the first.
 * **--output** *Format: <path to desired output directory>.*  Default: "<cwd>/output". Location to store the generated simulation results or configuration file(s)

The *--modes* option defines what modes are simulated (car only, or car and pt) and how they are simulated. Currently only cars can be simulated, i.e., we only support *--modes car_sim* for now. The public transport support (both teleported and simulated is to be added at a later stage). If absent it defaults to *--modes car_sim.*

The *--startttime* and *--endtime* option can be omitted in which case the entire day, i.e., all activities, will be simulated.

In addition to these general options that can always be used when the type is set to simulation; there are a number of conditional options available too. These are listed below
 * **--pt_stops_csv**   *Condition: --modes car_sim_pt_teleport. Format: <i>path</i> to the ptStops CSV file*.  Default: none. Location to obtain stop locations from in CSV format for PtMatrixBasedRouter
 
The *--pt_stops_csv* does two things. First it attempts to parse the provided file. Second it implicitly assumes the user would like to use the stop information to construct the pt teleportation travel times rather than the default as-the-crow-flies origin-destination travel times for pt that would otherwise be used in absence of any stop information. Since using a stop-to-stop travel time matrix is generally always an improvement it overrides the default behaviour and activated the MATSim PtMatrixBasedRouter, see also [MatrixBasedPtRouter](https://github.com/matsim-org/matsim-libs/tree/master/contribs/matrixbasedptrouter/src/main/java/org/matsim/contrib/matrixbasedptrouter) 
