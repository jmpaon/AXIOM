/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package exit;

/**
 *
 * @author jmpaon
 */
public class Timer {
    
    private boolean running;
    private long startTime;
    
    public Timer() {
        startTime = System.currentTimeMillis();
        running = true;
    }
    
    public void lapTime(String msg) {
        if (!running) return;
        long nowTime = System.currentTimeMillis() - startTime;
        System.out.println(msg + " " + nowTime + " ms");
    }
    
    public void lapTime() {
        lapTime("Timed: ");
    }
    
    public void stopTime() {
        stopTime("Ended timing: ");
    }
    
    public void stopTime(String msg) {
        if (!running) return;
        long nowTime = System.currentTimeMillis() - startTime;
        System.out.println(msg + " " + nowTime + " ms");
        running = false;
    }
    
    public void startTime() {
        this.startTime = System.currentTimeMillis();
        this.running = true;
    }
    
}
