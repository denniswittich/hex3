package de.denniswittich.hex3;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;

/**
 * Created by Dennis Wittich on 23.02.2017.
 */

public class Sounds {


    private static int sp_click;
    private static int sp_rmv;
    private static int sp_gameend;
    private static int sp_line;
    private static int sp_samecolor;
    private static SoundPool sp;

    static void setup(Context c) {
        sp = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
        sp_click = sp.load(c, R.raw.shorthigh, 1);
        sp_rmv = sp.load(c, R.raw.bubblenew2, 1);
        sp_gameend = sp.load(c, R.raw.finish3, 1);
        sp_line = sp.load(c, R.raw.line, 1);
        sp_samecolor = sp.load(c, R.raw.samecolor, 1);
    }


    public static void playGameEnd() {
        if (!Persistence.soundOn) {
            return;
        }
        sp.play(sp_gameend, 1, 1, 1, 0, 1);
    }

    public static void playRemove(int power) {
        if (!Persistence.soundOn) {
            return;
        }
        if (power == 0) {
            sp.play(sp_rmv, 1, 1, 1, 0, 1);
        } else if (power == 1) {
            sp.play(sp_line, 1, 1, 1, 0, 1);
        } else if (power == 2) {
            sp.play(sp_samecolor, 1, 1, 1, 0, 1);

        }

    }

    public static void playClick() {
        if (!Persistence.soundOn) {
            return;
        }
        sp.play(sp_click, 1, 1, 1, 0, 1);
    }

    public static void vibrate() {
        if (Persistence.rumbleOn && MainActivity.vibrator != null && MainActivity.vibrator.hasVibrator()) {
            MainActivity.vibrator.vibrate(50);
        }
    }
}
