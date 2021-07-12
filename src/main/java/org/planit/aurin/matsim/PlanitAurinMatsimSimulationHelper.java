package org.planit.aurin.matsim;

import java.util.Map;

/**
 * Helper methods to configure the MATSim simulation based on user arguments provided for this wrapper.
 * 
 * @author markr
 *
 */
public class PlanitAurinMatsimSimulationHelper extends PlanitAurinMatsimHelper {
  
  //----------------------------------------------------
  //--------TYPE -----------------------------
  //----------------------------------------------------
  
  /** Value reflecting the need to conduct a MATSim simulation */
  public static final String TYPE_SIMULATION_VALUE = "simulation";
  
          
  /** Check if the chosen type relates to generation a configuration file or not
   * 
   * @param keyValueMap to check
   * @return true when TYPE_DEFAULT_CONFIG_VALUE or TYPE_CONFIG_VALUE is used for key TYPE_KEY, false otherwise
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
  

}
