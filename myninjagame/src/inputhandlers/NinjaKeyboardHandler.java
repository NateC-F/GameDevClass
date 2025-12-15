package inputhandlers;

import gameframework.GameThread;
import gameframework.gamecharacters.Player;
import gameframework.gameobjects.GameObject;
import gameframework.inputhandlers.KeyboardHandler;
import java.awt.event.KeyEvent;

public class NinjaKeyboardHandler extends KeyboardHandler
{
    private static int direction = 0;
    @Override
    public void keyPressed(KeyEvent ke)
    {
        int keyCode = ke.getKeyCode();
        int keyHeldId = -1;

        switch (keyCode)
        {
            /* Allow character to be moved with the 'D','A','S' and 'W' in addition to
             * the arrow keys, that are the default trigger for those actions in the engine,
             * if you want to disable or change the functionality of the arrow keys, you need
             * to add entries here for them.
             */
            case KeyEvent.VK_D:
                keyPressedActionHandler(HANDLER_MOVE_RIGHT, ke, keyHeldId);
                direction = 2;
                break;
            case KeyEvent.VK_A:
                keyPressedActionHandler(HANDLER_MOVE_LEFT, ke, keyHeldId);
                direction = 1;
                break;
            case KeyEvent.VK_S:
                keyPressedActionHandler(HANDLER_MOVE_DOWN, ke, keyHeldId);
                break;
            case KeyEvent.VK_W:
                keyPressedActionHandler(HANDLER_MOVE_UP, ke, keyHeldId);
                break;
            case KeyEvent.VK_E:
                keyPressedActionHandler(HANDLER_RANGED_ATTACK_RIGHT, ke, keyHeldId);
                break;
            case KeyEvent.VK_SPACE:
                keyPressedActionHandler(HANDLER_JUMP, ke, keyHeldId);
                break;
            /*******/
            default:
                //any non overriden keys use the engine default
                super.keyPressed(ke);
        }
    }

    @Override
    public void keyReleased(KeyEvent ke)
    {
        int keyCode = ke.getKeyCode();

        switch (keyCode)
        {
            case KeyEvent.VK_D:
                keyReleasedActionHandler(HANDLER_MOVE_RIGHT);
                direction = 0;
                break;
            case KeyEvent.VK_A:
                keyReleasedActionHandler(HANDLER_MOVE_LEFT);
                direction = 0;
                break;
            case KeyEvent.VK_S:
                keyReleasedActionHandler(HANDLER_MOVE_DOWN);
                break;
            case KeyEvent.VK_W:
                keyReleasedActionHandler(HANDLER_MOVE_UP);
                break;
            case KeyEvent.VK_E:
                keyReleasedActionHandler(HANDLER_RANGED_ATTACK_RIGHT);
                break;
            case KeyEvent.VK_SPACE:
                keyReleasedActionHandler(HANDLER_JUMP);
                break;
            default:
                super.keyReleased(ke);
        }
    }

   /* @Override
    protected void keyPressedActionHandler(int action, KeyEvent ke, int keyHeldId)
    {
        //Handle corresponding input command
        Player player = Player.getActivePlayer();
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
                player.setGravity(20);
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
                player.jump();
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
                GameThread.displayFrameUpdateRate = !GameThread.displayFrameUpdateRate;
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
                break;
        }
    }

    @Override
    protected void keyReleasedActionHandler(int action)
    {
        Player player = Player.getActivePlayer();
        keysHeld.remove(action);

        switch (action)
        {
            case HANDLER_MOVE_RIGHT:
                if (!keysHeld.contains(HANDLER_MOVE_LEFT)) //Make sure player isn't trying to move in the opposite direction
                    player.stopX();
                break;
            case HANDLER_MOVE_LEFT:
                if (!keysHeld.contains(HANDLER_MOVE_RIGHT)) //Make sure player isn't trying to move in the opposite direction
                    player.stopX();
                break;
            case HANDLER_MOVE_UP:
                if (!keysHeld.contains(HANDLER_MOVE_DOWN)) //Make sure player isn't trying to move in the opposite direction
                    player.stopY();
                break;
            case HANDLER_MOVE_DOWN:
                if (!keysHeld.contains(HANDLER_MOVE_UP)) //Make sure player isn't trying to move in the opposite direction
                    player.stopY();
                player.setGravity(0.3);
                break;
        }
    }*/

    @Override
    protected void keyPressedActionHandler(int action, KeyEvent ke, int keyHeldId)
    {
        //Handle corresponding input command
        Player player = Player.getActivePlayer();
        keysHeld.add(action);

        switch (action)
        {
            case HANDLER_MOVE_DOWN:
                player.moveDown(ke.isControlDown());
                player.setGravity(20);
                break;
            default:
                super.keyPressedActionHandler(action, ke, keyHeldId);
        }
    }

    @Override
    protected void keyReleasedActionHandler(int action)
    {
        Player player = Player.getActivePlayer();
        keysHeld.remove(action);

        switch (action)
        {
            case HANDLER_MOVE_DOWN:
                if (!keysHeld.contains(HANDLER_MOVE_UP)) //Make sure player isn't trying to move in the opposite direction
                    player.stopY();
                player.setGravity(0.3);
                break;
            default:
                super.keyReleasedActionHandler(action);
        }
    }

    public static int getDirection(){
        // 0 = no direction, 1 = left, 2 = right
        return direction;
    }

}
