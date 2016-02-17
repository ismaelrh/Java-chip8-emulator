package chip8;

/**
 * Created by ismaro3 on 16/02/16.
 */
public class RegisterBank {

    //16 General Purpose registers (8 bits each). From V0 to VF.
    public static byte[] V = new byte[16];

    //I register, a 16-bit register used for storing memory addresses.
    public static byte I = 0x00;

    //PC, program counter. 16 bit (2 byte)
    public static short PC = 0x0000;

    //SP, stack pointer. 8 bit (1 byte)
    public static byte SP = 0x00; //todo: Where does the stack start

    //todo: specific purpose registers and PC,SP



    public static void printRegisters(){

        //Print General Purpose registers
        for(int i = 0; i < 16; i++){
            System.out.println(String.format("V%01X:    %01X",i,V[i]));
        }


    }

    public static void main(String[] args){
        RegisterBank.printRegisters();
    }
}
