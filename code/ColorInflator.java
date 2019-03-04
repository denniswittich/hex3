package de.denniswittich.hex3;

import android.graphics.Color;
import android.graphics.Paint;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by Dennis Wittich on 14.03.2017.
 */

public class ColorInflator {
    public static final Collection<Paint> vhcPaints = new ArrayList<>();
    public static final Collection<Paint> hcPaints = new ArrayList<>();
    public static final Collection<Paint> lcPaints = new ArrayList<>();

    public static void onBgChange(boolean black) {
        for (Paint p : hcPaints) {
            p.setColor(black ? Color.LTGRAY : Color.DKGRAY);
        }
        for (Paint p : lcPaints) {
            p.setColor(black ? Color.DKGRAY : Color.LTGRAY);
        }
        for (Paint p : vhcPaints) {
            p.setColor(black ? Color.WHITE : Color.BLACK);
        }
    }
}
