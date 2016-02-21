package chip8;

import com.google.common.io.Files;

import javax.swing.*;
import java.io.File;
import java.io.IOException;

/**
 * Created by ismaro3 on 18/02/16.
 */
public class Chip8 {



    private int cpuFreq;
    private Memory memory;
    private RegisterBank registerBank;
    private ControlUnit controlUnit;
    private Screen screen;
    private Keyboard keyboard;


    private long periodNanos; //Time for each cycle
    private int cyclesForRefreshing; //Cycles to refresh screen (60 times a second)

    public Chip8(int cpuFreq)
    {
        this.cpuFreq = cpuFreq;
        this.periodNanos = 1000000000/cpuFreq;
        this.cyclesForRefreshing = cpuFreq/60;
       initialize();
    }

    public void initialize(){
        memory = new Memory();
        registerBank = new RegisterBank();
        keyboard = new Keyboard();
        controlUnit = new ControlUnit(registerBank,memory,keyboard);
        prepareGUI(memory);
        System.out.println("[INFO] Chip-8 system initialized.");
    }

    private  void prepareGUI(Memory memory){
        JFrame f = new JFrame("CHIP-8 emulator (ismaro3)");
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        screen =  new Screen(memory);
        f.add(screen);
        f.pack();
        f.setVisible(true);

    }


    public void loadGame(String name) throws IOException {
        File file = new File("roms/" + name);
        byte[] bytes = Files.toByteArray(file);
        short currentAddress = (short)0x200;
        int loadedBytes = 0;
        for(byte b: bytes){
            memory.set(currentAddress,b);
            loadedBytes++;
            currentAddress = (short)(currentAddress +0x1);

        }
        System.out.println("[INFO] ROM \"" + name + "\" loaded in memory starting at 0x200 ("+loadedBytes+" Bytes).");

    }

    public void startEmulationLoop(){

        int emulatedCycles = 0;
        int refreshCycles = 0;
        long passedTime = 0;
        long initTime;
        long endTime;
        while(true){


            initTime = System.nanoTime();

            //1.- Fetch (Load instruction from memory according to PC)
            controlUnit.fetch();

            //2.- Increment PC before executing, so if a JMP is done, it will be overriden.
            controlUnit.incrementPC();

            //3.- Decode instruction and execute it
            controlUnit.decodeAndExecute();


            //4.- Update screen only every 1/60 seconds (Screen freq = 60Hz)
            if(memory.drawFlag && refreshCycles%cyclesForRefreshing==0){
                screen.paintScreen();
                refreshCycles=0;
                memory.drawFlag=false;

            }

            //5.- Decrement DT
            if(registerBank.DT > 0){
                registerBank.DT = (byte)(registerBank.DT - 0x01);
            }

            endTime = System.nanoTime();

            refreshCycles++;
            waitForCompleteCycle(endTime,initTime); //Wait time to simulate real speed

            /** Print ms rate */
            endTime = System.nanoTime();
            emulatedCycles++;
            passedTime += (endTime - initTime);
            if(emulatedCycles==cpuFreq){
                System.out.println("Time to emulate " + cpuFreq + " Hz: " + passedTime/1000000.0 + " ms");
                emulatedCycles=0;
                passedTime=0;
            }


        }
    }

    public void waitForCompleteCycle(long endTime, long initTime){

        long nanosToWait= periodNanos - (endTime - initTime);
        long initNanos = System.nanoTime();
        long targetNanos = initNanos + nanosToWait;
        while(System.nanoTime()<targetNanos){
            try {
                Thread.sleep(0);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }


    }
}
