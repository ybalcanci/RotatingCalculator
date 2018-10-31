package ybalcanci.rotatingcalculator;

import android.graphics.Canvas;
import android.graphics.Point;

public interface GameObject {
    void draw(Canvas canvas);
    void update(Point point);
}
