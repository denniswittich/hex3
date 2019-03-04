package de.denniswittich.hex3;

/**
 * Created by Dennis Wittich on 07.03.2017.
 */

public class Lerpers {

    public static float Smooth(float startValue, float targetValue, float pct)
    {
        return startValue + (targetValue - startValue) * (0.5f * (float)Math.sin(Math.PI * (pct - 0.5f)) + 0.5f);
    }

    public static float SmoothBump(float min, float max, float pct)
    {
        return min + (max - min) * (0.5f * (float)Math.sin(2f * Math.PI * (pct - 0.25f)) + 0.5f);
    }

    public static float Linear(float startValue, float targetValue, float pct)
    {
        return startValue + (targetValue - startValue) * pct;
    }

    public static float Bump(float minValue, float maxValue, float pct)
    {
        return minValue + (maxValue - minValue) * (-4f * (pct - 0.5f) * (pct - 0.5f) + 1f);
    }

    public static float SlowStart(float startValue, float targetValue, float pct)
    {
        return startValue + (targetValue - startValue) * pct * pct;
    }

    public static float SlowEnd(float startValue, float targetValue, float pct)
    {
        return startValue + (targetValue - startValue) * (2 * pct - pct * pct);
    }
    public static float OvershootEnd(float startValue, float targetValue, float pct)
    {
        return startValue + (targetValue - startValue) * (0.5f - 2f * (pct - 0.5f) * (pct - 0.5f) + (2 * pct - pct * pct));
    }


}
