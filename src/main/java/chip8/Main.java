package chip8;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class Main {

    private Frame mainFrame;
    private Label headerLabel;
    private Label statusLabel;
    private Panel controlPanel;




    public static void main(String[] args) {
        System.out.println("Main not implemented yet!");
        prepareGUI();
    }

    private static void prepareGUI(){
        JFrame f = new JFrame("CHIP-8 emulator (ismaro3)");
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.add(new Screen());
        f.pack();
        f.setVisible(true);

    }




}
