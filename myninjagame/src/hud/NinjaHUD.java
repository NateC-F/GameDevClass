package hud;

import gamecharacters.NinjaPlayer;
import gameframework.GameThread;
import gameframework.display.GameDisplay;
import gameframework.display.HUDPanel;
import gameframework.gamecharacters.Player;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class NinjaHUD extends HUDPanel
{
    public final int HEART_VALUE = 100;

    //GUI assets
    private static BufferedImage COIN_SPRITE;
    private static BufferedImage KEY_SPRITE;
    private Font PIXELIFY_SANS;

    //object data
    private ArrayList<Heart> hearts;
    private NinjaPlayer player;
    private int lastHealth;

    public Player getPlayer (){return player;}
    public void setPlayer(NinjaPlayer player)
    {
        if (player != null)
            this.player = player;
    }

    public NinjaHUD(int posX, int posY, int width, int height,
                    GameDisplay gameDisplay, NinjaPlayer player)
    {
        super(posX,posY,width,height,gameDisplay);
        setPlayer(player);
        enable(true);

        setTransparentBackground(true);

        hearts = new ArrayList<>();
        initializeHearts();

        lastHealth = 0;

        COIN_SPRITE =GameThread.resourceManager.loadImageResource("hud/Coin.png","");
        KEY_SPRITE = GameThread.resourceManager.loadImageResource("hud/Key.png","");

        PIXELIFY_SANS = GameThread.resourceManager.loadFontResource("fonts/PixelifySans.ttf",60);
    }

    private void initializeHearts()
    {
        //will be used to generate the amount of hearts a player has initially
        int heartAmount = player.getTotalHealth()/HEART_VALUE;
        while (heartAmount > 0)
        {
            Heart emptyHeart = new Heart(true);
            hearts.add(emptyHeart);
            heartAmount --;
        }

        //will be used to generate the amount of hearts that are full
        int fullHeartAmount = player.getCurHealth()/HEART_VALUE;
        for (Heart heart: hearts)
        {
            if (fullHeartAmount > 0)
                heart.setHeartIsEmpty(false);

            fullHeartAmount --;
        }
    }

    @Override
    public void displayContents(Graphics g)
    {
        int scale = 80;
        int subscale = 48;

        if (!isEnabled()) return;

        //display item box on screen
        ItemBox box = new ItemBox();
        g.drawImage(ItemBox.emptyBox,convertToHUDCoords(0,'X'),
                convertToHUDCoords(0, 'Y'), scale, scale,null);
        //draws the current weapon being held
        g.drawImage(box.initializeCurrentItem(),convertToHUDCoords(0,'X'),convertToHUDCoords(0,'Y'),
                scale,scale,null);

        //checks to update the status of hearts and clears the hearts
        if (player.getCurHealth() != lastHealth)
        {
            hearts.clear();
            initializeHearts();
            lastHealth = player.getCurHealth();
        }
        //draws the hearts
        int xPos = scale;
        for (Heart heart : hearts)
        {
            g.drawImage(heart.getSpriteState(), convertToHUDCoords(xPos, 'X'),
                    convertToHUDCoords(0, 'Y'), scale, scale, null);
            xPos += scale;
        }

        //sets properties of the text
        g.setFont(PIXELIFY_SANS);
        g.setColor(Color.orange);

        //draws amount of coins a player has.
        g.drawImage(COIN_SPRITE,convertToHUDCoords(0,'X'),
                convertToHUDCoords(scale + scale /8,'Y'), scale /2, scale /2,null);

        g.drawString(":"+ player.getScore(),convertToHUDCoords(scale /2,'X'),
                convertToHUDCoords(scale + subscale,'Y'));

        //draws the amount of keys that the player has
        g.drawImage(KEY_SPRITE,convertToHUDCoords(scale*3,'X'),
                convertToHUDCoords(scale + scale /8,'Y'), scale, scale /2,null);
        g.drawString(":"+player.getKeys(),convertToHUDCoords(scale *4,'X'),
                convertToHUDCoords(scale +subscale,'Y'));
    }
}
