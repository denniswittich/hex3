package de.denniswittich.hex3;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import de.denniswittich.hex3.States.MGS.MgDodge;
import de.denniswittich.hex3.States.MGS.MgFly;
import de.denniswittich.hex3.States.MGS.MgJump;
import de.denniswittich.hex3.States.MatchThree;
import de.denniswittich.hex3.States.Menu;
import de.denniswittich.hex3.States.MiniGames;
import de.denniswittich.hex3.States.Mods;
import de.denniswittich.hex3.States.StateMachine;
import de.denniswittich.hex3.States.Stats;
import de.denniswittich.hex3.States.Treasures;
import de.denniswittich.hex3.States.Upgrades;

/**
 * Created by Dennis Wittich on 16.02.2017.
 */

public class MainActivity extends Activity implements View.OnTouchListener, SurfaceHolder.Callback {

    private SurfaceHolder mSurfaceHolder;

    private StateMachine stateMachine;

    private boolean gameThreadRunning;
    private Thread gameThread;

    private boolean setupDone;
    public static Vibrator vibrator;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        vibrator =  (Vibrator)getSystemService(VIBRATOR_SERVICE);
        this.setVolumeControlStream(AudioManager.STREAM_MUSIC);


        setupDone = false;
        SurfaceView mGameView = new SurfaceView(this);
        mGameView.setOnTouchListener(this);
        mSurfaceHolder = mGameView.getHolder();
        mSurfaceHolder.addCallback(this);
        setContentView(mGameView);

        Persistence.sharedPreferences = getPreferences(Context.MODE_PRIVATE);
        Persistence.load();
        S.loadStrings(getResources());
    }


    private void setup(){
        Dimensions.screenHeight = Math.max(getResources().getDisplayMetrics().heightPixels, getResources().getDisplayMetrics().widthPixels);
        Dimensions.screenWidth = Math.min(getResources().getDisplayMetrics().heightPixels, getResources().getDisplayMetrics().widthPixels);
        Dimensions.setup();

        //Load prefs
        TheFont.loadFont(getAssets());
        Sounds.setup(this);
        //Sounds.setup(this);

        stateMachine = new StateMachine();
        Menu menu = new Menu();
        MatchThree matchThree = new MatchThree();
        Upgrades upgrades = new Upgrades();
        Stats stats = new Stats();
        Mods mods = new Mods();
        Treasures treasures = new Treasures();

        MiniGames miniGames = new MiniGames();
        MgDodge mgDodge = new MgDodge();
        MgFly mgFly = new MgFly();
        MgJump mgJump = new MgJump();

        stateMachine.link(menu, matchThree, upgrades, stats, treasures, mods, miniGames, mgDodge, mgFly, mgJump);
        menu.link(stateMachine,this);
        stats.link(stateMachine);
        matchThree.link(stateMachine);
        upgrades.link(stateMachine);
        treasures.link(stateMachine,this);
        mods.link(stateMachine);

        miniGames.link(stateMachine,this);
        mgDodge.link(stateMachine);
        mgJump.link(stateMachine);
        mgFly.link(stateMachine);

        startGameThread();
        stateMachine.setState(StateMachine.StateMenu);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if(stateMachine!=null){
            stateMachine.onTouch(event);
            return true;
        }else{
            return false;
        }
    }

    private void gameLoop(){
        long lastRenderingMillis = System.currentTimeMillis();
        Canvas c;
        while(gameThreadRunning){
            try {
                c = mSurfaceHolder.lockCanvas();
                if(c==null){
                    continue;
                }
                synchronized (mSurfaceHolder) {
                    float deltaTime = (System.currentTimeMillis() - lastRenderingMillis) / 1000.0f;
                    lastRenderingMillis = System.currentTimeMillis();
                    stateMachine.update(deltaTime);
                    stateMachine.render(c);
                }
                mSurfaceHolder.unlockCanvasAndPost(c);


            }catch (Exception e) {
                //throw e;
            }
        }
    }

    private void startGameThread(){
        gameThreadRunning = true;
        gameThread = new Thread(new Runnable(){
            public void run() {
                gameLoop();
            }
        }, "Rendering Thread");
        gameThread.setPriority(Thread.MAX_PRIORITY);
        gameThread.start();
    }

    private void stopGameThread(){
        if(gameThreadRunning){
            gameThreadRunning = false;
            try {
                gameThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if(!setupDone){
            setupDone = true;
            Canvas c = null;
            try {
                c = mSurfaceHolder.lockCanvas();
                synchronized (mSurfaceHolder) {
                    c.drawColor(Color.WHITE);
                }
            }catch (Exception e) {
                //throw e;
                //Log.d("EXC. IN RENDERING T.",e.toString());
            }finally {
                mSurfaceHolder.unlockCanvasAndPost(c);
            }
            Thread setupThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    setup();
                }
            });
            setupThread.start();
        }else{
            startGameThread();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        stopGameThread();
    }

    @Override
    public void onBackPressed() {
        if(stateMachine!=null){
            if(!stateMachine.onBackPressed()){
                super.onBackPressed();
            }
        }
    }

    @Override
    protected void onPause() {
        Persistence.save();
        stateMachine.onPause();
        super.onPause();
    }

}