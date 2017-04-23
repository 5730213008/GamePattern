/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gamepattern;

/**
 *
 * @author 5730213008
 */
public interface gametemplate {

    public void startGame();

    public void updateGame();

    public void resetGame();

    public void spawnPiece();

    public void rotatePiece(int newRotation);

    public boolean isNewGame();

    public boolean isPaused();

    public boolean isGameOver();

    public TileType getPieceType();

    public TileType getNextPieceType();

    public int getPieceCol();

    public int getPieceRow();

    public int getPieceRotation();

    public int getLevel();

    public int getScore();

    public int getline();
}
