/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package optimate;

import parameters.var.user.CustomScalarVariable;
import star.base.neo.ClientServerObject;
import star.segregatedflow.PressureSolver;

/**
 *
 * @author aarong
 */
public class PressureURFVariable extends CustomScalarVariable {

    public PressureURFVariable(PressureSolver cso, String key) {
        super(cso, key);
    }
    
    public PressureURFVariable() {
        super();
    }
    
    @Override
    public String exportAPIToUpdate(String doubleName, String objName) {
        return objName + ".setUrf(" + doubleName + ")";
    }  
    
    @Override
    protected Class[] getStarClass() {
        return new Class[] {PressureSolver.class};
    }

    @Override
    protected double getCurrentValueInSimulation(ClientServerObject cso) {
        return ((PressureSolver) cso).getUrf();
    }  
}