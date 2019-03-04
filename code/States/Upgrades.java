package de.denniswittich.hex3.States;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.MotionEvent;

import java.util.ArrayList;

import de.denniswittich.hex3.Sounds;
import de.denniswittich.hex3.TheFont;
import de.denniswittich.hex3.Dimensions;
import de.denniswittich.hex3.GameSettings;
import de.denniswittich.hex3.HexExample;
import de.denniswittich.hex3.Interfaces.Irenderable;
import de.denniswittich.hex3.Interfaces.Itouchable;
import de.denniswittich.hex3.Interfaces.Iupdateable;
import de.denniswittich.hex3.Persistence;
import de.denniswittich.hex3.Title;

/**
 * Created by Dennis on 16.02.2017.
 */

public class Upgrades implements Iupdateable, Irenderable, Itouchable {

    private final int PAGE_4 = 0;
    private final int PAGE_Z = 1;
    private final int PAGE_5 = 2;
    private final int PAGE_V = 3;
    private final int PAGE_U = 4;
    private final int PAGE_T = 5;
    private final int PAGE_M = 6;
    private final int PAGE_Y = 7;
    private final int PAGE_x2 = 8;
    private final int PAGE_X = 9;
    private final int PAGE_x3 = 10;

    private final ArrayList<Irenderable> renderables = new ArrayList<>();
    private final ArrayList<Itouchable> touchables = new ArrayList<>();
    private final ArrayList<Iupdateable> updatables = new ArrayList<>();
    private final ArrayList<HexExample> hexes = new ArrayList<>();

    private boolean leaveTriggered;
    private StateMachine stateMachine;
    private final Condition condition;
    private final Name name;

    private int page;
    private int hexPage = -1;

    private final Paint pageIndicator = new Paint(){{
        setColor(Color.GRAY);
    }};

    public void link(StateMachine stateMachine) {
        this.stateMachine = stateMachine;
    }

    public Upgrades() {
        condition = new Condition();
        Title title = new Title();
        name = new Name();
        PrevPage prevPage = new PrevPage();
        NextPage nextPage = new NextPage();

        renderables.add(condition);
        renderables.add(title);
        renderables.add(name);
        renderables.add(prevPage);
        renderables.add(nextPage);
    }

    public void init() {
        condition.myPage = -1;
        name.myPage = -1;
        page = 0;
        leaveTriggered = false;
        createPage();
        loadPage();
    }

    private void createPage(){

        updatables.removeAll(hexes);
        renderables.removeAll(hexes);
        hexes.clear();
        for (int x = -3; x <= 3 ; x++){
            for (int y = -3; y <= 3 ; y++){
                if(x*y>=0){
                    if(Math.abs(x)+Math.abs(y)<=3 ){
                        hexes.add(new HexExample(x,y, exampleType(x,y)));
                    }
                }else{
                    if(Math.max(Math.abs(x),Math.abs(y))<=3 ){
                        hexes.add(new HexExample(x,y, exampleType(x,y)));
                    }
                }
            }
        }
    }

    private void loadPage(){
        HexExample.setRandomShapeColor();
        for (HexExample hex : hexes) {
            hex.setType(exampleType(hex.x,hex.y));
        }
    }

    private int exampleType(int x, int y) {
        switch(page){
            case PAGE_4: // 4-hex
                if((x==0&& (y==-1 || y==0 || y==1 || y==2))){
                    return HexExample.SHAPE;
                }else if((x==0&& (y==-2 || y==-3 || y==3 ))){
                    return HexExample.OTHERREMOVE;
                }
                break;
            case PAGE_Z: // z-hex
                if((y==0&& (x==-1 || x==0 || x==1)) || (x==1&&y==-1) || (x==-1&&y==1)){
                    return HexExample.SHAPE;
                }else if((x==-2&& y==1)||(x==2&& y==-1)||
                        (x==0&& y==1)||(x==0&& y==-1)){
                    return HexExample.OTHERREMOVE;
                }else if((x==0&& y==3)||(x==2&& y==-3)||( x==-3 && y==0 )){
                    return HexExample.SAMECOLORRMV;
                }
                break;
            case PAGE_5: // 5-hex
                if((x==0&& (y==-2 || y==-1 || y==0 || y==1 || y==2 ))){
                    return HexExample.SHAPE;
                }else if((x==0&& (y==-3 || y==3 ))){
                    return HexExample.OTHERREMOVE;
                }else if((x==3&& y==0)||(x==2&& y==-2)||( x==-3 && y==1 )){
                    return HexExample.SAMECOLORRMV;
                }
                break;
            case PAGE_V: // v-hex
                if((x==1&& y==1)||(x==1&& y==0)||(x==-1&& y==1)||
                        (x==1&& y==-1)||(x==0&& y==1) ){
                    return HexExample.SHAPE;
                }else if((x==1&& y==-2)||(x==1&& y==-3)||
                        (x==-2&& y==1)||(x==-3&& y==1)){
                    return HexExample.OTHERREMOVE;
                }
                break;
            case PAGE_U: // u-hex
                if((x==0&& y==1)||(x==1&& y==0)||(x==2&& y==-1)||
                        (x==-1 && y==1)||(x==-2&& y==1) ){
                    return HexExample.SHAPE;
                }else if((x==-3&& y==1)||(x==3&& y==-2)){
                    return HexExample.OTHERREMOVE;
                }
                break;
            case PAGE_T: // u-hex
                if((x==0&& y==1)||(x==0&& y==0)||(x==0&& y==-1)||
                        (x==-1&& y==0)||(x==1&& y==-2) ){
                    return HexExample.SHAPE;
                }else if((x==1&& y==-1)||(x==1&& y==0)||(x==-1&& y==1)
                        ||(x==-1&& y==2)||(x==0&& y==2)||(x==0&& y==3)){
                    return HexExample.OTHERREMOVE;
                }
                break;
            case PAGE_M: // m-hex
                if((x==0&& y==-1)||(x==1&& y==-1)||(x==2&& y==-1)||
                        (x==-1&& y==0)||(x==-2&& y==1)||(x==0&& y==0)||(x==0&& y==1) ){
                    return HexExample.SHAPE;
                }else if((x==3&& y==-1)||(x==-3&& y==2)||(x==0&& y==2)||(x==0&& y==3)){
                    return HexExample.OTHERREMOVE;
                }else if((x==1&& y==-3)||(x==2&& y==1)||( x==-3 && y==0 )){
                    return HexExample.SAMECOLORRMV;
                }
                break;
            case PAGE_Y: // Y-hex
                if((x==0&& y==0)||(x==0&& y==1)||(x==0&& y==2)||
                        (x==-1&& y==0)||(x==-2&& y==0) ||
                        (x==1&& y==-1)||(x==2&& y==-2)){
                    return HexExample.SHAPE;
                }else if((x==3&& y==-3)||(x==0&& y==3) ||(x==-3&& y==0)){
                    return HexExample.OTHERREMOVE;
                }
                if(x*y>=0){
                    if(Math.abs(x)+Math.abs(y)<=2 ){
                        return HexExample.OTHERREMOVE;
                    }
                }else{
                    if(Math.max(Math.abs(x),Math.abs(y))<=2 ){
                        return HexExample.OTHERREMOVE;
                    }
                }
                break;
            case PAGE_x2: // x2
                if((y==0&& (x==0 || x==1 || x==2))){
                    return HexExample.SHAPE;
                }else if((x==-1&& (y==-1 || y==0 || y==1))){
                    return HexExample.SECOND;
                }
                break;
            case PAGE_X: // X-hex
                if((y==0&& (x==-2 ||x==-1 || x==0 || x==1 || x==2)) ||
                        (x==1&&y==-1) || (x==-1&&y==1) || (x==-2&&y==2) || (x==2&&y==-2)){
                    return HexExample.SHAPE;
                }else {
                    return HexExample.OTHERREMOVE;
                }
                //break;
            case PAGE_x3: // x3 hex
                if((x==-1&&y==1) || (x==0&&y==1) || (x==1&&y==1)){
                    return HexExample.SHAPE;
                }else if((x==-2&& (y==0 || y==1 || y==2))){
                    return HexExample.SECOND;
                }else if((x==0&&y==0) || (x==1&&y==-1) || (x==2&&y==-2)){
                    return HexExample.THIRD;
                }
                break;
        }
        return HexExample.FIXED;
    }

    @Override
    public void render(Canvas canvas) {

        if(hexPage!=page){
            hexPage = page;
            loadPage();
        }
        for (HexExample hex : hexes) {
            hex.render(canvas);
        }
        for (Irenderable ir : renderables) {
            ir.render(canvas);
        }

        canvas.drawCircle(Dimensions.screenWidth/(GameSettings.nrOfUpgrades+1)*(1+hexPage),
                Dimensions.screenHeight-Dimensions.blockSize/5f,Dimensions.blockSize/10f,pageIndicator);
    }

    @Override
    public void update(float deltaTime) {

        GameSettings.moveTimer+=deltaTime;
        if(GameSettings.moveTimer >=1.0f){
            GameSettings.moveTimer = 0.0f;
        }

        for (HexExample hex : hexes) {
            hex.update(deltaTime);
        }

        for (Iupdateable iu : updatables) {
            iu.update(deltaTime);
        }

        if(leaveTriggered){
            if(GameSettings.fadeOutTimer < 1.0f){
                GameSettings.fadeOutTimer += deltaTime*10f;
                if(GameSettings.fadeOutTimer >= 1.0f){
                    GameSettings.fadeOutTimer = 1.0f;
                    stateMachine.setState(StateMachine.StateTreasures);
                }
            }
        }
    }

    @Override
    public void onTouch(MotionEvent event) {
        if(leaveTriggered){
            return;
        }

        for (Itouchable it : touchables) {
            it.onTouch(event);
        }

        if(event.getAction() == MotionEvent.ACTION_UP){
            Sounds.playClick();
            if(event.getX()>Dimensions.screenHalfWidth){
                if(page++ >= GameSettings.nrOfUpgrades-1){
                    page = 0;
                }
            }else{
                if(page-- <= 0){
                    page = GameSettings.nrOfUpgrades-1;
                }
            }
        }
    }

    public void onBackPressed(){
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
            if(page != myPage){
                myPage = page;
                switch (page){
                    case PAGE_4:
                        text = "4-Hex";
                        break;
                    case PAGE_Z:
                        text = "Z-Hex";
                        break;
                    case PAGE_5:
                        text = "5-Hex";
                        break;
                    case PAGE_V:
                        text = "V-Hex";
                        break;
                    case PAGE_U:
                        text = "U-Hex";
                        break;
                    case PAGE_M:
                        text = "M-Hex";
                        break;
                    case PAGE_Y:
                        text = "Y-Hex";
                        break;
                    case PAGE_x2:
                        text = "Hexes x2";
                        break;
                    case PAGE_X:
                        text = "X-Hex";
                        break;
                    case PAGE_x3:
                        text = "Hexes x3";
                        break;
                    case PAGE_T:
                        text = "T-Hex";
                        break;

                }

            }
            canvas.drawText(text, drawX, drawY, paint);
        }
    }

    class Condition implements Irenderable {
        private final Paint paint = new Paint();
        private final Paint paintLocked = new Paint();
        private final Paint paintUnlocked = new Paint();
        final float drawX;
        final float drawY;
        String textLocked = "Locked";
        final String textLocked2 = "Locked";
        String textUnlocked = "Unlocked";
        final String textUnlocked2 = "Unlocked";
        String text = "Condition";
        String subText = "";
        int myPage = -1;

        Condition() {
            drawX = Dimensions.screenHalfWidth;
            float cy = Dimensions.screenHeight - (Dimensions.screenHeight-Dimensions.screenWidth)/4f;

            drawY = cy - Dimensions.blockSize;
            paint.setTypeface(TheFont.typo);
            paint.setTextAlign(Paint.Align.CENTER);
            paint.setColor(Color.GRAY);
            paint.setTextSize(Dimensions.blockSize*0.9f);

            paintLocked.setTypeface(TheFont.typo);
            paintLocked.setTextAlign(Paint.Align.CENTER);
            paintLocked.setColor(Color.RED);
            paintLocked.setTextSize(Dimensions.blockSize*2.2f);

            paintUnlocked.setTypeface(TheFont.typo);
            paintUnlocked.setTextAlign(Paint.Align.CENTER);
            paintUnlocked.setColor(Color.GREEN);
            paintUnlocked.setTextSize(Dimensions.blockSize*1.5f);
        }

        @Override
        public void render(Canvas canvas) {
            if(page != myPage){
                myPage = page;
                text = "Condition "+ page;
                text = "";
                textLocked = "";
                textUnlocked = textUnlocked2;
                subText = "";
                switch (page){
                    case PAGE_4: // 4 Hex
                        if(!Persistence.shape4unlocked){
                            textLocked = textLocked2;
                            text = "Games";
                            textUnlocked = "";
                            subText = Persistence.gamesCounter+" / "+GameSettings.gamesFor4Hex;
                        }
                        break;
                    case PAGE_Z: // z - hex
                        if(!Persistence.shapeZunlocked){
                            textLocked = textLocked2;
                            textUnlocked = "";
                            text = "Hexes";
                            subText = Persistence.hexCounter+" / "+GameSettings.hexesForZHex;
                        }
                        break;
                    case PAGE_5: // 5 Hex
                        if(!Persistence.shape5unlocked){
                            textLocked = textLocked2;
                            textUnlocked = "";
                            text = "Best Skill";
                            subText = Persistence.highskill+" / "+GameSettings.skillFor5Hex;
                        }
                        break;
                    case PAGE_V: //v hex
                        if(!Persistence.shapeVunlocked){
                            textLocked = textLocked2;
                            textUnlocked = "";
                            text = "Best Skill";
                            subText = Persistence.highskill+" / "+GameSettings.skillForVHex;
                        }
                        break;
                    case PAGE_U: // u Hex
                        if(!Persistence.shapeUunlocked){
                            textLocked = textLocked2;
                            textUnlocked = "";
                            text = "Highscore";
                            subText = Persistence.highScore+" / "+GameSettings.highscoreForUHex;
                        }
                        break;
                    case PAGE_M: // m-Hex
                        if(!Persistence.shapeMunlocked){
                            textLocked = textLocked2;
                            textUnlocked = "";
                            text = "V-Hexes";
                            subText = Persistence.shapeVcounter+" / "+GameSettings.vHexesForMHex;
                        }
                        break;
                    case PAGE_Y:
                        if(!Persistence.shapeYunlocked){
                            textLocked = textLocked2;
                            textUnlocked = "";
                            text = "U-Hexes";
                            subText = Persistence.shapeUcounter+" / "+GameSettings.uHexesForYHex;
                        }
                        break;
                    case PAGE_x2: // x2
                        if(!Persistence.x2unlocked){
                            textLocked = textLocked2;
                            textUnlocked = "";
                            text = "Y-Hexes";
                            subText = Persistence.shapeYcounter+" / "+GameSettings.yHexesForx2Hex;
                        }
                        break;
                    case PAGE_X: // X - Hex
                        if(!Persistence.shapeXunlocked){
                            textLocked = textLocked2;
                            textUnlocked = "";
                            text = "Best Skill";
                            subText = Persistence.highskill+" / "+GameSettings.skillForXHex;
                        }
                        break;
                    case PAGE_x3:
                        if(!Persistence.x3unlocked){
                            textLocked = textLocked2;
                            textUnlocked = "";
                            text = "X-Hexes";
                            subText = Persistence.shapeXcounter+" / "+GameSettings.xHexesForx3Hex;
                        }
                        break;
                    case PAGE_T:
                        if(!Persistence.shapeTunlocked){
                            textLocked = textLocked2;
                            textUnlocked = "";
                            text = "Best Skill";
                            subText = Persistence.highskill+" / "+GameSettings.skillForTHex;
                        }
                        break;
                }
            }
            canvas.drawText(text, drawX, drawY, paint);
            canvas.drawText(subText, drawX, drawY+Dimensions.blockSize, paint);
            canvas.drawText(textLocked, drawX, Dimensions.screenHalfHeight+Dimensions.blockSize*0.7f, paintLocked);
            canvas.drawText(textUnlocked, drawX, drawY+Dimensions.blockSize, paintUnlocked);
        }
    }

    class NextPage implements Irenderable {
        private final Paint paint = new Paint();
        final float drawX;
        final float drawY;

        NextPage() {
            drawX = Dimensions.screenWidth - Dimensions.blockSize*0.2f;
            drawY = Dimensions.screenHalfHeight;
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(Dimensions.blockSize*0.1f);
            paint.setStrokeJoin(Paint.Join.ROUND);
            paint.setStrokeCap(Paint.Cap.ROUND);
            paint.setTextAlign(Paint.Align.CENTER);
            paint.setColor(Color.GRAY);
            paint.setTextSize(Dimensions.blockSize);
        }

        @Override
        public void render(Canvas canvas) {
            canvas.drawLine(drawX-Dimensions.blockSize*0.8f,drawY-Dimensions.blockSize*0.8f,drawX,drawY,paint);
            canvas.drawLine(drawX-Dimensions.blockSize*0.8f,drawY+Dimensions.blockSize*0.8f,drawX,drawY,paint);
        }
    }

    class PrevPage implements Irenderable {
        private final Paint paint = new Paint();
        final float drawX;
        final float drawY;

        PrevPage() {
            drawX = Dimensions.blockSize*0.2f;
            drawY = Dimensions.screenHalfHeight;
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(Dimensions.blockSize*0.1f);
            paint.setStrokeJoin(Paint.Join.ROUND);
            paint.setStrokeCap(Paint.Cap.ROUND);
            paint.setTextAlign(Paint.Align.CENTER);
            paint.setColor(Color.GRAY);
            paint.setTextSize(Dimensions.blockSize);
        }

        @Override
        public void render(Canvas canvas) {
            canvas.drawLine(drawX+Dimensions.blockSize*0.8f,drawY-Dimensions.blockSize*0.8f,drawX,drawY,paint);
            canvas.drawLine(drawX+Dimensions.blockSize*0.8f,drawY+Dimensions.blockSize*0.8f,drawX,drawY,paint);
        }
    }
}

