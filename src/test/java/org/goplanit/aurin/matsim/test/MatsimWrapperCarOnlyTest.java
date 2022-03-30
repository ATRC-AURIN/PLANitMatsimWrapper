package org.goplanit.aurin.matsim.test;

import static org.junit.Assert.fail;

import java.net.URL;
import java.nio.file.Path;

import org.goplanit.aurin.matsim.PlanitAurinMatsimMain;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.goplanit.utils.misc.FileUtils;
import org.goplanit.utils.misc.UrlUtils;
import org.goplanit.utils.resource.ResourceUtils;

/**
 * Test the PLANit MATSim simulation Wrapper for the AURIN platform for various car only configures situations
 * 
 * TODO: add tests where we actively downsample to see if this works + if it generates a downsampled activity plans file as expected
 * 
 * @author markr
 *
 */
public class MatsimWrapperCarOnlyTest {
  
  private static final URL network = ResourceUtils.getResourceUrl("./Melbourne/car_simple_melbourne_network_cleaned.xml");
  private static final URL plans = ResourceUtils.getResourceUrl("./Melbourne/plans_victoria_car.xml");
  private static final URL activity_config = ResourceUtils.getResourceUrl("./Melbourne/activity_config.xml");
  private static final URL EXAMPLE_USER_CONFIG_NO_ACTIVITY_TYPES = ResourceUtils.getResourceUrl("./Melbourne/car_no_activity_types_user_config.xml");;
  
  private static final Path MATSIM_TMP_DIR = Path.of(".","output","car_tmp");

  /**
   * Ensure that generated output files in tmp dir are cleaned up by deleting dirs and content because otherwise
   * we get errors dir could not be created for some reason
   */
  @BeforeClass
  public static void beforeClass(){
    FileUtils.deleteDirectory(MATSIM_TMP_DIR.toAbsolutePath().toFile());
  }
  
  /**
   * Test with local inputs via command line call. Current plans file is already a sample, so no need to 
   * down sample scale at this point
   */
  @Test
  public void matsimSimulation() {
    try {
      
      double downSampleFactor = 1;
      int iterationsMax = 2;
      
      int linkStatsAverageInterval = 1;
      int linkStatsWriteInterval = 1;
        
      /** JAVA COMMAND LINE VERSION **
       java -jar PLANitAurinMatsim_version_.jar --type simulation --modes car_sim --crs epsg:3112 \ 
            --network "..\src\test\resources\Melbourne\car_simple_melbourne_network.xml" --network_crs epsg:3112 \
            --plans "..\src\test\resources\Melbourne\plans_victoria_car.xml" --plans_crs epsg:3112 --activity_config \
            "..\src\test\resources\Melbourne\activity_config.xml" --link_stats 1,1 --iterations_max 2 \ 
            --network_clean yes 
      ** JAVA COMMAND LINE VERSION **/               
      
      PlanitAurinMatsimMain.main(
          new String[]{
              "--type",
              "simulation",
              "--modes",
              "car_sim",
              "--crs",
              "epsg:3112",              
              "--network",
              UrlUtils.asLocalPath(network).toString(),
              "--network_crs",
              "epsg:3112",
              "--plans",
              UrlUtils.asLocalPath(plans).toString(),
              "--plans_crs",
              "epsg:3112",              
              "--activity_config",
              UrlUtils.asLocalPath(activity_config).toString(),
              "--flowcap_factor",
              String.valueOf(downSampleFactor),
              "--storagecap_factor",
              String.valueOf(downSampleFactor),
              "--link_stats",
              String.valueOf(linkStatsAverageInterval)+"," + String.valueOf(linkStatsWriteInterval),
              "--network_clean",
              "yes",
              "--iterations_max",
              String.valueOf(iterationsMax),
              "--output",
              MATSIM_TMP_DIR.toAbsolutePath().toString()
              });     
      
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("Error when testing Aurin MATSim simulation Wrapper - matsimSimulationTestCarOnly");
    }
  }
  
  /**
   * Test simulation run with inputs based on configuration  and override configuration file.Allows users
   * to configure their simulation as they see fit, but no checks are performed on correctness. So if it is
   * somehow incorrect the simulation will fail.
   */
  @Test
  public void matsimSimulationCustomConfigFile() {
    try {
                    
      /** use car template configuration with additionally defined locations for inputs + supplement with activity configuration **/
      PlanitAurinMatsimMain.main(
          new String[]{
              "--type",
              "simulation",
              "--config",
              UrlUtils.asLocalPath(EXAMPLE_USER_CONFIG_NO_ACTIVITY_TYPES).toString(),
              "--override_config",
              UrlUtils.asLocalPath(activity_config).toString()});     
      
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("Error when testing Aurin MATSim simulation Wrapper - matsimSimulationTestCarOnly");
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
              "car_sim",
              "--iterations_max",
              String.valueOf(iterationsMax)});      
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("Error when testing Aurin MATSim simulation Wrapper - matsimDefaultConfigOnly");
    }
  }

}

