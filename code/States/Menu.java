package de.denniswittich.hex3.States;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
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
import de.denniswittich.hex3.MainActivity;
import de.denniswittich.hex3.Persistence;
import de.denniswittich.hex3.Title;
import de.denniswittich.hex3.Tools;

/**
 * Created by Dennis Wittich on 16.02.2017.
 */

public class Menu implements Iupdateable, Irenderable, Itouchable {

    private final ArrayList<Irenderable> renderables = new ArrayList<>();
    private final ArrayList<Itouchable> touchables = new ArrayList<>();
    private final ArrayList<Iupdateable> updatables = new ArrayList<>();

    private boolean fadeoutTriggered;
    private boolean statsTriggered;
    private boolean playTriggered;
    private boolean treasuresTriggered;

    private StateMachine stateMachine;

    public Menu() {
        Title title = new Title();
        Play play = new Play();
        Treasures treasures = new Treasures();
        Highscore highscore = new Highscore();
        Stats stats = new Stats();
        SoundOnOff soundOnOff = new SoundOnOff();
        RumbleOnOff rumbleOnOff = new RumbleOnOff();

        renderables.add(soundOnOff);
        if (MainActivity.vibrator.hasVibrator()) {
            renderables.add(rumbleOnOff);
        }
        renderables.add(title);
        renderables.add(play);
        renderables.add(treasures);
        renderables.add(stats);
        renderables.add(highscore);

        updatables.add(highscore);

        touchables.add(soundOnOff);
        if (MainActivity.vibrator.hasVibrator()) {
            touchables.add(rumbleOnOff);
        }
        touchables.add(play);
        touchables.add(stats);
        touchables.add(treasures);
    }

    public void link(StateMachine stateMachine, MainActivity mainActivity) {
        this.stateMachine = stateMachine;
    }

    public void init() {
        statsTriggered = false;
        fadeoutTriggered = false;
        playTriggered = false;
        treasuresTriggered = false;
        Persistence.checkMods();
        Persistence.checkGames();
        Persistence.checkUnlocks();
        Persistence.countTreasures();
        ColorInflator.onBgChange(Persistence.darkTheme);
        Persistence.save();
    }

    @Override
    public void render(Canvas canvas) {
        for (Irenderable ir : renderables) {
            ir.render(canvas);
        }
    }

    @Override
    public void update(float deltaTime) {
        if (fadeoutTriggered) {
            if (GameSettings.fadeOutTimer < 1.0f) {
                GameSettings.fadeOutTimer += deltaTime * 10f;
                if (GameSettings.fadeOutTimer >= 1.0f) {
                    GameSettings.fadeOutTimer = 1.0f;
                    if (treasuresTriggered) {
                        stateMachine.setState(StateMachine.StateTreasures);
                    } else if (playTriggered) {
                        stateMachine.setState(StateMachine.StateMatchThree);
                    } else if (statsTriggered) {
                        stateMachine.setState(StateMachine.StateStats);
                    }
                }
            }
            return;
        }
        for (Iupdateable iu : updatables) {
            iu.update(deltaTime);
        }
    }

    @Override
    public void onTouch(MotionEvent event) {
        for (Itouchable it : touchables) {
            it.onTouch(event);
        }
    }

    public boolean onBackPressed() {
        return false;
    }


    //-------------------------------------------------------------


    class Highscore implements Irenderable, Iupdateable {
        final float sin60 = (float) Math.sin(Math.PI / 3.);
        final float cos60 = (float) Math.cos(Math.PI / 3.);

        final Path pathBorder = new Path();
        final Paint paintBorder = new Paint();
        final Paint paintText = new Paint();
        final float cy;
        final float outerWidth;
        final float lx;
        final float rx;
        final float radius;
        long skill = 0;
        float tickCountDown = 1f;
        String highScoreText = "0";
        final String highScoreLabel = "Skill";

        final float[] borderHsv = new float[3];

        Highscore() {
            borderHsv[0] = 120f;
            borderHsv[1] = 0.8f;
            borderHsv[2] = 1f;
            outerWidth = Dimensions.blockSize / 2f - cos60 * Dimensions.blockSize / 2f;
            lx = Dimensions.screenHalfWidth - sin60 * Dimensions.blockSize * 3f - Dimensions.blockSize / 2f;
            rx = Dimensions.screenHalfWidth + sin60 * Dimensions.blockSize * 3f + Dimensions.blockSize / 2f;
            cy = Dimensions.screenHeight * 0.35f;
            radius = Dimensions.blockSize / 2f;

            paintText.setTypeface(TheFont.typo);
            paintText.setTextAlign(Paint.Align.CENTER);
            paintText.setColor(Color.GRAY);
            paintText.setTextSize(Dimensions.blockSize * 0.8f);

            paintBorder.setColor(Color.HSVToColor(borderHsv));
            paintBorder.setStyle(Paint.Style.STROKE);
            paintBorder.setStrokeWidth(Dimensions.blockSize / 10f);
            paintBorder.setStrokeJoin(Paint.Join.ROUND);
            setPaths();
        }

        void setPaths() {
            pathBorder.reset();
            pathBorder.moveTo(lx, cy);
            pathBorder.lineTo(lx + outerWidth, cy - radius);
            pathBorder.lineTo(rx - outerWidth, cy - radius);
            pathBorder.lineTo(rx, cy);
            pathBorder.lineTo(rx - outerWidth, cy + radius);
            pathBorder.lineTo(lx + outerWidth, cy + radius);
            pathBorder.close();
        }

        @Override
        public void update(float deltaTime) {
            borderHsv[0] += deltaTime * 60;
            borderHsv[0] %= 360;
            paintBorder.setColor(Color.HSVToColor(borderHsv));
            if (skill < Persistence.skill) {

                tickCountDown -= deltaTime * 20f;
                if (tickCountDown <= 0f) {
                    tickCountDown = 1f;
                    if (Persistence.skill - skill > 100) {
                        skill += 100;
                    } else if (Persistence.skill - skill > 10) {
                        skill += 10;
                    } else {
                        skill += 1;
                    }


                    highScoreText = "" + skill;
                }
            } else if (skill > Persistence.skill) {
                tickCountDown -= deltaTime * 20f;
                if (tickCountDown <= 0f) {
                    tickCountDown = 1f;
                    if (Persistence.skill - skill < -100) {
                        skill -= 100;
                    } else if (Persistence.skill - skill < -10) {
                        skill -= 10;
                    } else {
                        skill -= 1;
                    }


                    highScoreText = "" + skill;
                }
            } else {

            }
        }

        @Override
        public void render(Canvas canvas) {
            canvas.drawText(highScoreLabel, Dimensions.screenHalfWidth, cy - Dimensions.blockSize * 0.8f, paintText);
            canvas.drawText(highScoreText, Dimensions.screenHalfWidth, cy + Dimensions.blockSize * 0.3f, paintText);
            canvas.drawPath(pathBorder, paintBorder);
        }
    }

    class Treasures implements Irenderable, Itouchable {
        private final Paint paint = new Paint();
        final float drawX;
        final float drawY;
        final String text = "Treasures";
        final Rect borders;
        final float width;
        final float height;

        Treasures() {
            drawX = Dimensions.screenHalfWidth;
            drawY = Dimensions.screenHeight * 0.5f;
            paint.setTypeface(TheFont.typo);
            paint.setTextAlign(Paint.Align.CENTER);
            paint.setColor(Color.GRAY);
            borders = Tools.getOptimalTextHeight(text, Dimensions.screenWidth, Dimensions.blockSize, paint);
            width = borders.width();
            height = borders.height();
        }

        @Override
        public void render(Canvas canvas) {
            canvas.drawText(text, drawX, drawY, paint);
        }

        public void onTouch(MotionEvent event) {
            if (fadeoutTriggered) {
                return;
            }
            float x = event.getX();
            float y = event.getY();
            if (x > drawX - width / 2f && x < drawX + width / 2f && y > drawY - height && y < drawY) {
                paint.setColor(Color.DKGRAY);
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (!treasuresTriggered) {
                        treasuresTriggered = true;
                        fadeoutTriggered = true;
                        Sounds.playClick();
                    }
                    paint.setColor(Color.GRAY);
                }
            } else {
                paint.setColor(Color.GRAY);
            }
        }
    }

    class Stats implements Irenderable, Itouchable {
        private final Paint paint = new Paint();
        final float drawX;
        final float drawY;
        final String text = "Stats";
        final Rect borders;
        final float width;
        final float height;

        Stats() {
            drawX = Dimensions.screenHalfWidth;
            drawY = Dimensions.screenHeight * 0.6f;
            paint.setTypeface(TheFont.typo);
            paint.setTextAlign(Paint.Align.CENTER);
            paint.setColor(Color.GRAY);
            borders = Tools.getOptimalTextHeight(text, Dimensions.screenWidth, Dimensions.blockSize, paint);
            width = borders.width();
            height = borders.height();
        }

        @Override
        public void render(Canvas canvas) {
            canvas.drawText(text, drawX, drawY, paint);
        }

        public void onTouch(MotionEvent event) {
            if (fadeoutTriggered) {
                return;
            }
            float x = event.getX();
            float y = event.getY();
            if (x > drawX - width / 2f && x < drawX + width / 2f && y > drawY - height && y < drawY) {
                paint.setColor(Color.DKGRAY);
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (!statsTriggered) {
                        statsTriggered = true;
                        fadeoutTriggered = true;
                        Sounds.playClick();
                    }
                    paint.setColor(Color.GRAY);
                }
            } else {
                paint.setColor(Color.GRAY);
            }
        }
    }

    class Play implements Irenderable, Itouchable {
        private final Paint paint = new Paint();
        final float drawX;
        final float drawY;
        final String text = "Play";
        final Rect borders;
        final float width;
        final float height;

        Play() {
            drawX = Dimensions.screenHalfWidth;
            drawY = Dimensions.screenHeight * 0.7f;
            paint.setTypeface(TheFont.typo);
            paint.setTextAlign(Paint.Align.CENTER);
            paint.setColor(Persistence.darkTheme ? Color.LTGRAY : Color.DKGRAY);
            ColorInflator.hcPaints.add(paint);
            borders = Tools.getOptimalTextHeight(text, Dimensions.screenWidth, Dimensions.blockSize, paint);
            width = borders.width();
            height = borders.height();
        }

        @Override
        public void render(Canvas canvas) {
            canvas.drawText(text, drawX, drawY, paint);
        }

        public void onTouch(MotionEvent event) {
            if (fadeoutTriggered) {
                return;
            }
            float x = event.getX();
            float y = event.getY();
            if (x > drawX - width / 2f && x < drawX + width / 2f && y > drawY - height && y < drawY) {
                paint.setColor(Color.GRAY);
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (!playTriggered) {
                        playTriggered = true;
                        fadeoutTriggered = true;
                        Sounds.playClick();
                    }
                    paint.setColor(Persistence.darkTheme ? Color.LTGRAY : Color.DKGRAY);
                }
            } else {
                paint.setColor(Persistence.darkTheme ? Color.LTGRAY : Color.DKGRAY);
            }
        }

    }

    class SoundOnOff implements Irenderable, Itouchable {
        private final Paint paint = new Paint();
        final float drawX;
        final float drawY;
        final String textOn = "Sound On";
        final String textOff = "Sound Off";
        final Rect borders;
        final float width;
        final float height;
        boolean showOn = true;

        SoundOnOff() {
            drawX = Dimensions.screenHalfWidth;
            drawY = Dimensions.screenHeight * 0.85f;
            paint.setTypeface(TheFont.typo);
            paint.setTextAlign(Paint.Align.CENTER);
            paint.setColor(Color.GRAY);
            borders = Tools.getOptimalTextHeight("Sound Off", Dimensions.screenWidth, Dimensions.blockSize * 0.6f, paint);
            width = borders.width();
            height = borders.height();
        }

        @Override
        public void render(Canvas canvas) {
            if (showOn != Persistence.soundOn) {
                showOn = Persistence.soundOn;

            }
            canvas.drawText(showOn ? textOn : textOff, drawX, drawY, paint);
        }

        public void onTouch(MotionEvent event) {
            if (fadeoutTriggered) {
                return;
            }
            float x = event.getX();
            float y = event.getY();
            if (x > drawX - width / 2f && x < drawX + width / 2f && y > drawY - height && y < drawY) {
                paint.setColor(Color.DKGRAY);
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    Persistence.soundOn = !Persistence.soundOn;
                    Persistence.save();
                    Sounds.playClick();
                    paint.setColor(Color.GRAY);
                }
            } else {
                paint.setColor(Color.GRAY);
            }
        }

    }

    class RumbleOnOff implements Irenderable, Itouchable {
        private final Paint paint = new Paint();
        final float drawX;
        final float drawY;
        final String textOn = "Rumble On";
        final String textOff = "Rumble Off";
        final Rect borders;
        final float width;
        final float height;
        boolean showOn = true;

        RumbleOnOff() {
            drawX = Dimensions.screenHalfWidth;
            drawY = Dimensions.screenHeight * 0.85f + Dimensions.blockSize * 0.8f;
            paint.setTypeface(TheFont.typo);
            paint.setTextAlign(Paint.Align.CENTER);
            paint.setColor(Color.GRAY);
            borders = Tools.getOptimalTextHeight("Rumble Off", Dimensions.screenWidth, Dimensions.blockSize * 0.6f, paint);
            width = borders.width();
            height = borders.height();
        }

        @Override
        public void render(Canvas canvas) {
            if (showOn != Persistence.rumbleOn) {
                showOn = Persistence.rumbleOn;

            }
            canvas.drawText(showOn ? textOn : textOff, drawX, drawY, paint);
        }

        public void onTouch(MotionEvent event) {
            if (fadeoutTriggered) {
                return;
            }
            float x = event.getX();
            float y = event.getY();
            if (x > drawX - width / 2f && x < drawX + width / 2f && y > drawY - height && y < drawY) {
                paint.setColor(Color.DKGRAY);
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    Persistence.rumbleOn = !Persistence.rumbleOn;
                    Persistence.save();
                    Sounds.playClick();
                    paint.setColor(Color.GRAY);
                }
            } else {
                paint.setColor(Color.GRAY);
            }
        }
    }
}
