package game;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;
import java.io.FileInputStream;

public class BackGround {
    private List<BufferedImage> backgrounds;
    private List<Integer> bgYPositions;
    private int speed;
    BufferedImage sky;

    public BackGround() {
        backgrounds = new ArrayList<>();
        bgYPositions = new ArrayList<>();
        speed = 2;

        try {
            sky = ImageIO.read(new FileInputStream("game/resources/sky.png"));
            backgrounds.add(ImageIO.read(new FileInputStream("game/resources/tree0.png")));
            backgrounds.add(ImageIO.read(new FileInputStream("game/resources/tree1.png")));
            //backgrounds.add(ImageIO.read(new FileInputStream("game/resources/tree_segment2.png")));
            //backgrounds.add(ImageIO.read(new FileInputStream("game/resources/tree_segment3.png")));
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (int i = 0; i < backgrounds.size(); i++) {
            bgYPositions.add(i * -GameMain.HEIGHT);
        }
    }

    public void update() {
        for (int i = 0; i < bgYPositions.size(); i++) {
            bgYPositions.set(i, bgYPositions.get(i) + speed);
            if (bgYPositions.get(i) >= GameMain.HEIGHT) {
                bgYPositions.set(i, -GameMain.HEIGHT * (backgrounds.size() - 1));
            }
        }
    }

    public void render(Graphics2D g) {
        g.drawImage(sky, null, 0,0 );
        for (int i = 0; i < backgrounds.size(); i++) {
            g.drawImage(backgrounds.get(i), 100, bgYPositions.get(i), null);
        }
        
    }
}