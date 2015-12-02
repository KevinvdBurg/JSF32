/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package calculate;

import java.util.List;

/**
 *
 * @author <a href="mailto:kevin.vanderburg@student.fontys.nl"> Kevin van der Burg </a>
 */
public class KochData implements java.io.Serializable{
   
    private List<Edge> edges;
    private KochFractal fractal;
    
    public KochData(List<Edge> edges, KochFractal fractal)
    {
        this.edges = edges;
        this.fractal = fractal;
    }
    
    public List<Edge> getEdges()
    {
        return edges;
    }

    public KochFractal getFractal()
    {
        return fractal;
    }

    
}
