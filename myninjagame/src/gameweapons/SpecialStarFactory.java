package gameweapons;

import gameframework.gamecharacters.GameCharacter;
import gameframework.weapons.Weapon;
import gameframework.weapons.WeaponFactory;

public class SpecialStarFactory implements WeaponFactory {

    @Override
    public Weapon createWeapon(GameCharacter weaponHolder) {
        SpecialStar ss = new SpecialStar("ninja_star_normal_static.png",weaponHolder);

        return ss;
    }
}
