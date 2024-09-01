import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.*;
import javax.imageio.ImageIO;
import javax.sound.sampled.*;
import java.util.Collections;
import java.util.Comparator;
import javax.swing.border.AbstractBorder;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.util.Random;


public class JJCatchGame extends JFrame {

    private Clip backgroundMusic;
    private Clip starCaughtSound;
    private Clip gameOverSound;
    private String playerName; 
    private GamePanel gamePanel;
    private LeaderboardPanel leaderboardPanel;
    private IntroPanel introPanel;

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public String getPlayerName() {
        return playerName;
    }
   
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new JJCatchGame().setVisible(true));
    }

    public JJCatchGame() {
        initUI();
        loadBackgroundMusic();
        loadStarCaughtSound();
        loadGameOverSound();
    }

    private void initUI() {
    setTitle("JJ CATCH!!!");
    setSize(800, 600);
    setResizable(false);
    setDefaultCloseOperation(EXIT_ON_CLOSE);
    setLocationRelativeTo(null);
    setLayout(new CardLayout());
    getContentPane().setBackground(Color.BLACK);
    //setUndecorated(true);
    //setExtendedState(JFrame.MAXIMIZED_BOTH);

    try {
        setIconImage(ImageIO.read(new File("jj.png")));
    } catch (IOException e) {
        e.printStackTrace();
    }

    gamePanel = new GamePanel(this);
    add(gamePanel, "GamePanel");

    MenuPanel menuPanel = new MenuPanel(this);
    add(menuPanel, "MenuPanel");

    introPanel = new IntroPanel(this, menuPanel);
    add(introPanel, "IntroPanel");

    leaderboardPanel = new LeaderboardPanel(this);
    add(leaderboardPanel, "LeaderboardPanel");

    CardLayout cl = (CardLayout) getContentPane().getLayout();
    cl.show(getContentPane(), "IntroPanel");  // Correctly show IntroPanel
    
    }
    
    public void clearPlayerName() {
        playerName = "";
    }

    private void loadBackgroundMusic() {
        try {
            File audioFile = new File("TAKE ON ME 8-BIT.wav");
            AudioInputStream audioIn = AudioSystem.getAudioInputStream(audioFile);
            backgroundMusic = AudioSystem.getClip();
            backgroundMusic.open(audioIn);
            backgroundMusic.loop(Clip.LOOP_CONTINUOUSLY);
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
    }

    public void playBackgroundMusic() {
        if (backgroundMusic != null && !backgroundMusic.isRunning()) {
            backgroundMusic.start();
        }
    }

    public void stopBackgroundMusic() {
        if (backgroundMusic != null && backgroundMusic.isRunning()) {
            backgroundMusic.stop();
        }
    }
    
    public void playSound(String soundFile) {
        try {
            File audioFile = new File(soundFile);
            AudioInputStream audioIn = AudioSystem.getAudioInputStream(audioFile);
            Clip clip = AudioSystem.getClip();
            clip.open(audioIn);
            clip.start();
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
    }
    
     public void starCaught() {
        playStarCaughtSound();
    }

    private void loadStarCaughtSound() {
        try {
            File audioFile = new File("starCaught.wav"); // Replace with your actual audio file path
            AudioInputStream audioIn = AudioSystem.getAudioInputStream(audioFile);
            starCaughtSound = AudioSystem.getClip();
            starCaughtSound.open(audioIn);
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
    }

    public void playStarCaughtSound() {
        if (starCaughtSound != null) {
            starCaughtSound.stop();
            starCaughtSound.setFramePosition(0);
            starCaughtSound.start();
        }
    }

    private void loadGameOverSound() {
        try {
            File audioFile = new File("gameOver.wav"); // Adjust path as necessary
            AudioInputStream audioIn = AudioSystem.getAudioInputStream(audioFile);
            gameOverSound = AudioSystem.getClip();
            gameOverSound.open(audioIn);
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
    }

    public void playGameOverSound() {
        if (gameOverSound != null) {
            gameOverSound.stop();
            gameOverSound.setFramePosition(0);
            gameOverSound.start();
        }
    }
    
    public void showLeaderboard(int score) {
        leaderboardPanel.updatePlayerInfo(playerName, score);
        CardLayout cl = (CardLayout) getContentPane().getLayout();
        cl.show(getContentPane(), "LeaderboardPanel");
    }

    public void restartGame() {
        clearPlayerName();
        introPanel.clearNameField();
        CardLayout cl = (CardLayout) getContentPane().getLayout();
        cl.show(getContentPane(), "IntroPanel");
    }
}

    
class IntroPanel extends JPanel {
    private final JJCatchGame parent;
    private final MenuPanel menuPanel;
    private BufferedImage welcomeImage;
    private BufferedImage backgroundImage;
    private JLayeredPane layeredPane;
    private JLabel startLabel;
    private Image gifImage;
    private JTextField nameField;
    private JButton submitButton;
    private JLabel welcomeLabel;

    public IntroPanel(JJCatchGame parent, MenuPanel menuPanel) {
        this.parent = parent;
        this.menuPanel = menuPanel;
        initUI();
        loadImages();
        setupLayeredPane();
    }

    private void initUI() {
    setLayout(new BorderLayout());
    layeredPane = new JLayeredPane();
    add(layeredPane, BorderLayout.CENTER);

    // Customize the text field
    nameField = new JTextField(20) {
        @Override
        protected void paintComponent(Graphics g) {
            if (!isOpaque()) {
                g.setColor(getBackground());
                g.fillRect(0, 0, getWidth(), getHeight());
            }
            super.paintComponent(g);
        }
    };
    nameField.setBounds(250, 320, 200, 30);
    nameField.setOpaque(false); // Make the text field transparent
    nameField.setBorder(BorderFactory.createLineBorder(Color.WHITE)); // Set white border
    nameField.setFont(nameField.getFont().deriveFont(Font.BOLD, 16f)); // Bold, size 20 font
    nameField.setForeground(Color.BLACK); // Set text color to white

    submitButton = new JButton("Start");
    submitButton.setBounds(460, 320, 100, 30);
    Color lightPurple = Color.decode("#7A37E6"); // Light purple color
    submitButton.setBackground(lightPurple); // Sets the text color to light purple
    submitButton.setForeground(Color.WHITE); // White text color
    submitButton.setFocusPainted(false); // Remove focus border
    submitButton.setFont(new Font("Arial", Font.BOLD, 16)); // Custom font and size
    submitButton.setBorder(BorderFactory.createEmptyBorder()); // Remove border color

    //////////
    submitButton.addActionListener(e -> {
        String playerName = nameField.getText().trim();
        if (!playerName.isEmpty()) {
          parent.setPlayerName(playerName);
          CardLayout cl = (CardLayout) parent.getContentPane().getLayout();
          cl.show(parent.getContentPane(), "MenuPanel");
          parent.playSound("button_click.wav");
        } else {
          // Show message box here
          JOptionPane.showMessageDialog(parent, "Please enter player name");
        }
      });
      /////////////

    layeredPane.add(nameField, JLayeredPane.PALETTE_LAYER);
    layeredPane.add(submitButton, JLayeredPane.PALETTE_LAYER);
}



    private void loadImages() {
        try { 
            welcomeImage = ImageIO.read(new File("welcome.png"));
            ImageIcon gifBackground = new ImageIcon("background2.gif"); // Replace with your GIF path
            gifImage = gifBackground.getImage();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void customizeLabel(JLabel label, String imagePath) {
        ImageIcon icon = new ImageIcon(imagePath);
        Image image = icon.getImage().getScaledInstance(200, 100, Image.SCALE_SMOOTH); 
        label.setIcon(new ImageIcon(image));
        label.setHorizontalAlignment(JLabel.CENTER);
        label.setVerticalAlignment(JLabel.CENTER);
    }

    private void setupLayeredPane() {
        layeredPane.setPreferredSize(new Dimension(800, 600));

        JPanel backgroundPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (gifImage != null) {
                    g.drawImage(gifImage, 0, 0, getWidth(), getHeight(), this);
                }
            }
        };

        backgroundPanel.setBounds(0, 0, 800, 600);
        layeredPane.add(backgroundPanel, JLayeredPane.DEFAULT_LAYER);

        welcomeLabel = new JLabel(new ImageIcon(welcomeImage));
        welcomeLabel.setBounds(100, 90, welcomeImage.getWidth(), welcomeImage.getHeight()); // Adjust these values as needed
        layeredPane.add(welcomeLabel, JLayeredPane.PALETTE_LAYER);
    }

    public void clearNameField() {
        nameField.setText("");
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(800, 600); 
    }
}

class RoundedBorder extends AbstractBorder {
    private final int radius;

    public RoundedBorder(int radius) {
        this.radius = radius;
    }

    @Override
    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
        super.paintBorder(c, g, x, y, width, height);
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setColor(c.getForeground());
        g2d.drawRoundRect(x, y, width - 1, height - 1, radius, radius);
        g2d.dispose();
    }

    @Override
    public Insets getBorderInsets(Component c) {
        return new Insets(this.radius, this.radius, this.radius, this.radius);
    }

    @Override
    public Insets getBorderInsets(Component c, Insets insets) {
        insets.left = insets.top = insets.right = insets.bottom = this.radius;
        return insets;
    }
}

class MenuPanel extends JPanel {
    private final JJCatchGame parent;
    private ImageIcon gifBackground;
    private Image gifImage;
    private JLabel player1Label;
    private JLabel player2Label;

    public MenuPanel(JJCatchGame parent) {
        this.parent = parent;
        initUI();
        loadImages();
    }

    private void initUI() {
        setLayout(new BorderLayout());

        JLayeredPane layeredPane = new JLayeredPane();
        add(layeredPane, BorderLayout.CENTER);

        JPanel selectionPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (gifImage != null) {
                    g.drawImage(gifImage, 0, 0, getWidth(), getHeight(), this);
                }
            }
        };
        selectionPanel.setBounds(0, 0, 800, 600);
        layeredPane.add(selectionPanel, JLayeredPane.DEFAULT_LAYER);

        JLabel chooseCharacterLabel = new JLabel();
        customizeLabel(chooseCharacterLabel, "chooseCharacterLabel.png");
        chooseCharacterLabel.setBounds(10, 20, 800, 200); 

        player1Label = new JLabel();
        player2Label = new JLabel();

        customizeImageLabel(player1Label, "JannarvasaV2.png", 300, 300); 
        customizeImageLabel(player2Label, "JayasavranV2.png", 300, 300); 

        player1Label.setBounds(80, 180, 300, 300); 
        player2Label.setBounds(420, 180, 300, 300); 

        player1Label.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                parent.playSound("button_click.wav");  // Play sound effect on click
                startGame("Jannarvasa.png");
            }
        });

        player2Label.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                parent.playSound("button_click.wav");  // Play sound effect on click
                startGame("Jayasavran.png");
            }
        });

        layeredPane.add(chooseCharacterLabel, JLayeredPane.PALETTE_LAYER);
        layeredPane.add(player1Label, JLayeredPane.PALETTE_LAYER);
        layeredPane.add(player2Label, JLayeredPane.PALETTE_LAYER);
    }

      private void loadImages() {
        gifBackground = new ImageIcon("background.gif"); // Load GIF background
        gifImage = gifBackground.getImage();
    }

    private void customizeImageLabel(JLabel label, String imagePath, int width, int height) {
        ImageIcon icon = new ImageIcon(imagePath);
        Image image = icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
        label.setIcon(new ImageIcon(image));
        label.setHorizontalTextPosition(JLabel.CENTER);
        label.setVerticalTextPosition(JLabel.BOTTOM);
    }

    private void customizeLabel(JLabel label, String imagePath) {
        ImageIcon icon = new ImageIcon(imagePath);
        Image image = icon.getImage().getScaledInstance(800, 200, Image.SCALE_SMOOTH); 
        label.setIcon(new ImageIcon(image));
        label.setHorizontalAlignment(JLabel.CENTER);
    }

    private void startGame(String playerImagePath) {
        GamePanel gamePanel = new GamePanel(parent);
        gamePanel.setPlayerImagePath(playerImagePath);
        parent.getContentPane().add(gamePanel, "GamePanel");
        parent.playBackgroundMusic(); 

        CardLayout cl = (CardLayout) parent.getContentPane().getLayout();
        cl.show(parent.getContentPane(), "GamePanel");

        gamePanel.requestFocusInWindow();
        gamePanel.startGame();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (gifImage != null) {
            g.drawImage(gifImage, 0, 0, getWidth(), getHeight(), this);
        }
    }
}

class GamePanel extends JPanel implements ActionListener {

    private final Timer timer;
    private final JJCatchGame parent;
    private final ArrayList<Star> stars;
    private final ArrayList<Rock> rocks;
    private Basket basket;
    private int score;
    private int lives;
    private String playerImagePath;
    private boolean gameOver;
    private String playerName;
    private static final int INITIAL_DELAY = 15;
    private BufferedImage backgroundImage;
    private BufferedImage lifeImage;
    private BufferedImage starImage;
    private BufferedImage rockImage;
    private JLabel restartLabel;
    private JLabel endLabel;
    private JLabel playerInfoLabel;
    private ImageIcon gifBackground;
    private Image gifImage;



    
    public GamePanel(JJCatchGame parent) {
        this.parent = parent;
        this.playerName = parent.getPlayerName();
        setFocusable(true);
        setBackground(Color.BLACK);
        setOpaque(true);
        setPreferredSize(new Dimension(800, 600));

        try {
            gifBackground = new ImageIcon("background.gif"); // Replace with your GIF path
            gifImage = gifBackground.getImage();
        } catch (Exception e) {
            e.printStackTrace();
        }

        timer = new Timer(INITIAL_DELAY, this);
        stars = new ArrayList<>();
        rocks = new ArrayList<>();
        addKeyListener(new TAdapter());
        loadImages();
        initUI();
    }

    private void loadImages() {
        try {
            backgroundImage = ImageIO.read(new File("background.png"));
            lifeImage = ImageIO.read(new File("life.png"));
            starImage = ImageIO.read(new File("star.png"));
            rockImage = ImageIO.read(new File("rock.png"));
            basket = new Basket("basket.png"); 
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initUI() {
        setLayout(null);

        restartLabel = new JLabel();
        endLabel = new JLabel();
        playerInfoLabel = new JLabel();

        restartLabel.setBounds(190, 300, 200, 100);
        endLabel.setBounds(400, 300, 200, 100);
        playerInfoLabel.setBounds(250, 200, 300, 50);

        restartLabel.setSize(200, 100);
        endLabel.setSize(200, 100);
        playerInfoLabel.setSize(300, 50);

        restartLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                restartGame();
                parent.playSound("button_click.wav");
            }
        });

        endLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                System.exit(0);
            }
        });

        add(restartLabel);
        add(endLabel);
        add(playerInfoLabel);

        hideGameOverComponents();
    }

    public void setPlayerImagePath(String playerImagePath) {
        this.playerImagePath = playerImagePath;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public void startGame() {
        basket = new Basket(playerImagePath);
        score = 0;
        lives = 3;
        gameOver = false;
        stars.clear();
        rocks.clear();
        timer.start();
        hideGameOverComponents();
    }

    private void endGame() {
        timer.stop();
        gameOver = true;
        if (parent != null) {
            parent.playGameOverSound();
            parent.showLeaderboard(score);
        }
    }

    private void showGameOverComponents() {
        customizeLabel(restartLabel, "restartButton.png");
        customizeLabel(endLabel, "endButton.png");

        restartLabel.setVisible(true);
        endLabel.setVisible(true);
    }

    private void hideGameOverComponents() {
        restartLabel.setVisible(false);
        endLabel.setVisible(false);
        playerInfoLabel.setVisible(false);
    }

    private void customizeLabel(JLabel label, String imagePath) {
        ImageIcon icon = new ImageIcon(imagePath);
        Image image = icon.getImage().getScaledInstance(label.getWidth(), label.getHeight(), Image.SCALE_SMOOTH);
        label.setIcon(new ImageIcon(image));
    }

    private void restartGame() {
        parent.restartGame();
        score = 0;
        lives = 3;
        gameOver = false;
        stars.clear();
        rocks.clear();
        basket = new Basket(playerImagePath);
        timer.start();
        hideGameOverComponents();
        requestFocusInWindow();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (gifImage != null) {
            g.drawImage(gifImage, 0, 0, getWidth(), getHeight(), this);
        }

        if (gameOver) {
            drawGameOver(g);
        } else {
            drawGame(g);
        }
    }

    private void drawGame(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        basket.draw(g2d);
        for (Star star : stars) {
            star.draw(g2d, starImage);
        }
        for (Rock rock : rocks) {
            rock.draw(g2d, rockImage);
        }
        drawScore(g2d);
        drawLives(g2d);
        Toolkit.getDefaultToolkit().sync();
    }

    private void drawScore(Graphics2D g2d) {
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Gotham", Font.BOLD, 18));
        g2d.drawString("Score: " + score, 10, 20);
        if (playerName != null) {
            g2d.drawString("Player: " + playerName, 10, 40);
        }
    }

    private void drawLives(Graphics2D g2d) {
        for (int i = 0; i < lives; i++) {
            g2d.drawImage(lifeImage, 10 + (i * 35), 50, null);
        }
    }

    private void drawGameOver(Graphics g) {
        String msg = "GAME OVER HUHU >_<";
        Font small = new Font("Gotham", Font.BOLD, 50);
        FontMetrics fm = getFontMetrics(small);

        g.setColor(Color.WHITE);
        g.setFont(small);
        g.drawString(msg, (getWidth() - fm.stringWidth(msg)) / 2, getHeight() / 2);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (gameOver) {
            return;
        }

        updateStars();
        updateRocks();
        checkCollisions();
        basket.move();
        repaint();
    }

    private void updateStars() {
        for (int i = 0; i < stars.size(); i++) {
            Star star = stars.get(i);
            if (star.getY() > getHeight()) {
                stars.remove(i);
                i--;
                if (--lives <= 0) {
                    endGame();
                }
            } else {
                star.move();
            }
        }

        if (stars.isEmpty() && Math.random() < 0.01) {
            stars.add(new Star());
        }
    }

    private void updateRocks() {
        for (int i = 0; i < rocks.size(); i++) {
            Rock rock = rocks.get(i);
            if (rock.getY() > getHeight()) {
                rocks.remove(i);
                i--;
            } else {
                rock.move();
            }
        }

        if (Math.random() < 0.01) {
            rocks.add(new Rock());
        }
    }

    private void checkCollisions() {
        Rectangle basketBounds = basket.getBounds();
        for (int i = 0; i < stars.size(); i++) {
            Star star = stars.get(i);
            if (basketBounds.intersects(star.getBounds())) {
                stars.remove(i);
                score += 10;
                parent.playStarCaughtSound();
                break;
            }
        }

        for (int i = 0; i < rocks.size(); i++) {
            Rock rock = rocks.get(i);
            if (basketBounds.intersects(rock.getBounds())) {
                rocks.remove(i);
                if (--lives <= 0) {
                    endGame();
                }
                break;
            }
        }
    }



    

    private class TAdapter extends KeyAdapter {
        @Override
        public void keyReleased(KeyEvent e) {
            basket.keyReleased(e);
        }

        @Override
        public void keyPressed(KeyEvent e) {
            basket.keyPressed(e);
        }
    }
}

class Basket {
    private int x;
    private int y;
    private int dx;
    private int width;
    private int height;
    private BufferedImage image;

    public Basket(String imagePath) {
        this.x = 400;
        this.y = 500; //600
        this.dx = 0;

        // Check if imagePath is valid, otherwise use a default path
        if (imagePath == null || imagePath.isEmpty()) {
            imagePath = "basket.png"; // Path to a default image
        }

        try {
            File imageFile = new File(imagePath);
            if (!imageFile.exists()) {
                throw new IOException("File not found: " + imagePath);
            }
            this.image = ImageIO.read(imageFile);
            this.width = image.getWidth();
            this.height = image.getHeight();
        } catch (IOException e) {
            e.printStackTrace();
            // Set default dimensions if image loading fails
            this.width = 150;
            this.height = 150;
        }
    }

    public void move() {
        x += dx;
        if (x < 0) {
            x = 0;
        }
        if (x > 700) {
            x = 700;
        }
    }

    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        if (key == KeyEvent.VK_LEFT) {
            dx = -13; // Faster left movement
        }
        if (key == KeyEvent.VK_RIGHT) {
            dx = 13; // Faster right movement
        }
    }

    public void keyReleased(KeyEvent e) {
        int key = e.getKeyCode();
        if (key == KeyEvent.VK_LEFT || key == KeyEvent.VK_RIGHT) {
            dx = 0;
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

    public int getWidth() {
        return width;
    }

    public void draw(Graphics2D g2d) {
        if (image != null) {
            g2d.drawImage(image, x, y, null);
        }
    }
}

class Star {
    private static final int X_AXIS_MIN = 50; // Minimum x-axis position
    private static final int X_AXIS_MAX = 750; // Maximum x-axis position (replace with your desired value)
    private int x;
    private int y;
    private int speed;

    public Star() {
     // Generate random x within the defined boundaries
    Random random = new Random();
    this.x = random.nextInt(X_AXIS_MAX - X_AXIS_MIN) + X_AXIS_MIN;
    this.y = 0;
    this.speed = 3 + (int) (Math.random() * 3); // Adjusted for varying speeds
    }

    public void move() {
        y += speed;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, 20, 20); // Adjust the size of the rectangle as needed
    }

    public void draw(Graphics2D g2d, BufferedImage starImage) {
        g2d.drawImage(starImage, x, y, null);
    }
}

class Rock {
    private int x;
    private int y;
    private int speed;

    public Rock() {
        this.x = (int) (Math.random() * 800);
        this.y = 0;
        this.speed = 4 + (int) (Math.random() * 4); // Adjusted for varying speeds
    }

    public void move() {
        y += speed;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, 20, 20); // Adjust the size of the rectangle as needed
    }

    public void draw(Graphics2D g2d, BufferedImage rockImage) {
        g2d.drawImage(rockImage, x, y, null);
    }
}


class LeaderboardPanel extends JPanel {
    private final JJCatchGame parent;
    private JLabel playerInfoLabel;
    private JButton restartButton;
    private JButton endButton;
    private ImageIcon gifBackground;
    private Image gifImage;
    private ArrayList<PlayerScore> scores;
    private JLayeredPane layeredPane;

    public LeaderboardPanel(JJCatchGame parent) {
        this.parent = parent;
        scores = new ArrayList<>();
        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout());

        // Load GIF background
        try {
            gifBackground = new ImageIcon("background4.gif"); // Replace with your GIF path
            gifImage = gifBackground.getImage();
        } catch (Exception e) {
            e.printStackTrace();
        }

        layeredPane = new JLayeredPane();
        add(layeredPane, BorderLayout.CENTER);

        // Background panel
        JPanel backgroundPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (gifImage != null) {
                    g.drawImage(gifImage, 0, 0, getWidth(), getHeight(), this);
                }
            }
        };
        backgroundPanel.setBounds(0, 0, 800, 600);
        layeredPane.add(backgroundPanel, JLayeredPane.DEFAULT_LAYER);

        // Player info label
        playerInfoLabel = new JLabel("", SwingConstants.CENTER);
        playerInfoLabel.setFont(new Font("Gotham", Font.BOLD, 20));
        playerInfoLabel.setForeground(Color.WHITE); // Customize the color
        playerInfoLabel.setBounds(100, 230, 600, 100); // Adjust the position and size as needed
        layeredPane.add(playerInfoLabel, JLayeredPane.PALETTE_LAYER);

        // Restart button
        restartButton = new JButton("New Game");
        customizeButton(restartButton);
        restartButton.setBounds(250, 440, 120, 30); // Set position and size
        restartButton.addActionListener(e -> {
            parent.restartGame();
            parent.clearPlayerName(); // Clear the player name on restart
        });
        layeredPane.add(restartButton, JLayeredPane.PALETTE_LAYER);

        // End button
        endButton = new JButton("End");
        customizeButton(endButton);
        endButton.setBounds(420, 440, 120, 30); // Set position and size
        endButton.addActionListener(e -> {
            System.exit(0);
        });
        layeredPane.add(endButton, JLayeredPane.PALETTE_LAYER);
    }

    private void customizeButton(JButton button) {
        button.setFont(new Font("Arial", Font.BOLD, 16));
        button.setForeground(Color.WHITE);
        Color lightPurple = Color.decode("#8E68E6"); // Light purple color
        button.setBackground(lightPurple); // Sets the button background color to light purple
        button.setFocusPainted(false); // Removes the focus border
        button.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
        button.setContentAreaFilled(false); // Remove the default button background
        button.setOpaque(true); // Make the background color visible
    }

    public void updatePlayerInfo(String playerName, int score) {
        scores.removeIf(ps -> ps.getName().equals(playerName)); // Prevent duplicate names
        scores.add(new PlayerScore(playerName, score));
        Collections.sort(scores, Comparator.comparingInt(PlayerScore::getScore).reversed());

        StringBuilder sb = new StringBuilder("<html>Leaderboard:<br/>");
        for (int i = 0; i < Math.min(3, scores.size()); i++) {
            PlayerScore ps = scores.get(i);
            sb.append((i + 1)).append(". <span style='color:white;'>").append(ps.getName()).append("</span>: <span style='color:white;'>").append(ps.getScore()).append("</span><br/>");
        }
        sb.append("</html>");

        playerInfoLabel.setText(sb.toString());
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        // Draw GIF background
        if (gifImage != null) {
            g.drawImage(gifImage, 0, 0, getWidth(), getHeight(), this);
        }
    }
}

class PlayerScore {
    private final String name;
    private final int score;

    public PlayerScore(String name, int score) {
        this.name = name;
        this.score = score;
    }

    public String getName() {
        return name;
    }

    public int getScore() {
        return score;
    }
} 
