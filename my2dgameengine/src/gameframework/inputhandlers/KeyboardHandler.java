package gameframework.inputhandlers;

import gameframework.GameThread;
import gameframework.gamecharacters.Player;
import gameframework.gameobjects.GameObject;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashSet;

/*
 * This is class is used to handle all keyboard events to the game window, most are used to control
 * the player. Besides triggering certain actions in the player, it can also keep track of whether
 * some keys are pressed simultaneously.
 */
public class KeyboardHandler implements KeyListener
{

    /* IDs for all possible default action handlers (each action handler is mapped to a key).
     * Games that use the engine are allowed to map different keys to the default handlers
     * or use their own handlers by overriding the keyPressed and keyReleased event methods.
     */
    public static final int HANDLER_MOVE_RIGHT = 0;
    public static final int HANDLER_MOVE_LEFT = 1;
    public static final int HANDLER_MOVE_UP = 2;
    public static final int HANDLER_MOVE_DOWN = 3;
    public static final int HANDLER_ATTACK_RIGHT = 4;
    public static final int HANDLER_ATTACK_LEFT = 5;
    public static final int HANDLER_ATTACK_UP = 6;
    public static final int HANDLER_ATTACK_DOWN = 7;
    public static final int HANDLER_ATTACK = 8;
    public static final int HANDLER_RANGED_ATTACK_RIGHT = 9;
    public static final int HANDLER_RANGED_ATTACK_LEFT = 10;
    public static final int HANDLER_RANGED_ATTACK_UP = 11;
    public static final int HANDLER_RANGED_ATTACK_DOWN = 12;
    public static final int HANDLER_INVENTORY = 13;
    public static final int HANDLER_JUMP = 14;
    public static final int HANDLER_OBJECT_ACTION = 15;
    public static final int HANDLER_NEXT_LEVEL = 16;
    public static final int HANDLER_DUMP_OBJECTS = 17;
    public static final int HANDLER_CHANGE_CHARACTER = 18;
    public static final int HANDLER_RESTART_GAME = 19;
    public static final int HANDLER_DISPLAY_STATUS = 20;
    public static final int HANDLER_DISPLAY_FRAMERATE = 21;
    public static final int HANDLER_DEBUG_MODE = 22;
    public static final int HANDLER_ENABLE_SOUNDEFFECTS = 23;
    public static final int HANDLER_GAME_PAUSED = 24;
    public static final int HANDLER_BOUNDSONLY_MODE = 25;
    public static final int HANDLER_BORDERSONLY_MODE = 26;
    public static final int HANDLER_DASH = 27;
    public HashSet<Integer> keysHeld;

    public KeyboardHandler()
    {
        keysHeld = new HashSet<>();
    }

    @Override
    public void keyTyped(KeyEvent e)
    {

    }

    @Override
    public void keyPressed(KeyEvent ke)
    {
        int keyCode = ke.getKeyCode();
        int keyHeldId = -1;

        switch (keyCode)
        {
            case KeyEvent.VK_RIGHT:
                keyPressedActionHandler(HANDLER_MOVE_RIGHT, ke, keyHeldId);
                break;
            case KeyEvent.VK_LEFT:
                keyPressedActionHandler(HANDLER_MOVE_LEFT, ke, keyHeldId);
                break;
            case KeyEvent.VK_DOWN:
                keyPressedActionHandler(HANDLER_MOVE_DOWN, ke, keyHeldId);
                break;
            case KeyEvent.VK_UP:
                keyPressedActionHandler(HANDLER_MOVE_UP, ke, keyHeldId);
                break;
            case KeyEvent.VK_D:
                keyPressedActionHandler(HANDLER_ATTACK_RIGHT, ke, keyHeldId);
                break;
            case KeyEvent.VK_SPACE:
                keyPressedActionHandler(HANDLER_JUMP, ke, keyHeldId);
                break;
            case KeyEvent.VK_SHIFT:
                keyPressedActionHandler(HANDLER_DASH, ke, keyHeldId);
                break;
            case KeyEvent.VK_A:
                keyPressedActionHandler(HANDLER_ATTACK_LEFT, ke, keyHeldId);
                break;
            case KeyEvent.VK_S:
                keyPressedActionHandler(HANDLER_ATTACK_DOWN, ke, keyHeldId);
                break;
            case KeyEvent.VK_W:
                keyPressedActionHandler(HANDLER_ATTACK_UP, ke, keyHeldId);
                break;
            case KeyEvent.VK_H:
                keyPressedActionHandler(HANDLER_RANGED_ATTACK_RIGHT, ke, keyHeldId);
                break;
            case KeyEvent.VK_F:
                keyPressedActionHandler(HANDLER_RANGED_ATTACK_LEFT, ke, keyHeldId);
                break;
            case KeyEvent.VK_T:
                keyPressedActionHandler(HANDLER_RANGED_ATTACK_UP, ke, keyHeldId);
                break;
            case KeyEvent.VK_G:
                keyPressedActionHandler(HANDLER_RANGED_ATTACK_DOWN, ke, keyHeldId);
                break;
            case KeyEvent.VK_F3:
                keyPressedActionHandler(HANDLER_DISPLAY_STATUS, ke, keyHeldId);
                break;
            case KeyEvent.VK_U:
                keyPressedActionHandler(HANDLER_DUMP_OBJECTS, ke, keyHeldId);
                break;
            case KeyEvent.VK_L:
                keyPressedActionHandler(HANDLER_NEXT_LEVEL, ke, keyHeldId);
                break;
            case KeyEvent.VK_R:
                keyPressedActionHandler(HANDLER_RESTART_GAME, ke, keyHeldId);
                break;
            case KeyEvent.VK_I:
                keyPressedActionHandler(HANDLER_INVENTORY, ke, keyHeldId);
                break;
            case KeyEvent.VK_P:
                //Use 'P' key to change between playable characters
                keyPressedActionHandler(HANDLER_CHANGE_CHARACTER, ke, keyHeldId);
                break;
            case KeyEvent.VK_Z:
                //Use 'Z' key for player to perform an action on an item (for example open a chest)
                keyPressedActionHandler(HANDLER_OBJECT_ACTION, ke, keyHeldId);
                break;
            case KeyEvent.VK_F1:
                keyPressedActionHandler(HANDLER_DISPLAY_FRAMERATE, ke, keyHeldId);
                break;
            case KeyEvent.VK_F4:
                keyPressedActionHandler(HANDLER_BOUNDSONLY_MODE, ke, keyHeldId);
                break;
            case KeyEvent.VK_F5:
                keyPressedActionHandler(HANDLER_BORDERSONLY_MODE, ke, keyHeldId);
                break;
            case KeyEvent.VK_F6:
                keyPressedActionHandler(HANDLER_DEBUG_MODE, ke, keyHeldId);
                break;
            case KeyEvent.VK_F7:
                keyPressedActionHandler(HANDLER_ENABLE_SOUNDEFFECTS, ke, keyHeldId);
                break;
            case KeyEvent.VK_F8:
                keyPressedActionHandler(HANDLER_GAME_PAUSED, ke, keyHeldId);
                break;
            default:
        }
    }

    @Override
    public void keyReleased(KeyEvent e)
    {
        int keyCode = e.getKeyCode();

        switch (keyCode)
        {
            case KeyEvent.VK_RIGHT:
                keyReleasedActionHandler(HANDLER_MOVE_RIGHT);
                break;
            case KeyEvent.VK_LEFT:
                keyReleasedActionHandler(HANDLER_MOVE_LEFT);
                break;
            case KeyEvent.VK_DOWN:
                keyReleasedActionHandler(HANDLER_MOVE_DOWN);
                break;
            case KeyEvent.VK_UP:
                keyReleasedActionHandler(HANDLER_MOVE_UP);
                break;
            case KeyEvent.VK_Z:
                keyReleasedActionHandler(HANDLER_OBJECT_ACTION);
                break;
        }
    }

    protected void keyPressedActionHandler(int action, KeyEvent ke, int keyHeldId)
    {
        //Handle corresponding input command
        Player player = Player.getActivePlayer();

        if (player == null)
            return; // no active player yet

        //Previously was getting the keycode for the input, which was not the same as the int assigned to each input type
        keysHeld.add(action);

        switch (action)
        {
            case HANDLER_MOVE_RIGHT:
                player.moveRight(ke.isControlDown());
                break;
            case HANDLER_MOVE_LEFT:
                player.moveLeft(ke.isControlDown());
                break;
            case HANDLER_MOVE_UP:
                player.moveUp(ke.isControlDown());
                break;
            case HANDLER_MOVE_DOWN:
                player.moveDown(ke.isControlDown());
                break;
            case HANDLER_ATTACK_RIGHT:
                break;
            case HANDLER_ATTACK_LEFT:
                break;
            case HANDLER_ATTACK_UP:
                break;
            case HANDLER_ATTACK_DOWN:
                break;
            case HANDLER_RANGED_ATTACK_DOWN:
                break;
            case HANDLER_RANGED_ATTACK_RIGHT:
                player.attack();
                break;
            case HANDLER_RANGED_ATTACK_LEFT:
                break;
            case HANDLER_RANGED_ATTACK_UP:
                break;
            case HANDLER_ATTACK:
                player.attack();
                break;
            case HANDLER_INVENTORY:
                //Toggle character inventory
                break;
            case HANDLER_JUMP:
                // buffer the jump input so timing is more forgiving (coyote time, jump buffering)
                //player.bufferJump();
                //Change requested by Arthur to fix wall jumping
                player.jump();
                player.setJumpHeld(true);
                break;
            case HANDLER_DASH:
                player.dash();
                break;
            case HANDLER_OBJECT_ACTION:
                break;
            case HANDLER_NEXT_LEVEL:
                break;
            case HANDLER_DUMP_OBJECTS:
                break;
            case HANDLER_CHANGE_CHARACTER:
                //Change between playable characters
                GameThread.changePlayableCharacter();
                break;
            case HANDLER_RESTART_GAME:
                break;
            case HANDLER_DISPLAY_STATUS:
                break;
            case HANDLER_DISPLAY_FRAMERATE:
                //Activate/Deactivate display of frame rate and update rate
                GameThread.toggleDisplayFrameUpdateRate();
                break;
            case HANDLER_BOUNDSONLY_MODE:
                //Display only collision bound rectangles mode
                GameObject.drawBoundsRect = !GameObject.drawBoundsRect;
                if (GameObject.drawBoundsRect)
                {
                    GameObject.disableRendering = true;
                    GameObject.drawSpriteBorders = false;
                }
                else
                    GameObject.disableRendering = false;
                break;
            case HANDLER_BORDERSONLY_MODE:
                //Display only sprite borders mode
                GameObject.drawSpriteBorders = !GameObject.drawSpriteBorders;
                if (GameObject.drawSpriteBorders)
                {
                    GameObject.disableRendering = true;
                    GameObject.drawBoundsRect = false;
                }
                else
                    GameObject.disableRendering = false;
                break;
            case HANDLER_GAME_PAUSED:
                break;
            case HANDLER_DEBUG_MODE:
                //Enable/Disable debug info
                break;
            case HANDLER_ENABLE_SOUNDEFFECTS:
                //Enable/Disable sound effects
                GameThread.enableSoundEffects = !GameThread.enableSoundEffects;
                break;
        }
    }

    protected void keyReleasedActionHandler(int action)
    {
        Player player = Player.getActivePlayer();
        if (player == null)
            return;

        keysHeld.remove(action);

        switch (action)
        {
            case HANDLER_MOVE_RIGHT:
                if (!keysHeld.contains(HANDLER_MOVE_LEFT)) //Make sure player isn't trying to move in the opposite direction
                    player.stopX();
                else
                    player.moveLeft(/*player.isRunning()*/false);
                break;
            case HANDLER_MOVE_LEFT:
                if (!keysHeld.contains(HANDLER_MOVE_RIGHT)) //Make sure player isn't trying to move in the opposite direction
                    player.stopX();
                else
                    player.moveRight(/*player.isRunning()*/false);
                break;
            case HANDLER_MOVE_UP:
                // Since right now we have no upward movement, commenting this stops the player's movement from stuttering
                // when the UP key is pressed.

                /*if (!keysHeld.contains(HANDLER_MOVE_DOWN)) //Make sure player isn't trying to move in the opposite direction
                    player.stopY();*/
                break;
            case HANDLER_MOVE_DOWN:
                // Same reasoning as the previous, pressing this key currently causes a movement stutter.
                // Can be revisited when increased fall speed is implemented

                /*if (!keysHeld.contains(HANDLER_MOVE_UP)) //Make sure player isn't trying to move in the opposite direction
                    player.stopY();*/
                break;
            case HANDLER_JUMP:
                // Cancel any buffered jump input on release
                player.cancelJumpBuffer();
                player.setJumpHeld(false);
                break;
        }
    }
}
