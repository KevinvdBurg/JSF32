/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package network;

import calculate.Edge;
import calculate.KochManager;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import network.Network.RequestAllEdges;
import network.Network.RequestAllEdgesSeparate;

/**
 * 
 * @author Martin Drost <info@martindrost.nl>
 */
public class KochServer {
    
    public static void main(String[] args) {
        KochServer ks = new KochServer();
        
    }
    
    public KochServer()
    {
        
        try {
            // establish server socket
            ServerSocket s = new ServerSocket(Network.port);

            // wait for client connection
            while(!s.isClosed())
            {
                Socket incoming = s.accept();
                System.out.println("Connected");
                
                //Open a listener for the new socket
                openSocketListener(incoming);
                
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void openSocketListener(final Socket socket)
    {
        Runnable socketThread = new Runnable()
        {
            @Override
            public void run()
            {
    
                KochManager kochManager = new KochManager();
                
                try {
                    OutputStream outStream = socket.getOutputStream();
                    InputStream inStream = socket.getInputStream();

                    ObjectInputStream in = new ObjectInputStream(inStream);
                    ObjectOutputStream out = new ObjectOutputStream(outStream);

                    while(!socket.isClosed())
                    {
                        boolean done = false;
                        Object inObject = null;
                        while (!done) 
                        {
                            try {
                                inObject = in.readObject();

                                if(inObject instanceof RequestAllEdgesSeparate)
                                {
                                    RequestAllEdgesSeparate edgeRequest = (RequestAllEdgesSeparate)inObject;
                                    kochManager.changeLevel(edgeRequest.level);

                                }
                                else if(inObject instanceof RequestAllEdges)
                                {
                                    RequestAllEdges edgeRequest = (RequestAllEdges)inObject;
                                    kochManager.changeLevel(edgeRequest.level);

                                    for(Edge edge : kochManager.getEdges())
                                    {
                                        out.writeObject(edge);
                                        out.flush();
                                    }

                                    out.writeObject("end");
                                    out.flush();
                                }
                                else if(inObject instanceof String)
                                {
                                    String string = (String) inObject;
                                    if(string.toLowerCase().equals("end"))
                                        done = true;
                                }
                            } catch (ClassNotFoundException e) {
                                // TODO Auto-generated catch block
                                System.out.println("Object type not known");
                            }
                        }
                    }
                } catch (IOException ex) {
                    Logger.getLogger(KochServer.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        };
        new Thread(socketThread).start();
    }
}
