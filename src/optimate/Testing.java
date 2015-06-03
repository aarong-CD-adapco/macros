/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package optimate;

import core.OptimateFile;
import core.apiFramework.OptimateProject;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.StringWriter;
import star.common.Simulation;
import star.common.StarMacro;

/**
 *
 * @author aarong
 */
public class Testing extends StarMacro {
    
    Simulation _sim;

    @Override
    public void execute() {
        _sim = getActiveSimulation();
        write();
        try {               
            read();
        } catch (FileNotFoundException ex) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            ex.printStackTrace(pw);
            _sim.println(sw.toString());
        }
    }
    
    private void write() {
//        OptimateProject proj = OptimateProject.newInstance(_sim);
//        _sim.println(proj.getActiveStudy().getAnalysis().getCheckFreq());
//        proj.getActiveStudy().getAnalysis().setCheckFreq(15);
//        _sim.println(proj.getActiveStudy().getAnalysis().getCheckFreq());
//        File f = new File(_sim.getSessionDir() + File.separator + "test.optm");
//        proj.saveAs(f);
    }
    
    private void read() throws FileNotFoundException {
//        OptimateFile file = OptimateFile.newInstance(new File(_sim.getSessionDir() + File.separator + "test.optm"));
//        OptimateProject proj = OptimateProject.newInstance(_sim, file);
//        _sim.println(proj.getActiveStudy().getAnalysis().getCheckFreq());
    }
}
