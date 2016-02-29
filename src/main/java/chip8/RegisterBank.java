package chip8;

/**
 * @author Ismael Rodríguez, ismaro3
 * Implements register Bank.
 */
public class RegisterBank {

    //16 General Purpose registers (8 bits each). From V0 to VF.
    public  byte[] V;

    //I register, a 16-bit register used for storing memory addresses.
    public  short I;

    //PC, program counter. 16 bit (2 byte)
    public  short PC;

    //SP, stack pointer. 8 bit (1 byte)
    public  byte SP;

    //DT, delay timer. 8 bit (1 byte)
    public  byte DT;

    //ST, sound timer. 8 bit (1 byte)
    public  byte ST ;


    public RegisterBank(){
        V = new byte[16];
        I = 0x0000;
        PC = 0x0200;
        SP = 0x00;
        DT = 0x00;
        ST = 0x00;
    }


    /**
     * Prints status of registers via stdout.
     */
    public  void printRegisters(){

        //Print General Purpose registers
        for(int i = 0; i < 16; i++){
            System.out.println(String.format("V%01X:    %01X",i,V[i]));
        }

        System.out.println(String.format("I:       %01X",I));

        System.out.println(String.format("SP:      %01X",SP));

        System.out.println(String.format("DT:      %01X",DT));

        System.out.println(String.format("ST:      %01X",ST));


    }


}
