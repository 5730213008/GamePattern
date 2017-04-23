/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gamepattern;

import java.awt.Color;
import java.awt.GridLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;

/**
 *
 * @author 5730213008
 */
public class GameDemo extends JFrame {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // Welcome w = new Welcome();

        Tetris tetris = new Tetris();
        tetris.startGame();

    }

}
