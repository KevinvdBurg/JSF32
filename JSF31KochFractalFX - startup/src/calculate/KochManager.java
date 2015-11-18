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
import javafx.application.Platform;
import javafx.concurrent.Task;

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
            application.clearKochPanel();
            
            this.tempEdgeList = new ArrayList<Edge>();
            
            this.kochFractal.setLevel(nxt);
            
            timeStamp = new TimeStamp();
            timeStamp.setBegin("Begin berekenen van edges.");

            
            Task leftTask = new Task<Void>()
            {
                @Override 
                public Void call() {
                    kochFractal.generateLeftEdge();
                    return null;
                }
            };
            new Thread(leftTask).start();
            
            Task bottomTask = new Task<Void>()
            {
                @Override 
                public Void call() {
                    kochFractal.generateBottomEdge();
                    return null;
                }
            };
            new Thread(bottomTask).start();
            
            Task rightTask = new Task<Void>()
            {
                @Override 
                public Void call() {
                    kochFractal.generateRightEdge();
                    return null;
                }
            };
            new Thread(rightTask).start();
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

        TimeStamp timeStamp = new TimeStamp();
        timeStamp.setBegin("Begin met tekenen.");

        for(Edge edge : tempEdgeList)
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
            final Edge edge = (Edge) arg;
            
            tempEdgeList.add(edge);
            
            Platform.runLater(new Runnable(){
                @Override
                public void run() {
                   application.drawEdge(edge);
                }
            });
            
        }
    }
}
