package de.denniswittich.hex3;

import android.content.res.Resources;

/**
 * Created by Dennis Wittich on 23.02.2017.
 */

class S {
    public static String title;


    public static void loadStrings(Resources r) {
        title = r.getString(R.string.title);
    }
}