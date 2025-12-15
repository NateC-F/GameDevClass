package gameframework.inputhandlers;

import gameframework.GameData;
import gameframework.display.GameDisplay;
import gameframework.gamecharacters.Player;

import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import javax.swing.event.MouseInputListener;

/*
 * This is class is used to handle all mouse events
 * to the game window, most are used to control and
 * trigger certain actions of the player.
 */
public class MouseHandler implements MouseInputListener,
        MouseMotionListener
{
    protected GameDisplay display;
    private GameData    data;
    public static int targetX;
    public static int targetY;

    public MouseHandler(GameDisplay display, GameData data)
    {
        this.display = display;
        this.data = data;
    }

    @Override
    public void mouseClicked(MouseEvent me)
    {





    }

    @Override
    public void mousePressed(MouseEvent me)
    {
//        JOptionPane.showMessageDialog(null,
//                     "X: " + (me.getX() + display.getCameraPosition().x) +
//                     " Y: " + (me.getY() + display.getCameraPosition().y),
//                     "Mouse Position Info", JOptionPane.INFORMATION_MESSAGE);
//
        if (me.getButton() == MouseEvent.BUTTON1)
        {
            System.out.println("Left button clicked");
            targetX = me.getX() + display.getCameraPosition().x;
            targetY = me.getY() + display.getCameraPosition().y;
            Player.getActivePlayer().specialActionA(true);

        }
        else if (me.getButton() == MouseEvent.BUTTON2)
        {
            System.out.println("Middle button clicked");
            Player.getActivePlayer().specialActionB(true);
        }
        else if (me.getButton() == MouseEvent.BUTTON3)
        {
            System.out.println("Right button clicked");
            Player.getActivePlayer().specialActionC(true);
        }
    }

    @Override
    public void mouseReleased(MouseEvent me)
    {
        if (me.getButton() == MouseEvent.BUTTON1)
        {
            System.out.println("Left button released");
            Player.getActivePlayer().specialActionA(false);

        }
        else if (me.getButton() == MouseEvent.BUTTON2)
        {
            System.out.println("Middle button released");
            Player.getActivePlayer().specialActionB(false);
        }
        else if (me.getButton() == MouseEvent.BUTTON3)
        {
            System.out.println("Right button released");
            Player.getActivePlayer().specialActionC(false);
        }
    }

    @Override
    public void mouseEntered(MouseEvent e)
    {
    }

    @Override
    public void mouseExited(MouseEvent e)
    {
    }

    @Override
    public void mouseDragged(MouseEvent e)
    {
    }

    @Override
    public void mouseMoved(MouseEvent e)
    {
    }

}

