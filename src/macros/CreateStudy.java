/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package macros;

import core.PostProcess;
import core.Project;
import core.SolverProcess;
import core.apiFramework.LicenseManager;
import core.apiFramework.OptimateProject;
import core.apiFramework.OptimateStudy;
import core.listeners.ProcessStreamListener;
import core.listeners.SolverListener;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
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
public class CreateStudy extends StarMacro implements ProcessStreamListener, SolverListener {

    Simulation _sim;

    @Override
    public void execute() {
        _sim = getActiveSimulation();

        OptimateProject proj = OptimateProject.newInstance(_sim);
        OptimateStudy study = proj.getActiveStudy();

        study.setStudyType(OptimateStudy.StudyType.DESIGN_SWEEP);
        study.setSaveMode(Project.HeedsSaveMode.NONE);
        study.getAnalysis().setNumJobs(3);
        study.getSimulationSettings().setNumberOfCPUsPerDesign(1);
        study.getSimulationSettings().clearSolution(true);
        study.getLicenseManager().remove(LicenseManager.CoreLicenseType.POWERTOKEN);
        study.getLicenseManager().add(LicenseManager.CoreLicenseType.SUITE, 1);
        study.getLicenseManager().add(LicenseManager.ParallelLicenseType.HPC, 1);

        AbstractVariable depth;
        depth = study.getVariableManager().createChild(getDepth());
        depth.setOptimizationMinimum(0.05);
        depth.setOptimizationMaximum(0.1);
        depth.setResolution(3);
        depth.setBaseline(0.05);

        AbstractVariable thickness = study.getVariableManager().createChild(getThickness());
        thickness.setOptimizationMinimum(0.005);
        thickness.setOptimizationMaximum(0.01);
        thickness.setResolution(3);
        thickness.setBaseline(0.005);

        Response dp = study.getResponseManager().createChild(getPressureDrop());

        for (Scene sc : _sim.getSceneManager().getScenes()) {
            study.getModelViewManager().createChild(sc);
        }

        for (StarPlot sp : _sim.getPlotManager().getPlots()) {
            study.getModelViewManager().createChild(sp);
        }

        try {
            proj.saveAs(new File(_sim.getSessionDir() + File.separator + "sweep.optm"));
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
