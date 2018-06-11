package ybalcanci.rotatingcalculator;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;

public class CirclePlayer implements GameObject {
    private int x;
    private int y;
    private int radius;
    private String color;
    private Paint paint;

    public CirclePlayer(int x, int y, int radius, String color) {
        this.x = x;
        this.y = y;
        this.radius = radius;
        this.color = color;
    }


    @Override
    public void draw(Canvas canvas) {
        paint = new Paint();
        paint.setColor(Color.parseColor(color));
        paint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(x, y, radius, paint);
    }

    @Override
    public void update(Point point) {
        x = point.x;
        y = point.y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getRadius() {
        return radius;
    }
}
