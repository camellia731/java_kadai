package game;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.io.FileInputStream;


public class Object {
    protected int x;
    protected int y;
    protected int width;
    protected int height;
    protected BufferedImage image;
    protected String type;
    public static final int FALL_SPEED = 4;

    public Object(int x, int y, String type) {
        this.x = x;
        this.y = y;
        this.type = type;
        try {
            loadImage();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (image != null) {
            width = image.getWidth();
            height = image.getHeight();
        }
    }

    protected void loadImage() throws IOException {
        switch (type) {
            case "apple":
                image = ImageIO.read(new FileInputStream("game/resources/apple.png"));
                break;
            case "banana":
                image = ImageIO.read(new FileInputStream("game/resources/banana.png"));
                break;
            case "coconut":
                image = ImageIO.read(new FileInputStream("game/resources/coconut.png"));
                break;
            case "branch":
                image = ImageIO.read(new FileInputStream("game/resources/branch.png"));
                break;

        }
    }

    public void update() {
        y += FALL_SPEED;
    }

    public void render(Graphics2D g) {
        if (image != null) {
            g.drawImage(image, x, y, null);
        }
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}

class Item extends Object {
    private int scoreValue;

    public Item(int x, int y, String type) {
        super(x, y, type);
        switch (type) {
            case "apple":
                scoreValue = 1;
                break;
            case "banana":
                scoreValue = 3;
                break;
        }
    }

    public int getScoreValue() {
        return scoreValue;
    }
}

class Obstacle extends Object {
    private int damage;

    public Obstacle(int x, int y, String type) {
        super(x, y, type);
        switch (type) {
            case "coconut":
                damage = 10;
                break;
            case "branch":
                damage = 20;
                break;
        }
    }

    public int getDamage() {
        return damage;
    }
}