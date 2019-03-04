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

public class Hex implements Irenderable , Iupdateable {
    private static final Random random = new Random();
    public static final float sin60 = (float)Math.sin(Math.PI/3.);
    public static final float cos60 = (float)Math.cos(Math.PI/3.);

    private final Paint paintUL = new Paint();
    private final Path pathUL = new Path();
    private final Paint paintDR = new Paint();
    private final Path pathDR = new Path();
    public float cx,cy;
    private float outx;
    private float outy;
    private float radius;

    public int type;

    public int x,y;
    public boolean shrinkAnimation;
    public boolean setRandomType;

    public Hex(int hexX, int hexY){
        this.x = hexX;
        this.y = hexY;
        this.cx = Dimensions.screenHalfWidth + hexX*sin60*Dimensions.blockSize;
        this.cy = Dimensions.screenHalfHeight + hexY*Dimensions.blockSize + hexX*cos60*Dimensions.blockSize;
        this.radius = Dimensions.blockSize/2f;
        paintUL.setStyle(Paint.Style.FILL);
        paintDR.setStyle(Paint.Style.FILL);
        setRandomType();
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
        if(GameSettings.gameEnded){
            if(GameSettings.gameEndTimer == 1f){
                return;
            }
            cx = Lerpers.Linear(cx,outx,GameSettings.gameEndTimer);
            cy = Lerpers.Linear(cy,outy,GameSettings.gameEndTimer);
            //paintUL.setAlpha((int)(255*(1f-GameSettings.gameEndTimer)));
            //paintDR.setAlpha((int)(255*(1f-GameSettings.gameEndTimer)));
            setPaths();
        }
        //Log.d("r","r");
        canvas.drawPath(pathUL,paintUL);
        canvas.drawPath(pathDR,paintDR);
    }

    public void reloadPaints() {
        switch (type){
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
    }

    public void setRandomType() {
        float angle = random.nextFloat()*360f;
        outx = Dimensions.screenHalfWidth+(float)Math.sin(angle)*Dimensions.screenHeight*0.7f;
        outy = Dimensions.screenHalfHeight+(float)Math.cos(angle)*Dimensions.screenHeight*0.7f;
        type = random.nextInt(7);
        reloadPaints();
    }

    @Override
    public void update(float deltaTime) {
        float cxt = Dimensions.screenHalfWidth + x*sin60*Dimensions.blockSize;
        float cyt = Dimensions.screenHalfHeight + y*Dimensions.blockSize + x*cos60*Dimensions.blockSize;

        if(shrinkAnimation){
            if(GameSettings.moveTimer<0.5f){
                radius = Dimensions.blockSize/2f * (1-(GameSettings.moveTimer*2f));
            }else{
                if(setRandomType){
                    setRandomType = false;
                    setRandomType();
                }
                radius = Dimensions.blockSize/2f * ((GameSettings.moveTimer -0.5f)*2f);
                cx = cxt;
                cy = cyt;
            }
        }else{
            cx += (cxt-cx)* GameSettings.moveTimer;
            cy += (cyt-cy)* GameSettings.moveTimer;
        }
        setPaths();
    }
}
