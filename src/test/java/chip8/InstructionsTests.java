package chip8;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by ismaro3 on 17/02/16.
 */
public class InstructionsTests {


    /**
     * 00E0 - CLS
     *
     * Clear the display.
     */
    @Test
    public void cls(){
        //First, fill the screen
        for(int x = 0; x < 64; x++){
            for(int y = 0; y < 32; y++){
                ScreenMemory.pixels[x][y] = true;
            }
        }

        Instructions.cls();

        boolean orOfPixels = false;
        for(int x = 0; x < 64; x++){
            for(int y = 0; y < 32; y++){
                orOfPixels = orOfPixels |  ScreenMemory.pixels[x][y];
            }
        }

        assertEquals(false,orOfPixels);



    }
    /**
     * 00EE - RET
     * Return from a subroutine.
     * The interpreter sets the program counter to the address at the top of the stack,
     * then subtracts 1 from the stack pointer
     */
    @Test
    public  void ret(){

        //Add some value to stack
        Memory.stack[0x00] = (byte) 0xBEBA;
        Memory.stack[0x01] = (byte) 0xCAFE;

        //Set stack top
        RegisterBank.SP = (byte)0x01;

        Instructions.ret();

        assertEquals((byte)0xCAFE,RegisterBank.PC); //PC = previous top of stack
        assertEquals((byte)0x00,RegisterBank.SP); //SP has been decreased

    }

    /**
     *  1nnn - JP addr
     *  Jump to location nnn.
     *  The interpreter sets the program counter to nnn.
     *  Most-significant 4 bits are set to 0
     */
    @Test
    public void jp(){

        //Check that works
        Instructions.jp((short)0x0BEB);
        assertEquals((short)0x0BEB,RegisterBank.PC);

        //Check that 4 most-significant bits are set to 0
        Instructions.jp((short)0xFBEB);
        assertEquals((short)0x0BEB,RegisterBank.PC);



    }




    /**
     *  2nnn - CALL addr
     *  Call subroutine at nnn.
     *  The interpreter increments the stack pointer, then puts the current PC on the top of the stack. The PC is then set to nnn.
     */
    @Test
    public void call(){


        RegisterBank.SP = (byte)0x00; //Set stack pointer to 0x00 before testing
        RegisterBank.PC = (short) 0x0DAD; //Set SP to 0xDAD before testing.
        short subroutine_addr = 0x0BEB;

        Instructions.call(subroutine_addr);

        assertEquals((byte)0x01,RegisterBank.SP); //SP is incremented
        assertEquals((short)0x0DAD,Memory.stack[RegisterBank.SP]); //Previous PC is on top of the stack
        assertEquals((short)0x0BEB,RegisterBank.PC); //PC is set to 0x0BEB



    }



    /**
     *  3xkk - SE Vx, byte
     *  Skip next instruction if Vx = kk.
     *  The interpreter compares register Vx to kk, and if they are equal, increments the program counter by 2
     *  (Remember that each instruction is 2 bytes long).
     */
    @Test
    public void seByte(){

        //Test that skips if V0 = byte (0xDA)

        RegisterBank.PC = 0x00; //PC = 0x00 before testing
        RegisterBank.V[0x0] = (byte) 0xDA; //Set V0 to 0xDA

        Instructions.seByte((byte)0x0,(byte)0xDA);

        assertEquals((short)0x02,RegisterBank.PC); //Check that PC has been increased by 2.

        //Test that not skips if V0 != byte

        RegisterBank.PC = 0x00; //PC = 0x00 before testing
        RegisterBank.V[0x0] = (byte) 0xDA; //Set V0 to 0xDA

        Instructions.seByte((byte)0x0,(byte)0xDD); //0xDA != 0xDD

        assertEquals((short)0x00,RegisterBank.PC); //Check that PC has not been increased.




    }

    /**
     * 4xkk - SNE Vx, byte
     * Skip next instruction if Vx != kk.
     * The interpreter compares register Vx to kk, and if they are not equal, increments the program counter by 2.
     */
    @Test
    public void sneByte(){

        //Test that not skips if V0 = byte (0xDA)

        RegisterBank.PC = 0x00; //PC = 0x00 before testing
        RegisterBank.V[0x0] = (byte) 0xDA; //Set V0 to 0xDA

        Instructions.sneByte((byte)0x0,(byte)0xDA);

        assertEquals((short)0x00,RegisterBank.PC); //Check that PC has been increased by 2.

        //Test that  skips if V0 != byte

        RegisterBank.PC = 0x00; //PC = 0x00 before testing
        RegisterBank.V[0x0] = (byte) 0xDA; //Set V0 to 0xDA

        Instructions.sneByte((byte)0x0,(byte)0xDD); //0xDA != 0xDD

        assertEquals((short)0x02,RegisterBank.PC); //Check that PC has not been increased.


    }





    /**
     * 5xy0 - SE Vx, Vy
     * Skip next instruction if Vx = Vy.
     *  The interpreter compares register Vx to register Vy, and if they are equal, increments the program counter by 2.
     */
    @Test
    public void seRegister(){

        //Test that skips if V0 = V1 (0xDA)

        RegisterBank.PC = 0x00; //PC = 0x00 before testing
        RegisterBank.V[0x0] = (byte) 0xDA; //Set V0 to 0xDA
        RegisterBank.V[0x1] = (byte) 0xDA; //Set V1 to 0xDA

        Instructions.seRegister((byte)0x0,(byte)0x01);

        assertEquals((short)0x02,RegisterBank.PC); //Check that PC has been increased by 2.

        //Test that not skips if V0 != V1

        RegisterBank.PC = 0x00; //PC = 0x00 before testing
        RegisterBank.V[0x0] = (byte) 0xDA; //Set V0 to 0xDA
        RegisterBank.V[0x1] = (byte) 0xDD; //Set V1 to 0xDD

        Instructions.seRegister((byte)0x0,(byte)0x01); //0xDA != 0xDD

        assertEquals((short)0x00,RegisterBank.PC); //Check that PC has not been increased.

    }
    /**
     * 6xkk - LD Vx, byte
     * Set Vx = kk.
     * The interpreter puts the value kk into register Vx.
     */
    @Test
    public void loadByte(){

        Instructions.ldByteOnRegister((byte)9,(byte)0x9);

        assertEquals(RegisterBank.V[9],0x9);
    }



    /**
     * 7xkk - ADD Vx, byte
     * Set Vx = Vx + kk.
     * Adds the value kk to the value of register Vx, then stores the result in Vx.
     */
    @Test
    public void addByte(){

        RegisterBank.V[1] = 0x10;
        Instructions.addByte((byte)0x1,(byte)0x5);

        assertEquals(RegisterBank.V[1],0x15);

        //Test overflow
        RegisterBank.V[2] = (byte)0xFF;
        Instructions.addByte((byte)0x2,(byte)0x1);

        assertEquals(RegisterBank.V[2],0x0);


    }


    /**
     * 8xy0 - LD Vx, Vy
     * Set Vx = Vy.
     * Stores the value of register Vy in register Vx.
     */
    @Test
    public void loadRegister(){

        RegisterBank.V[1] = 0x1;
        RegisterBank.V[2] = 0x2;
        Instructions.ldRegisterOnRegister((byte)0x1,(byte)0x2);

        assertEquals(RegisterBank.V[1],RegisterBank.V[2]);
    }




    /**
     * 8xy4 - ADD Vx, Vy
     * Set Vx = Vx + Vy, set VF = carry.
     * The values of Vx and Vy are added together. If the result is greater than 8 bits (i.e., > 255,) VF is set to 1,
     * otherwise 0. Only the lowest 8 bits of the result are kept, and stored in Vx.
     */

    @Test
    public  void addByteCarry(){

        //Check no overflow
        RegisterBank.V[0x0] = (byte)0xF0;
        RegisterBank.V[0x1] = (byte)0x01;

        Instructions.addRegisterCarry((byte)0x0,(byte)0x1);

        assertEquals((byte)0xF1,RegisterBank.V[0x0]); //Check sum
        assertEquals(0,RegisterBank.V[0xF]);  //Check carry


        //Check overflow
        RegisterBank.V[0x0] = (byte)0xFF;
        RegisterBank.V[0x1] = (byte)0xFF;

        Instructions.addRegisterCarry((byte)0x0,(byte)0x1);

        assertEquals((byte)0xFE,RegisterBank.V[0x0]); //Check sum
        assertEquals(1,RegisterBank.V[0xF]);  //Check carry



    }

    /**
     *   8xy1 - OR Vx, Vy
     *   Set Vx = Vx OR Vy.
     *    Performs a bitwise OR on the values of Vx and Vy, then stores the result in Vx. A bitwise OR compares the
     *    corresponding bits from two values, and if either bit is 1, then the same bit in the result is also 1. Otherwise, it is 0.
     */
    @Test
    public void or(){
        //Ox55 or OxAA = 0xFF
        RegisterBank.V[0] = (byte) 0x55;
        RegisterBank.V[1] = (byte)0xAA;

        Instructions.or((byte)0x0,(byte)0x1);

        assertEquals((byte)0xFF,RegisterBank.V[0x0]);

        //0x00 or 0x00 = 0x00
        RegisterBank.V[0] = (byte) 0x00;
        RegisterBank.V[1] = (byte)0x00;

        Instructions.or((byte)0x0,(byte)0x1);

        assertEquals((byte)0x00,RegisterBank.V[0x0]);


    }



    /**
     * 8xy2 - AND Vx, Vy
     *  Set Vx = Vx AND Vy.
     * Performs a bitwise AND on the values of Vx and Vy, then stores the result in Vx. A bitwise AND compares the
     * corresponding bits from two values, and if both bits are 1, then the same bit in the result is also 1. Otherwise, it is 0.
     */
    @Test
    public void and(){
        //OxFF and 0x00 = 00
        RegisterBank.V[0] = (byte) 0xFF;
        RegisterBank.V[1] = (byte)0x00;

        Instructions.and((byte)0x0,(byte)0x1);

        assertEquals((byte)0x00,RegisterBank.V[0x0]);

        //0xEF and 0x0F = 0x0F
        RegisterBank.V[0] = (byte) 0xEF;
        RegisterBank.V[1] = (byte)0x0F;

        Instructions.and((byte)0x0,(byte)0x1);

        assertEquals((byte)0x0F,RegisterBank.V[0x0]);


    }


    @Test
    public void xor(){
        //OxFF xor 0xFF = 0x00
        RegisterBank.V[0] = (byte) 0xFF;
        RegisterBank.V[1] = (byte)0xFF;

        Instructions.xor((byte)0x0,(byte)0x1);

        assertEquals((byte)0x00,RegisterBank.V[0x0]);

        //0xFF xor 0x00 = 0xFF
        RegisterBank.V[0] = (byte) 0xFF;
        RegisterBank.V[1] = (byte)0x00;

        Instructions.xor((byte)0x0,(byte)0x1);

        assertEquals((byte)0xFF,RegisterBank.V[0x0]);

        //0xFF xor 0x0F = 0xF0

        RegisterBank.V[0] = (byte) 0xFF;
        RegisterBank.V[1] = (byte)0x0F;

        Instructions.xor((byte)0x0,(byte)0x1);

        assertEquals((byte)0xF0,RegisterBank.V[0x0]);



    }

    /**
     * 8xy5 - SUB Vx, Vy
     * Set Vx = Vx - Vy, set VF = NOT borrow.
     * If Vx > Vy, then VF is set to 1, otherwise 0. Then Vy is subtracted from Vx, and the results stored in Vx.
     */
    @Test
    public  void substractRegisterCarry(){

        //Substact without borrow (0x0A sub 0x01 = 0x09 and VF = 1)
        RegisterBank.V[0x0] = 0x0A;
        RegisterBank.V[0x1] = 0x01;

        Instructions.sub((byte)0x0,(byte)0x1);

        assertEquals((byte)0x09,RegisterBank.V[0x0]); //Result must be 0x09
        assertEquals((byte)0x01,RegisterBank.V[0xF]); //Vf = 1


        //Substract with borrow (0x01 sub 0x0A = 0xF7 and VF = 0)
        RegisterBank.V[0x0] = 0x01;
        RegisterBank.V[0x1] = 0x0A;

        Instructions.sub((byte)0x0,(byte)0x1);

        assertEquals((byte)0xF7,RegisterBank.V[0x0]); //Result must be 0xF7
        assertEquals((byte)0x00,RegisterBank.V[0xF]); //Vf = 0

        //Substract with borrow (0x00 sub 0x01 = 0xFf and VF = 0)
        RegisterBank.V[0x0] = 0x00;
        RegisterBank.V[0x1] = 0x01;

        Instructions.sub((byte)0x0,(byte)0x1);

        assertEquals((byte)0xFF,RegisterBank.V[0x0]); //Result must be 0xFF
        assertEquals((byte)0x00,RegisterBank.V[0xF]); //Vf = 0



    }




    /**
     *  8xy6 - SHR Vx {, Vy}
     *  Set Vx = Vx SHR 1.
     *  Shifts VX right by one. VF is set to the value of the least significant bit of VX before the shift.
     */
    @Test
    public void bitShiftRight(){

        //0xFF SHR 1 = 0x7F, VF = 1
        RegisterBank.V[0] = (byte)0xFF;

        Instructions.shr((byte)0x0);

        assertEquals((byte)0x7F,RegisterBank.V[0x0]); //Vx = 0x7F
        assertEquals((byte)0x01,RegisterBank.V[0xF]); //Vf = 0x01


        //0xF0 SHR 1 = 0x70, VF = 0
        RegisterBank.V[0] = (byte)0xF0;

        Instructions.shr((byte)0x0);

        assertEquals((byte)0x78,RegisterBank.V[0x0]); //Vx = 0x70
        assertEquals((byte)0x00,RegisterBank.V[0xF]); //Vf = 0x00


    }



    /**
     *  8xy7 - SUBN Vx, Vy
     *  Set Vx = Vy - Vx, set VF = NOT borrow.
     *  If Vy > Vx, then VF is set to 1, otherwise 0. Then Vx is subtracted from Vy, and the results stored in Vx.
     */
    @Test
    public void subn(){

        //Substraction without borrow. 0xFF sub 0x01 = 0xFE and VF=0x01
        RegisterBank.V[0] = (byte)0x01;
        RegisterBank.V[1] = (byte)0xFF;

        Instructions.subn((byte)0x00,(byte)0x01);

        assertEquals((byte)0xFE,RegisterBank.V[0x0]); //Vx = Vx - Vy = 0xFE
        assertEquals((byte)0x01,RegisterBank.V[0xF]); //Vf = 1 (Not borrow)


        //Substract with borrow (0x01 sub 0x0A = 0xF7 and VF = 0)
        RegisterBank.V[0] = (byte)0x0A;
        RegisterBank.V[1] = (byte)0x01;

        Instructions.subn((byte)0x00,(byte)0x01);

        assertEquals((byte)0xF7,RegisterBank.V[0x0]); //Vx = Vx - Vy = 0xFE
        assertEquals((byte)0x00,RegisterBank.V[0xF]); //Vf = 1 (Not borrow)

    }


    /**
     * 8xyE - SHL Vx {, Vy}
     * Set Vx = Vx SHL 1.
     * If the most-significant bit of Vx is 1, then VF is set to 1, otherwise to 0. Then Vx is multiplied by 2.
     */

    @Test
    public void shl(){

        //0xFF SHL 1 = 0xFE and VF = 1
        RegisterBank.V[0] = (byte)0xFF;

        Instructions.shl((byte)0x0);

        assertEquals((byte)0xFE,RegisterBank.V[0x0]); //Vx = 0xFE
        assertEquals((byte)0x01,RegisterBank.V[0xF]); //Vf = 0x01


        //0x7F SHL 1 = 0xFE and VF = 0
        RegisterBank.V[0] = (byte)0x7F;

        Instructions.shl((byte)0x0);

        assertEquals((byte)0xFE,RegisterBank.V[0x0]); //Vx = 0xFE
        assertEquals((byte)0x00,RegisterBank.V[0xF]); //Vf = 0x00


    }


    /**
     *  9xy0 - SNE Vx, Vy
     *  Skip next instruction if Vx != Vy.
     *
     *  The values of Vx and Vy are compared, and if they are not equal, the program counter is increased by 2.
     */
    @Test
    public void sneRegister(){

        //Test that not skips if V0 = V1 (0xDA)

        RegisterBank.PC = 0x00; //PC = 0x00 before testing
        RegisterBank.V[0x0] = (byte) 0xDA; //Set V0 to 0xDA
        RegisterBank.V[0x1] = (byte) 0xDA; //Set V1 to 0xDA

        Instructions.sneRegister((byte)0x0,(byte)0x01);

        assertEquals((short)0x00,RegisterBank.PC); //Check that PC has been increased by 2.

        //Test that  skips if V0 != V1
        RegisterBank.PC = 0x00; //PC = 0x00 before testing
        RegisterBank.V[0x0] = (byte) 0xDA; //Set V0 to 0xDA
        RegisterBank.V[0x1] = (byte) 0xDD; //Set V1 to 0xDD

        Instructions.sneRegister((byte)0x0,(byte)0x01); //0xDA != 0xDD

        assertEquals((short)0x02,RegisterBank.PC); //Check that PC has not been increased.

    }


    /**
     * Annn - LD I, addr
     * Set I = nnn.
     * The value of register I is set to nnn.
     */
    @Test
    public void loadAddressOnI(){

        Instructions.loadAddressOnI((short)0x0BEB);

        assertEquals((short)0x0BEB,RegisterBank.I);

    }



    /**
     * Bnnn - JP V0, addr
     * Jump to location nnn + V0.
     *
     * The program counter is set to nnn plus the value of V0.
     *
     */
    @Test
     public void jpSum(){

         short nnn = 0x0D00;
         RegisterBank.V[0] = (byte) 0xDD;

         Instructions.jpSum(nnn);

         assertEquals((short)0x0DDD,RegisterBank.PC);
     }


    /**
     * Cxkk - RND Vx, byte
     * Set Vx = random byte AND kk.
     *
     * The interpreter generates a random number from 0 to 255, which is then ANDed with the value kk.
     * The results are stored in Vx. See instruction 8xy2 for more information on AND.
     */
    @Test
    public void rnd(){

        Instructions.randomEnabled = false;
        byte kk = (byte)0xDE;

        //0xBA and 0xDE = 0x9A. Use V0 to store
        Instructions.rnd((byte)0x0,kk);


        Instructions.randomEnabled = false;
        assertEquals((byte)0x9A,RegisterBank.V[0]);


    }

    /**
     *  Fx07 - LD Vx, DT
     *   Set Vx = delay timer value.
     *
     *   The value of DT is placed into Vx.
     */
    @Test
    public void loadDTOnRegister(){

        RegisterBank.DT = (byte)0xDD;

        Instructions.loadDTOnRegister((byte)0x0);

        assertEquals((byte)0xDD,RegisterBank.V[0]);
    }



    /**
     *   Fx15 - LD DT, Vx
     *   Set delay timer = Vx.
     *
     *   DT is set equal to the value of Vx.
     */
    @Test
    public void loadRegisterOnDT(){
        RegisterBank.V[0x0] = (byte)0xDD;

        Instructions.loadRegisterOnDT((byte)0x0);

        assertEquals((byte)0xDD,RegisterBank.DT);
    }


    /**
     * Fx18 - LD ST, Vx
     * Set sound timer = Vx.
     *
     * ST is set equal to the value of Vx.
     */
    @Test
    public void loadRegisterOnST(){
        RegisterBank.V[0x0] = (byte)0xDD;

        Instructions.loadRegisterOnST((byte)0x0);

        assertEquals((byte)0xDD,RegisterBank.ST);

    }


    /**
     * Fx1E - ADD I, Vx
     * Set I = I + Vx.
     *
     * The values of I and Vx are added, and the results are stored in I.
     */
    @Test
    public void addToI(){

        //Check 0x10 + 0x0F = 0x1F
        RegisterBank.I = 0x10;
        RegisterBank.V[0x0] = 0x0F;

        Instructions.addToI((byte)0x0);

        assertEquals((short)0x1F,RegisterBank.I);

        //Check 0xFF0 + 0x1 = 0xFF1
        RegisterBank.I = 0xFF0;
        RegisterBank.V[0x0] = 0x01;

        Instructions.addToI((byte)0x0);

        assertEquals((short)0xFF1,RegisterBank.I);

        //Check 0xFF0 + 0x10 = 0x000
        RegisterBank.I = 0xFF0;
        RegisterBank.V[0x0] = 0x10;

        Instructions.addToI((byte)0x0);

        assertEquals((short)0x1000,RegisterBank.I);

    }



    /**
     *  Fx29 - LD F, Vx
     *  Set I = location of sprite for digit Vx.
     *
     *  The value of I is set to the location for the hexadecimal sprite corresponding to the value of Vx
     *
     */
    @Test
    public void loadHexadecimalSpriteOnI(){

        ScreenMemory.loadDefaultSpritesOnMemory();

        RegisterBank.V[0] = (byte)0xB;

        Instructions.loadHexadecimalSpriteOnI((byte)0x0);

        assertEquals((short)(ScreenMemory.hexadecimalSpritesStartAddress +0x00B*5),RegisterBank.I);



    }


    /**
     *  Fx33 - LD B, Vx
     *   Store BCD representation of Vx in memory locations I, I+1, and I+2.
     *
     *   The interpreter takes the decimal value of Vx, and places the hundreds digit in memory at location in I,
     *   the tens digit at location I+1, and the ones digit at location I+2.
     */
    @Test
    public void loadBCDtoMemory(){

        //Try with 123
        RegisterBank.V[0x0] = (byte)123; //Store 123 in register
        short startMemoryAddr = (short)0x0200;

        Instructions.loadBCDtoMemory((byte)0x0,startMemoryAddr);

        assertEquals((byte)0x01,Memory.get(startMemoryAddr));
        assertEquals((byte)0x02,Memory.get((short)(startMemoryAddr+1)));
        assertEquals((byte)0x03,Memory.get((short)(startMemoryAddr+2)));

        //Try with 010
        RegisterBank.V[0x0] = (byte)10; //Store 123 in register
        startMemoryAddr = (short)0x0200;

        Instructions.loadBCDtoMemory((byte)0x0,startMemoryAddr);

        assertEquals((byte)0x00,Memory.get(startMemoryAddr));
        assertEquals((byte)0x01,Memory.get((short)(startMemoryAddr+1)));
        assertEquals((byte)0x00,Memory.get((short)(startMemoryAddr+2)));


    }



    /**
     *  Fx55 - LD [I], Vx
     *  Store registers V0 through Vx in memory starting at location I.
     *
     *  The interpreter copies the values of registers V0 through Vx into memory, starting at the address in I.
     *
     */
    @Test
    public void loadRegisterSequenceToMemory(){

        //Vx = x, where x = [0,F]
        for(byte pos = 0x0; pos <= 0xF; pos++){
            RegisterBank.V[pos] = pos;
        }

        short memoryAddress = 0x200;

        Instructions.loadRegisterSequenceToMemory((byte)0xF,memoryAddress);

        for(byte pos = 0x0; pos <= 0xF; pos++){
            assertEquals((byte)pos,Memory.get((short)(memoryAddress+pos)));
        }

    }



    /**
     *   Fx65 - LD Vx, [I]
     *   Read registers V0 through Vx from memory starting at location I.
     *
     *   The interpreter reads values from memory starting at location I into registers V0 through Vx.
     */
    @Test
    public void loadMemorySequenceToRegister(){


        short memoryAddress = 0x200;
        //MEM[200+x] = x, where x = [0,F]
        for(byte pos = 0x0; pos <= 0xF; pos++){
            Memory.set((short)(memoryAddress+pos),pos);
        }

        Instructions.loadMemorySequenceToRegister((byte)0xF,memoryAddress);

        for(byte pos = 0x0; pos <= 0xF; pos++){
            assertEquals(pos,RegisterBank.V[pos]);
        }



    }




    /**
     * Dxyn - DRW Vx, Vy, nibble
     * Display n-byte sprite starting at memory location I at (Vx, Vy), set VF = collision.
     *
     * The interpreter reads n bytes from memory, starting at the address stored in I. These bytes are then displayed as
     * sprites on screen at coordinates (Vx, Vy). Sprites are XORed onto the existing screen.
     * If this causes any pixels to be erased, VF is set to 1, otherwise it is set to 0. If the sprite is
     * positioned so part of it is outside the coordinates of the display, it wraps around to the opposite side of the
     * screen. See instruction 8xy3 for more information on XOR, and section 2.4, Display, for more information on the
     * Chip-8 screen and sprites.
     */
    @Test
    public void draw(){

        ScreenMemory.loadDefaultSpritesOnMemory();


        //1.- Check A on (10,10) -> draw and Vf = 0
        //Pass address of A hexadecimal character to I
        RegisterBank.V[0x0] = (byte)0xA; //Character to draw
        RegisterBank.V[0x1] = (byte)0xA; //x=10
        RegisterBank.V[0x2] = (byte)0xA; //y=10
        Instructions.loadHexadecimalSpriteOnI((byte)0x00);

        //Draw character A in (10,10)
        Instructions.draw((byte)0x1,(byte)0x2,(byte)0x5);

        assertTrue(isSameByte((byte)0xF0,10,10));
        assertTrue(isSameByte((byte)0x90,10,11));
        assertTrue(isSameByte((byte)0xF0,10,12));
        assertTrue(isSameByte((byte)0x90,10,13));
        assertTrue(isSameByte((byte)0x90,10,14));

        assertEquals((byte)0x0,RegisterBank.V[(byte)0xF]);

        //2.- Check second A on (10,10) -> erased and Vf = 1 (it is erased)
        //Pass address of A hexadecimal character to I

        //Draw character A in (10,10)
        Instructions.draw((byte)0x1,(byte)0x2,(byte)0x5);

        assertTrue(isSameByte((byte)0x00,10,10));
        assertTrue(isSameByte((byte)0x00,10,11));
        assertTrue(isSameByte((byte)0x00,10,12));
        assertTrue(isSameByte((byte)0x00,10,13));
        assertTrue(isSameByte((byte)0x00,10,14));

        assertEquals((byte)0x1,RegisterBank.V[(byte)0xF]);


        //1.- Check A on (62,0) -> draw and Vf = 0, but overflows to other side
        //Pass address of A hexadecimal character to I
        RegisterBank.V[0x1] = (byte)62; //x=10
        RegisterBank.V[0x2] = (byte)0x0; //y=0

        //Draw character A in (10,10)
        Instructions.draw((byte)0x1,(byte)0x2,(byte)0x5);

        assertTrue(isSameByte((byte)0xF0,62,0));
        assertTrue(isSameByte((byte)0x90,62,1));
        assertTrue(isSameByte((byte)0xF0,62,2));
        assertTrue(isSameByte((byte)0x90,62,3));
        assertTrue(isSameByte((byte)0x90,62,4));



    }


    /**
     * Returns true if byte read from ScreenMemory[x +1,x+2...x+7][y] are equal to b.
     * If x>=64, it returns to 0.
     */
    private boolean isSameByte(byte b,int x, int y){
        boolean theSame = true;
        for(int i = 0; i <=7; i++){

            theSame = theSame && (isBitSet(b,7-i) == ScreenMemory.pixels[(x+i)%64][y]);
        }
        return theSame;
    }

    private  Boolean isBitSet(byte b, int bit)
    {
        return (b & (1 << bit)) != 0;
    }


}

