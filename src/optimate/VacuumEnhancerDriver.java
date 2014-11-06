/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package optimate;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map.Entry;
import star.base.report.AreaAverageReport;
import star.base.report.ExpressionReport;
import star.base.report.Report;
import star.base.report.ReportManager;
import star.common.Boundary;
import star.common.ConstantScalarProfileMethod;
import star.common.PrimitiveFieldFunction;
import star.common.Region;
import star.common.Simulation;
import star.common.StagnationBoundary;
import star.common.StarMacro;
import star.common.StepStoppingCriterion;
import star.common.WallBoundary;
import star.flow.MassFlowReport;
import star.flow.StaticPressureProfile;
import star.flow.TotalPressureProfile;

/**
 *
 * @author aarong
 */
public class VacuumEnhancerDriver extends StarMacro {

    //***********************CUSTOM VARIABLES TO BE EDITED********************************
    /**
     * This array contains double values for each engine pressure operating
     * condition to be investigated.
     */
    private final double[] enginePressures = new double[]{-19000.0, -20000.0, -21000.0};
    /**
     * This array contains double values for the positive pressure difference
     * between the closed port simulation and open port simulations.
     */
    private final double[] port_2Pressures = new double[]{1000.0, 2000.0, 3000.0};
    /**
     * The number of iterations for each operating condition;
     */
    private final int iterations = 700;
    /**
     * This is the common prefix for all reports that export the pressure on the
     * port in the closed position.
     */
    private final String closedPortPrefix = "Port2Pressure_";
    /**
     * this is the common prefix for all reports that export the mass flow
     * through the port in the opened position.
     */
    private final String openedPortPrefix = "EngineMassFlow_";
    /**
     * The name of the port boundary in the simulation file.
     */
    private final String portBoundaryName = "Secondary_Port";
    /**
     * The name of the engine intake boundary in the simulation file.
     */
    private final String engineBoundaryName = "Outlet";
    //**************************DO NOT EDIT BELOW HERE*************************************

    private final HashMap<String, ExpressionReport> reportMap = new HashMap<String, ExpressionReport>();
    private Simulation sim;
    private Boundary port;
    private Boundary engine;

    @Override
    public void execute() {
        boolean cont;
        sim = getActiveSimulation();

        cont = validateReports();

        if (cont) {
            cont = validateBoundaries();
        }

        if (cont) {
            evaluateDesigns();
            exportResults();
        }
    }

    /**
     * Ensures that the simulation contains a report for each design point of
     * interest and that the naming convention is respected. Specifically there
     * must be nxm + n expression reports (where n is the number of engine
     * pressure conditions and m is the number of port pressure conditions) that
     * follow the naming convention.
     *
     * The naming convention is: closedPortPrefix_0 - closedPortPrefix_n-1 and
     * openPortPrefix_0_0 - openPortPrefix_n-1_m-1
     *
     * @return True is reports can be validated otherwise false;
     */
    private boolean validateReports() {
        ReportManager man = sim.getReportManager();

        for (int i = 0; i < enginePressures.length; i++) {
            try {

                String name = closedPortPrefix + i;
                Report report = man.getReport(name);
                if (report instanceof ExpressionReport) {
                    reportMap.put(name, (ExpressionReport) report);
                } else {
                    sim.println("Error: All reports must be expression reports.");
                    return false;
                }

                for (int j = 0; j < port_2Pressures.length; j++) {

                    String name2 = openedPortPrefix + i + "_" + j;
                    Report report2 = man.getReport(name2);
                    if (report2 instanceof ExpressionReport) {
                        reportMap.put(name2, (ExpressionReport) report2);
                    } else {
                        sim.println("Error: All reports must be expression reports.");
                        return false;
                    }

                }
            } catch (Exception ex) {
                sim.println("Error: The sim does not contain a valid set of existing reports for this study.");
                return false;
            }

        }
        return true;
    }

    /**
     * Ensures that the port and engine boundaries can be retrieved by the
     * specified port and engine boundary names. It assumes the simulation
     * contains only one region.
     *
     * @return True if boundaries are retrieved otherwise false.
     */
    private boolean validateBoundaries() {
        Collection<Region> regs = sim.getRegionManager().getRegions();
        try {
            Region r = regs.toArray(new Region[regs.size()])[0];
            port = r.getBoundaryManager().getBoundary(portBoundaryName);
            engine = r.getBoundaryManager().getBoundary(engineBoundaryName);
            return true;
        } catch (Exception ex) {
            sim.println("Error: The sim file doesn't contain a boundary " + portBoundaryName + "\n or does not contain a boundary " + engineBoundaryName + "!");
        }
        return false;
    }

    /**
     * Systematically evaluate each closed and open operating condition and
     * updates the applicable report with the appropriate values.
     */
    private void evaluateDesigns() {
        int i = 0;
        double closedPressure;

        for (double d : enginePressures) {
            closeBoundary(d);
            closedPressure = evaluateClosedCondition(i);
            int j = 0;
            for (double d2 : port_2Pressures) {
                openBoundary(closedPressure + d2);
                evaluateOpenCondition(i, j);
                j++;
            }

            i++;
        }
    }

    private void closeBoundary(double d) {
        port.setBoundaryType(WallBoundary.class);
        StaticPressureProfile prof = engine.getValues().get(StaticPressureProfile.class);
        prof.getMethod(ConstantScalarProfileMethod.class).getQuantity().setValue(d);
    }

    private void openBoundary(double d) {
        port.setBoundaryType(StagnationBoundary.class);
        TotalPressureProfile prof = port.getValues().get(TotalPressureProfile.class);
        prof.getMethod(ConstantScalarProfileMethod.class).getQuantity().setValue(d);
    }

    private double evaluateClosedCondition(int enginePressureIndex) {
        StepStoppingCriterion stop = (StepStoppingCriterion) sim.getSolverStoppingCriterionManager().getSolverStoppingCriterion("Maximum Steps");
        int currSteps = stop.getMaximumNumberSteps();
        stop.setMaximumNumberSteps(currSteps + iterations);
        sim.println("Evaluating closed condition " + enginePressureIndex + ".");
        sim.getSimulationIterator().run();
        String reportName = closedPortPrefix + enginePressureIndex;
        ExpressionReport r = reportMap.get(reportName);
        double val = evaluateAreaReport(port, (PrimitiveFieldFunction) sim.getFieldFunctionManager().getFunction("Pressure"));
        r.setDefinition(Double.toString(val));
        return val;
    }

    private double evaluateOpenCondition(int enginePressureIndex, int portPressureIndex) {
        StepStoppingCriterion stop = (StepStoppingCriterion) sim.getSolverStoppingCriterionManager().getSolverStoppingCriterion("Maximum Steps");
        int currSteps = stop.getMaximumNumberSteps();
        stop.setMaximumNumberSteps(currSteps + iterations);
        sim.println("Evaluating open condition " + enginePressureIndex + " " + portPressureIndex + ".");
        sim.getSimulationIterator().run();
        String reportName = openedPortPrefix + enginePressureIndex + "_" + portPressureIndex;
        ExpressionReport r = reportMap.get(reportName);
        double val = evaluateMassFlowReport(engine);
        r.setDefinition(Double.toString(val));
        return val;
    }

    private double evaluateAreaReport(Boundary b, PrimitiveFieldFunction function) {
        AreaAverageReport rep = sim.getReportManager().createReport(AreaAverageReport.class);
        rep.setScalar(function);
        rep.getParts().setObjects(b);
        double d = rep.getReportMonitorValue();
        sim.getReportManager().removeObjects(rep);
        return d;
    }

    private double evaluateMassFlowReport(Boundary b) {
        MassFlowReport rep = sim.getReportManager().createReport(MassFlowReport.class);
        rep.getParts().setObjects(b);
        double d = rep.getReportMonitorValue();
        sim.getReportManager().removeObjects(rep);
        return d;
    }

    private void exportResults() {
        File f = new File(sim.getSessionDir() + File.separator + "results");
        BufferedWriter out = null;

        try {
            out = new BufferedWriter(new PrintWriter(f));

            for (Entry<String, ExpressionReport> entry : reportMap.entrySet()) {
                out.write(entry.getKey() + ", " + entry.getValue().getReportMonitorValue() + "\n");
            }
            out.flush();
            out.close();
        } catch (FileNotFoundException ex) {
            sim.println("Error writing results file.");
        } catch (IOException ex) {
            sim.println("Error writing results file.");
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException ex) {
                }
            }
        }
    }
}
