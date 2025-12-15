package gameframework.display;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

public class StartMenu extends JPanel {

    private static final int BUTTON_WIDTH = 350;
    private static final int BUTTON_HEIGHT = 60;
    private static final Font BUTTON_FONT = new Font("Monospaced", Font.BOLD, 36);

    private static Rectangle playButton;
    private static Rectangle quitButton;
    private static boolean playHover = false;
    private static boolean quitHover = false;

    public static BufferedImage backgroundImage;
    private static MenuListener menuListener;
    private Font pixelFont;

    public interface MenuListener {
        void onPlayClicked();
        void onQuitClicked();
    }

    public StartMenu(int width, int height, BufferedImage background) {

        backgroundImage = background;
        setPreferredSize(new Dimension(width, height));
        setLayout(null);

        try {
            pixelFont = new Font("Monospaced", Font.BOLD, 72);
        } catch (Exception e) {
            pixelFont = new Font("Monospaced", Font.BOLD, 72);
        }

        int centerX = width / 2;
        int centerY = height / 2;

        playButton = new Rectangle(centerX - BUTTON_WIDTH / 2, centerY - 50,
                BUTTON_WIDTH, BUTTON_HEIGHT);
        quitButton = new Rectangle(centerX - BUTTON_WIDTH / 2, centerY + 80,
                BUTTON_WIDTH, BUTTON_HEIGHT);

        addMouseListener(new MouseAdapter() {

            @Override
            public void mousePressed(MouseEvent e) {
                Point p = e.getPoint();

                if (playButton.contains(p)) {
                    if (menuListener != null)
                        menuListener.onPlayClicked();
                }
                else if (quitButton.contains(p)) {
                    if (menuListener != null)
                        menuListener.onQuitClicked();
                }
                removeAll();
            }
        });

        addMouseMotionListener(new MouseAdapter() {

            @Override
            public void mouseMoved(MouseEvent e) {
                Point p = e.getPoint();

                boolean oldPlay = playHover;
                boolean oldQuit = quitHover;

                playHover = playButton.contains(p);
                quitHover = quitButton.contains(p);

                if (oldPlay != playHover || oldQuit != quitHover) {
                    repaint();
                }
                removeAll();
            }

        });
    }

    public void setMenuListener(MenuListener listener) {
        menuListener = listener;
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;

        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        if (backgroundImage != null) {
            int bgHeight = backgroundImage.getHeight();
            int bgWidth = backgroundImage.getWidth();
            int cropWidth = (int)(bgWidth * 0.3);
            int cropHeight = (int)(bgHeight * 0.4);
            BufferedImage mountainSection =
                    backgroundImage.getSubimage(0, 0, cropWidth, cropHeight);

            g2d.drawImage(mountainSection, 0, 0, getWidth(), getHeight(), null);
        } else {
            g2d.setColor(new Color(40, 20, 60));
            g2d.fillRect(0, 0, getWidth(), getHeight());
        }

        g2d.setColor(new Color(0, 0, 0, 100));
        g2d.fillRect(0, 0, getWidth(), getHeight());

        g2d.setFont(pixelFont);
        g2d.setColor(Color.WHITE);

        String title = "THE FIRST NINJA";
        FontMetrics fm = g2d.getFontMetrics();
        int titleX = (getWidth() - fm.stringWidth(title)) / 2;
        int titleY = getHeight() / 2 - 200;

        g2d.setColor(new Color(0, 0, 0, 150));
        g2d.drawString(title, titleX + 3, titleY + 3);

        g2d.setColor(new Color(220, 50, 50));
        g2d.drawString(title, titleX, titleY);

        drawButton(g2d, playButton, "PLAY", playHover);
        drawButton(g2d, quitButton, "QUIT", quitHover);
    }

    private void drawButton(Graphics2D g2d, Rectangle button,
                            String text, boolean hovered) {

        g2d.setColor(hovered
                ? new Color(220, 50, 50, 220)
                : new Color(100, 30, 30, 180));

        g2d.fillRoundRect(button.x, button.y,
                button.width, button.height, 20, 20);

        g2d.setColor(Color.WHITE);
        g2d.setStroke(new BasicStroke(3));
        g2d.drawRoundRect(button.x, button.y,
                button.width, button.height, 20, 20);

        g2d.setFont(BUTTON_FONT);
        FontMetrics fm = g2d.getFontMetrics();

        int textX = button.x + (button.width - fm.stringWidth(text)) / 2;
        int textY = button.y + ((button.height - fm.getHeight()) / 2)
                + fm.getAscent();

        g2d.setColor(new Color(0, 0, 0, 150));
        g2d.drawString(text, textX + 2, textY + 2);

        g2d.setColor(Color.WHITE);
        g2d.drawString(text, textX, textY);
    }
}

