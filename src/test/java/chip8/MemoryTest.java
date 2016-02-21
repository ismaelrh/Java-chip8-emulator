package chip8;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

public class MemoryTest {


  private Memory memory;


  @Before
  public void initialize() {
    memory = new Memory();

  }

  @Test
  public void setAndGet(){
    memory.set((short)0x154,(byte)0x12);
    byte result = memory.get((short)0x154);
    assertEquals(result,(byte)0x12);
  }

  @Test
  public void setForbidden(){

    ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    //redirect the System-output (normaly the console) to a variable
    System.setErr(new PrintStream(outContent));

    memory.set((short)0x1FFF,(byte)0x12);
    assertTrue(outContent.toString().contains("Memory SET access out of range"));
  }


  @Test
  public void getForbidden(){

    ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    //redirect the System-output (normaly the console) to a variable
    System.setErr(new PrintStream(outContent));

    memory.get((short)0x1FFF);
    assertTrue(outContent.toString().contains("Memory GET access out of range"));
  }





} 
