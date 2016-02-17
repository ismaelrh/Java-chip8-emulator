package chip8;

import java.util.Random;


/**
 * Created by ismaro3 on 17/02/16.
 */
public class Instructions {

    public static boolean randomEnabled = true;



    private static Random random = new Random();
    /**
     * 00EE - RET
     * Return from a subroutine.
     * The interpreter sets the program counter to the address at the top of the stack,
     * then subtracts 1 from the stack pointer
     */
    public static void ret(){

        RegisterBank.PC = Memory.stack[RegisterBank.SP]; //PC = address at top of stack
        RegisterBank.SP = (byte)(RegisterBank.SP - 0x01);


    }

    /**
     *  1nnn - JP addr
     *  Jump to location nnn.
     *  The interpreter sets the program counter to nnn.
     *  Most-significant 4 bits are set to 0
     */
    public static void jp(short address){

      RegisterBank.PC = (short) (address & 0x0FFF);


    }

    /**
     *  2nnn - CALL addr
     *  Call subroutine at nnn.
     *  The interpreter increments the stack pointer, then puts the current PC on the top of the stack. The PC is then set to nnn.
     */
    public static void call(short addr){

        RegisterBank.SP = (byte)(RegisterBank.SP + (byte)0x01); //Increment SP
        Memory.stack[RegisterBank.SP] = RegisterBank.PC; //Put the current PC on the top of the stack.
        RegisterBank.PC = addr; //The PC is set to addr.


    }


    /**
     *  3xkk - SE Vx, byte
     *  Skip next instruction if Vx = kk.
     *  The interpreter compares register Vx to kk, and if they are equal, increments the program counter by 2
     *  (Remember that each instruction is 2 bytes long).
     */
    public static void seByte(byte x, byte kk){

        if(RegisterBank.V[x]==kk){
            RegisterBank.PC = (short)(RegisterBank.PC + (short)0x0002);
        }

    }

    /**
     * 4xkk - SNE Vx, byte
     * Skip next instruction if Vx != kk.
     * The interpreter compares register Vx to kk, and if they are not equal, increments the program counter by 2.
     */
    public static void sneByte(byte x, byte kk){

        if(RegisterBank.V[x]!=kk){
            RegisterBank.PC = (short)(RegisterBank.PC + (short)0x0002);
        }

    }

    /**
     * 5xy0 - SE Vx, Vy
     * Skip next instruction if Vx = Vy.
     *  The interpreter compares register Vx to register Vy, and if they are equal, increments the program counter by 2.
     */
    public static void seRegister(byte x, byte y){

        if(RegisterBank.V[x]==RegisterBank.V[y]){
            RegisterBank.PC = (short)(RegisterBank.PC + (short)0x0002);
        }

    }

    /**
     * 6xkk - LD Vx, byte
     * Set Vx = kk.
     * The interpreter puts the value kk into register Vx.
     */
    public static void ldByteOnRegister(byte x, byte kk){
        RegisterBank.V[x]=kk;
    }


    /**
     * 7xkk - ADD Vx, byte
     * Set Vx = Vx + kk.
     * Adds the value kk to the value of register Vx, then stores the result in Vx.
     */
    public static void addByte(byte x, byte kk){
        RegisterBank.V[x] = (byte) (RegisterBank.V[x] + kk);
    }


    /**
     * 8xy0 - LD Vx, Vy
     * Set Vx = Vy.
     * Stores the value of register Vy in register Vx.
     */
    public static void ldRegisterOnRegister(byte x, byte y){
        RegisterBank.V[x] = RegisterBank.V[y];
    }





    /**
     *   8xy1 - OR Vx, Vy
     *   Set Vx = Vx OR Vy.
     *    Performs a bitwise OR on the values of Vx and Vy, then stores the result in Vx. A bitwise OR compares the
     *    corresponding bits from two values, and if either bit is 1, then the same bit in the result is also 1. Otherwise, it is 0.
     */
    public static void or(byte x, byte y){
        RegisterBank.V[x] = (byte)(RegisterBank.V[x] | RegisterBank.V[y]);
    }


    /**
     * 8xy2 - AND Vx, Vy
     *  Set Vx = Vx AND Vy.
     * Performs a bitwise AND on the values of Vx and Vy, then stores the result in Vx. A bitwise AND compares the
     * corresponding bits from two values, and if both bits are 1, then the same bit in the result is also 1. Otherwise, it is 0.
     */
    public static void and(byte x, byte y){
        RegisterBank.V[x] = (byte)(RegisterBank.V[x] & RegisterBank.V[y]);
    }


    /**
     *  8xy3 - XOR Vx, Vy
     *  Set Vx = Vx XOR Vy.
     *  Performs a bitwise exclusive OR on the values of Vx and Vy, then stores the result in Vx. An exclusive OR
     *  compares the corresponding bits from two values, and if the bits are not both the same, then the corresponding
     *  bit in the result is set to 1. Otherwise, it is 0.
     */
    public static void xor(byte x, byte y){
        RegisterBank.V[x] = (byte)(RegisterBank.V[x] ^RegisterBank.V[y]);
    }


    /**
     * 8xy4 - ADD Vx, Vy
     *  Set Vx = Vx + Vy, set VF = carry.
     *  The values of Vx and Vy are added together. If the result is greater than 8 bits (i.e., > 255,) VF is set to 1,
     *  otherwise 0. Only the lowest 8 bits of the result are kept, and stored in Vx.
     */

    public static void addRegisterCarry(byte x, byte y){

        byte result = (byte)(RegisterBank.V[x] + RegisterBank.V[y]);

        //Java treats all bytes as signed. With this, we have an unsigned int.
        //These three ints are used to check for overflow
        int int_result = (result & 0xff);
        int int_vy = (RegisterBank.V[y]& 0xff);
        int int_vx = (RegisterBank.V[x]& 0xff);

        //Check overflow, if result is less than one of the parameters
        if(int_result <  int_vy || int_result < int_vx){
            RegisterBank.V[0xF] = (byte)0x1;
        }
        else{
            RegisterBank.V[0xF] = (byte)0x0;
        }

        RegisterBank.V[x] =result;

    }


    /**
     * 8xy5 - SUB Vx, Vy
     * Set Vx = Vx - Vy, set VF = NOT borrow.
     * If Vx > Vy, then VF is set to 1, otherwise 0. Then Vy is subtracted from Vx, and the results stored in Vx.
     */
    public static void sub(byte x, byte y){
        byte result = (byte)(RegisterBank.V[x] - RegisterBank.V[y]);

        int int_vy = (RegisterBank.V[y]& 0xff);
        int int_vx = (RegisterBank.V[x]& 0xff);

        if(int_vx > int_vy){
            RegisterBank.V[0xF] = 0x1;
        }
        else{
            RegisterBank.V[0xF] = 0x0;
        }

        RegisterBank.V[x] = result;

    }

    /**
     *  8xy6 - SHR Vx {, Vy}
     *  Set Vx = Vx SHR 1.
     *  Shifts VX right by one. VF is set to the value of the least significant bit of VX before the shift.
     *  //todo: Mastering the CHIP8 says another thing...
     *  //wikipedia: On the original interpreter, the value of VY is shifted, and the result is stored into VX.
     *  On current implementations, Y is ignored.
     */
    public static void shr(byte x){

        byte leastSignificant = (byte)(RegisterBank.V[x] & (byte)0x01);
        RegisterBank.V[0xF] = leastSignificant; //Set VF to the least significant bit of Vx before the shift.

        //We have to cast it to unsigned int to work properly. If we don't do it, Bitwise operation does the cast
        //with sign, so the result is incorrect.
        int int_vx = (RegisterBank.V[x]&0xFF);
        RegisterBank.V[x] = (byte) (int_vx >>> 1); // >>> operator means right shift one bit without sign propagation.



    }


    /**
     *  8xy7 - SUBN Vx, Vy
     *  Set Vx = Vy - Vx, set VF = NOT borrow.
     *  If Vy > Vx, then VF is set to 1, otherwise 0. Then Vx is subtracted from Vy, and the results stored in Vx.
     */
    public static void subn(byte x, byte y){

        byte result = (byte)(RegisterBank.V[y] - RegisterBank.V[x]);

        int int_vy = (RegisterBank.V[y]& 0xff);
        int int_vx = (RegisterBank.V[x]& 0xff);

        if(int_vy > int_vx){
            RegisterBank.V[0xF] = 0x1;
        }
        else{
            RegisterBank.V[0xF] = 0x0;
        }

        RegisterBank.V[x] = result;


    }


    /**
     * 8xyE - SHL Vx {, Vy}
     * Set Vx = Vx SHL 1.
     * If the most-significant bit of Vx is 1, then VF is set to 1, otherwise to 0. Then Vx is multiplied by 2.
     * todo: Using modern approach as seen on documentation. Mastering chip8 follows original approach. (!)
     */
    public static void shl(byte x){

        byte mostSignificant = (byte)(RegisterBank.V[x] & 0x80);
        if(mostSignificant!=0){
            //If 0x10000000 -> set to 0x01
            mostSignificant = (byte)0x01;
        }
        RegisterBank.V[0xF] = mostSignificant; //Set VF to the least significant bit of Vx before the shift.

        RegisterBank.printRegisters();

        //We have to cast it to unsigned int to work properly. If we don't do it, Bitwise operation does the cast
        //with sign, so the result is incorrect.
        int int_vx = (RegisterBank.V[x]&0xFF);
        RegisterBank.V[x] = (byte) (int_vx << 1); // >>> operator means right shift one bit without sign propagation.



    }


    /**
     *  9xy0 - SNE Vx, Vy
     *  Skip next instruction if Vx != Vy.
     *
     *  The values of Vx and Vy are compared, and if they are not equal, the program counter is increased by 2.
     */
    public static void sneRegister(byte x, byte y){

        if(RegisterBank.V[x]!=RegisterBank.V[y]){
            RegisterBank.PC = (short)(RegisterBank.PC + (short)0x0002);
        }

    }

    /**
     * Annn - LD I, addr
     * Set I = nnn.
     * The value of register I is set to nnn.
     */
    public static void loadAddressOnI(short address){

        RegisterBank.I = address;

    }


    /**
     * Bnnn - JP V0, addr
     * Jump to location nnn + V0.
     *
     * The program counter is set to nnn plus the value of V0.
     *
     */
    public static void jpSum(short nnn){

        int int_v0 = RegisterBank.V[0] & 0xff; //Unsigned
        int int_nnn = nnn & 0xfff; //unsigned


       RegisterBank.PC = (short) (int_v0 + int_nnn);
    }


    /**
     * Cxkk - RND Vx, byte
     * Set Vx = random byte AND kk.
     *
     * The interpreter generates a random number from 0 to 255, which is then ANDed with the value kk.
     * The results are stored in Vx. See instruction 8xy2 for more information on AND.
     */
    public static void rnd(byte x, byte kk){

        byte randomByte = randomByte();
        RegisterBank.V[x] = (byte)(randomByte & kk);
    }




    /*
    If randomEnabled, returns a random Byte.
    Else, returns 0xBA.
     */
    private static byte randomByte(){
        if(randomEnabled){
            return (byte) random.nextInt(266);
        }
        else{
            return (byte)0xBA;
        }

    }




}
