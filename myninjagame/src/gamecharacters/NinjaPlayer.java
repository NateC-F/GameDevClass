package gamecharacters;

import gameframework.GameThread;
import gameframework.animations.Animation;
import gameframework.animations.Spritesheet;
import gameframework.gamecharacters.Player;
import gameframework.gameobjects.Collectible;
import gameframework.gameobjects.GameObject;
import gameframework.gameobjects.GameObjects;
import gameframework.inputhandlers.KeyboardHandler;
import gameframework.inputhandlers.MouseHandler;
import gameframework.sound.GameAudio;
import gameframework.sound.GameClip;
import gameobjects.NinjaGameObjectType;
import gameobjects.NinjaPlayerKunai;
import gameobjects.NinjaShuriken;
import inputhandlers.NinjaKeyboardHandler;

import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.logging.Handler;

public class NinjaPlayer extends Player
{
    private Player ninjaPlayer;
    private long currentTime;
    private long lastAttackTime = 0;
    private long attackDelay = 500;
    private boolean canClipThroughLedges = true;
    private boolean dropThroughLedge = false;
    private final int MELEEDAMAGE = 50;
    private GameAudio gameAudio = new GameAudio();
    //added by Yoyo
    private int coins;
    private int keys;
    private final int DEFAULT_NINJA_JUMP_IMPULSE=-13;

    private long last_damage_taken;
    private final double DAMAGE_DELAY = 1000;

    //Nate
    private static int score;
    private static GameObjects keyList;

    public int getCoins() {return coins;}
    public void setCoins(int newCoins)
    {
        if (coins > 0)
            coins = newCoins;
    }

    public int getKeys() {return keyList.size();}
    public void setKeys(int keys)
    {
        if (keys > 0)
            this.keys = keys;
    }

    public NinjaPlayer(String name, int x, int y,
                     int scaleWidth, int scaleHeight)
    {
        super(name, x, y, scaleWidth, scaleHeight);
        setType(NinjaGameObjectType.PLAYER);
        //Nate
        if (keyList==null)
            keyList=new GameObjects(false);
    }

    @Override
    public void initializeStatus()
    {
        //initialize character attributes
        speed = 6;
        score = 0;
        setTotalHealth(400);
        setCurHealth(400);
        curAnimation = idle;
        setDamage(MELEEDAMAGE);
        //Added by yoyo
        setKeys(0);
        setCoins(0);
        jumpImpulse = DEFAULT_NINJA_JUMP_IMPULSE;
        last_damage_taken = 0;

    }

    @Override
    public void initializeAnimations()
    {
        //initialize all of this character's animations here
        BufferedImage image = GameThread.resourceManager.loadImageResource("spritesheets/ninja_player_idle.png", "");
        idle = new Animation(image, "ninja_player_idle", scaleWidth, scaleHeight);

        Spritesheet spritesheet = new Spritesheet("ninja_player_walk_right.png", 4, 1, 4);
        walkRight = new Animation(spritesheet, scaleWidth, scaleHeight);
        walkRight.setSpeed(5);
        runRight = walkRight;
        //runRight.linkSoundEffect("running.wav", true, false);

        spritesheet = new Spritesheet("ninja_player_walk_left.png", 4, 1, 4, false);
        walkLeft = new Animation(spritesheet, scaleWidth, scaleHeight);
        walkLeft.setSpeed(5);
        runLeft = walkLeft;
        //runLeft.linkSoundEffect("running.wav", true, false);

        spritesheet = new Spritesheet("ninja_player_walk_left.png", 4, 1, 4, false);
        walkDown = new Animation(spritesheet, scaleWidth, scaleHeight);
//        if (true){
//            setGravity(0.6);
//        }
//        else {
//            setGravity(0.3);
//        }
        spritesheet = new Spritesheet("ninja_player_attack_right.png", 2, 1, 2);
        attackRight = new Animation(spritesheet, scaleWidth, scaleHeight);
        attackRight.setSpeed(4);
        attackRight.setRunTimes(1);
        attackRight.linkSoundEffect("player-melee-attack.wav", true, false);

        if(NinjaKeyboardHandler.getDirection() == 2)
            spritesheet = new Spritesheet("ninja_player_jump_right.png", 1, 1, 1);
        else if (NinjaKeyboardHandler.getDirection() == 1)
            spritesheet = new Spritesheet("ninja_player_jump_left.png", 1, 1, 1);
        else
            spritesheet = new Spritesheet("ninja_player_jump.png", 1, 1, 1);
        jump = new Animation(spritesheet, scaleWidth , scaleHeight);
        jump.setSpeed(6);
        jump.runOnlyOnce();
    }

    @Override
    public Animation getMoveUpAnimation()
    {
        if (getVelX() < 0)
            return walkLeft;
        else return walkRight;
    }

    @Override
    public Animation getMoveDownAnimation()
    {
        return walkDown;
    }

    @Override
    public Animation getRunUpAnimation()
    {
        if (getVelX() < 0)
            return runLeft;
        else return runRight;
    }

    @Override
    public Animation getRunDownAnimation()
    {
        return getRunUpAnimation();
    }

    @Override
    public boolean handleObjectCollision(GameObject object){
        if(object.getType() == NinjaGameObjectType.NINJA_SHURIKEN || object.getType() == NinjaGameObjectType.SKELETON_BONE || object.getType() == NinjaGameObjectType.PEA_PROJECTILE){
            setCurHealth(getCurHealth() - object.getDamage());
            /*System.out.println("\n\n" + object.getDamage() + " has been received, new health is: " + getCurHealth() +
                     " The object that hit was NinjaGameObjectType: " + object.getType() + "\n\n");*/
            GameThread.data.removeObjectWhenSafe(object);
            if(getCurHealth() <= 0){
                GameThread.data.removeObjectWhenSafe(ninjaPlayer);
                gameAudio.playClip("game-over.wav", false);
            } else gameAudio.playClip("death-hit-sound.wav", false);
        }

        if (object.getType() == NinjaGameObjectType.SAMURAI_MINI_BOSS)
            return false;

        if(canClipThroughLedges && isLedge(object)){

            if(dropThroughLedge && isLedge(object))
                return true;

            if(isFalling() && isLandingOnTopOf(object))
                return super.handleObjectCollision(object);
            else
                return false;

        }
        return super.handleObjectCollision(object);

    }

    private boolean isLedge(GameObject object)
    {
        String name = object.getName().toLowerCase();
        return name.contains("ledge") || name.contains("sideledge");
    }


    public void update(GameObjects gameObjects)
    {
        super.update(gameObjects);
        // get the current system time
        currentTime = System.currentTimeMillis();

        // Code to grab the ninja player instance from the object list
        if (ninjaPlayer == null) {
            for (GameObject obj : gameObjects) {
                if (obj instanceof NinjaPlayer) {
                    ninjaPlayer = (NinjaPlayer) obj;
//                    System.out.println("\nfound the player!\n"); // Test debug statement
//                    System.out.println(ninjaPlayer.getPosition());
                    break;
                }
            }

            if (ninjaPlayer == null)
                return;

        }
        if(NinjaKeyboardHandler.getDirection() == 3)
            dropThroughLedge = true;

    }

    //handle left click attack
    @Override
    public void specialActionA(boolean startingAction)
    {
        //Test to see if we get the target cords
        System.out.println(MouseHandler.targetX + " " + MouseHandler.targetY);

        //spawn kunai
        if (currentTime - lastAttackTime >= attackDelay) {
            GameObject newKunai = spawnKunai();
            GameThread.data.addObjectWhenSafe(newKunai);
            lastAttackTime = currentTime;
            attackRight.setRunTimes(1);
            attack();
        }
    }

    private GameObject spawnKunai(){
        if (MouseHandler.targetX > ninjaPlayer.getX()){
            return new NinjaPlayerKunai("kunai_right.png", getX() + 50, getY() + 50, 5, 30, 30, true, ninjaPlayer, ninjaPlayer.getPosition(), 5000);
        }
        if (MouseHandler.targetX < ninjaPlayer.getX()){
            return new NinjaPlayerKunai("kunai_left.png", getX() + 50, getY() + 50, 5, 30, 30, true, ninjaPlayer, ninjaPlayer.getPosition(), 5000);
        }
        else
            return new NinjaPlayerKunai("kunai.png", getX() + 50, getY() + 50, 5, 30, 30, true, ninjaPlayer, ninjaPlayer.getPosition(), 5000);
    }

    public void addKey(Collectible key)
    {
        keyList.add(key);
    }

    public boolean useKey()
    {
        if (!keyList.isEmpty())
        {
            keyList.removeFirst();
            return true;
        }
        return false;
    }



    /* Temporary used for debugging*/
    public boolean isNinja() { return true;}
    public void setPosition(int x, int y)
    {
        if (x != getX())
            x = x;
        if (x < getX() - 50)
            x = x;
        if (x < getX() - 70)
            x = x;
        if (x < getX() - 100)
            x = x;
        if (x < getX() - 30)
            x = x;
        super.setPosition(x, y);
    }

    @Override
    public void takeDamage(int damage)
    {
        if (System.currentTimeMillis()-last_damage_taken < DAMAGE_DELAY)
            return;
        super.takeDamage(damage);
        last_damage_taken = System.currentTimeMillis();
    }
    /********/
}
