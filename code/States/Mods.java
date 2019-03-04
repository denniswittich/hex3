package de.denniswittich.hex3.States;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.view.MotionEvent;

import java.util.ArrayList;

import de.denniswittich.hex3.ColorInflator;
import de.denniswittich.hex3.Dimensions;
import de.denniswittich.hex3.GameSettings;
import de.denniswittich.hex3.Hex;
import de.denniswittich.hex3.Interfaces.Irenderable;
import de.denniswittich.hex3.Interfaces.Itouchable;
import de.denniswittich.hex3.Interfaces.Iupdateable;
import de.denniswittich.hex3.Lerpers;
import de.denniswittich.hex3.Persistence;
import de.denniswittich.hex3.Sounds;
import de.denniswittich.hex3.TheFont;
import de.denniswittich.hex3.Title;
import de.denniswittich.hex3.Tools;

/**
 * Created by Dennis Wittich on 16.02.2017.
 */

public class Mods implements Iupdateable, Irenderable, Itouchable {

    private final int PAGE_INVERTGRAVITY = 0;
    private final int PAGE_BUBBLES = 1;
    private final int PAGE_DARK = 2;
    private final int PAGE_SPACE = 3;
    private final int PAGE_RAINBOW = 4;
    private final int PAGE_FIRE = 5;

    private final ArrayList<Irenderable> renderables = new ArrayList<>();
    private final ArrayList<Itouchable> touchables = new ArrayList<>();
    private final ArrayList<Iupdateable> updatables = new ArrayList<>();

    private boolean leaveTriggered;

    private StateMachine stateMachine;
    private final Condition condition;
    private final Name name;

    private final InvertGravity invertGravity;
    private final BubblesIcon bubblesIcon;
    private final DarkThemeIcon darkThemeIcon;
    private final StarsIcon starsIcon;
    private final RainbowIcon rainbowIcon;
    private final FireIcon fireIcon;

    private int page;
    private boolean consumed;
    private int hexPage = -1;

    private final Paint pageIndicator = new Paint() {{
        setColor(Color.GRAY);
    }};

    public void link(StateMachine stateMachine) {
        this.stateMachine = stateMachine;
    }

    public Mods() {
        condition = new Condition();
        Title title = new Title();
        name = new Name();
        PrevPage prevPage = new PrevPage();
        NextPage nextPage = new NextPage();

        invertGravity = new InvertGravity();
        bubblesIcon = new BubblesIcon();
        darkThemeIcon = new DarkThemeIcon();
        starsIcon = new StarsIcon();
        fireIcon = new FireIcon();
        rainbowIcon = new RainbowIcon();

        renderables.add(condition);
        renderables.add(title);
        renderables.add(name);
        renderables.add(prevPage);
        renderables.add(nextPage);

        touchables.add(condition);
    }

    public void init() {
        GameSettings.gameEnded = false;
        GameSettings.moveTimer = 0f;
        condition.myPage = -1;
        name.myPage = -1;
        page = 0;
        leaveTriggered = false;
    }

    @Override
    public void render(Canvas canvas) {
        if (hexPage != page) {
            hexPage = page;
        }

        switch (page) {
            case PAGE_INVERTGRAVITY:
                invertGravity.render(canvas);
                break;
            case PAGE_BUBBLES:
                bubblesIcon.render(canvas);
                break;
            case PAGE_DARK:
                darkThemeIcon.render(canvas);
                break;
            case PAGE_SPACE:
                starsIcon.render(canvas);
                break;
            case PAGE_RAINBOW:
                rainbowIcon.render(canvas);
                break;
            case PAGE_FIRE:
                fireIcon.render(canvas);
                break;
        }

        for (Irenderable ir : renderables) {
            ir.render(canvas);
        }

        canvas.drawCircle(Dimensions.screenWidth / (GameSettings.nrOfMods + 1) * (1 + hexPage),
                Dimensions.screenHeight - Dimensions.blockSize / 5f, Dimensions.blockSize / 10f, pageIndicator);
    }

    @Override
    public void update(float deltaTime) {

        switch (page) {
            case PAGE_INVERTGRAVITY:
                invertGravity.update(deltaTime);
                break;
            case PAGE_BUBBLES:
                bubblesIcon.update(deltaTime);
                break;
            case PAGE_DARK:
                darkThemeIcon.update(deltaTime);
                break;
            case PAGE_SPACE:
                starsIcon.update(deltaTime);
                break;
            case PAGE_RAINBOW:
                rainbowIcon.update(deltaTime);
                break;
            case PAGE_FIRE:
                fireIcon.update(deltaTime);
                break;
        }

        for (Iupdateable iu : updatables) {
            iu.update(deltaTime);
        }

        if (leaveTriggered) {
            if (GameSettings.fadeOutTimer < 1.0f) {
                GameSettings.fadeOutTimer += deltaTime * 10f;
                if (GameSettings.fadeOutTimer >= 1.0f) {
                    GameSettings.fadeOutTimer = 1.0f;
                    stateMachine.setState(StateMachine.StateTreasures);
                }
            }
        }

    }

    @Override
    public void onTouch(MotionEvent event) {
        if (leaveTriggered) {
            return;
        }

        consumed = false;

        for (Itouchable it : touchables) {
            it.onTouch(event);
        }

        if (!consumed) {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                Sounds.playClick();
                if (event.getX() > Dimensions.screenHalfWidth) {
                    if (page++ >= GameSettings.nrOfMods - 1) {
                        page = 0;
                    }
                } else {
                    if (page-- <= 0) {
                        page = GameSettings.nrOfMods - 1;
                    }
                }
            }
        }
    }

    public void onBackPressed() {
        leaveTriggered = true;
    }

    //-------------------------------------------------------------


    class Name implements Irenderable {
        private final Paint paint;
        final float drawX;
        final float drawY;
        String text = "Name";
        int myPage = -1;

        Name() {
            drawX = Dimensions.screenHalfWidth;
            drawY = Dimensions.screenHalfHeight - Dimensions.blockSize * 4f;
            paint = new Paint();
            paint.setTextSize(Dimensions.blockSize);
            paint.setTypeface(TheFont.typo);
            paint.setTextAlign(Paint.Align.CENTER);
            paint.setColor(Color.GRAY);
        }

        @Override
        public void render(Canvas canvas) {
            if (page != myPage) {
                myPage = page;
                switch (page) {
                    case PAGE_INVERTGRAVITY:
                        text = "Anti Gravity";
                        break;
                    case PAGE_BUBBLES:
                        text = "Bubbles";
                        break;
                    case PAGE_DARK:
                        text = "Dark Theme";
                        break;
                    case PAGE_SPACE:
                        text = "Stars";
                        break;
                    case PAGE_RAINBOW:
                        text = "Rainbow";
                        break;
                    case PAGE_FIRE:
                        text = "Firework";
                        break;
                }
            }
            canvas.drawText(text, drawX, drawY, paint);
        }
    }

    class Condition implements Irenderable, Itouchable {
        private final Paint paint = new Paint();
        private final Paint paintLocked = new Paint();
        private final Paint paintOnOff = new Paint();
        final float drawX;
        final float drawY;
        String textLocked = "Locked";
        final String textLocked2 = "Locked";
        String textOnOff = "Off";
        final String textOn = "On";
        final String textOff = "Off";
        String text = "Condition";
        String subText = "";
        int myPage = -1;

        Condition() {
            drawX = Dimensions.screenHalfWidth;
            float cy = Dimensions.screenHeight - (Dimensions.screenHeight - Dimensions.screenWidth) / 4f;

            drawY = cy - Dimensions.blockSize;
            paint.setTypeface(TheFont.typo);
            paint.setTextAlign(Paint.Align.CENTER);
            paint.setColor(Color.GRAY);
            paint.setTextSize(Dimensions.blockSize * 0.9f);

            paintLocked.setTypeface(TheFont.typo);
            paintLocked.setTextAlign(Paint.Align.CENTER);
            paintLocked.setColor(Color.RED);
            paintLocked.setTextSize(Dimensions.blockSize * 2.2f);

            paintOnOff.setTypeface(TheFont.typo);
            paintOnOff.setTextAlign(Paint.Align.CENTER);
            paintOnOff.setColor(Color.GRAY);
            paintOnOff.setTextSize(Dimensions.blockSize * 1.5f);
        }

        @Override
        public void render(Canvas canvas) {
            if (page != myPage) {
                myPage = page;
                text = "Condition " + page;
                text = "";
                textLocked = "";
                textOnOff = textOn;
                subText = "";
                switch (page) {
                    case PAGE_INVERTGRAVITY:
                        if (!Persistence.invertGravityUnlocked) {
                            textLocked = textLocked2;
                            textOnOff = "";
                            text = "Hexes";
                            subText = Persistence.hexCounter + " / " + GameSettings.hexesForInvertGravity;
                        } else {
                            if (Persistence.invertGravity) {
                                textOnOff = textOn;
                            } else {
                                textOnOff = textOff;
                            }
                        }
                        break;
                    case PAGE_BUBBLES:
                        if (!Persistence.bubblesUnlocked) {
                            textLocked = textLocked2;
                            textOnOff = "";
                            text = "Hexes";
                            subText = Persistence.hexCounter + " / " + GameSettings.hexesForBubbles;
                        } else {
                            if (Persistence.bubbles) {
                                textOnOff = textOn;
                            } else {
                                textOnOff = textOff;
                            }
                        }
                        break;
                    case PAGE_DARK:
                        if (!Persistence.darkThemeUnlocked) {
                            textLocked = textLocked2;
                            textOnOff = "";
                            text = "Hexes";
                            subText = Persistence.hexCounter + " / " + GameSettings.hexesForDarkTheme;
                        } else {
                            if (Persistence.darkTheme) {
                                textOnOff = textOn;
                            } else {
                                textOnOff = textOff;
                            }
                        }
                        break;
                    case PAGE_SPACE:
                        if (!Persistence.spaceUnlocked) {
                            textLocked = textLocked2;
                            textOnOff = "";
                            text = "Hexes";
                            subText = Persistence.hexCounter + " / " + GameSettings.hexesForSpace;
                        } else {
                            if (Persistence.space) {
                                textOnOff = textOn;
                            } else {
                                textOnOff = textOff;
                            }
                        }
                        break;
                    case PAGE_RAINBOW:
                        if (!Persistence.rainbowUnlocked) {
                            textLocked = textLocked2;
                            textOnOff = "";
                            text = "Hexes";
                            subText = Persistence.hexCounter + " / " + GameSettings.hexesForRainbow;
                        } else {
                            if (Persistence.rainbow) {
                                textOnOff = textOn;
                            } else {
                                textOnOff = textOff;
                            }
                        }
                        break;
                    case PAGE_FIRE:
                        if (!Persistence.fireUnlocked) {
                            textLocked = textLocked2;
                            textOnOff = "";
                            text = "Mini Games";
                            subText = Persistence.hsDodge + Persistence.hsFly + Persistence.hsJump + " / " + GameSettings.mgForFire;
                        } else {
                            if (Persistence.fire) {
                                textOnOff = textOn;
                            } else {
                                textOnOff = textOff;
                            }
                        }
                        break;
                }
            }
            canvas.drawText(text, drawX, drawY, paint);
            canvas.drawText(subText, drawX, drawY + Dimensions.blockSize, paint);
            canvas.drawText(textLocked, drawX, Dimensions.screenHalfHeight + Dimensions.blockSize * 0.7f, paintLocked);
            canvas.drawText(textOnOff, drawX, drawY + Dimensions.blockSize, paintOnOff);
        }

        @Override
        public void onTouch(MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (event.getY() > drawY - Dimensions.blockSize / 2f) {
                    switch (page) {
                        case PAGE_INVERTGRAVITY: // Invert Grav
                            if (Persistence.invertGravityUnlocked) {
                                consumed = true;
                                Persistence.invertGravity = !Persistence.invertGravity;
                                if (Persistence.invertGravity) {
                                    textOnOff = textOn;
                                } else {
                                    textOnOff = textOff;
                                }
                            }
                            break;
                        case PAGE_BUBBLES: // Bubbles
                            if (Persistence.bubblesUnlocked) {
                                consumed = true;
                                Persistence.bubbles = !Persistence.bubbles;
                                if (Persistence.bubbles) {
                                    textOnOff = textOn;
                                } else {
                                    textOnOff = textOff;
                                }
                            }
                            break;
                        case PAGE_DARK:
                            if (Persistence.darkThemeUnlocked) {
                                consumed = true;
                                Persistence.darkTheme = !Persistence.darkTheme;
                                if (Persistence.darkTheme) {
                                    textOnOff = textOn;
                                } else {
                                    textOnOff = textOff;
                                }
                                ColorInflator.onBgChange(Persistence.darkTheme);
                            }
                            break;
                        case PAGE_SPACE:
                            if (Persistence.spaceUnlocked) {
                                consumed = true;
                                Persistence.space = !Persistence.space;
                                if (Persistence.space) {
                                    textOnOff = textOn;
                                } else {
                                    textOnOff = textOff;
                                }
                            }
                            break;
                        case PAGE_RAINBOW:
                            if (Persistence.rainbowUnlocked) {
                                consumed = true;
                                Persistence.rainbow = !Persistence.rainbow;
                                if (Persistence.rainbow) {
                                    textOnOff = textOn;
                                } else {
                                    textOnOff = textOff;
                                }
                            }
                            break;
                        case PAGE_FIRE:
                            if (Persistence.fireUnlocked) {
                                consumed = true;
                                Persistence.fire = !Persistence.fire;
                                if (Persistence.fire) {
                                    textOnOff = textOn;
                                } else {
                                    textOnOff = textOff;
                                }
                            }
                            break;
                    }
                }
            }
        }
    }

    class NextPage implements Irenderable {
        private final Paint paint = new Paint();
        final float drawX;
        final float drawY;

        NextPage() {
            drawX = Dimensions.screenWidth - Dimensions.blockSize * 0.2f;
            drawY = Dimensions.screenHalfHeight;
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(Dimensions.blockSize * 0.1f);
            paint.setStrokeJoin(Paint.Join.ROUND);
            paint.setStrokeCap(Paint.Cap.ROUND);
            paint.setTextAlign(Paint.Align.CENTER);
            paint.setColor(Color.GRAY);
            paint.setTextSize(Dimensions.blockSize);
        }

        @Override
        public void render(Canvas canvas) {
            canvas.drawLine(drawX - Dimensions.blockSize * 0.8f, drawY - Dimensions.blockSize * 0.8f, drawX, drawY, paint);
            canvas.drawLine(drawX - Dimensions.blockSize * 0.8f, drawY + Dimensions.blockSize * 0.8f, drawX, drawY, paint);
        }
    }

    class PrevPage implements Irenderable {
        private final Paint paint = new Paint();
        final float drawX;
        final float drawY;

        PrevPage() {
            drawX = Dimensions.blockSize * 0.2f;
            drawY = Dimensions.screenHalfHeight;
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(Dimensions.blockSize * 0.1f);
            paint.setStrokeJoin(Paint.Join.ROUND);
            paint.setStrokeCap(Paint.Cap.ROUND);
            paint.setTextAlign(Paint.Align.CENTER);
            paint.setColor(Color.GRAY);
            paint.setTextSize(Dimensions.blockSize);
        }

        @Override
        public void render(Canvas canvas) {
            canvas.drawLine(drawX + Dimensions.blockSize * 0.8f, drawY - Dimensions.blockSize * 0.8f, drawX, drawY, paint);
            canvas.drawLine(drawX + Dimensions.blockSize * 0.8f, drawY + Dimensions.blockSize * 0.8f, drawX, drawY, paint);
        }
    }

    class InvertGravity implements Irenderable, Iupdateable {
        final Paint paintArrow = new Paint();
        final Path pathArrow = new Path();
        float time = 0f;

        InvertGravity() {
            setPath(Dimensions.screenHalfHeight, Dimensions.blockSize * 1.5f);
            paintArrow.setColor(Color.GRAY);
            paintArrow.setAlpha(0);
            paintArrow.setStyle(Paint.Style.STROKE);
            paintArrow.setStrokeWidth(Dimensions.blockSize / 4f);
            paintArrow.setStrokeJoin(Paint.Join.ROUND);
        }

        void setPath(float cy, float r) {
            float cx = Dimensions.screenHalfWidth;
            pathArrow.reset();
            pathArrow.moveTo(cx - r, cy + 2 * r);
            pathArrow.lineTo(cx - r, cy);
            pathArrow.lineTo(cx - 2 * r, cy);
            pathArrow.lineTo(cx, cy - 2 * r);
            pathArrow.lineTo(cx + 2 * r, cy);
            pathArrow.lineTo(cx + r, cy);
            pathArrow.lineTo(cx + r, cy + 2 * r);
            pathArrow.close();
        }

        @Override
        public void render(Canvas canvas) {
            canvas.drawPath(pathArrow, paintArrow);
        }

        @Override
        public void update(float deltaTime) {
            time += deltaTime / 2f;
            time = time % 1f;
            float cy = Lerpers.Smooth(Dimensions.screenHalfHeight + Dimensions.blockSize, Dimensions.screenHalfHeight - Dimensions.blockSize, time);
            paintArrow.setAlpha((int) Lerpers.SmoothBump(0, 254, time));
            setPath(cy, Dimensions.blockSize);
        }
    }

    class BubblesIcon implements Irenderable, Iupdateable {
        final Paint paintBubble = new Paint();
        float time = 0f;
        float delta = 0f;

        BubblesIcon() {
            paintBubble.setColor(Color.GRAY);
        }

        @Override
        public void render(Canvas canvas) {
            canvas.drawCircle(Dimensions.screenHalfWidth + Dimensions.blockSize * 1.5f + delta / 10f, Dimensions.screenHalfHeight - delta, Dimensions.blockSize, paintBubble);
            canvas.drawCircle(Dimensions.screenHalfWidth - Dimensions.blockSize + delta, Dimensions.screenHalfHeight + Dimensions.blockSize * 1.8f + delta / 2f, Dimensions.blockSize * 0.8f, paintBubble);
            canvas.drawCircle(Dimensions.screenHalfWidth - Dimensions.blockSize / 2f - delta / 2f, Dimensions.screenHalfHeight - Dimensions.blockSize + delta, Dimensions.blockSize * 0.6f, paintBubble);
        }

        @Override
        public void update(float deltaTime) {
            time += deltaTime / 20f;
            time = time % 1f;
            delta = Lerpers.SmoothBump(-Dimensions.blockSize, +Dimensions.blockSize, time);
        }
    }

    class StarsIcon implements Irenderable, Iupdateable {
        final Paint paint = new Paint();
        final Paint paint2 = new Paint();
        float time = 0f;
        float time2 = 0.5f;
        float angle;
        float cx, cy, vx, vy;
        float cx2, cy2, vx2, vy2;
        float cx3, cy3, vx3, vy3;
        float cx4, cy4, vx4, vy4;
        float cx5, cy5, vx5, vy5;
        float cx6, cy6, vx6, vy6;

        StarsIcon() {
            paint.setColor(Color.GRAY);
            paint2.setColor(Color.GRAY);
            newDirection1();
        }

        void newDirection1() {
            cx = Dimensions.screenHalfWidth;
            cy = Dimensions.screenHalfHeight;
            angle = (float) (Math.random() * Math.PI * 2f);
            vx = (float) Math.sin(angle);
            vy = (float) Math.cos(angle);

            cx2 = Dimensions.screenHalfWidth;
            cy2 = Dimensions.screenHalfHeight;
            vx2 = (float) Math.sin(angle + (float) Math.PI * 2f / 3f);
            vy2 = (float) Math.cos(angle + (float) Math.PI * 2f / 3f);

            cx3 = Dimensions.screenHalfWidth;
            cy3 = Dimensions.screenHalfHeight;
            vx3 = (float) Math.sin(angle - (float) Math.PI * 2f / 3f);
            vy3 = (float) Math.cos(angle - (float) Math.PI * 2f / 3f);
        }

        void newDirection2() {
            cx4 = Dimensions.screenHalfWidth;
            cy4 = Dimensions.screenHalfHeight;
            angle = (float) (Math.random() * Math.PI * 2f);
            vx4 = (float) Math.sin(angle);
            vy4 = (float) Math.cos(angle);

            cx5 = Dimensions.screenHalfWidth;
            cy5 = Dimensions.screenHalfHeight;
            vx5 = (float) Math.sin(angle + (float) Math.PI * 2f / 3f);
            vy5 = (float) Math.cos(angle + (float) Math.PI * 2f / 3f);

            cx6 = Dimensions.screenHalfWidth;
            cy6 = Dimensions.screenHalfHeight;
            vx6 = (float) Math.sin(angle - (float) Math.PI * 2f / 3f);
            vy6 = (float) Math.cos(angle - (float) Math.PI * 2f / 3f);
        }

        @Override
        public void render(Canvas canvas) {
            canvas.drawCircle(cx, cy, Dimensions.blockSize / 10f, paint);
            canvas.drawCircle(cx2, cy2, Dimensions.blockSize / 10f, paint);
            canvas.drawCircle(cx3, cy3, Dimensions.blockSize / 10f, paint);

            canvas.drawCircle(cx4, cy4, Dimensions.blockSize / 10f, paint);
            canvas.drawCircle(cx5, cy5, Dimensions.blockSize / 10f, paint);
            canvas.drawCircle(cx6, cy6, Dimensions.blockSize / 10f, paint);

        }

        @Override
        public void update(float deltaTime) {
            time += deltaTime * 2f;
            if (time > 1f) {
                newDirection1();
                time = 0f;
            }
            time2 += deltaTime * 2f;
            if (time2 > 1f) {
                newDirection2();
                time2 = 0f;
            }
            cx += vx * deltaTime * Dimensions.blockSize * 6f;
            cy += vy * deltaTime * Dimensions.blockSize * 6f;
            cx2 += vx2 * deltaTime * Dimensions.blockSize * 6f;
            cy2 += vy2 * deltaTime * Dimensions.blockSize * 6f;
            cx3 += vx3 * deltaTime * Dimensions.blockSize * 6f;
            cy3 += vy3 * deltaTime * Dimensions.blockSize * 6f;

            cx4 += vx4 * deltaTime * Dimensions.blockSize * 6f;
            cy4 += vy4 * deltaTime * Dimensions.blockSize * 6f;
            cx5 += vx5 * deltaTime * Dimensions.blockSize * 6f;
            cy5 += vy5 * deltaTime * Dimensions.blockSize * 6f;
            cx6 += vx6 * deltaTime * Dimensions.blockSize * 6f;
            cy6 += vy6 * deltaTime * Dimensions.blockSize * 6f;
            paint.setAlpha((int) ((1f - time) * 255));
        }
    }

    class FireIcon implements Irenderable, Iupdateable {
        final Paint paint = new Paint();
        float time = 0f;
        float delta = 0f;
        final float[] hsv = new float[]{120f, 0.8f, 1.0f};

        FireIcon() {
            paint.setColor(Color.RED);
        }

        @Override
        public void render(Canvas canvas) {
            canvas.drawCircle(Dimensions.screenHalfWidth + delta * 0.7f, Dimensions.screenHalfHeight + delta * 0.7f,
                    Dimensions.blockSize / 10f, paint);
            canvas.drawCircle(Dimensions.screenHalfWidth - delta * 0.7f, Dimensions.screenHalfHeight + delta * 0.7f,
                    Dimensions.blockSize / 10f, paint);
            canvas.drawCircle(Dimensions.screenHalfWidth + delta * 0.7f, Dimensions.screenHalfHeight - delta * 0.7f,
                    Dimensions.blockSize / 10f, paint);
            canvas.drawCircle(Dimensions.screenHalfWidth - delta * 0.7f, Dimensions.screenHalfHeight - delta * 0.7f,
                    Dimensions.blockSize / 10f, paint);
            canvas.drawCircle(Dimensions.screenHalfWidth + delta, Dimensions.screenHalfHeight,
                    Dimensions.blockSize / 10f, paint);
            canvas.drawCircle(Dimensions.screenHalfWidth - delta, Dimensions.screenHalfHeight,
                    Dimensions.blockSize / 10f, paint);
            canvas.drawCircle(Dimensions.screenHalfWidth, Dimensions.screenHalfHeight - delta,
                    Dimensions.blockSize / 10f, paint);
            canvas.drawCircle(Dimensions.screenHalfWidth, Dimensions.screenHalfHeight + delta,
                    Dimensions.blockSize / 10f, paint);
        }

        @Override
        public void update(float deltaTime) {
            time += deltaTime;
            if (time > 1f) {
                hsv[0] = (float) Math.random() * 360;
                time = 0f;
                paint.setColor(Color.HSVToColor(hsv));
            }
            delta = Lerpers.SlowEnd(0, Dimensions.blockSize * 2.5f, time);
            paint.setAlpha((int) ((1f - time) * 255));
        }
    }


    class DarkThemeIcon implements Irenderable, Iupdateable {

        final Path pathBigDarkHex = new Path();
        final Paint paintBigHex = new Paint();
        final Hex[] hexes = new Hex[19];
        private float time = 0f;

        DarkThemeIcon() {
            hexes[0] = new Hex(0, 0);
            hexes[1] = new Hex(1, 0);
            hexes[2] = new Hex(-1, 0);
            hexes[3] = new Hex(0, 1);
            hexes[4] = new Hex(0, -1);
            hexes[5] = new Hex(1, -1);
            hexes[6] = new Hex(-1, 1);

            hexes[7] = new Hex(2, 0);
            hexes[8] = new Hex(2, -1);
            hexes[9] = new Hex(-2, 0);
            hexes[10] = new Hex(-2, 1);
            hexes[11] = new Hex(-1, 2);
            hexes[12] = new Hex(0, 2);
            hexes[13] = new Hex(0, -2);
            hexes[14] = new Hex(1, -2);
            hexes[15] = new Hex(1, 1);
            hexes[16] = new Hex(-1, -1);
            hexes[17] = new Hex(2, -2);
            hexes[18] = new Hex(-2, 2);

            for (Hex hexe : hexes) {
                hexe.setRandomType();
            }

            Tools.setHexSharpPath(pathBigDarkHex, Dimensions.screenHalfWidth, Dimensions.screenHalfHeight, Dimensions.blockSize * 2.5f + Dimensions.blockMargin);
            paintBigHex.setStrokeJoin(Paint.Join.ROUND);
            paintBigHex.setColor(Color.BLACK);
            paintBigHex.setStyle(Paint.Style.FILL_AND_STROKE);
            paintBigHex.setStrokeJoin(Paint.Join.ROUND);
            paintBigHex.setStrokeWidth(Dimensions.blockSize / 6f);
        }

        @Override
        public void render(Canvas canvas) {
            if (!Persistence.darkTheme) {
                canvas.drawPath(pathBigDarkHex, paintBigHex);
            }
            if (time > 1f) {
                time = 0f;
                hexes[(int) (Math.random() * hexes.length)].setRandomType();
            }
            for (Hex hexe : hexes) {
                hexe.render(canvas);
            }
        }

        @Override
        public void update(float deltaTime) {
            time += deltaTime * 3f;
        }
    }

    class RainbowIcon implements Irenderable, Iupdateable {

        final Path pathBigDarkHex = new Path();
        final Paint paintBigHex = new Paint();
        final Hex[] hexes = new Hex[19];
        private float time = 0f;
        private float huetime = 0f;
        final float[] hsv = new float[]{0f, 0.8f, 0.5f};

        RainbowIcon() {
            hexes[0] = new Hex(0, 0);
            hexes[1] = new Hex(1, 0);
            hexes[2] = new Hex(-1, 0);
            hexes[3] = new Hex(0, 1);
            hexes[4] = new Hex(0, -1);
            hexes[5] = new Hex(1, -1);
            hexes[6] = new Hex(-1, 1);

            hexes[7] = new Hex(2, 0);
            hexes[8] = new Hex(2, -1);
            hexes[9] = new Hex(-2, 0);
            hexes[10] = new Hex(-2, 1);
            hexes[11] = new Hex(-1, 2);
            hexes[12] = new Hex(0, 2);
            hexes[13] = new Hex(0, -2);
            hexes[14] = new Hex(1, -2);
            hexes[15] = new Hex(1, 1);
            hexes[16] = new Hex(-1, -1);
            hexes[17] = new Hex(2, -2);
            hexes[18] = new Hex(-2, 2);

            for (Hex hexe : hexes) {
                hexe.setRandomType();
            }

            Tools.setHexSharpPath(pathBigDarkHex, Dimensions.screenHalfWidth, Dimensions.screenHalfHeight, Dimensions.blockSize * 2.5f + Dimensions.blockMargin);
            paintBigHex.setStrokeJoin(Paint.Join.ROUND);
            paintBigHex.setColor(Color.GRAY);
            paintBigHex.setStyle(Paint.Style.FILL_AND_STROKE);
            paintBigHex.setStrokeJoin(Paint.Join.ROUND);
            paintBigHex.setStrokeWidth(Dimensions.blockSize / 6f);
        }

        @Override
        public void render(Canvas canvas) {
            canvas.drawPath(pathBigDarkHex, paintBigHex);

            if (time > 1f) {
                time = 0f;
                hexes[(int) (Math.random() * hexes.length)].setRandomType();
            }
            for (Hex hexe : hexes) {
                hexe.render(canvas);
            }
        }

        @Override
        public void update(float deltaTime) {
            time += deltaTime * 3f;
            huetime += deltaTime / 15f;
            huetime = huetime % 1f;
            hsv[0] = (int) (huetime * 360);
            paintBigHex.setColor(Color.HSVToColor(hsv));
        }
    }
}

