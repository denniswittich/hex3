package de.denniswittich.hex3.Backgrounds;

import android.graphics.Canvas;
import android.graphics.Color;

import de.denniswittich.hex3.Interfaces.Irenderable;
import de.denniswittich.hex3.Interfaces.Iupdateable;
import de.denniswittich.hex3.Persistence;

/**
 * Created by Dennis Wittich on 14.03.2017.
 */

public class BackGroundRainbow implements Iupdateable, Irenderable {

    private final float[] hsv = new float[3]; // x,y,r,vx,vy
    private float timer = 0f;
    private boolean isDarkTheme;

    public BackGroundRainbow() {
        isDarkTheme = Persistence.darkTheme;
        hsv[0] = 0f;
        if (!isDarkTheme) {
            hsv[1] = 0.2f;
            hsv[2] = 1f;
        } else {
            hsv[1] = 1f;
            hsv[2] = 0.2f;
        }
    }

    @Override
    public void update(float deltaTime) {
        if (isDarkTheme != Persistence.darkTheme) {
            isDarkTheme = Persistence.darkTheme;
            if (!isDarkTheme) {
                hsv[1] = 0.2f;
                hsv[2] = 1f;
            } else {
                hsv[1] = 1f;
                hsv[2] = 0.2f;
            }
        }
        timer += deltaTime / 15f;
        timer = timer % 1f;
        hsv[0] = (int) (timer * 360);
    }

    @Override
    public void render(Canvas canvas) {
        canvas.drawColor(Color.HSVToColor(hsv));
    }
}
