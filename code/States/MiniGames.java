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
import de.denniswittich.hex3.Interfaces.Irenderable;
import de.denniswittich.hex3.Interfaces.Itouchable;
import de.denniswittich.hex3.Interfaces.Iupdateable;
import de.denniswittich.hex3.Lerpers;
import de.denniswittich.hex3.MainActivity;
import de.denniswittich.hex3.Persistence;
import de.denniswittich.hex3.Sounds;
import de.denniswittich.hex3.TheFont;
import de.denniswittich.hex3.Title;
import de.denniswittich.hex3.Tools;

/**
 * Created by Dennis Wittich on 16.02.2017.
 */

public class MiniGames implements Iupdateable, Irenderable, Itouchable {

    private final int PAGE_DODGE = 0;
    private final int PAGE_JUMP = 1;
    private final int PAGE_FLY = 2;

    private final ArrayList<Irenderable> renderables = new ArrayList<>();
    private final ArrayList<Itouchable> touchables = new ArrayList<>();
    private final ArrayList<Iupdateable> updatables = new ArrayList<>();

    private boolean leaveTriggered;
    private boolean startFlyTriggered;
    private boolean startJumpTriggered;
    private boolean startDodgeTriggered;
    private boolean startTapTriggered;
    private boolean startPulseTriggered;

    private StateMachine stateMachine;

    private final Condition condition;
    private final Name name;

    private final IconDodge icon_dodge;
    private final IconJump icon_jump;
    private final IconFly icon_fly;

    private int page;
    private boolean consumed;
    private int hexPage = -1;

    private final Paint pageIndicator = new Paint() {{
        setColor(Color.GRAY);
    }};

    public void link(StateMachine stateMachine, MainActivity mainActivity) {
        this.stateMachine = stateMachine;
    }

    public MiniGames() {
        condition = new Condition();
        Title title = new Title();
        name = new Name();
        PrevPage prevPage = new PrevPage();
        NextPage nextPage = new NextPage();

        icon_dodge = new IconDodge();
        icon_jump = new IconJump();
        icon_fly = new IconFly();

        HighScore highScore = new HighScore();
        CoinsIndicator coinsIndicator = new CoinsIndicator();

        renderables.add(condition);
        renderables.add(title);
        renderables.add(name);
        renderables.add(prevPage);
        renderables.add(nextPage);

        renderables.add(highScore);
        renderables.add(coinsIndicator);

        updatables.add(highScore);

        touchables.add(condition);
    }

    public void init() {
        GameSettings.gameEnded = false;
        GameSettings.moveTimer = 0f;
        condition.myPage = -1;
        name.myPage = -1;
        page = 0;
        leaveTriggered = false;
        startFlyTriggered = false;
        startJumpTriggered = false;
        startDodgeTriggered = false;
        startTapTriggered = false;
        startPulseTriggered = false;

        Persistence.checkGames();
        Persistence.checkUnlocks();
        Persistence.countTreasures();
    }

    @Override
    public void render(Canvas canvas) {
        if (hexPage != page) {
            hexPage = page;
        }

        switch (page) {
            case PAGE_DODGE:
                icon_dodge.render(canvas);
                break;
            case PAGE_JUMP:
                icon_jump.render(canvas);
                break;
            case PAGE_FLY:
                icon_fly.render(canvas);
                break;
        }

        for (Irenderable ir : renderables) {
            ir.render(canvas);
        }

        canvas.drawCircle(Dimensions.screenWidth / (GameSettings.nrOfMinigames + 1) * (1 + hexPage),
                Dimensions.screenHeight - Dimensions.blockSize / 5f, Dimensions.blockSize / 10f, pageIndicator);
    }

    @Override
    public void update(float deltaTime) {
        switch (page) {
            case PAGE_DODGE:
                icon_dodge.update(deltaTime);
                break;
            case PAGE_JUMP:
                icon_jump.update(deltaTime);
                break;
            case PAGE_FLY:
                icon_fly.update(deltaTime);
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
                    if (startDodgeTriggered) {
                        stateMachine.setState(StateMachine.StateMgDodge);
                    } else if (startFlyTriggered) {
                        stateMachine.setState(StateMachine.StateMgFly);
                    } else if (startJumpTriggered) {
                        stateMachine.setState(StateMachine.StateMgJump);
                    } else if (startTapTriggered) {
                        stateMachine.setState(StateMachine.StateMgTap);
                    } else if (startPulseTriggered) {
                        stateMachine.setState(StateMachine.StateMgPulse);
                    } else {
                        stateMachine.setState(StateMachine.StateTreasures);
                    }
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
                    if (page++ >= GameSettings.nrOfMinigames - 1) {
                        page = 0;
                    }
                } else {
                    if (page-- <= 0) {
                        page = GameSettings.nrOfMinigames - 1;
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
                    case PAGE_DODGE:
                        text = "Dodge";
                        break;
                    case PAGE_JUMP:
                        text = "Jump";
                        break;
                    case PAGE_FLY:
                        text = "Fly";
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
        String textPlay = "Play";
        final String textPlay2 = "Play";
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
                textPlay = textPlay2;
                subText = "";
                switch (page) {
                    case PAGE_DODGE:
                        if (!Persistence.mgDodgeUnlocked) {
                            textLocked = textLocked2;
                            textPlay = "";
                            text = "Best Skill";
                            subText = Persistence.highskill + " / " + GameSettings.skillForDodge;
                        }
                        break;
                    case PAGE_JUMP:
                        if (!Persistence.mgJumpUnlocked) {
                            textLocked = textLocked2;
                            textPlay = "";
                            text = "Dodge";
                            subText = Persistence.hsDodge + " / " + GameSettings.hsDodgeForJump;
                        }
                        break;
                    case PAGE_FLY:
                        if (!Persistence.mgFlyUnlocked) {
                            textLocked = textLocked2;
                            textPlay = "";
                            text = "Jump";
                            subText = Persistence.hsJump + " / " + GameSettings.hsJumpForFly;
                        }
                        break;
                }
            }
            canvas.drawText(text, drawX, drawY, paint);
            canvas.drawText(subText, drawX, drawY + Dimensions.blockSize, paint);
            canvas.drawText(textLocked, drawX, Dimensions.screenHalfHeight + Dimensions.blockSize * 0.7f, paintLocked);
            canvas.drawText(textPlay, drawX, drawY, paintOnOff);
        }

        @Override
        public void onTouch(MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (event.getY() > drawY - Dimensions.blockSize) {
                    switch (page) {
                        case PAGE_DODGE:
                            if (Persistence.mgDodgeUnlocked) {
                                consumed = true;
                                startDodgeTriggered = true;
                                leaveTriggered = true;
                            }
                            break;
                        case PAGE_JUMP:
                            if (Persistence.mgJumpUnlocked) {
                                consumed = true;
                                startJumpTriggered = true;
                                leaveTriggered = true;
                            }
                            break;
                        case PAGE_FLY:
                            if (Persistence.mgFlyUnlocked) {
                                consumed = true;
                                startFlyTriggered = true;
                                leaveTriggered = true;
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

    class IconDodge implements Irenderable, Iupdateable {

        final Path path = new Path();
        final Paint paint = new Paint();
        final Paint floorPaint = new Paint();
        float hexHeight = 0f;
        float timer = 0f;

        IconDodge() {
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeJoin(Paint.Join.ROUND);
            paint.setColor(Persistence.darkTheme ? Color.WHITE : Color.BLACK);
            paint.setStrokeWidth(Dimensions.blockSize / 6f);
            ColorInflator.vhcPaints.add(paint);

            floorPaint.setStyle(Paint.Style.STROKE);
            floorPaint.setStrokeCap(Paint.Cap.ROUND);
            floorPaint.setStrokeWidth(Dimensions.blockSize / 6f);
            floorPaint.setColor(Color.GRAY);

            Tools.setHexPath(path, Dimensions.screenHalfWidth, Dimensions.screenHalfHeight + Dimensions.blockSize, Dimensions.blockSize / 2f);
        }

        @Override
        public void render(Canvas canvas) {
            canvas.drawPath(path, paint);
            canvas.drawLine(Dimensions.screenWidth * 0.25f, Dimensions.screenHalfHeight + Dimensions.blockSize * 3f / 2f + Dimensions.blockSize / 12f,
                    Dimensions.screenWidth * 0.75f, Dimensions.screenHalfHeight + Dimensions.blockSize * 3f / 2f + Dimensions.blockSize / 12f, floorPaint);
        }

        @Override
        public void update(float deltaTime) {
            timer += deltaTime;
            timer = timer % 1f;

            hexHeight = Lerpers.SmoothBump(-Dimensions.blockSize * 1.5f, Dimensions.blockSize * 1.5f, timer);
            Tools.setHexPath(path, Dimensions.screenHalfWidth + hexHeight, Dimensions.screenHalfHeight + Dimensions.blockSize, Dimensions.blockSize / 2f);

        }
    }

    class IconJump implements Irenderable, Iupdateable {

        final Path path = new Path();
        final Paint paint = new Paint();
        final Paint floorPaint = new Paint();
        float hexHeight = 0f;
        float timer = 0f;

        IconJump() {
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeJoin(Paint.Join.ROUND);
            paint.setColor(Persistence.darkTheme ? Color.WHITE : Color.BLACK);
            paint.setStrokeWidth(Dimensions.blockSize / 6f);
            ColorInflator.vhcPaints.add(paint);

            floorPaint.setStyle(Paint.Style.STROKE);
            floorPaint.setStrokeCap(Paint.Cap.ROUND);
            floorPaint.setStrokeWidth(Dimensions.blockSize / 6f);
            floorPaint.setColor(Color.GRAY);

            Tools.setHexPath(path, Dimensions.screenHalfWidth, Dimensions.screenHalfHeight + Dimensions.blockSize, Dimensions.blockSize / 2f);
        }

        @Override
        public void render(Canvas canvas) {
            canvas.drawPath(path, paint);
            canvas.drawLine(Dimensions.screenWidth * 0.25f, Dimensions.screenHalfHeight + Dimensions.blockSize * 3f / 2f + Dimensions.blockSize / 12f,
                    Dimensions.screenWidth * 0.75f, Dimensions.screenHalfHeight + Dimensions.blockSize * 3f / 2f + Dimensions.blockSize / 12f, floorPaint);
        }

        @Override
        public void update(float deltaTime) {
            timer += deltaTime;
            timer = timer % 1f;

            hexHeight = Lerpers.Bump(0, Dimensions.blockSize * 2f, timer);
            Tools.setHexPath(path, Dimensions.screenHalfWidth, Dimensions.screenHalfHeight + Dimensions.blockSize - hexHeight, Dimensions.blockSize / 2f);

        }
    }

    class IconFly implements Irenderable, Iupdateable {

        final Path path = new Path();
        final Paint paint = new Paint();
        final Paint floorPaint = new Paint();
        float hexHeight = 0f;
        float timer = 0f;

        IconFly() {
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeJoin(Paint.Join.ROUND);
            paint.setColor(Persistence.darkTheme ? Color.WHITE : Color.BLACK);
            paint.setStrokeWidth(Dimensions.blockSize / 6f);
            ColorInflator.vhcPaints.add(paint);

            floorPaint.setStyle(Paint.Style.STROKE);
            floorPaint.setStrokeCap(Paint.Cap.ROUND);
            floorPaint.setStrokeWidth(Dimensions.blockSize / 6f);
            floorPaint.setColor(Color.GRAY);

            Tools.setHexPath(path, Dimensions.screenHalfWidth, Dimensions.screenHalfHeight + Dimensions.blockSize, Dimensions.blockSize / 2f);
        }

        @Override
        public void render(Canvas canvas) {
            canvas.drawPath(path, paint);
            canvas.drawLine(Dimensions.screenWidth * 0.25f, Dimensions.screenHalfHeight + Dimensions.blockSize * 3f / 2f + Dimensions.blockSize / 12f,
                    Dimensions.screenWidth * 0.75f, Dimensions.screenHalfHeight + Dimensions.blockSize * 3f / 2f + Dimensions.blockSize / 12f, floorPaint);
        }

        @Override
        public void update(float deltaTime) {
            timer += deltaTime;
            timer = timer % 1f;

            hexHeight = Lerpers.SmoothBump(-Dimensions.blockSize / 2f, Dimensions.blockSize / 2f, timer);
            Tools.setHexPath(path, Dimensions.screenHalfWidth, Dimensions.screenHalfHeight + hexHeight, Dimensions.blockSize / 2f);

        }
    }

    class HighScore implements Irenderable, Iupdateable {
        private final Paint paint = new Paint();
        final float drawX;
        final float drawY;
        String text = "0";
        int showscore = 0;

        HighScore() {
            drawX = Dimensions.screenHalfWidth;
            drawY = Dimensions.screenHalfHeight + 3 * Dimensions.blockSize;
            paint.setTypeface(TheFont.typo);
            paint.setTextAlign(Paint.Align.CENTER);
            paint.setColor(Color.DKGRAY);
            paint.setTextSize(Dimensions.blockSize);

            ColorInflator.hcPaints.add(paint);
        }

        @Override
        public void render(Canvas canvas) {
            if (showscore > 0) {
                canvas.drawText(text, drawX, drawY, paint);
            }
        }

        @Override
        public void update(float deltaTime) {
            int score = 0;
            switch (page) {
                case PAGE_JUMP:
                    score = Persistence.hsJump;
                    break;
                case PAGE_DODGE:
                    score = Persistence.hsDodge;
                    break;
                case PAGE_FLY:
                    score = Persistence.hsFly;
                    break;
            }
            if (showscore != score) {
                showscore = score;
                text = "" + showscore;
            }
        }
    }

    class CoinsIndicator implements Irenderable {
        final Path coinPath = new Path();
        final Paint coinPaint = new Paint();

        final Paint textPaint = new Paint();
        final float cy;
        long coinsToShow = -1;
        String text = "x0";

        CoinsIndicator() {
            cy = Dimensions.screenHeight - (Dimensions.screenHeight - Dimensions.screenWidth) * 0.3f + Dimensions.blockSize;
            coinPaint.setStyle(Paint.Style.STROKE);
            coinPaint.setStrokeJoin(Paint.Join.ROUND);
            coinPaint.setColor(Persistence.darkTheme ? Color.WHITE : Color.BLACK);
            coinPaint.setStrokeWidth(Dimensions.blockSize / 10f);
            ColorInflator.vhcPaints.add(coinPaint);
            Tools.setHexPath(coinPath, Dimensions.screenHalfWidth - Dimensions.blockSize / 2f, cy - Dimensions.blockSize / 2f, Dimensions.blockSize / 2.6f);

            textPaint.setColor(Color.GRAY);
            textPaint.setTextAlign(Paint.Align.LEFT);
            textPaint.setTypeface(TheFont.typo);
            textPaint.setTextSize(Dimensions.blockSize * 0.8f);
        }

        @Override
        public void render(Canvas canvas) {
            switch (page) {
                case PAGE_DODGE:
                    if (!Persistence.mgDodgeUnlocked) {
                        return;
                    }
                    break;
                case PAGE_JUMP:
                    if (!Persistence.mgJumpUnlocked) {
                        return;
                    }
                    break;
                case PAGE_FLY:
                    if (!Persistence.mgFlyUnlocked) {
                        return;
                    }
                    break;
            }
            if (coinsToShow != Persistence.hexCoins) {
                coinsToShow = Persistence.hexCoins;
                if (coinsToShow == 0) {
                    textPaint.setColor(Color.RED);
                    text = "x0";
                } else {
                    textPaint.setColor(Color.GRAY);
                    text = "x" + coinsToShow;
                }

            }
            canvas.drawPath(coinPath, coinPaint);
            canvas.drawText(text, Dimensions.screenHalfWidth, cy - Dimensions.blockSize / 10f, textPaint);
        }
    }
}

