package gameframework.display;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;

import gameframework.GameData;
import gameframework.GameLevel;
import gameframework.GameThread;
import gameframework.gamecharacters.Player;
import gameframework.gameobjects.GameObject;
import gameframework.inputhandlers.KeyboardHandler;
import gameframework.inputhandlers.MouseHandler;

/*
 * This is the game display class which is in charge of all rendering to the screen, the class is inherited
 * from JFrame. To draw we are using a buffering strategy instead of regular paint in order to avoid flicker
 * and have smoother animations.
 */
public class GameDisplay extends JFrame
{
    private GameData data;
    private static int displayWidth;
    private static int displayHeight;

    private BufferStrategy bufferStrategy;

    // input handlers
    private KeyboardHandler keyboardHandler;
    private MouseHandler mouseHandler;

    // camera attributes
    private static Point cameraOrigin;

    // Heads Up Display panel
    private static HUDPanel hud = null;  //The heads up display is initially null (game developers are supposed to set it)

    /* We intend to be able to toggle a message on the game window, these attributes hold the properties of
     * how that message displays (like position (X & Y offsets) and text color), for example we can use it to
     * show game information like frame rates, update rates, etc */
    private final static int DEFAULT_MESSAGE_OFFSET = 100;   //Determines X & Y offsets of message position, in pixels
    private final static Font DEFAULT_MESSAGE_FONT = new Font("Arial", Font.BOLD, 42);
    private String message;
    private Color messageColor;
    private Font messageFont;
    private int messageOffsetX;
    private int messageOffsetY;
    /****/

    private static BufferedImage background;

    public GameDisplay(GameData data)
    {
        super();
        setBounds(0,0, displayWidth, displayHeight);
        setVisible(true);
        setFocusable(true);
        setResizable(false);
        this.data = data;
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // initialize camera origin
        cameraOrigin = new Point(0,0);

        // setup buffer strategy of 2 buffers/layers
        createBufferStrategy(2);
        bufferStrategy = getBufferStrategy();

        // load game background
        GameLevel level = GameThread.getCurrentLevel();
        background = level != null ?
                GameThread.resourceManager.loadImageResource(level.getBackground(), level.getName()):
                null;

        // add input handlers for keyboard, mouse, controllers, etc
        keyboardHandler = new KeyboardHandler();
        addKeyListener(keyboardHandler);
        mouseHandler = new MouseHandler(this, data);
        addMouseListener(mouseHandler);
        addMouseMotionListener(mouseHandler);

        setMessage("");
        setMessageColor(Color.WHITE);
        messageFont = DEFAULT_MESSAGE_FONT;
        messageOffsetX = DEFAULT_MESSAGE_OFFSET;
        messageOffsetY = DEFAULT_MESSAGE_OFFSET;
    }

    public void setData(GameData data)
    {
        if (data != null)
            this.data = data;
    }

    public static BufferedImage getCurBackground() { return background; }

    public String getMessage()
    {
        return message;
    }

    public void setMessage(String message)
    {
        // The message can be an empty string "" (we set it to that to hide it)
        this.message = message;
    }

    public Color getMessageColor()
    {
        return messageColor;
    }

    public void setMessageColor(Color messageColor)
    {
        if (messageColor != null)
            this.messageColor = messageColor;
    }

    public int getMessageOffsetX()
    {
        return messageOffsetX;
    }

    public int getMessageOffsetY()
    {
        return messageOffsetY;
    }

    public void setMessageOffsets(int offsetX, int offsetY)
    {
        if (offsetX > 0 && offsetX < displayWidth &&
                offsetY > 0 && offsetY < displayHeight
        )
        {
            messageOffsetX = offsetX;
            messageOffsetY = offsetY;
        }
    }

    public static void setDisplayResolution(int newDisplayWidth, int newDisplayHeight)
    {
        if (newDisplayWidth > 0 && newDisplayHeight > 0)
        {
            displayWidth = newDisplayWidth;
            displayHeight = newDisplayHeight;
        }
    }

    public void changeKeyboardHandler(KeyboardHandler newKeyboardHandler)
    {
        removeKeyListener(keyboardHandler);
        addKeyListener(newKeyboardHandler);
    }

    public void render()
    {
        Graphics g = bufferStrategy.getDrawGraphics();

        //make sure we are using the proper background for this level
        GameLevel level = GameThread.getCurrentLevel();
        background = level != null ?
                GameThread.resourceManager.loadImageResource(level.getBackground(), level.getName()):
                null;

        Player player = Player.getActivePlayer();
        setCameraPos(g, new Point(player.getX(), player.getY()));
        drawCameraScreen(g);

        /* This code is in charge of rendering the objects in the game to the display. We are
         * optimizing the rendering process by only drawing those objects that are currently
         * showing on the screen. */
        for (GameObject object : data.getObjects())
        {
            if ( objectWithinCameraView(object) )
                object.render(g);
        }

        //render heads up display if available
        renderHUD(g);

        renderDisplayMessage(g);

        g.dispose();
        bufferStrategy.show();

    }

    private void renderHUD(Graphics g)
    {
        if (hud != null && hud.isEnabled())
            hud.render(g);
    }

    //If a message is set then display it on top of the screen
    private void renderDisplayMessage(Graphics g)
    {
        if ( !message.isEmpty())
        {
            g.setColor(messageColor);
            g.setFont(messageFont);
            g.drawString(message, cameraOrigin.x + messageOffsetX,
                    cameraOrigin.y + messageOffsetY);
        }
    }

    private BufferedImage createCameraScreen()
    {
        BufferedImage cameraScreenImage;

        /* The background dimensions have to be both bigger than the display dimensions to create
         * a screen subimage, and be able to do the scroll through large background effect. Otherwise
         * the whole background is simply scaled every time to fully fit within the display window.*/
        if (background.getWidth() >= displayWidth &&
                background.getHeight() >= displayHeight )
            cameraScreenImage = background.getSubimage(cameraOrigin.x, cameraOrigin.y,
                    displayWidth, displayHeight);
        else
            cameraScreenImage = background;

        return cameraScreenImage;
    }

    private void drawCameraScreen(Graphics g)
    {
        BufferedImage cameraScreen =
                createCameraScreen();
        g.drawImage(cameraScreen, cameraOrigin.x, cameraOrigin.y,  displayWidth, displayHeight, null);

    }

    public Point getCameraPosition()
    {
        return cameraOrigin;
    }

    /* Set the camera position (camera origin attribute) so that the display is centered in the
     * coordinates passed as camera center, whenever the display is rendered we will only see the
     * section of the large background at the current camera position.
     */
    public void setCameraPos(Graphics g, Point cameraCenter)
    {
        cameraOrigin.x = cameraCenter.x - displayWidth / 2;
        cameraOrigin.y = cameraCenter.y - displayHeight / 2;

        /* This section of the code makes sure to adjust the display screen section properly
         * once the camera is moving close to the bounds of the background, that is keep the
         * camera fixed if we are reaching any of the ends of the game level background
         * (no more data to scroll to).
         */
        if (cameraOrigin.x < 0)
            cameraOrigin.x = 0;
        if (cameraOrigin.y < 0)
            cameraOrigin.y = 0;
        if ( cameraOrigin.x > background.getWidth() - displayWidth)
            cameraOrigin.x = background.getWidth() - displayWidth;
        if ( cameraOrigin.y > background.getHeight() - displayHeight)
            cameraOrigin.y = background.getHeight() - displayHeight;

        //This call effectively sets the origin of the graphics context to our desired camera position.
        g.translate(-cameraOrigin.x, -cameraOrigin.y);
    }

    //Returns true if object is within the current camera view.
    public static boolean objectWithinCameraView(GameObject go)
    {
        boolean withinView = false;
        Rectangle cameraBounds = new Rectangle(cameraOrigin.x, cameraOrigin.y,
                displayWidth, displayHeight);

        if (
                go.isWithinBounds(cameraBounds)
        )
            withinView = true;

        return withinView;
    }

    /**
     * set up the heads up display
     */
    public static HUDPanel getHUD()
    {
        return hud;
    }
    public static void setHUD(HUDPanel newHUD)
    {
        if (newHUD != null)
            hud = newHUD;
    }
    public static void activateHUD(boolean on)
    {
        hud.enable(on);
    }
    /**/


}
