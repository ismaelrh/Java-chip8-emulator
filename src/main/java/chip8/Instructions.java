package chip8;

import org.jcp.xml.dsig.internal.MacOutputStream;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by ismaro3 on 17/02/16.
 */
public class Instructions {


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

        RegisterBank.printRegisters();

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

        byte mostSignificant = (byte)(RegisterBank.V[x] & (byte)0x08);
        RegisterBank.V[0xF] = mostSignificant; //Set VF to the least significant bit of Vx before the shift.

        RegisterBank.printRegisters();

        //We have to cast it to unsigned int to work properly. If we don't do it, Bitwise operation does the cast
        //with sign, so the result is incorrect.
        int int_vx = (RegisterBank.V[x]&0xFF);
        RegisterBank.V[x] = (byte) (int_vx << 1); // >>> operator means right shift one bit without sign propagation.



    }





}
