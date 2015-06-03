/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package macros;

import star.common.Simulation;
import star.common.StarMacro;

/**
 *
 * @author aarong
 */
public class SceneResolutionTest extends StarMacro {

    Simulation sim;
    String sceneName;
    
    @Override
    public void execute() {
        sim = getActiveSimulation();
    }
    
}
