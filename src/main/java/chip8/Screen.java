package chip8;

import javax.swing.*;
import java.awt.*;

class Screen extends JPanel {

        private Graphics g;
        private int scale = 10;
        private int width = 64*scale;
        private int height = 32*scale;
        public Screen() {
        }

        public Dimension getPreferredSize() {
            return new Dimension(width,height);
        }


        public void paintPixel(boolean white, int x, int y){
            if(white){
                g.setColor(Color.WHITE);
            }
            else{
                g.setColor(Color.BLACK);
            }

            if(x==0){
                System.out.println("painting at 0: " + x*scale);
            }
            g.fillRect(x*scale,y*scale,scale,scale);

        }


        public  void paintFullScreen(){

            for(int y = 0; y < 32; y++){
                for(int x = 0; x < 64; x++){
                    boolean value = ScreenMemory.pixels[x][y];
                    paintPixel(value,x,y);
                }
            }
        }


        public void paintComponent(Graphics g) {
            super.paintComponent(g);

            this.g = g;

            // Draw background
            g.drawString("This is my custom Panel!",10,20);
            g.setColor(Color.BLACK);
            g.fillRect(0,0,width,height);

            ScreenMemory.loadDefaultSpritesOnMemory();
            RegisterBank.V[0] = (byte)0xC;
            RegisterBank.V[1] = (byte)12;
            RegisterBank.V[2] = (byte)12;
            Instructions.loadHexadecimalSpriteOnI((byte)0x0);
            Instructions.draw((byte)1,(byte)2,(byte)5);

            RegisterBank.V[0] = (byte)0xA;
            RegisterBank.V[1] = (byte)20;
            RegisterBank.V[2] = (byte)12;
            Instructions.loadHexadecimalSpriteOnI((byte)0x0);
            Instructions.draw((byte)1,(byte)2,(byte)5);

            RegisterBank.V[0] = (byte)0xF;
            RegisterBank.V[1] = (byte)28;
            RegisterBank.V[2] = (byte)12;
            Instructions.loadHexadecimalSpriteOnI((byte)0x0);
            Instructions.draw((byte)1,(byte)2,(byte)5);

            RegisterBank.V[0] = (byte)0xE;
            RegisterBank.V[1] = (byte)36;
            RegisterBank.V[2] = (byte)12;
            Instructions.loadHexadecimalSpriteOnI((byte)0x0);
            Instructions.draw((byte)1,(byte)2,(byte)5);


            paintFullScreen();
            RegisterBank.printRegisters();

            //ScreenMemory.printScreen();
        }
    }