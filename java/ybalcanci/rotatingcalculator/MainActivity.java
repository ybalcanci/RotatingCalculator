package ybalcanci.rotatingcalculator;

import android.app.Activity;
import android.graphics.Point;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Display;
import android.view.Window;
import android.view.WindowManager;

import ybalcanci.rotatingcalculator.R;

public class MainActivity extends Activity {

    Display display;
    Point size;
    GamePanel gamePanel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        display = getWindowManager().getDefaultDisplay();
        size = new Point();
        display.getSize(size);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        gamePanel = new GamePanel(this, size);
        setContentView(gamePanel);
    }

    @Override
    protected void onStart() {
        super.onStart();
        System.out.println("ONSTART IS CALLED IN MAIN ACTIVITY @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
    }
    @Override
    protected void onPause() {
        super.onPause();
        gamePanel.thread.setPaused(true);
        System.out.println("ONPAUSE IS CALLED IN MAIN ACTIVITY @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
    }
    @Override
    protected void onResume() {
        super.onResume();
        gamePanel.thread.setPaused(false);
        System.out.println("ONRESUME IS CALLED IN MAIN ACTIVITY @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
    }
}
