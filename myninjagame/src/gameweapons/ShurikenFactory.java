package gameweapons;

import gameframework.gamecharacters.GameCharacter;
import gameframework.weapons.Weapon;
import gameframework.weapons.WeaponFactory;

public class ShurikenFactory implements WeaponFactory {
    @Override
    public Weapon createWeapon(GameCharacter weaponHolder) {

        Shuriken shuriken = new Shuriken("shuriken_static.png", weaponHolder);

        return shuriken;
    }
}
