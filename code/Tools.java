package de.denniswittich.hex3;

import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;

/**
 * Created by Dennis Wittich on 16.02.2017.
 */

public class Tools {

    private static final float sin60 = (float)Math.sin(Math.PI/3.);
    private static final float cos60 = (float)Math.cos(Math.PI/3.);
    public static final float sin45 = (float)Math.sin(Math.PI/4.);

    public static Rect getOptimalTextHeight(String text,float maxWidth,float maxHeight, Paint paint){
        float height = maxHeight;
        Rect bounds = new Rect();
        paint.setTextSize(height);
        paint.getTextBounds(text,0,text.length(),bounds);
        while (bounds.width()>maxWidth || bounds.height()>maxHeight){
            height -= 1.0f;
            paint.setTextSize(height);
            paint.getTextBounds(text,0,text.length(),bounds);
        }
        return bounds;
    }

    public static void setHexPath(Path path, float cx, float cy, float radius){
        float ty = cy - sin60*radius;
        float by = cy + sin60*radius;
        float ilx = cx - cos60*radius;
        float irx = cx + cos60*radius;

        path.reset();
        path.moveTo(ilx,by);
        path.lineTo(cx-radius,cy);
        path.lineTo(ilx,ty);
        path.lineTo(irx,ty);
        path.lineTo(cx+radius,cy);
        path.lineTo(irx,by);
        path.close();
    }

    public static void setHexSharpPath(Path path, float cx, float cy, float radius){
        float ity = cy - cos60*radius;
        float iby = cy + cos60*radius;
        float lx = cx - sin60*radius;
        float rx = cx + sin60*radius;

        path.reset();
        path.moveTo(lx,iby);
        path.lineTo(lx,ity);
        path.lineTo(cx,cy-radius);
        path.lineTo(rx,ity);
        path.lineTo(rx,iby);
        path.lineTo(cx,cy+radius);
        path.close();
    }
}
