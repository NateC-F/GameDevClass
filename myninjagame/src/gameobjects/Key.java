package gameobjects;

import gamecharacters.NinjaPlayer;
import gameframework.GameThread;
import gameframework.animations.Animation;
import gameframework.gamecharacters.Player;
import gameframework.gameobjects.Collectible;
import gameframework.resourcemanagement.ResourceManager;

import java.awt.image.BufferedImage;

public class Key extends Collectible
{
    public Key(String name, int x, int y, int z, int scaleWidth, int scaleHeight)
    {
        super(name, x, y, z, scaleWidth, scaleHeight);
        setGravity(0);
    }

    @Override
    public void collect()
    {
        super.collect();
        Player player = Player.getActivePlayer();
        GameThread.gameAudio.playClip("collect.wav", false);
        if (player instanceof NinjaPlayer)
            ((NinjaPlayer)player).addKey(this);
    }
}
