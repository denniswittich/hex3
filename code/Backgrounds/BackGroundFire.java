package de.denniswittich.hex3.Backgrounds;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import java.util.Random;

import de.denniswittich.hex3.Dimensions;
import de.denniswittich.hex3.Interfaces.Irenderable;
import de.denniswittich.hex3.Interfaces.Iupdateable;

/**
 * Created by Dennis Wittich on 14.03.2017.
 */

public class BackGroundFire implements Iupdateable, Irenderable {
    public static boolean FIRE;
    private final int nrOfBubbles = 25;
    private final int nrOfValues = 4 * nrOfBubbles;
    private final float[] particles = new float[nrOfValues]; // x,y,vx,vy
    private final float particleSize;

    private final Paint paint = new Paint();
    private final float[] hsv = new float[3];
    private float timer;
    private final Random random = new Random();

    public BackGroundFire() {
        hsv[0] = 120f;
        hsv[1] = 0.8f;
        hsv[2] = 1f;
        particleSize = Dimensions.blockSize / 5f;
    }

    @Override
    public void update(float deltaTime) {

        if (timer < 1f) {
            timer += deltaTime * 2f;
            paint.setAlpha((int) (255f * (1f - timer)));
            for (int i = 0; i < nrOfValues; i += 4) {
                particles[i] += particles[i + 2] * deltaTime * (0.8f + random.nextFloat() * 0.4f);
                particles[i + 1] += particles[i + 3] * deltaTime * (0.8f + random.nextFloat() * 0.4f);
            }

        }
        if (FIRE && timer >= 1f) {
            FIRE = false;
            float initialSpeed = Dimensions.blockSize * 6f * (1f + random.nextFloat());
            timer = 0f;
            hsv[0] = random.nextFloat() * 360;
            hsv[1] = 0.8f;
            hsv[2] = 1f;
            paint.setColor(Color.HSVToColor(hsv));

            float cx = Dimensions.screenWidth * 0.2f + random.nextFloat() * Dimensions.screenWidth * 0.6f;
            float cy = Dimensions.screenHalfHeight / 2f + random.nextFloat() * Dimensions.screenHalfHeight / 2f;

            for (int i = 0; i < nrOfValues; i += 4) {
                float angle = random.nextFloat() * (float) Math.PI * 2f;
                particles[i] = cx;
                particles[i + 1] = cy;
                particles[i + 2] = initialSpeed * (float) Math.sin(angle);
                particles[i + 3] = initialSpeed * (float) Math.cos(angle);
            }
        }
    }

    @Override
    public void render(Canvas canvas) {
        if (timer < 1f) {
            for (int i = 0; i < nrOfValues; i += 4) {
                canvas.drawCircle(particles[i], particles[i + 1], particleSize, paint);
            }
        }

    }
}
