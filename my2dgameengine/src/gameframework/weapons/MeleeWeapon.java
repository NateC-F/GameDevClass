package gameframework.weapons;

import gameframework.GameThread;
import gameframework.gamecharacters.GameCharacter;
import gameframework.gameobjects.GameObject;
import gameframework.gameobjects.GameObjectType;
import gameframework.gameobjects.GameObjects;

import java.awt.*;

public abstract class MeleeWeapon extends Weapon
{

    public MeleeWeapon(String name, GameCharacter weaponHolder, int damage, int width, int height) {
        super(name, weaponHolder, damage, width, height);

    }




    @Override
    public void attack() {
        //prevent spam
        if (attacking)
            return;

        attacking = true;
        System.out.println("MELEE ATTACK");

    }

    @Override
    public void update(GameObjects objects) {
        super.update(objects);

        if (attacking) {

            checkAttackCollisions(objects);


        }
    }

    private void checkAttackCollisions(GameObjects objects)
    {
    for (GameObject obj : objects) {


        if(obj != getWeaponHolder() && obj != this && obj.getType() != GameObjectType.INANIMATE)
            checkCollision(obj);



    }
        attacking = false;
    }


}
