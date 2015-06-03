/**
 * Example macro that adds generic objects with
 * a property list to the simulation in a hierarchy.
 */
package macro;

import java.util.*;

import star.common.*;
import star.base.neo.*;
import star.base.query.*;
import star.base.query.Query;
import star.base.generic.*;

public class testgenericHierarchy extends StarMacro {
    
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
    
    // build a property list
    NeoProperty props = new NeoProperty();
    props.put("Integer", 1);
    
    // create a generic objects in a hierarchy
    GenericObject obj1 = mgr.createGenericObject("level 1", props);
    GenericObject obj2 = obj1.getGenericObjectChildren().createGenericObject("level 2", props);
    GenericObject obj3 = obj2.getGenericObjectChildren().createGenericObject("level 3", props);
    
    sim.println(obj1.getPresentationName() + " parent: " + obj1.getParent().getParent().getPresentationName());
    sim.println(obj2.getPresentationName() + " parent: " + obj2.getParent().getParent().getPresentationName());
    sim.println(obj3.getPresentationName() + " parent: " + obj3.getParent().getParent().getPresentationName());
  }
  
  private void loadStarGeneric() {
      NeoProperty args = new NeoProperty();
      args.put("BaseName", "StarGeneric");
      sim.execute("LoadLibrary", args); // NOI18N    
  }
}
