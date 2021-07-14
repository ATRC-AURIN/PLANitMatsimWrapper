package org.planit.aurin.matsim;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Logger;

import org.matsim.api.core.v01.Scenario;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigWriter;
import org.matsim.core.controler.Controler;
import org.matsim.core.scenario.ScenarioUtils;
import org.planit.logging.Logging;
import org.planit.utils.args.ArgumentParser;
import org.planit.utils.args.ArgumentStyle;
import org.planit.utils.exceptions.PlanItException;

/**
 * Access point for running a PLANit MATsim simulation that based on its inputs conducts a simulation run.
 * <p>
 * Command line options are available which should be provided such that the key is preceded with a double hyphen and the value follows directly (if any) with any number of 
 * spaces in between (no hyphens), e.g., {@code --<key> <value>}.
 * 
 * This access point provides three types of functionality: (i) <i> Run a simple MATSim simulation using basic command line options</i>, (ii) <i> Generate a MATSim configuration file
 * using basic command line options</i>, (iii) <i> Run a MATSim simulation using custom MATSim configuration file </i>, (iv) <i> generate standard 
 * default MATSim configuration file to adjust afterwards for future MATsim simulation run using (iii). these various types of functionality are triggered by
 * setting the {@ --type} parameter.
 *  
 * <ul>
 * <li>--type   indicates the type of functionality, options: {@code simulation, config, default_config}</li>
 * </ul>
 * 
 * When choosing {@code default_config} all other configuration settings are ignored except for the --output option on where to store the result, 
 * when choosing {@code config} the configurable options are included in the config file that is generated as well as the defaults that otherwise would be
 * applied by this wrapper's simulation runs (currently car only) , otherwise it is treated the same as {@code default_config}. 
 * <p>
 * When choosing {--type @code simulation}, one can either utilise MATsim config files to configure the simulation or the exposed
 * command line configuration options. When configuring the simulation here the default simulator of MATSim is used (qsim). 
 * The following command line options are available when configuring a simulation via the command line:
 * 
 * <ul>
 * <li>--modes            indicates the mode support, options {@code car_sim (default), car_sim_pt_teleport, car_pt_sim}</li>
 * <li>--crs              indicates the coordinate reference system to use in MATSim internally, e.g. EPSG:1234</li>
 * <li>--network          path to the network file, when absent network is assumed in the cwd under "./network.xml"</li>
 * <li>--network_crs      coordinate reference system of the network file, e.g. EPSG:1234, when absent it is used as is in MATSim</li>
 * <li>--plans            path to the activities file, when absent plans are assumed in the cwd under "./plans.xml"</li>
 * <li>--plans_crs        coordinate reference system of the plans file, e.g. EPSG:1234, when absent it is used as is in MATSim</li>
 * <li>--activity_config  path to activity config file defining activity types portion in MATSim config file format (plancalcscore section only) compatible with the plans file</li>
 * <li>--starttime        start time of the simulation in "hh:mm:ss" format, ignore activities in the plans file before this time</li>
 * <li>--endtime          end time of the simulation in "hh:mm:ss" format, ignore activities in the plans file after this time</li>
 * <li>--iterations_max maximum number of iterations the simulation will run before terminating </li>
 * </ul> 
 * <p>
 * The {@code --modes} option defines what modes are simulated (car only, or car and pt) and how they are simulated. Currently only cars can be simulated, i.e., 
 * we only support {@code --modes car_sim}. The public transport support (both teleported and simulated is to be added at a later stage). If absent it defaults to
 * {@code --modes car_sim}
 * <p>
 * <p>
 * The {@code --startttime} and {@code --endtime} option can be omitted in which case the entire day, i.e., all activities, will be simulated. 
 * <p>
 * In case the user decides not to use these shortcuts but instead prefers its own configuration file(s) that is also possible, in which case the following two commands should be used:
 *  <ul>
 *  <li>--config TODO</li>
 *  <li>--override_config TODO (can be multiple in order)</li>
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
  
  /** Conduct a MATSim simulation based on the provided command line configuration information. 
   * 
   * @param keyValueMap to use
   * @param outputDir to use, use default if null
   */  
  private static void runSimulation(final Map<String, String> keyValueMap, Path outputDir) {
    if(outputDir == null) {
      outputDir = PlanitAurinMatsimHelper.DEFAULT_OUTPUT_PATH;
    }
    
    /* simulation is using MATSim config files to configure everything or use command line arguments instead */
    Config config = null;
    if(PlanitAurinMatsimHelper.isSimulationConfigurationFileBased(keyValueMap)) {
      config = PlanitAurinMatsimHelper.createConfigurationFromFiles(
          PlanitAurinMatsimHelper.getConfigFileLocation(keyValueMap), PlanitAurinMatsimHelper.getOverrideConfigFileLocation(keyValueMap));
    }else {
      config = PlanitAurinMatsimHelper.createConfigurationFromCommandLine(keyValueMap);
    }
    
    runSimulation(config);    
  }

  /** Conduct a MATSim simulation based on the provided configuration.
   * 
   * @param config to use
   */
  private static void runSimulation(Config config) {
    Scenario scenario = ScenarioUtils.loadScenario(config);   
    Controler controller = new Controler(scenario);
    controller.run();    
  }

  /** Generate a MATSim configuration file and persist in output directory. Useful to allow users to get started and allow them to edit it offline and then provide it as input
   * again to this wrapper for an actual simulation run
   * 
   * @param keyValueMap to use
   * @param outputDir to use, use default if null
   */
  private static void generateMatsimConfiguration(final Map<String, String> keyValueMap, Path outputDir) {
    if(outputDir == null) {
      outputDir = PlanitAurinMatsimHelper.DEFAULT_OUTPUT_PATH;
    }
    
    /* DEFAULT MATSIM FULL CONFIG */
    String absOutputDir = outputDir.toAbsolutePath().toString();
    if( PlanitAurinMatsimHelper.TYPE_DEFAULT_CONFIG_VALUE.equals(keyValueMap.get(PlanitAurinMatsimHelper.TYPE_KEY))){
      org.matsim.run.CreateFullConfig.main(new String[] {absOutputDir});
    }
    /* CUSTOMISED MATSIM CONFIG */
    else if(PlanitAurinMatsimHelper.TYPE_CONFIG_VALUE.equals(keyValueMap.get(PlanitAurinMatsimHelper.TYPE_KEY))) {
      
      Config config = PlanitAurinMatsimHelper.createConfigurationFromCommandLine(keyValueMap);                      
      new ConfigWriter(config).write(absOutputDir);
    }
    
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
      
      Map<String, String> keyValueMap = getKeyValueMap(args);
      if (keyValueMap.containsKey(ARGUMENT_HELP)) {

        // TODO
        LOGGER.info("--help is not yet implemented, see Javadoc instead for available arguments");

      } else {

        if(!keyValueMap.containsKey(PlanitAurinMatsimHelper.TYPE_KEY)) {
          LOGGER.warning("--type missing, unable to proceed");
          return;
        }
        
        Path outputDir = PlanitAurinMatsimHelper.parseOutputDirectory(keyValueMap);        
        if(PlanitAurinMatsimHelper.isConfigurationType(keyValueMap)) {
          
          generateMatsimConfiguration(keyValueMap, outputDir);
          
        }else if(PlanitAurinMatsimHelper.isSimulationType(keyValueMap)) {
          
          runSimulation(keyValueMap, outputDir);
          
        }else {
          LOGGER.warning("--type value %s unknown, unable to proceed");
          return;          
        }

      }
    } catch (Exception e) {
      LOGGER.severe(e.getMessage());
      LOGGER.severe("Unable to execute MATSim simulation from PLANit AURIN wrapper, terminating");
    }

  }

}
