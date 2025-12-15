package gameobjects;

import gamecharacters.NinjaPlayer;
import gameframework.GameThread;
import gameframework.animations.Animation;
import gameframework.animations.Spritesheet;
import gameframework.gamecharacters.Player;
import gameframework.gameobjects.GameObject;
import gameframework.gameobjects.GameObjectType;
import gameframework.gameobjects.GameObjects;
import gameframework.gameobjects.InanimateObject;

public class Chest extends InanimateObject
{
    private Animation closed;
    private Animation open;
    private Boolean isOpen;
    private final int OPEN_X_BOUNDS = 50;
    private final int OPEN_Y_BOUNDS = 50;

    public Chest(String name, int x, int y, int z, int scaleWidth, int scaleHeight)
    {
        super(name, x, y, z, scaleWidth, scaleHeight);
        requiresUpdating=true;
        isOpen=false;
        initializeAnimations();
        curAnimation = closed;
    }

    public void initializeAnimations()
    {
        Spritesheet spritesheet = new Spritesheet("chest_closed.png",1,1,1,false);
        closed = new Animation(spritesheet,scaleWidth,scaleHeight);

        spritesheet = new Spritesheet("chest_opened.png",1,1,1,false);
        open = new Animation(spritesheet,scaleWidth,scaleHeight);

    }

    public void openChest()
    {
        Player player = Player.getActivePlayer();

        if (player instanceof NinjaPlayer && !isOpen && ((NinjaPlayer)player).useKey())
        {
            curAnimation = open;
            isOpen = true;
            GameThread.gameAudio.playClip("chest-open.wav", false);
            player.setScore(player.getScore() + 500);
        }
    }


    @Override
    public boolean handleObjectCollision(GameObject object)
    {
        return false;
    }

    @Override
    public void update(GameObjects objects)
    {
        super.update(objects);
        int playerX = Player.getActivePlayer().getX();
        int playerY = Player.getActivePlayer().getY();
        if (Math.abs(playerX-getX())<OPEN_X_BOUNDS && Math.abs(playerY-getY())<OPEN_Y_BOUNDS)
            openChest();
    }
}
