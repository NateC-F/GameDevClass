package gameframework.weapons;
import gameframework.gameobjects.GameObject;
import java.awt.*;



public class HitBox
{
    private Rectangle hb;


    public HitBox(int x, int y, int width, int height)
    {
        setHb(new Rectangle(x,y,width,height));

    }

    //used to check if current hitbox overlaps another
    //use this in weapons class
    public boolean intersects(GameObject object){

        return hb.intersects(object.getBounds());
    }

    public void update(int x, int y, int width, int height){
        hb.x = x;
        hb.y = y;
        hb.width = width;
        hb.height = height;
    }

    public Rectangle getBounds(){
        return hb;
    }



    public Rectangle getHb() {
        return hb;
    }

    public void setHb(Rectangle rectangle) {
        this.hb = rectangle;
    }
}
