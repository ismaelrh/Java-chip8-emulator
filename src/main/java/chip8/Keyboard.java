package chip8;

import java.awt.*;
import java.awt.event.KeyEvent;

/**
 * @author Ismael Rodríguez, ismaro3
 *
 * Class that manages the keyboard input.
 *
 * ORIGINAL:
 * 1 2 3 C
 * 4 5 6 D
 * 7 8 9 E
 * A 0 B F
 *
 * MAPPED:
 * 1 2 3 4
 * Q W E R
 * A S D F
 * Z X C V
 */
public class Keyboard {

    public boolean[] pressed; //Array that stores the 16 posible pressed keys
    public int numberOfPressedKeys = 0;
    byte lastPressed;

    public Keyboard(){
        pressed = new boolean[16];
        prepareInput();
    }

    /**
     * Blocks all execution until a key if pressed.
     * Then, it returns the pressed key byte.
     */
    public byte waitForKey(){
        while(numberOfPressedKeys==0){
            try {
                Thread.sleep(0);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return lastPressed;
    }

    /**
     * Prepares the input, registering the listeners.
     * When a key is pressed and it is inside the system range, its position in "pressed" is set to true
     * and numberOfPressedKeys is incremented. If it is released, its position is set to false and numberOfPressedKeys
     * is decremented.
     */
    private void prepareInput(){

        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(new KeyEventDispatcher() {

            @Override
            public boolean dispatchKeyEvent(KeyEvent ke) {
                synchronized (Keyboard.class) {
                    switch (ke.getID()) {
                        case KeyEvent.KEY_PRESSED:
                            //System.out.println("pressed " + ke.getKeyCode());
                            if(setKey(true,ke.getKeyCode())){
                                numberOfPressedKeys++;
                            }
                            break;

                        case KeyEvent.KEY_RELEASED:
                            //System.out.println("released " + ke.getKeyCode());
                            if(setKey(false,ke.getKeyCode())){
                                numberOfPressedKeys--;
                            }
                            break;
                    }
                    return false;
                }
            }
        });
    }


    /**
     * Returns true if a correct key has been pressed.
     * Also, sets proper position in pressed to true or false (if pressed or released)
     * and lastPressed to the last pressed key.
     */
    private boolean setKey(boolean value, int keycode){

        switch(keycode){
            case KeyEvent.VK_1:
                pressed[0x1] = value;
                lastPressed=0x1;
                break;
            case KeyEvent.VK_2:
                pressed[0x2] = value;
                lastPressed=0x2;
                break;
            case KeyEvent.VK_3:
                pressed[0x3] = value;
                lastPressed=0x3;
                break;
            case KeyEvent.VK_4:
                pressed[0xC] = value;
                lastPressed=0xC;
                break;
            case KeyEvent.VK_Q:
                pressed[0x4] = value;
                lastPressed=0x4;
                break;
            case KeyEvent.VK_W:
                pressed[0x5] = value;
                lastPressed=0x5;
                break;
            case KeyEvent.VK_E:
                pressed[0x6] = value;
                lastPressed=0x6;
                break;
            case KeyEvent.VK_R:
                pressed[0xD] = value;
                lastPressed=0xD;
                break;
            case KeyEvent.VK_A:
                pressed[0x7] = value;
                lastPressed=0x7;
                break;
            case KeyEvent.VK_S:
                pressed[0x8] = value;
                lastPressed=0x8;
                break;
            case KeyEvent.VK_D:
                pressed[0x9] = value;
                lastPressed=0x9;
                break;
            case KeyEvent.VK_F:
                pressed[0xE] = value;
                lastPressed=0xE;
                break;
            case KeyEvent.VK_Z:
                pressed[0xA] = value;
                lastPressed=0xA;
                break;
            case KeyEvent.VK_X:
                pressed[0x0] = value;
                lastPressed=0x0;
                break;
            case KeyEvent.VK_C:
                pressed[0xB] = value;
                lastPressed=0xB;
                break;
            case KeyEvent.VK_V:
                pressed[0xF] = value;
                lastPressed=0xF;
                break;
            default:
                return false;

        }
        return true;

    }




}
