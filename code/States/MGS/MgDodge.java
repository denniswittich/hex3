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
import de.denniswittich.hex3.Lerpers;
import de.denniswittich.hex3.Persistence;
import de.denniswittich.hex3.Sounds;
import de.denniswittich.hex3.States.StateMachine;
import de.denniswittich.hex3.TheFont;
import de.denniswittich.hex3.Tools;

/**
 * Created by Dennis Wittich on 14.03.2017.
 */

public class MgDodge implements Irenderable, Iupdateable, Itouchable {
    private final int L = 0;
    private final int R = 1;

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

    private int w1Type = R;
    private int w2Type = L;
    private int targetPos = -1;

    private final Paint floorPaint = new Paint();

    private final Random random = new Random();
    private float redSecond = 1f;

    private float moveTimer;
    private float wall1Progress;
    private float wall2Progress;
    private float wall1y;
    private float wall2y;
    private final float hexRadius;
    private float hexX;
    private boolean isPressed;
    private boolean downPressed;
    private float pressTimer;

    public void link(StateMachine stateMachine){
        this.stateMachine = stateMachine;
    }

    public MgDodge(){
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
        hexPaint.setColor(Persistence.darkTheme?Color.WHITE:Color.BLACK);
        hexPaint.setStrokeWidth(Dimensions.blockSize/6f);
        ColorInflator.vhcPaints.add(hexPaint);

        floorPaint.setStyle(Paint.Style.STROKE);
        floorPaint.setStrokeCap(Paint.Cap.ROUND);
        floorPaint.setStrokeWidth(Dimensions.blockSize/6f);
        floorPaint.setColor(Color.GRAY);

        w1Paint.setStyle(Paint.Style.STROKE);
        w1Paint.setStrokeCap(Paint.Cap.ROUND);
        w1Paint.setStrokeWidth(Dimensions.blockSize/6f);
        w1Paint.setColor(Color.GRAY);

        w2Paint.setStyle(Paint.Style.STROKE);
        w2Paint.setStrokeCap(Paint.Cap.ROUND);
        w2Paint.setStrokeWidth(Dimensions.blockSize/6f);
        w2Paint.setColor(Color.GRAY);

        hexRadius = Dimensions.blockSize/2f;
    }

    public void init(){
        pressTimer = 0f;
        isPressed = false;
        float wallHeight = Dimensions.blockSize;
        downPressed = false;
        gameStarted = false;
        leaveTriggered = false;
        newGame();
    }

    private void newGame(){
        redSecond = 1f;
        hexPaint.setColor(Persistence.darkTheme?Color.WHITE:Color.BLACK);
        if(score>Persistence.hsDodge){
            Persistence.hsDodge = score;
            Persistence.save();
        }
        w1Type = R;
        w2Type = L;
        targetPos = -1;
        gameStarted = false;
        moveTimer = 1f;
        wall1Progress = 1f;
        wall2Progress = 2f;
        wall1y = - Dimensions.screenHeight;
        wall2y = - Dimensions.screenHeight*2f;
        hexX = Dimensions.screenHalfWidth-Dimensions.screenHalfWidth/4f;

        Tools.setHexPath(hexPath,hexX,Dimensions.screenHalfHeight+Dimensions.blockSize,hexRadius);
    }

    @Override
    public void update(float deltaTime) {
        if(leaveTriggered){
            if(GameSettings.fadeOutTimer < 1.0f){
                GameSettings.fadeOutTimer += deltaTime*10f;
                if(GameSettings.fadeOutTimer >= 1.0f){
                    GameSettings.fadeOutTimer = 1.0f;
                    stateMachine.setState(StateMachine.StateMiniGames);
                }
            }
            return;
        }
        if(redSecond<1f){
            if(moveTimer<1f){
                moveTimer+=deltaTime*2f*(1+score/25f);
                if(moveTimer>1f){
                    moveTimer = 1f;
                }
                hexX = Lerpers.Linear(hexX,Dimensions.screenHalfWidth+targetPos*Dimensions.screenHalfWidth/4f,moveTimer);
                Tools.setHexPath(hexPath,hexX,Dimensions.screenHalfHeight+Dimensions.blockSize,hexRadius);
            }
            downPressed = false;
            redSecond+=deltaTime;
            hexPaint.setColor(Color.RED);
            if(redSecond>=1f){
                newGame();
            }
            return;
        }

        for (Iupdateable updatable : updatables) {
            updatable.update(deltaTime);
        }

        if(!gameStarted){
            if(isPressed){
                if(Persistence.hexCoins<=0){
                    return;
                }
                if(pressTimer == 0f){
                    score = 0;
                    Persistence.hexCoins--;
                }
                pressTimer+=deltaTime*6f;
                if(pressTimer>1f){
                    pressTimer = 0.1f;
                    if(score<25){
                        score++;
                        Persistence.hexCoins--;
                    }
                }
            }else if(pressTimer>0f){
                pressTimer = 0f;
                Sounds.playClick();
                Persistence.save();
                gameStarted = true;
            }
            downPressed = false;
            return;
        }

        if(downPressed){
            downPressed = false;
            //            if(moveTimer>=1f){
//                Sounds.playClick();
//                moveTimer = 0f;
//            }
        }

        wall1Progress -= deltaTime*0.9f*(1+score/25f);
        if(wall1Progress<0f){
            wall1Progress = wall2Progress+1f+random.nextFloat()/2f;
            w1Type = random.nextInt(2);
            score++;
            BackGroundFire.FIRE = true;
        }
        wall1y = Dimensions.screenHalfHeight+Dimensions.blockSize+hexRadius - wall1Progress*Dimensions.screenHalfWidth;

        wall2Progress -= deltaTime*0.9f*(1+score/25f);
        if(wall2Progress<0f){
            wall2Progress = wall1Progress+1f+random.nextFloat()/2f;
            w2Type = random.nextInt(2);
            score++;
            BackGroundFire.FIRE = true;
        }
        wall2y = Dimensions.screenHalfHeight+Dimensions.blockSize+hexRadius - wall2Progress*Dimensions.screenHalfWidth;

        boolean l_occ = false;
        boolean r_occ = false;
        if(wall1y>Dimensions.screenHalfHeight+Dimensions.blockSize-hexRadius){
            switch (w1Type){
                case L:
                    l_occ = true;
                    break;
                case R:
                    r_occ = true;
                    break;
            }
        }
        if(wall2y>Dimensions.screenHalfHeight+Dimensions.blockSize-hexRadius){
            switch (w2Type){
                case L:
                    l_occ = true;
                    break;
                case R:
                    r_occ = true;
                    break;
            }
        }

        if((targetPos==-1 && l_occ)|| (targetPos==1 && r_occ)){
            redSecond=0f;
            Sounds.vibrate();
            return;
        }

        if(moveTimer<1f){
            moveTimer+=deltaTime*2f*(1+score/25f);
            if(moveTimer>1f){
                moveTimer = 1f;
            }
            hexX = Lerpers.Linear(hexX,Dimensions.screenHalfWidth+targetPos*Dimensions.screenHalfWidth/4f,moveTimer);
        }

        if(wall1Progress<=1f && wall1Progress>0.5f){
            w1Paint.setAlpha((int)((1f-wall1Progress)*2f*255f));
        }else if(wall1Progress>1f){
            w1Paint.setAlpha(0);
        }else{
            w1Paint.setAlpha(255);
        }

        if(wall2Progress<=1f && wall2Progress>0.5f){
            w2Paint.setAlpha((int)((1f-wall2Progress)*2f*255f));
        }else if(wall2Progress>1f){
            w2Paint.setAlpha(0);
        }else{
            w2Paint.setAlpha(255);
        }

        Tools.setHexPath(hexPath,hexX,Dimensions.screenHalfHeight+Dimensions.blockSize,hexRadius);

    }

    @Override
    public void render(Canvas canvas) {
        canvas.drawLine(Dimensions.screenWidth*0.25f,Dimensions.screenHalfHeight+Dimensions.blockSize+hexRadius+Dimensions.blockSize/12f,
                Dimensions.screenWidth*0.75f,Dimensions.screenHalfHeight+Dimensions.blockSize+hexRadius+Dimensions.blockSize/12f, floorPaint);
        switch (w1Type){
            case L:
                canvas.drawLine(Dimensions.screenHalfWidth/2f,wall1y,
                        Dimensions.screenHalfWidth,wall1y, w1Paint);
                break;
            case R:
                canvas.drawLine(Dimensions.screenHalfWidth,wall1y,
                        Dimensions.screenWidth-Dimensions.screenHalfWidth/2f,wall1y, w1Paint);
                break;
        }
        switch (w2Type){
            case L:
                canvas.drawLine(Dimensions.screenHalfWidth/2f,wall2y,
                        Dimensions.screenHalfWidth,wall2y, w2Paint);
                break;
            case R:
                canvas.drawLine(Dimensions.screenHalfWidth,wall2y,
                        Dimensions.screenWidth-Dimensions.screenHalfWidth/2f,wall2y, w2Paint);
                break;

        }
        canvas.drawPath(hexPath,hexPaint);
        for (Irenderable renderable : renderables) {
            renderable.render(canvas);
        }
    }

    @Override
    public void onTouch(MotionEvent event) {
        if(event.getAction()==MotionEvent.ACTION_DOWN){
            isPressed = true;
            downPressed = true;
            if(gameStarted && redSecond == 1f){
                Sounds.playClick();
                moveTimer = 0f;
                moveTimer = 0f;
                if(targetPos == -1){
                    targetPos = 1;
                }else{
                    targetPos = -1;
                }
            }
        }
        if(event.getAction()==MotionEvent.ACTION_UP){
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
            drawY = Dimensions.screenHalfHeight+3*Dimensions.blockSize;
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
            if(showscore != score){
                showscore = score;
                text = "" + showscore;
            }
        }
    }

    class StartHelpText implements Irenderable, Iupdateable{
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
            drawY = Dimensions.screenHalfHeight-2.5f*Dimensions.blockSize;
            paint.setTypeface(TheFont.typo);
            paint.setTextAlign(Paint.Align.CENTER);
            paint.setColor(Color.DKGRAY);
            paint.setTextSize(Dimensions.blockSize*0.8f);
            ColorInflator.hcPaints.add(paint);
        }

        @Override
        public void render(Canvas canvas) {
            if(gameStarted){
                return;
            }
            canvas.drawText(curText, drawX, drawY, paint);
        }

        @Override
        public void update(float deltaTime) {
            if(gameStarted){
                return;
            }
            timer += deltaTime/2f;
            if(timer<0.5f){
                if(Persistence.hexCoins>0){
                    curText = text1;
                }else{
                    curText = text3;
                }
            }else {
                if(Persistence.hexCoins>0){
                    curText = text2;
                }else{
                    curText = text3;
                }
                if(timer>1.0f){
                    timer = 0f;
                }
            }
        }

    }

    class CoinsIndicator implements Irenderable{
        final Path coinPath = new Path();
        final Paint coinPaint = new Paint();

        final Paint textPaint = new Paint();
        final float cy;
        long coinsToShow = -1;
        String text = "x0";

        CoinsIndicator(){
            cy = Dimensions.screenHeight - (Dimensions.screenHeight-Dimensions.screenWidth)*0.3f + Dimensions.blockSize;
            coinPaint.setStyle(Paint.Style.STROKE);
            coinPaint.setStrokeJoin(Paint.Join.ROUND);
            coinPaint.setColor(Persistence.darkTheme?Color.WHITE:Color.BLACK);
            coinPaint.setStrokeWidth(Dimensions.blockSize/10f);
            ColorInflator.vhcPaints.add(coinPaint);
            Tools.setHexPath(coinPath,Dimensions.screenHalfWidth-Dimensions.blockSize/2f,cy-Dimensions.blockSize/2f,Dimensions.blockSize/2.6f);

            textPaint.setColor(Color.GRAY);
            textPaint.setTextAlign(Paint.Align.LEFT);
            textPaint.setTypeface(TheFont.typo);
            textPaint.setTextSize(Dimensions.blockSize*0.8f);
        }

        @Override
        public void render(Canvas canvas) {
            if(coinsToShow!=Persistence.hexCoins){
                coinsToShow = Persistence.hexCoins;
                if(coinsToShow == 0){
                    textPaint.setColor(Color.RED);
                    text = "x0";
                }else{
                    textPaint.setColor(Color.GRAY);
                    text = "x"+coinsToShow;
                }

            }
            canvas.drawPath(coinPath,coinPaint);
            canvas.drawText(text,Dimensions.screenHalfWidth,cy-Dimensions.blockSize/10f,textPaint);
        }
    }
}

