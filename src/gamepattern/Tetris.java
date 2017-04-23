/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gamepattern;

import java.awt.BorderLayout;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import static java.time.Clock.system;
import java.util.Random;
import javax.swing.JFrame;
import static javax.swing.JFrame.EXIT_ON_CLOSE;

/**
 *
 * @author 5730213008
 */
public class Tetris extends JFrame implements gametemplate {

    private static final long serialVersionUID = -4722429764792514382L;

    private static final long FRAME_TIME = 1000L / 50L;

    private static final int TYPE_COUNT = TileType.values().length;

    private BoardPanel board;

    private SidePanel side;

    private boolean isPaused;

    private boolean isNewGame;

    private boolean isGameOver;

    private int level;

    private int score;

    private Random random;

    private game.Clock logicTimer;

    private TileType currentType;

    private TileType nextType;

    private int currentCol;

    private int currentRow;

    private int currentRotation;

    private int dropCooldown;

    private float gameSpeed;
    private int line = 0;

    public Tetris() {
        /*
		 * Set the basic properties of the window.
         */
        super("Tetris");
        setLayout(new BorderLayout());
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);

        /*
		 * Initialize the BoardPanel and SidePanel instances.
         */
        this.board = new BoardPanel(this);
        this.side = new SidePanel(this);

        /*
		 * Add the BoardPanel and SidePanel instances to the window.
         */
        add(board, BorderLayout.CENTER);
        add(side, BorderLayout.EAST);

        /*
		 * Adds a custom anonymous KeyListener to the frame.
         */
        addKeyListener(new KeyAdapter() {

            @Override
            public void keyPressed(KeyEvent e) {

                switch (e.getKeyCode()) {

                    /*
				 * Drop - When pressed, we check to see that the game is not
				 * paused and that there is no drop cooldown, then set the
				 * logic timer to run at a speed of 25 cycles per second.
                     */
                    case KeyEvent.VK_S:
                        if (!isPaused && dropCooldown == 0) {
                            logicTimer.setCyclesPerSecond(25.0f);
                        }
                        break;

                    /*
				 * Move Left - When pressed, we check to see that the game is
				 * not paused and that the position to the left of the current
				 * position is valid. If so, we decrement the current column by 1.
                     */
                    case KeyEvent.VK_A:
                        if (!isPaused && board.isValidAndEmpty(currentType, currentCol - 1, currentRow, currentRotation)) {
                            currentCol--;
                        }
                        break;

                    /*
				 * Move Right - When pressed, we check to see that the game is
				 * not paused and that the position to the right of the current
				 * position is valid. If so, we increment the current column by 1.
                     */
                    case KeyEvent.VK_D:
                        if (!isPaused && board.isValidAndEmpty(currentType, currentCol + 1, currentRow, currentRotation)) {
                            currentCol++;
                        }
                        break;

                    /*
				 * Rotate Anticlockwise - When pressed, check to see that the game is not paused
				 * and then attempt to rotate the piece anticlockwise. Because of the size and
				 * complexity of the rotation code, as well as it's similarity to clockwise
				 * rotation, the code for rotating the piece is handled in another method.
                     */
                    case KeyEvent.VK_Q:
                        if (!isPaused) {
                            rotatePiece((currentRotation == 0) ? 3 : currentRotation - 1);
                        }
                        break;

                    /*
			     * Rotate Clockwise - When pressed, check to see that the game is not paused
				 * and then attempt to rotate the piece clockwise. Because of the size and
				 * complexity of the rotation code, as well as it's similarity to anticlockwise
				 * rotation, the code for rotating the piece is handled in another method.
                     */
                    case KeyEvent.VK_E:
                        if (!isPaused) {
                            rotatePiece((currentRotation == 3) ? 0 : currentRotation + 1);
                        }
                        break;

                    /*
				 * Pause Game - When pressed, check to see that we're currently playing a game.
				 * If so, toggle the pause variable and update the logic timer to reflect this
				 * change, otherwise the game will execute a huge number of updates and essentially
				 * cause an instant game over when we unpause if we stay paused for more than a
				 * minute or so.
                     */
                    case KeyEvent.VK_P:
                        if (!isGameOver && !isNewGame) {
                            isPaused = !isPaused;
                            logicTimer.setPaused(isPaused);
                        }
                        break;

                    /*
				 * Start Game - When pressed, check to see that we're in either a game over or new
				 * game state. If so, reset the game.
                     */
                    case KeyEvent.VK_ENTER:
                        if (isGameOver || isNewGame) {
                            resetGame();
                        }
                        break;

                }
            }

            @Override
            public void keyReleased(KeyEvent e) {

                switch (e.getKeyCode()) {

                    /*
				 * Drop - When released, we set the speed of the logic timer
				 * back to whatever the current game speed is and clear out
				 * any cycles that might still be elapsed.
                     */
                    case KeyEvent.VK_S:
                        logicTimer.setCyclesPerSecond(gameSpeed);
                        logicTimer.reset();
                        break;
                }

            }

        });

        /*
		 * Here we resize the frame to hold the BoardPanel and SidePanel instances,
		 * center the window on the screen, and show it to the user.
         */
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    @Override
    public void startGame() {
        /*
		 * Initialize our random number generator, logic timer, and new game variables.
         */
        this.random = new Random();
        this.isNewGame = true;
        this.gameSpeed = 1.0f;

        /*
		 * Setup the timer to keep the game from running before the user presses enter
		 * to start it.
         */
        this.logicTimer = new game.Clock(gameSpeed);
        logicTimer.setPaused(true);

        while (true) {
            //Get the time that the frame started.
            long start = System.nanoTime();

            //Update the logic timer.
            logicTimer.update();

            /*
			 * If a cycle has elapsed on the timer, we can update the game and
			 * move our current piece down.
             */
            if (logicTimer.hasElapsedCycle()) {
                updateGame();
            }

            //Decrement the drop cool down if necessary.
            if (dropCooldown > 0) {
                dropCooldown--;
            }

            //Display the window to the user.
            renderGame();

            /*
			 * Sleep to cap the framerate.
             */
            long delta = (System.nanoTime() - start) / 1000000L;
            if (delta < FRAME_TIME) {
                try {
                    Thread.sleep(FRAME_TIME - delta);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void updateGame() {
        /*
		 * Check to see if the piece's position can move down to the next row.
         */
        if (board.isValidAndEmpty(currentType, currentCol, currentRow + 1, currentRotation)) {
            //Increment the current row if it's safe to do so.
            currentRow++;
        } else {
            /*
			 * We've either reached the bottom of the board, or landed on another piece, so
			 * we need to add the piece to the board.
             */
            board.addPiece(currentType, currentCol, currentRow, currentRotation);

            /*
			 * Check to see if adding the new piece resulted in any cleared lines. If so,
			 * increase the player's score. (Up to 4 lines can be cleared in a single go;
			 * [1 = 100pts, 2 = 200pts, 3 = 400pts, 4 = 800pts]).
             */
            int cleared = board.checkLines();

            if (cleared > 0 && cleared == 1) {
                line += 1;
                score += 100 << cleared;
                //System.out.println("line:" + line);
            }
            else if(cleared > 0 && cleared == 2){ 
                line += 2;
                score += 100 << cleared;
            }
            else if(cleared > 0 && cleared == 3){
                line += 2;
                score += 100 << cleared;
            }
            else if(cleared > 0 && cleared == 4){
                line += 2;
                score += 100 << cleared;
            }

            /*
			 * Increase the speed slightly for the next piece and update the game's timer
			 * to reflect the increase.
             */
            gameSpeed += 0.035f;
            logicTimer.setCyclesPerSecond(gameSpeed);
            logicTimer.reset();

            /*
			 * Set the drop cooldown so the next piece doesn't automatically come flying
			 * in from the heavens immediately after this piece hits if we've not reacted
			 * yet. (~0.5 second buffer).
             */
            dropCooldown = 25;

            /*
			 * Update the difficulty level. This has no effect on the game, and is only
			 * used in the "Level" string in the SidePanel.
             */
            level = (int) (gameSpeed * 1.70f);
           
            /*
			 * Spawn a new piece to control.
             */
            spawnPiece();
        }
    }

    @Override
    public void resetGame() {
        this.level = 1;
        this.score = 0;
        this.line=0;
        this.gameSpeed = 1.0f;
        this.nextType = TileType.values()[random.nextInt(TYPE_COUNT)];
        this.isNewGame = false;
        this.isGameOver = false;
        board.clear();
        logicTimer.reset();
        logicTimer.setCyclesPerSecond(gameSpeed);
        spawnPiece();
    }

    /**
     * Spawns a new piece and resets our piece's variables to their default
     * values.
     */
    public void spawnPiece() {
        /*
		 * Poll the last piece and reset our position and rotation to
		 * their default variables, then pick the next piece to use.
         */
        this.currentType = nextType;
        this.currentCol = currentType.getSpawnColumn();
        this.currentRow = currentType.getSpawnRow();
        this.currentRotation = 0;
        this.nextType = TileType.values()[random.nextInt(TYPE_COUNT)];

        /*
		 * If the spawn point is invalid, we need to pause the game and flag that we've lost
		 * because it means that the pieces on the board have gotten too high.
         */
        if (!board.isValidAndEmpty(currentType, currentCol, currentRow, currentRotation)) {
            this.isGameOver = true;
            logicTimer.setPaused(true);
        }
    }

    @Override
    public void rotatePiece(int newRotation) {
        /*
		 * Sometimes pieces will need to be moved when rotated to avoid clipping
		 * out of the board (the I piece is a good example of this). Here we store
		 * a temporary row and column in case we need to move the tile as well.
         */
        int newColumn = currentCol;
        int newRow = currentRow;

        /*
		 * Get the insets for each of the sides. These are used to determine how
		 * many empty rows or columns there are on a given side.
         */
        int left = currentType.getLeftInset(newRotation);
        int right = currentType.getRightInset(newRotation);
        int top = currentType.getTopInset(newRotation);
        int bottom = currentType.getBottomInset(newRotation);

        /*
		 * If the current piece is too far to the left or right, move the piece away from the edges
		 * so that the piece doesn't clip out of the map and automatically become invalid.
         */
        if (currentCol < -left) {
            newColumn -= currentCol - left;
        } else if (currentCol + currentType.getDimension() - right >= BoardPanel.COL_COUNT) {
            newColumn -= (currentCol + currentType.getDimension() - right) - BoardPanel.COL_COUNT + 1;
        }

        /*
		 * If the current piece is too far to the top or bottom, move the piece away from the edges
		 * so that the piece doesn't clip out of the map and automatically become invalid.
         */
        if (currentRow < -top) {
            newRow -= currentRow - top;
        } else if (currentRow + currentType.getDimension() - bottom >= BoardPanel.ROW_COUNT) {
            newRow -= (currentRow + currentType.getDimension() - bottom) - BoardPanel.ROW_COUNT + 1;
        }

        /*
		 * Check to see if the new position is acceptable. If it is, update the rotation and
		 * position of the piece.
         */
        if (board.isValidAndEmpty(currentType, newColumn, newRow, newRotation)) {
            currentRotation = newRotation;
            currentRow = newRow;
            currentCol = newColumn;
        }
    }

    @Override
    public boolean isNewGame() {
        return isNewGame;
    }

    @Override
    public boolean isPaused() {
        return isPaused;
    }

    @Override
    public boolean isGameOver() {
        return isGameOver;
    }

    @Override
    public TileType getPieceType() {
        return currentType;
    }

    @Override
    public TileType getNextPieceType() {
        return nextType;
    }

    @Override
    public int getPieceCol() {
        return currentCol;
    }

    @Override
    public int getPieceRow() {
        return currentRow;
    }

    @Override
    public int getPieceRotation() {
        return currentRotation;
    }

    @Override
    public int getLevel() {
        return level;
    }

    @Override
    public int getScore() {
        return score;
    }

    private void renderGame() {
        board.repaint();
        side.repaint();
    }

}