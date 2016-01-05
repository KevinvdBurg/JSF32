/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package network;

import java.io.Serializable;

/**
 * 
 * @author Martin Drost <info@martindrost.nl>
 */
public class Network {
    
    public static String ip = "localhost";
    public static int port = 8189;
    
    
    static class RequestAllEdgesSeparate implements Serializable
    {
        public int level;
        
        public RequestAllEdgesSeparate(int level)
        {
            this.level = level;
        }
    }
    
    static class RequestAllEdges implements Serializable
    {
        public int level;
        
        public RequestAllEdges(int level)
        {
            this.level = level;
        }
    }

}
