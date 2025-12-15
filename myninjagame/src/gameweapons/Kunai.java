package gameweapons;

import gameframework.gamecharacters.GameCharacter;
import gameframework.gameobjects.GameObjects;
import gameframework.weapons.Projectile;
import gameframework.weapons.RangedWeapon;

import java.awt.*;

public class Kunai extends RangedWeapon {
    public Kunai(String name, GameCharacter weaponHolder) {
        super(name, weaponHolder, 1, 75, 75);
    }

    @Override
    public void update(GameObjects objects)
    {
        super.update(objects);



    }

    @Override
    protected Projectile createProjectile() {
        int x = getWeaponHolder().getWeapon().getX();
        int y = getWeaponHolder().getWeapon().getY();

        //offset
        int spawnX;
        if (getWeaponHolder().isMovingRight()) {
            spawnX = x + 100;
        } else if (getWeaponHolder().isMovingLeft()) {
            spawnX = x - 50;
        }
        else{
            spawnX = x;
        }

        int spawnY = y - 40;



        //create starting position of projectile
        Point startPos = new Point(spawnX, spawnY);

        return new KunaiProjectile("kunai.png", spawnX, spawnY, 50, 50,this, getWeaponHolder(), 8,
                startPos);
    }

}
