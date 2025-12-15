package gameframework.display;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * This class implements the heads up display for games, its the area in the
 * game screen that shows the player's health status, lives, score and other
 * important info */
public abstract class HUDPanel
{

    //Relative position of HUD panel within the display screen
    private int posX;
    private int posY;

    //Dimensions of HUD panel
    private int width;
    private int height;

    private boolean transparentBackground = true;    //By default assume background is transparent
    //In the case the background isn't transparent this attributes
    //determine either the color or image background to use.
    private Color backgroundColor = null;
    private BufferedImage backgroundImage = null;

    private boolean enabled;

    private GameDisplay gameDisplay;

    public HUDPanel(GameDisplay gameDisplay)
    {
        this.gameDisplay = gameDisplay;
    }

    public HUDPanel(int posX, int posY, int width, int height, GameDisplay gameDisplay)
    {
        //set panel position relative to the game screen and initialize its size
        this.posX = posX;
        this.posY = posY;
        this.width = width;
        this.height = height;

        this.gameDisplay = gameDisplay;
    }

    public Rectangle getBounds()
    {
        return new Rectangle(posX, posY, width, height);
    }

    public void render(Graphics g)
    {
        Rectangle rect = getBounds();
        Point cameraPos = gameDisplay.getCameraPosition();
        rect.x += cameraPos.x;
        rect.y += cameraPos.y;

        if (backgroundColor != null)
        {
            //paint background with solid color
            g.setColor(backgroundColor);
            g.drawRect(rect.x, rect.y, rect.width, rect.height);
        }

        if (backgroundImage != null)
            g.drawImage(backgroundImage, rect.x, rect.y, rect.width, rect.height, null);

        displayContents(g);

    }

    public void reposition(int newPosX, int newPosY)
    {
        posX = newPosX;
        posY = newPosY;
    }

    public int convertToHUDCoords(int relativePos, char xOrY)
    {
        Point cameraPos = gameDisplay.getCameraPosition();
        if (xOrY == 'X')
            return relativePos + posX + cameraPos.x;
        else
            return relativePos + posY + cameraPos.y;
    }

    public boolean isEnabled()
    {
        return enabled;
    }

    public void enable(boolean enabled)
    {
        this.enabled = enabled;
    }

    //Developers must override this method to implement their own HUD display
    public abstract void displayContents(Graphics g);

    public boolean isTransparentBackground()
    {
        return transparentBackground;
    }

    public void setTransparentBackground(boolean updatedTransparentBackground)
    {
        transparentBackground = updatedTransparentBackground;
    }

    public Color getBackgroundColor()
    {
        return backgroundColor;
    }

    public void setBackgroundColor(Color newBackgroundColor)
    {
        if (newBackgroundColor != null)
            backgroundColor = newBackgroundColor;
    }

    public BufferedImage getBackgroundImage()
    {
        return backgroundImage;
    }

    public void setBackgroundImage(BufferedImage newBackgroundImage)
    {
        if (newBackgroundImage != null)
            backgroundImage = newBackgroundImage;
    }
}
