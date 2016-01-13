/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jna;
import com.sun.jna.*;

/**
 *
 * @author Martin Drost <info@martindrost.nl>
 */
public class Jna {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        ILibrary lib = (ILibrary) Native.loadLibrary("kernel32", ILibrary.class);
        SYSTEMTIME sysTime = new SYSTEMTIME(); 
        
        long startTime;
        startTime = System.nanoTime();
        for(int i = 0; i < 1000000; i++)
            lib.GetSystemTime(sysTime);
        System.out.println("GetSystemTime: " + ((System.nanoTime() - startTime)/1000000) + "ms");
        
        startTime = System.nanoTime();
        for(int i = 0; i < 1000000; i++)
            System.nanoTime();
        System.out.println("nanoTime: " + ((System.nanoTime() - startTime)/1000000) + "ms");
        
    }
    
}
