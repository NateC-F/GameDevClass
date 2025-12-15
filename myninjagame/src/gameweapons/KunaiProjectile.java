package gameweapons;

import gameframework.animations.Animation;
import gameframework.animations.Spritesheet;
import gameframework.gameobjects.GameObject;
import gameframework.weapons.Projectile;
import gameframework.weapons.Weapon;

import java.awt.*;

public class KunaiProjectile extends Projectile {

    public KunaiProjectile(String name, int x, int y, int scaleWidth, int scaleHeight, Weapon origin, GameObject owner, int speed, Point startPosition) {
        super(name, x, y, scaleWidth, scaleHeight, origin, owner, speed, startPosition);
    }

    //needs to be finished
    @Override
    public void initAnimations() {
        Spritesheet regularSpinSpritesheet = new Spritesheet("kunai_left.png",
                1, 1, 1, false);
        Animation normal = new Animation(regularSpinSpritesheet, scaleWidth, scaleHeight);
        normal.setSpeed(1);
        setSpin1(normal);

        curAnimation = getSpin1();
    }
}
