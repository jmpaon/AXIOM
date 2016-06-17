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
    
    interface PlotPrinter {
        String repl(char c, int times);
    }
    
    public String plot(int height, int width) {
        if(height < 5) throw new IllegalArgumentException("height must be at least 5");
        if(width  < 5) throw new IllegalArgumentException("width must be at least 5");
        char[][] plot = new char[height][width];
        
        PlotPrinter p = new PlotPrinter() {
            
            @Override
            public String repl(char c, int times) {
                String s="";
                for(int i=0; i<times; i++) {
                    s += c;
                }
                return s;
            } 

        };
        
        for(Point point : points) {
            System.out.printf("x:%s y:%s %n", point.x, point.y);
            System.out.printf("max-x:%s x:%s %n", scale(max_x(), point.x, height), scale(max_y(), point.y, width));
            write(plot, scale(max_x(), point.x, height), scale(max_y(), point.y, width), point.label);
        }
        
        String plotPrint = String.format("%s", p.repl('#', width+2));
        for(char[] row : plot) {
            plotPrint += String.format("#%s#", new String(row));
        }
        plotPrint += String.format("%s", p.repl('#', width+2));
        
        return plotPrint;
        
    }
    
    private void write(char[][] table, int x, int y, String text) {
        if(x < 0 || x >= table.length) throw new IndexOutOfBoundsException("x value out of bounds");
        if(y < 0 || y >= table[x].length) throw new IndexOutOfBoundsException("x value out of bounds");
        table[x][y] = text.charAt(0);
        if(y==table[x].length-1) return;
        if(text.length()>1) write(table, x, y+1, text.substring(1));
    }
    
    
    private int scale(double max, double val, int scale) {
        return (int) Math.round(val/max * scale) ;
    }
    
    private int scale(double max, double min, double val, int scale) {
        return (int) Math.round((val - min) / (max - min) * scale);
    }
    
    private Point scale(Point point, char[][] plot) {
        
    }
    
    public void addPoint(double x, double y, String label) {
        if (labelExists(label)) throw new IllegalStateException(
                String.format("Label '%s' already exists in plot %s", label, this.hashCode()));
        this.points.add(new Point(x, y, label));
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
}
