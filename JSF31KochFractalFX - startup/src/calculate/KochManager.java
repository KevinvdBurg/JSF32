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
    
    private Task<Void> rightTask = null;
    private Task<Void> leftTask = null;
    private Task<Void> bottomTask = null;
    private Task<Void> drawTask = null;
    
    private Thread tRight = null;
    private Thread tLeft = null;
    private Thread tBottom = null;
    private Thread tDraw = null;
    
    
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
            timeStamp.setBegin("Start berekenen");
            
            leftTask = new Task<Void>()
            {
                @Override 
                public Void call() throws InterruptedException {
                    kochFractal.generateLeftEdge();
                    
                    updateProgress(0, kochFractal.getNrOfEdges());
                    updateMessage(kochFractal.getNrLeft());
                    return null;
                }
            };
            
            
            
            bottomTask = new Task<Void>()
            {
                @Override 
                public Void call() throws InterruptedException {
                    kochFractal.generateBottomEdge();
                    
                    updateProgress(0, kochFractal.getNrOfEdges());
                    updateMessage(kochFractal.getNrBottom());
                    return null;
                }
            };
            
            rightTask = new Task<Void>()
            {
                @Override 
                public Void call() throws InterruptedException {
                    kochFractal.generateRightEdge();
                    
                    updateProgress(0, kochFractal.getNrOfEdges());
                    updateMessage(kochFractal.getNrRight());
                    return null;
                }
            };
            
            drawTask = new Task<Void>()
            {
                @Override 
                public Void call() throws InterruptedException, ExecutionException {
                    rightTask.get();
                    leftTask.get();
                    bottomTask.get();
                    
                    edgeList = tempEdgeList;
                    
                    timeStamp.setEnd("Stop berekenen");
                    application.setTextCalc(timeStamp.toString());
                    application.setTextNrEdges(edgeList.size()+"");
                    
                    application.requestDrawEdges();
                    
                    return null;
                }
            };

            application.ProgressBottomBar.progressProperty().bind(bottomTask.progressProperty());
            application.ProgressRightBar.progressProperty().bind(rightTask.progressProperty());
            application.ProgressLeftBar.progressProperty().bind(leftTask.progressProperty());
            application.getlabelCountLeft().textProperty().bind(leftTask.messageProperty());
            application.getlabelCountBottom().textProperty().bind(bottomTask.messageProperty());
            application.getlabelCountRight().textProperty().bind(rightTask.messageProperty());
            
            tLeft = new Thread(leftTask);
            tBottom = new Thread(bottomTask);
            tRight = new Thread(rightTask);
            tDraw = new Thread(drawTask);
            
            tLeft.start();
            tBottom.start();
            tRight.start();
            tDraw.start();
            
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
        timeStamp.setBegin("Start tekenen");

        for(Edge edge : edgeList)
        {
            this.application.drawEdge(edge);
        }
        
        timeStamp.setEnd("Stop tekenen");
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
