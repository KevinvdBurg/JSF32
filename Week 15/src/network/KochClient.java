/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package network;

import calculate.Edge;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import static java.lang.Thread.sleep;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.application.Platform;
import jsf31kochfractalfx.JSF31KochFractalFX;
import network.Network.RequestAllEdges;
import network.Network.RequestAllEdgesSeparate;

/**
 * 
 * @author Martin Drost <info@martindrost.nl>
 */
public class KochClient {
    private Socket socket;
    private OutputStream outStream;
    private InputStream inStream;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private JSF31KochFractalFX application;
    
    public KochClient(JSF31KochFractalFX application)
    {   
        try {
            this.application = application;
            this.socket = new Socket(Network.ip, Network.port);
            this.outStream = socket.getOutputStream();
            this.inStream = socket.getInputStream();

            // Let op: volgorde is van belang!
            this.out = new ObjectOutputStream(outStream);
            this.in = new ObjectInputStream(inStream);
        } catch (IOException ex) {
            Logger.getLogger(KochClient.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    public List<Edge> requestAllEdges(int level)
    {
        List<Edge> edges = new ArrayList<Edge>();
        
        try
        {

            RequestAllEdges edgeRequest = new RequestAllEdges(level);
            
            out.writeObject(edgeRequest);
            out.flush();
            
            boolean done = false;
            while(!done)
            {
                Object input = in.readObject();
                if(input instanceof Edge)
                {
                    edges.add((Edge)input);
                }
                else if(input instanceof String)
                {
                    String string = (String) input;
                    if(string.toLowerCase().equals("end"))
                        done = true;
                }
            }
        }
        catch (IOException e)
        {  
           e.printStackTrace();
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(KochClient.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return edges;
    }
    
    public void requestAllEdgesSeperate(final int level)
    {
        final List<Edge> edges = new ArrayList<Edge>();
        Runnable socketThread = new Runnable()
        {
            @Override
            public void run()
            {
                try
                {

                    application.edges = new ArrayList<>();
                    application.clearKochPanel();
                    RequestAllEdgesSeparate edgeRequest = new RequestAllEdgesSeparate(level);

                    out.writeObject(edgeRequest);
                    out.flush();

                    boolean done = false;
                    while(!done)
                    {
                        Object input = in.readObject();
                        if(input instanceof Edge)
                        {
                            final Edge edge = (Edge)input;
                            application.edges.add(edge);
                            application.drawEdge(edge);
                            sleep(1);
                        }
                        else if(input instanceof String)
                        {
                            String string = (String) input;
                            if(string.toLowerCase().equals("end"))
                                done = true;
                        }
                    }
                }
                catch (IOException e)
                {  
                   e.printStackTrace();
                } catch (ClassNotFoundException ex) {
                    Logger.getLogger(KochClient.class.getName()).log(Level.SEVERE, null, ex);
                } catch (InterruptedException ex) {
                    Logger.getLogger(KochClient.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        };
        Thread thread = new Thread(socketThread);
        thread.start();
    }
    
    public void closeConnection()
    {
        try {
            this.socket.close();
        } catch (IOException ex) {
            Logger.getLogger(KochClient.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
