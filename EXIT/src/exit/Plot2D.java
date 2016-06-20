/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package exit;

import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author jmpaon
 */
public class Plot2D {
    
    private List<Point> points;
    
    public Plot2D() {
        points = new LinkedList<>();
    }
    
    public void addPoint(double x, double y, String label) {
        if (labelExists(label)) throw new IllegalStateException(
                String.format("Label '%s' already exists in plot %s", label, this.hashCode()));
        this.points.add(new Point(x, y, label));
    }    
    
    public String plot(int height, int width) {
        if(height < 5) throw new IllegalArgumentException("height must be at least 5");
        if(width  < 5) throw new IllegalArgumentException("width must be at least 5");
        
        char[][] plot = new char[height][width];
        
        for(Point point : points) {

            
            int plotted_x = scale(max_x(), point.x, width);
            int plotted_y = scale(max_y(), point.y, height);
            System.out.printf("plotted x: %d, plotted y: %d %n", plotted_x, plotted_y);
            write(plot, plotted_x, plotted_y, point.label);
            
        }
        
        String plotPrint = String.format("%s%n", repl('#', width+2));
        for (int i = plot.length-1; i >= 0; i--) {
            char[] row = plot[i];
            plotPrint += String.format("#%s#%n", new String(row));
        }
        plotPrint += String.format("%s%n", repl('#', width+2));        
        return plotPrint;
    }
    
    private void write(char[][] table, int x, int y, String text) {
        if(y < 1 || y > table.length) throw new IndexOutOfBoundsException(String.format("y value (%d) out of bounds (%d)", y, table.length  ));
        if(x < 1 || x > table[y-1].length) throw new IndexOutOfBoundsException(String.format("x value (%d) out of bounds (%d)", x, table[y-1].length ));
        
        table[y-1][x-1] = text.charAt(0);
        if(x >=  table[y-1].length-1) return;
        if(text.length()>1) write(table, x+1, y, text.substring(1));
    }
    
    
    private int scale(double max, double val, int scale) {
        return (int) (val/max * scale) ;
    }
    
    private int scale(double max, double min, double val, int scale) {
        return (int) ((val - min) / (max - min) * scale);
    }
    

    

    
    double max_x() {
        double max=points.get(0).x;
        for(Point point : points) { max = point.x > max ? point.x : max; }
        return max;
    }
    
    double max_y() {
        double max=points.get(0).y;
        for(Point point : points) { max = point.y > max ? point.y : max; }
        return max;        
    }

    double min_x() {
        double min=points.get(0).x;
        for(Point point : points) { min = point.x < min ? point.x : min; }
        return min;        
    }

    double min_y() {
        double min=points.get(0).y;
        for(Point point : points) { min = point.y < min ? point.y : min; }
        return min;        
    }        

    
    private boolean labelExists(String label) {
        for(Point point : points) {
            if(point.label.equals(label)) return true;
        }
        return false;
    }
    
    public class Point {
        
        public final double x;
        public final double y;
        public final String label;
        
        public Point(double x, double y, String label) {
            this.x = x;
            this.y = y;
            this.label = label;
        }
    }
    
    private String repl(char c, int times) {
        String s="";
        for(int i=0; i<times; i++) {
            s += c;
        }
        return s;
    }     
    
    
}
