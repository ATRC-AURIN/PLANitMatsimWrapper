package org.goplanit.aurin.matsim;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.logging.Logger;

import org.matsim.api.core.v01.Scenario;
import org.matsim.contrib.matrixbasedptrouter.MatrixBasedPtModule;
import org.matsim.contrib.matrixbasedptrouter.MatrixBasedPtRouterConfigGroup;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigWriter;
import org.matsim.core.controler.Controler;
import org.matsim.core.scenario.ScenarioUtils;
import org.goplanit.logging.Logging;
import org.goplanit.utils.args.ArgumentParser;
import org.goplanit.utils.args.ArgumentStyle;
import org.goplanit.utils.exceptions.PlanItException;

/**
 * Access point for running a PLANit MATsim simulation or generating config files to do so at a later stage via this same wrapper.
 * <p>
 * Command line options are available which should be provided such that the key is preceded with a double hyphen and the value follows directly (if any) with any number of 
 * spaces in between (no hyphens), e.g., {@code --<key> <value>}.
 * 
 * This access point provides three types of functionality: (i) <i> Run a simple MATSim simulation using basic command line options</i>, (ii) <i> Generate a MATSim configuration file
 * using basic command line options</i>, (iii) <i> Run a MATSim simulation using custom MATSim configuration file </i>, (iv) <i> generate standard 
 * default MATSim configuration file to adjust afterwards for future MATsim simulation run using (iii). these various types of functionality are triggered by
 * setting the {@code --type} parameter </i>.
 *  
 * <ul>
 * <li>--type   indicates the type of functionality, options: {@code simulation, config, default_config}</li>
 * </ul>
 * 
 * When choosing {@code default_config} all other configuration settings are ignored except for the --output option on where to store the result, 
 * when choosing {@code config} the configurable options are included in the config file that is generated as well as the defaults that otherwise would be
 * applied by this wrapper's simulation runs (currently car only) , otherwise it is treated the same as {@code default_config}. In other words when using default_config
 * it provides the user with a template with all default options explicitly listed, whereas config provides the tailored configuration file used by this wrapper including 
 * modifications made by (under simulation listed) command line options provided.
 * <p>
 * When choosing {--type @code simulation}, one can either utilise MATsim config files to configure the simulation or the exposed
 * command line configuration options. When configuring the simulation here the default simulator of MATSim is used (qsim). 
 * The following command line options are available when configuring a simulation via the command line:
 * 
 * <ul>
 * <li>--modes              Options [car_sim, car_sim_pt_teleport, car_pt_sim]. Default car_sim. Defines the type of simulation to configure for and/or run</li>
 * <li>--crs                Format: "epsg:xyz". Default: WGS84 (EPSG:4326). Indicates the coordinate reference system to use in MATSim internally, e.g. EPSG:1234.</li>
 * <li>--network            Format: <i>path</i> to the network file. Default: cwd under "./network.xml"</li>
 * <li>--network_crs        Format: "epsg:xyz". Default: unchanged. Coordinate reference system of the network file, converted to --crs in simulation if different</li>
 * <li>--network_clean      Options: [yes, no]. Default: no. When yes, apply a network clean operation on memory model of network before simulating, persists result under original network input location when possible. Can be used to remove unreachable links if needed</li> 
 * <li>--plans              Format: <i>path</i> to the activities file. Default: the cwd under "./plans.xml"</li>
 * <li>--plans_crs          Format: "epsg:xyz. Default: unchanged. Coordinate reference system of the plans file, converted to --crs in simulation if different</li>
 * <li>--plans_sample       Format: between 0 and 1. Default: 1. Sample of the population plans applied in simulation. When in config mode, downsampled plan is persisted as well</li>
 * <li>--activity_config    Format: <i>path</i> to activity config file. Defining activity types portion in MATSim config file format (plancalcscore section only) compatible with the plans file</li>
 * <li>--starttime          Format: "hh:mm:ss". Default:00:00:00. Start time of the simulation in, ignore activities in the plans file before this time.</li>
 * <li>--endtime            Format: "hh:mm:ss". Default:00:00:00. End time of the simulation in "hh:mm:ss" format, ignore activities in the plans file after this time.</li>
 * <li>--flowcap_factor     Format: between 0 and 1. Default 1. Scale link flow capacity. Use icw down sampling of population plans to remain consistent</li>
 * <li>--storagecap_factor  Format: between 0 and 1. Default 1. Scale link storage capacity. Use icw down sampling of population plans to remain consistent</li>
 * <li>--link_stats         Format: <i>int1,int2</i>". Default: from config file. Set linkStats configuration, <i>int1</i> is the iteration interval to average over, <i>int2</i> is iteration persistence interval, int1 is smaller or equal than int2, when int2 is 0, no persistence </li>
 * <li>--iterations_max     Format: positive number. Default: none. Maximum number of iterations the simulation will run before terminating. Mandatory</li>
 * <li>--output             Format: <i>directory</i>.  Default: "./output". Location to store the generated simulation results or configuration file(s).</li>
 * </ul> 
 * 
 * The {@code --modes} option defines what modes are simulated (car only, or car and pt) and how they are simulated. Currently only cars can be simulated, i.e., 
 * we only support {@code --modes car_sim}. The public transport support (both teleported and simulated is to be added at a later stage). If absent it defaults to
 * {@code --modes car_sim}
 * <p>
 * The {@code --startttime} and {@code --endtime} option can be omitted in which case the entire day, i.e., all activities, will be simulated.
 * <p>
 * 
 * In addition to these general options that can always be used when the type is set to simulation; there are a number of conditional options available too. These are listed below
 * <ul>
 * <li>--pt-stops-csv       Condition: --modes car_sim_pt_teleport. Format: <i>path</i> to the ptStops CSV file.  Default: none. Location to obtain stop locations from in csv format for PtMatrixBasedRouter</li>
 * </ul> 
 * 
 * The {@code --pt-stops-csv} does two things. First it attempts to parse the provided file. Second it implicitly assumes the user would like to use the stop information to construct the pt teleportation travel times rather than
 * the default as-the-crow-flies origin-destination travel times for pt that would otherwise be used in absence of any stop information. Since using a stop-to-stop travel time matrix is generally always an improvement it overrides the default behaviour and activated the MATSim
 * PtMatrixBasedRouter, see also {@link https://github.com/matsim-org/matsim-libs/tree/master/contribs/matrixbasedptrouter/src/main/java/org/matsim/contrib/matrixbasedptrouter}
 * <p>
 * In case the user decides not to use these shortcuts but instead prefers its own configuration file(s) that is also possible, in which case the following two commands should be used:
 *  <ul>
 *  <li>--config          Format: <i>path</i> to config file to use. Default: none. This configuration should ideally be complete unless the--override_config is also used.</li>
 *  <li>--override_config Format: <i>path</i> to additional config file. Default: none. Options in this configuration override or supplement the ones in the --config one. Optional</li>
 * </ul> 
 * 
 * We note that when the above options are used all other command line options for simulation are ignored since they custom configuration file takes precedence.
 * 
 * @author markr
 *
 */
public class PlanitAurinMatsimMain {
  
  /** logger to use */
  private static Logger LOGGER = null;

  /**
   * Create a key value map based on provided arguments. If a key does not require a value, then it receives an
   * empty string.
   * 
   * @param args to parse
   * @return arguments as key value pairs
   * @throws PlanItException thrown if error
   */
  private static Map<String, String> getKeyValueMap(String[] args) throws PlanItException {

    Map<String, String> keyValueMap = ArgumentParser.convertArgsToMap(args, ArgumentStyle.DOUBLEHYPHEN);
    Map<String, String> lowerCaseKeyValueMap = new HashMap<String, String>(keyValueMap.size());
    for (Entry<String, String> entry : keyValueMap.entrySet()) {
      lowerCaseKeyValueMap.put(entry.getKey().toLowerCase(), entry.getValue());
    }
    return lowerCaseKeyValueMap;

  }
  
  /** Add modules programmatically in case configuration requires it 
   * 
   * @param controller to override modules on (if any)
   * @param config to extract information from
   */
  private static void configureOverridingModules(final Controler controller, final Config config) {
    
    /* Matrix based pt router requires overriding routing module (if it is configured) */
    if(config.getModules().containsKey(MatrixBasedPtRouterConfigGroup.GROUP_NAME)) {
      controller.addOverridingModule(new MatrixBasedPtModule());
    }
  }

  /** Conduct a MATSim simulation based on the provided command line configuration information. 
   * 
   * @param keyValueMap to use
   * @param outputDir to use, use default if null
   */  
  private static void runSimulation(final Map<String, String> keyValueMap, Path outputDir) {
    if(outputDir == null) {
      outputDir = MatsimHelper.DEFAULT_OUTPUT_PATH;
    }
    
    /* simulation is using MATSim config files to configure everything or use command line arguments instead */
    Optional<Config> config = null;      
    if(MatsimHelper.isSimulationConfigurationFileBased(keyValueMap)) {
      LOGGER.info(String.format("Running MATSim simulation using command line configuration file"));
      config = MatsimHelper.createConfigurationFromFiles(
          MatsimHelper.getConfigFileLocation(keyValueMap), MatsimHelper.getOverrideConfigFileLocation(keyValueMap));
    }else {
        LOGGER.info(String.format("Running MATSim simulation using command line configuration options"));
        config = MatsimHelper.createConfigurationFromCommandLine(keyValueMap);
    }  
    config.ifPresentOrElse((theConfig) -> runSimulation(theConfig, keyValueMap), () -> LOGGER.severe("Unable to run MATSim simulation, configuration not available"));    
  }

  /** Conduct a MATSim simulation based on the provided configuration.
   * 
   * @param config to use
   * @param keyValueMap to use
   */
  private static void runSimulation(Config config, final Map<String, String> keyValueMap) {    
    Scenario scenario = ScenarioUtils.loadScenario(config);
    
    /* clean network on the fly if required */
    if(MatsimHelper.isNetworkCleanActivated(keyValueMap)) {
      MatsimHelper.cleanAndPersistMatsimNetwork(keyValueMap, scenario.getNetwork());
    }
            
    /* controller */
    Controler controller = new Controler(scenario);
    
    /* special module configuration */
    configureOverridingModules(controller, config);
    
    /* simulation */
    controller.run();    
  }

  /** Generate a MATSim configuration file and persist in output directory. Useful to allow users to get started and allow them to edit it offline and then provide it as input
   * again to this wrapper for an actual simulation run
   * 
   * @param keyValueMap to use
   * @param outputDir to use, use default if null
   * @return location where config file has been created
   * @throws PlanItException thrown if unsuccessful
   */
  private static String generateMatsimConfiguration(final Map<String, String> keyValueMap, Path outputDir) throws PlanItException {
    if(outputDir == null) {
      outputDir = MatsimHelper.DEFAULT_OUTPUT_PATH;
    }
    
    /* DEFAULT MATSIM FULL CONFIG */
    String outputFileLocation = Path.of(outputDir.toString(), MatsimHelper.DEFAULT_MATSIM_CONFIG_FILE).normalize().toAbsolutePath().toString();
    if( MatsimHelper.TYPE_DEFAULT_CONFIG_VALUE.equals(keyValueMap.get(MatsimHelper.TYPE_KEY))){
      org.matsim.run.CreateFullConfig.main(new String[] {outputFileLocation});
    }
    /* CUSTOMISED MATSIM CONFIG */
    else if(MatsimHelper.TYPE_CONFIG_VALUE.equals(keyValueMap.get(MatsimHelper.TYPE_KEY))) {
      
      Config config = MatsimHelper.createConfigurationFromCommandLine(keyValueMap).orElseThrow(() -> new PlanItException("Unable to generate MATSim configuration"));                      
      new ConfigWriter(config).write(outputFileLocation);
    }
    return outputFileLocation;
  }

  /** Path from which application was invoked */
  public static final Path CURRENT_PATH = Path.of("");    

  /** Help key */
  public static final String ARGUMENT_HELP = "help";  

  /**
   * Access point
   * 
   * @param args arguments provided
   */
  public static void main(String[] args) {
    try {
      LOGGER = Logging.createLogger(PlanitAurinMatsimMain.class);
      if(LOGGER==null) {
        throw new PlanItException("Unable to instantiate logger using default PLANit logging.properties");
      }
      
      Map<String, String> keyValueMap = getKeyValueMap(args);
      if (keyValueMap.containsKey(ARGUMENT_HELP)) {

        // TODO
        LOGGER.info("--help is not yet implemented, see Javadoc instead for available arguments");

      } else {

        if(!keyValueMap.containsKey(MatsimHelper.TYPE_KEY)) {
          LOGGER.warning("--type argument missing, unable to proceed with MATSim simulation wrapper");
          return;
        }
        
        /* DOWN SAMPLING OF PLANS/POPULATION */
        Path outputDir = MatsimHelper.parseOutputDirectory(keyValueMap);
        if(MatsimHelper.isPopulationPlansDownSampled(keyValueMap)) {
          /* down sampling cannot be done in memory. Requires creating new plans file
           * So create new down sampled plans file and overwrite original plans file location so it is used
           * for simulation (if that is the type) */
          Path downSampledPopulationPath = MatsimHelper.createDownSampledPopulation(keyValueMap, outputDir);
          if(downSampledPopulationPath != null) {
            keyValueMap.put(MatsimHelper.PLANS_KEY, downSampledPopulationPath.toString());
          }
        }
        
        /* TYPE: CONFIGURATION ONLY */ 
        if(MatsimHelper.isConfigurationType(keyValueMap)) {
          
          final String outputFileLocation = generateMatsimConfiguration(keyValueMap, outputDir);
          LOGGER.info(String.format("Generated MATSim configuration file: %s",outputFileLocation));
          if(MatsimHelper.isPopulationPlansDownSampled(keyValueMap)) {
            LOGGER.info(String.format("Generated downsampled MATSim plans file: %s",keyValueMap.get(MatsimHelper.PLANS_KEY))); 
          }
          
        }
        /* TYPE: SIMULATION ONLY */
        else if(MatsimHelper.isSimulationType(keyValueMap)) {
          
          LOGGER.info(String.format("Running MATSim simulation"));
          runSimulation(keyValueMap, outputDir);
          if(MatsimHelper.isPopulationPlansDownSampled(keyValueMap)) {
            /* any temporary downsampled path should be deleted upon termination of the simulation */
            Files.delete(Path.of(keyValueMap.get(MatsimHelper.PLANS_KEY)));
          }          
          
        }else {
          LOGGER.warning("--type value %s unknown, unable to proceed");
          return;          
        }

      }
    } catch (Exception e) {
      if(LOGGER !=null) {
        LOGGER.severe(e.getMessage());
        LOGGER.severe("Unable to execute MATSim simulation from PLANit AURIN wrapper, terminating");
      }else {
        e.printStackTrace();
      }
    }

  }

}
