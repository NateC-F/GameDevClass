package hud;

import gameframework.display.GameDisplay;
import gameframework.display.HUDPanel;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

/**
 * A simple test HUD that displays static info to confirm
 * the HUD system is rendering correctly.
 */
public class BasicHUD extends HUDPanel
{
    private int score;
    private int lives;

    public BasicHUD(GameDisplay gameDisplay)
    {
        super(100, 100, 200, 80, gameDisplay); // position and size on screen
        this.score = 0;
        this.lives = 3;
        setBackgroundColor(new Color(0, 0, 0, 150)); // semi-transparent black
        setTransparentBackground(false);
        enable(true);
    }

    @Override
    public void displayContents(Graphics g)
    {
        if (!isEnabled())
            return;

        // Draw HUD frame
        g.setColor(Color.WHITE);
        g.drawRect(convertToHUDCoords(0, 'X'), convertToHUDCoords(0, 'Y'), 200, 80);

        // Draw text
        g.setFont(new Font("Times New Roman", Font.BOLD, 14));
        g.setColor(Color.GREEN);
        g.drawString("HUD ACTIVE", convertToHUDCoords(10, 'X'), convertToHUDCoords(20, 'Y'));
        g.setColor(Color.WHITE);
        g.drawString("Score: " + score, convertToHUDCoords(10, 'X'), convertToHUDCoords(40, 'Y'));
        g.drawString("Lives: " + lives, convertToHUDCoords(10, 'X'), convertToHUDCoords(60, 'Y'));
    }

}

