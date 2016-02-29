package chip8;

/**
 * @author Ismael Rodríguez, ismaro3
 * Control unit of the CPU.
 * It manages fetch, decode and execute phase.
 */
public class ControlUnit {

    private RegisterBank registerBank;
    private Memory memory;
    private ProcessingUnit pu;

    private short currentInstruction;


    public ControlUnit(RegisterBank registerBank, Memory memory, Keyboard keyboard){
        this.registerBank = registerBank;
        this.memory = memory;
        this.pu = new ProcessingUnit(memory,registerBank,keyboard);
    }


    /**
     * Fetchs current instruction from memory and stores it in currentInstruction.
     */
    public void fetch(){
        short pc = registerBank.PC;

        byte mostSignificantByte =  memory.get(pc);
        byte lessSignificantByte = memory.get((short)(pc+0x1));

        //0x00FF is neccesary or Java puts FF instead of 00's !!
        currentInstruction =  (short)((short) (mostSignificantByte << 8) | (lessSignificantByte & 0x00FF));

    }


    /**
     * Decodes the current instruction, extracting the operation code and its operands, and then executes it.
     */
    public void decodeAndExecute(){

        //System.out.printf("Current PC: 0x%04X - Next PC: 0x%04X - INST: 0x%04X\n",registerBank.PC-2,registerBank.PC,inst);
        //OOEO

        byte x = extractX(currentInstruction);
        byte y = extractY(currentInstruction);
        byte n = extractN(currentInstruction);
        short nnn = extractNNN(currentInstruction);
        
        if(matches(currentInstruction,0,0,0xE,0)){
            pu.cls();
            return;
        }
        //00EE
        else if(matches(currentInstruction,0,0,0xE,0xE)){
            pu.ret();
            return;
        }
        //1nnn
        else if(matches(currentInstruction,1,null,null,null)){
            pu.jp(nnn);
            return;
        }
        //2nnn
        else if(matches(currentInstruction,2,null,null,null)){
            pu.call(nnn);
            return;
        }
        //3xkk
        else if(matches(currentInstruction,3,null,null,null)){
            pu.seByte(x,extractKK(currentInstruction));
            return;
        }
        //4xkk
        else if(matches(currentInstruction,4,null,null,null)){
            pu.sneByte(x,extractKK(currentInstruction));
            return;
        }
        //5xy0
        else if(matches(currentInstruction,5,null,null,0)){
            pu.seRegister(x,y);
            return;
        }
        //6xkk
        else if(matches(currentInstruction,6,null,null,null)){
            pu.ldByteOnRegister(x,extractKK(currentInstruction));
            return;
        }
        //7xkk
        else if(matches(currentInstruction,7,null,null,null)){
            pu.addByte(x,extractKK(currentInstruction));
            return;
        }
        //8xy...
        else if(matches(currentInstruction,8,null,null,null)){
            byte last = extractNibble(currentInstruction,0);

            switch(last){

                case 0x0: //8xy0
                    pu.ldRegisterOnRegister(x,y);
                    return;
                case 0x1: //8xy1
                    pu.or(x,y);
                    return;
                case 0x2: //8xy2
                    pu.and(x,y);
                    return;
                case 0x3: //8xy3
                    pu.xor(x,y);
                    return;
                case 0x4: //8xy4
                    pu.addRegisterCarry(x,y);
                    return;
                case 0x5: //8xy5
                    pu.sub(x,y);
                    return;
                case 0x6: //8xy6
                    pu.shr(x);
                    return;
                case 0x7: //8xy7
                    pu.subn(x,y);
                    return;
                case 0xE: //8xyE
                    pu.shl(x);
                    return;
                default:  //Invalid instruction

            }

        }
        //9xy0
        else if(matches(currentInstruction,9,null,null,0)){
            pu.sneRegister(x,y);
            return;
        }
        //Annn
        else if(matches(currentInstruction,0xA,null,null,null)){

            pu.loadAddressOnI(nnn);
            return;
        }
        //Bnnn
        else if(matches(currentInstruction,0xB,null,null,null)){
            pu.jpSum(nnn);
            return;
        }
        //Cxkk
        else if(matches(currentInstruction,0xC,null,null,null)){

            pu.rnd(x,extractKK(currentInstruction));
            return;
        }
        //Dxyn
        else if(matches(currentInstruction,0xD,null,null,null)){
            pu.draw(x,y,n);
            return;
        }
        //Ex9E
        else if(matches(currentInstruction,0xE,null,0x9,0xE)){
            pu.skipIfPressed(x);
            return;
        }
        //ExA1
        else if(matches(currentInstruction,0xE,null,0xA,0x1)){
            pu.skipIfNotPressed(x);
            return;
        }
        //Fx...
        else if(matches(currentInstruction,0xF,null,null,null)){

            //Fx07
            if(matches(currentInstruction,0xF,null,0x0,0x7)){

                pu.loadDTOnRegister(x);
                return;
            }
            //Fx0A
            else if(matches(currentInstruction,0xF,null,0x0,0xA)){
                pu.waitKey(x);
                return;
            }
            //Fx15
            else if(matches(currentInstruction,0xF,null,0x1,0x5)){
                pu.loadRegisterOnDT(x);
                return;
            }
            //Fx18
            else if(matches(currentInstruction,0xF,null,0x1,0x8)){

                pu.loadRegisterOnST(x);
                return;
            }
            //Fx1E
            else if(matches(currentInstruction,0xF,null,0x1,0xE)){

                pu.addToI(x);
                return;
            }
            //Fx29
            else if(matches(currentInstruction,0xF,null,0x2,0x9)){
                pu.loadHexadecimalSpriteOnI(x);
                return;
            }
            //Fx33
            else if(matches(currentInstruction,0xF,null,0x3,0x3)){
                pu.loadBCDtoMemory(x);
                return;
            }
            //Fx55
            else if(matches(currentInstruction,0xF,null,0x5,0x5)){
                pu.loadRegisterSequenceToMemory(x);
                return;
            }
            //Fx65
            else if(matches(currentInstruction,0xF,null,0x6,0x5)){
                pu.loadMemorySequenceToRegister(x);

                return;
            }

        }
            //Incorrect operation
            System.out.printf("[ERROR] Unknown instruction: %04X\n",currentInstruction);



    }


    /**
     * Increments PC by 2 (Each instruction is 2-byte log)
     */
    public void incrementPC(){
        registerBank.PC = (short) (registerBank.PC + 0x2);
    }


    /**
     * Returns the nibble in position <position> starting from lower.
     */
    private  byte extractNibble(short instruction, int position){

        instruction = (short)(instruction >> position*4);

        //Return only last 4 bits
        return (byte)(instruction & 0x000F);
    }


    /**
     * Returns true if instruction inst is composed by bytes (three,two,one,zero).
     * False otherwise.
     */
    private  boolean matches(short inst,Integer three, Integer two, Integer one, Integer zero){

        boolean matches = true;
        if(three!=null){
            matches &= (three == extractNibble(inst,3));
        }
        if(two!=null){
            matches &= (two == extractNibble(inst,2));
        }
        if(one!=null){
            matches &= (one == extractNibble(inst,1));
        }
        if(zero!=null){
            matches &= (zero == extractNibble(inst,0));
        }
        return matches;


    }

    //nnn are the 12 lowest bits (oNNN)
    private short extractNNN(short instruction){
        return (short)(instruction & 0xFFF);
    }

    //kk are the 8 lowest bits (ookk)
    private byte extractKK(short instruction){
        return (byte)(instruction & 0xFF);
    }

    //x are the oXoo
    private byte extractX(short instruction){
        return (byte) ( (instruction & 0x0F00) >>> 8);
    }

    //y are the ooYo
    private byte extractY(short instruction){
        return (byte) ( (instruction & 0x00F0) >>> 4);
    }

    //n are the ooNo
    private byte extractN(short instruction){
        return (byte) (instruction & 0x00F);
    }



}
