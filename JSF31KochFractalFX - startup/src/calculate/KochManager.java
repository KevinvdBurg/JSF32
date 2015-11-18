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
import java.util.concurrent.ExecutionException;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.paint.Color;

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

            
            final Task leftTask = new Task<Void>()
            {
                @Override 
                public Void call() throws InterruptedException {
                    kochFractal.generateLeftEdge();
                    return null;
                }
            };
            application.ProgressLeftBar.progressProperty().bind(leftTask.progressProperty());
            new Thread(leftTask).start();
            
            final Task bottomTask = new Task<Void>()
            {
                @Override 
                public Void call() throws InterruptedException {
                    kochFractal.generateBottomEdge();
                    return null;
                }
            };
            application.ProgressBottomBar.progressProperty().bind(bottomTask.progressProperty());
            new Thread(bottomTask).start();
            
            final Task rightTask = new Task<Void>()
            {
                @Override 
                public Void call() throws InterruptedException {
                    kochFractal.generateRightEdge();
                    return null;
                }
            };
            application.ProgressRightBar.progressProperty().bind(rightTask.progressProperty());
            new Thread(rightTask).start();
            
            
            Task drawTask = new Task<Void>()
            {
                @Override 
                public Void call() throws InterruptedException, ExecutionException {
                    rightTask.get();
                    leftTask.get();
                    bottomTask.get();
                    
                    edgeList = tempEdgeList;
                    application.requestDrawEdges();
                    
                    return null;
                }
            };
            new Thread(drawTask).start();
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
        application.clearKochPanel();
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
            final Edge edge = (Edge) arg;
            
            tempEdgeList.add(edge);
            
            Platform.runLater(new Runnable(){
                @Override
                public void run() {
                   Edge e = new Edge(edge.X1,edge.Y1,edge.X2,edge.Y2, Color.WHITE);
                   application.drawEdge(e);
                }
            });
            
        }
    }
}
