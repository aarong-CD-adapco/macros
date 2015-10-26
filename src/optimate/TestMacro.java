/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package optimate;

import star.common.Simulation;
import star.common.StarMacro;

/**
 *
 * @author aarong
 */
public class TestMacro extends StarMacro {

    @Override
    public void execute() {
        Simulation sim = getActiveSimulation();
    }
    
}
