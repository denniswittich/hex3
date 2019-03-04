package de.denniswittich.hex3.States;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.MotionEvent;

import java.util.ArrayList;

import de.denniswittich.hex3.ColorInflator;
import de.denniswittich.hex3.GameSettings;
import de.denniswittich.hex3.Sounds;
import de.denniswittich.hex3.TheFont;
import de.denniswittich.hex3.Dimensions;
import de.denniswittich.hex3.Interfaces.Irenderable;
import de.denniswittich.hex3.Interfaces.Itouchable;
import de.denniswittich.hex3.Interfaces.Iupdateable;
import de.denniswittich.hex3.Persistence;
import de.denniswittich.hex3.Title;

/**
 * Created by Dennis Wittich on 16.02.2017.
 */

public class Stats implements Iupdateable, Irenderable, Itouchable {

    private final ArrayList<Irenderable> renderables = new ArrayList<>();

    private boolean fadeoutTriggered;
    private StateMachine stateMachine;
    private final UnlockTable unlockTable;


    public Stats() {
        Title title = new Title();
        unlockTable = new UnlockTable();

        renderables.add(title);
        renderables.add(unlockTable);
    }

    public void link(StateMachine stateMachine) {
        this.stateMachine = stateMachine;
    }

    public void init() {
        fadeoutTriggered = false;
        unlockTable.updateEntries();
    }

    @Override
    public void render(Canvas canvas) {
        for (Irenderable ir : renderables) {
            ir.render(canvas);
        }
    }

    @Override
    public void update(float deltaTime) {
        unlockTable.update(deltaTime);

        if (fadeoutTriggered) {
            if (GameSettings.fadeOutTimer < 1.0f) {
                GameSettings.fadeOutTimer += deltaTime * 10f;
                if (GameSettings.fadeOutTimer >= 1.0f) {
                    GameSettings.fadeOutTimer = 1.0f;
                    stateMachine.setState(StateMachine.StateMenu);
                }
            }
        }
    }

    @Override
    public void onTouch(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            Sounds.playClick();
            fadeoutTriggered = true;
        }
    }

    public void onBackPressed() {
        fadeoutTriggered = true;
    }

    //-------------------------------------------------------------


    class UnlockTable implements Irenderable, Iupdateable {

        final String[] entriesl = new String[]{"Skill", "Best Skill", "Highscore", "Combo", "Games", "Hexes", "4-Hexes", "Z-Hexes", "5-Hexes", "V-Hexes", "U-Hexes", "T-Hexes", "M-Hexes", "Y-Hexes", "X-Hexes"};
        final String[] entriesr = new String[]{"", "", "", "", "", "", "", "", "", "", "", "", "", "", ""};

        final Paint paintl = new Paint();
        final Paint paintr = new Paint();
        final float cy;
        float highlightPos = 0f;
        final float[] highLightHsv = new float[3];


        UnlockTable() {
            highLightHsv[0] = 120f;
            highLightHsv[1] = 0.8f;
            highLightHsv[2] = 1f;
            paintl.setColor(Color.DKGRAY);
            paintl.setTextSize(Dimensions.blockSize * 0.6f);
            paintl.setTextAlign(Paint.Align.RIGHT);
            paintl.setTypeface(TheFont.typo);
            ColorInflator.hcPaints.add(paintl);

            paintr.setColor(Color.GRAY);
            paintr.setTextSize(Dimensions.blockSize * 0.6f);
            paintr.setTextAlign(Paint.Align.LEFT);
            paintr.setTypeface(TheFont.typo);

            cy = Dimensions.screenHalfHeight - Dimensions.blockSize * 0.5f;
        }

        void updateEntries() {
            entriesr[0] = " " + Persistence.skill;
            entriesr[1] = " " + Persistence.highskill;
            entriesr[2] = " " + Persistence.highScore;
            entriesr[3] = " " + Persistence.highestCombo;
            entriesr[4] = " " + Persistence.gamesCounter;
            entriesr[5] = " " + Persistence.hexCounter;
            entriesr[6] = " " + Persistence.shape4counter;
            entriesr[7] = " " + Persistence.shapeZcounter;
            entriesr[8] = " " + Persistence.shape5counter;
            entriesr[9] = " " + Persistence.shapeVcounter;
            entriesr[10] = " " + Persistence.shapeUcounter;
            entriesr[11] = " " + Persistence.shapeTcounter;
            entriesr[12] = " " + Persistence.shapeMcounter;
            entriesr[13] = " " + Persistence.shapeYcounter;
            entriesr[14] = " " + Persistence.shapeXcounter;
        }

        @Override
        public void render(Canvas canvas) {
            boolean highlight;
            for (int i = 0; i < 15; i++) {
                highlight = i == (int) highlightPos;
                if (highlight) {
                    paintl.setColor(Color.HSVToColor(highLightHsv));
                }
                canvas.drawText(entriesl[i], Dimensions.screenHalfWidth, cy + (i - 4) * Dimensions.blockSize * 0.7f, paintl);
                canvas.drawText(entriesr[i], Dimensions.screenHalfWidth, cy + (i - 4) * Dimensions.blockSize * 0.7f, paintr);
                if (highlight) {
                    paintl.setColor(Persistence.darkTheme ? Color.LTGRAY : Color.DKGRAY);
                }
            }
        }

        @Override
        public void update(float deltaTime) {
            highlightPos += deltaTime * 5f;
            highlightPos %= 26;
            highLightHsv[0] += deltaTime * 60f;
            highLightHsv[0] %= 360f;
        }
    }
}