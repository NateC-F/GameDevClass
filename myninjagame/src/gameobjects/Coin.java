package gameobjects;

import gamecharacters.NinjaPlayer;
import gameframework.GameThread;
import gameframework.gamecharacters.Player;
import gameframework.gameobjects.Collectible;

public class Coin extends Collectible
{
    private final int DEFAULT_SCORE_VALUE = 100;
    private int score_value;
    public Coin(String name, int x, int y, int z, int scaleWidth, int scaleHeight)
    {
        super(name, x, y, z, scaleWidth, scaleHeight);
        setGravity(0);
        score_value = DEFAULT_SCORE_VALUE;
    }

    @Override
    public void collect()
    {
        super.collect();
        Player player = Player.getActivePlayer();
        GameThread.gameAudio.playClip("collect.wav", false);
        player.setScore(player.getScore() + score_value);
        if (player instanceof NinjaPlayer)
            ((NinjaPlayer)player).setCoins( ((NinjaPlayer)player).getCoins()+1);
    }

    public void setScore_value(int new_score_value)
    {
        if (new_score_value>0)
            score_value = new_score_value;
    }

}

