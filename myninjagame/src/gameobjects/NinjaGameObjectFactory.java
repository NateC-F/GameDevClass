package gameobjects;

import gamecharacters.*;
import gameframework.gameobjects.GameObject;
import gameframework.gameobjects.GameObjectFactory;
import gameframework.gameobjects.Collectible;

public class NinjaGameObjectFactory extends GameObjectFactory
{
    public GameObject createGameObject(String objectStr)
    {
        GameObject gameObject = super.createGameObject(objectStr);

        if (gameObject == null)
        {
            switch (type)
            {
                case NinjaGameObjectType.DARK_ELF:
                    gameObject = new DarkElf(subtype, posX, posY, 2, scaleWidth, scaleHeight);
                    break;
                case NinjaGameObjectType.SAMURAI_MINI_BOSS:
                    gameObject = new SamuraiBoss(subtype, posX, posY, 2, scaleWidth, scaleHeight);
                    break;
                case NinjaGameObjectType.LEVEL_BOSS:
                    gameObject = new LevelBoss(subtype, posX, posY, 2, scaleWidth, scaleHeight);
                    break;
                case NinjaGameObjectType.NINJA:
                    gameObject = new Ninja(subtype, posX, posY, 2, scaleWidth, scaleHeight);
                    break;
                case NinjaGameObjectType.SKELETON:
                    gameObject = new Skeleton(subtype, posX, posY, 2, scaleWidth, scaleHeight);
                    break;
                case NinjaGameObjectType.FIRE_SPIRIT:
                    gameObject = new FireSpirit(subtype, posX, posY, 2, scaleWidth * 2, scaleHeight * 2);
                    break;
                case NinjaGameObjectType.PEASHOOTER_MINI_BOSS:
                    gameObject = new PeaShooterBoss(subtype, posX, posY, 2, scaleWidth, scaleHeight);
                    break;
                case NinjaGameObjectType.THROWN_WEAPON:
                    gameObject = new ThrownWeapon(subtype, posX, posY, 2, scaleWidth, scaleHeight, thrownByPlayer, owner);
                    break;
                case NinjaGameObjectType.KEY:
                    gameObject = new Key(subtype+".png", posX, posY, 2, scaleWidth, scaleHeight);
                    break;
                case NinjaGameObjectType.COIN:
                    gameObject = new Coin(subtype+".png", posX, posY, 2, scaleWidth, scaleHeight);
                    break;
                case NinjaGameObjectType.CHEST:
                    gameObject = new Chest(subtype+".png",posX,posY,2,scaleWidth,scaleHeight);
                    break;

            }
        }
        return gameObject;
    }
}
