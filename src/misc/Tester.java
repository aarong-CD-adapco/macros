/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package misc;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author aarong
 */
public class Tester {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        List<String> lList = new ArrayList<String>();
        lList.add("my");
        lList.add("new");
        lList.add("list");
        
        String list = "my new list";
        
        String nnnList = list.replace(", ", " ").replace("  ", " ").trim();
        
//        String nList = list.replace(",", " ");
//        
//        String nnList = nList.replace("  ", " ");
//        
//        String nnnList = nnList.trim();
        
        String[] array = nnnList.split(" ");
        
        boolean[] found = new boolean[] {false, false, false};
        
        int idx = 0;
        for (String s : array) {
            if (s.equals(lList.get(idx))) {
                found[idx] = true;
            }
            idx++;
        }
        
        for (boolean b : found) {
            System.out.println(b);
        }
    }
    
}
