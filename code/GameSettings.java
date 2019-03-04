package de.denniswittich.hex3;

/**
 * Created by Dennis Wittich on 16.02.2017.
 */

public class GameSettings {

    public static final int gamesFor4Hex = 10;
    public static final int hexesForZHex = 20000;
    public static final int skillFor5Hex = 500;
    public static final int skillForVHex = 700;
    public static final int skillForTHex = 850;
    public static final int highscoreForUHex = 1000;
    public static final int vHexesForMHex = 250;
    public static final int uHexesForYHex = 500;
    public static final int skillForXHex = 1000;
    public static final int yHexesForx2Hex = 25;
    public static final int xHexesForx3Hex = 25;

    public static final int hexesForInvertGravity = 50000;
    public static final int hexesForBubbles = 100000;
    public static final int hexesForDarkTheme = 175000;
    public static final int hexesForSpace =250000;
    public static final int hexesForRainbow =350000;
    public static final int mgForFire = 150;

    public static final int skillForDodge = 200;
    public static final int hsDodgeForJump = 40;
    public static final int hsJumpForFly = 40;

    public static final long skillToShowIndicator = 200;

    public static final int skillForCoin = 100;

    public static float hintTimer;
    public static float moveTimer;
    public static float fadeInTimer,fadeOutTimer;

    public final static float lifePerBlock = 0.02f;
    public static boolean gameEnded;
    public static float gameEndTimer;

    public final static int nrOfUpgrades = 11;
    public final static int nrOfMinigames = 3;
    public final static int nrOfMods = 6;

    public static final int nrOfTreasures = nrOfUpgrades+nrOfMinigames+nrOfMods;
}
