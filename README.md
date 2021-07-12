# PLANitOsmMatsimWrapper

Wrapper for Aurin services to access exposed functionality of PLANit MATsim simulations. 

The purpose of this wrapper is twofold:

* Allow novice users to run a MATSim simulation with minimal need for configuration (network, activities, default settings)
* Allow more experienced users to provide custom configuration settings to run more complex MATSim simulations in the cloud

To service both needs this wrapper exposes two main features:

* Possibility to generate a MATSim configuration file
** based on the wrapper's predefined settings
** based on MATSim default settings (full config generator)
* Possibility to run a MATSim simulation 
** based on wrapper's predefined settings without a MATSim config file
** based on user provided MATSim config file

This approach allows a user to not bother with dealing with any MATSim config file and instead rely on the out-of-the-box predefined settings of the wrapper, or alternatively, let the wrapper generate a config file based on its predefined settings, adjust this file to suit the user's needs, and then conduct a simulation, or provide a completely custom configuration. The latter requires the most in-depth knowledge of MATSim whereas the first option requires little to no knowledge of MATSim.

## Maven parent

Projects need to be built from Maven before they can be run. The common maven configuration can be found in the PLANitParentPom project which acts as the parent for this project's pom.xml.

> Make sure you install the PLANitParentPom pom.xml before conducting a maven build (in Eclipse) on this project, otherwise it cannot find the references dependencies, plugins, and other resources.

## FAT Jar

To run the wrapper in a stand-alone fashion (not from IDE) all dependencies need to be made available within the runnable jar. To support this a separate pom.xml is provided in the project that builds such a jar.
