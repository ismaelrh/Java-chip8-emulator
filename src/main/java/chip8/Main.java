package chip8;


/**
 * @author Ismael Rodríguez, ismaro3
 *
 * Main class, launches the emulator at the specified frequency and rom.
 * Only chip-8 system is implemented (Not chip-48 or anything else).
 */
public class Main {


    public static void main(String[] args) {

        int frequencyInHz = 500;
        String rom = "INVADERS";

        try{
            Chip8 chip8 = new Chip8(frequencyInHz); //500Hz
            chip8.loadGame(rom);                    //Rom to load
            chip8.startEmulationLoop();             //Start! :)
        }
        catch(Exception ex){
            ex.printStackTrace();
        }

    }






}
