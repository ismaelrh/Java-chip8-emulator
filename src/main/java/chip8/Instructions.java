package chip8;

import org.junit.Test;

import java.util.Random;

import static org.junit.Assert.assertEquals;


/**
 * Created by ismaro3 on 17/02/16.
 */
public class Instructions {

    public static boolean randomEnabled = true;




    private static Random random = new Random();

    /**
     * 00E0 - CLS
     *
     * Clear the display.
     */
    public static void cls(){

        for(int x = 0; x < 64; x++){
            for(int y = 0; y < 32; y++){
                ScreenMemory.pixels[x][y] = false;
            }
        }

    }
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


    /**
     *  Fx07 - LD Vx, DT
     *   Set Vx = delay timer value.
     *
     *   The value of DT is placed into Vx.
     */
    public static void loadDTOnRegister(byte x){


        RegisterBank.V[x] = RegisterBank.DT;
    }


    /**
     *   Fx15 - LD DT, Vx
     *   Set delay timer = Vx.
     *
     *   DT is set equal to the value of Vx.
     */
    public static void loadRegisterOnDT(byte x){
        RegisterBank.DT = RegisterBank.V[x];
    }



    /**
     * Fx18 - LD ST, Vx
     * Set sound timer = Vx.
     *
     * ST is set equal to the value of Vx.
     */
    public static void loadRegisterOnST(byte x){
        RegisterBank.ST = RegisterBank.V[x];
    }


    /**
     * Fx1E - ADD I, Vx
     * Set I = I + Vx.
     *
     * The values of I and Vx are added, and the results are stored in I.
     */
    //TODO: warning, do we have to clear the most significats 4 bits of I? They are not used... Now I don't do it.
    public static void addToI(byte x){

        int int_vx = RegisterBank.V[x] & 0xFF; //Unsigned
        int int_i = RegisterBank.I & 0xFFFF; //Unsigned

        RegisterBank.I = (short)(int_vx + int_i);

    }

    /**
     *  Fx29 - LD F, Vx
     *  Set I = location of sprite for digit Vx.
     *
     *  The value of I is set to the location for the hexadecimal sprite corresponding to the value of Vx
     *
     */
    public static void loadHexadecimalSpriteOnI(byte x){


        RegisterBank.I = (short) (ScreenMemory.hexadecimalSpritesStartAddress + 5*RegisterBank.V[x]);


    }

    /**
     *  Fx33 - LD B, Vx
     *   Store BCD representation of Vx in memory locations I, I+1, and I+2.
     *
     *   The interpreter takes the decimal value of Vx, and places the hundreds digit in memory at location in I,
     *   the tens digit at location I+1, and the ones digit at location I+2.
     */
    public static void loadBCDtoMemory(byte x, short startMemoryAddr){

        int int_vx = RegisterBank.V[x] & 0xff; //Get unsigned int from register Vx

        int hundreds = int_vx / 100; //Calculate hundreds
        int_vx = int_vx - hundreds*100;

        int tens = int_vx/10; //Calculate tens
        int_vx = int_vx - tens*10;

        int units = int_vx; //Calculate units

        Memory.set(startMemoryAddr,(byte)hundreds);
        Memory.set((short)(startMemoryAddr+1),(byte)tens);
        Memory.set((short)(startMemoryAddr+2),(byte)units);


    }

    /**
     *  Fx55 - LD [I], Vx
     *  Store registers V0 through Vx in memory starting at location I.
     *
     *  The interpreter copies the values of registers V0 through Vx into memory, starting at the address in I.
     *
     */
    public static void loadRegisterSequenceToMemory(byte x, short startAddress){

        for(byte reg = 0; reg <= x; reg++){
            Memory.set((short)(startAddress+reg),RegisterBank.V[reg]);
        }

    }


    /**
     *   Fx65 - LD Vx, [I]
     *   Read registers V0 through Vx from memory starting at location I.
     *
     *   The interpreter reads values from memory starting at location I into registers V0 through Vx.
     */
    public static void loadMemorySequenceToRegister(byte x, short startAddress){

        for(byte reg = 0; reg <= x; reg++){
            RegisterBank.V[reg] = Memory.get((short)(startAddress+reg));
        }
    }



    /**
     * Dxyn - DRW Vx, Vy, nibble [only used 4 less significant bytes]
     * Display n-byte sprite starting at memory location I at (Vx, Vy), set VF = collision.
     *
     * The interpreter reads n bytes from memory, starting at the address stored in I. These bytes are then displayed as
     * sprites on screen at coordinates (Vx, Vy). Sprites are XORed onto the existing screen.
     * If this causes any pixels to be erased, VF is set to 1, otherwise it is set to 0. If the sprite is
     * positioned so part of it is outside the coordinates of the display, it wraps around to the opposite side of the
     * screen. See instruction 8xy3 for more information on XOR, and section 2.4, Display, for more information on the
     * Chip-8 screen and sprites.
     */
    public static void draw(byte x, byte y, byte nibble){

        byte readBytes = 0;


        byte vf = (byte)0x0;
        while(readBytes < nibble){

            byte currentByte = Memory.get((short)(RegisterBank.I +readBytes)); //Read one byte
            for(int i = 0; i <=7; i++){
                    //For every pixel

                    //Calculate real coordinate
                    int real_x = (RegisterBank.V[x] + i)%64;
                    int real_y = (RegisterBank.V[y] + readBytes)%32;

                    boolean previousPixel = ScreenMemory.pixels[real_x][real_y]; //Previous value of pixel
                    boolean newPixel = previousPixel ^ isBitSet(currentByte,7-i); //XOR

                    ScreenMemory.pixels[real_x][real_y] = newPixel;

                    if(previousPixel == true && newPixel == false){
                        //A pixel has been erased
                        vf = (byte)0x01;
                    }

            }

            RegisterBank.V[0xF] = vf; //Set Vf. Will be 1 if a pixel has been erased
            readBytes++;
        }

    }


    private static Boolean isBitSet(byte b, int bit)
    {
        return (b & (1 << bit)) != 0;
    }


}
