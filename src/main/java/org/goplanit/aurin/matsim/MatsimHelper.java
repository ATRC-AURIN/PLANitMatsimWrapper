package org.goplanit.aurin.matsim;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Logger;

import org.matsim.api.core.v01.TransportMode;
import org.matsim.api.core.v01.population.Population;
import org.matsim.contrib.matrixbasedptrouter.MatrixBasedPtRouterConfigGroup;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.population.PopulationUtils;
import org.matsim.core.utils.misc.Time;
import org.goplanit.utils.exceptions.PlanItException;
import org.goplanit.utils.math.Precision;
import org.goplanit.utils.misc.StringUtils;
import org.goplanit.utils.misc.UriUtils;
import org.goplanit.utils.resource.ResourceUtils;
import org.goplanit.utils.unit.Unit;

/**
 * Helper methods to configure the AURIN MATSim wrapper based on user arguments provided for this wrapper.
 * 
 * @author markr
 *
 */
public class MatsimHelper {
  
  /** the logger to use */
  private static final Logger LOGGER = Logger.getLogger(MatsimHelper.class.getCanonicalName());
  
  /** Path from which application was invoked */
  public static final Path CURRENT_PATH = Path.of(".").toAbsolutePath();
  
  /** the resource file reflecting the base car only configuration for a MATSim run */
  public static final String DEFAULT_CAR_CONFIG_RESOURCE = "baseconfig_car.xml";
  
  /** the resource file reflecting the car simulation with public transport teleportation support default configuration for a MATSim run */
  public static final String DEFAULT_CAR_SIM_PT_TELE_CONFIG_RESOURCE = "baseconfig_car_pt_tele.xml";  
  
  /** the default name for a MATSim configuration file*/
  public static final String DEFAULT_MATSIM_CONFIG_FILE = "config.xml";
    
  //----------------------------------------------------
  //-------- OUTPUT_PATH ---------------------------
  //----------------------------------------------------
  
  /** Key reflecting the type of functionality to unlock*/
  public static final String OUTPUT_KEY = "output";
  
  /** Output path defaults to directory where this application was run from */
  public static final Path DEFAULT_OUTPUT_PATH = Path.of(CURRENT_PATH.toString(),"output");  
  
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
  
  /** the string representation used in MATSim to convert all geo locations to in the output (when not set by the user). Default is WGS84*/
  protected static String MATSIM_DEFAULT_GLOBAL_CRS = "EPSG:4326";
  
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
  
  /** Key reflecting the network crs */
  public static final String NETWORK_CLEAN_KEY = "network_clean";
  
  /** value indicating we should not clean the network */
  public static final String NETWORK_CLEAN_NO_VALUE = "no";
  
  /** value indicating we should clean the network */
  public static final String NETWORK_CLEAN_YES_VALUE = "yes";
  
  /** the default setting for cleaning the network on the fly (no). It is disabled because
   * we assume this has been done during parsing already */
  protected static String DEFAULT_NETWORK_CLEAN = NETWORK_CLEAN_NO_VALUE;  
  
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
  public static String MATSIM_DEFAULT_STARTTIME = "00:00:00";
  
  /** the string representation used in MATSim for the default qsim end time*/
  public static String MATSIM_DEFAULT_ENDTIME = MATSIM_DEFAULT_STARTTIME;  
  
  /** Key reflecting the start time to use in simulation */
  public static final String STARTTIME_KEY = "starttime";    
    
  /** Key reflecting the end time to use in simulation */
  public static final String ENDTIME_KEY = "endtime";     
  
  //----------------------------------------------------
  //-------- ITERATIONS -------------------------------------
  //----------------------------------------------------  
  
  /** the default maximum number of iterations run when not set by user */
  public static Integer DEFAULT_ITERATIONS_MAX = 10;
  
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
  //-------- CAPACITY ----------------------------------
  //----------------------------------------------------  
    
  /** Key reflecting the factor to apply to all link flow capacities in simulation */
  public static final String CAPACITY_FLOW_FACTOR_KEY = "flowcap_factor";      
  
  /** Key reflecting the factor to apply to all link storage capacities in simulation */
  public static final String CAPACITY_STORAGE_FACTOR_KEY = "storagecap_factor";   
  
  //----------------------------------------------------
  //-------- LINK STATS --------------------------------
  //----------------------------------------------------
  
  /** Key reflecting the linkStats configuration to apply to the simulation */
  public static final String LINK_STATS_KEY = "link_stats";  

  //----------------------------------------------------
  //-------- PT MATRIX BASED ROUTER --------------------
  //----------------------------------------------------

  /** Key reflecting the location of the CSV file containing the supported pt stops for the network*/
  public static final String PT_STOPS_CSV_KEY = "pt-stops-csv";
  
  /** Estimate of car teleported speed. Used to for example derive pt teleported speed (multiplied by factor) when
   * including pt as teleported mode */
  public static final double DEFAULT_CAR_TELEPORTED_SPEED_ESTIMATE = 60.0;   
    
  
  /** create a local file in the given directory and file name location for a resource that is available from within a jar file
   * 
   * @param directory to use
   * @param resourceFileNameToUse to use
   * @param resourceLocation URI of resource in jar file
   * @return location of created file which nis a copy of the jar resource on the local file system in desired location
   * @throws IOException thrown if error
   */
  private static Path createLocalFileFromJarResourceOn(final Path directory, final String resourceFileNameToUse, URI resourceLocation) throws IOException {
    InputStream baseConfigInputStream = ResourceUtils.getResourceAsInputStream(resourceLocation);      
    Path jarResourceFileLocation = Path.of(directory.toString(), resourceFileNameToUse);    
    Files.copy(baseConfigInputStream,  jarResourceFileLocation);
    baseConfigInputStream.close();
    return jarResourceFileLocation;
  }

  /** Collect the plans file location from command line arguments. If not set we use the current working directory and default plans name MATSIM_DEFAULT_PLANS.
   * 
   * @param keyValueMap to extract location from
   * @return found location
   */
  private static Path extractPlansFileLocation(Map<String, String> keyValueMap) {
    String planFileLocation = keyValueMap.get(PLANS_KEY);     
    if(StringUtils.isNullOrBlank(planFileLocation)) {
      planFileLocation = Paths.get(CURRENT_PATH.toString(), MATSIM_DEFAULT_PLANS).toString();
    }      
    return Paths.get(planFileLocation).normalize();
  }

  /** Configure the available modes in the simulation based on command line arguments provided
   * 
   * @param config to alter
   * @param keyValueMap to extract configuration choice from
   */
  private static void configureModes(final Config config, final Map<String, String> keyValueMap) {
    String modesValue = keyValueMap.get(MODES_KEY);
    switch (modesValue) {
      case MODES_CAR_SIM_PT_TELEPORT_VALUE:
        LOGGER.info("[SETTING] teleportation mode: pt");
      case MODES_CAR_SIM_VALUE:
        config.changeMode().setModes(new String[] {MATSIM_CAR_MODE});
        LOGGER.info("[SETTING] simulation mode: car");
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
      
      Path networkFileLocationAsPath = parseNetworkFileLocation(keyValueMap);           
      
      /* set network path location */
      LOGGER.info(String.format("[SETTING] MATSim network file: %s", networkFileLocationAsPath.toString()));
      config.network().setInputFile(networkFileLocationAsPath.toString());
      
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
      config.plans().setInputFile(planFileLocationAsPath.toString());
      
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

  /** Configure the link stats, i.e., override the default on how the hourly link stats are persisted
   * if at all.
   * <p> 
   * The link stats are in fact two values, one for the averaging interval (number of iterations to average over) while
   * the second is the interval to use for persisting the link stats. Clearly the former must be smaller than the latter.
   * They are expected to be comma separated
   * 
   * @param config to use
   * @param keyValueMap to use
   */
  private static void configureLinkStats(Config config, Map<String, String> keyValueMap) {
    int averageOverIterations = config.linkStats().getAverageLinkStatsOverIterations();
    int writeInterval = config.linkStats().getWriteLinkStatsInterval();
    TRY : try {
      String linkStats = keyValueMap.get(LINK_STATS_KEY);
      if(linkStats != null) {
        String[] linkStatsArray = linkStats.split(","); 
        if(linkStatsArray.length!=2) {
          LOGGER.warning(String.format("IGNORED: %s value is not a comma seaprated string of length 2, adopting defaults", LINK_STATS_KEY));
          break TRY;
        }
        averageOverIterations = Integer.parseInt(linkStatsArray[0]);
        config.linkStats().setAverageLinkStatsOverIterations(averageOverIterations);
        writeInterval = Integer.parseInt(linkStatsArray[1]);
        config.linkStats().setWriteLinkStatsInterval(writeInterval);
      }
    }catch(Exception e) {
      LOGGER.warning(String.format("IGNORED: %s value is not properly configured", LINK_STATS_KEY));
    }  
    
    if(writeInterval > 0) {
      LOGGER.info(String.format("[SETTING] MATSim linkStats averaged over %d iterations", averageOverIterations));
      LOGGER.info(String.format("[SETTING] MATSim linkStats persisted every %d iterations", writeInterval));
    }else {
      LOGGER.info("[SETTING] MATSim linkStats disabled");
    }
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
      LOGGER.warning(String.format("Missing activity configuration file (--%s)",ACTIVITY_CONFIG_KEY));
      return;
    }
    if(!Paths.get(activityConfigValue).toFile().exists()) {
      LOGGER.warning(String.format("Activity configuration file (--%s) %s not available",ACTIVITY_CONFIG_KEY, activityConfigValue));
      return;
    }
    
    /* merge two config files assuming the activity config file ONLY contains the activity configuration portion */
    LOGGER.info(String.format("[SETTING] MATSim activity config settings: %s", activityConfigValue));        
    ConfigUtils.loadConfig(config, activityConfigValue);
  }
  
  /**
   * We enable MATSim's pt matrix based routing only when any of the required input files for PtMatrixBased simulation are configured, i.e.,
   * the --pt-stops-csv is present.
   *   
   * @param config to configure
   * @param keyValueMap to extract settings from
   */
  private static void configurePtMatrixRouter(final Config config, final Map<String, String> keyValueMap) {
    String ptStopsCsvValue = keyValueMap.get(PT_STOPS_CSV_KEY);
    if(StringUtils.isNullOrBlank(ptStopsCsvValue )) {
      LOGGER.info(String.format("No pt stops CSV provided, MATSim matrix based router not activated"));
      return;
    }
        
    /* parse pt stops and configure PtMatrixBased router */
    var configGroup = new MatrixBasedPtRouterConfigGroup();
    configGroup.setPtStopsInputFile(ptStopsCsvValue);
    configGroup.setUsingTravelTimesAndDistances(false);
    
    /* in absence of a stop-to-stop travel time matrix, MATSim will generate one, but for that to work it needs a teleported mode speed to be set explicitly
     * if not, it simply crashes hard. therefore, set it here explicitly */
    var ptModeRoutingParams = config.plansCalcRoute().getModeRoutingParams().get(TransportMode.pt);
    if(ptModeRoutingParams.getTeleportedModeSpeed()==null) {
      LOGGER.warning("MATSim teleported mode free speed not set in config file. Consider setting it when using PtMatrixRouting, instead inferring speed from freespeed factor instead");

      try {
        final double carMaxSpeedEstimate = Unit.KM_HOUR.convertTo(Unit.METER_SECOND, DEFAULT_CAR_TELEPORTED_SPEED_ESTIMATE);
        double freeSpeedToUse = carMaxSpeedEstimate / ptModeRoutingParams.getTeleportedModeFreespeedFactor();

        double configuredFactor = ptModeRoutingParams.getTeleportedModeFreespeedFactor();
        ptModeRoutingParams.setTeleportedModeFreespeedFactor(null); // must be removed otherwise we can't set speed     
        ptModeRoutingParams.setTeleportedModeSpeed(freeSpeedToUse); //meter per second
        LOGGER.warning(String.format("MATSim teleported mode free speed (car reference speed estimate / free speed factor) set to %.2f / %.2f  = %.2f m/s",carMaxSpeedEstimate, configuredFactor,freeSpeedToUse));        
      } catch (PlanItException e) {
        LOGGER.severe(e.getMessage());
        LOGGER.severe("Unable to convert speed from km/h to m/s");
      }      
    }
    config.addModule(configGroup);
  }

  /**
   * Create the in-memory configuration based on provided config file location where all user options are not predicated on other user options, i.e., they are unconditional.
   * 
   * @param configFileTemplateLocation to create in memory configuration for
   * @throws URISyntaxException when invalid URI 
   * @throws PlanItException when default config cannot be located
   * @throws IOException thrown if error
   */
  private static Config createConfigurationFromFile(final String configFileTemplateLocation) throws URISyntaxException, PlanItException, IOException {
    URI baseConfigUri = ResourceUtils.getResourceUri(configFileTemplateLocation);
    LOGGER.fine(String.format("configuration template sourced from %s",baseConfigUri.toString()));
    
    /* when using resource in jar, extract it as local temp file for Matsim */
    Path configFileLocation = null;
    if(UriUtils.isInJar(baseConfigUri)){      
      /* MATSim is not able to extract from jar, so we have to locally create the file and then delete it afterwards */
      configFileLocation = createLocalFileFromJarResourceOn(CURRENT_PATH,configFileTemplateLocation, baseConfigUri);
    }else {
      /* else just treat it as the local file that it is */
      configFileLocation = Path.of(baseConfigUri);
    }

    /* update config with local config file used */
    if(Files.notExists(configFileLocation)) {
      throw new PlanItException("Configuration file %s to use, not found",configFileTemplateLocation);
    }
    
    Config config = ConfigUtils.createConfig();    
    ConfigUtils.loadConfig(config, configFileLocation.toAbsolutePath().toString());

    /* clean up by deleting temporary file if needed*/
    if(UriUtils.isInJar(baseConfigUri)){
      LOGGER.fine(String.format("deleting local temp file %s",configFileLocation.toString()));
      Files.delete(configFileLocation);
    }
    
    return config;
  }  

  /**
   * Create the default configuration for a car only simulation based on this wrapper's default config
   * file.
   * 
   * @throws URISyntaxException when invalid URI 
   * @throws PlanItException when default config cannot be located
   * @throws IOException thrown if error
   */
  private static Config createDefaultCarSimConfiguration() throws URISyntaxException, PlanItException, IOException {
    LOGGER.info("Using PLANit default car only configuration template");
    return createConfigurationFromFile(DEFAULT_CAR_CONFIG_RESOURCE);
  }
  
  /**
   * Create the default configuration for a car simulation with teleported public transport mode based on this wrapper's default config
   * file.
   * 
   * @throws URISyntaxException when invalid URI 
   * @throws PlanItException when default config cannot be located
   * @throws IOException thrown if error
   */
  private static Config createDefaultCarSimPtTeleportConfiguration() throws URISyntaxException, PlanItException, IOException {
    LOGGER.info("Using PLANit default car simulation with teleported public transport configuration template");
    return createConfigurationFromFile(DEFAULT_CAR_SIM_PT_TELE_CONFIG_RESOURCE);
  }  

  /** Verify if the simulation is based on using MATSim configuration file(s) or using command line arguments
   * 
   * @param keyValueMap to extract from
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
  
  /** Check if the user wants the in-memory MATSim network to be cleaned before conducting the simulation
   * 
   * @param keyValueMap to check
   * @return true when so, false otherwise 
   */
  public static boolean isNetworkCleanActivated(final Map<String, String> keyValueMap) {
    String cleanFlag = keyValueMap.get(NETWORK_CLEAN_KEY);
    if(cleanFlag==null) {
      cleanFlag = DEFAULT_NETWORK_CLEAN;
    }
    
    switch (cleanFlag) {
      case NETWORK_CLEAN_NO_VALUE:
        LOGGER.info(String.format("[SETTING] MATSim clean network: %s", NETWORK_CLEAN_NO_VALUE));
        return false;        
      case NETWORK_CLEAN_YES_VALUE:
        LOGGER.info(String.format("[SETTING] MATSim clean network: %s", NETWORK_CLEAN_YES_VALUE));
        return true;        
      default:
        LOGGER.warning(String.format("Invalid value for --%s switch",NETWORK_CLEAN_KEY));
        LOGGER.info(String.format("[SETTING] MATSim clean network: %s", NETWORK_CLEAN_NO_VALUE));
        return false;
    }
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
    return overrideConfigFileLocation; 
  }

  /** The output directory to use. If not configure the default is provided which is the working directory of the application
   * 
   * @param keyValueMap to extract information from
   * @return parsed output directory as path instance
   * @throws PlanItException thrown if error
   */
  public static Path parseOutputDirectory(final Map<String, String> keyValueMap) throws PlanItException {
    PlanItException.throwIfNull(keyValueMap, "Configuration information null");
    
    Path outputDir = null;
    if(keyValueMap.containsKey(OUTPUT_KEY)) {
      outputDir = Paths.get(keyValueMap.get(OUTPUT_KEY));  
    }else {
      outputDir = DEFAULT_OUTPUT_PATH; 
    }
    
    outputDir = outputDir.normalize();
    LOGGER.info(String.format("[SETTING] MATSim output directory : %s", outputDir.toString()));        
    return outputDir;
  }


  /** Determine what modesType we are working with (car, car and pt (teleport), car and pt))
   * 
   * @param keyValueMap to extract information from
   * @return type found
   */
  public static ModesType parseModesType(final Map<String, String> keyValueMap) {
    if(!keyValueMap.containsKey(MODES_KEY)) {
        return ModesType.CAR_ONLY;
    }
    
    return ModesType.of(keyValueMap.get(MODES_KEY));
  }

  /** Collect the network file location as path based on the user configuration (if any) or defaults
   * 
   * @param keyValueMap to use
   * @return network file location to use
   */
  public static Path parseNetworkFileLocation(Map<String, String> keyValueMap) {        
    String networkFileLocation = keyValueMap.get(NETWORK_KEY);       
    if(StringUtils.isNullOrBlank(networkFileLocation)) {
      networkFileLocation = Paths.get(CURRENT_PATH.toString(), MATSIM_DEFAULT_NETWORK).toString();
    }      
    return Paths.get(networkFileLocation).normalize();                        
  }

  /** Generate a MATSim configuration in memory based on command line arguments.
   * 
   * @param keyValueMap to extract command line arguments from
   * @return created MATSim config instance, null if unable to create
   */  
  public static Optional<Config> createConfigurationFromCommandLine(final Map<String, String> keyValueMap) {
    Config config = null;
    
    /* mode specific base config template */
    var modesType = MatsimHelper.parseModesType(keyValueMap);
    try {      
      switch (modesType) {
      case CAR_ONLY:
        config = createDefaultCarSimConfiguration(); 
        break;
      case CAR_PT_TELEPORT:
        config = createDefaultCarSimPtTeleportConfiguration();
        break;
      default:
        throw new PlanItException("Only car, or car with public transport as teleportation are currently supported, consider using a different --modes values than %s", keyValueMap.get(MODES_KEY));
      }
    }catch(Exception e) {
      LOGGER.severe(e.getMessage());
      LOGGER.severe("Unable to create default MATSim car configuration file to supplement with command line arguments ");
      return Optional.empty();
    }
    
    /* unconditional configuration */
    {
      /* do this first to ensure that other options are not overwritten by this additional config file in case
       * the user includes more than just the activity configuration portion */
      MatsimHelper.configureActivityConfig(config,keyValueMap);
      
      MatsimHelper.configureModes(config, keyValueMap);
      MatsimHelper.configureCrs(config,keyValueMap);
      MatsimHelper.configureNetwork(config,keyValueMap);
      MatsimHelper.configureNetworkCrs(config,keyValueMap);
      MatsimHelper.configurePlans(config,keyValueMap);
      MatsimHelper.configurePlansCrs(config,keyValueMap);  
      MatsimHelper.configureStartTime(config,keyValueMap);
      MatsimHelper.configureEndTime(config,keyValueMap);
      MatsimHelper.configureFlowCapacityFactor(config,keyValueMap);
      MatsimHelper.configureStorageCapacityFactor(config,keyValueMap);
      MatsimHelper.configureLinkStats(config,keyValueMap);
      MatsimHelper.configureIterationsMax(config,keyValueMap);      
    }
    
    /* conditional configuration */
    {
      if(modesType.equals(ModesType.CAR_PT_TELEPORT)) {
        MatsimHelper.configurePtMatrixRouter(config,keyValueMap);
      }      
    }
    
    return Optional.of(config);
  }

  /** Generate a MATSim configuration in memory based on config file(s). 
   * 
   * @param configFile location to parse base config file from
   * @param overrideFiles locations to parse additional config files from to override base config file
   * @return created MATSim config instance
   */  
  public static Optional<Config> createConfigurationFromFiles(final String configFile, final String... overrideFiles) {  
    Config config = ConfigUtils.loadConfig(configFile);
    if(overrideFiles!=null) {
      for(int index=0;index<overrideFiles.length;++index) {
        ConfigUtils.loadConfig(config, overrideFiles[index]);
      }
    }
    return Optional.of(config);
  }

  /** A plans or populations file cannot be downsampled on the fly and conduct a simulation. Therefore it is created
   * separately via this method based on the original plans file from the command line arguments and the provided output 
   * directory to store it in. The original plans file name is supplemented with the sample size of the new population to create
   * the new file name 
   * 
   * @param keyValueMap to extract from
   * @param outputDir to use
   * @return path to down sampled plans file
   */
  public static Path createDownSampledPopulation(Map<String, String> keyValueMap, Path outputDir) {
    if(MatsimHelper.isPopulationPlansDownSampled(keyValueMap)) {
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

  /** Conduct a clean on MATSim network and persist result in location of where the MATSim network was sourced from (append "_cleaned" to origin name in the process)
   * 
   * @param matsimNetwork MATSim network to clean
   */
  public static void cleanAndPersistMatsimNetwork(Map<String, String> keyValueMap, org.matsim.api.core.v01.network.Network matsimNetwork) {
    LOGGER.info("Cleaning MATSim network");
    new org.matsim.core.network.algorithms.NetworkCleaner().run(matsimNetwork);      
    Path networkFileLocationAsPath = MatsimHelper.parseNetworkFileLocation(keyValueMap);
    var originalFileName = networkFileLocationAsPath.getFileName().toString();
    var cleanedNetworkFileName = new StringBuilder().append(originalFileName.substring(0, originalFileName.lastIndexOf('.'))).append("_cleaned").append(originalFileName.substring(originalFileName.lastIndexOf('.'))).toString();
    var networkCleanedPath = Path.of(networkFileLocationAsPath.getParent().toString(),cleanedNetworkFileName);
    LOGGER.info(String.format("Persisting cleaned MATSim network as %s",networkCleanedPath.toAbsolutePath().toString()));
    org.matsim.core.network.NetworkUtils.writeNetwork(matsimNetwork, networkCleanedPath.toString());
  }
}
