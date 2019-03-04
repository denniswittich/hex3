package de.denniswittich.hex3.Backgrounds;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import java.util.Random;

import de.denniswittich.hex3.ColorInflator;
import de.denniswittich.hex3.Dimensions;
import de.denniswittich.hex3.Interfaces.Irenderable;
import de.denniswittich.hex3.Interfaces.Iupdateable;

/**
 * Created by Dennis Wittich on 14.03.2017.
 */

public class BackGroundSpace implements Iupdateable, Irenderable {

    private final int nrOfBubbles = 40;
    private final int nrOfValues = 5 * nrOfBubbles;
    private final float[] bubbles = new float[nrOfValues]; // x,y,r,vx,vy
    private final float maxR;

    private final Paint paintBubble = new Paint();
    private final Random random = new Random();

    public BackGroundSpace() {
        ColorInflator.lcPaints.add(paintBubble);
        maxR = Dimensions.blockSize / 8f;

        paintBubble.setColor(Color.LTGRAY);
        for (int i = 0; i < nrOfValues; i += 5) {
            bubbles[i] = -2 * maxR;
            bubbles[i + 1] = -2 * maxR;
        }
    }

    @Override
    public void update(float deltaTime) {
        deltaTime /= 1f;
        for (int i = 0; i < nrOfValues; i += 5) {
            if (bubbles[i] < -maxR || bubbles[i] > Dimensions.screenWidth + maxR ||
                    bubbles[i + 1] < -maxR || bubbles[i + 1] > Dimensions.screenHeight + maxR) {
                double angle = random.nextDouble() * Math.PI * 2.;
                bubbles[i] = Dimensions.screenHalfWidth;
                bubbles[i + 1] = Dimensions.screenHalfHeight;
                bubbles[i + 2] = 0;
                bubbles[i + 3] = (float) Math.sin(angle) * Dimensions.screenWidth;
                bubbles[i + 4] = (float) Math.cos(angle) * Dimensions.screenWidth;
            }
            bubbles[i] += bubbles[i + 3] * deltaTime;
            bubbles[i + 1] += bubbles[i + 4] * deltaTime;
            bubbles[i + 2] += deltaTime * maxR;
        }
    }

    @Override
    public void render(Canvas canvas) {
        for (int i = 0; i < nrOfValues; i += 5) {
            canvas.drawCircle(bubbles[i], bubbles[i + 1], bubbles[i + 2], paintBubble);
        }
    }
}
