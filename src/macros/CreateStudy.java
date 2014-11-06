/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package macros;

import core.OptimateProperties;
import core.Project;
import parameters.Objective;
import parameters.Response;
import parameters.var.DesignParameterVariable;
import star.base.report.Report;
import star.cadmodeler.CadModel;
import star.cadmodeler.DesignParameter;
import star.cadmodeler.SolidModelManager;
import star.common.Simulation;
import star.common.StarMacro;

/**
 *
 * @author aarong
 */
public class CreateStudy extends StarMacro {

    @Override
    public void execute() {
        Simulation sim = getActiveSimulation();
        
        OptimateProperties op = new OptimateProperties();
        Project project = new Project(sim, op, Project.UserMode.NORMAL);
        
        project.setRunMode(Project.RunMode.Optimization);
        project.setOptMode(Project.OptMode.Single);
        project.setRuns(50);
        project.setJobs(2);
        project.setCPUs(4);
        project.clear = true;
        
        CadModel cadModel = (CadModel) sim.get(SolidModelManager.class).getObject("3D-CAD Model 1");
        DesignParameter dp = cadModel.getDesignParameterManager().getObject("thickness");
        
        DesignParameterVariable var1 = project.getVariableManager().createChild(dp);
        var1.setOptimizationMinimum(10.0);
        var1.setOptimizationMaximum(15.0);
        var1.setBaseline(12.0);
        var1.setIncrement(0.01);
        
        Report r = sim.getReportManager().getReport("Pressure Drop");
        Response res = project.getResponseManager().createChild(r);
        res.setLowerErrorBound(0.0);
        
        Objective o = project.getObjectiveManager().createChild(res.getKey());
        o.setGoal(Objective.ObjectiveGoal.MINIMIZE);
        o.setUseBaselineForNorm(true);
        
        res.addObjective(o);
        
        project.buildProject();
    }
    
}
