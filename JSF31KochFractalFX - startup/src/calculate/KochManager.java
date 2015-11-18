/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package calculate;

import java.util.Observable;
import java.util.Observer;
import jsf31kochfractalfx.JSF31KochFractalFX;
import java.util.List;
import java.util.ArrayList;

/**
 *
 * @author Martin
 */
public class KochManager {
    
    private JSF31KochFractalFX application;
    private List<Edge> edgeList; 
    private List<Edge> tempEdgeList; 
    private KochFractal kochFractal;
    TimeStamp timeStamp;
    
    public KochManager(JSF31KochFractalFX application)
    {
        this.edgeList = new ArrayList<Edge>();
        this.tempEdgeList = new ArrayList<Edge>();
        this.application = application; //Add application
        
        this.kochFractal = new KochFractal();
        this.kochFractal.setLevel(1);
        
        KochFractalObserver kochFractalObserver = new KochFractalObserver();//Create observer
        this.kochFractal.addObserver(kochFractalObserver);//Add observer
    }
    
    public void changeLevel(final int nxt)
    {
        try
        {
            this.tempEdgeList = new ArrayList<Edge>();
            
            this.kochFractal.setLevel(nxt);
            
            timeStamp = new TimeStamp();
            timeStamp.setBegin("Begin berekenen van edges.");


            final  Thread leftThread = new Thread()
            {
                @Override
                public synchronized void run() {
                   kochFractal.generateLeftEdge();
                   notify();
                }
            };
            leftThread.start();
            
            final Thread bottomThread = new Thread()
            {
                @Override
                public synchronized void run() {
                    kochFractal.generateBottomEdge();
                    notify();
                }
            };
            bottomThread.start();

            final Thread rightThread = new Thread()
            {
                @Override
                public synchronized void run() {
                    kochFractal.generateRightEdge();
                    notify();
                }
            };
            rightThread.start();
            
            
            final Thread drawThread = new Thread()
            {
                @Override
                public void run() 
                {
                    try
                    {
                        leftThread.join();
                        bottomThread.join();
                        rightThread.join();
                        timeStamp.setEnd("Edges berekend!");
                        application.setTextCalc(timeStamp.toString());

                        edgeList = tempEdgeList;
                        application.requestDrawEdges();

                        application.setTextNrEdges(edgeList.size()+"");
                    }
                    catch(Exception e)
                    {

                    }
                }
            };
            drawThread.start();
        }
        catch(Exception e)
        {
            
        }

    }
    
    public KochFractal getNewKochFractal(int level)
    {
        
        return kochFractal;
        
    }
    
    public void drawEdges()
    { 
        this.application.clearKochPanel();
        // Opdracht 8 Verplaats naar Change Level
/*        this.kochFractal.generateLeftEdge();
        this.kochFractal.generateBottomEdge();
        this.kochFractal.generateRightEdge();*/

        TimeStamp timeStamp = new TimeStamp();
        timeStamp.setBegin("Begin met tekenen.");

        for(Edge edge : edgeList)
        {
            this.application.drawEdge(edge);
        }
        
        timeStamp.setEnd("Klaar met tekenen!");
        this.application.setTextDraw(timeStamp.toString());
    }
    
    public class KochFractalObserver implements Observer
    {
        @Override
        public synchronized void update(Observable o, Object arg)
        {
            Edge edge = (Edge) arg;
            
            tempEdgeList.add(edge);

            System.out.println("");
            System.out.println("Edge " + tempEdgeList.size() + " coordinates:");
            System.out.println("Start: " + edge.X2 + ", " + edge.Y2);
            System.out.println("End: " + edge.X1 + ", " + edge.Y1);
        }
    }
}
