package org.goplanit.aurin.matsim.test;

import static org.junit.Assert.fail;

import java.io.File;
import java.net.URL;

import org.goplanit.aurin.matsim.PlanitAurinMatsimMain;
import org.junit.AfterClass;
import org.junit.Test;
import org.goplanit.utils.misc.FileUtils;
import org.goplanit.utils.misc.UrlUtils;
import org.goplanit.utils.resource.ResourceUtils;

/**
 * Test the PLANit MATSim simulation Wrapper for the AURIN platform for various car simulation and public transport as teleportation mode situations
 * 
 * TODO: add tests where we actively downsample to see if this works + if it generates a downsampled activity plans file as expected
 * 
 * @author markr
 *
 */
public class MatsimWrapperCarSimPtTeleportTest {
  
  private static final URL NETWORK_URL = ResourceUtils.getResourceUrl("./Melbourne/car_pt_simple_melbourne_network.xml");
  private static final URL CLEANED_NETWORK_URL = ResourceUtils.getResourceUrl("./Melbourne/car_pt_simple_melbourne_network_cleaned.xml");
  private static final URL PLANS_URL = ResourceUtils.getResourceUrl("./Melbourne/plans_victoria_car_pt_tele.xml");
  private static final URL ACTIVITY_CONFIG_URL = ResourceUtils.getResourceUrl("./Melbourne/activity_config.xml");
  
  private static final File MATSIM_TMP_DIR = new File("./output/car_pt_tele_tmp");

  /**
   * Ensure that generated output files in tmp dir are cleaned up by deleting dirs and content because otherwise
   * we get errors dir could not be created for some reason
   */
  @AfterClass
  public static void afterClass(){
    FileUtils.deleteDirectory(MATSIM_TMP_DIR);
  }
  
  /**
   * Test with local inputs via command line call. Current plans file is already a sample, so no need to 
   * down sample scale at this point
   */
  @Test
  public void matsimSimulation() {
    try {
      
      double downSampleFactor = 1;
      int iterationsMax = 1;
      
      int linkStatsAverageInterval = 1;
      int linkStatsWriteInterval = 1;
        
      // java -jar PLANitAurinMatsim_version_.jar --type simulation --modes car_sim_pt_teleport --crs epsg:3112 
      //      --network "..\src\test\resources\Melbourne\car_pt_tele_simple_melbourne_network_cleaned.xml" --network_crs epsg:3112
      //      --plans "..\src\test\resources\Melbourne\plans_victoria_car_pt_tele.xml" --plans_crs epsg:3112 --activity_config
      //      "..\src\test\resources\Melbourne\activity_config.xml" --link_stats 1,1 --iterations_max 2
      PlanitAurinMatsimMain.main(
          new String[]{
              "--type",
              "simulation",
              "--modes",
              "car_sim_pt_teleport",
              "--crs",
              "epsg:3112",              
              "--network",
              UrlUtils.asLocalPath(CLEANED_NETWORK_URL).toString(),
              "--network_clean",
              "no",
              "--network_crs",
              "epsg:3112",
              "--plans",
              UrlUtils.asLocalPath(PLANS_URL).toString(),
              "--plans_crs",
              "epsg:3112",              
              "--activity_config",
              UrlUtils.asLocalPath(ACTIVITY_CONFIG_URL).toString(),
              "--flowcap_factor",
              String.valueOf(downSampleFactor),
              "--storagecap_factor",
              String.valueOf(downSampleFactor),
              "--link_stats",
              String.valueOf(linkStatsAverageInterval)+"," + String.valueOf(linkStatsWriteInterval),              
              "--iterations_max",
              String.valueOf(iterationsMax)});     
      
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("Error when testing Aurin MATSim simulation Wrapper - MatsimWrapperCarSimPtTeleportTest");
    }
  }
  
  /**
   * Test to generate a configuration file based on the wrapper's tailored config file and the additional command line
   * options provided by the user (if any). The user can then make custom changes and use the config file to run a simulation
   * via this wrapper at a later stage
   * 
   */
  @Test
  public void matsimTemplateConfigGenerator() {
    try {  
        
      int iterationsMax = 250;
      
      PlanitAurinMatsimMain.main(
          new String[]{
              "--type",
              "config",
              "--modes",
              "car_sim_pt_teleport",
              "--iterations_max",
              String.valueOf(iterationsMax)});      
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("Error when testing Aurin MATSim simulation Wrapper - MatsimWrapperCarSimPtTeleportTest");
    }
  }

}

