package org.planit.aurin.matsim;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.logging.Logger;

import org.matsim.core.config.Config;
import org.planit.utils.exceptions.PlanItException;

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
    String type = keyValueMap.get(MODES_KEY);
    switch (type) {
      case MODES_CAR_SIM_VALUE:
        config.changeMode().setModes(new String[] {MATSIM_CAR_MODE});
        break;
      case MODES_CAR_SIM_PT_TELEPORT_VALUE:
        LOGGER.warning(String.format("value %s for --modes not yet supported, ignored", type));
        break;        
      case MODES_CAR_PT_SIM_VALUE:
        LOGGER.warning(String.format("value %s for --modes not yet supported, ignored", type));
        break;                
      default:
        LOGGER.warning(String.format("Unknown value %s for --modes argument, ignored", type));
    }        
  }


  public static void configureNetwork(final Config config, final Map<String, String> keyValueMap) {
    // TODO Auto-generated method stub
    
  }


  public static void configureActivityConfig(final Config config, final Map<String, String> keyValueMap) {
    // TODO Auto-generated method stub
    
  }


  public static void configureCoordinateReferenceSystem(final Config config, final Map<String, String> keyValueMap) {
    // TODO Auto-generated method stub
    
  }


  public static void configureMaxIterations(final Config config, final Map<String, String> keyValueMap) {
    // TODO Auto-generated method stub
    
  }


  public static void configurePlans(final Config config, final Map<String, String> keyValueMap) {
    // TODO Auto-generated method stub
    
  }


  public static void configureStartTime(final Config config, final Map<String, String> keyValueMap) {
    // TODO Auto-generated method stub
    
  }


  public static void configureEndTime(final Config config, final Map<String, String> keyValueMap) {
    // TODO Auto-generated method stub
    
  }  
  
}
