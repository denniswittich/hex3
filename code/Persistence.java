package de.denniswittich.hex3;

import android.content.SharedPreferences;

/**
 * Created by Dennis Wittich on 17.02.2017.
 */

public class Persistence {
    public static final boolean debugMode = false;
    public static final boolean godMode = false;

    public static SharedPreferences sharedPreferences;

    public static boolean soundOn;
    public static boolean rumbleOn;
    public static long highScore;
    public static long skill;
    public static long highskill;
    public static long hexCounter;
    public static long gamesCounter;
    public static long hexCoins;

    public static long shape4counter;
    public static long shape5counter;
    public static long shapeVcounter;
    public static long shapeUcounter;
    public static long shapeZcounter;
    public static long shapeXcounter;
    public static long shapeYcounter;
    public static long shapeMcounter;
    public static long shapeTcounter;
    public static int treasureCounter;
    public static int highestCombo;

    public static int hsFly;
    public static int hsDodge;
    public static int hsJump;

    public static boolean shape4unlocked;
    public static boolean shape5unlocked;
    public static boolean shapeVunlocked;
    public static boolean shapeUunlocked;
    public static boolean shapeZunlocked;
    public static boolean shapeXunlocked;
    public static boolean shapeYunlocked;
    public static boolean shapeMunlocked;
    public static boolean shapeTunlocked;
    public static boolean x2unlocked;
    public static boolean x3unlocked;

    public static boolean invertGravityUnlocked;
    public static boolean bubblesUnlocked;
    public static boolean spaceUnlocked;
    public static boolean rainbowUnlocked;
    public static boolean fireUnlocked;
    public static boolean darkThemeUnlocked;

    public static boolean mgDodgeUnlocked;
    public static boolean mgJumpUnlocked;
    public static boolean mgFlyUnlocked;

    public static boolean invertGravity;
    public static boolean bubbles;
    public static boolean darkTheme;
    public static boolean rainbow;
    public static boolean space;
    public static boolean fire;
    public static boolean rmvAds;

    public static void load(){
        if(sharedPreferences==null){
            return;
        }
        //sharedPreferences.edit().clear().commit();

        hexCoins = sharedPreferences.getLong("hexCoins",0);
        hexCounter = sharedPreferences.getLong("hexCounter",0);
        skill = sharedPreferences.getLong("skill",0);
        highskill = sharedPreferences.getLong("highskill",0);
        highScore = sharedPreferences.getLong("highScore",0);
        highestCombo = sharedPreferences.getInt("highestCombo",0);
        gamesCounter = sharedPreferences.getLong("gamesCounter",0);
        soundOn = sharedPreferences.getBoolean("soundOn",false);
        rumbleOn = sharedPreferences.getBoolean("rumbleOn",false);

        hsDodge = sharedPreferences.getInt("hsDodge",0);
        hsJump = sharedPreferences.getInt("hsJump",0);
        hsFly = sharedPreferences.getInt("hsFly",0);

        rmvAds = sharedPreferences.getBoolean("rmvAds",false);
        bubbles = sharedPreferences.getBoolean("bubbles",false);
        space = sharedPreferences.getBoolean("space",false);
        fire = sharedPreferences.getBoolean("fire",false);
        invertGravity = sharedPreferences.getBoolean("invertGravity",false);
        darkTheme = sharedPreferences.getBoolean("darkTheme",false);
        rainbow = sharedPreferences.getBoolean("rainbow",false);

        shape4counter = sharedPreferences.getLong("shape4counter",0);
        shape5counter = sharedPreferences.getLong("shape5counter",0);
        shapeVcounter = sharedPreferences.getLong("shapeVcounter",0);
        shapeUcounter = sharedPreferences.getLong("shapeUcounter",0);
        shapeZcounter = sharedPreferences.getLong("shapeZcounter",0);
        shapeXcounter = sharedPreferences.getLong("shapeXcounter",0);
        shapeYcounter = sharedPreferences.getLong("shapeYcounter",0);
        shapeMcounter = sharedPreferences.getLong("shapeMcounter",0);
        shapeTcounter = sharedPreferences.getLong("shapeTcounter",0);
 //       debuging();
        checkGames();
        checkUnlocks();
        checkMods();
        countTreasures();
    }

    private static void debuging() {
//        hexCounter = 399999;
//        hexCoins = 100;
//        shapeMcounter = 24;
//        shapeVcounter = 199;
//        shapeYcounter = 24;
    }

    public static void checkGames(){
        mgDodgeUnlocked = highskill >= GameSettings.skillForDodge || debugMode;
        mgJumpUnlocked = hsDodge >= GameSettings.hsDodgeForJump || debugMode;
        mgFlyUnlocked = hsJump >= GameSettings.hsJumpForFly || debugMode;
    }

    public static void checkMods(){
        invertGravityUnlocked = hexCounter >= GameSettings.hexesForInvertGravity || debugMode;
        bubblesUnlocked = hexCounter >= GameSettings.hexesForBubbles || debugMode;
        spaceUnlocked = hexCounter >= GameSettings.hexesForSpace || debugMode;
        rainbowUnlocked = hexCounter >= GameSettings.hexesForRainbow || debugMode;
        darkThemeUnlocked = hexCounter >= GameSettings.hexesForDarkTheme || debugMode;
        fireUnlocked = (hsJump+hsFly+hsDodge) >= GameSettings.mgForFire || debugMode;
    }

    public static void checkUnlocks() {
        shape4unlocked = gamesCounter >=GameSettings.gamesFor4Hex || debugMode;
        shapeZunlocked = hexCounter >=GameSettings.hexesForZHex || debugMode;
        shape5unlocked = highskill >=GameSettings.skillFor5Hex || debugMode;
        shapeVunlocked = highskill >=GameSettings.skillForVHex || debugMode;
        shapeUunlocked = highScore >=GameSettings.highscoreForUHex  || debugMode;
        shapeMunlocked = shapeVcounter >=GameSettings.vHexesForMHex || debugMode;
        shapeTunlocked = highskill >=GameSettings.skillForTHex || debugMode;
        shapeYunlocked = shapeUcounter >=GameSettings.uHexesForYHex|| debugMode;
        shapeXunlocked = highskill >=GameSettings.skillForXHex || debugMode;
        x2unlocked = shapeYcounter >=GameSettings.yHexesForx2Hex || debugMode;
        x3unlocked = shapeXcounter >=GameSettings.xHexesForx3Hex || debugMode;

    }

    public static int checkNewUnlocks(){

        int newUpgrade = -1;
        if(!shape4unlocked && gamesCounter >=GameSettings.gamesFor4Hex){
            shape4unlocked = true;
            newUpgrade = 0;
        }
        if(!shapeZunlocked && hexCounter >=GameSettings.hexesForZHex){
            shapeZunlocked = true;
            newUpgrade = 1;
        }
        if(!shape5unlocked && highskill >=GameSettings.skillFor5Hex){
            shape5unlocked = true;
            newUpgrade = 2;
        }
        if(!shapeVunlocked && highskill >=GameSettings.skillForVHex){
            shapeVunlocked = true;
            newUpgrade = 3;
        }
        if(!shapeUunlocked && highScore >=GameSettings.highscoreForUHex ){
            shapeUunlocked = true;
            newUpgrade = 4;
        }
        if(!shapeMunlocked &&shapeVcounter >=GameSettings.vHexesForMHex){
            shapeMunlocked = true;
            newUpgrade = 5;
        }
        if(!shapeYunlocked && shapeUcounter >=GameSettings.uHexesForYHex){
            shapeYunlocked = true;
            newUpgrade = 6;
        }
        if(!shapeXunlocked && highskill >=GameSettings.skillForXHex ){
            shapeXunlocked = true;
            newUpgrade = 7;
        }
        if(!x2unlocked && shapeYcounter >=GameSettings.yHexesForx2Hex){
            x2unlocked = true;
            newUpgrade = 8;
        }
        if(!x3unlocked && shapeXcounter >=GameSettings.xHexesForx3Hex){
            x3unlocked = true;
            newUpgrade = 9;
        }
        if(!shapeTunlocked && highskill >=GameSettings.skillForTHex ){
            shapeTunlocked = true;
            newUpgrade = 10;
        }

        return newUpgrade;
    }

    public static int checkNewMiniGames(){

        int newUpgrade = -1;

        if(!mgDodgeUnlocked && highskill >= GameSettings.skillForDodge ){
            mgDodgeUnlocked = true;
            newUpgrade = 1;
        }
        if(!mgJumpUnlocked && hsDodge >= GameSettings.hsDodgeForJump){
            mgJumpUnlocked = true;
            newUpgrade = 2;
        }
        if(!mgFlyUnlocked && hsJump >= GameSettings.hsJumpForFly ){
            mgFlyUnlocked = true;
            newUpgrade = 3;
        }
        return newUpgrade;
    }

    public static int checkNewMods(){

        int newUpgrade = -1;
        if(!darkThemeUnlocked && hexCounter >= GameSettings.hexesForDarkTheme){
            darkThemeUnlocked = true;
            newUpgrade = 0;
        }
        if(!invertGravityUnlocked && hexCounter >= GameSettings.hexesForInvertGravity){
            invertGravityUnlocked = true;
            newUpgrade = 1;
        }
        if(!bubblesUnlocked && hexCounter >= GameSettings.hexesForBubbles){
            bubblesUnlocked = true;
            newUpgrade = 2;
        }
        if(!spaceUnlocked && hexCounter >= GameSettings.hexesForSpace){
            spaceUnlocked = true;
            newUpgrade = 3;
        }
        if(!fireUnlocked && (hsJump+hsFly+hsDodge) >= GameSettings.mgForFire){
            fireUnlocked = true;
            newUpgrade = 4;
        }
        if(!rainbowUnlocked && hexCounter >= GameSettings.hexesForRainbow){
            rainbowUnlocked = true;
            newUpgrade = 5;
        }
        return newUpgrade;
    }

    public static void countTreasures(){
        treasureCounter = 0;
        if(shape4unlocked){
            treasureCounter++;
        }
        if(shapeZunlocked){
            treasureCounter++;
        }
        if(shape5unlocked){
            treasureCounter++;
        }
        if(shapeVunlocked){
            treasureCounter++;
        }
        if(shapeUunlocked){
            treasureCounter++;
        }
        if(shapeMunlocked){
            treasureCounter++;
        }
        if(shapeYunlocked){
            treasureCounter++;
        }
        if(shapeXunlocked){
            treasureCounter++;
        }
        if(shapeTunlocked){
            treasureCounter++;
        }
        if(x2unlocked){
            treasureCounter++;
        }
        if(x3unlocked){
            treasureCounter++;
        }

        if(mgFlyUnlocked){
            treasureCounter++;
        }
        if(mgDodgeUnlocked){
            treasureCounter++;
        }
        if(mgJumpUnlocked){
            treasureCounter++;
        }

        if(darkThemeUnlocked){
            treasureCounter++;
        }
        if(rainbowUnlocked){
            treasureCounter++;
        }
        if(fireUnlocked){
            treasureCounter++;
        }
        if(bubblesUnlocked){
            treasureCounter++;
        }
        if(spaceUnlocked){
            treasureCounter++;
        }
        if(invertGravityUnlocked){
            treasureCounter++;
        }
    }

    public static void save(){
        if(sharedPreferences==null){
            return;
        }
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putLong("hexCoins", hexCoins);
        editor.putLong("hexCounter", hexCounter);
        editor.putLong("highScore", highScore);
        editor.putLong("skill", skill);
        editor.putLong("highskill", highskill);
        editor.putInt("highestCombo", highestCombo);
        editor.putLong("gamesCounter", gamesCounter);
        editor.putBoolean("soundOn",soundOn);
        editor.putBoolean("rumbleOn",rumbleOn);

        editor.putInt("hsDodge", hsDodge);
        editor.putInt("hsJump", hsJump);
        editor.putInt("hsFly", hsFly);

        editor.putBoolean("invertGravity",invertGravity);
        editor.putBoolean("rmvAds",rmvAds);
        editor.putBoolean("bubbles",bubbles);
        editor.putBoolean("space",space);
        editor.putBoolean("fire",fire);
        editor.putBoolean("darkTheme",darkTheme);
        editor.putBoolean("rainbow",rainbow);

        editor.putLong("shape4counter",shape4counter);
        editor.putLong("shape5counter",shape5counter);
        editor.putLong("shapeVcounter",shapeVcounter);
        editor.putLong("shapeUcounter",shapeUcounter);
        editor.putLong("shapeZcounter",shapeZcounter);
        editor.putLong("shapeXcounter",shapeXcounter);
        editor.putLong("shapeYcounter",shapeYcounter);
        editor.putLong("shapeMcounter",shapeMcounter);
        editor.putLong("shapeTcounter",shapeTcounter);

        editor.apply();
    }

}
