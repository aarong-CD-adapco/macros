/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package macros;

import star.common.StarMacro;

/**
 *
 * @author aarong
 */
public class OptimateAfterMeshingTestMacro extends StarMacro {

    @Override
    public void execute() {
        getActiveSimulation().println("This macro should be run after meshing.");
    }
    
}
