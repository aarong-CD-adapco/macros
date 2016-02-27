/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package macros;

import core.PostProcess;
import core.Project;
import core.SolverProcess;
import core.StarDriverInsertedMacro;
import core.apiFramework.DesignManager;
import core.apiFramework.InsertedMacro;
import core.apiFramework.InsertedMacroManager;
import core.apiFramework.LicenseManager;
import core.apiFramework.OptimateProject;
import core.apiFramework.OptimateStudy;
import core.listeners.ProcessStreamListener;
import core.listeners.SolverListener;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import parameters.Design;
import parameters.DesignSet;
import parameters.HEEDSInFile;
import parameters.Response;
import parameters.var.AbstractVariable;
import star.base.report.Report;
import star.cadmodeler.CadModel;
import star.cadmodeler.DesignParameter;
import star.cadmodeler.SolidModelManager;
import star.common.Simulation;
import star.common.StarMacro;
import star.common.StarPlot;
import star.vis.Scene;
import workers.RunManager;

/**
 *
 * @author aarong
 */
public class CreateStudyWDesignSet extends StarMacro implements ProcessStreamListener, SolverListener {

    Simulation _sim;

    @Override
    public void execute() {
        _sim = getActiveSimulation();

        OptimateProject proj = OptimateProject.newInstance(_sim);
        OptimateStudy study = proj.getActiveStudy();

        study.setStudyType(OptimateStudy.StudyType.DESIGN_SWEEP);
        
        //Tell the study that a design set will be used to run the design sweep
        study.designSweepUsesDesignSet(true);
        
        study.setSaveMode(Project.HeedsSaveMode.NONE);
        study.getAnalysis().setNumJobs(3);
        study.getSimulationSettings().setNumberOfCPUsPerDesign(1);
        study.getSimulationSettings().clearSolution(true);
        study.getLicenseManager().remove(LicenseManager.CoreLicenseType.POWERTOKEN);
        study.getLicenseManager().add(LicenseManager.CoreLicenseType.SUITE, 1);
        study.getLicenseManager().add(LicenseManager.ParallelLicenseType.HPC, 1);

        AbstractVariable depth;
        depth = study.getVariableManager().createChild(getDepth());
        
        AbstractVariable thickness = study.getVariableManager().createChild(getThickness());
        
        //Create design set for the design sweep
        DesignSet designSet = study.getDesignSetManager().createChild();
        DesignManager designs = designSet.getDesignsManager();
        
        Design d1 = designs.createChild();
        d1.setValue(depth.getName(), 0.05);
        d1.setValue(thickness.getName(), 0.005);
        
        Design d2 = designs.createChild();
        d2.setValue(depth.getName(), 0.1);
        d2.setValue(thickness.getName(), 0.01);

        Design d3 = designs.createChild();
        d3.setValue(depth.getName(), 0.125);
        d3.setValue(thickness.getName(), 0.0125);
        
        //Set the design set for the study
        study.setDesignSetForDesignSweep(designSet);
        
        
        Response dp = study.getResponseManager().createChild(getPressureDrop());

        for (Scene sc : _sim.getSceneManager().getScenes()) {
            study.getModelViewManager().createChild(sc);
        }

        for (StarPlot sp : _sim.getPlotManager().getPlots()) {
            study.getModelViewManager().createChild(sp);
        }
        
        InsertedMacroManager manager = study.getInsertedMacroManager();
        InsertedMacro macro = manager.createChild(new File(_sim.getSessionDir() + File.separator + "Macro1.java"));
        manager.setMacroPlacement(macro, StarDriverInsertedMacro.DriverPlacement.BEFOREMESH);  //There are 3 different places during a study that a macro can be played.
        macro.getInputFile().setSource(HEEDSInFile.Source.STUDY);  //Tells heeds that the study directory will contain a file named Macro1.java and that this must be moved to each design directory.


        try {
            proj.saveAs(new File(_sim.getSessionDir() + File.separator + "sweepWDesignSet.optm"));
            proj.writeInputFiles(true);
            run(proj);
            openPost();
        } catch (Exception ex) {
            print(ex);
        }
    }

    private DesignParameter getDepth() {
        CadModel cad = (CadModel) _sim.get(SolidModelManager.class).getObject("3D-CAD Model 1");
        return (DesignParameter) cad.getDesignParameterManager().getObject("depth");
    }

    private DesignParameter getThickness() {
        CadModel cad = (CadModel) _sim.get(SolidModelManager.class).getObject("3D-CAD Model 1");
        return (DesignParameter) cad.getDesignParameterManager().getObject("thickness");
    }

    private Report getPressureDrop() {
        return _sim.getReportManager().getReport("InletPressure");
    }

    private void run(OptimateProject proj) throws IOException {
        _sim.saveState(_sim.getSessionDir() + File.separator + "pipeBlockage.sim");
        final SolverProcess solverProc = new SolverProcess(proj, false, SolverProcess.SolverMode.START);
        RunManager man = new RunManager(solverProc);
        man.addProcessStreamListener(this);
        man.addSolverListener(this);
        man.execute();
    }

    private void openPost() throws IOException {
        PostProcess postProc = new PostProcess(PostProcess.PostMode.OPEN_AND_LOAD, new File(_sim.getSessionDir() + File.separator + "sweep.optm"));
        postProc.start();
    }

    private void print(Exception ex) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        ex.printStackTrace(pw);
        _sim.println(sw.toString());
    }

    @Override
    public void streamUpdated(String string) {
        _sim.println(string);
    }

    @Override
    public void solverStarted(SolverProcess.SolverMode sm) {
        _sim.println("started");
    }

    @Override
    public void solverStopped() {
        _sim.println("stopped");
    }

    @Override
    public void solverPaused() {
        _sim.println("paused");
    }

    @Override
    public void solverResumed() {
        _sim.println("resumed");
    }
}
