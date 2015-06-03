/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package optimate;

import core.apiFramework.OptimateObject;
import core.apiFramework.properties.BooleanKey;
import core.apiFramework.properties.CharKey;
import core.apiFramework.properties.DoubleKey;
import core.apiFramework.properties.EnumKey;
import core.apiFramework.properties.IntegerKey;
import core.apiFramework.properties.OptimateObjectKey;
import core.apiFramework.properties.PropertyKey;
import core.apiFramework.properties.StringArrayKey;
import core.apiFramework.properties.StringKey;
//import core.apiFramework.properties.UnmappedOptimateObject;
import java.util.Map;

/**
 *
 * @author aarong
 */
public class TestOptimateObject extends OptimateObject {
    
    StringKey string = new StringKey("string", true);
    IntegerKey integer = new IntegerKey("integer", true);
    DoubleKey doubleVal = new DoubleKey("double", true);
    CharKey character = new CharKey("char", true);
    BooleanKey bool = new BooleanKey("boolean", true);
    EnumKey enumeration = new EnumKey("enum", true);
    OptimateObjectKey oObject = new OptimateObjectKey("Optimate Object", true);

//    @UnmappedOptimateObject
    TestUnmappedOptimateObject nonMappedObject = new TestUnmappedOptimateObject();
    
    
    public TestOptimateObject() {
        super();
        set(string, "string1");
        set(integer, 0);
        set(doubleVal, 0.0);
        set(character, 'c');
        set(bool, true);
        set(enumeration, Type.ONE);
    }
    
    public void set(String s) {
        set(string, s);
    }
    public void set(int i) {
        set(integer, i);
    }
    public void set(double d) {
        set(doubleVal, d);
    }
    public void set(char c) {
        set(character, c);
    }
    public void set(boolean b) {
        set(bool, b);
    }
    public void set(Type type) {
        set(enumeration, type);
    }
//    public void set(TestOptimateObject oo) {
//        set(oObject, oo);
//    }
//    
//    public TestOptimateObject get() {
//        return get(oObject);
//    }
    
    public TestUnmappedOptimateObject getUnmapped() {
        return nonMappedObject;
    }
    
    @Override
    public String toString() {
        String toReturn = "TestOptimateObject";
        toReturn += has(OptimateObject.NAME) ? " " + getName() + ":\n" : ":\n";
        toReturn += "\tProperties:\n";
        for (Map.Entry<PropertyKey, Object> entry : _propertyMap._properties.entrySet()) {
            if (entry.getKey() instanceof StringArrayKey) {
                toReturn += "\t\tString Array:\n";
                for (String s : (String[]) entry.getValue()) {
                    toReturn += "\t\t\t" + s + "\n";
                }
            } else if (entry.getKey() instanceof OptimateObjectKey) {
//                toReturn += ((OptimateObject) entry.getValue()).toString();
            } else {
                toReturn += "\t\t" + entry.getKey() + ", " + entry.getValue() + "\n";
            }
        }
        
//        for (OptimateObject oo : getChildObjects()) {
//            toReturn += oo.toString();
//        }
        
        return toReturn;
    }    
    
    public enum Type {
        ONE, TWO;
    }
    
    public static void test() {
        TestOptimateObject obj1 = new TestOptimateObject();
        obj1.setName("Obj 1");
//        TestOptimateObject innerObj = new TestOptimateObject();
//        innerObj.setName("inner object");
//        obj1.set(innerObj);
        
        System.out.println(obj1.toString());
        
//        String[] a1 = new String[]{"string element 1", "string element 2"};
//        String[] a2 = new String[]{"string element 3", "string element 4"};
        
//        TestOptimateObject obj2 = obj1.copy();
//        obj2.setName("Obj 2");
//        
//        System.out.println();
//        System.out.println();
//        
//        System.out.println(obj2.toString());
//        
//        obj2.set("string2");
//        obj2.set(1);
//        obj2.set('d');
//        obj2.set(2.0);
//        obj2.set(false);
//        obj2.set(TestOptimateObject.Type.TWO);
//        
//        TestUnmappedOptimateObject obj4 = obj2.getUnmapped();
//        obj4.set("changed property");
        
//        TestOptimateObject obj3 = obj2.get();
//        obj3.set("string3");
//        obj3.set(2);
//        obj3.set('e');
//        obj3.set(3.0);
//        obj3.set(false);
//        obj3.set(TestOptimateObject.Type.TWO);
        
        System.out.println();
        System.out.println();
        System.out.println();
        System.out.println();
        
//        System.out.println(obj1.toString());
//        System.out.println(obj2.toString());
    }
}
