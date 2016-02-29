package chip8;
/**
 * @author Ismael Rodríguez, ismaro3
 *
 * Class that implements main memory, stack, screen memory (pixels) and drawFlag.
 * 4096 Bytes of memory.
 * Each address is 16bit.
 */
public class Memory {


    //Preferences of screen
    private static int width = 64;
    private static int height = 32;
    public static short hexadecimalSpritesStartAddress = 0x000;

    private  byte[] memory; //4KB of memory

    public  short[] stack; //Stack, 16 16-bit values

    //Screen array. False = black. True = white.
    public  boolean[][] pixels;

    //Set to true when a sprite has been set to be drawn.
    public boolean drawFlag;

    //Default 8x5 sprites from 0 to F
    private static byte[] sprite_0 = new byte[]{(byte)0xF0,(byte)0x90,(byte)0x90,(byte)0x90,(byte)0xF0};
    private static byte[] sprite_1 = new byte[]{(byte)0x20,(byte)0x60,(byte)0x20,(byte)0x20,(byte)0x70};
    private static byte[] sprite_2 = new byte[]{(byte)0xF0,(byte)0x10,(byte)0xF0,(byte)0x80,(byte)0xF0};
    private static byte[] sprite_3 = new byte[]{(byte)0xF0,(byte)0x10,(byte)0xF0,(byte)0x10,(byte)0xF0};
    private static byte[] sprite_4 = new byte[]{(byte)0x90,(byte)0x90,(byte)0xF0,(byte)0x10,(byte)0x10};
    private static byte[] sprite_5 = new byte[]{(byte)0xF0,(byte)0x80,(byte)0xF0,(byte)0x10,(byte)0xF0};
    private static byte[] sprite_6 = new byte[]{(byte)0xF0,(byte)0x80,(byte)0xF0,(byte)0x90,(byte)0xF0};
    private static byte[] sprite_7 = new byte[]{(byte)0xF0,(byte)0x10,(byte)0x20,(byte)0x40,(byte)0x40};
    private static byte[] sprite_8 = new byte[]{(byte)0xF0,(byte)0x90,(byte)0xF0,(byte)0x90,(byte)0xF0};
    private static byte[] sprite_9 = new byte[]{(byte)0xF0,(byte)0x90,(byte)0xF0,(byte)0x10,(byte)0xF0};
    private static byte[] sprite_A = new byte[]{(byte)0xF0,(byte)0x90,(byte)0xF0,(byte)0x90,(byte)0x90};
    private static byte[] sprite_B = new byte[]{(byte)0xE0,(byte)0x90,(byte)0xE0,(byte)0x90,(byte)0xE0};
    private static byte[] sprite_C = new byte[]{(byte)0xF0,(byte)0x80,(byte)0x80,(byte)0x80,(byte)0xF0};
    private static byte[] sprite_D = new byte[]{(byte)0xE0,(byte)0x90,(byte)0x90,(byte)0x90,(byte)0xE0};
    private static byte[] sprite_E = new byte[]{(byte)0xF0,(byte)0x80,(byte)0xF0,(byte)0x80,(byte)0xF0};
    private static byte[] sprite_F = new byte[]{(byte)0xF0,(byte)0x80,(byte)0xF0,(byte)0x80,(byte)0x80};


    /**
     * Creates a new memory object and loads default sprites in it.
     */
    public Memory(){
        this.memory = new byte[4096];
        this.stack = new short[16];
        this.pixels = new boolean[width][height];
        loadDefaultSpritesOnMemory();

    }

    /**
     * Returns the content of a memory address.
     */
    public  byte get(short address){
        if(address>0xFFF){
            System.err.println(String.format("Memory GET access out of range: 0x%4s",address));
            return 0x0;
        }
        else{
            return memory[address];
        }
    }

    /**
     * Sets the content of one byte of memory.
     */
    public  void set(short address, byte content){
        if(address>0xFFF){
            System.err.println(String.format("Memory SET access out of range: 0x%4s", address));
        }
        else{
            memory[address] = content;
        }
    }


    /**
     * Prints memory from startAddress to endAddress via stdout.
     */
    public  void printMemory(short startAddress,short endAddress){
        short currentAddress = startAddress;
        while(currentAddress<=endAddress){
           System.out.println(String.format("0x%03X:  %02X",currentAddress,memory[currentAddress]));
            currentAddress+=0x1;
        }

    }

    /**
     * Loads default sprites on memory.
     */
    private  void loadDefaultSpritesOnMemory(){
        for(byte i = 0; i < sprite_0.length;i++){
            set((short)(hexadecimalSpritesStartAddress + i),sprite_0[i]);
        }
        for(byte i = 0; i < sprite_1.length;i++){
            set((short)(hexadecimalSpritesStartAddress +5 + i),sprite_1[i]);
        }
        for(byte i = 0; i < sprite_2.length;i++){
            set((short)(hexadecimalSpritesStartAddress + 10 + i),sprite_2[i]);
        }
        for(byte i = 0; i < sprite_3.length;i++){
           set((short)(hexadecimalSpritesStartAddress + 15 + i),sprite_3[i]);
        }
        for(byte i = 0; i < sprite_4.length;i++){
           set((short)(hexadecimalSpritesStartAddress + 20 + i),sprite_4[i]);
        }
        for(byte i = 0; i < sprite_5.length;i++){
           set((short)(hexadecimalSpritesStartAddress + 25 + i),sprite_5[i]);
        }
        for(byte i = 0; i < sprite_6.length;i++){
            set((short)(hexadecimalSpritesStartAddress + 30 + i),sprite_6[i]);
        }
        for(byte i = 0; i < sprite_7.length;i++){
            set((short)(hexadecimalSpritesStartAddress + 35 + i),sprite_7[i]);
        }
        for(byte i = 0; i < sprite_8.length;i++){
            set((short)(hexadecimalSpritesStartAddress + 40 + i),sprite_8[i]);
        }
        for(byte i = 0; i < sprite_9.length;i++){
            set((short)(hexadecimalSpritesStartAddress + 45 + i),sprite_9[i]);
        }
        for(byte i = 0; i < sprite_A.length;i++){
            set((short)(hexadecimalSpritesStartAddress + 50 + i),sprite_A[i]);
        }
        for(byte i = 0; i < sprite_B.length;i++){
            set((short)(hexadecimalSpritesStartAddress + 55 + i),sprite_B[i]);
        }
        for(byte i = 0; i < sprite_C.length;i++){
            set((short)(hexadecimalSpritesStartAddress + 60 + i),sprite_C[i]);
        }
        for(byte i = 0; i < sprite_D.length;i++){
            set((short)(hexadecimalSpritesStartAddress + 65 + i),sprite_D[i]);
        }
        for(byte i = 0; i < sprite_E.length;i++){
            set((short)(hexadecimalSpritesStartAddress + 70 + i),sprite_E[i]);
        }
        for(byte i = 0; i < sprite_F.length;i++){
            set((short)(hexadecimalSpritesStartAddress + 75 + i),sprite_F[i]);
        }

    }

    /**
     * Prints the screen memory via stdout.
     */
    public  void printScreen(){
        for(int x= 0; x < width; x++){
            System.out.print("-");
        }
        System.out.print("\n");
        for(int y = 0; y < height; y++){
            for(int x = 0; x < width; x++){
                boolean value = pixels[x][y];
                if(value){
                    System.out.print("▮");
                }
                else{
                    System.out.print(" ");
                }
                if(x==width-1){
                    System.out.print("|\n");
                }
                if(x==0){
                    System.out.print("|");
                }
            }
        }
        for(int x= 0; x < width; x++){
            System.out.print("-");
        }
    }


}
