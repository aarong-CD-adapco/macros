/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package optimate;

import core.apiFramework.OptimateObject;
import core.apiFramework.properties.OptimateObjectKey;
import core.apiFramework.properties.PropertyKey;
import core.apiFramework.properties.StringArrayKey;
import core.apiFramework.properties.StringKey;
import java.util.Map;

/**
 *
 * @author aarong
 */
public class TestUnmappedOptimateObject extends OptimateObject {
    
    StringKey unMappedKey = new StringKey("Unmapped object property key", true);
    
    public TestUnmappedOptimateObject() {
        super();
        set(unMappedKey, "unmapped property");
    }
    
    public void set(String s) {
        set(unMappedKey, s);
    }
    
    @Override
    public String toString() {
        String toReturn = "TestUnmappedOptimateObject";
        toReturn += has(OptimateObject.NAME) ? " " + getName() + ":\n" : ":\n";
        toReturn += "\tProperties:\n";
        for (Map.Entry<PropertyKey, Object> entry : _propertyMap._properties.entrySet()) {
            if (entry.getKey() instanceof StringArrayKey) {
                toReturn += "\t\tString Array:\n";
                for (String s : (String[]) entry.getValue()) {
                    toReturn += "\t\t\t" + s + "\n";
                }
            } else if (entry.getKey() instanceof OptimateObjectKey) {
                toReturn += ((OptimateObject) entry.getValue()).toString();
            } else {
                toReturn += "\t\t" + entry.getKey() + ", " + entry.getValue() + "\n";
            }
        }
        
        return toReturn;
    }    
}
