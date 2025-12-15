package gameframework.weapons;

import gameframework.gamecharacters.GameCharacter;


//abstract factory for creating weapons in the game
//each weapon has its own weapon factory which implements this factory
//as of now, character is required for creation of weapon
public interface WeaponFactory {
    public Weapon createWeapon(GameCharacter weaponHolder);
}


