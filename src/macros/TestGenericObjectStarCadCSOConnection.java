/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package macros;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import star.base.generic.GenericObject;
import star.base.generic.GenericObjectManager;
import star.base.neo.ClientServerObject;
import star.base.neo.ClientServerObjectKey;
import star.base.neo.NeoProperty;
import star.common.Simulation;
import star.common.StarMacro;
import star.starcad2.StarCadDesignParameter;
import star.starcad2.StarCadDesignParameterDouble;
import star.starcad2.StarCadDocument;
import star.starcad2.StarCadDocumentManager;

/**
 *
 * @author aarong
 */
public class TestGenericObjectStarCadCSOConnection extends StarMacro {
    
    private final String goLabel = "StarCadDesignParameterDoubleCSO";

    Simulation sim;
    GenericObjectManager gom;
    List<GenericObject> starCadGOs;

    @Override
    public void execute() {
        initialize();
        
        try {
            createGenericObjects();
            starCadGOs = getGenericObjects();
            printGenericObjects();
        } catch (Exception ex) {
            print(ex);
        }
        
        
    }
    
    private void printGenericObjects() {
        for (GenericObject go : starCadGOs) {
            ClientServerObject obj = getCSObject(go);
            
            if (obj == null) {
                throw new IllegalStateException("Generic Object \"" + go.getPresentationName() + "\" does not contain a valid reference to a client server object.");
            }
            
            sim.println(go.toString());
            sim.println(obj.toString());
        }
    }
    
    private <T extends ClientServerObject> T getCSObject(GenericObject go) {
        ClientServerObjectKey csoKey = go.getGenericProperties().getObjectKey(goLabel, sim.getObjectRegistry());
        
        if (csoKey == null) {
            return null;
        } else {
            return go.getGenericProperties().getObject(goLabel, sim.getObjectRegistry());
        }
    }
    
    private void createGenericObjects() {
        StarCadDocumentManager manager = sim.get(StarCadDocumentManager.class);
        
        if (manager.getObjects().isEmpty()) {
            throw new IllegalStateException("The simulation contains not Cad Client documents.");
        }
        
        for (StarCadDocument doc : manager.getObjects()) {
            for (StarCadDesignParameter dp : doc.getStarCadDesignParameters().getObjects()) {
                StarCadDesignParameterDouble dpd;
                
                if (dp instanceof StarCadDesignParameterDouble) {
                    dpd = (StarCadDesignParameterDouble) dp;
                    if (!hasGenericObject(dpd)) {
                        createGenericObject(dpd);
                    }
                }
            }
        }
        
    }
    
    private List<GenericObject> getGenericObjects() {
        List<GenericObject> list = new ArrayList<>();
        
        for (GenericObject go : gom.getObjects()) {
            ClientServerObjectKey csoKey = go.getGenericProperties().getObjectKey(goLabel, sim.getObjectRegistry());
            
            if (csoKey != null) {
                list.add(go);
            }
        }
        
        return list;
    }
    
    private void createGenericObject(StarCadDesignParameterDouble dp) {
        if (hasGenericObject(dp)) {
            return;
        }
        
        NeoProperty props = new NeoProperty();
        props.put(goLabel, dp);
        
        gom.createGenericObject(dp.getPresentationName(), props);
        
    }
    
    private boolean hasGenericObject(StarCadDesignParameterDouble dp) {
        for (GenericObject go : gom.getObjects()) {
            ClientServerObjectKey csoKey = go.getGenericProperties().getObjectKey(goLabel, sim.getObjectRegistry());
            
            if (csoKey != null) {
                if (dp.equals(go.getGenericProperties().getObject(goLabel, sim.getObjectRegistry()))) {
                    return true;
                }
            }
        }
        
        return false;
    }
    
    private void initialize() {
        sim = getActiveSimulation();
        NeoProperty args = new NeoProperty();
        args.put("BaseName", "StarGeneric");
        sim.execute("LoadLibrary", args); // NOI18N  
        gom = GenericObjectManager.get(sim);
    }
    
    private void print(Exception ex) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        ex.printStackTrace(pw);
        sim.println(sw.toString());
    }
    
    
}
