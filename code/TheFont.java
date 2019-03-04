package de.denniswittich.hex3;

import android.content.res.AssetManager;
import android.graphics.Typeface;

/**
 * Created by Dennis Wittich on 16.02.2017.
 */

public class TheFont {
    public static Typeface typo;

    public static void loadFont(AssetManager assetManager){
        setupPaints(assetManager);
    }

    private static void setupPaints(AssetManager assetManager){
        typo = Typeface.createFromAsset(assetManager, "fonts/mvboli.ttf");
    }

}
