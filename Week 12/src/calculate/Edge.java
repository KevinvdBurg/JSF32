/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package calculate;

import javafx.scene.paint.Color;

/**
 *
 * @author Kevin van der Burg
 */
public class Edge implements java.io.Serializable{
    public double X1, Y1, X2, Y2;
    
    public double hue;
    public double saturation;
    public double brightness;
    
    public Edge(double X1, double Y1, double X2, double Y2, double hue, double saturation, double brightness) {
        this.X1 = X1;
        this.Y1 = Y1;
        this.X2 = X2;
        this.Y2 = Y2;
        this.hue = hue;
        this.saturation = saturation;
        this.brightness = brightness;
    }

    public Color getColor(){
        return Color.hsb(hue, saturation, brightness);
    }

    @Override
    public String toString() {
        return "Edge{" + "X1=" + X1 + ", Y1=" + Y1 + ", X2=" + X2 + ", Y2=" + Y2 + ", hue=" + hue + ", saturation=" + saturation + ", brightness=" + brightness + '}';
    }
        
        
}
