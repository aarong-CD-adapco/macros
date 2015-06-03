/**
 * Examble macro that reads an existing generic object from
 * a simulation.
 */
package macro;

import java.util.*;

import star.common.*;
import star.base.neo.*;
import star.base.query.*;
import star.base.generic.*;

public class testgenericread extends StarMacro {
    
    private Simulation sim;

  public void execute() {
    execute0();
  }

  private void execute0() {

    sim = getActiveSimulation();
    
    // load generic library
    loadStarGeneric();
    
    // get generic object manager
    GenericObjectManager mgr = GenericObjectManager.get(sim);
    
    // get an existing generic object from the simulation
    GenericObject obj = (GenericObject) mgr.getObject("xyz");
    
    // read generic properties
    NeoProperty genericProperties = obj.getGenericProperties();
    
    // primitive values
    sim.println("Integer: " + genericProperties.getInt("Integer"));
    sim.println("Double: " + genericProperties.getDouble("Double"));
    sim.println("String: " + genericProperties.getString("String"));
    sim.println("Boolean: " + genericProperties.getBoolean("Boolean"));
    sim.println("Char: " + genericProperties.getInt("Char"));
    
    // object reference - checks for null
    ClientServerObjectKey regionKey = genericProperties.getObjectKey("Region", sim.getObjectRegistry());
    if (regionKey == null)
        sim.println("Region: null");
    else {
        Region region = genericProperties.getObject("Region", sim.getObjectRegistry());
        sim.println("Region: " + region.getPresentationName() + ", key: " + regionKey.getObjectKeyAsString());
    }
    
    // vector of object references
    Vector<String> v = genericProperties.<String>getVector("Units");
    Vector<ClientServerObject> units = genericProperties.getObjectVector("Units", sim.getObjectRegistry());
    sim.println("Units: ");
    for (ClientServerObject u : units) {
        sim.println("   " + u.getPresentationName());
    }
  }
  
  private void loadStarGeneric() {
      NeoProperty args = new NeoProperty();
      args.put("BaseName", "StarGeneric");
      sim.execute("LoadLibrary", args); // NOI18N    
  }
}
