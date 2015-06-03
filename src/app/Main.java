/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package app;

import java.io.PrintWriter;
import java.io.StringWriter;
import optimate.TestOptimateObject;

/**
 *
 * @author aarong
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        System.out.println("Testing while loop");

        boolean b = true;
        int count = 1;

        while (b && count <= 25) {
            try {
                System.out.println("Loop " + count);
                if (count > 20) {
                    throw new Exception();
                }
                count++;
            } catch (Exception ex) {
                b = false;
            }
        }
    }

    static void printStackTrace(Exception ex) {
        StringWriter sw = new StringWriter();
        ex.printStackTrace(new PrintWriter(sw));
        System.out.println(sw.toString());
    }
}
