package de.denniswittich.hex3.States;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.view.MotionEvent;

import java.util.ArrayList;
import java.util.Random;

import de.denniswittich.hex3.Backgrounds.BackGroundFire;
import de.denniswittich.hex3.ColorInflator;
import de.denniswittich.hex3.Lerpers;
import de.denniswittich.hex3.Sounds;
import de.denniswittich.hex3.TheFont;
import de.denniswittich.hex3.Dimensions;
import de.denniswittich.hex3.GameSettings;
import de.denniswittich.hex3.Hex;
import de.denniswittich.hex3.Interfaces.Irenderable;
import de.denniswittich.hex3.Interfaces.Itouchable;
import de.denniswittich.hex3.Interfaces.Iupdateable;
import de.denniswittich.hex3.Model.HexRemoveTable;
import de.denniswittich.hex3.Persistence;
import de.denniswittich.hex3.Title;
import de.denniswittich.hex3.Tools;

/**
 * Created by Dennis Wittich on 16.02.2017.
 */

public class MatchThree implements Iupdateable, Irenderable, Itouchable {

    public final static byte UPLEFT = 0;
    public final static byte UPRIGHT = 1;
    public final static byte UP = 2;
    public final static byte DOWNLEFT = 4;
    public final static byte DOWNRIGH = 5;
    public final static byte DOWN = 6;

    public static String BIGLETTER = "";

    private final Paint indicatorPaint = new Paint();
    private final Paint gameEndPaint = new Paint();

    private final Hex[][] modelTable = new Hex[40][40];

    private static final float tan30 = (float)Math.tan(Math.PI/6.);

    private final ArrayList<Irenderable> renderables = new ArrayList<>();
    private final ArrayList<Itouchable> touchables = new ArrayList<>();
    private final ArrayList<Iupdateable> updatables = new ArrayList<>();
    private final ArrayList<Hex> hexes = new ArrayList<>();

    private byte command = -1;
    private int combocount = 0;

    private boolean gameStarted;
    private boolean leaveTriggered;

    private float downX;
    private float downY;

    private final BigLetter bigLetter;
    private StateMachine stateMachine;
    private final SkillShower skillShower;
    private final Firework firework;

    private Hex firstSwipeBlock;
    private Hex secondSwipeBlock;
    private float life;
    private int swipes;
    private int score;
    private String gameEndText = "";
    private boolean paused;

    public void link(StateMachine stateMachine) {
        this.stateMachine = stateMachine;
    }

    public MatchThree() {
        paused = false;
        bigLetter = new BigLetter();
        CurrentScore currentScore = new CurrentScore();
        LiveBar liveBar = new LiveBar();
        Title title = new Title();
        skillShower = new SkillShower();
        CoinsIndicator coinsIndicator = new CoinsIndicator();
        firework = new Firework();

        gameEndPaint.setColor(Color.DKGRAY);
        gameEndPaint.setTypeface(TheFont.typo);
        gameEndPaint.setTextSize(Dimensions.blockSize);
        gameEndPaint.setTextAlign(Paint.Align.CENTER);
        gameEndPaint.setStrokeWidth(Dimensions.blockSize/10f);
        gameEndPaint.setStrokeCap(Paint.Cap.ROUND);
        ColorInflator.hcPaints.add(gameEndPaint);

        indicatorPaint.setColor(Color.DKGRAY);
        ColorInflator.hcPaints.add(indicatorPaint);
        indicatorPaint.setAlpha(122);

        updatables.add(currentScore);

        renderables.add(coinsIndicator);
        renderables.add(skillShower);
        renderables.add(currentScore);
        renderables.add(liveBar);
        renderables.add(title);

        touchables.add(liveBar);
    }

    public void init() {
        firework.show = false;
        skillShower.setCurrentSkill();
        firstSwipeBlock = null;
        secondSwipeBlock = null;
        life = 1.0f;
        score = 0;
        swipes = 0;
        boolean isPressed = false;
        leaveTriggered = false;
        GameSettings.hintTimer = 0.0f;
        GameSettings.moveTimer = 1.0f;
        GameSettings.gameEndTimer = 0f;
        GameSettings.gameEnded = false;

        setupGame();
        BIGLETTER = "";
    }

    private void refreshModel(){
        for (int x = 0; x < 40; x++) {
            for (int y = 0; y < 40; y++) {
        modelTable[x][y] = null;
            }
        }
        for (Hex hex : hexes) {
            modelTable[hex.x+20][hex.y+20] = hex;
        }
    }

    private void setupGame(){
        gameStarted = false;
        life = 1.0f;
        GameSettings.moveTimer = 1f;
        score = 0;
        swipes = 0;

        updatables.removeAll(hexes);
        renderables.removeAll(hexes);

        hexes.clear();
        for (int x = -4; x <= 4 ; x++){
            for (int y = -4; y <= 4 ; y++){
                if(x*y>=0){
                    if(Math.abs(x)+Math.abs(y)<=4 ){
                        hexes.add(new Hex(x,y));
                    }
                }else{
                    Math.abs(x);
                    Math.abs(y);
                    hexes.add(new Hex(x,y));
                }
            }
        }

        boolean levelValid = false;
        while (!levelValid){
            for (Hex hex : hexes) {
                if(hex.x*hex.y>=0){
                    if(Math.abs(hex.x)+Math.abs(hex.y)<=4 ){
                        hex.setRandomType();
                    }
                }else{
                    if(Math.max(Math.abs(hex.x),Math.abs(hex.y))<=4 ){
                        hex.setRandomType();
                    }
                }

            }
            while (removeSame(false)){
                dropBlocks();
            }
            int startType = (int)(Math.random()*6);
            for (Hex hex : hexes) {
                if((hex.x == 1 && hex.y ==0)||(hex.x == 0 && hex.y ==1)||(hex.x == 0 && hex.y ==-1)){
                    hex.type = startType;
                    hex.reloadPaints();
                }
            }
            levelValid = !removeSame(false);
            if(!levelValid){
                dropBlocks();
            }
        }
        for (Hex hex : hexes) {
            hex.reloadPaints();
        }
    }

    private boolean removeSame(boolean increaseScore){
        BIGLETTER = "";
        refreshModel();
        HexRemoveTable.setupBlocksToRmv(modelTable,increaseScore);
        if(HexRemoveTable.blocksToMoveUp.isEmpty()){
            if(combocount>Persistence.highestCombo){
                Persistence.highestCombo = combocount;
            }
            combocount = 0;
            return false;
        }else{
            combocount++;
            for (Hex hex : HexRemoveTable.blocksToMoveUp) {
                if(increaseScore){
                    Persistence.hexCounter++;
                    if(HexRemoveTable.foundx3){
                        score+=3;
                        life += GameSettings.lifePerBlock*3;
                    }else if(HexRemoveTable.foundx2){
                        score+=2;
                        life += GameSettings.lifePerBlock*2;
                    }else{
                        score++;
                        life += GameSettings.lifePerBlock;
                    }
                    if(life>1.0f){
                        life = 1.0f;
                    }
                }
                if(Persistence.invertGravity){
                    hex.y = 5;
                    if(hex.x>0){
                        hex.y-=hex.x;
                    }
                }else{
                    hex.y = -5;
                    if(hex.x<0){
                        hex.y-=hex.x;
                    }
                }

                if(increaseScore){
                    hex.setRandomType = true;
                }else{
                    hex.setRandomType();
                }
            }
            if(increaseScore){
                BackGroundFire.FIRE = true;
                if(HexRemoveTable.removesLine){
                    Sounds.playRemove(1);
                }else if(HexRemoveTable.removesSame){
                    Sounds.playRemove(2);
                }else{
                    Sounds.playRemove(0);
                }

                Sounds.vibrate();
            }
            return true;
        }
    }

    private boolean dropBlocks(){
        if(Persistence.invertGravity){
            return floatBlocks();
        }
        boolean movedSmth = false;
        boolean moved = true;

        while(moved) {
            moved = false;
            for (Hex b : hexes) {
                if(b.y == 4 || (b.x==1 && b.y==3) || (b.x==2 && b.y==2) || (b.x==3 && b.y==1) || (b.x==4 && b.y==0)){
                    continue;
                }
                boolean canMove = true;
                for (Hex b2 : hexes) {
                    if(b2.y == (b.y+1) && b2.x==(b.x)){
                        canMove = false;
                        break;
                    }
                }
                if(canMove){
                    b.y ++;
                    moved = true;
                    movedSmth = true;
                }
            }
        }
        return movedSmth;
    }

    private boolean floatBlocks(){
        boolean movedSmth = false;
        boolean moved = true;

        while(moved) {
            moved = false;
            for (Hex b : hexes) {
                if(b.y == -4 || (b.x==-1 && b.y==-3) || (b.x==-2 && b.y==-2) || (b.x==-3 && b.y==-1) || (b.x==-4 && b.y==0)){
                    continue;
                }
                boolean canMove = true;
                for (Hex b2 : hexes) {
                    if(b2.y == (b.y-1) && b2.x==(b.x)){
                        canMove = false;
                        break;
                    }
                }
                if(canMove){
                    b.y --;
                    moved = true;
                    movedSmth = true;
                }
            }
        }
        return movedSmth;
    }

    @Override
    public void render(Canvas canvas) {

        for (Irenderable ir : renderables) {
            ir.render(canvas);
        }

        if(GameSettings.gameEnded){
            firework.render(canvas);
            gameEndPaint.setAlpha((int)(255*GameSettings.gameEndTimer));
            canvas.drawText(gameEndText,Dimensions.screenHalfWidth,Dimensions.screenHalfHeight,gameEndPaint);
        }
        if(paused){
            canvas.drawLine(Dimensions.screenHalfWidth-Dimensions.blockSize,Dimensions.screenHalfHeight-Dimensions.blockSize,
                    Dimensions.screenHalfWidth+Dimensions.blockSize,Dimensions.screenHalfHeight,gameEndPaint);
            canvas.drawLine(Dimensions.screenHalfWidth-Dimensions.blockSize,Dimensions.screenHalfHeight+Dimensions.blockSize,
                    Dimensions.screenHalfWidth+Dimensions.blockSize,Dimensions.screenHalfHeight,gameEndPaint);
            canvas.drawLine(Dimensions.screenHalfWidth-Dimensions.blockSize,Dimensions.screenHalfHeight-Dimensions.blockSize,
                    Dimensions.screenHalfWidth-Dimensions.blockSize,Dimensions.screenHalfHeight+Dimensions.blockSize,gameEndPaint);
        }else{
            if(GameSettings.fadeInTimer>=1f){
                for (Hex hex : hexes) {
                    hex.render(canvas);
                }
            }
        }

        if(!gameStarted && ! GameSettings.gameEnded){
            if(Persistence.highskill<GameSettings.skillToShowIndicator){
               indicatorPaint.setAlpha((int)Lerpers.SmoothBump(0f,255f,GameSettings.hintTimer));
                float ix = Lerpers.Smooth(Dimensions.screenHalfWidth+Hex.sin60*Dimensions.blockSize,Dimensions.screenHalfWidth,GameSettings.hintTimer);
                float iy = Lerpers.Smooth(Dimensions.screenHalfHeight+Hex.cos60*Dimensions.blockSize,Dimensions.screenHalfHeight,GameSettings.hintTimer);
                canvas.drawCircle(ix,iy,Dimensions.blockSize/4f,indicatorPaint);
            }
        }

        bigLetter.render(canvas);
    }

    @Override
    public void update(float deltaTime) {
        if(paused){
            return;
        }
        if(GameSettings.gameEnded){
            skillShower.update(deltaTime);
            firework.update(deltaTime);
            if(GameSettings.gameEndTimer<1.0f){
                GameSettings.gameEndTimer+=deltaTime;
                if(GameSettings.gameEndTimer>1.0f){
                    GameSettings.gameEndTimer =1.0f;
                }
            }
            if(leaveTriggered){
                if(GameSettings.fadeOutTimer < 1.0f){
                    GameSettings.fadeOutTimer += deltaTime*10f;
                    if(GameSettings.fadeOutTimer >= 1.0f){
                        GameSettings.fadeOutTimer = 1.0f;
                        stateMachine.setState(StateMachine.StateMenu);
                    }
                }
                return;
            }
            return;
        }
        if(gameStarted){

            life -= deltaTime/(30f-swipes/5f);
            if(life < 0f){
                if(!Persistence.debugMode){
                    onGameEnd();
                }else{
                    life += 1;
                }
            }
        }else{
            if(GameSettings.hintTimer < 1.0f) {
                GameSettings.hintTimer += deltaTime;
                if(GameSettings.hintTimer >=1.0f) {
                    GameSettings.hintTimer = 0.0f;
                }
            }
        }

        if(GameSettings.fadeInTimer < 1.0f){
            GameSettings.fadeInTimer += deltaTime*10f;
            return;
        }

        if(GameSettings.moveTimer < 1.0f){
            GameSettings.moveTimer+=deltaTime*4f;
            if(GameSettings.moveTimer >=1.0f){
                BIGLETTER = "";
                GameSettings.moveTimer = 1.0f;
                for (Hex hex : hexes) {
                    hex.update(deltaTime);
                }
                if(dropBlocks()){
                    GameSettings.moveTimer = 0.0f;
                    for (Hex hex : hexes) {
                        hex.shrinkAnimation = false;
                    }
                }else if(removeSame(true)){

                    gameStarted = true;
                    firstSwipeBlock = null;
                    secondSwipeBlock = null;
                    GameSettings.moveTimer = 0.0f;
                    for (Hex hex : HexRemoveTable.blocksToMoveUp) {
                        hex.shrinkAnimation = true;
                    }
                }else if(firstSwipeBlock!=null && secondSwipeBlock !=null && !Persistence.godMode){
                    int tmpX = firstSwipeBlock.x;
                    int tmpY = firstSwipeBlock.y;
                    firstSwipeBlock.x = secondSwipeBlock.x;
                    firstSwipeBlock.y = secondSwipeBlock.y;
                    secondSwipeBlock.x = tmpX;
                    secondSwipeBlock.y = tmpY;
                    firstSwipeBlock = null;
                    secondSwipeBlock = null;
                    GameSettings.moveTimer = 0.0f;
                    for (Hex hex : hexes) {
                        hex.shrinkAnimation = false;
                    }
                }else{
                    GameSettings.moveTimer = 1.0f;
                    for (Hex hex : hexes) {
                        hex.shrinkAnimation = false;
                    }
                }
                for (Hex hex : hexes) {
                    hex.update(deltaTime);
                }
            }
        }
        for (Hex hex : hexes) {
            hex.update(deltaTime);
        }

        for (Iupdateable iu : updatables) {
            iu.update(deltaTime);
        }

        if(command!=-1){
            if(swipeCurrent()){
                swipes++;
                GameSettings.moveTimer = 0f;
            }
            command = -1;
        }

        if(leaveTriggered){
            if(GameSettings.fadeOutTimer < 1.0f){
                GameSettings.fadeOutTimer += deltaTime*10f;
                if(GameSettings.fadeOutTimer >= 1.0f){
                    stateMachine.setState(StateMachine.StateMenu);
                }
            }
        }
    }

    private void pause(){
        if(gameStarted&&!GameSettings.gameEnded){
            paused = true;
        }
    }

    private void onGameEnd() {
        paused = false;
        Sounds.playGameEnd();

        if(score < Persistence.skill){
            Persistence.skill = (int)(Persistence.skill * 0.9f + score * 0.1f);
            skillShower.rightPaint.setColor(0xFFEA0000);
        }else if(score>Persistence.skill){
            Persistence.skill = (int)(Persistence.skill * 0.7f + score * 0.3f);
            skillShower.rightPaint.setColor(0xFF4FC900);
        }
        if(Persistence.skill>Persistence.highskill){
            Persistence.highskill = Persistence.skill;
        }
        Persistence.hexCoins+=Persistence.skill/GameSettings.skillForCoin;
        Persistence.gamesCounter++;
        GameSettings.gameEnded = true;

        gameEndPaint.setColor(Color.GRAY);
        gameEndText = "Game Over!";

        if(score > Persistence.highScore){
            Persistence.highScore = score;
            gameEndPaint.setColor(Persistence.darkTheme?Color.LTGRAY:Color.DKGRAY);
            gameEndText = "New Highscore!";
            firework.show = true;
            firework.timer = 1f;
        }
        if(Persistence.checkNewUnlocks()>=0){
            gameEndPaint.setColor(Persistence.darkTheme?Color.LTGRAY:Color.DKGRAY);
            gameEndText = "New Upgrade!";
            firework.show = true;
            firework.timer = 1f;
        }
        if(Persistence.checkNewMiniGames()>=0){
            gameEndPaint.setColor(Persistence.darkTheme?Color.LTGRAY:Color.DKGRAY);
            gameEndText = "New Mini Game!";
            firework.show = true;
            firework.timer = 1f;
        }
        if(Persistence.checkNewMods()>=0){
            gameEndPaint.setColor(Persistence.darkTheme?Color.LTGRAY:Color.DKGRAY);
            gameEndText = "New Mod!";
            firework.show = true;
            firework.timer = 1f;
        }
        Persistence.countTreasures();
        Persistence.save();
    }

    private boolean swipeCurrent() {
        if(GameSettings.moveTimer < 1.0f ){
            return false;
        }
        float nmhd = Dimensions.screenHalfHeight;
        float mhd;
        for (Hex block :hexes) {
            mhd = Math.abs(downX - block.cx) + Math.abs(downY - block.cy);
            if(mhd < nmhd){
                nmhd = mhd;
                firstSwipeBlock = block;
            }
        }
        if(nmhd > Dimensions.blockSize * 0.7f){
            firstSwipeBlock = null ;
            return false;
        }
        switch (command){
            case UPLEFT:
                for (Hex block :hexes) {
                    if(block.y == firstSwipeBlock.y && block.x == firstSwipeBlock.x-1){
                        secondSwipeBlock = block;
                        break;
                    }
                }
                break;
            case DOWNRIGH:
                for (Hex block :hexes) {
                    if(block.y == firstSwipeBlock.y && block.x == firstSwipeBlock.x+1){
                        secondSwipeBlock = block;
                        break;
                    }
                }
                break;
            case DOWN:
                for (Hex block :hexes) {
                    if(block.x == firstSwipeBlock.x && block.y == firstSwipeBlock.y+1){
                        secondSwipeBlock = block;
                        break;
                    }
                }
                break;
            case UP:
                for (Hex block :hexes) {
                    if(block.x == firstSwipeBlock.x && block.y == firstSwipeBlock.y-1){
                        secondSwipeBlock = block;
                        break;
                    }
                }
                break;
            case DOWNLEFT:
                for (Hex block :hexes) {
                    if(block.x == firstSwipeBlock.x-1 && block.y == firstSwipeBlock.y+1){
                        secondSwipeBlock = block;
                        break;
                    }
                }
                break;
            case UPRIGHT:
                for (Hex block :hexes) {
                    if(block.x == firstSwipeBlock.x+1 && block.y == firstSwipeBlock.y-1){
                        secondSwipeBlock = block;
                        break;
                    }
                }
                break;
        }
        if(secondSwipeBlock == null){
            return false;
        }
        int tmpX = firstSwipeBlock.x;
        int tmpY = firstSwipeBlock.y;
        firstSwipeBlock.x = secondSwipeBlock.x;
        firstSwipeBlock.y = secondSwipeBlock.y;
        secondSwipeBlock.x = tmpX;
        secondSwipeBlock.y = tmpY;
        return true;
    }

    @Override
    public void onTouch(MotionEvent event) {
        if(GameSettings.gameEnded){
            if(GameSettings.gameEndTimer==1f && event.getAction() == MotionEvent.ACTION_UP){
                leaveTriggered = true;
            }
            return;
        }
        if(paused && event.getAction() == MotionEvent.ACTION_UP){
            paused = false;
            return;
        }
        for (Itouchable it : touchables) {
            it.onTouch(event);
        }
        if(GameSettings.moveTimer<1f){
            return;
        }
        if(event.getAction()==MotionEvent.ACTION_DOWN){
            downX = event.getX();
            downY = event.getY();
        }else if(event.getAction()==MotionEvent.ACTION_UP){
            float dx = event.getX()-downX;
            float dy = event.getY()-downY;
            if (Math.sqrt(dx*dx + dy*dy)>Dimensions.blockSize*0.7f){
                if(dy>0f){
                    if(dx>tan30*dy){
                        command = DOWNRIGH;
                    }else if (dx<-tan30*dy){
                        command = DOWNLEFT;
                    }else{
                        command = DOWN;
                    }
                }else{
                    if(dx>-tan30*dy){
                        command = UPRIGHT;
                    }else if (dx<tan30*dy){
                        command = UPLEFT;
                    }else{
                        command = UP;
                    }
                }
            }
        }

    }

    public void onBackPressed(){
        if(gameStarted && ! GameSettings.gameEnded && !paused){
            pause();
            return;
        }
        if(!gameStarted){
            leaveTriggered = true;
            return;
        }
        if(GameSettings.gameEndTimer<1.0f){
            if(! GameSettings.gameEnded){
                onGameEnd();
            }
        }else{
            leaveTriggered = true;
        }
    }

    public void onPause() {
        pause();
    }

    //-------------------------------------------------------------


    class SkillShower implements  Irenderable, Iupdateable{

        final Paint leftPaint = new Paint();
        final Paint rightPaint = new Paint();
        long skillToShow;
        float incCountDown = 1.0f;
        final String textLeft = "Skill ";
        String textRight = "";
        final float cy;

        SkillShower(){
            leftPaint.setColor(Color.GRAY);
            leftPaint.setTextSize(Dimensions.blockSize);
            leftPaint.setTextAlign(Paint.Align.RIGHT);
            leftPaint.setTypeface(TheFont.typo);

            rightPaint.setColor(Color.GRAY);
            rightPaint.setTextSize(Dimensions.blockSize);
            rightPaint.setTextAlign(Paint.Align.LEFT);
            rightPaint.setTypeface(TheFont.typo);

            cy = Dimensions.screenHalfHeight + Dimensions.blockSize*1.5f;
        }

        void setCurrentSkill(){
            skillToShow = Persistence.skill;
            textRight = ""+skillToShow;
        }

        @Override
        public void render(Canvas canvas) {
            if(GameSettings.gameEnded){
                if(GameSettings.gameEndTimer>=1f){
                    leftPaint.setAlpha(255);
                    rightPaint.setAlpha(255);
                }else{
                    leftPaint.setAlpha((int)(255f*(GameSettings.gameEndTimer)));
                    rightPaint.setAlpha((int)(255f*(GameSettings.gameEndTimer)));
                }
                canvas.drawText(textLeft,Dimensions.screenHalfWidth,cy,leftPaint);
                canvas.drawText(textRight,Dimensions.screenHalfWidth,cy,rightPaint);
            }
        }

        @Override
        public void update(float deltaTime) {

            if(skillToShow < Persistence.skill){
                incCountDown -= deltaTime*50f;
                if(incCountDown<=0f){
                    incCountDown = 1f;
                    skillToShow++;
                    textRight = " "+skillToShow;

                }
            }else if(skillToShow > Persistence.skill){
                incCountDown -= deltaTime*50f;
                if(incCountDown<=0f){
                    incCountDown = 1f;
                    skillToShow--;
                    textRight = " "+skillToShow;

                }
            }
        }
    }

    class LiveBar implements  Irenderable, Itouchable{
        final float sin60 = (float)Math.sin(Math.PI/3.);
        final float cos60 = (float)Math.cos(Math.PI/3.);

        final Paint paintBG = new Paint();
        final Path pathBG = new Path();
        final Paint paintFILL = new Paint();
        final Path pathFILL = new Path();
        final Paint paintBorder = new Paint();
        final float cy;
        final float outerWidth;
        final float lx;
        final float rx;
        final float radius;

        final float[] innerHSV = new float[3];

        LiveBar(){
            innerHSV[0] = 120f;
            innerHSV[1] = 0.8f;
            innerHSV[2] = 1f;
            outerWidth = Dimensions.blockSize/2f - cos60*Dimensions.blockSize/2f;
            lx = Dimensions.screenHalfWidth - sin60*Dimensions.blockSize*4f - Dimensions.blockSize/2f;
            rx = Dimensions.screenHalfWidth + sin60*Dimensions.blockSize*4f + Dimensions.blockSize/2f;
            cy = Dimensions.screenHeight - (Dimensions.screenHeight-Dimensions.screenWidth)/4f + Dimensions.blockSize/2f;
            radius = Dimensions.blockSize/2f;

            paintBG.setStyle(Paint.Style.FILL);
            paintBorder.setColor(Color.LTGRAY);
            paintBorder.setStyle(Paint.Style.STROKE);
            paintBorder.setStrokeWidth(Dimensions.blockSize/10f);
            paintBorder.setStrokeJoin(Paint.Join.ROUND);
            paintBorder.setStrokeCap(Paint.Cap.ROUND);
            paintFILL.setStyle(Paint.Style.FILL);
            setPaths();

            ColorInflator.lcPaints.add(paintBorder);
        }

        private void setInnerPath() {
            pathFILL.reset();
            pathFILL.moveTo(lx,cy);
            pathFILL.lineTo(lx+outerWidth,cy-radius);
            pathFILL.lineTo(lx+outerWidth+(rx-outerWidth-lx-outerWidth)*life,cy-radius);
            pathFILL.lineTo(lx+(rx-lx)*life,cy);
            pathFILL.lineTo(lx+outerWidth+(rx-outerWidth-lx-outerWidth)*life,cy+radius);
            pathFILL.lineTo(lx+outerWidth,cy+radius);
            pathFILL.close();
        }

        void setPaths(){
            pathBG.reset();
            pathBG.moveTo(lx,cy);
            pathBG.lineTo(lx+outerWidth,cy-radius);
            pathBG.lineTo(rx-outerWidth,cy-radius);
            pathBG.lineTo(rx,cy);
            pathBG.lineTo(rx-outerWidth,cy+radius);
            pathBG.lineTo(lx+outerWidth,cy+radius);
            pathBG.close();
        }

        @Override
        public void render(Canvas canvas) {
            if(GameSettings.gameEnded){
                if(GameSettings.gameEndTimer>=1f){
                    return;
                }else{
                    paintFILL.setAlpha((int)(255*(1f-GameSettings.gameEndTimer)));
                    paintBorder.setAlpha((int)(255*(1f-GameSettings.gameEndTimer)));
                }
            }else{
                paintFILL.setAlpha(255);
                paintBorder.setAlpha(255);
                setInnerPath();
                innerHSV[0] = life*120f;
                paintFILL.setColor(Color.HSVToColor(innerHSV));

            }
            canvas.drawPath(pathFILL, paintFILL);
            canvas.drawPath(pathBG, paintBorder);
            if(!paused&&gameStarted&&!GameSettings.gameEnded){
                canvas.drawLine(Dimensions.screenHalfWidth-radius*0.4f,cy+radius*0.65f,Dimensions.screenHalfWidth-radius*0.4f,cy-radius*0.65f,paintBorder);
                canvas.drawLine(Dimensions.screenHalfWidth+radius*0.4f,cy+radius*0.65f,Dimensions.screenHalfWidth+radius*0.4f,cy-radius*0.65f,paintBorder);
            }
        }

        @Override
        public void onTouch(MotionEvent event) {
            if(event.getAction()==MotionEvent.ACTION_UP){
                if(event.getY()<cy+radius && event.getY()>cy-radius){
                    if(!paused&&gameStarted&&!GameSettings.gameEnded){
                        pause();
                    }
                }
            }
        }
    }

    class BigLetter implements Irenderable{

        final Paint paint = new Paint();
        float size = 0.0f;

        BigLetter(){
            paint.setTypeface(TheFont.typo);
            paint.setColor(Color.BLACK);
            paint.setAlpha(200);
            paint.setTextAlign(Paint.Align.CENTER);

            ColorInflator.vhcPaints.add(paint);
        }

        @Override
        public void render(Canvas canvas) {
            if(GameSettings.moveTimer<1.0f){
                size = Dimensions.blockSize*10f*(1f-GameSettings.moveTimer);
                paint.setTextSize(size);
                canvas.drawText(BIGLETTER,Dimensions.screenHalfWidth,Dimensions.screenHalfHeight+size*0.4f,paint);
            }

        }
    }

    class CurrentScore implements Irenderable, Iupdateable {
        private final Paint paint = new Paint();
        final float drawX;
        final float drawY;
        String text = "0";
        int showscore = -1;
        float tickCountDown = 1f;

        CurrentScore() {
            drawX = Dimensions.screenHalfWidth;
            float cy = Dimensions.screenHeight - (Dimensions.screenHeight-Dimensions.screenWidth)/4f;

            drawY = cy - Dimensions.blockSize*0.15f;
            paint.setTypeface(TheFont.typo);
            paint.setTextAlign(Paint.Align.CENTER);
            paint.setColor(Color.DKGRAY);
            paint.setTextSize(Dimensions.blockSize);

            ColorInflator.hcPaints.add(paint);
        }

        @Override
        public void render(Canvas canvas) {
//            if(score == 0){
//                canvas.drawText("match 3 colors", drawX, drawY, paint);
//            }else{
                canvas.drawText(text, drawX, drawY, paint);
//            }
        }

        @Override
        public void update(float deltaTime) {
            if(showscore < score){
                tickCountDown -= deltaTime*20f;
                if(tickCountDown<=0f){
                    tickCountDown = 1f;
                    showscore++;
                    text = "" + showscore;
                }
            }else if(showscore > score){
                showscore = score;
                text = "" + showscore;
            }
        }
    }

    class CoinsIndicator implements Irenderable{
        final Path coinPath = new Path();
        final Paint coinPaint = new Paint();

        final Paint textPaint = new Paint();
        final float cy;
        long coinsToShow = -1;
        String text = "+0";

        CoinsIndicator(){
            cy = Dimensions.screenHalfHeight+Dimensions.blockSize*4f;
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

            if(GameSettings.gameEnded){
                if(GameSettings.gameEndTimer>=1f){
                    coinPaint.setAlpha(255);
                    textPaint.setAlpha(255);
                }else{
                    coinPaint.setAlpha((int)(255*(GameSettings.gameEndTimer)));
                    textPaint.setAlpha((int)(255*(GameSettings.gameEndTimer)));
                }
            }else{
                return;
            }

            if(coinsToShow!=Persistence.skill/GameSettings.skillForCoin){
                coinsToShow = Persistence.skill/GameSettings.skillForCoin;
                text = "+"+coinsToShow;
            }
            if(coinsToShow == 0){
                return;
            }
            canvas.drawPath(coinPath,coinPaint);
            canvas.drawText(text,Dimensions.screenHalfWidth,cy-Dimensions.blockSize/10f,textPaint);
        }
    }

    class Firework implements Irenderable, Iupdateable{

        final int nrOfBubbles = 25;
        final int nrOfValues = 4*nrOfBubbles;
        final float[] particles = new float[nrOfValues]; // x,y,vx,vy
        final float particleSize;
        float initialSpeed;

        boolean show;

        final Paint paint = new Paint();
        final float[] hsv = new float[3];
        float timer;
        final Random random = new Random();

        Firework(){
            hsv[0] = 120f;
            hsv[1] = 0.8f;
            hsv[2] = 1f;
            particleSize = Dimensions.blockSize/10f;
        }

        @Override
        public void update(float deltaTime) {
            if(!GameSettings.gameEnded){
                return;
            }
            if(!show){
                return;
            }
            timer+=deltaTime;
            if(timer>1f){
                initialSpeed = Dimensions.blockSize*3f*(1f+random.nextFloat());
                timer = 0f;
                hsv[0] = random.nextFloat()*360;
                hsv[1] = 0.8f;
                hsv[2] = 1f;
                paint.setColor(Color.HSVToColor(hsv));

                float cx = Dimensions.screenWidth*0.2f+random.nextFloat()*Dimensions.screenWidth*0.6f;
                float cy = Dimensions.screenHalfHeight/2f+random.nextFloat()*Dimensions.screenHalfHeight/2f;

                for (int i = 0; i < nrOfValues; i+=4) {
                    float angle = random.nextFloat()*(float)Math.PI*2f;
                    particles[i]=cx;
                    particles[i+1]=cy;
                    particles[i+2]=initialSpeed*(float)Math.sin(angle);
                    particles[i+3]=initialSpeed*(float)Math.cos(angle);
                }
            }else{
                for (int i = 0; i < nrOfValues; i+=4) {
                    particles[i]+=particles[i+2]*deltaTime*(0.8f+random.nextFloat()*0.4f);
                    particles[i+1]+=particles[i+3]*deltaTime*(0.8f+random.nextFloat()*0.4f);
                }
            }
        }

        @Override
        public void render(Canvas canvas) {
            if(!GameSettings.gameEnded){
                return;
            }
            if(!show){
                return;
            }
            paint.setAlpha((int)(255f * (1f-timer)));
            for (int i = 0; i < nrOfValues; i+=4) {
                canvas.drawCircle(particles[i],particles[i+1],particleSize,paint);
            }
        }
    }
}

