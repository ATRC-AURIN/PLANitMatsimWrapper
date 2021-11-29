package org.goplanit.aurin.matsim.test;

import static org.junit.Assert.fail;

import org.goplanit.aurin.matsim.PlanitAurinMatsimMain;
import org.junit.Test;

/**
 * Test the creation of the default config file via the wrapper
 * 
 * @author markr
 *
 */
public class MatsimWrapperDefaultConfigTest {
    
  /**
   * Test to generate a default (full - template based) configuration file, otherwise known as the full config in 
   * MATSim terms
   */
  @Test
  public void matsimDefaultConfigGenerator() {
    try {  
        
      PlanitAurinMatsimMain.main(
          new String[]{
              "--type",
              "default_config"});
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("Error when testing Aurin MATSim simulation Wrapper - MatsimWrapperDefaultConfigTest");
    }
  }  
}

