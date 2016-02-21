package chip8;

import javax.swing.*;
import java.awt.*;

public class Main {

    private Frame mainFrame;
    private Label headerLabel;
    private Label statusLabel;
    private Panel controlPanel;


    public static void main(String[] args) {
        //System.out.println("Main not implemented yet!");
        //prepareGUI();
        try{
            Chip8 chip8 = new Chip8(500);
            chip8.loadGame("WIPEOFF");
            chip8.startEmulationLoop();
        }
        catch(Exception ex){
            ex.printStackTrace();
        }

    }






}
