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

public class BackGroundBubbles implements Iupdateable, Irenderable {

    private final int nrOfBubbles = 10;
    private final int nrOfValues = 5 * nrOfBubbles;
    private final float[] bubbles = new float[nrOfValues]; // x,y,r,vx,vy
    private final float minR;
    private final float maxR;

    private final Paint paintBubble = new Paint();
    private final Random random = new Random();

    public BackGroundBubbles() {
        ColorInflator.lcPaints.add(paintBubble);
        minR = Dimensions.blockSize / 2f;
        maxR = Dimensions.blockSize * 1.5f;

        paintBubble.setColor(Color.LTGRAY);
        for (int i = 0; i < nrOfValues; i += 5) {
            bubbles[i] = -2 * maxR;
            bubbles[i + 1] = -2 * maxR;
        }
    }

    @Override
    public void update(float deltaTime) {
        deltaTime /= 6f;
        for (int i = 0; i < nrOfValues; i += 5) {
            if (bubbles[i] < -maxR || bubbles[i] > Dimensions.screenWidth + maxR ||
                    bubbles[i + 1] < -maxR || bubbles[i + 1] > Dimensions.screenHeight + maxR) {
                int side = random.nextInt(4);
                switch (side) {
                    case 0: // from left
                        bubbles[i] = -maxR;
                        bubbles[i + 1] = Dimensions.screenHeight * random.nextFloat();
                        bubbles[i + 3] = Dimensions.screenWidth / (1 + random.nextFloat());
                        bubbles[i + 4] = Dimensions.screenHeight * (0.5f - random.nextFloat());
                        break;
                    case 1: // from right
                        bubbles[i] = Dimensions.screenWidth + maxR;
                        bubbles[i + 1] = Dimensions.screenHeight * random.nextFloat();
                        bubbles[i + 3] = -Dimensions.screenWidth / (1 + random.nextFloat());
                        bubbles[i + 4] = Dimensions.screenHeight * (0.5f - random.nextFloat());
                        break;
                    case 2: // from top
                        bubbles[i] = Dimensions.screenWidth * random.nextFloat();
                        bubbles[i + 1] = -maxR;
                        bubbles[i + 3] = Dimensions.screenWidth * (0.5f - random.nextFloat());
                        bubbles[i + 4] = Dimensions.screenHeight / (1 + random.nextFloat());
                        break;
                    case 3: // from bottom
                        bubbles[i] = Dimensions.screenWidth * random.nextFloat();
                        bubbles[i + 1] = Dimensions.screenHeight + maxR;
                        bubbles[i + 3] = Dimensions.screenWidth * (0.5f - random.nextFloat());
                        bubbles[i + 4] = -Dimensions.screenHeight / (1 + random.nextFloat());
                        break;
                }
                bubbles[i + 2] = minR + (maxR - minR) * random.nextFloat();
            }
            bubbles[i] += bubbles[i + 3] * deltaTime;
            bubbles[i + 1] += bubbles[i + 4] * deltaTime;
        }
    }

    @Override
    public void render(Canvas canvas) {
        for (int i = 0; i < nrOfValues; i += 5) {
            canvas.drawCircle(bubbles[i], bubbles[i + 1], bubbles[i + 2], paintBubble);
        }
    }
}
