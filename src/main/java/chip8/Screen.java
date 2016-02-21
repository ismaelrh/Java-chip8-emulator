package chip8;

import javax.swing.*;
import java.awt.*;

class Screen extends JPanel {

        private Graphics g;
        private int scale = 10;
        private int width = 64*scale;
        private int height = 32*scale;

        private Memory memory;



        public Screen(Memory memory) {
            this.memory = memory;

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

            g.fillRect(x*scale,y*scale,scale,scale);

        }


        public  void paintFullScreen(){

            for(int y = 0; y < 32; y++){
                for(int x = 0; x < 64; x++){
                    boolean value = memory.pixels[x][y];
                    paintPixel(value,x,y);
                }
            }
        }


    public void paintScreen(){
        repaint();
    }


        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            this.g = g;

            // Draw background
            g.setColor(Color.BLACK);
            g.fillRect(0,0,width,height);

            paintFullScreen();


            //memory.printScreen();
        }
    }