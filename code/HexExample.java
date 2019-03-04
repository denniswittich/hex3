package de.denniswittich.hex3;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;

import java.util.Random;

import de.denniswittich.hex3.Interfaces.Irenderable;
import de.denniswittich.hex3.Interfaces.Iupdateable;

/**
 * Created by Dennis Wittich on 04.03.2017.
 */

public class HexExample implements Irenderable , Iupdateable {
    public static final int SHAPE = 0;
    public static final int SECOND = 4;
    public static final int THIRD = 5;
    public static final int SAMECOLORRMV = 1;
    public static final int FIXED = 2;
    public static final int OTHERREMOVE = 3;

    private static int SHAPECOLOR = 3;
    private static int SECONDCOLOR = 2;
    private static int THIRDCOLOR = 1;

    private static final Random random = new Random();
    private static final float sin60 = (float)Math.sin(Math.PI/3.);
    private static final float cos60 = (float)Math.cos(Math.PI/3.);

    private final Paint paintUL = new Paint();
    private final Path pathUL = new Path();
    private final Paint paintDR = new Paint();
    private final Path pathDR = new Path();
    private final float cx;
    private final float cy;
    private float radius;

    private int type;

    public final int x;
    public final int y;

    public static void setRandomShapeColor(){
        SHAPECOLOR = random.nextInt(7);
        do{
            SECONDCOLOR = random.nextInt(7);
        }while(SHAPECOLOR == SECONDCOLOR);
        do{
            THIRDCOLOR = random.nextInt(7);
        }while(SHAPECOLOR == THIRDCOLOR || SECONDCOLOR == THIRDCOLOR);
    }

    public HexExample(int hexX, int hexY, int type){
        //Log.d("h","h");
        this.type = type;
        this.x = hexX;
        this.y = hexY;
        this.cx = Dimensions.screenHalfWidth + hexX*sin60*Dimensions.blockSize;
        this.cy = Dimensions.screenHalfHeight + hexY*Dimensions.blockSize + hexX*cos60*Dimensions.blockSize;
        this.radius = Dimensions.blockSize/2f;
        paintUL.setStyle(Paint.Style.FILL);
        paintDR.setStyle(Paint.Style.FILL);
        reloadPaints();
        setPaths();
    }

    private void setPaths(){
        float ty = cy - sin60 * radius;
        float by = cy + sin60 * radius;
        float ilx = cx - cos60 * radius;
        float irx = cx + cos60 * radius;

        pathUL.reset();
        pathUL.moveTo(ilx, by);
        pathUL.lineTo(cx-radius,cy);
        pathUL.lineTo(ilx, ty);
        pathUL.lineTo(irx, ty);
        pathUL.close();

        pathDR.reset();
        pathDR.moveTo(ilx, by);
        pathDR.lineTo(irx, by);
        pathDR.lineTo(cx+radius,cy);
        pathDR.lineTo(irx, ty);
        pathDR.close();
    }

    @Override
    public void render(Canvas canvas) {
        //Log.d("r","r");
        canvas.drawPath(pathUL,paintUL);
        canvas.drawPath(pathDR,paintDR);
    }

    private void reloadPaints() {
        switch (type){
            case OTHERREMOVE:
            case FIXED: // gray
                paintUL.setColor(0xFF969696);
                paintDR.setColor(0xFF7A7A7A);
                break;
            case SHAPE:
                switch (SHAPECOLOR){
                    case 0: // orange
                        paintUL.setColor(0xFFFF7F00);
                        paintDR.setColor(0xFFF26D00);
                        break;
                    case 1: // green
                        paintUL.setColor(0xFF4FC900);
                        paintDR.setColor(0xFF4BBC02);
                        break;
                    case 2: // blue
                        paintUL.setColor(0xFF0092DB);
                        paintDR.setColor(0xFF008AAA);
                        break;
                    case 3: // red
                        paintUL.setColor(0xFFEA0000);
                        paintDR.setColor(0xFFDD0000);
                        break;
                    case 4: // mud
                        paintUL.setColor(0xFFC6C100);
                        paintDR.setColor(0xFFB2AD00);
                        break;
                    case 5: // purple
                        paintUL.setColor(0xFFC800E5);
                        paintDR.setColor(0xFFB900E0);
                        break;
                    case 6: // cyan
                        paintUL.setColor(0xFF00DDB3);
                        paintDR.setColor(0xFF00D3AA);
                        break;
                }

                break;
            case SECOND:
                switch (SECONDCOLOR){
                    case 0: // orange
                        paintUL.setColor(0xFFFF7F00);
                        paintDR.setColor(0xFFF26D00);
                        break;
                    case 1: // green
                        paintUL.setColor(0xFF4FC900);
                        paintDR.setColor(0xFF4BBC02);
                        break;
                    case 2: // blue
                        paintUL.setColor(0xFF0092DB);
                        paintDR.setColor(0xFF008AAA);
                        break;
                    case 3: // red
                        paintUL.setColor(0xFFEA0000);
                        paintDR.setColor(0xFFDD0000);
                        break;
                    case 4: // mud
                        paintUL.setColor(0xFFC6C100);
                        paintDR.setColor(0xFFB2AD00);
                        break;
                    case 5: // purple
                        paintUL.setColor(0xFFC800E5);
                        paintDR.setColor(0xFFB900E0);
                        break;
                    case 6: // cyan
                        paintUL.setColor(0xFF00DDB3);
                        paintDR.setColor(0xFF00D3AA);
                        break;
                }
                break;
            case THIRD:
                switch (THIRDCOLOR){
                    case 0: // orange
                        paintUL.setColor(0xFFFF7F00);
                        paintDR.setColor(0xFFF26D00);
                        break;
                    case 1: // green
                        paintUL.setColor(0xFF4FC900);
                        paintDR.setColor(0xFF4BBC02);
                        break;
                    case 2: // blue
                        paintUL.setColor(0xFF0092DB);
                        paintDR.setColor(0xFF008AAA);
                        break;
                    case 3: // red
                        paintUL.setColor(0xFFEA0000);
                        paintDR.setColor(0xFFDD0000);
                        break;
                    case 4: // mud
                        paintUL.setColor(0xFFC6C100);
                        paintDR.setColor(0xFFB2AD00);
                        break;
                    case 5: // purple
                        paintUL.setColor(0xFFC800E5);
                        paintDR.setColor(0xFFB900E0);
                        break;
                    case 6: // cyan
                        paintUL.setColor(0xFF00DDB3);
                        paintDR.setColor(0xFF00D3AA);
                        break;
                }
                break;
            case SAMECOLORRMV:
                switch (SHAPECOLOR){
                    case 0: // orange
                        paintUL.setColor(0xFFFFD2A9);
                        paintDR.setColor(0xFFE8BC9C);
                        break;
                    case 1: // green
                        paintUL.setColor(0xFF9AC67D);
                        paintDR.setColor(0xFF8CB274);
                        break;
                    case 2: // blue
                        paintUL.setColor(0xFF86BACE);
                        paintDR.setColor(0xFF6F9DA5);
                        break;
                    case 3: // red
                        paintUL.setColor(0xFFDD8585);
                        paintDR.setColor(0xFFC67B7B);
                        break;
                    case 4: // mud
                        paintUL.setColor(0xFFBCB97D);
                        paintDR.setColor(0xFFA5A26D);
                        break;
                    case 5: // purple
                        paintUL.setColor(0xFFD290DD);
                        paintDR.setColor(0xFFC98BD8);
                        break;
                    case 6: // cyan
                        paintUL.setColor(0xFF89DBCC);
                        paintDR.setColor(0xFF8BD1C3);
                        break;
                }
                break;
        }
    }


    @Override
    public void update(float deltaTime) {
        if(type == FIXED ){
            if(radius != Dimensions.blockSize/2f){
                radius = Dimensions.blockSize/2f;
                setPaths();
            }
            return;
        }else if(type == SHAPE || type == SECOND || type == THIRD){
            radius = Lerpers.SmoothBump(Dimensions.blockSize/2f,Dimensions.blockSize/2.4f,GameSettings.moveTimer);
            setPaths();
            return;
        }
        radius = Lerpers.SmoothBump(Dimensions.blockSize/2f,Dimensions.blockSize/4f,GameSettings.moveTimer);
        setPaths();
    }

    public void setType(int type) {
        this.type = type;
        reloadPaints();
    }
}
