package de.denniswittich.hex3.States.MGS;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.view.MotionEvent;

import java.util.ArrayList;
import java.util.Random;

import de.denniswittich.hex3.Backgrounds.BackGroundFire;
import de.denniswittich.hex3.ColorInflator;
import de.denniswittich.hex3.Dimensions;
import de.denniswittich.hex3.GameSettings;
import de.denniswittich.hex3.Interfaces.Irenderable;
import de.denniswittich.hex3.Interfaces.Itouchable;
import de.denniswittich.hex3.Interfaces.Iupdateable;
import de.denniswittich.hex3.Persistence;
import de.denniswittich.hex3.Sounds;
import de.denniswittich.hex3.States.StateMachine;
import de.denniswittich.hex3.TheFont;
import de.denniswittich.hex3.Tools;

/**
 * Created by Dennis Wittich on 14.03.2017.
 */

public class MgFly implements Irenderable, Iupdateable, Itouchable {
    final int U = 0;
    final int D = 1;
    final int UD = 2;

    private StateMachine stateMachine;

    private final ArrayList<Irenderable> renderables = new ArrayList<>();
    private final ArrayList<Iupdateable> updatables = new ArrayList<>();
    private int score;
    private boolean gameStarted;
    private boolean leaveTriggered;

    private final Path hexPath = new Path();
    private final Paint hexPaint = new Paint();
    private final Paint w1Paint = new Paint();
    private final Paint w2Paint = new Paint();

    private final Paint floorPaint = new Paint();
    private final Random random = new Random();

    private final float minLowerUp;
    private final float maxLowerUp;
    private final float hexMinHeight;
    private final float hexMaxHeight;
    private float wall1Progress;
    private float wall2Progress;
    private float wall1x;
    private float wall2x;
    private float wall1LowerUp;
    private float wall2LowerUp;
    private final float hexRadius;
    private float hexHeight;
    private boolean isPressed;
    private boolean downPressed;
    private float redSecond;
    private float pressTimer;

    public void link(StateMachine stateMachine) {
        this.stateMachine = stateMachine;
    }

    public MgFly() {
        CurrentScore currentScore = new CurrentScore();
        CoinsIndicator coinsIndicator = new CoinsIndicator();
        StartHelpText startHelpText = new StartHelpText();

        renderables.add(coinsIndicator);
        renderables.add(currentScore);
        renderables.add(startHelpText);
        updatables.add(startHelpText);
        updatables.add(currentScore);

        hexPaint.setStyle(Paint.Style.STROKE);
        hexPaint.setStrokeJoin(Paint.Join.ROUND);
        hexPaint.setColor(Persistence.darkTheme ? Color.WHITE : Color.BLACK);
        hexPaint.setStrokeWidth(Dimensions.blockSize / 6f);
        ColorInflator.vhcPaints.add(hexPaint);

        floorPaint.setStyle(Paint.Style.STROKE);
        floorPaint.setStrokeCap(Paint.Cap.ROUND);
        floorPaint.setStrokeWidth(Dimensions.blockSize / 6f);
        floorPaint.setColor(Color.GRAY);

        w1Paint.setStyle(Paint.Style.STROKE);
        w1Paint.setStrokeCap(Paint.Cap.ROUND);
        w1Paint.setStrokeWidth(Dimensions.blockSize / 6f);
        w1Paint.setColor(Color.GRAY);

        w2Paint.setStyle(Paint.Style.STROKE);
        w2Paint.setStrokeCap(Paint.Cap.ROUND);
        w2Paint.setStrokeWidth(Dimensions.blockSize / 6f);
        w2Paint.setColor(Color.GRAY);

        maxLowerUp = Dimensions.screenHalfHeight - Dimensions.blockSize;
        minLowerUp = Dimensions.screenHalfHeight + Dimensions.blockSize;

        hexRadius = Dimensions.blockSize / 2f;
        hexMinHeight = Dimensions.screenHalfHeight + Dimensions.blockSize;
        hexMaxHeight = Dimensions.screenHalfHeight - Dimensions.blockSize * 3;
    }

    public void init() {
        pressTimer = 0f;
        isPressed = false;
        downPressed = false;
        gameStarted = false;
        leaveTriggered = false;
        newGame();
    }

    private void newGame() {
        redSecond = 1f;
        hexPaint.setColor(Persistence.darkTheme ? Color.WHITE : Color.BLACK);
        if (score > Persistence.hsFly) {
            Persistence.hsFly = score;
            Persistence.save();
        }

        gameStarted = false;
        wall1Progress = 1f;
        wall2Progress = 2f;
        wall1x = -hexRadius;
        wall2x = -hexRadius;
        hexHeight = hexMinHeight;

        wall1LowerUp = minLowerUp;
        wall2LowerUp = maxLowerUp;

        Tools.setHexPath(hexPath, Dimensions.screenHalfWidth, Dimensions.screenHalfHeight + Dimensions.blockSize, hexRadius);
    }

    @Override
    public void update(float deltaTime) {
        if (leaveTriggered) {
            if (GameSettings.fadeOutTimer < 1.0f) {
                GameSettings.fadeOutTimer += deltaTime * 10f;
                if (GameSettings.fadeOutTimer >= 1.0f) {
                    GameSettings.fadeOutTimer = 1.0f;
                    stateMachine.setState(StateMachine.StateMiniGames);
                }
            }
            return;
        }

        if (redSecond < 1f) {
            redSecond += deltaTime;
            downPressed = false;
            hexPaint.setColor(Color.RED);
            if (redSecond >= 1f) {
                newGame();
            }
            return;
        }

        for (Iupdateable updatable : updatables) {
            updatable.update(deltaTime);
        }

        if (!gameStarted) {
            if (isPressed) {
                if (Persistence.hexCoins <= 0) {
                    return;
                }
                if (pressTimer == 0f) {
                    score = 0;
                    Persistence.hexCoins--;
                }
                pressTimer += deltaTime * 6f;
                if (pressTimer > 1f) {
                    pressTimer = 0.1f;
                    if (score < 25) {
                        score++;
                        Persistence.hexCoins--;
                    }
                }
            } else if (pressTimer > 0f) {
                pressTimer = 0f;
                Sounds.playClick();
                Persistence.save();
                gameStarted = true;
            }
            downPressed = false;
            return;
        }

        if (downPressed) {
            downPressed = false;
            if (!gameStarted) {
                if (Persistence.hexCoins > 0 || Persistence.debugMode) {
                    Persistence.hexCoins--;
                    Persistence.save();
                    gameStarted = true;
                    score = Persistence.godMode ? 45 : 0;
                    Sounds.playClick();
                } else {
                    // play Sound not possible
                }
                return;
            }
        }

        if (wall1Progress >= 0.5f && wall1Progress - deltaTime * 0.35f * (1 + score / 25f) < 0.5f) {
            if (hexHeight > wall1LowerUp - hexRadius || hexHeight < wall1LowerUp - Dimensions.blockSize * 2f + hexRadius) {
                redSecond = 0f;
                Sounds.vibrate();
                return;
            } else {
                score++;
                BackGroundFire.FIRE = true;
            }
        }

        if (wall2Progress >= 0.5f && wall2Progress - deltaTime * 0.35f * (1 + score / 25f) < 0.5f) {
            if (hexHeight > wall2LowerUp - hexRadius || hexHeight < wall2LowerUp - Dimensions.blockSize * 2f + hexRadius) {
                redSecond = 0f;
                Sounds.vibrate();
                return;
            } else {
                score++;
                BackGroundFire.FIRE = true;
            }
        }

        wall1Progress -= deltaTime * 0.35f * (1 + score / 25f);
        if (wall1Progress < 0f) {
            wall1Progress = wall2Progress + 0.5f + random.nextFloat();
            wall1LowerUp = minLowerUp + random.nextFloat() * (maxLowerUp - minLowerUp);
        }
        wall1x = (wall1Progress) * Dimensions.screenWidth;

        wall2Progress -= deltaTime * 0.35f * (1 + score / 25f);
        if (wall2Progress < 0f) {
            wall2Progress = wall1Progress + 0.5f + random.nextFloat();
            wall2LowerUp = minLowerUp + random.nextFloat() * (maxLowerUp - minLowerUp);
        }
        wall2x = (wall2Progress) * Dimensions.screenWidth;

        if (isPressed) {
            hexHeight -= deltaTime * (1 + score / 25f) * hexRadius * 3.5f;
            if (hexHeight < hexMaxHeight) {
                hexHeight = hexMaxHeight;
            }
        } else {
            hexHeight += deltaTime * (1 + score / 25f) * hexRadius * 3.5f;
            if (hexHeight > hexMinHeight) {
                hexHeight = hexMinHeight;
            }
        }

        if (wall1Progress < 1f && wall1Progress > 0.75f) {
            w1Paint.setAlpha((int) ((1f - wall1Progress) * 4f * 255f));
        } else if (wall1Progress > 0f && wall1Progress < 0.25f) {
            w1Paint.setAlpha((int) (wall1Progress * 4f * 255f));
        } else {
            w1Paint.setAlpha(255);
        }

        if (wall2Progress < 1f && wall2Progress > 0.75f) {
            w2Paint.setAlpha((int) ((1f - wall2Progress) * 4f * 255f));
        } else if (wall2Progress > 0f && wall2Progress < 0.25f) {
            w2Paint.setAlpha((int) (wall2Progress * 4f * 255f));
        } else {
            w2Paint.setAlpha(255);
        }

        Tools.setHexPath(hexPath, Dimensions.screenHalfWidth, hexHeight, hexRadius);
    }

    @Override
    public void render(Canvas canvas) {
        canvas.drawLine(Dimensions.screenWidth * 0.25f, Dimensions.screenHalfHeight + Dimensions.blockSize + hexRadius + Dimensions.blockSize / 12f,
                Dimensions.screenWidth * 0.75f, Dimensions.screenHalfHeight + Dimensions.blockSize + hexRadius + Dimensions.blockSize / 12f, floorPaint);

        canvas.drawLine(wall1x, Dimensions.screenHalfHeight + Dimensions.blockSize + hexRadius, wall1x, wall1LowerUp, w1Paint);
        canvas.drawLine(wall1x, wall1LowerUp - Dimensions.blockSize * 2f, wall1x, Dimensions.screenHalfHeight - Dimensions.blockSize * 3 - hexRadius, w1Paint);
        canvas.drawLine(wall2x, Dimensions.screenHalfHeight + Dimensions.blockSize + hexRadius, wall2x, wall2LowerUp, w2Paint);
        canvas.drawLine(wall2x, wall2LowerUp - Dimensions.blockSize * 2f, wall2x, Dimensions.screenHalfHeight - Dimensions.blockSize * 3 - hexRadius, w2Paint);
        canvas.drawPath(hexPath, hexPaint);
        for (Irenderable renderable : renderables) {
            renderable.render(canvas);
        }
    }

    @Override
    public void onTouch(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            isPressed = true;
            downPressed = true;
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            isPressed = false;
        }
    }

    public void onBackPressed() {
        leaveTriggered = true;
    }

    class CurrentScore implements Irenderable, Iupdateable {
        private final Paint paint = new Paint();
        final float drawX;
        final float drawY;
        String text = "0";
        int showscore = 0;

        CurrentScore() {
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
            canvas.drawText(text, drawX, drawY, paint);
        }

        @Override
        public void update(float deltaTime) {
            if (showscore != score) {
                showscore = score;
                text = "" + showscore;
            }
        }
    }

    class StartHelpText implements Irenderable, Iupdateable {
        private final Paint paint = new Paint();
        final float drawX;
        final float drawY;
        final String text1 = "TAP TO PLAY";
        final String text2 = "HOLD TO BOOST";
        final String text3 = "NO COINS";
        String curText = "";
        float timer;

        StartHelpText() {
            drawX = Dimensions.screenHalfWidth;
            drawY = Dimensions.screenHalfHeight - 2.5f * Dimensions.blockSize;
            paint.setTypeface(TheFont.typo);
            paint.setTextAlign(Paint.Align.CENTER);
            paint.setColor(Color.DKGRAY);
            paint.setTextSize(Dimensions.blockSize * 0.8f);
            ColorInflator.hcPaints.add(paint);
        }

        @Override
        public void render(Canvas canvas) {
            if (gameStarted) {
                return;
            }
            canvas.drawText(curText, drawX, drawY, paint);
        }

        @Override
        public void update(float deltaTime) {
            if (gameStarted) {
                return;
            }
            timer += deltaTime / 2f;
            if (timer < 0.5f) {
                if (Persistence.hexCoins > 0) {
                    curText = text1;
                } else {
                    curText = text3;
                }
            } else {
                if (Persistence.hexCoins > 0) {
                    curText = text2;
                } else {
                    curText = text3;
                }
                if (timer > 1.0f) {
                    timer = 0f;
                }
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

