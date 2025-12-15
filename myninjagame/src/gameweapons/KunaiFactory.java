package gameweapons;

import gameframework.gamecharacters.GameCharacter;
import gameframework.weapons.Weapon;
import gameframework.weapons.WeaponFactory;

public class KunaiFactory implements WeaponFactory {
    @Override
    public Weapon createWeapon(GameCharacter weaponHolder) {
        Kunai kunai = new Kunai("kunai.png",weaponHolder);
        return kunai;
    }
}
