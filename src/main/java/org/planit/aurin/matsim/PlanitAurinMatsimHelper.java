package org.planit.aurin.matsim;

import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.logging.Logger;

import org.matsim.api.core.v01.population.Population;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.population.PopulationUtils;
import org.matsim.core.utils.misc.Time;
import org.planit.utils.exceptions.PlanItException;
import org.planit.utils.math.Precision;
import org.planit.utils.misc.StringUtils;
import org.planit.utils.resource.ResourceUtils;

/**
 * Helper methods to configure the AURIN MATSim wrapper based on user arguments provided for this wrapper.
 * 
 * @author markr
 *
 */
public class PlanitAurinMatsimHelper {
  
  /** the logger to use */
  private static final Logger LOGGER = Logger.getLogger(PlanitAurinMatsimHelper.class.getCanonicalName());
  
  /** Path from which application was invoked */
  public static final Path CURRENT_PATH = Path.of("");
  
  /** the resource file reflecting the base car only configuration for a MATSim run */
  public static final String DEFAULT_CAR_CONFIG_RESOURCE = "baseconfig_car.xml";
  
  /** the default name for a MATSim configuration file*/
  public static final String DEFAULT_MATSIM_CONFIG_FILE = "config.xml";  
  
  //----------------------------------------------------
  //-------- OUTPUT_PATH ---------------------------
  //----------------------------------------------------
  
  /** Key reflecting the type of functionality to unlock*/
  public static final String OUTPUT_KEY = "output";
  
  /** Output path defaults to directory where this application was run from */
  public static final Path DEFAULT_OUTPUT_PATH = CURRENT_PATH;  
  
  //----------------------------------------------------
  //-------- TYPE --------------------------------------
  //----------------------------------------------------
  
  /** Key reflecting the type of functionality to unlock*/
  public static final String TYPE_KEY = "type";
    
  /** Value reflecting the need to generate the MATSim full default config file*/
  public static final String TYPE_DEFAULT_CONFIG_VALUE = "default_config";
  
  /** Value reflecting the need to generate an amended config file based on user command line settings and default config file*/
  public static final String TYPE_CONFIG_VALUE = "config";  
  
  /** Value reflecting the need to conduct a MATSim simulation run*/
  public static final String TYPE_SIMULATION_VALUE = "simulation";    
  
  //----------------------------------------------------
  //-------- MODES -------------------------------------
  //----------------------------------------------------  
  
  /** the string representation used in MATSim for the mode car */
  protected static String MATSIM_CAR_MODE = "car";
  
  /** Key reflecting the modes to use in simulation */
  public static final String MODES_KEY = "modes";
  
  /** Value reflecting the car mode as only mode, which is to be simulated */
  public static final String MODES_CAR_SIM_VALUE = "car_sim";
  
  /** Value reflecting the car and pt modes as modes, where cars are simulated and PT is teleported 
   * TODO: NOT YET SUPPORTED */
  public static final String MODES_CAR_SIM_PT_TELEPORT_VALUE = "car_sim_pt_teleport";  
  
  /** Value reflecting the car and pt modes as modes, which are to be simulated 
   * TODO: NOT YET SUPPORTED */
  public static final String MODES_CAR_PT_SIM_VALUE = "car_pt_sim";
  
  //----------------------------------------------------
  //-------- CRS -------------------------------------
  //----------------------------------------------------  
  
  /** the string representation used in MATSim for the default CRS Atlantis */
  protected static String MATSIM_DEFAULT_GLOBAL_CRS = "Atlantis";
  
  /** Key reflecting the CRS to use in simulation */
  public static final String CRS_KEY = "crs";  
  
  //----------------------------------------------------
  //-------- NETWORK------------------------------------
  //----------------------------------------------------  
  
  /** the default network file name in MATSim*/
  protected static String MATSIM_DEFAULT_NETWORK = "network.xml";
  
  /** Key reflecting the network file location */
  public static final String NETWORK_KEY = "network";
  
  /** Key reflecting the network crs */
  public static final String NETWORK_CRS_KEY = "network_crs";    
  
  //----------------------------------------------------
  //-------- PLANS --------------------------------------
  //----------------------------------------------------  
  
  /** the default network file name in MATSim*/
  protected static String MATSIM_DEFAULT_PLANS = "plans.xml";
  
  /** Key reflecting the plan file location */
  public static final String PLANS_KEY = "plans";
  
  /** Key reflecting the network crs */
  public static final String PLANS_CRS_KEY = "plans_crs";
  
  /** Key reflecting the plan sample population percentage to use */
  public static final String PLANS_SAMPLE_KEY = "plans_sample";         
  
  //----------------------------------------------------
  //-------- STARTTIME/ENDTIME -------------------------
  //----------------------------------------------------  
  
  /** the string representation used in MATSim for the default qsim start time*/
  protected static String MATSIM_DEFAULT_STARTTIME = "00:00:00";
  
  /** the string representation used in MATSim for the default qsim end time*/
  protected static String MATSIM_DEFAULT_ENDTIME = MATSIM_DEFAULT_STARTTIME;  
  
  /** Key reflecting the start time to use in simulation */
  public static final String STARTTIME_KEY = "starttime";    
    
  /** Key reflecting the end time to use in simulation */
  public static final String ENDTIME_KEY = "endtime";     
  
  //----------------------------------------------------
  //-------- ITERATIONS -------------------------------------
  //----------------------------------------------------  
  
  /** the default maximum number of iterations run when not set by user */
  protected static Integer DEFAULT_ITERATIONS_MAX = 10;
  
  /** Key reflecting the maximum number of iterations to run in simulation */
  public static final String ITERATIONS_MAX_KEY = "iterations_max";  
  
  //----------------------------------------------------
  //-------- ACTIVITY CONFIG ---------------------------
  //----------------------------------------------------  
    
  /** Key reflecting the plan file location */
  public static final String ACTIVITY_CONFIG_KEY = "activity_config";  
  
  //----------------------------------------------------
  //-------- CONFIG ------------------------------------
  //----------------------------------------------------  
    
  /** Key reflecting the MATSim config file location in case simulation is run based on a file */
  public static final String CONFIG_KEY = "config";
  
  /** Key reflecting the MATSim override config file locations in case simulation is run based on config file(s) */
  public static final String OVERRIDE_CONFIG_KEY = "override_config";   
  
  //----------------------------------------------------
  //-------- CAPACITY ---------------------------
  //----------------------------------------------------  
    
  /** Key reflecting the factor to apply to all link flow capacities in simulation */
  public static final String CAPACITY_FLOW_FACTOR_KEY = "flowcap_factor";      
  
  /** Key reflecting the factor to apply to all link storage capacities in simulation */
  public static final String CAPACITY_STORAGE_FACTOR_KEY = "storagecap_factor";       
    
  /** Collect the plans file location from command line arguments. If not set we use the current working directory and default plans name MATSIM_DEFAULT_PLANS.
   * 
   * @param keyValueMap to extract location from
   * @return found location
   */
  private static Path extractPlansFileLocation(Map<String, String> keyValueMap) {
    String planFileLocation = keyValueMap.get(PLANS_KEY);     
    if(StringUtils.isNullOrBlank(planFileLocation)) {
      planFileLocation = Paths.get(CURRENT_PATH.toString(), MATSIM_DEFAULT_PLANS).toAbsolutePath().toString();
    }      
    return Paths.get(planFileLocation);
  }

  /** Configure the available modes in the simulation based on command line arguments provided
   * 
   * @param config to alter
   * @param keyValueMap to extract configuration choice from
   */
  private static void configureModes(final Config config, final Map<String, String> keyValueMap) {
    String modesValue = keyValueMap.get(MODES_KEY);
    switch (modesValue) {
      case MODES_CAR_SIM_VALUE:
        config.changeMode().setModes(new String[] {MATSIM_CAR_MODE});
        LOGGER.info("[SETTING] simulation mode: car");
        break;
      case MODES_CAR_SIM_PT_TELEPORT_VALUE:
        LOGGER.warning(String.format("value %s for --modes not yet supported, ignored", modesValue));
        break;        
      case MODES_CAR_PT_SIM_VALUE:
        LOGGER.warning(String.format("value %s for --modes not yet supported, ignored", modesValue));
        break;                
      default:
        LOGGER.warning(String.format("Unknown value %s for --modes argument, ignored", modesValue));
    }        
  }

  /** Configure the CRS of the simulation. If not set we use the default MATSIM_DEFAULT_GLOBAL_CRS
   * 
   * @param config to configure
   * @param keyValueMap to extract location from
   */  
  private static void configureCrs(Config config, Map<String, String> keyValueMap) {
    String crsValue = keyValueMap.get(CRS_KEY);
    if(StringUtils.isNullOrBlank(crsValue)) {
      crsValue = MATSIM_DEFAULT_GLOBAL_CRS;
    }
    
    LOGGER.info(String.format("[SETTING] MATSim core CRS: %s", crsValue));
    config.global().setCoordinateSystem(crsValue);
  }

  /** Configure the location of the network. If not set we use the current working directory and default network name MATSIM_DEFAULT_NETWORK.
   * When invalid path is provided we log a warning and ignore.
   * 
   * @param config to configure
   * @param keyValueMap to extract location from
   */
  private static void configureNetwork(final Config config, final Map<String, String> keyValueMap) {
    
    String networkFileLocation = keyValueMap.get(NETWORK_KEY);     
    try {      
      
      Path networkFileLocationAsPath = null;      
      if(StringUtils.isNullOrBlank(networkFileLocation)) {
        networkFileLocation = Paths.get(CURRENT_PATH.toString(), MATSIM_DEFAULT_NETWORK).toAbsolutePath().toString();
      }      
      networkFileLocationAsPath = Paths.get(networkFileLocation);
      
      /* set network path location */
      LOGGER.info(String.format("[SETTING] MATSim network file: %s", networkFileLocationAsPath.toString()));
      config.network().setInputFile(networkFileLocationAsPath.toAbsolutePath().toString());
      
    }catch (Exception e) {
      LOGGER.warning(String.format("Invalid network file location %s for --network, ignored", networkFileLocation));
    }   
  }

  /** Configure the CRS of the network. If not set we use the geo data as is without any conversion. Otherwise it is converted to the MATSim 
   * simulation global CRS.
   * 
   * @param config to configure
   * @param keyValueMap to extract location from
   */  
  private static void configureNetworkCrs(Config config, Map<String, String> keyValueMap) {
    String crsValue = keyValueMap.get(NETWORK_CRS_KEY);
    if(StringUtils.isNullOrBlank(crsValue)) {
      crsValue = MATSIM_DEFAULT_GLOBAL_CRS;
    }
    
    LOGGER.info(String.format("[SETTING] MATSim network input CRS: %s", crsValue));
    config.network().setInputCRS(crsValue);
  }

  /** Configure the location of the activities. If not set we use the current working directory and default plans name MATSIM_DEFAULT_PLANS.
   * When invalid path is provided we log a warning and ignore.
   * 
   * @param config to configure
   * @param keyValueMap to extract location from
   */
  private static void configurePlans(final Config config, final Map<String, String> keyValueMap) {
    String planFileLocation = keyValueMap.get(PLANS_KEY);     
    try {      
      
      Path planFileLocationAsPath = extractPlansFileLocation(keyValueMap);
      
      /* set plans path location */
      LOGGER.info(String.format("[SETTING] MATSim plans/population file: %s", planFileLocationAsPath.toString()));
      config.plans().setInputFile(planFileLocationAsPath.toAbsolutePath().toString());
      
    }catch (Exception e) {
      LOGGER.warning(String.format("Invalid plans file location %s for --plans, ignored", planFileLocation));
    } 
  }

  /** Configure the CRS of the plans. If not set we use the geo data as is without any conversion. Otherwise it is converted to the MATSim 
   * simulation global CRS.
   * 
   * @param config to configure
   * @param keyValueMap to extract location from
   */   
  private static void configurePlansCrs(Config config, Map<String, String> keyValueMap) {
    String crsValue = keyValueMap.get(PLANS_CRS_KEY);
    if(StringUtils.isNullOrBlank(crsValue)) {
      crsValue = MATSIM_DEFAULT_GLOBAL_CRS;
    }
    
    LOGGER.info(String.format("[SETTING] MATSim plans CRS: %s", crsValue));
    config.plans().setInputCRS(crsValue);
  }

  /** Configure the start time of the simulation. If not set we use the default MATSIM_DEFAULT_STARTTIME and all activities
   * are considered. 
   * 
   * @param config to configure
   * @param keyValueMap to extract location from
   */     
  private static void configureStartTime(final Config config, final Map<String, String> keyValueMap) {
    String startTimeValue = keyValueMap.get(STARTTIME_KEY);
    if(StringUtils.isNullOrBlank(startTimeValue)) {
      startTimeValue = MATSIM_DEFAULT_STARTTIME;
    }
           
    LOGGER.info(String.format("[SETTING] MATSim simulation start time: %s", startTimeValue));
    config.qsim().setStartTime(Time.parseTime(startTimeValue));
  }

  /** Configure the end time of the simulation. If not set we use the default MATSIM_DEFAULT_ENDTIME and all activities
   * are considered. 
   * 
   * @param config to configure
   * @param keyValueMap to extract location from
   */       
  private static void configureEndTime(final Config config, final Map<String, String> keyValueMap) {
    String endTimeValue = keyValueMap.get(ENDTIME_KEY);
    if(StringUtils.isNullOrBlank(endTimeValue)) {
      endTimeValue = MATSIM_DEFAULT_ENDTIME;
    }
           
    LOGGER.info(String.format("[SETTING] MATSim simulation end time: %s", endTimeValue));
    config.qsim().setStartTime(Time.parseTime(endTimeValue));  }

  /** Configure the storage capacity factor to apply to all links in simulation
   * 
   * @param config to use
   * @param keyValueMap to extract value from
   */
  private static void configureStorageCapacityFactor(Config config, Map<String, String> keyValueMap) {
    double factor = 1;
    try {
      String storageCapacityFactor = keyValueMap.get(CAPACITY_STORAGE_FACTOR_KEY);
      if(storageCapacityFactor != null) {
        factor = Double.parseDouble(storageCapacityFactor);
        config.qsim().setStorageCapFactor(factor);
      }
    }catch(Exception e) {
      LOGGER.warning("IGNORED: Simulation storage capacity factor is not a valid floating point value");
    }        
    LOGGER.info(String.format("[SETTING] MATSim storage capacity factor: %.2f", factor));    
  }

  /** Configure the flow capacity factor to apply to all links in simulation
   * 
   * @param config to use
   * @param keyValueMap to extract value from
   */  
  private static void configureFlowCapacityFactor(Config config, Map<String, String> keyValueMap) {
    double factor = 1;
    try {
      String flowCapacityFactor = keyValueMap.get(CAPACITY_FLOW_FACTOR_KEY);
      if(flowCapacityFactor != null) {
        factor = Double.parseDouble(flowCapacityFactor);
        config.qsim().setFlowCapFactor(factor);
      }
    }catch(Exception e) {
      LOGGER.warning("IGNORED: Simulation flow capacity factor is not a valid floating point value");
    }  
    LOGGER.info(String.format("[SETTING] MATSim flow capacity factor: %.2f", factor));        
  }

  /** Configure the maximum number of iterations of the simulation. If not set we use the default DEFAULT_ITERATIONS_MAX.
   * 
   * @param config to configure
   * @param keyValueMap to extract location from
   */     
  private static void configureIterationsMax(final Config config, final Map<String, String> keyValueMap) {
    String iterationsMaxValue = keyValueMap.get(ITERATIONS_MAX_KEY);
    
    Integer iterationsMax = null; 
    if(StringUtils.isNullOrBlank(iterationsMaxValue)) {
      iterationsMax = DEFAULT_ITERATIONS_MAX;
    }else {
      iterationsMax = Integer.parseInt(iterationsMaxValue);  
    }
           
    LOGGER.info(String.format("[SETTING] MATSim max iterations: %s", iterationsMaxValue));        
    config.controler().setLastIteration(iterationsMax);    
  }

  /** Reads a separate config file that is supposed to ONLY contain the activity types configuration that goes alongside
   * the plans.xml. The configuration of the activities in this config file is merged with the provided config.
   * 
   * Note: Currently there is no fail safe for if users provide additional configuration in this file. This is now
   * simply merged with the config as well. IDeally we refactor this so that ONLY the activity component is merged. However
   * the MATSim code is quite messy and not documented very well on how to do this elegantly.
   * 
   * @param config to merge with activity configuration
   * @param keyValueMap to use to locate the activity configuration file
   */
  private static void configureActivityConfig(final Config config, final Map<String, String> keyValueMap) {
    String activityConfigValue = keyValueMap.get(ACTIVITY_CONFIG_KEY);
    if(StringUtils.isNullOrBlank(activityConfigValue )) {
      LOGGER.warning(String.format("Missing activity configuration file (--%s), invalid simulation run",ACTIVITY_CONFIG_KEY));
      return;
    }
    if(!Paths.get(activityConfigValue).toFile().exists()) {
      LOGGER.warning(String.format("Provided activity configuration file (--%s) not available, invalid simulation run",ACTIVITY_CONFIG_KEY));
    }
    
    /* merge two config files assuming the activity config file ONLY contains the activity configuration portion */
    LOGGER.info(String.format("[SETTING] MATSim activity config settings: %s", activityConfigValue));        
    ConfigUtils.loadConfig(config, activityConfigValue);
  }

  /**
   * Create the default configuration for a car only simulation based on this wrapper's default config
   * file.
   */
  private static Config createDefaultCarConfiguration() {
    Config config = ConfigUtils.createConfig();
    URL baseConfigUrl = ResourceUtils.getResourceUrl(DEFAULT_CAR_CONFIG_RESOURCE);    
    ConfigUtils.loadConfig(config, baseConfigUrl.getPath());
    return config;
  }

  /** Verify if the simulation is based on using MATSim configuration file(s) or using command line arguments
   * 
   * @return true when based on config files, false otherwise
   */
  public static boolean isSimulationConfigurationFileBased(final Map<String, String> keyValueMap) {
    return keyValueMap.containsKey(CONFIG_KEY) && isSimulationType(keyValueMap);
  }  
          
  /** Check if the chosen type relates to generation a configuration file or not
   * 
   * @param keyValueMap to check
   * @return true when TYPE_DEFAULT_CONFIG_VALUE or TYPE_CONFIG_VALUE is used for key TYPE_KEY, false otherwise
   */
  public static boolean isConfigurationType(final Map<String, String> keyValueMap) {
    String type = keyValueMap.get(TYPE_KEY);
    switch (type) {
      case TYPE_DEFAULT_CONFIG_VALUE:
        return true;
      case TYPE_CONFIG_VALUE:
        return true;      
      default:
        return false;
    }
  }  
  
  /** Check if the chosen type relates to running a MATSim simulation
   * 
   * @param keyValueMap to check
   * @return true when 
   */
  public static boolean isSimulationType(final Map<String, String> keyValueMap) {
    String type = keyValueMap.get(TYPE_KEY);
    switch (type) {
      case TYPE_SIMULATION_VALUE:
        return true;
      default:
        return false;
    }
  }   
   

  /** Collect the location of the config file from the command line arguments (if any)
   * 
   * @param keyValueMap to extract from
   * @return found config file location
   */
  public static String getConfigFileLocation(Map<String, String> keyValueMap) {
    if(!isSimulationConfigurationFileBased(keyValueMap)) {
      LOGGER.warning("Cannot extract config file location when simulation is not config file based");
      return null;
    }
    
    String configFileLocation = keyValueMap.get(CONFIG_KEY);
    if(!StringUtils.isNullOrBlank(configFileLocation)) {    
      LOGGER.info(String.format("[SETTING] MATSim config file location : %s", configFileLocation));        
    }
    return configFileLocation;    
  }

  /** Collect the location of an additional config file which overrides the base config file for the available contents
   * 
   * @param keyValueMap to extract from
   * @return found override config file location
   */  
  public static String getOverrideConfigFileLocation(final Map<String, String> keyValueMap) {
    if(!isSimulationConfigurationFileBased(keyValueMap)) {
      LOGGER.warning("Cannot extract override config file locations when simulation is not properly config file based");
      return null;
    }

    String overrideConfigFileLocation = keyValueMap.get(OVERRIDE_CONFIG_KEY);
    if(!StringUtils.isNullOrBlank(overrideConfigFileLocation)) {
      LOGGER.info(String.format("[SETTING] MATSim additional config file location : %s", overrideConfigFileLocation));        
    }
    return keyValueMap.get(overrideConfigFileLocation); 
  }

  /** The output directory to use. If not configure the default is provided which is the working directory of the application
   * 
   * @param keyValueMap to extract information from
   * @throws PlanItException thrown if error
   */
  public static Path parseOutputDirectory(final Map<String, String> keyValueMap) throws PlanItException {
    PlanItException.throwIfNull(keyValueMap, "Configuration information null");
    
    Path outputDir = null;
    if(keyValueMap.containsKey(OUTPUT_KEY)) {
      outputDir = Paths.get(keyValueMap.get(OUTPUT_KEY));  
    }else {
      outputDir =DEFAULT_OUTPUT_PATH; 
    }
    
    LOGGER.info(String.format("[SETTING] MATSim output directory : %s", outputDir.toString()));        
    return outputDir;
  }


  /** Generate a MATSim configuration in memory based on command line arguments. 
   * 
   * @param keyValueMap to extract command line arguments from
   * @return created MATSim config instance
   */  
  public static Config createConfigurationFromCommandLine(final Map<String, String> keyValueMap) {
    Config config = createDefaultCarConfiguration();
    
    /* do this first to ensure that other options are not overwritten by this additional config file in case
     * the user includes more than just the activity configuration portion */
    PlanitAurinMatsimHelper.configureActivityConfig(config,keyValueMap);
    
    PlanitAurinMatsimHelper.configureModes(config, keyValueMap);
    PlanitAurinMatsimHelper.configureCrs(config,keyValueMap);
    PlanitAurinMatsimHelper.configureNetwork(config,keyValueMap);
    PlanitAurinMatsimHelper.configureNetworkCrs(config,keyValueMap);
    PlanitAurinMatsimHelper.configurePlans(config,keyValueMap);
    PlanitAurinMatsimHelper.configurePlansCrs(config,keyValueMap);  
    PlanitAurinMatsimHelper.configureStartTime(config,keyValueMap);
    PlanitAurinMatsimHelper.configureEndTime(config,keyValueMap);
    PlanitAurinMatsimHelper.configureFlowCapacityFactor(config,keyValueMap);
    PlanitAurinMatsimHelper.configureStorageCapacityFactor(config,keyValueMap);
    PlanitAurinMatsimHelper.configureIterationsMax(config,keyValueMap);
    
    return config;
  }  
  
  /** Generate a MATSim configuration in memory based on config file(s). 
   * 
   * @param configFile location to parse base config file from
   * @param overrideFiles locations to parse additional config files from to override base config file
   * @return created MATSim config instance
   */  
  public static Config createConfigurationFromFiles(final String configFile, final String... overrideFiles) {  
    Config config = ConfigUtils.loadConfig(configFile);
    if(overrideFiles!=null) {
      for(int index=0;index<overrideFiles.length;++index) {
        ConfigUtils.loadConfig(config, overrideFiles[index]);
      }
    }
    return config;
  }

  /** Verify if population (plans) is to be down sampled
   * 
   * @param keyValueMap to check for
   * @return true when down sampling is enable, false otherwise
   */
  public static boolean isPopulationPlansDownSampled(Map<String, String> keyValueMap) {
    if(!keyValueMap.containsKey(PLANS_SAMPLE_KEY)) {
      return false;
    }
    
    try {
      double downSamplingFactor = Double.parseDouble(keyValueMap.get(PLANS_SAMPLE_KEY));
      if(downSamplingFactor>1) {
        LOGGER.warning("IGNORED: Plans down sampling percentage is larger than 1, should be between 0 and 1");  
      }
      return (downSamplingFactor + Precision.EPSILON_3) < 1;
    }catch(Exception e) {
      LOGGER.warning("IGNORED: Plans down sampling percentage is not a valid floating point value");
      return false;
    }
  }
  
  /** A plans or populations file cannot be downsampled on the fly and conduct a simulation. Therefore it is created
   * separately via this method based on the original plans file from the command line arguments and the provided output 
   * directory to store it in. The original plans file name is supplemented with the sample size of the new population to create
   * the new file name 
   * 
   * @param keyValueMap
   * @param outputDir
   * @return path to down sampled plans file
   */
  public static Path createDownSampledPopulation(Map<String, String> keyValueMap, Path outputDir) {
    if(PlanitAurinMatsimHelper.isPopulationPlansDownSampled(keyValueMap)) {
      Path originalPlanFileLocationAsPath = extractPlansFileLocation(keyValueMap);
      double sampleSize = Double.parseDouble(keyValueMap.get(PLANS_SAMPLE_KEY));
      Population population = PopulationUtils.readPopulation(originalPlanFileLocationAsPath.toAbsolutePath().toString());

      PopulationUtils.sampleDown(population, sampleSize);
      
      String[] originalPlansFileName = originalPlanFileLocationAsPath.subpath(originalPlanFileLocationAsPath.getNameCount()-1, originalPlanFileLocationAsPath.getNameCount()).toString().split(".");
      Path updatedPlansFileLocationAsPath = Path.of(outputDir.toAbsolutePath().toString(), originalPlansFileName[0],String.format("_sample_%.4f", sampleSize),originalPlansFileName[1]);
      PopulationUtils.writePopulation(population, updatedPlansFileLocationAsPath.toAbsolutePath().toString());
      
      LOGGER.info(String.format("[Downsampled MATSim plans file %s by factor %.4f",originalPlanFileLocationAsPath.toString(), sampleSize)); 
      return updatedPlansFileLocationAsPath;
    }
    return null;
  }  
}
