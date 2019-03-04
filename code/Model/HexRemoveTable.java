package de.denniswittich.hex3.Model;

import java.util.HashSet;
import java.util.Set;

import de.denniswittich.hex3.Hex;
import de.denniswittich.hex3.Persistence;
import de.denniswittich.hex3.States.MatchThree;

/**
 * Created by Dennis Wittich on 05.03.2017.
 */

public class HexRemoveTable {
    public static final Set<Hex> blocksToMoveUp = new HashSet<>();
    private static Hex[][] hexes;
    private static final boolean[] typeRemoved = new boolean[7];
    public static boolean foundx2 = false;
    public static boolean foundx3 = false;
    private static boolean found4 = false;
    private static boolean foundZ = false;
    private static boolean foundT = false;
    private static boolean found5 = false;
    private static boolean foundV = false;
    private static boolean foundU = false;
    private static boolean foundM = false;
    private static boolean foundY = false;
    private static boolean foundX = false;

    public static boolean removesLine;
    public static boolean removesSame;

    public static void setupBlocksToRmv(Hex[][] hexesIn, boolean increaseShapeCountersIn) {
        blocksToMoveUp.clear();
        hexes = hexesIn;
        boolean increaseShapeCounters = increaseShapeCountersIn;

        removesLine = false;
        removesSame = false;

        foundx2 = false;
        found4 = false;
        foundZ = false;
        foundT = false;
        found5 = false;
        foundV = false;
        foundU = false;
        foundM = false;
        foundY = false;
        foundX = false;

        for (int i = 0; i < 7; i++) {
            typeRemoved[i] = false;
        }

        for (int x = 16; x < 25; x++) {
            for (int y = 16; y < 25; y++) {
                if (hexes[x][y] == null) {
                    continue;
                }
                remove3(x, y);
                if (Persistence.shape4unlocked) {
                    remove4(x, y);
                }
                if (Persistence.shapeZunlocked) {
                    removeZ(x, y);
                }
                if (Persistence.shape5unlocked) {
                    remove5(x, y);

                }
                if (Persistence.shapeVunlocked) {
                    removeV(x, y);
                }
                if (Persistence.shapeUunlocked) {
                    removeU(x, y);
                }
                if (Persistence.shapeMunlocked) {
                    removeM(x, y);
                }
                if (Persistence.shapeYunlocked) {
                    removeY(x, y);
                }
                if (Persistence.shapeXunlocked) {
                    removeX(x, y);
                }
                if (Persistence.shapeTunlocked) {
                    removeT(x, y);
                }
            }
        }
        int colorsRemoved = 0;
        for (int i = 0; i < 7; i++) {
            if (typeRemoved[i]) {
                colorsRemoved++;
            }
        }

        foundx2 = Persistence.x2unlocked && colorsRemoved >= 2;
        foundx3 = Persistence.x3unlocked && colorsRemoved >= 3;
        if (increaseShapeCounters) {
            if (foundx3) {
                MatchThree.BIGLETTER = "x3";
            } else if (foundx2) {
                MatchThree.BIGLETTER = "x2";
            } else if (foundX) {
                MatchThree.BIGLETTER = "X";
                Persistence.shapeXcounter++;
            } else if (foundY) {
                MatchThree.BIGLETTER = "Y";
                Persistence.shapeYcounter++;
            } else if (foundM) {
                MatchThree.BIGLETTER = "M";
                Persistence.shapeMcounter++;
            } else if (foundT) {
                MatchThree.BIGLETTER = "T";
                Persistence.shapeTcounter++;
            } else if (foundU) {
                MatchThree.BIGLETTER = "U";
                Persistence.shapeUcounter++;
            } else if (foundV) {
                MatchThree.BIGLETTER = "V";
                Persistence.shapeVcounter++;
            } else if (found5) {
                MatchThree.BIGLETTER = "5";
                Persistence.shape5counter++;
            } else if (foundZ) {
                MatchThree.BIGLETTER = "Z";
                Persistence.shapeZcounter++;
            } else if (found4) {
                MatchThree.BIGLETTER = "4";
                Persistence.shape4counter++;
            }
        }
    }

    private static void remove3(int x, int y) {
        if (isSameType(x, y, 0, 1) && isSameType(x, y, 0, 2)) {
            blocksToMoveUp.add(hexes[x][y]);
            blocksToMoveUp.add(hexes[x][y + 1]);
            blocksToMoveUp.add(hexes[x][y + 2]);
            typeRemoved[hexes[x][y].type] = true;
        }
        if (isSameType(x, y, 1, 0) && isSameType(x, y, 2, 0)) {
            blocksToMoveUp.add(hexes[x][y]);
            blocksToMoveUp.add(hexes[x + 1][y]);
            blocksToMoveUp.add(hexes[x + 2][y]);
            typeRemoved[hexes[x][y].type] = true;
        }
        if (isSameType(x, y, -1, 1) && isSameType(x, y, -2, 2)) {
            blocksToMoveUp.add(hexes[x][y]);
            blocksToMoveUp.add(hexes[x - 1][y + 1]);
            blocksToMoveUp.add(hexes[x - 2][y + 2]);
            typeRemoved[hexes[x][y].type] = true;
        }
    }

    private static void remove4(int x, int y) {
        if (isSameType(x, y, 0, 1) && isSameType(x, y, 0, 2) && isSameType(x, y, 0, 3)) {
            removeRecursive(x, y, MatchThree.UP);
            removeRecursive(x, y, MatchThree.DOWN);
            found4 = true;
            typeRemoved[hexes[x][y].type] = true;
        }
        if (isSameType(x, y, 1, 0) && isSameType(x, y, 2, 0) && isSameType(x, y, 3, 0)) {
            removeRecursive(x, y, MatchThree.UPLEFT);
            removeRecursive(x, y, MatchThree.DOWNRIGH);
            found4 = true;
            typeRemoved[hexes[x][y].type] = true;
        }
        if (isSameType(x, y, -1, 1) && isSameType(x, y, -2, 2) && isSameType(x, y, -3, 3)) {
            removeRecursive(x, y, MatchThree.DOWNLEFT);
            removeRecursive(x, y, MatchThree.UPRIGHT);
            found4 = true;
            typeRemoved[hexes[x][y].type] = true;
        }
    }

    private static void remove5(int x, int y) {
        if (isSameType(x, y, 0, 1) && isSameType(x, y, 0, 2) && isSameType(x, y, 0, 3) && isSameType(x, y, 0, 4)) {
            removeRecursive(x, y, MatchThree.UP);
            removeRecursive(x, y, MatchThree.DOWN);
            removeType(hexes[x][y].type);
            found5 = true;
            typeRemoved[hexes[x][y].type] = true;
        }
        if (isSameType(x, y, 1, 0) && isSameType(x, y, 2, 0) && isSameType(x, y, 3, 0) && isSameType(x, y, 4, 0)) {
            removeRecursive(x, y, MatchThree.UPLEFT);
            removeRecursive(x, y, MatchThree.DOWNRIGH);
            removeType(hexes[x][y].type);
            found5 = true;
            typeRemoved[hexes[x][y].type] = true;
        }
        if (isSameType(x, y, -1, 1) && isSameType(x, y, -2, 2) && isSameType(x, y, -3, 3) && isSameType(x, y, -4, 4)) {
            removeRecursive(x, y, MatchThree.DOWNLEFT);
            removeRecursive(x, y, MatchThree.UPRIGHT);
            removeType(hexes[x][y].type);
            found5 = true;
            typeRemoved[hexes[x][y].type] = true;
        }
    }

    private static void removeV(int x, int y) {
        if (isSameType(x, y, 0, 1) && isSameType(x, y, 0, 2) && isSameType(x, y, 1, 0) && isSameType(x, y, 2, 0)) {
            removeRecursive(x, y, MatchThree.DOWNRIGH);
            removeRecursive(x, y, MatchThree.DOWN);
            foundV = true;
            typeRemoved[hexes[x][y].type] = true;
        }
        if (isSameType(x, y, 0, 1) && isSameType(x, y, 0, 2) && isSameType(x, y, -1, 1) && isSameType(x, y, -2, 2)) {
            removeRecursive(x, y, MatchThree.DOWN);
            removeRecursive(x, y, MatchThree.DOWNLEFT);
            foundV = true;
            typeRemoved[hexes[x][y].type] = true;
        }
        if (isSameType(x, y, 0, -1) && isSameType(x, y, 0, -2) && isSameType(x, y, -1, 0) && isSameType(x, y, -2, 0)) {
            removeRecursive(x, y, MatchThree.UPLEFT);
            removeRecursive(x, y, MatchThree.UP);
            foundV = true;
            typeRemoved[hexes[x][y].type] = true;
        }
        if (isSameType(x, y, -1, 0) && isSameType(x, y, -2, 0) && isSameType(x, y, -1, 1) && isSameType(x, y, -2, 2)) {
            removeRecursive(x, y, MatchThree.DOWNLEFT);
            removeRecursive(x, y, MatchThree.UPLEFT);
            foundV = true;
            typeRemoved[hexes[x][y].type] = true;
        }
        if (isSameType(x, y, 1, -1) && isSameType(x, y, 2, -2) && isSameType(x, y, 1, 0) && isSameType(x, y, 2, 0)) {
            removeRecursive(x, y, MatchThree.DOWNRIGH);
            removeRecursive(x, y, MatchThree.UPRIGHT);
            foundV = true;
            typeRemoved[hexes[x][y].type] = true;
        }
        if (isSameType(x, y, 1, -1) && isSameType(x, y, 2, -2) && isSameType(x, y, 0, -1) && isSameType(x, y, 0, -2)) {
            removeRecursive(x, y, MatchThree.UP);
            removeRecursive(x, y, MatchThree.UPRIGHT);
            foundV = true;
            typeRemoved[hexes[x][y].type] = true;
        }
    }

    private static void removeU(int x, int y) {
        if (isSameType(x, y, -1, 1) && isSameType(x, y, -2, 2) && isSameType(x, y, 1, 0) && isSameType(x, y, 2, 0)) {
            removeRecursive(x, y, MatchThree.DOWNLEFT);
            removeRecursive(x, y, MatchThree.DOWNRIGH);
            foundU = true;
            typeRemoved[hexes[x][y].type] = true;
        }
        if (isSameType(x, y, -1, 1) && isSameType(x, y, -2, 2) && isSameType(x, y, 0, -1) && isSameType(x, y, 0, -2)) {
            removeRecursive(x, y, MatchThree.DOWNLEFT);
            removeRecursive(x, y, MatchThree.UP);
            foundU = true;
            typeRemoved[hexes[x][y].type] = true;
        }
        if (isSameType(x, y, 0, -1) && isSameType(x, y, 0, -2) && isSameType(x, y, 1, 0) && isSameType(x, y, 2, 0)) {
            removeRecursive(x, y, MatchThree.UP);
            removeRecursive(x, y, MatchThree.DOWNRIGH);
            foundU = true;
            typeRemoved[hexes[x][y].type] = true;
        }
        if (isSameType(x, y, 0, 1) && isSameType(x, y, 0, 2) && isSameType(x, y, 1, -1) && isSameType(x, y, 2, -2)) {
            removeRecursive(x, y, MatchThree.DOWN);
            removeRecursive(x, y, MatchThree.UPRIGHT);
            foundU = true;
            typeRemoved[hexes[x][y].type] = true;
        }
        if (isSameType(x, y, 0, 1) && isSameType(x, y, 0, 2) && isSameType(x, y, -1, 0) && isSameType(x, y, -2, 0)) {
            removeRecursive(x, y, MatchThree.DOWN);
            removeRecursive(x, y, MatchThree.UPLEFT);
            foundU = true;
            typeRemoved[hexes[x][y].type] = true;
        }
        if (isSameType(x, y, 1, -1) && isSameType(x, y, 2, -2) && isSameType(x, y, -1, 0) && isSameType(x, y, -2, 0)) {
            removeRecursive(x, y, MatchThree.UPRIGHT);
            removeRecursive(x, y, MatchThree.UPLEFT);
            foundU = true;
            typeRemoved[hexes[x][y].type] = true;
        }
    }

    private static void removeM(int x, int y) {
        if (isSameType(x, y, -1, 1) && isSameType(x, y, -2, 2) && isSameType(x, y, 1, 0) && isSameType(x, y, 2, 0)
                && isSameType(x, y, 0, 1) && isSameType(x, y, 0, 2)) {
            removeRecursive(x, y, MatchThree.DOWNLEFT);
            removeRecursive(x, y, MatchThree.DOWNRIGH);
            removeRecursive(x, y, MatchThree.DOWN);
            removeType(hexes[x][y].type);
            foundM = true;
            typeRemoved[hexes[x][y].type] = true;
        }
        if (isSameType(x, y, -1, 1) && isSameType(x, y, -2, 2) && isSameType(x, y, 0, -1) && isSameType(x, y, 0, -2)
                && isSameType(x, y, -1, 0) && isSameType(x, y, -2, 0)) {
            removeRecursive(x, y, MatchThree.DOWNLEFT);
            removeRecursive(x, y, MatchThree.UP);
            removeRecursive(x, y, MatchThree.UPLEFT);
            removeType(hexes[x][y].type);
            foundM = true;
            typeRemoved[hexes[x][y].type] = true;
        }
        if (isSameType(x, y, 0, -1) && isSameType(x, y, 0, -2) && isSameType(x, y, 1, 0) && isSameType(x, y, 2, 0)
                && isSameType(x, y, 1, -1) && isSameType(x, y, 2, -2)) {
            removeRecursive(x, y, MatchThree.UP);
            removeRecursive(x, y, MatchThree.DOWNRIGH);
            removeRecursive(x, y, MatchThree.UPRIGHT);
            removeType(hexes[x][y].type);
            foundM = true;
            typeRemoved[hexes[x][y].type] = true;
        }

        if (isSameType(x, y, 0, 1) && isSameType(x, y, 0, 2) && isSameType(x, y, 1, -1) && isSameType(x, y, 2, -2)
                && isSameType(x, y, 1, 0) && isSameType(x, y, 2, 0)) {
            removeRecursive(x, y, MatchThree.DOWN);
            removeRecursive(x, y, MatchThree.UPRIGHT);
            removeRecursive(x, y, MatchThree.DOWNRIGH);
            removeType(hexes[x][y].type);
            foundM = true;
            typeRemoved[hexes[x][y].type] = true;
        }
        if (isSameType(x, y, 0, 1) && isSameType(x, y, 0, 2) && isSameType(x, y, -1, 0) && isSameType(x, y, -2, 0)
                && isSameType(x, y, -1, 1) && isSameType(x, y, -2, 2)) {
            removeRecursive(x, y, MatchThree.DOWN);
            removeRecursive(x, y, MatchThree.UPLEFT);
            removeRecursive(x, y, MatchThree.DOWNLEFT);
            removeType(hexes[x][y].type);
            foundM = true;
            typeRemoved[hexes[x][y].type] = true;
        }
        if (isSameType(x, y, 1, -1) && isSameType(x, y, 2, -2) && isSameType(x, y, -1, 0) && isSameType(x, y, -2, 0)
                && isSameType(x, y, 0, -1) && isSameType(x, y, 0, -2)) {
            removeRecursive(x, y, MatchThree.UPRIGHT);
            removeRecursive(x, y, MatchThree.UPLEFT);
            removeRecursive(x, y, MatchThree.UP);
            removeType(hexes[x][y].type);
            foundM = true;
            typeRemoved[hexes[x][y].type] = true;
        }
    }

    private static void removeY(int x, int y) {
        if (isSameType(x, y, -1, 1) && isSameType(x, y, -2, 2) && isSameType(x, y, 1, 0) && isSameType(x, y, 2, 0)
                && isSameType(x, y, 0, -1) && isSameType(x, y, 0, -2)) {
            removeArea(x, y, 2);
            foundY = true;
            typeRemoved[hexes[x][y].type] = true;
        }
        if (isSameType(x, y, -1, 0) && isSameType(x, y, -2, 0) && isSameType(x, y, 1, -1) && isSameType(x, y, 2, -2)
                && isSameType(x, y, 0, 1) && isSameType(x, y, 0, 2)) {
            removeArea(x, y, 2);
            foundY = true;
            typeRemoved[hexes[x][y].type] = true;
        }
    }

    private static void removeX(int x, int y) {
        if (isSameType(x, y, -1, 1) && isSameType(x, y, -2, 2) && isSameType(x, y, 1, -1) && isSameType(x, y, 2, -2)
                && isSameType(x, y, 0, -1) && isSameType(x, y, 0, -2) && isSameType(x, y, 0, 1) && isSameType(x, y, 0, 2)) {
            removeAll();
            foundX = true;
            typeRemoved[hexes[x][y].type] = true;
        }
        if (isSameType(x, y, -1, 1) && isSameType(x, y, -2, 2) && isSameType(x, y, 1, -1) && isSameType(x, y, 2, -2)
                && isSameType(x, y, 1, 0) && isSameType(x, y, 2, 0) && isSameType(x, y, -1, 0) && isSameType(x, y, -2, 0)) {
            removeAll();
            foundX = true;
            typeRemoved[hexes[x][y].type] = true;
        }
        if (isSameType(x, y, 0, -1) && isSameType(x, y, 0, -2) && isSameType(x, y, 0, 1) && isSameType(x, y, 0, 2)
                && isSameType(x, y, 1, 0) && isSameType(x, y, 2, 0) && isSameType(x, y, -1, 0) && isSameType(x, y, -2, 0)) {
            removeAll();
            foundX = true;
            typeRemoved[hexes[x][y].type] = true;
        }
    }

    private static void removeT(int x, int y) {
        // DOWNRIGHT
        if (isSameType(x, y, 1, 0) && isSameType(x, y, -1, 0) && isSameType(x, y, -1, 1) && isSameType(x, y, -1, -1)) {
            if (hexes[x][y - 1] != null) {
                blocksToMoveUp.add(hexes[x][y - 1]);
            }
            if (hexes[x][y + 1] != null) {
                blocksToMoveUp.add(hexes[x][y + 1]);
            }
            if (hexes[x + 1][y - 1] != null) {
                blocksToMoveUp.add(hexes[x + 1][y - 1]);
            }
            if (hexes[x + 1][y + 1] != null) {
                blocksToMoveUp.add(hexes[x + 1][y + 1]);
            }
            removeRecursive(x, y, MatchThree.DOWNRIGH);
            foundT = true;
            typeRemoved[hexes[x][y].type] = true;
        }
        if (isSameType(x, y, 1, 0) && isSameType(x, y, -1, 0) && isSameType(x, y, 0, -1) && isSameType(x, y, -2, 1)) {
            if (hexes[x + 1][y - 1] != null) {
                blocksToMoveUp.add(hexes[x + 1][y - 1]);
            }
            if (hexes[x + 2][y - 1] != null) {
                blocksToMoveUp.add(hexes[x + 2][y - 1]);
            }
            if (hexes[x - 1][y + 1] != null) {
                blocksToMoveUp.add(hexes[x - 1][y + 1]);
            }
            if (hexes[x][y + 1] != null) {
                blocksToMoveUp.add(hexes[x][y + 1]);
            }
            removeRecursive(x, y, MatchThree.DOWNRIGH);
            foundT = true;
            typeRemoved[hexes[x][y].type] = true;
        }
        if (isSameType(x, y, 1, 0) && isSameType(x, y, -1, 0) && isSameType(x, y, 1, 1) && isSameType(x, y, 1, -1)) {
            if (hexes[x][y - 1] != null) {
                blocksToMoveUp.add(hexes[x][y - 1]);
            }
            if (hexes[x][y + 1] != null) {
                blocksToMoveUp.add(hexes[x][y + 1]);
            }
            if (hexes[x - 1][y - 1] != null) {
                blocksToMoveUp.add(hexes[x - 1][y - 1]);
            }
            if (hexes[x - 1][y + 1] != null) {
                blocksToMoveUp.add(hexes[x - 1][y + 1]);
            }
            removeRecursive(x, y, MatchThree.UPLEFT);
            foundT = true;
            typeRemoved[hexes[x][y].type] = true;
        }
        if (isSameType(x, y, 1, 0) && isSameType(x, y, -1, 0) && isSameType(x, y, 0, 1) && isSameType(x, y, 2, -1)) {
            if (hexes[x - 1][y + 1] != null) {
                blocksToMoveUp.add(hexes[x - 1][y + 1]);
            }
            if (hexes[x - 2][y + 1] != null) {
                blocksToMoveUp.add(hexes[x - 2][y + 1]);
            }
            if (hexes[x - 1][y - 1] != null) {
                blocksToMoveUp.add(hexes[x - 1][y - 1]);
            }
            if (hexes[x][y - 1] != null) {
                blocksToMoveUp.add(hexes[x][y - 1]);
            }
            removeRecursive(x, y, MatchThree.UPLEFT);
            foundT = true;
            typeRemoved[hexes[x][y].type] = true;
        }
        //UP
        if (isSameType(x, y, 0, 1) && isSameType(x, y, 0, -1) && isSameType(x, y, -1, 0) && isSameType(x, y, 1, -2)) {
            if (hexes[x + 1][y - 1] != null) {
                blocksToMoveUp.add(hexes[x + 1][y - 1]);
            }
            if (hexes[x + 1][y] != null) {
                blocksToMoveUp.add(hexes[x + 1][y]);
            }
            if (hexes[x - 1][y + 1] != null) {
                blocksToMoveUp.add(hexes[x - 1][y + 1]);
            }
            if (hexes[x - 1][y + 2] != null) {
                blocksToMoveUp.add(hexes[x - 1][y + 2]);
            }
            removeRecursive(x, y, MatchThree.DOWN);
            foundT = true;
            typeRemoved[hexes[x][y].type] = true;
        }
        if (isSameType(x, y, 0, 1) && isSameType(x, y, 0, -1) && isSameType(x, y, 1, -1) && isSameType(x, y, -1, -1)) {
            if (hexes[x + 1][y] != null) {
                blocksToMoveUp.add(hexes[x + 1][y]);
            }
            if (hexes[x + 2][y] != null) {
                blocksToMoveUp.add(hexes[x + 2][y]);
            }
            if (hexes[x - 1][y + 1] != null) {
                blocksToMoveUp.add(hexes[x - 1][y + 1]);
            }
            if (hexes[x - 1][y] != null) {
                blocksToMoveUp.add(hexes[x - 1][y]);
            }
            removeRecursive(x, y, MatchThree.DOWN);
            foundT = true;
            typeRemoved[hexes[x][y].type] = true;
        }
        if (isSameType(x, y, 0, 1) && isSameType(x, y, 0, -1) && isSameType(x, y, -1, 2) && isSameType(x, y, 1, 0)) {
            if (hexes[x + 1][y - 1] != null) {
                blocksToMoveUp.add(hexes[x + 1][y - 1]);
            }
            if (hexes[x + 1][y - 2] != null) {
                blocksToMoveUp.add(hexes[x + 1][y - 2]);
            }
            if (hexes[x - 1][y + 1] != null) {
                blocksToMoveUp.add(hexes[x - 1][y + 1]);
            }
            if (hexes[x - 1][y] != null) {
                blocksToMoveUp.add(hexes[x - 1][y]);
            }
            removeRecursive(x, y, MatchThree.UP);
            foundT = true;
            typeRemoved[hexes[x][y].type] = true;
        }
        if (isSameType(x, y, 0, 1) && isSameType(x, y, 0, -1) && isSameType(x, y, -1, 1) && isSameType(x, y, 1, 1)) {
            if (hexes[x + 1][y - 1] != null) {
                blocksToMoveUp.add(hexes[x + 1][y - 1]);
            }
            if (hexes[x + 1][y] != null) {
                blocksToMoveUp.add(hexes[x + 1][y]);
            }
            if (hexes[x - 1][y - 1] != null) {
                blocksToMoveUp.add(hexes[x - 1][y - 1]);
            }
            if (hexes[x - 1][y] != null) {
                blocksToMoveUp.add(hexes[x - 1][y]);
            }
            removeRecursive(x, y, MatchThree.UP);
            foundT = true;
            typeRemoved[hexes[x][y].type] = true;
        }
        //DOWNLEFT
        if (isSameType(x, y, 1, -1) && isSameType(x, y, -1, 1) && isSameType(x, y, 1, 0) && isSameType(x, y, 1, -2)) {
            if (hexes[x][y - 1] != null) {
                blocksToMoveUp.add(hexes[x][y - 1]);
            }
            if (hexes[x][y + 1] != null) {
                blocksToMoveUp.add(hexes[x][y + 1]);
            }
            if (hexes[x - 1][y + 2] != null) {
                blocksToMoveUp.add(hexes[x - 1][y + 2]);
            }
            if (hexes[x - 1][y] != null) {
                blocksToMoveUp.add(hexes[x - 1][y]);
            }
            removeRecursive(x, y, MatchThree.DOWNLEFT);
            foundT = true;
            typeRemoved[hexes[x][y].type] = true;
        }
        if (isSameType(x, y, 1, -1) && isSameType(x, y, -1, 1) && isSameType(x, y, 0, -1) && isSameType(x, y, 2, -1)) {
            if (hexes[x + 1][y] != null) {
                blocksToMoveUp.add(hexes[x + 1][y]);
            }
            if (hexes[x - 1][y] != null) {
                blocksToMoveUp.add(hexes[x - 1][y]);
            }
            if (hexes[x - 2][y + 1] != null) {
                blocksToMoveUp.add(hexes[x - 2][y + 1]);
            }
            if (hexes[x][y + 1] != null) {
                blocksToMoveUp.add(hexes[x][y + 1]);
            }
            removeRecursive(x, y, MatchThree.DOWNLEFT);
            foundT = true;
            typeRemoved[hexes[x][y].type] = true;
        }
        if (isSameType(x, y, 1, -1) && isSameType(x, y, -1, 1) && isSameType(x, y, -1, 0) && isSameType(x, y, -1, 2)) {
            if (hexes[x][y - 1] != null) {
                blocksToMoveUp.add(hexes[x][y - 1]);
            }
            if (hexes[x][y + 1] != null) {
                blocksToMoveUp.add(hexes[x][y + 1]);
            }
            if (hexes[x + 1][y - 2] != null) {
                blocksToMoveUp.add(hexes[x + 1][y - 2]);
            }
            if (hexes[x + 1][y] != null) {
                blocksToMoveUp.add(hexes[x + 1][y]);
            }
            removeRecursive(x, y, MatchThree.UPRIGHT);
            foundT = true;
            typeRemoved[hexes[x][y].type] = true;
        }
        if (isSameType(x, y, 1, -1) && isSameType(x, y, -1, 1) && isSameType(x, y, 0, 1) && isSameType(x, y, -2, 1)) {
            if (hexes[x + 1][y] != null) {
                blocksToMoveUp.add(hexes[x + 1][y]);
            }
            if (hexes[x - 1][y] != null) {
                blocksToMoveUp.add(hexes[x - 1][y]);
            }
            if (hexes[x + 2][y - 1] != null) {
                blocksToMoveUp.add(hexes[x + 2][y - 1]);
            }
            if (hexes[x][y - 1] != null) {
                blocksToMoveUp.add(hexes[x][y - 1]);
            }
            removeRecursive(x, y, MatchThree.UPRIGHT);
            foundT = true;
            typeRemoved[hexes[x][y].type] = true;
        }
    }

    private static void removeZ(int x, int y) {
        if (isSameType(x, y, 0, 1) && isSameType(x, y, 0, -1) && isSameType(x, y, 1, -1) && isSameType(x, y, -1, 1)) {
            if (hexes[x + 1][y] != null) {
                blocksToMoveUp.add(hexes[x + 1][y]);
            }
            if (hexes[x - 1][y] != null) {
                blocksToMoveUp.add(hexes[x - 1][y]);
            }
            if (hexes[x + 1][y - 2] != null) {
                blocksToMoveUp.add(hexes[x + 1][y - 2]);
            }
            if (hexes[x - 1][y + 2] != null) {
                blocksToMoveUp.add(hexes[x - 1][y + 2]);
            }
            removeType(hexes[x][y].type);
            foundZ = true;
            typeRemoved[hexes[x][y].type] = true;
        }
        if (isSameType(x, y, 0, 1) && isSameType(x, y, 0, -1) && isSameType(x, y, 1, 0) && isSameType(x, y, -1, 0)) {
            if (hexes[x + 1][y - 1] != null) {
                blocksToMoveUp.add(hexes[x + 1][y - 1]);
            }
            if (hexes[x - 1][y + 1] != null) {
                blocksToMoveUp.add(hexes[x - 1][y + 1]);
            }
            if (hexes[x - 1][y - 1] != null) {
                blocksToMoveUp.add(hexes[x - 1][y - 1]);
            }
            if (hexes[x + 1][y + 1] != null) {
                blocksToMoveUp.add(hexes[x + 1][y + 1]);
            }
            removeType(hexes[x][y].type);
            foundZ = true;
            typeRemoved[hexes[x][y].type] = true;
        }
        if (isSameType(x, y, 1, 0) && isSameType(x, y, -1, 0) && isSameType(x, y, 1, -1) && isSameType(x, y, -1, 1)) {
            if (hexes[x][y - 1] != null) {
                blocksToMoveUp.add(hexes[x][y - 1]);
            }
            if (hexes[x][y + 1] != null) {
                blocksToMoveUp.add(hexes[x][y + 1]);
            }
            if (hexes[x + 2][y - 1] != null) {
                blocksToMoveUp.add(hexes[x + 2][y - 1]);
            }
            if (hexes[x - 2][y + 1] != null) {
                blocksToMoveUp.add(hexes[x - 2][y + 1]);
            }
            removeType(hexes[x][y].type);
            foundZ = true;
            typeRemoved[hexes[x][y].type] = true;
        }
    }

    private static void removeAll() {
        for (int x = 16; x < 25; x++) {
            for (int y = 16; y < 25; y++) {
                if (hexes[x][y] != null) {
                    blocksToMoveUp.add(hexes[x][y]);
                }
            }
        }
        removesSame = true;
        removesLine = true;
    }

    private static void removeType(int type) {
        for (int x = 16; x < 25; x++) {
            for (int y = 16; y < 25; y++) {
                if (hexes[x][y] != null && hexes[x][y].type == type) {
                    blocksToMoveUp.add(hexes[x][y]);
                }
            }
        }
        removesSame = true;
    }

    private static void removeRecursive(int x, int y, byte direction) {
        while (hexes[x][y] != null) {
            blocksToMoveUp.add(hexes[x][y]);
            switch (direction) {
                case MatchThree.DOWNRIGH:
                    x++;
                    break;
                case MatchThree.UPLEFT:
                    x--;
                    break;
                case MatchThree.UP:
                    y--;
                    break;
                case MatchThree.DOWN:
                    y++;
                    break;
                case MatchThree.UPRIGHT:
                    x++;
                    y--;
                    break;
                case MatchThree.DOWNLEFT:
                    x--;
                    y++;
                    break;
            }
        }
        removesLine = true;
    }

    private static void removeArea(int xi, int yi, int r) {
        for (int x = 16; x < 25; x++) {
            for (int y = 16; y < 25; y++) {
                if (hexes[x][y] == null) {
                    continue;
                }
                int dx = x - xi;
                int dy = y - yi;

                if (dx * dy >= 0) {
                    if (Math.abs(dx) + Math.abs(dy) <= r) {
                        blocksToMoveUp.add(hexes[x][y]);
                    }
                } else {
                    if (Math.max(Math.abs(dx), Math.abs(dy)) <= r) {
                        blocksToMoveUp.add(hexes[x][y]);
                    }
                }
            }
        }
    }

    private static boolean isSameType(int x, int y, int dx, int dy) {
        return (hexes[x + dx][y + dy] != null && hexes[x + dx][y + dy].type == hexes[x][y].type);
    }

}
