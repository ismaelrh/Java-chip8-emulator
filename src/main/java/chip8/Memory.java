package chip8;
/**
 * 4096 Bytes of memory.
 * Each address is 16bit.
 */
public class Memory {


    private static byte[] memory = new byte[4096]; //4KB of memory


    /**
     * Returns the content of a memory address
     * @param address 2 byte
     * @return 1 byte
     */
    public static byte get(short address){
        if(address>0xFFF){
            System.err.println(String.format("Memory GET access out of range: 0x%4s",address));
            return 0x0;
        }
        else{
            return memory[address];
        }
    }

    /**
     * Sets the content of one byte of memory
     * @param address 2 byte
     * @param content 1 byte
     */
    public static void set(short address, byte content){
        if(address>0xFFF){
            System.err.println(String.format("Memory SET access out of range: 0x%4s", address));
        }
        else{
            memory[address] = content;
        }
    }


    public static void printMemory(short startAddress,short endAddress){
        short currentAddress = startAddress;
        while(currentAddress<=endAddress){
           System.out.println(String.format("0x%03X:  %02X",currentAddress,memory[currentAddress]));
            currentAddress+=0x1;
        }

    }


    public static void main(String[] args){
        Memory.printMemory((short) 0x000,(short) 0x0FF);
    }











}
