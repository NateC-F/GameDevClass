package gameframework.weapons;

import gameframework.GameThread;
import gameframework.gamecharacters.GameCharacter;
import gameframework.gameobjects.GameObject;
import gameframework.gameobjects.GameObjects;

import java.awt.*;

//need to initialize projectile within this class

public abstract class RangedWeapon extends Weapon
{
    //pass hitbox into weapon constructor
    public RangedWeapon(String name, GameCharacter weaponHolder, int damage, int width, int height) {
        super(name, weaponHolder, damage, width, height);
    }

//override from weapon class
    @Override
    public void attack(){
        Projectile p = createProjectile();
        GameThread.data.addObjectWhenSafe(p);
    }


    @Override
    public void update(GameObjects objects){
        //setPosition(getWeaponHolder().getX(), getWeaponHolder().getY());
        super.update(objects);

    }

    protected abstract Projectile createProjectile();


}
