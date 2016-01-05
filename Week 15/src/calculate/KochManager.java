/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package calculate;

import java.io.IOException;
import java.io.ObjectOutputStream;
import static java.lang.Thread.sleep;
import java.util.Observable;
import java.util.Observer;
import jsf31kochfractalfx.JSF31KochFractalFX;
import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.scene.paint.Color;

/**
 *
 * @author Martin
 */
public class KochManager {
    
    private List<Edge> edgeList; 
    private List<Edge> tempEdgeList; 
    private KochFractal kochFractal;
    private List<List<Edge>> cachedFractals;
    
    private Runnable rightTask = null;
    private Runnable leftTask = null;
    private Runnable bottomTask = null;
    
    private Thread tRight = null;
    private Thread tLeft = null;
    private Thread tBottom = null;
    
    private int count;
    
    private ObjectOutputStream out;
    public boolean sendSeparate;
    
    TimeStamp timeStamp;
    
    public KochManager(ObjectOutputStream out)
    {
        this.cachedFractals = new ArrayList<>();
        for(int i = 0; i < 12; i++)
        {
            this.cachedFractals.add(new ArrayList<Edge>());
        }
        this.sendSeparate = false;
        this.out = out;
        this.edgeList = new ArrayList<Edge>();
        this.tempEdgeList = new ArrayList<Edge>();
        
        this.kochFractal = new KochFractal();
        this.kochFractal.setLevel(1);
        
        KochFractalObserver kochFractalObserver = new KochFractalObserver();//Create observer
        this.kochFractal.addObserver(kochFractalObserver);//Add observer
    }
    
    public void changeLevel(final int nxt)
    {
        this.tempEdgeList = new ArrayList<Edge>();
        this.kochFractal.cancel();

        this.count = 0;
        this.kochFractal = new KochFractal();
        this.kochFractal.setLevel(nxt);
        
        KochFractalObserver kochFractalObserver = new KochFractalObserver();//Create observer
        this.kochFractal.addObserver(kochFractalObserver);//Add observer
        
        if(this.cachedFractals.get(nxt-1).size() > 0)
        {
            for(Edge edge : this.cachedFractals.get(nxt-1))
            {
                this.kochFractal.changed();
                this.kochFractal.notifyObservers(edge);
            }
            edgeList = tempEdgeList;
            return;
        }

        timeStamp = new TimeStamp();
        timeStamp.setBegin("Start berekenen");

        leftTask = new Runnable()
        {
            @Override
            public void run()
            {
                ObservableList edges = FXCollections.observableList(new ArrayList<Edge>());
                try {
                    kochFractal.generateLeftEdge(edges);
                } catch (InterruptedException ex) {
                    Logger.getLogger(KochManager.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        };

        bottomTask = new Runnable()
        {
            @Override
            public void run()
            {
                ObservableList edges = FXCollections.observableList(new ArrayList<Edge>());
                try {
                    kochFractal.generateBottomEdge(edges);
                } catch (InterruptedException ex) {
                    Logger.getLogger(KochManager.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        };

        rightTask = new Runnable()
        {
            @Override
            public void run()
            {
                ObservableList edges = FXCollections.observableList(new ArrayList<Edge>());
                try {
                    kochFractal.generateRightEdge(edges);
                } catch (InterruptedException ex) {
                    Logger.getLogger(KochManager.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        };

        tLeft = new Thread(leftTask);
        tBottom = new Thread(bottomTask);
        tRight = new Thread(rightTask);

        tLeft.start();
        tBottom.start();
        tRight.start();
        
        try {
            tLeft.join();
            tBottom.join();
            tRight.join();
            edgeList = tempEdgeList;
            this.cachedFractals.set(nxt - 1, edgeList);
        } catch (InterruptedException ex) {
            Logger.getLogger(KochManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void edgesCalculated()
    {
        count++;
        System.out.println("requestDraw:" + count);
        if(count != 3 || kochFractal.isCancelled())
            return;
        System.out.println("Draw");

        edgeList = tempEdgeList;

        timeStamp.setEnd("Stop berekenen");
    }
    
    public KochFractal getNewKochFractal(int level)
    {
        
        return kochFractal;
        
    }
    
    public class KochFractalObserver implements Observer
    {
        @Override
        public synchronized void update(Observable o, Object arg)
        {
            final Edge edge = (Edge) arg;
            tempEdgeList.add(edge);
            
            if(sendSeparate)
            {
                try {
                    out.writeObject(edge);
                } catch (IOException ex) {
                    Logger.getLogger(KochManager.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }
    
    public List<Edge> getEdges()
    {
        return this.edgeList;
    }
}