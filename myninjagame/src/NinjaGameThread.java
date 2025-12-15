import gamecharacters.FireSpirit;
import gameframework.display.StartMenu;
import hud.NinjaHUD;
import gamecharacters.ElfPlayer;
import gamecharacters.NinjaPlayer;
import gameframework.GameLevel;
import gameframework.GameThread;
import gameframework.display.GameDisplay;
import gameframework.gameobjects.GameObjectFactory;
import gameobjects.NinjaGameObjectFactory;
import inputhandlers.NinjaKeyboardHandler;
import gameframework.weapons.Weapon;
import gameframework.weapons.WeaponFactory;
import gameweapons.ShurikenFactory;
//import gameweapons.SpecialStarFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class NinjaGameThread extends GameThread
{
    public static final int DISPLAY_WIDTH = 1920 /*1280*/;
    public static final int DISPLAY_HEIGHT = 1080 /*800*/;

    private static JFrame menuFrame;
    private static StartMenu startMenu;
    public volatile static boolean gameStarted = false;

    public NinjaGameThread(GameObjectFactory gameObjectFactory)
    {
        super(gameObjectFactory);
    }

    private static void showStartMenu()
    {

        menuFrame = new JFrame("Ninja Game - Menu");
        menuFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        menuFrame.setResizable(false);

        BufferedImage menuBackground = null;
        try
        {
            menuBackground = resourceManager.loadImageResource("FirstLevel_BG.png", "level1");
        } catch (Exception e)
        {
            System.out.println("Could not load menu background, using default");
        }

        startMenu = new StartMenu(DISPLAY_WIDTH, DISPLAY_HEIGHT, menuBackground);

        startMenu.setMenuListener(new StartMenu.MenuListener()
        {
            @Override
            public void onPlayClicked()
            {

                menuFrame.setVisible(false);
                menuFrame.dispose();
                gameStarted = true;
            }

            @Override
            public void onQuitClicked()
            {
                System.exit(0);
            }
        });

        menuFrame.add(startMenu);
        menuFrame.pack();
        menuFrame.setLocationRelativeTo(null);
        menuFrame.setVisible(true);

    }


    public static void main(String[] args)
    {
        //Put names of objects with no collision here:
        disableCollisionNames.add("gold-coin.png");
        disableCollisionNames.add("silver-coin.png");
        disableCollisionNames.add("Spikes_03.png");
        disableCollisionNames.add("Spikes_04.png");
        disableCollisionNames.add("Pillar_01.png");
        disableCollisionNames.add("Pillar_02.png");
        disableCollisionNames.add("Pillar_03.png");
        disableCollisionNames.add("Pillar_04.png");
        disableCollisionNames.add("gold-key.png");
        disableCollisionNames.add("Chain_01.png");
        disableCollisionNames.add("Chain_02.png");
        disableCollisionNames.add("Chain_03.png");
        disableCollisionNames.add("Chain_04.png");
        disableCollisionNames.add("skull1.png");
        disableCollisionNames.add("skull2.png");
        disableCollisionNames.add("Web_01.png");
        disableCollisionNames.add("Web_02.png");
        disableCollisionNames.add("Web_03.png");
        disableCollisionNames.add("Web_04.png");

        //set game resolution
        GameDisplay.setDisplayResolution(DISPLAY_WIDTH, DISPLAY_HEIGHT);
        //define all game levels
        GameLevel level1 = new GameLevel("level1", 1, "FirstLevel_BG.png",
                "theme-1.wav", new Point(500, 655));
        GameThread.addLevel(level1);

        //create a specific game factory for the game
        NinjaGameObjectFactory ninjaGameObjectFactory =
                new NinjaGameObjectFactory();

        /* set up any required optimizations here*/

        // Lets make characters and objects that are offscreen only update every three update cycles
        GameThread.OFFSCREEN_OBJECTS_UPDATE_INTERVAL = 3;

        // Setup object area optimization, keep objects in different areas of the screen in separate lists
        // to optimize all kinds of object interactions (like collisions, etc)
        GameThread.AREA_GRID_COLS = 6;
        GameThread.AREA_GRID_ROWS = 1;
        /********/

        showStartMenu();
        // freeze this thread until the user presses play in the start menu
        while (!gameStarted);

        //create the game thread and start the engine
        NinjaGameThread ninjaGameThread = new NinjaGameThread(ninjaGameObjectFactory);
        ninjaGameThread.setGameTitle("My Ninja Game.");
        GameThread.changeKeyboardHandler(new NinjaKeyboardHandler());

        //guest player in the game :-) (for the time being)
        ElfPlayer elfPlayer = new ElfPlayer("elf1", 300, 300,
                150, 150);
        GameThread.addPlayableCharacter(elfPlayer, false);

        //create all playable characters for the game
        NinjaPlayer ninjaPlayer = new NinjaPlayer("ninja1", 200, 200,
                130, 130);
        GameThread.addPlayableCharacter(ninjaPlayer, true);

        //RANGED WEAPON TESTING
        WeaponFactory shurikenFactory = new ShurikenFactory();
        Weapon shuriken = shurikenFactory.createWeapon(ninjaPlayer);
        ninjaPlayer.setWeapon(shuriken);
        GameThread.data.addObjectWhenSafe(shuriken);

        //MELEE WEAPON TESTING
        /*WeaponFactory specialStarFactory = new SpecialStarFactory();
        Weapon specialStar = specialStarFactory.createWeapon(ninjaPlayer);
        ninjaPlayer.setWeapon(specialStar);
        GameThread.data.addObjectWhenSafe(specialStar);*/

        //creates the hud of the game
        NinjaHUD HUD = new NinjaHUD(50,70,500,500,display,ninjaPlayer);
        GameDisplay.setHUD(HUD);

        try
        {
            ninjaGameThread.gameRun();
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}   
