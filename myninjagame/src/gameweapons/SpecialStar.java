package gameweapons;

import gameframework.gamecharacters.GameCharacter;
import gameframework.gameobjects.GameObjects;
import gameframework.weapons.MeleeWeapon;

public class SpecialStar extends MeleeWeapon {
    public SpecialStar(String name, GameCharacter weaponHolder) {
        super(name,weaponHolder,10,100,100);

    }

    @Override
    public void update(GameObjects objects)
    {
        super.update(objects);

    }
}
