package org.planit.aurin.matsim.test;

import static org.junit.Assert.fail;

import java.net.URL;
import org.junit.Test;
import org.planit.aurin.matsim.PlanitAurinMatsimMain;
import org.planit.utils.misc.UrlUtils;
import org.planit.utils.resource.ResourceUtils;

/**
 * Test the PLANit MATSim simulation Wrapper for the AURIN platform
 * 
 * @author markr
 *
 */
public class AurinMatsimWrapperTest {

  /**
   * Test with local inputs via command line call
   */
  @Test
  public void matsimSimulationTestCarOnlyTenPercent() {
    try {
      
      URL network = ResourceUtils.getResourceUrl("./Melbourne/car_simple_melbourne_network.xml");
      URL plans = ResourceUtils.getResourceUrl("./Melbourne/plans_victoria.xml");
      URL activity_config = ResourceUtils.getResourceUrl("./Melbourne/activity_config.xml");

      double downSampleFactor = 0.1;
      int iterationsMax = 2;
      
      // Run simulation with settings equivalent to an executable jar call of: 
      // java -jar PLANitAurinMatsim.jar XXXXXXX
      PlanitAurinMatsimMain.main(
          new String[]{
              "--type",
              "simulation",
              "--modes",
              "car_sim",
              "--crs",
              "epsg:3112",              
              "--network",
              UrlUtils.asLocalPath(network).toAbsolutePath().toString(),
              "--network_crs",
              "epsg:3112",
              "--plans",
              UrlUtils.asLocalPath(plans).toAbsolutePath().toString(),
              "--plans_crs",
              "epsg:28355",              
              "--activity_config",
              UrlUtils.asLocalPath(activity_config).toAbsolutePath().toString(),
              "--flowcap_factor",
              String.valueOf(downSampleFactor),
              "--storagecap_factor",
              String.valueOf(downSampleFactor),
              "--iterations_max",
              String.valueOf(iterationsMax)});     
      
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("Error when testing Aurin MATsim simulation Wrapper");
    }
  }

}

