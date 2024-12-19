package game;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javax.imageio.ImageIO;

public class GameMain extends JPanel implements Runnable {
    public static final int WIDTH = 600;
    public static final int HEIGHT = 600;
    public static final int CLEAR_SCORE = 30;
    private Thread thread;
    private boolean running;
    private BufferedImage image;
    private Graphics2D g;

    private enum GameState {
        START, PLAYING, PAUSED, GAME_CLEAR, GAME_OVER
    }

    private GameState gameState;
    private InputHandler inputHandler;
    private BackGround backGround;
    private Player player;
    private List<Object> objects;
    private int score;
    private int hp;
    private final int MAX_HP = 120;
    private Font scoreFont;
    private Font hpFont;
    private Font startScreenFont;
    private Font gameOverFont;
    private Font gameClearFont;
    private Random random;
    private boolean pKeyPressed = false;

    public GameMain() {
        super();
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setFocusable(true);
        requestFocus();
        gameState = GameState.START;
        inputHandler = new InputHandler();
        addKeyListener(inputHandler);
        backGround = new BackGround();
        player = new Player();
        objects = new ArrayList<>();
        score = 0;
        hp = MAX_HP;
        scoreFont = new Font("Arial", Font.PLAIN, 24);
        hpFont = new Font("Arial", Font.PLAIN, 24);
        startScreenFont = new Font("Arial", Font.BOLD, 48);
        gameOverFont = new Font("Arial", Font.BOLD, 48);
        gameClearFont = new Font("Arial", Font.BOLD, 48);
        random = new Random();
    }

    public void addNotify() {
        super.addNotify();
        if (thread == null) {
            thread = new Thread(this);
            thread.start();
        }
    }

    public void run() {
        init();

        long lastTime = System.nanoTime();
        double amountOfTicks = 60.0;
        double ns = 1000000000 / amountOfTicks;
        double delta = 0;
        long timer = System.currentTimeMillis();

        while (running) {
            long now = System.nanoTime();
            delta += (now - lastTime) / ns;
            lastTime = now;

            while (delta >= 1) {
                update();
                delta--;
            }

            render();

            if (System.currentTimeMillis() - timer > 1000) {
                timer += 1000;
            }
        }
    }

    private void init() {
        image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        g = (Graphics2D) image.getGraphics();
        running = true;
        gameState = GameState.START;
    }

    private void update() {
        switch (gameState) {
            case START:
                handleStartScreenInput();
                break;
            case PLAYING:
                handlePlayingInput();
                backGround.update();
                player.update();
                spawnObjects();
                updateObjects();
                checkCollisions();
                checkGameOver();
                checkGameClear();
                break;
            case PAUSED:
                handlePausedInput();
                break;
            case GAME_CLEAR:
                handleGameClearInput();
                break;
            case GAME_OVER:
                handleGameOverInput();
                break;
        }
    }

    private void render() {
        Graphics2D g2d = (Graphics2D) getGraphics();
        g2d.drawImage(image, 0, 0, null);
        g2d.dispose();

        g.clearRect(0, 0, WIDTH, HEIGHT);

        switch (gameState) {
            case START:
                drawStartScreen();
                break;
            case PLAYING:
                backGround.render(g);
                player.render(g);
                for (Object obj : objects) {
                    obj.render(g);
                }
                drawScore();
                drawHp();
                break;
            case PAUSED:
                backGround.render(g);
                player.render(g);
                for (Object obj : objects) {
                    obj.render(g);
                }
                drawScore();
                drawHp();
                drawPausedScreen();
                break;
            case GAME_CLEAR:
                drawGameClearScreen();
                break;
            case GAME_OVER:
                drawGameOverScreen();
                break;
        }
    }

    private void handleStartScreenInput() {
        if (inputHandler.isKeyPressed(KeyEvent.VK_ENTER)) {
            gameState = GameState.PLAYING;
            //resetGame();
        }
    }

    private void handlePlayingInput() {
        if (inputHandler.isKeyPressed(KeyEvent.VK_LEFT)) {
            player.moveLeft();
        } else if (inputHandler.isKeyPressed(KeyEvent.VK_RIGHT)) {
            player.moveRight();
        } else {
            player.stay();
        }

        boolean pKeyCurrentlyPressed = 
        inputHandler.isKeyPressed(KeyEvent.VK_P);

        if (pKeyCurrentlyPressed && !pKeyPressed) {
            gameState = GameState.PAUSED;
        }
        pKeyPressed = pKeyCurrentlyPressed; 
    }
    

    private void handlePausedInput() {
        boolean pKeyCurrentlyPressed = 
        inputHandler.isKeyPressed(KeyEvent.VK_P);
        if (pKeyCurrentlyPressed && !pKeyPressed) {
            gameState = GameState.PLAYING;
        }

        pKeyPressed = pKeyCurrentlyPressed;
    }

    private void handleGameOverInput() {
        if (inputHandler.isKeyPressed(KeyEvent.VK_ENTER)) {
            resetGame(); 
            gameState = GameState.START;
        }
    }

    private void handleGameClearInput() {
        if (inputHandler.isKeyPressed(KeyEvent.VK_ENTER)) {
            gameState = GameState.START;
        }
    }

    private void spawnObjects() {
        if (random.nextInt(100) < 2) {
            objects.add(new Item(random.nextInt(WIDTH - 40), 0, "apple"));
        }
        if (random.nextInt(100) < 1) {
            objects.add(new Item(random.nextInt(WIDTH - 40), 0, "banana"));
        }
        if (random.nextInt(100) < 2) {
            objects.add(new Obstacle(random.nextInt(WIDTH - 40), 0, "coconut"));
        }
        if (random.nextInt(100) < 3) {
            objects.add(new Obstacle(random.nextInt(WIDTH - 40), 0, "branch"));
        }
    }

    private void updateObjects() {
        for (int i = 0; i < objects.size(); i++) {
            Object obj = objects.get(i);
            obj.update();
            if (obj.getY() > HEIGHT) {
                objects.remove(i);
                i--;
            }
        }
    }

    private void checkCollisions() {
        Rectangle playerRect = player.getBounds();
        for (int i = 0; i < objects.size(); i++) {
            Object obj = objects.get(i);
            Rectangle objectRect = obj.getBounds();
            if (playerRect.intersects(objectRect)) {
                if (obj instanceof Item) {
                    score += ((Item) obj).getScoreValue();
                    objects.remove(i);
                    i--;
                } else if (obj instanceof Obstacle) {
                    hp -= ((Obstacle) obj).getDamage();
                    objects.remove(i);
                    i--;
                }
            }
        }
    }

    private void checkGameOver() {
        if (hp <= 0) {
            gameState = GameState.GAME_OVER;
        }
    }

    private void checkGameClear() {
        if (score >= CLEAR_SCORE) {
            resetGame();
            gameState = GameState.GAME_CLEAR;
        }
    }

    private void resetGame() {
        score = 0;
        hp = MAX_HP;
        objects.clear();
    }

    private void drawScore() {
        g.setFont(scoreFont);
        g.setColor(Color.BLACK);
        g.drawString("Score: " + score, 10, 30);
    }

    private void drawHp() {
        g.setFont(hpFont);
        g.setColor(Color.RED);
        g.drawString("HP: " + hp, 10, 60);
    }

    private void drawStartScreen() {
        g.setFont(startScreenFont);
        g.setColor(Color.BLUE);
        g.drawString("Monkey Tree Climbing", WIDTH / 2 - 280, HEIGHT / 2 - 50);
        g.setFont(scoreFont);
        g.drawString("Press Enter to Start", WIDTH / 2 - 100, HEIGHT / 2 + 50);
    }

    private void drawPausedScreen() {
        g.setFont(gameOverFont);
        g.setColor(Color.BLACK);
        g.drawString("PAUSED", WIDTH / 2 - 100, HEIGHT / 2);
    }

    private void drawGameOverScreen() {
        g.setFont(gameOverFont);
        g.setColor(Color.RED);
        g.drawString("GAME OVER", WIDTH / 2 - 150, HEIGHT / 2 - 50);
        g.setColor(Color.WHITE);
        g.setFont(scoreFont);
        g.drawString("Score: " + score, WIDTH / 2 - 60, HEIGHT / 2 + 20);
        g.setFont(scoreFont);
        g.drawString("Press Enter to Restart", WIDTH / 2 - 120, HEIGHT / 2 + 50);
    }

    private void drawGameClearScreen() {
        g.setFont(gameClearFont);
        g.setColor(Color.GREEN);
        g.drawString("GAME CLEAR!", WIDTH / 2 - 180, HEIGHT / 2 - 50);
        g.setFont(scoreFont);
        g.drawString("Press Enter to Restart", WIDTH / 2 - 120, HEIGHT / 2 + 50);
    }

    public static void main(String[] args) {
        JFrame window = new JFrame("Monkey Tree Climbing");
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setContentPane(new GameMain());
        window.pack();
        window.setLocationRelativeTo(null);
        window.setVisible(true);
    }
}