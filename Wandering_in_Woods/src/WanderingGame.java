import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;

/*
 * Wandering in woods is developed using java swing
 * this game has 3 levels which gets user inputs from dialogue box and displays game using swing
 * when player meets it plays music and celebration graphics
*/
public class WanderingGame extends JFrame {
    // variable declaration for the game
    private final int numOfRows;
    private final int numOfCols;
    private final int cellSize = 50;
    private final int playerSize = 20;
    private int[] playerX, playerY;
    private int[] playerXInitialPos, playerYInitialPos;
    private Color[] playerColors = { Color.BLUE, Color.RED, Color.GREEN, Color.YELLOW, Color.ORANGE, Color.BLACK,
            Color.CYAN };
    private String[] playerColorString = { "BLUE", "RED", "GREEN", "YELLOW", "ORANGE", "BLACK", "CYAN" };
    private int currentPlayer = 1;
    private JLabel infoLabel;
    private int level = 0;
    private int[] meetingHappenedPlayer;
    private int longestedRunWithoutMeeting = Integer.MIN_VALUE, shortestRunWithoutMeeting = Integer.MAX_VALUE,
            numberOfSteps = 0, totalSteps = 0, numberOfMeeting = 0;
    private Clip clip;

    // constructor to initialize the game
    public WanderingGame(int numOfRows, int numOfCols, int[] playerX, int[] playerY, int level) {
        this.numOfRows = numOfRows;
        this.numOfCols = numOfCols;
        this.level = level;

        this.playerXInitialPos = new int[playerX.length];
        this.playerYInitialPos = new int[playerY.length];
        for (int i = 0; i < playerX.length; i++) {
            this.playerXInitialPos[i] = playerX[i];
            this.playerYInitialPos[i] = playerY[i];
        }

        meetingHappenedPlayer = new int[playerX.length];
        longestedRunWithoutMeeting = Integer.MIN_VALUE;
        shortestRunWithoutMeeting = Integer.MAX_VALUE;
        numberOfSteps = 0;
        totalSteps = 0;
        numberOfMeeting = 0;
        initializeCelebration();

        setTitle("Wandering In Woods");
        setSize(numOfCols * cellSize, numOfRows * cellSize);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(true);

        this.playerX = playerX;
        this.playerY = playerY;

        JPanel gridPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawGrid(g);
                drawPlayers(g);
            }
        };

        add(gridPanel);

        setupKeyBindings();

        infoLabel = new JLabel("Welcome to Grid Game!");
        infoLabel.setHorizontalAlignment(SwingConstants.CENTER);
        infoLabel.setPreferredSize(new Dimension(getWidth(), 400));
        infoLabel.setAutoscrolls(true);
        infoLabel.setFont(new Font("Arial", Font.BOLD, 16));
        add(infoLabel, BorderLayout.SOUTH);

        startGame();
    }

    // method to draw grid with lines vertically and horizontally
    private void drawGrid(Graphics g) {
        g.setColor(Color.BLACK);

        for (int x = 0; x < numOfCols; x++) {
            g.drawLine(x * cellSize, 0, x * cellSize, getHeight());
        }

        for (int y = 0; y < numOfRows; y++) {
            g.drawLine(0, y * cellSize, getWidth(), y * cellSize);
        }
    }

    // method to play music and display celebration graphics
    private void celebrationMusicAndGraphics() {
        try {
            AudioInputStream sound = initializeCelebration();
            clip.open(sound);
            clip.loop(Clip.LOOP_CONTINUOUSLY);

        } catch (Exception e) {
            System.out.println(e);
        }
    }

    private AudioInputStream initializeCelebration() {
        try {
            File file = new File("celebration.wav");
            AudioInputStream sound = AudioSystem.getAudioInputStream(file);
            clip = AudioSystem.getClip();
            return sound;
        } catch (Exception e) {

        }
        return null;
    }

    // method to draw players with colors and shape as circle
    private void drawPlayers(Graphics g) {
        for (int i = 0; i < playerX.length; i++) {
            g.setColor(playerColors[i]);
            g.fillOval(playerX[i] * cellSize, playerY[i] * cellSize, playerSize, playerSize);
        }

    }

    // method to register keys for player movements
    private void setupKeyBindings() {

        InputMap inputMap = ((JComponent) getContentPane()).getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = ((JComponent) getContentPane()).getActionMap();

        inputMap.put(KeyStroke.getKeyStroke("UP"), "up");
        inputMap.put(KeyStroke.getKeyStroke("DOWN"), "down");
        inputMap.put(KeyStroke.getKeyStroke("LEFT"), "left");
        inputMap.put(KeyStroke.getKeyStroke("RIGHT"), "right");

        actionMap.put("up", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clip.close();
                movePlayer(playerX[currentPlayer - 1], playerY[currentPlayer - 1] - 1, playerX[currentPlayer - 1],
                        playerY[currentPlayer - 1]);
            }
        });

        actionMap.put("down", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clip.close();
                movePlayer(playerX[currentPlayer - 1], playerY[currentPlayer - 1] + 1, playerX[currentPlayer - 1],
                        playerY[currentPlayer - 1]);
            }
        });

        actionMap.put("left", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clip.close();
                movePlayer(playerX[currentPlayer - 1] - 1, playerY[currentPlayer - 1], playerX[currentPlayer - 1],
                        playerY[currentPlayer - 1]);
            }
        });

        actionMap.put("right", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clip.close();
                movePlayer(playerX[currentPlayer - 1] + 1, playerY[currentPlayer - 1], playerX[currentPlayer - 1],
                        playerY[currentPlayer - 1]);
            }
        });

    }

    // method to move player to new position if its valid and calculate the
    // statistics
    private void movePlayer(int newX, int newY, int oldX, int oldY) {
        if (isValidPosition(newX, newY)) {
            totalSteps++;
            numberOfSteps++;
            if (longestedRunWithoutMeeting < numberOfSteps) {
                longestedRunWithoutMeeting = numberOfSteps;
            }

            if (isAnyPlayerMet(newX, newY)) {
                celebrationMusicAndGraphics();
                if (shortestRunWithoutMeeting > numberOfSteps) {
                    shortestRunWithoutMeeting = numberOfSteps;
                }
                numberOfSteps = 0;
                numberOfMeeting++;
                meetingHappenedPlayer[currentPlayer - 1] = meetingHappenedPlayer[currentPlayer - 1] + 1;
                final ImageIcon icon = new ImageIcon("celebration.gif");
                if (level == 1) {

                    JOptionPane.showMessageDialog(this, "Game Over! Players met.", "Game Over",
                            JOptionPane.INFORMATION_MESSAGE, icon);
                    resetGame();
                } else {
                    JOptionPane.showMessageDialog(this, "Hurray! Players met.", "Players met",
                            JOptionPane.INFORMATION_MESSAGE, icon);
                }

            }

            playerX[currentPlayer - 1] = newX;
            playerY[currentPlayer - 1] = newY;
            if (currentPlayer == playerX.length) {
                currentPlayer = 1;
            } else {
                currentPlayer++;
            }

            if (level == 1) {
                infoLabel.setText(playerPositionAndColors());
            } else if (level == 2) {
                System.out.println("<html>" + playerPositionAndColors() + "<br/>" + playerMovements() + "</html>");
                infoLabel.setText("<html>" + playerPositionAndColors() + "<br/>" + playerMovements() + "</html>");
            } else {
                infoLabel.setText("<html>" + playerPositionAndColors() + "<br/>" + playerMovements() + "<br/>"
                        + getNearestPlayer() + "<br/>"
                        + "</html>");
            }

            repaint();

        }
    }

    // method to get nearest player from current player
    private String getNearestPlayer() {
        int minDistance = Integer.MAX_VALUE;
        int nearestPlayer = -1;

        for (int otherPlayer = 0; otherPlayer < playerX.length; otherPlayer++) {
            if (otherPlayer != currentPlayer - 1) {
                int distanceBetweenPlayers = Math.abs(playerX[currentPlayer - 1] - playerX[otherPlayer]) +
                        Math.abs(playerY[currentPlayer - 1] - playerY[otherPlayer]);

                if (distanceBetweenPlayers < minDistance) {
                    minDistance = distanceBetweenPlayers;
                    nearestPlayer = otherPlayer;
                }
            }
        }
        return "Current player " + currentPlayer + " has nearest player " + (nearestPlayer + 1) + " with distance "
                + minDistance + "<br/>";
    }

    // method to get player movements statistics for level 2 and 3 longested run,
    // shortest run and average run
    private String playerMovements() {
        String message = "";
        for (int i = 0; i < meetingHappenedPlayer.length; i++) {
            if (meetingHappenedPlayer[i] > 0) {
                message += "Player " + (i + 1) + " has met " + meetingHappenedPlayer[i] + " player <br/>";
            }
        }
        message += "Longest run without meeting: " + longestedRunWithoutMeeting + "<br/>";
        message += "Shortest run without meeting: " + (shortestRunWithoutMeeting== Integer.MAX_VALUE? "-":shortestRunWithoutMeeting) + "<br/>";
        message += "Average run without meeting: "
                + (numberOfMeeting == 0 ? totalSteps : (totalSteps / numberOfMeeting)) + "<br/>";
        return message;
    }

    // method to check if any player met
    private boolean isAnyPlayerMet(int newX, int newY) {
        for (int i = 0; i < playerX.length; i++) {
            if (i != (currentPlayer-1) && playerX[i] == newX && playerY[i] == newY) {
                return true;
            }
        }
        return false;
    }

    // method to check if the position is valid
    private boolean isValidPosition(int x, int y) {
        return x >= 0 && x < numOfCols && y >= 0 && y < numOfRows;
    }

    // method to start the game and display welcome message
    private void startGame() {
        String message = playerPositionAndColors();
        JOptionPane.showMessageDialog(this,
                "<html>Welcome to the Grid Game! <br/> " + message + "<br/> uses up, down, right, left keys. </html>");
        repaint();
    }

    // method to display player position and colors
    private String playerPositionAndColors() {
        String message = "";
        for (int i = 0; i < playerX.length; i++) {
            message += "Player " + (i + 1) + " is at position (" + playerX[i] + ", " + playerY[i] + ") with color "
                    + playerColorString[i] + "<br/>";
        }
        message = message + "<br/> Current player is " + currentPlayer;
        return message;
    }

    // method to reset the game
    private void resetGame() {
        this.currentPlayer = 1;
        this.playerX = this.playerXInitialPos;
        this.playerY = this.playerYInitialPos;
        meetingHappenedPlayer = new int[playerX.length];
        longestedRunWithoutMeeting = Integer.MIN_VALUE;
        shortestRunWithoutMeeting = Integer.MAX_VALUE;
        numberOfSteps = 0;
        totalSteps = 0;
        numberOfMeeting = 0;
        repaint();
    }

    // main method to start the game
    public static void main(String[] args) {

        int level = 0;
        level = readLevelFromUser(level);
        int numOfRows = 0;
        int numOfCols = 0;
        int[] playerX = null, playerY = null;

        if (level == 1) {
            while (numOfRows <= 0 || numOfCols <= 0 || numOfRows > 10 || numOfCols > 10) {
                try {
                    numOfRows = Integer.parseInt(JOptionPane.showInputDialog("Enter the size of grid:"));
                    numOfCols = numOfRows;
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(null, "Invalid size of grid. Please enter a valid size of grid");
                    continue;
                }
            }

            playerX = new int[2];
            playerY = new int[2];
            playerX[0] = 0;
            playerY[0] = 0;
            playerX[1] = numOfCols - 1;
            playerY[1] = numOfRows - 1;

        } else {
            while (numOfRows <= 0 || numOfCols <= 0 || numOfRows > 10 || numOfCols > 10) {
                try {
                    numOfRows = Integer.parseInt(JOptionPane.showInputDialog("Enter number of rows:"));
                    numOfCols = Integer.parseInt(JOptionPane.showInputDialog("Enter number of columns:"));
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(null,
                            "Invalid number of rows or columns. Please enter a valid number of rows and columns");
                    continue;
                }
            }

            int numOfPlayers = validateNumOfPlayers(level == 2 ? 5 : 8);
            playerX = new int[numOfPlayers];
            playerY = new int[numOfPlayers];
            for (int i = 0; i < numOfPlayers; i++) {
                while (playerX[i] <= 0 || playerX[i] >= numOfRows || playerY[i] <= 0 || playerY[i] >= numOfCols) {

                    playerX[i] = Integer
                            .parseInt(JOptionPane.showInputDialog("Enter player " + (i + 1) + " row position:"));
                    if (playerX[i] <= 0 || playerX[i] >= numOfRows) {
                        JOptionPane.showMessageDialog(null, "Invalid row position. Please enter a valid row position");
                        continue;
                    }
                    playerY[i] = Integer
                            .parseInt(JOptionPane.showInputDialog("Enter player " + (i + 1) + " column position:"));
                    if (playerY[i] <= 0 || playerY[i] >= numOfCols) {
                        JOptionPane.showMessageDialog(null,
                                "Invalid column position. Please enter a valid column position");
                        continue;
                    }
                }
            }

        }

        int row = numOfRows;
        int col = numOfCols;
        int[] playerPosX = playerX, playerPosY = playerY;
        int gameLevel = level;
        SwingUtilities.invokeLater(() -> {
            WanderingGame game = new WanderingGame(row, col, playerPosX, playerPosY, gameLevel);
            game.setVisible(true);
        });
    }

    // method to validate number of players
    private static int validateNumOfPlayers(int maxPlayers) {
        int numOfPlayers = 0;
        while (numOfPlayers == 0) {
            try {
                numOfPlayers = Integer.parseInt(JOptionPane.showInputDialog("Enter number of players:"));
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null,
                        "Invalid number of players. Please enter a valid number of players");
                continue;
            }
            if (maxPlayers >= numOfPlayers && numOfPlayers >= 2) {
                return numOfPlayers;
            } else {
                JOptionPane.showMessageDialog(null,
                        "Invalid number of players. Please enter a valid number of players");
                numOfPlayers = 0;
            }
        }

        return 0;
    }

    // method to read level from user
    private static int readLevelFromUser(int level) {
        while (level != 1 || level != 2 || level != 3) {

            try {
                level = Integer.parseInt(JOptionPane.showInputDialog("Enter level of the game:"));
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Invalid level. Please select level 1, 2 or 3");
                continue;
            }
            if (level == 1) {
                JOptionPane.showMessageDialog(null,
                        "You have selected level 1 with grid can allow be square and 2 players positioned at top left and bottom right corners");
                return level;
            } else if (level == 2) {
                JOptionPane.showMessageDialog(null,
                        "You have selected level 2 with custom player position (maximum 5) and custom grid row and columns");
                return level;
            } else if (level == 3) {
                JOptionPane.showMessageDialog(null,
                        "You have selected level 3 with custom player position (maximum 8 players) and custom grid row and columns");
                return level;
            } else {
                JOptionPane.showMessageDialog(null, "Invalid level. Please select level 1, 2 or 3");
            }
        }
        return level;
    }
}
