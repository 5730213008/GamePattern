/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gamepattern;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

/**
 *
 * @author 5730213008
 */
public class Welcome implements ActionListener {

    private String NAME;
    GridLayout gl = new GridLayout();
    JFrame f = new JFrame();
    JTextField name = new JTextField();
    JLabel lbname = new JLabel("NAME: ");
    JButton easy = new JButton("Easy");

    Welcome() {
        
        name.addActionListener(this);
        gl.setColumns(2);
        gl.setRows(6);
        // lbname.setText("NAME: ");
        f.setVisible(true);
        f.setTitle("Welome!! To Tetris");
        f.setLayout(gl);
        f.setSize(400, BoardPanel.PANEL_HEIGHT);
        f.setResizable(false);
        f.setBackground(Color.black);
        f.add(lbname);
        f.add(name);
        f.add(easy);

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand() == easy.getActionCommand()) {
            try {
                if (name.getText() != null) {
                        NAME = name.getText();
                        System.out.print("Name: "+NAME);
                }
                else{NAME = "Player 1 ";}
            } catch (Exception ee) {
            }

        }
    }
}
