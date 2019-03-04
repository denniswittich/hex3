package de.denniswittich.hex3;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import de.denniswittich.hex3.Interfaces.Irenderable;

/**
 * Created by Dennis Wittich on 14.03.2017.
 */

public class Title  implements Irenderable {

    private final Paint paint = new Paint();
    private final float cy;

    public Title(){
        ColorInflator.hcPaints.add(paint);
        paint.setColor(Color.DKGRAY);
        paint.setTextSize(Dimensions.blockSize*2f);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTypeface(TheFont.typo);
        cy = (Dimensions.screenHeight-Dimensions.screenWidth)/4f + Dimensions.blockSize;
    }

    @Override
    public void render(Canvas canvas) {
        canvas.drawText(S.title,Dimensions.screenHalfWidth,cy,paint);
    }
}
