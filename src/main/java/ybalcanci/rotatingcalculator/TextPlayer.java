package ybalcanci.rotatingcalculator;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;

public class TextPlayer implements GameObject {
    String text;
    private int x;
    private int y;
    private int color;
    private Paint paint;
    float textSize;

    public TextPlayer(String text, int x, int y, int color) {
        this.text = text;
        this.x = x;
        this.y = y;
        this.color = color;
        textSize = 48;
    }

    public void setTextSize(float textSize) {
        this.textSize = textSize;
    }

    @Override
    public void draw(Canvas canvas) {
        paint = new Paint();
        paint.setColor(color);
        paint.setTextSize(textSize);
        canvas.drawText(text, x - 11, y + 10, paint);
    }


    public void update(String text) {
        this.text = text;
    }

    @Override
    public void update(Point point) {
        x = point.x;
        y = point.y;
    }

    public String getText() {
        return text;
    }
}
