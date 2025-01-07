package game;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.io.FileInputStream;

public class Player {
    private int x;
    private int y;
    private int width;
    private int height;
    private BufferedImage imageCenter;
    private BufferedImage imageLeft;
    private BufferedImage imageRight;
    private BufferedImage currentImage;

    public Player() {
        x = GameMain.WIDTH / 2 - 20;
        y = GameMain.HEIGHT - 100;
        try {
            imageCenter = ImageIO.read(new FileInputStream("game/resources/monkey_idle.png"));
            imageLeft = ImageIO.read(new FileInputStream("game/resources/monkey_climb_left1.png"));
            imageRight = ImageIO.read(new FileInputStream("game/resources/monkey_climb_right1.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        currentImage = imageCenter;
        width = currentImage.getWidth();
        height = currentImage.getHeight();
    }

    public void update() {

    }

    public void render(Graphics2D g) {
        g.drawImage(currentImage, x, y, null);
    }

    public void moveLeft() {
        int LEFT_MAX = 100;
        x -= 5;
        if (x < LEFT_MAX) {
            x = LEFT_MAX;
        }
        currentImage = imageLeft;
    }

    public void moveRight() {
        int RIGHT_MAX = GameMain.WIDTH-width-100;
        x += 5;
        if (x > RIGHT_MAX) {
            x = RIGHT_MAX;
        }
        currentImage = imageRight;
    }

    public void stay() {
        currentImage = imageCenter;
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }
}