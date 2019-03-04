package de.denniswittich.hex3;

/**
 * Created by Dennis Wittich on 16.02.2017.
 */

public class Dimensions {
    public static float screenHeight;
    public static float screenWidth;

    public static float screenHalfHeight;
    public static float screenHalfWidth;

    public static float blockMargin;
    public static float blockSize;

    public static void setup(){
        float screenHeightToScreenWidth = screenHeight / screenWidth;
        screenHalfHeight = screenHeight/2f;
        screenHalfWidth = screenWidth/2f;

        float topBottomHeight = (screenHeight - screenWidth) / 2f;
        float topCenterY = topBottomHeight / 2f;
        float bottomCenterY = screenHeight - topBottomHeight / 2f;

        float levelIconMargin = screenWidth / 20f;
        float levelIconSize = (screenWidth - (6f * levelIconMargin)) / 7f;

        blockMargin = screenWidth/50f;
        blockSize = (screenWidth - (9f*blockMargin))/8f;
    }
}
