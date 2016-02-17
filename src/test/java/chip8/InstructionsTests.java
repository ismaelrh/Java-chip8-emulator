package chip8;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
/**
 * Created by ismaro3 on 17/02/16.
 */
public class InstructionsTests {


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

    public void shl(){

        //0xFF SHL 1 = 0xFE and VF = 1
        RegisterBank.V[0] = (byte)0xFF;

        Instructions.shl((byte)0x0);

        assertEquals((byte)0x7F,RegisterBank.V[0x0]); //Vx = 0xFE
        assertEquals((byte)0x01,RegisterBank.V[0xF]); //Vf = 0x01


        //0x7F SHL 1 = 0xFE and VF = 0
        RegisterBank.V[0] = (byte)0x7F;

        Instructions.shl((byte)0x0);

        assertEquals((byte)0xFE,RegisterBank.V[0x0]); //Vx = 0xFE
        assertEquals((byte)0x01,RegisterBank.V[0x0]); //Vf = 0x01


    }







}

