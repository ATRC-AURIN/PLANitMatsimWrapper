package org.planit.aurin.matsim;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.logging.Logger;

import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.utils.misc.Time;
import org.planit.utils.exceptions.PlanItException;
import org.planit.utils.misc.StringUtils;

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
  
  //----------------------------------------------------
  //-------- PLAN --------------------------------------
  //----------------------------------------------------  
  
  /** the default network file name in MATSim*/
  protected static String MATSIM_DEFAULT_PLANS = "plans.xml";
  
  /** Key reflecting the plan file location */
  public static final String PLANS_KEY = "plans";   
  
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
  //-------- ACTIVITY CONFIG --------------------------
  //----------------------------------------------------  
    
  /** Key reflecting the plan file location */
  public static final String ACTIVITY_CONFIG_KEY = "activity_config";    
    
          
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
   

  /** The output directory to use. If not configure the default is provided which is the working directory of the application
   * 
   * @param keyValueMap to extract information from
   * @throws PlanItException thrown if error
   */
  public static Path parseOutputDirectory(Map<String, String> keyValueMap) throws PlanItException {
    PlanItException.throwIfNull(keyValueMap, "Configuration information null");
    
    if(keyValueMap.containsKey(OUTPUT_KEY)) {
      return Paths.get(keyValueMap.get(OUTPUT_KEY));  
    }else {
     return DEFAULT_OUTPUT_PATH; 
    }
    
  }


  /** Configure the available modes in the simulation based on command line arguments provided
   * 
   * @param config to alter
   * @param keyValueMap to extract configuration choice from
   */
  public static void configureModes(final Config config, final Map<String, String> keyValueMap) {
    String modesValue = keyValueMap.get(MODES_KEY);
    switch (modesValue) {
      case MODES_CAR_SIM_VALUE:
        config.changeMode().setModes(new String[] {MATSIM_CAR_MODE});
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
  public static void configureCrs(Config config, Map<String, String> keyValueMap) {
    String crsValue = keyValueMap.get(CRS_KEY);
    if(StringUtils.isNullOrBlank(crsValue)) {
      crsValue = MATSIM_DEFAULT_GLOBAL_CRS;
    }
    
    config.global().setCoordinateSystem(crsValue);
  }  

  /** Configure the location of the network. If not set we use the current working directory and default network name MATSIM_DEFAULT_NETWORK.
   * When invalid path is provided we log a warning and ignore.
   * 
   * @param config to configure
   * @param keyValueMap to extract location from
   */
  public static void configureNetwork(final Config config, final Map<String, String> keyValueMap) {
    
    String networkFileLocation = keyValueMap.get(NETWORK_KEY);     
    try {      
      
      Path networkFileLocationAsPath = null;      
      if(StringUtils.isNullOrBlank(networkFileLocation)) {
        networkFileLocation = Paths.get(CURRENT_PATH.toString(), MATSIM_DEFAULT_NETWORK).toAbsolutePath().toString();
      }      
      networkFileLocationAsPath = Paths.get(networkFileLocation);
      
      /* set network path location */
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
  public static void configureNetworkCrs(Config config, Map<String, String> keyValueMap) {
    String crsValue = keyValueMap.get(CRS_KEY);
    if(StringUtils.isNullOrBlank(crsValue)) {
      crsValue = MATSIM_DEFAULT_GLOBAL_CRS;
    }
    
    config.network().setInputCRS(crsValue);
  }   

  /** Configure the location of the activities. If not set we use the current working directory and default plans name MATSIM_DEFAULT_PLANS.
   * When invalid path is provided we log a warning and ignore.
   * 
   * @param config to configure
   * @param keyValueMap to extract location from
   */
  public static void configurePlans(final Config config, final Map<String, String> keyValueMap) {
    String planFileLocation = keyValueMap.get(PLANS_KEY);     
    try {      
      
      Path planFileLocationAsPath = null;      
      if(StringUtils.isNullOrBlank(planFileLocation)) {
        planFileLocation = Paths.get(CURRENT_PATH.toString(), MATSIM_DEFAULT_PLANS).toAbsolutePath().toString();
      }      
      planFileLocationAsPath = Paths.get(planFileLocation);
      
      /* set plans path location */
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
  public static void configurePlansCrs(Config config, Map<String, String> keyValueMap) {
    String crsValue = keyValueMap.get(CRS_KEY);
    if(StringUtils.isNullOrBlank(crsValue)) {
      crsValue = MATSIM_DEFAULT_GLOBAL_CRS;
    }
    
    config.plans().setInputCRS(crsValue);
  }

  /** Configure the start time of the simulation. If not set we use the default MATSIM_DEFAULT_STARTTIME and all activities
   * are considered. 
   * 
   * @param config to configure
   * @param keyValueMap to extract location from
   */     
  public static void configureStartTime(final Config config, final Map<String, String> keyValueMap) {
    String startTimeValue = keyValueMap.get(STARTTIME_KEY);
    if(StringUtils.isNullOrBlank(startTimeValue)) {
      startTimeValue = MATSIM_DEFAULT_STARTTIME;
    }
           
    config.qsim().setStartTime(Time.parseTime(startTimeValue));
  }

  /** Configure the end time of the simulation. If not set we use the default MATSIM_DEFAULT_ENDTIME and all activities
   * are considered. 
   * 
   * @param config to configure
   * @param keyValueMap to extract location from
   */       
  public static void configureEndTime(final Config config, final Map<String, String> keyValueMap) {
    String startTimeValue = keyValueMap.get(ENDTIME_KEY);
    if(StringUtils.isNullOrBlank(startTimeValue)) {
      startTimeValue = MATSIM_DEFAULT_ENDTIME;
    }
           
    config.qsim().setStartTime(Time.parseTime(startTimeValue));  }

  /** Configure the maximum number of iterations of the simulation. If not set we use the default DEFAULT_ITERATIONS_MAX.
   * 
   * @param config to configure
   * @param keyValueMap to extract location from
   */     
  public static void configureIterationsMax(final Config config, final Map<String, String> keyValueMap) {
    String iterationsMaxValue = keyValueMap.get(ITERATIONS_MAX_KEY);
    
    Integer iterationsMax = null; 
    if(StringUtils.isNullOrBlank(iterationsMaxValue)) {
      iterationsMax = DEFAULT_ITERATIONS_MAX;
    }else {
      iterationsMax = Integer.parseInt(iterationsMaxValue);  
    }
           
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
  public static void configureActivityConfig(final Config config, final Map<String, String> keyValueMap) {
    String activityConfigValue = keyValueMap.get(ACTIVITY_CONFIG_KEY);
    if(StringUtils.isNullOrBlank(activityConfigValue )) {
      LOGGER.warning(String.format("Missing activity configuration file (--%s), invalid simulation run",ACTIVITY_CONFIG_KEY));
      return;
    }
    if(!Paths.get(activityConfigValue).toFile().exists()) {
      LOGGER.warning(String.format("Provided activity configuration file (--%s) not available, invalid simulation run",ACTIVITY_CONFIG_KEY));
    }
    
    /* merge two config files assuming the activity config file ONLY contains the activity configuration portion */
    ConfigUtils.loadConfig(config, activityConfigValue);
  }

  
}
