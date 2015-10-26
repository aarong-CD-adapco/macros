/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package macros;

import java.lang.reflect.InvocationTargetException;
import javax.swing.SwingUtilities;
import star.common.Simulation;
import star.common.StarMacro;

/**
 *
 * @author aarong
 */
public class TestMacro extends StarMacro {

    Simulation _sim;

    @Override
    public void execute() {
        _sim = getActiveSimulation();

        try {

            doAsyncTaskAsSync();

            complete();
        } catch (InterruptedException | InvocationTargetException ex) {

        }
    }

    private void doAsyncTaskAsSync() throws InterruptedException, InvocationTargetException {
        SwingUtilities.invokeAndWait(new Runnable() {

            @Override
            public void run() {
                doAsyncTask();
            }
        });
    }

    private void doAsyncTask() {
        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                int count = 0;

                while (count <= 10000) {
                    _sim.println(count);
                    count++;
                }
            }
        });

        thread.start();
    }

    private void complete() {
        _sim.println("**********************macro finished**********************");
    }

}
