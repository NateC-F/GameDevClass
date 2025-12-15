package inputhandlers;

import gameframework.GameData;
import gameframework.display.GameDisplay;
import gameframework.inputhandlers.MouseHandler;

import java.awt.event.MouseEvent;

public class NinjaMouseHandler extends MouseHandler {
    public NinjaMouseHandler(GameDisplay display, GameData data) {
        super(display, data);
    }
    public static int targetX;
    public static int targetY;
    //pseudocode for now
    /*
    when left click{
        get clicked pos
        get cur pos
        subtract target x and y from cur x and y to get the distance that needs to be traveled
        figure out how to get the numbers you need to increase/decrease the kuni's pos by to move toward the target
        play throw animation
        spawn in kuni at the pos of the player (make sure it is orientated correctly)
        (making sure it can't collide w/ player)(making it appear behind the player so it blends in with the animation after it is thrown)
        make the kuni's pos x and y increase/decrease by the number that was found previously
        have kuni travel in that direction until it hits a wall, boss, or enemy
    }
     */


    public void getTargetX(MouseEvent me){
        targetX = me.getX() + display.getCameraPosition().x;
    }
    public void getTargetY(MouseEvent me){
        targetY = me.getY() + display.getCameraPosition().y;
    }
}
