package gameweapons;

import gameframework.animations.Animation;
import gameframework.animations.Spritesheet;
import gameframework.gameobjects.GameObject;
import gameframework.weapons.Projectile;
import gameframework.weapons.Weapon;

import java.awt.*;

public class ShurikenProjectile extends Projectile {
    public ShurikenProjectile(String name, int x, int y, int scaleWidth, int scaleHeight, Weapon origin, GameObject owner, int speed,
                              Point startPosition) {
        super(name, x, y, scaleWidth, scaleHeight, origin, owner,speed, startPosition);
    }

    @Override
    public void initAnimations() {
        /*Spritesheet emptySpritesheet = new Spritesheet("shuriken_static.png",
                1, 1, 1, false);
        Animation empty = new Animation(emptySpritesheet, scaleWidth, scaleHeight);
        empty.setSpeed(0);
        setDisappear(empty);*/

        Spritesheet regularSpinSpritesheet = new Spritesheet("ninja_star_normal_throwing.png",
                2, 1, 2, false);
        Animation normal = new Animation(regularSpinSpritesheet, scaleWidth, scaleHeight);
        normal.setSpeed(1);
        setSpin1(normal);

        /*Spritesheet regularSpinSpritesheet2 = new Spritesheet("ninja_star_special_throwing.png",
                2, 1, 2, false);
        Animation normal2 = new Animation(regularSpinSpritesheet2, scaleWidth - 5, scaleHeight - 5);
        normal2.setSpeed(1);
        setSpin2(normal2);*/

        curAnimation = getSpin1();
    }
}
