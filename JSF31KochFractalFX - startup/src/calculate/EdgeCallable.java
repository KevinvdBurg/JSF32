/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package calculate;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.Callable;
import java.util.concurrent.CyclicBarrier;

/**
 *
 * @author Martijn
 */
public abstract class EdgeCallable implements Callable<List<Edge>>, Observer {

    KochFractal own;
    CyclicBarrier barrier;
    List<Edge> edges;
    
    public EdgeCallable(int level, CyclicBarrier barrier) {
        own = new KochFractal();
        own.setLevel(level);
        own.addObserver(this);
        
        edges = new ArrayList<>();
        
        this.barrier = barrier;
    }
    
    @Override
    public List<Edge> call() throws Exception {
        generate();
         
        barrier.await();
        
        return edges;
    }

    @Override
    public void update(Observable o, Object arg) {
        edges.add((Edge)arg);
    }
    
    public abstract void generate();
}
