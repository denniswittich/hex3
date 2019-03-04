package de.denniswittich.hex3.States;

import android.graphics.Canvas;
import android.graphics.Color;
import android.view.MotionEvent;

import de.denniswittich.hex3.Backgrounds.BackGroundBubbles;
import de.denniswittich.hex3.Backgrounds.BackGroundFire;
import de.denniswittich.hex3.Backgrounds.BackGroundRainbow;
import de.denniswittich.hex3.Backgrounds.BackGroundSpace;
import de.denniswittich.hex3.GameSettings;
import de.denniswittich.hex3.Interfaces.Irenderable;
import de.denniswittich.hex3.Interfaces.Itouchable;
import de.denniswittich.hex3.Interfaces.Iupdateable;
import de.denniswittich.hex3.Persistence;
import de.denniswittich.hex3.States.MGS.MgDodge;
import de.denniswittich.hex3.States.MGS.MgFly;
import de.denniswittich.hex3.States.MGS.MgJump;
import de.denniswittich.hex3.States.Menu;
import de.denniswittich.hex3.States.MatchThree;
import de.denniswittich.hex3.States.Mods;
import de.denniswittich.hex3.States.Stats;
import de.denniswittich.hex3.States.Treasures;
import de.denniswittich.hex3.States.Upgrades;
import de.denniswittich.hex3.Title;

/**
 * Created by Dennis Wittich on 16.02.2017.
 */

public class StateMachine implements Irenderable,Iupdateable,Itouchable {
    public final static int StateMenu = 0;
    public final static int StateMatchThree = 1;
    public final static int StateUpgrades = 2;
    public final static int StateStats = 3;
    public final static int StateTreasures = 4;
    public final static int StateMods = 5;
    public final static int StateMiniGames = 6;
    public final static int StateMgPulse = 7;
    public final static int StateMgDodge = 8;
    public final static int StateMgJump = 9;
    public final static int StateMgTap = 10;
    public final static int StateMgFly = 11;
    private int state = -1;

    private Menu menu;
    private MatchThree matchThree;
    private Upgrades upgrades;
    private Stats stats;
    private Treasures treasures;
    private Mods mods;

    private MiniGames miniGames;
    private MgDodge mgDodge;
    private MgFly mgFly;
    private MgJump mgJump;

    private BackGroundBubbles backGroundBubbles;
    private BackGroundSpace backGroundSpace;
    private BackGroundFire backGroundFire;
    private BackGroundRainbow backGroundRainbow;

    private Title title;

    public void link(Menu menu, MatchThree matchThree, Upgrades upgrades, Stats stats, Treasures treasures, Mods mods,
                     MiniGames miniGames, MgDodge mgDodge, MgFly mgFly, MgJump mgJump){
        this.menu = menu;
        this.matchThree = matchThree;
        this.upgrades = upgrades;
        this.stats = stats;
        this.treasures = treasures;
        this.mods = mods;

        this.miniGames = miniGames;
        this.mgDodge = mgDodge;
        this.mgFly = mgFly;
        this.mgJump = mgJump;

        this.backGroundBubbles = new BackGroundBubbles();
        this.backGroundSpace = new BackGroundSpace();
        this.backGroundFire = new BackGroundFire();
        this.backGroundRainbow = new BackGroundRainbow();

        this.title = new Title();
    }

    public void setState(int newState){
        if(state == newState){
            return;
        }
        GameSettings.fadeInTimer = 0f;
        GameSettings.fadeOutTimer = 0f;
        switch (newState){
            case StateMenu:
                menu.init();
                break;
            case StateMatchThree:
                matchThree.init();
                break;
            case StateUpgrades:
                upgrades.init();
                break;
            case StateStats:
                stats.init();
                break;
            case StateTreasures:
                treasures.init();
                break;
            case StateMods:
                mods.init();
                break;
            case StateMiniGames:
                miniGames.init();
                break;
            case StateMgDodge:
                mgDodge.init();
                break;
            case StateMgFly:
                mgFly.init();
                break;
            case StateMgJump:
                mgJump.init();
                break;
        }
        state = newState;
    }

    @Override
    public void render(Canvas canvas) {
        if(Persistence.rainbow){
            backGroundRainbow.render(canvas);
        }else{
            canvas.drawColor(Persistence.darkTheme?Color.BLACK:Color.WHITE);
        }
        if(Persistence.space){
            backGroundSpace.render(canvas);
        }
        if(Persistence.bubbles){
            backGroundBubbles.render(canvas);
        }

        switch (state){
            case StateMenu:
                menu.render(canvas);
                break;
            case StateMatchThree:
                matchThree.render(canvas);
                break;
            case StateUpgrades:
                upgrades.render(canvas);
                break;
            case StateStats:
                stats.render(canvas);
                break;
            case StateTreasures:
                treasures.render(canvas);
                break;
            case StateMods:
                mods.render(canvas);
                break;
            case StateMiniGames:
                miniGames.render(canvas);
                break;
            case StateMgDodge:
                mgDodge.render(canvas);
                break;
            case StateMgFly:
                mgFly.render(canvas);
                break;
            case StateMgJump:
                mgJump.render(canvas);
                break;
        }
        if(Persistence.fire && (state == StateMatchThree || state == StateMgFly || state == StateMgDodge || state == StateMgJump)){
            backGroundFire.render(canvas);
        }
        if(GameSettings.fadeInTimer < 1.0f){
            if(Persistence.darkTheme){
                canvas.drawColor(Color.argb((int)((1f-GameSettings.fadeInTimer)*255f),0,0,0));
            }else{
                canvas.drawColor(Color.argb((int)((1f-GameSettings.fadeInTimer)*255f),255,255,255));
            }
        }else if(GameSettings.fadeOutTimer > 0.0f){
            if(Persistence.darkTheme){
                canvas.drawColor(Color.argb((int)(GameSettings.fadeOutTimer*255f),0,0,0));
            }else{
                canvas.drawColor(Color.argb((int)(GameSettings.fadeOutTimer*255f),255,255,255));
            }
        }
        title.render(canvas);
    }

    @Override
    public void update(float deltaTime) {
        if(Persistence.rainbow){
            backGroundRainbow.update(deltaTime);
        }
        if(Persistence.bubbles){
            backGroundBubbles.update(deltaTime);
        }
        if(Persistence.space){
            backGroundSpace.update(deltaTime);
        }
        if(Persistence.fire && (state == StateMatchThree || state == StateMgFly || state == StateMgDodge || state == StateMgJump)){
            backGroundFire.update(deltaTime);
        }

        if(GameSettings.fadeInTimer < 1.0f){
            GameSettings.fadeInTimer += deltaTime*10f;
        }
        switch (state){
            case StateMenu:
                menu.update(deltaTime);
                break;
            case StateMatchThree:
                matchThree.update(deltaTime);
                break;
            case StateUpgrades:
                upgrades.update(deltaTime);
                break;
            case StateStats:
                stats.update(deltaTime);
                break;
            case StateTreasures:
                treasures.update(deltaTime);
                break;
            case StateMods:
                mods.update(deltaTime);
                break;
            case StateMiniGames:
                miniGames.update(deltaTime);
                break;
            case StateMgDodge:
                mgDodge.update(deltaTime);
                break;
            case StateMgFly:
                mgFly.update(deltaTime);
                break;
            case StateMgJump:
                mgJump.update(deltaTime);
                break;
        }
    }

    @Override
    public void onTouch(MotionEvent event) {
        if(GameSettings.fadeInTimer<1f || GameSettings.fadeOutTimer>0f){
            return;
        }
        switch (state){
            case StateMenu:
                menu.onTouch(event);
                break;
            case StateMatchThree:
                matchThree.onTouch(event);
                break;
            case StateUpgrades:
                upgrades.onTouch(event);
                break;
            case StateStats:
                stats.onTouch(event);
                break;
            case StateTreasures:
                treasures.onTouch(event);
                break;
            case StateMods:
                mods.onTouch(event);
                break;
            case StateMiniGames:
                miniGames.onTouch(event);
                break;
            case StateMgDodge:
                mgDodge.onTouch(event);
                break;
            case StateMgFly:
                mgFly.onTouch(event);
                break;
            case StateMgJump:
                mgJump.onTouch(event);
                break;
        }
    }

    public boolean onBackPressed() {
        switch (state){
            case StateMenu:
                return (menu.onBackPressed());
            case StateMatchThree:
                matchThree.onBackPressed();
                break;
            case StateUpgrades:
                upgrades.onBackPressed();
                break;
            case StateStats:
                stats.onBackPressed();
                break;
            case StateTreasures:
                treasures.onBackPressed();
                break;
            case StateMods:
                mods.onBackPressed();
                break;
            case StateMiniGames:
                miniGames.onBackPressed();
                break;
            case StateMgDodge:
                mgDodge.onBackPressed();
                break;
            case StateMgFly:
                mgFly.onBackPressed();
                break;
            case StateMgJump:
                mgJump.onBackPressed();
                break;
        }
        return true;
    }

    public void onPause() {
        switch (state){
            case StateMatchThree:
                matchThree.onPause();
                break;
        }
    }
}
