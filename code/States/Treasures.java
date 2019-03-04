package de.denniswittich.hex3.States;

import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.net.Uri;
import android.view.MotionEvent;

import java.util.ArrayList;

import de.denniswittich.hex3.Dimensions;
import de.denniswittich.hex3.GameSettings;
import de.denniswittich.hex3.Interfaces.Irenderable;
import de.denniswittich.hex3.Interfaces.Itouchable;
import de.denniswittich.hex3.Interfaces.Iupdateable;
import de.denniswittich.hex3.MainActivity;
import de.denniswittich.hex3.Persistence;
import de.denniswittich.hex3.Sounds;
import de.denniswittich.hex3.TheFont;
import de.denniswittich.hex3.Title;
import de.denniswittich.hex3.Tools;

/**
 * Created by Dennis Wittich on 16.02.2017.
 */

public class Treasures implements Iupdateable,Irenderable,Itouchable {

    private final ArrayList<Irenderable> renderables = new ArrayList<>();
    private final ArrayList<Itouchable> touchables = new ArrayList<>();
    private final ArrayList<Iupdateable> updatables = new ArrayList<>();

    private boolean fadeoutTriggered;
    private boolean leaveTriggered;
    private boolean minigamesTriggered;
    private boolean upgradesTriggered;
    private boolean modsTriggered;
    private StateMachine stateMachine;
    private MainActivity mainActivity;

    public Treasures(){
        Title title = new Title();
        Upgrades upgrades = new Upgrades();
        Mods mods = new Mods();
        UpgradeCounter upgradeCounter = new UpgradeCounter();
        MiniGames miniGames = new MiniGames();

        RateApp rateApp = new RateApp();
        MoreGames moreGames = new MoreGames();

        renderables.add(title);
        renderables.add(upgrades);
        renderables.add(mods);
        renderables.add(miniGames);
        renderables.add(upgradeCounter);
        renderables.add(rateApp);
        renderables.add(moreGames);

        updatables.add(upgradeCounter);

        touchables.add(rateApp);
        touchables.add(moreGames);
        touchables.add(upgrades);
        touchables.add(miniGames);
        touchables.add(mods);
    }

    public void link(StateMachine stateMachine, MainActivity mainActivity){
        this.stateMachine = stateMachine;
        this.mainActivity = mainActivity;
    }

    public void init(){
        leaveTriggered = false;
        minigamesTriggered = false;
        fadeoutTriggered = false;
        upgradesTriggered = false;
        modsTriggered = false;
        Persistence.checkMods();
        Persistence.checkGames();
        Persistence.checkUnlocks();
        Persistence.countTreasures();
    }

    @Override
    public void render(Canvas canvas) {
        for (Irenderable ir: renderables ) {
            ir.render(canvas);
        }
    }

    @Override
    public void update(float deltaTime) {

        if(fadeoutTriggered){
            if(GameSettings.fadeOutTimer < 1.0f){
                GameSettings.fadeOutTimer += deltaTime*10f;
                if(GameSettings.fadeOutTimer >= 1.0f){
                    GameSettings.fadeOutTimer = 1.0f;
                    if(modsTriggered){
                        stateMachine.setState(StateMachine.StateMods);
                    }else if(upgradesTriggered){
                        stateMachine.setState(StateMachine.StateUpgrades);
                    }else if (minigamesTriggered){
                        stateMachine.setState(StateMachine.StateMiniGames);
                    }else if (leaveTriggered){
                        stateMachine.setState(StateMachine.StateMenu);
                    }
                }
            }
            return;
        }
        for (Iupdateable iu:updatables) {
            iu.update(deltaTime);
        }
    }

    @Override
    public void onTouch(MotionEvent event) {
        for (Itouchable it: touchables ) {
            it.onTouch(event);
        }
    }

    public void onBackPressed(){
        leaveTriggered = true;
        fadeoutTriggered = true;
    }

    //-------------------------------------------------------------

    class UpgradeCounter implements  Irenderable, Iupdateable{
        final float sin60 = (float)Math.sin(Math.PI/3.);
        final float cos60 = (float)Math.cos(Math.PI/3.);

        final Path pathBorder = new Path();
        final Paint paintBorder = new Paint();
        final Paint paintText = new Paint();
        final float cy;
        final float outerWidth;
        final float lx;
        final float rx;
        final float radius;
        long upgradeCount = -1;
        String highScoreText = "0 / 17";
        final String highScoreLabel = "Treasures";

        final float[] borderHsv = new float[3];

        UpgradeCounter(){
            borderHsv[0] = 120f;
            borderHsv[1] = 0.8f;
            borderHsv[2] = 1f;
            outerWidth = Dimensions.blockSize/2f - cos60*Dimensions.blockSize/2f;
            lx = Dimensions.screenHalfWidth - sin60*Dimensions.blockSize*3f - Dimensions.blockSize/2f;
            rx = Dimensions.screenHalfWidth + sin60*Dimensions.blockSize*3f + Dimensions.blockSize/2f;
            cy = Dimensions.screenHeight *0.35f ;
            radius = Dimensions.blockSize/2f;

            paintText.setTypeface(TheFont.typo);
            paintText.setTextAlign(Paint.Align.CENTER);
            paintText.setColor(Color.GRAY);
            paintText.setTextSize(Dimensions.blockSize*0.8f);

            paintBorder.setColor(Color.HSVToColor(borderHsv));
            paintBorder.setStyle(Paint.Style.STROKE);
            paintBorder.setStrokeWidth(Dimensions.blockSize/10f);
            paintBorder.setStrokeJoin(Paint.Join.ROUND);
            setPaths();
        }

        void setPaths(){
            pathBorder.reset();
            pathBorder.moveTo(lx,cy);
            pathBorder.lineTo(lx+outerWidth,cy-radius);
            pathBorder.lineTo(rx-outerWidth,cy-radius);
            pathBorder.lineTo(rx,cy);
            pathBorder.lineTo(rx-outerWidth,cy+radius);
            pathBorder.lineTo(lx+outerWidth,cy+radius);
            pathBorder.close();
        }

        @Override
        public void update(float deltaTime) {
            borderHsv[0]+=deltaTime*60;
            borderHsv[0]%=360;
            paintBorder.setColor(Color.HSVToColor(borderHsv));
            if(upgradeCount != Persistence.treasureCounter){
                upgradeCount = Persistence.treasureCounter;
                highScoreText = "" + upgradeCount + " / "+ GameSettings.nrOfTreasures;
            }
        }

        @Override
        public void render(Canvas canvas) {
            canvas.drawText(highScoreLabel,Dimensions.screenHalfWidth,cy-Dimensions.blockSize*0.8f,paintText);
            canvas.drawText(highScoreText,Dimensions.screenHalfWidth,cy+Dimensions.blockSize*0.3f,paintText);
            canvas.drawPath(pathBorder, paintBorder);
        }
    }

    class Mods implements Irenderable,Itouchable{
        private final Paint paint = new Paint();
        final float drawX;
        final float drawY;
        final String text = "Mods";
        final Rect borders;
        final float width;
        final float height;

        Mods(){
            drawX = Dimensions.screenHalfWidth;
            drawY = Dimensions.screenHeight * 0.7f;
            paint.setTypeface(TheFont.typo);
            paint.setTextAlign(Paint.Align.CENTER);
            paint.setColor(Color.GRAY);
            borders = Tools.getOptimalTextHeight(text,Dimensions.screenWidth,Dimensions.blockSize ,paint);
            width = borders.width();
            height = borders.height();
        }

        @Override
        public void render(Canvas canvas) {
            canvas.drawText(text,drawX,drawY,paint);
        }

        public void onTouch(MotionEvent event){
            if( fadeoutTriggered){
                return;
            }
            float x = event.getX();
            float y = event.getY();
            if( x>drawX-width/2f && x<drawX+width/2f && y>drawY-height && y<drawY){
                paint.setColor(Color.DKGRAY);
                if(event.getAction()==MotionEvent.ACTION_UP){
                    if(!modsTriggered){
                        modsTriggered = true;
                        fadeoutTriggered = true;
                        Sounds.playClick();
                    }
                    paint.setColor(Color.GRAY);
                }
            }else{
                paint.setColor(Color.GRAY);
            }
        }
    }

    class MiniGames implements Irenderable,Itouchable{
        private final Paint paint = new Paint();
        final float drawX;
        final float drawY;
        final String text = "Mini Games";
        final Rect borders;
        final float width;
        final float height;

        MiniGames(){
            drawX = Dimensions.screenHalfWidth;
            drawY = Dimensions.screenHeight * 0.5f;
            paint.setTypeface(TheFont.typo);
            paint.setTextAlign(Paint.Align.CENTER);
            paint.setColor(Color.GRAY);
            borders = Tools.getOptimalTextHeight(text,Dimensions.screenWidth,Dimensions.blockSize ,paint);
            width = borders.width();
            height = borders.height();
        }

        @Override
        public void render(Canvas canvas) {
            canvas.drawText(text,drawX,drawY,paint);
        }

        public void onTouch(MotionEvent event){
            if( fadeoutTriggered){
                return;
            }
            float x = event.getX();
            float y = event.getY();
            if( x>drawX-width/2f && x<drawX+width/2f && y>drawY-height && y<drawY){
                paint.setColor(Color.DKGRAY);
                if(event.getAction()==MotionEvent.ACTION_UP){
                    if(!minigamesTriggered){
                        minigamesTriggered = true;
                        fadeoutTriggered = true;
                        Sounds.playClick();
                    }
                    paint.setColor(Color.GRAY);
                }
            }else{
                paint.setColor(Color.GRAY);
            }
        }
    }

    class Upgrades implements Irenderable,Itouchable{
        private final Paint paint = new Paint();
        final float drawX;
        final float drawY;
        final String text = "Upgrades";
        final Rect borders;
        final float width;
        final float height;

        Upgrades(){
            drawX = Dimensions.screenHalfWidth;
            drawY = Dimensions.screenHeight * 0.6f;
            paint.setTypeface(TheFont.typo);
            paint.setTextAlign(Paint.Align.CENTER);
            paint.setColor(Color.GRAY);
            borders = Tools.getOptimalTextHeight(text,Dimensions.screenWidth,Dimensions.blockSize ,paint);
            width = borders.width();
            height = borders.height();
        }

        @Override
        public void render(Canvas canvas) {
            canvas.drawText(text,drawX,drawY,paint);
        }

        public void onTouch(MotionEvent event){
            if( fadeoutTriggered){
                return;
            }
            float x = event.getX();
            float y = event.getY();
            if( x>drawX-width/2f && x<drawX+width/2f && y>drawY-height && y<drawY){
                paint.setColor(Color.DKGRAY);
                if(event.getAction()==MotionEvent.ACTION_UP){
                    if(!upgradesTriggered){
                        upgradesTriggered = true;
                        fadeoutTriggered = true;
                        Sounds.playClick();
                    }
                    paint.setColor(Color.GRAY);
                }
            }else{
                paint.setColor(Color.GRAY);
            }
        }

    }

    class MoreGames implements Irenderable,Itouchable{
        private final Paint paint = new Paint();
        final float drawX;
        final float drawY;
        final String text = "More Free Games";
        final Rect borders;
        final float width;
        final float height;
        boolean showOn = true;

        MoreGames(){
            drawX = Dimensions.screenHalfWidth;
            drawY = Dimensions.screenHeight * 0.85f;
            paint.setTypeface(TheFont.typo);
            paint.setTextAlign(Paint.Align.CENTER);
            paint.setColor(Color.GRAY);
            borders = Tools.getOptimalTextHeight(text,Dimensions.screenWidth,Dimensions.blockSize*0.6f ,paint);
            width = borders.width();
            height = borders.height();
        }

        @Override
        public void render(Canvas canvas) {
            if(showOn!=Persistence.soundOn){
                showOn=Persistence.soundOn;

            }
            canvas.drawText(text,drawX,drawY,paint);
        }

        public void onTouch(MotionEvent event){
            if( fadeoutTriggered){
                return;
            }
            float x = event.getX();
            float y = event.getY();
            if( x>drawX-width/2f && x<drawX+width/2f && y>drawY-height && y<drawY){
                paint.setColor(Color.DKGRAY);
                if(event.getAction()==MotionEvent.ACTION_UP){
                    mainActivity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/developer?id=Dennis+Wittich")));
                    paint.setColor(Color.GRAY);
                }
            }else{
                paint.setColor(Color.GRAY);
            }
        }

    }

    class RateApp implements Irenderable,Itouchable{
        private final Paint paint = new Paint();
        final float drawX;
        final float drawY;
        final String text = "Rate App";
        final Rect borders;
        final float width;
        final float height;
        boolean showOn = true;

        RateApp(){
            drawX = Dimensions.screenHalfWidth;
            drawY = Dimensions.screenHeight * 0.85f + Dimensions.blockSize*0.8f;
            paint.setTypeface(TheFont.typo);
            paint.setTextAlign(Paint.Align.CENTER);
            paint.setColor(Color.GRAY);
            borders = Tools.getOptimalTextHeight(text,Dimensions.screenWidth,Dimensions.blockSize*0.6f ,paint);
            width = borders.width();
            height = borders.height();
        }

        @Override
        public void render(Canvas canvas) {
            if(showOn!=Persistence.rumbleOn){
                showOn=Persistence.rumbleOn;

            }
            canvas.drawText(text,drawX,drawY,paint);
        }

        public void onTouch(MotionEvent event){
            if( fadeoutTriggered){
                return;
            }
            float x = event.getX();
            float y = event.getY();
            if( x>drawX-width/2f && x<drawX+width/2f && y>drawY-height && y<drawY){
                paint.setColor(Color.DKGRAY);
                if(event.getAction()==MotionEvent.ACTION_UP){
                    mainActivity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=de.denniswittich.hex3")));
                    paint.setColor(Color.GRAY);
                }
            }else{
                paint.setColor(Color.GRAY);
            }
        }
    }
}
