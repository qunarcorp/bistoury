package qunar.tc.decompiler.code;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.Timeout;
import qunar.tc.decompiler.code.Instruction;
import qunar.tc.decompiler.code.JumpInstruction;

import java.lang.reflect.Array;

public class InstructionTest {

  @Rule public final ExpectedException thrown = ExpectedException.none();

  @Rule public final Timeout globalTimeout = new Timeout(10000);

  // Test written by Diffblue Cover.
  @Test
  public void canFallThroughOutputFalse() {

    // Arrange
    final int[] myIntArray = {};
    final Instruction instruction = new Instruction(200, 2, false, 2, myIntArray);

    // Act and Assert result
    Assert.assertFalse(instruction.canFallThrough());
  }

  // Test written by Diffblue Cover.
  @Test
  public void canFallThroughOutputFalse2() {

    // Arrange
    final int[] myIntArray = {};
    final Instruction instruction = new Instruction(170, 2, false, 2, myIntArray);

    // Act and Assert result
    Assert.assertFalse(instruction.canFallThrough());
  }

  // Test written by Diffblue Cover.
  @Test
  public void canFallThroughOutputFalse3() {

    // Arrange
    final int[] myIntArray = {};
    final Instruction instruction = new Instruction(172, 2, false, 2, myIntArray);

    // Act and Assert result
    Assert.assertFalse(instruction.canFallThrough());
  }

  // Test written by Diffblue Cover.
  @Test
  public void canFallThroughOutputTrue() {

    // Arrange
    final int[] myIntArray = {};
    final Instruction instruction = new Instruction(236, 2, false, 2, myIntArray);

    // Act and Assert result
    Assert.assertTrue(instruction.canFallThrough());
  }

  // Test written by Diffblue Cover.
  @Test
  public void cloneOutputNotNull() {

    // Arrange
    final int[] myIntArray = {};
    final Instruction instruction = new Instruction(168, 2, false, 2, myIntArray);

    // Act
    final Instruction actual = instruction.clone();

    // Assert result
    Assert.assertNotNull(actual);
    Assert.assertEquals(0, ((JumpInstruction)actual).destination);
    Assert.assertEquals(2, ((JumpInstruction)actual).bytecodeVersion);
    Assert.assertArrayEquals(new int[] {}, ((JumpInstruction)actual).operands);
    Assert.assertEquals(168, ((JumpInstruction)actual).opcode);
    Assert.assertFalse(((JumpInstruction)actual).wide);
    Assert.assertEquals(2, ((JumpInstruction)actual).group);
  }

  // Test written by Diffblue Cover.
  @Test
  public void cloneOutputNotNull2() {

    // Arrange
    final int[] myIntArray = {};
    final Instruction instruction = new Instruction(201, 2, false, 2, myIntArray);

    // Act
    final Instruction actual = instruction.clone();

    // Assert result
    Assert.assertNotNull(actual);
    Assert.assertEquals(0, ((JumpInstruction)actual).destination);
    Assert.assertEquals(2, ((JumpInstruction)actual).bytecodeVersion);
    Assert.assertArrayEquals(new int[] {}, ((JumpInstruction)actual).operands);
    Assert.assertEquals(201, ((JumpInstruction)actual).opcode);
    Assert.assertFalse(((JumpInstruction)actual).wide);
    Assert.assertEquals(2, ((JumpInstruction)actual).group);
  }

  // Test written by Diffblue Cover.
  @Test
  public void cloneOutputNotNull3() {

    // Arrange
    final int[] myIntArray = {};
    final Instruction instruction = new Instruction(167, 2, false, 2, myIntArray);

    // Act
    final Instruction actual = instruction.clone();

    // Assert result
    Assert.assertNotNull(actual);
    Assert.assertEquals(0, ((JumpInstruction)actual).destination);
    Assert.assertEquals(2, ((JumpInstruction)actual).bytecodeVersion);
    Assert.assertArrayEquals(new int[] {}, ((JumpInstruction)actual).operands);
    Assert.assertEquals(167, ((JumpInstruction)actual).opcode);
    Assert.assertFalse(((JumpInstruction)actual).wide);
    Assert.assertEquals(2, ((JumpInstruction)actual).group);
  }

  // Test written by Diffblue Cover.
  @Test
  public void cloneOutputNotNull4() {

    // Arrange
    final int[] myIntArray = {};
    final Instruction instruction = new Instruction(200, 2, false, 2, myIntArray);

    // Act
    final Instruction actual = instruction.clone();

    // Assert result
    Assert.assertNotNull(actual);
    Assert.assertEquals(0, ((JumpInstruction)actual).destination);
    Assert.assertEquals(2, ((JumpInstruction)actual).bytecodeVersion);
    Assert.assertArrayEquals(new int[] {}, ((JumpInstruction)actual).operands);
    Assert.assertEquals(200, ((JumpInstruction)actual).opcode);
    Assert.assertFalse(((JumpInstruction)actual).wide);
    Assert.assertEquals(2, ((JumpInstruction)actual).group);
  }

  // Test written by Diffblue Cover.
  @Test
  public void cloneOutputNotNull5() {

    // Arrange
    final int[] myIntArray = {};
    final Instruction instruction = new Instruction(192, 2, false, 2, myIntArray);

    // Act
    final Instruction actual = instruction.clone();

    // Assert result
    Assert.assertNotNull(actual);
    Assert.assertEquals(2, actual.bytecodeVersion);
    Assert.assertArrayEquals(new int[] {}, actual.operands);
    Assert.assertEquals(192, actual.opcode);
    Assert.assertFalse(actual.wide);
    Assert.assertEquals(2, actual.group);
  }

  // Test written by Diffblue Cover.
  @Test
  public void createInputPositiveFalsePositivePositive1OutputNotNull() {

    // Arrange
    final int[] operands = {0};

    // Act
    final Instruction actual = Instruction.create(168, false, 2, 2, operands);

    // Assert result
    Assert.assertNotNull(actual);
    Assert.assertEquals(0, ((JumpInstruction)actual).destination);
    Assert.assertEquals(2, ((JumpInstruction)actual).bytecodeVersion);
    Assert.assertArrayEquals(new int[] {0}, ((JumpInstruction)actual).operands);
    Assert.assertEquals(168, ((JumpInstruction)actual).opcode);
    Assert.assertFalse(((JumpInstruction)actual).wide);
    Assert.assertEquals(2, ((JumpInstruction)actual).group);
  }

  // Test written by Diffblue Cover.
  @Test
  public void createInputPositiveFalsePositivePositive1OutputNotNull2() {

    // Arrange
    final int[] operands = {0};

    // Act
    final Instruction actual = Instruction.create(201, false, 2, 2, operands);

    // Assert result
    Assert.assertNotNull(actual);
    Assert.assertEquals(0, ((JumpInstruction)actual).destination);
    Assert.assertEquals(2, ((JumpInstruction)actual).bytecodeVersion);
    Assert.assertArrayEquals(new int[] {0}, ((JumpInstruction)actual).operands);
    Assert.assertEquals(201, ((JumpInstruction)actual).opcode);
    Assert.assertFalse(((JumpInstruction)actual).wide);
    Assert.assertEquals(2, ((JumpInstruction)actual).group);
  }

  // Test written by Diffblue Cover.
  @Test
  public void createInputPositiveFalsePositivePositive1OutputNotNull3() {

    // Arrange
    final int[] operands = {0};

    // Act
    final Instruction actual = Instruction.create(167, false, 2, 2, operands);

    // Assert result
    Assert.assertNotNull(actual);
    Assert.assertEquals(0, ((JumpInstruction)actual).destination);
    Assert.assertEquals(2, ((JumpInstruction)actual).bytecodeVersion);
    Assert.assertArrayEquals(new int[] {0}, ((JumpInstruction)actual).operands);
    Assert.assertEquals(167, ((JumpInstruction)actual).opcode);
    Assert.assertFalse(((JumpInstruction)actual).wide);
    Assert.assertEquals(2, ((JumpInstruction)actual).group);
  }

  // Test written by Diffblue Cover.
  @Test
  public void createInputPositiveFalsePositivePositive1OutputNotNull4() {

    // Arrange
    final int[] operands = {0};

    // Act
    final Instruction actual = Instruction.create(200, false, 2, 2, operands);

    // Assert result
    Assert.assertNotNull(actual);
    Assert.assertEquals(0, ((JumpInstruction)actual).destination);
    Assert.assertEquals(2, ((JumpInstruction)actual).bytecodeVersion);
    Assert.assertArrayEquals(new int[] {0}, ((JumpInstruction)actual).operands);
    Assert.assertEquals(200, ((JumpInstruction)actual).opcode);
    Assert.assertFalse(((JumpInstruction)actual).wide);
    Assert.assertEquals(2, ((JumpInstruction)actual).group);
  }

  // Test written by Diffblue Cover.
  @Test
  public void createInputPositiveFalsePositivePositive1OutputNotNull7() {

    // Arrange
    final int[] operands = {0};

    // Act
    final Instruction actual = Instruction.create(169, false, 2, 2, operands);

    // Assert result
    Assert.assertNotNull(actual);
    Assert.assertEquals(2, actual.bytecodeVersion);
    Assert.assertArrayEquals(new int[] {0}, actual.operands);
    Assert.assertEquals(169, actual.opcode);
    Assert.assertFalse(actual.wide);
    Assert.assertEquals(2, actual.group);
  }

  // Test written by Diffblue Cover.
  @Test
  public void equalsInputNotNullNotNullOutputFalse() {

    // Arrange
    final int[] myIntArray = {};
    final Instruction i1 = new Instruction(24, 2, false, 2, myIntArray);
    final int[] myIntArray1 = {};
    final Instruction i2 = new Instruction(16, 2, false, 2, myIntArray1);

    // Act and Assert result
    Assert.assertFalse(Instruction.equals(i1, i2));
  }

  // Test written by Diffblue Cover.
  @Test
  public void equalsInputNotNullNotNullOutputFalse2() {

    // Arrange
    final int[] myIntArray = {};
    final Instruction i1 = new Instruction(16, 2, true, 2, myIntArray);
    final int[] myIntArray1 = {};
    final Instruction i2 = new Instruction(16, 2, false, 2, myIntArray1);

    // Act and Assert result
    Assert.assertFalse(Instruction.equals(i1, i2));
  }

  // Test written by Diffblue Cover.
  @Test
  public void equalsInputNotNullNotNullOutputFalse3() {

    // Arrange
    final int[] myIntArray = {0};
    final Instruction i1 = new Instruction(16, 2, false, 2, myIntArray);
    final int[] myIntArray1 = {};
    final Instruction i2 = new Instruction(16, 2, false, 2, myIntArray1);

    // Act and Assert result
    Assert.assertFalse(Instruction.equals(i1, i2));
  }

  // Test written by Diffblue Cover.
  @Test
  public void equalsInputNotNullNotNullOutputTrue() {

    // Arrange
    final int[] myIntArray = {};
    final Instruction i1 = new Instruction(16, 2, false, 2, myIntArray);
    final int[] myIntArray1 = {};
    final Instruction i2 = new Instruction(16, 2, false, 2, myIntArray1);

    // Act and Assert result
    Assert.assertTrue(Instruction.equals(i1, i2));
  }

  // Test written by Diffblue Cover.
  @Test
  public void operandInputPositiveOutputZero() {

    // Arrange
    final int[] myIntArray = {1, 1, 0};
    final Instruction instruction = new Instruction(2, 2, false, 4, myIntArray);

    // Act and Assert result
    Assert.assertEquals(0, instruction.operand(2));
  }

  // Test written by Diffblue Cover.
  @Test
  public void operandsCountOutputZero() {

    // Arrange
    final int[] myIntArray = {};
    final Instruction instruction = new Instruction(197_200, 2, false, 2, myIntArray);

    // Act and Assert result
    Assert.assertEquals(0, instruction.operandsCount());
  }
}
