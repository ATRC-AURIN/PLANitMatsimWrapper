package org.goplanit.aurin.matsim;

/**
 * the different options on what modes to support
 * 
 * @author markr
 *
 */
public enum ModesType {

  NONE("none"),
  CAR_ONLY("car_sim"),
  CAR_PT_TELEPORT("car_sim_pt_teleport"),
  CAR_PT("car_pt_sim");
   
  final String value;
  
  private ModesType(final String value){
    this.value = value;
    
  }
  
  /** Construct from its value
   * 
   * @param modesType to covert
   * @return ModesType, NONE if not compatible
   */
  public static ModesType of(final String modesType) {
    switch (modesType) {
    case "car_sim":
      return CAR_ONLY;
    case "car_sim_pt_teleport":
      return CAR_PT_TELEPORT;
    case "car_pt_sim":
      return CAR_PT;      
    default:
      return NONE;
    }
  }  
   
  public String getValue() {
    return value;
  }
}
