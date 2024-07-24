package com.toxicstoxm;

import javax.swing.*;
import java.awt.*;

public class Window extends JFrame {

    private final JPanel jp;
    private JPanel[][] content;
    private final Color black;
    private final Color white;
    public Window(int width, int height) {
        black = new Color(0, 0, 0);
        white = new Color(255, 255, 255);
        this.setResizable(false);
        GridLayout gridLayout = new GridLayout(width, height);
        gridLayout.preferredLayoutSize(this);
        gridLayout.setHgap(0);
        gridLayout.setVgap(0);

        jp = new JPanel();
        jp.setLayout(gridLayout);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setContentPane(jp);
        this.setVisible(true);
    }

    public void pushPixels(boolean[][] matrix) {
        if (content != null && (content.length != matrix.length || content[0].length != matrix[0].length)) {
            content = null;
        }
        if (content == null) init(matrix.length, matrix[0].length);

        JPanel temp;
        for (int i = 0; i < matrix.length; i++) {
            boolean[] row = matrix[i];
            for (int j = 0; j < row.length; j++) {
                temp = content[i][j];

                Color c =  temp.getBackground();
                Color cc = row[j] ? white : black;
                if (c != cc) {
                    temp.setBackground(cc);
                }
            }
        }
        repaint();
    }

    public void init(int height, int width) {
        content = new JPanel[height][width];

        jp.removeAll();

        JPanel tmp;
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                tmp = new JPanel();
                tmp.setBackground(black);
                tmp.setSize(1, 1);
                content[i][j] = tmp;
                jp.add(tmp);
            }
        }
        this.pack();
        jp.repaint();
    }
}
