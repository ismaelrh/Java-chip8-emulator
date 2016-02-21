package chip8;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by ismaro3 on 17/02/16.
 */
public class ProcessingUnitTest {


    private Memory memory;
    private RegisterBank registerBank;
    private ProcessingUnit instructions;
    private Keyboard keyboard;


    @Before
    public void initialize() {
        memory = new Memory();
        registerBank = new RegisterBank();
        keyboard = new Keyboard();
        instructions = new ProcessingUnit(memory,registerBank,keyboard);
    }
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
                memory.pixels[x][y] = true;
            }
        }

        instructions.cls();

        boolean orOfPixels = false;
        for(int x = 0; x < 64; x++){
            for(int y = 0; y < 32; y++){
                orOfPixels = orOfPixels |  memory.pixels[x][y];
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
        memory.stack[0x00] = (byte) 0xBEBA;
        memory.stack[0x01] = (byte) 0xCAFE;

        //Set stack top
        registerBank.SP = (byte)0x01;

        instructions.ret();

        assertEquals((byte)0xCAFE,registerBank.PC); //PC = previous top of stack
        assertEquals((byte)0x00,registerBank.SP); //SP has been decreased

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
        instructions.jp((short)0x0BEB);
        assertEquals((short)0x0BEB,registerBank.PC);

        //Check that 4 most-significant bits are set to 0
        instructions.jp((short)0xFBEB);
        assertEquals((short)0x0BEB,registerBank.PC);



    }


    /**
     *  2nnn - CALL addr
     *  Call subroutine at nnn.
     *  The interpreter increments the stack pointer, then puts the current PC on the top of the stack. The PC is then set to nnn.
     */
    @Test
    public void call(){


        registerBank.SP = (byte)0x00; //Set stack pointer to 0x00 before testing
        registerBank.PC = (short) 0x0DAD; //Set SP to 0xDAD before testing.
        short subroutine_addr = 0x0BEB;

        instructions.call(subroutine_addr);

        assertEquals((byte)0x01,registerBank.SP); //SP is incremented
        assertEquals((short)0x0DAD,memory.stack[registerBank.SP]); //Previous PC is on top of the stack
        assertEquals((short)0x0BEB,registerBank.PC); //PC is set to 0x0BEB



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

        registerBank.PC = 0x00; //PC = 0x00 before testing
        registerBank.V[0x0] = (byte) 0xDA; //Set V0 to 0xDA

        instructions.seByte((byte)0x0,(byte)0xDA);

        assertEquals((short)0x02,registerBank.PC); //Check that PC has been increased by 2.

        //Test that not skips if V0 != byte

        registerBank.PC = 0x00; //PC = 0x00 before testing
        registerBank.V[0x0] = (byte) 0xDA; //Set V0 to 0xDA

        instructions.seByte((byte)0x0,(byte)0xDD); //0xDA != 0xDD

        assertEquals((short)0x00,registerBank.PC); //Check that PC has not been increased.




    }

    /**
     * 4xkk - SNE Vx, byte
     * Skip next instruction if Vx != kk.
     * The interpreter compares register Vx to kk, and if they are not equal, increments the program counter by 2.
     */
    @Test
    public void sneByte(){

        //Test that not skips if V0 = byte (0xDA)

        registerBank.PC = 0x00; //PC = 0x00 before testing
        registerBank.V[0x0] = (byte) 0xDA; //Set V0 to 0xDA

        instructions.sneByte((byte)0x0,(byte)0xDA);

        assertEquals((short)0x00,registerBank.PC); //Check that PC has been increased by 2.

        //Test that  skips if V0 != byte

        registerBank.PC = 0x00; //PC = 0x00 before testing
        registerBank.V[0x0] = (byte) 0xDA; //Set V0 to 0xDA

        instructions.sneByte((byte)0x0,(byte)0xDD); //0xDA != 0xDD

        assertEquals((short)0x02,registerBank.PC); //Check that PC has not been increased.


    }


    /**
     * 5xy0 - SE Vx, Vy
     * Skip next instruction if Vx = Vy.
     *  The interpreter compares register Vx to register Vy, and if they are equal, increments the program counter by 2.
     */
    @Test
    public void seRegister(){

        //Test that skips if V0 = V1 (0xDA)

        registerBank.PC = 0x00; //PC = 0x00 before testing
        registerBank.V[0x0] = (byte) 0xDA; //Set V0 to 0xDA
        registerBank.V[0x1] = (byte) 0xDA; //Set V1 to 0xDA

        instructions.seRegister((byte)0x0,(byte)0x01);

        assertEquals((short)0x02,registerBank.PC); //Check that PC has been increased by 2.

        //Test that not skips if V0 != V1

        registerBank.PC = 0x00; //PC = 0x00 before testing
        registerBank.V[0x0] = (byte) 0xDA; //Set V0 to 0xDA
        registerBank.V[0x1] = (byte) 0xDD; //Set V1 to 0xDD

        instructions.seRegister((byte)0x0,(byte)0x01); //0xDA != 0xDD

        assertEquals((short)0x00,registerBank.PC); //Check that PC has not been increased.

    }

    /**
     * 6xkk - LD Vx, byte
     * Set Vx = kk.
     * The interpreter puts the value kk into register Vx.
     */
    @Test
    public void loadByte(){

        instructions.ldByteOnRegister((byte)9,(byte)0x9);

        assertEquals(registerBank.V[9],0x9);
    }



    /**
     * 7xkk - ADD Vx, byte
     * Set Vx = Vx + kk.
     * Adds the value kk to the value of register Vx, then stores the result in Vx.
     */
    @Test
    public void addByte(){

        registerBank.V[1] = 0x10;
        instructions.addByte((byte)0x1,(byte)0x5);

        assertEquals(registerBank.V[1],0x15);

        //Test overflow
        registerBank.V[2] = (byte)0xFF;
        instructions.addByte((byte)0x2,(byte)0x1);

        assertEquals(registerBank.V[2],0x0);


    }


    /**
     * 8xy0 - LD Vx, Vy
     * Set Vx = Vy.
     * Stores the value of register Vy in register Vx.
     */
    @Test
    public void loadRegister(){

        registerBank.V[1] = 0x1;
        registerBank.V[2] = 0x2;
        instructions.ldRegisterOnRegister((byte)0x1,(byte)0x2);

        assertEquals(registerBank.V[1],registerBank.V[2]);
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
        registerBank.V[0x0] = (byte)0xF0;
        registerBank.V[0x1] = (byte)0x01;

        instructions.addRegisterCarry((byte)0x0,(byte)0x1);

        assertEquals((byte)0xF1,registerBank.V[0x0]); //Check sum
        assertEquals(0,registerBank.V[0xF]);  //Check carry


        //Check overflow
        registerBank.V[0x0] = (byte)0xFF;
        registerBank.V[0x1] = (byte)0xFF;

        instructions.addRegisterCarry((byte)0x0,(byte)0x1);

        assertEquals((byte)0xFE,registerBank.V[0x0]); //Check sum
        assertEquals(1,registerBank.V[0xF]);  //Check carry



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
        registerBank.V[0] = (byte) 0x55;
        registerBank.V[1] = (byte)0xAA;

        instructions.or((byte)0x0,(byte)0x1);

        assertEquals((byte)0xFF,registerBank.V[0x0]);

        //0x00 or 0x00 = 0x00
        registerBank.V[0] = (byte) 0x00;
        registerBank.V[1] = (byte)0x00;

        instructions.or((byte)0x0,(byte)0x1);

        assertEquals((byte)0x00,registerBank.V[0x0]);


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
        registerBank.V[0] = (byte) 0xFF;
        registerBank.V[1] = (byte)0x00;

        instructions.and((byte)0x0,(byte)0x1);

        assertEquals((byte)0x00,registerBank.V[0x0]);

        //0xEF and 0x0F = 0x0F
        registerBank.V[0] = (byte) 0xEF;
        registerBank.V[1] = (byte)0x0F;

        instructions.and((byte)0x0,(byte)0x1);

        assertEquals((byte)0x0F,registerBank.V[0x0]);


    }


    @Test
    public void xor(){
        //OxFF xor 0xFF = 0x00
        registerBank.V[0] = (byte) 0xFF;
        registerBank.V[1] = (byte)0xFF;

        instructions.xor((byte)0x0,(byte)0x1);

        assertEquals((byte)0x00,registerBank.V[0x0]);

        //0xFF xor 0x00 = 0xFF
        registerBank.V[0] = (byte) 0xFF;
        registerBank.V[1] = (byte)0x00;

        instructions.xor((byte)0x0,(byte)0x1);

        assertEquals((byte)0xFF,registerBank.V[0x0]);

        //0xFF xor 0x0F = 0xF0

        registerBank.V[0] = (byte) 0xFF;
        registerBank.V[1] = (byte)0x0F;

        instructions.xor((byte)0x0,(byte)0x1);

        assertEquals((byte)0xF0,registerBank.V[0x0]);



    }

    /**
     * 8xy5 - SUB Vx, Vy
     * Set Vx = Vx - Vy, set VF = NOT borrow.
     * If Vx > Vy, then VF is set to 1, otherwise 0. Then Vy is subtracted from Vx, and the results stored in Vx.
     */
    @Test
    public  void substractRegisterCarry(){

        //Substact without borrow (0x0A sub 0x01 = 0x09 and VF = 1)
        registerBank.V[0x0] = 0x0A;
        registerBank.V[0x1] = 0x01;

        instructions.sub((byte)0x0,(byte)0x1);

        assertEquals((byte)0x09,registerBank.V[0x0]); //Result must be 0x09
        assertEquals((byte)0x01,registerBank.V[0xF]); //Vf = 1


        //Substract with borrow (0x01 sub 0x0A = 0xF7 and VF = 0)
        registerBank.V[0x0] = 0x01;
        registerBank.V[0x1] = 0x0A;

        instructions.sub((byte)0x0,(byte)0x1);

        assertEquals((byte)0xF7,registerBank.V[0x0]); //Result must be 0xF7
        assertEquals((byte)0x00,registerBank.V[0xF]); //Vf = 0

        //Substract with borrow (0x00 sub 0x01 = 0xFf and VF = 0)
        registerBank.V[0x0] = 0x00;
        registerBank.V[0x1] = 0x01;

        instructions.sub((byte)0x0,(byte)0x1);

        assertEquals((byte)0xFF,registerBank.V[0x0]); //Result must be 0xFF
        assertEquals((byte)0x00,registerBank.V[0xF]); //Vf = 0



    }




    /**
     *  8xy6 - SHR Vx {, Vy}
     *  Set Vx = Vx SHR 1.
     *  Shifts VX right by one. VF is set to the value of the least significant bit of VX before the shift.
     */
    @Test
    public void bitShiftRight(){

        //0xFF SHR 1 = 0x7F, VF = 1
        registerBank.V[0] = (byte)0xFF;

        instructions.shr((byte)0x0);

        assertEquals((byte)0x7F,registerBank.V[0x0]); //Vx = 0x7F
        assertEquals((byte)0x01,registerBank.V[0xF]); //Vf = 0x01


        //0xF0 SHR 1 = 0x70, VF = 0
        registerBank.V[0] = (byte)0xF0;

        instructions.shr((byte)0x0);

        assertEquals((byte)0x78,registerBank.V[0x0]); //Vx = 0x70
        assertEquals((byte)0x00,registerBank.V[0xF]); //Vf = 0x00


    }



    /**
     *  8xy7 - SUBN Vx, Vy
     *  Set Vx = Vy - Vx, set VF = NOT borrow.
     *  If Vy > Vx, then VF is set to 1, otherwise 0. Then Vx is subtracted from Vy, and the results stored in Vx.
     */
    @Test
    public void subn(){

        //Substraction without borrow. 0xFF sub 0x01 = 0xFE and VF=0x01
        registerBank.V[0] = (byte)0x01;
        registerBank.V[1] = (byte)0xFF;

        instructions.subn((byte)0x00,(byte)0x01);

        assertEquals((byte)0xFE,registerBank.V[0x0]); //Vx = Vx - Vy = 0xFE
        assertEquals((byte)0x01,registerBank.V[0xF]); //Vf = 1 (Not borrow)


        //Substract with borrow (0x01 sub 0x0A = 0xF7 and VF = 0)
        registerBank.V[0] = (byte)0x0A;
        registerBank.V[1] = (byte)0x01;

        instructions.subn((byte)0x00,(byte)0x01);

        assertEquals((byte)0xF7,registerBank.V[0x0]); //Vx = Vx - Vy = 0xFE
        assertEquals((byte)0x00,registerBank.V[0xF]); //Vf = 1 (Not borrow)

    }


    /**
     * 8xyE - SHL Vx {, Vy}
     * Set Vx = Vx SHL 1.
     * If the most-significant bit of Vx is 1, then VF is set to 1, otherwise to 0. Then Vx is multiplied by 2.
     */

    @Test
    public void shl(){

        //0xFF SHL 1 = 0xFE and VF = 1
        registerBank.V[0] = (byte)0xFF;

        instructions.shl((byte)0x0);

        assertEquals((byte)0xFE,registerBank.V[0x0]); //Vx = 0xFE
        assertEquals((byte)0x01,registerBank.V[0xF]); //Vf = 0x01


        //0x7F SHL 1 = 0xFE and VF = 0
        registerBank.V[0] = (byte)0x7F;

        instructions.shl((byte)0x0);

        assertEquals((byte)0xFE,registerBank.V[0x0]); //Vx = 0xFE
        assertEquals((byte)0x00,registerBank.V[0xF]); //Vf = 0x00


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

        registerBank.PC = 0x00; //PC = 0x00 before testing
        registerBank.V[0x0] = (byte) 0xDA; //Set V0 to 0xDA
        registerBank.V[0x1] = (byte) 0xDA; //Set V1 to 0xDA

        instructions.sneRegister((byte)0x0,(byte)0x01);

        assertEquals((short)0x00,registerBank.PC); //Check that PC has been increased by 2.

        //Test that  skips if V0 != V1
        registerBank.PC = 0x00; //PC = 0x00 before testing
        registerBank.V[0x0] = (byte) 0xDA; //Set V0 to 0xDA
        registerBank.V[0x1] = (byte) 0xDD; //Set V1 to 0xDD

        instructions.sneRegister((byte)0x0,(byte)0x01); //0xDA != 0xDD

        assertEquals((short)0x02,registerBank.PC); //Check that PC has not been increased.

    }


    /**
     * Annn - LD I, addr
     * Set I = nnn.
     * The value of register I is set to nnn.
     */
    @Test
    public void loadAddressOnI(){

        instructions.loadAddressOnI((short)0x0BEB);

        assertEquals((short)0x0BEB,registerBank.I);

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
         registerBank.V[0] = (byte) 0xDD;

         instructions.jpSum(nnn);

         assertEquals((short)0x0DDD,registerBank.PC);
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

        instructions.randomEnabled = false;
        byte kk = (byte)0xDE;

        //0xBA and 0xDE = 0x9A. Use V0 to store
        instructions.rnd((byte)0x0,kk);


        instructions.randomEnabled = false;
        assertEquals((byte)0x9A,registerBank.V[0]);


    }

    /**
     *  Fx07 - LD Vx, DT
     *   Set Vx = delay timer value.
     *
     *   The value of DT is placed into Vx.
     */
    @Test
    public void loadDTOnRegister(){

        registerBank.DT = (byte)0xDD;

        instructions.loadDTOnRegister((byte)0x0);

        assertEquals((byte)0xDD,registerBank.V[0]);
    }



    /**
     *   Fx15 - LD DT, Vx
     *   Set delay timer = Vx.
     *
     *   DT is set equal to the value of Vx.
     */
    @Test
    public void loadRegisterOnDT(){
        registerBank.V[0x0] = (byte)0xDD;

        instructions.loadRegisterOnDT((byte)0x0);

        assertEquals((byte)0xDD,registerBank.DT);
    }


    /**
     * Fx18 - LD ST, Vx
     * Set sound timer = Vx.
     *
     * ST is set equal to the value of Vx.
     */
    @Test
    public void loadRegisterOnST(){
        registerBank.V[0x0] = (byte)0xDD;

        instructions.loadRegisterOnST((byte)0x0);

        assertEquals((byte)0xDD,registerBank.ST);

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
        registerBank.I = 0x10;
        registerBank.V[0x0] = 0x0F;

        instructions.addToI((byte)0x0);

        assertEquals((short)0x1F,registerBank.I);

        //Check 0xFF0 + 0x1 = 0xFF1
        registerBank.I = 0xFF0;
        registerBank.V[0x0] = 0x01;

        instructions.addToI((byte)0x0);

        assertEquals((short)0xFF1,registerBank.I);

        //Check 0xFF0 + 0x10 = 0x000
        registerBank.I = 0xFF0;
        registerBank.V[0x0] = 0x10;

        instructions.addToI((byte)0x0);

        assertEquals((short)0x1000,registerBank.I);

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


        registerBank.V[0] = (byte)0xB;

        instructions.loadHexadecimalSpriteOnI((byte)0x0);

        assertEquals((short)(memory.hexadecimalSpritesStartAddress +0x00B*5),registerBank.I);



    }


    /**
     *  Fx33 - LD B, Vx
     *   Store BCD representation of Vx in memory locations I, I+1, and I+2.
     *
     *   The interpreter takes the decimal value of Vx, and places the hundreds digit in memory at location in I,
     *   the tens digit at location I+1, and the ones digit at location I+2.
     */
    @Test
    public void loadBCDtomemory(){

        //Try with 123
        registerBank.V[0x0] = (byte)123; //Store 123 in register
        registerBank.I = 0x0200;


        instructions.loadBCDtoMemory((byte)0x0);

        assertEquals((byte)0x01,memory.get(registerBank.I));
        assertEquals((byte)0x02,memory.get((short)(registerBank.I+1)));
        assertEquals((byte)0x03,memory.get((short)(registerBank.I+2)));

        //Try with 010
        registerBank.V[0x0] = (byte)10; //Store 123 in register

        instructions.loadBCDtoMemory((byte)0x0);

        assertEquals((byte)0x00,memory.get(registerBank.I));
        assertEquals((byte)0x01,memory.get((short)(registerBank.I+1)));
        assertEquals((byte)0x00,memory.get((short)(registerBank.I+2)));


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
            registerBank.V[pos] = pos;
        }

        registerBank.I =0x200;

        instructions.loadRegisterSequenceToMemory((byte)0xF);

        for(byte pos = 0x0; pos <= 0xF; pos++){
            assertEquals((byte)pos,memory.get((short)( registerBank.I+pos)));
        }

    }



    /**
     *   Fx65 - LD Vx, [I]
     *   Read registers V0 through Vx from memory starting at location I.
     *
     *   The interpreter reads values from memory starting at location I into registers V0 through Vx.
     */
    @Test
    public void loadmemorySequenceToRegister(){


        registerBank.I = 0x200;
        //MEM[200+x] = x, where x = [0,F]
        for(byte pos = 0x0; pos <= 0xF; pos++){
            memory.set((short)( registerBank.I+pos),pos);
        }

        instructions.loadMemorySequenceToRegister((byte)0xF);

        for(byte pos = 0x0; pos <= 0xF; pos++){
            assertEquals(pos,registerBank.V[pos]);
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



        //1.- Check A on (10,10) -> draw and Vf = 0
        //Pass address of A hexadecimal character to I
        registerBank.V[0x0] = (byte)0xA; //Character to draw
        registerBank.V[0x1] = (byte)0xA; //x=10
        registerBank.V[0x2] = (byte)0xA; //y=10
        instructions.loadHexadecimalSpriteOnI((byte)0x00);

        //Draw character A in (10,10)
        instructions.draw((byte)0x1,(byte)0x2,(byte)0x5);

        assertTrue(isSameByte((byte)0xF0,10,10));
        assertTrue(isSameByte((byte)0x90,10,11));
        assertTrue(isSameByte((byte)0xF0,10,12));
        assertTrue(isSameByte((byte)0x90,10,13));
        assertTrue(isSameByte((byte)0x90,10,14));

        assertEquals((byte)0x0,registerBank.V[(byte)0xF]);

        //2.- Check second A on (10,10) -> erased and Vf = 1 (it is erased)
        //Pass address of A hexadecimal character to I

        //Draw character A in (10,10)
        instructions.draw((byte)0x1,(byte)0x2,(byte)0x5);

        assertTrue(isSameByte((byte)0x00,10,10));
        assertTrue(isSameByte((byte)0x00,10,11));
        assertTrue(isSameByte((byte)0x00,10,12));
        assertTrue(isSameByte((byte)0x00,10,13));
        assertTrue(isSameByte((byte)0x00,10,14));

        assertEquals((byte)0x1,registerBank.V[(byte)0xF]);


        //1.- Check A on (62,0) -> draw and Vf = 0, but overflows to other side
        //Pass address of A hexadecimal character to I
        registerBank.V[0x1] = (byte)62; //x=10
        registerBank.V[0x2] = (byte)0x0; //y=0

        //Draw character A in (10,10)
        instructions.draw((byte)0x1,(byte)0x2,(byte)0x5);

        assertTrue(isSameByte((byte)0xF0,62,0));
        assertTrue(isSameByte((byte)0x90,62,1));
        assertTrue(isSameByte((byte)0xF0,62,2));
        assertTrue(isSameByte((byte)0x90,62,3));
        assertTrue(isSameByte((byte)0x90,62,4));



    }

    /**
     * Ex9E - SKP Vx
     * Skip next instruction if key with the value of Vx is pressed.
     *
     * Checks the keyboard, and if the key corresponding to the value of Vx is currently in the
     * down position, PC is increased by 2.
     */
    @Test
    public void skipIfPressed(){



        //Check for A, and it's pressed
        registerBank.PC = 0x200;
        registerBank.V[0] = 0xA;
        keyboard.pressed[0xA] = true; //Simulate that it's pressed

        instructions.skipIfPressed((byte)0x0);

        assertEquals((short)0x202,registerBank.PC);

        //Chec, for B, and it's not pressed
        registerBank.PC = 0x200;
        registerBank.V[0] = 0xB;
        keyboard.pressed[0xB] = false; //Simulate that it's not pressed

        instructions.skipIfPressed((byte)0x0);

        assertEquals((short)0x200,registerBank.PC);



    }


    /**
     * ExA1 - SKnP Vx
     * Skip next instruction if key with the value of Vx is not pressed.
     *
     * Checks the keyboard, and if the key corresponding to the value of Vx is currently in the
     * up position, PC is increased by 2.
     */
    @Test
    public void skipIfNotPressed(){



        //Check for A, and it's pressed
        registerBank.PC = 0x200;
        registerBank.V[0] = 0xA;
        keyboard.pressed[0xA] = true; //Simulate that it's pressed

        instructions.skipIfNotPressed((byte)0x0);

        assertEquals((short)0x200,registerBank.PC);

        //Chec, for B, and it's not pressed
        registerBank.PC = 0x200;
        registerBank.V[0] = 0xB;
        keyboard.pressed[0xB] = false; //Simulate that it's not pressed

        instructions.skipIfNotPressed((byte)0x0);

        assertEquals((short)0x202,registerBank.PC);



    }

    /**
     * Fx0A - LD Vx, K
     * Wait for a key press, store the value of the key in Vx.
     *
     * All executions stops until a key is pressed, then the value of that key is
     * stored in Vx.
     */
    @Test
    public void waitKey(){

        byte x = 0x0; //Store result in Vx

        keyboard.lastPressed = 0xA; //Simulate last pressed = A
        keyboard.numberOfPressedKeys = 1; //Simulate 1 pressed key

        instructions.waitKey(x);

        assertEquals(0xA,registerBank.V[x]);

    }





    /**
     * Returns true if byte read from Screenmemory[x +1,x+2...x+7][y] are equal to b.
     * If x>=64, it returns to 0.
     */
    private boolean isSameByte(byte b,int x, int y){
        boolean theSame = true;
        for(int i = 0; i <=7; i++){

            theSame = theSame && (isBitSet(b,7-i) == memory.pixels[(x+i)%64][y]);
        }
        return theSame;
    }

    private  Boolean isBitSet(byte b, int bit)
    {
        return (b & (1 << bit)) != 0;
    }


}

