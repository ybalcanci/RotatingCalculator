package ybalcanci.rotatingcalculator;

import android.graphics.Canvas;
import android.view.SurfaceHolder;

public class MainThread extends Thread {
    public static final int MAX_FPS = 30;
    private double averageFPS;
    private boolean paused;
    private SurfaceHolder surfaceHolder;
    private GamePanel gamePanel;
    private boolean running;
    public static Canvas canvas;

    public void setRunning(boolean running){
        this.running = running;
    }

    public boolean isPaused() {
        return paused;
    }

    public void setPaused(boolean paused) {
        this.paused = paused;
    }

    public boolean isRunning(){
        return this.running;
    }

    public MainThread(SurfaceHolder surfaceHolder, GamePanel gamePanel){
        super();
        this.paused = false;
        this.running = false;
        this.surfaceHolder = surfaceHolder;
        this.gamePanel = gamePanel;
    }

    @Override
    public void run(){
        long startTime;
        long timeMillis = 1000/MAX_FPS;
        long waitTime;
        int frameCount = 0;
        long totalTime = 0;
        long targetTime = 1000/MAX_FPS;

        while(running){
            if(!this.paused) {
                startTime = System.nanoTime();
                canvas = null;

                try {
                    canvas = this.surfaceHolder.lockCanvas();
                    synchronized (surfaceHolder) {
                        this.gamePanel.update();
                        this.gamePanel.draw(canvas);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    try {
                        surfaceHolder.unlockCanvasAndPost(canvas);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
                timeMillis = (System.nanoTime() - startTime) / 1000000;
                waitTime = targetTime - timeMillis;
                try {
                    if (waitTime > 0)
                        this.sleep(waitTime);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                totalTime += System.nanoTime() - startTime;
                frameCount++;
                if (frameCount == MAX_FPS) {
                    averageFPS = 1000 / ((totalTime / frameCount) / 1000000);
                    frameCount = 0;
                    totalTime = 0;
                    System.out.println(averageFPS);
                }
            }
        }
    }
}
