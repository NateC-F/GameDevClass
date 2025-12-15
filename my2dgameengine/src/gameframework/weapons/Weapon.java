package gameframework.weapons;

import gameframework.gamecharacters.GameCharacter;
import gameframework.gameobjects.GameObject;
import gameframework.gameobjects.GameObjectType;
import gameframework.gameobjects.GameObjects;
import gameframework.gameobjects.InanimateObject;
//import gameobjects.NinjaGameObjectType;

import java.awt.*;

public abstract class Weapon extends InanimateObject
{
    private int damage;
    private HitBox hb;
    protected GameCharacter weaponHolder;

    protected boolean attacking = false;



    public Weapon(String name, GameCharacter weaponHolder, int damage, int width, int height)
    {
        super(name, weaponHolder.getX(), weaponHolder.getY(), 1, width, height);

        this.weaponHolder = weaponHolder;
        this.setDamage(damage);

        //create weapon's hitbox
        setHb(new HitBox(weaponHolder.getX(), weaponHolder.getY(), width, height));

        requiresUpdating = true;

        System.out.println("Weapon constructed: " + this);
        System.out.println("Weapon created at " + getX() + ", " + getY());

    }


    /*@Override
    public void render(Graphics g){
        super.render(g);

        if(hb != null){
            Rectangle bounds = hb.getBounds();
            Color oldColor = g.getColor();
            g.setColor(Color.RED);
            g.drawRect(bounds.x, bounds.y, bounds.width, bounds.height);
            g.setColor(oldColor);
        }
    }*/



    public void checkCollision(GameObject object){

        if(!attacking){
            return;
        }
        if(hb.intersects(object)){

            if(object instanceof GameCharacter target){
                target.takeDamage(damage);
                System.out.println("HITBOX COLLISION DETECTED");
                System.out.println(target.getName() + " IS HIT!");
            }


            }

        }



    public abstract void attack();


    @Override
    public void update(GameObjects objects){


        super.update(objects);

            int x;
            int y;

            if (weaponHolder.isMovingLeft()) {
                x = weaponHolder.getX() + 5;
                y = weaponHolder.getY() + 70;
            } else if (weaponHolder.isMovingRight()) {
                x = weaponHolder.getX() + 50;
                y = weaponHolder.getY() + 70;
            } else {
                x = weaponHolder.getX() + 50;
                y = weaponHolder.getY() + 70;
            }

            setPosition(x, y);

            updateHitBox(x,y);

        }



    @Override
    public boolean shouldIgnoreCollisionWith(GameObject other){
        boolean shouldIgnore = false;

        switch(other.getType()){

            case GameObjectType.PLAYER:
                shouldIgnore = true;
                break;

            case GameObjectType.NPC:
                shouldIgnore = true;
                break;

        }
        return shouldIgnore;
    }




    public void updateHitBox(int x, int y)
    {
        hb.update(x,y,getScaleWidth(),getScaleHeight());
    }


    //handle collision
    public boolean handleObjectCollision(GameObject object) {
        return false;
    }



    public int getDamage() {
        return damage;
    }

    public void setDamage(int damage) {
        this.damage = damage;
    }


    public GameCharacter getWeaponHolder() {
        return weaponHolder;
    }

    public void setWeaponHolder(GameCharacter weaponHolder) {
        this.weaponHolder = weaponHolder;
    }

    public HitBox getHb() {
        return hb;
    }

    public void setHb(HitBox hb) {
        this.hb = hb;
    }

    public boolean isAttacking() {
        return attacking;
    }

    public void setAttacking(boolean attacking) {
        this.attacking = attacking;
    }

}
