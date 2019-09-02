package qunar.tc.decompiler.struct.gen;

import static org.mockito.AdditionalMatchers.or;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.Timeout;
import qunar.tc.decompiler.struct.gen.VarType;

import java.lang.reflect.Array;

public class VarTypeTest {

  @Rule public final ExpectedException thrown = ExpectedException.none();

  @Rule public final Timeout globalTimeout = new Timeout(10000);

  // Test written by Diffblue Cover.

  @Test
  public void constructorInputNotNullFalseOutputIllegalArgumentException() {

    // Arrange
    final String signature = "/";
    final boolean clType = false;

    // Act, creating object to test constructor
    thrown.expect(IllegalArgumentException.class);
    final VarType varType = new VarType(signature, clType);

    // The method is not expected to return due to exception thrown
  }

  // Test written by Diffblue Cover.

  @Test
  public void constructorInputNotNullFalseOutputIllegalArgumentException2() {

    // Arrange
    final String signature = "/";
    final boolean clType = false;

    // Act, creating object to test constructor
    thrown.expect(IllegalArgumentException.class);
    final VarType varType = new VarType(signature, clType);

    // The method is not expected to return due to exception thrown
  }

  // Test written by Diffblue Cover.

  @Test
  public void constructorInputNotNullFalseOutputVoid() {

    // Arrange
    final String signature = "D";
    final boolean clType = false;

    // Act, creating object to test constructor
    final VarType varType = new VarType(signature, clType);

    // Assert side effects
    Assert.assertEquals(2, varType.stackSize);
    Assert.assertEquals(2, varType.type);
    Assert.assertEquals(5, varType.typeFamily);
    Assert.assertEquals("D", varType.value);
  }

  // Test written by Diffblue Cover.

  @Test
  public void constructorInputNotNullOutputIllegalArgumentException() {

    // Arrange
    final String signature = "\'";

    // Act, creating object to test constructor
    thrown.expect(IllegalArgumentException.class);
    final VarType varType = new VarType(signature);

    // The method is not expected to return due to exception thrown
  }

  // Test written by Diffblue Cover.

  @Test
  public void constructorInputNotNullOutputIllegalArgumentException2() {

    // Arrange
    final String signature = ",";

    // Act, creating object to test constructor
    thrown.expect(IllegalArgumentException.class);
    final VarType varType = new VarType(signature);

    // The method is not expected to return due to exception thrown
  }

  // Test written by Diffblue Cover.

  @Test
  public void constructorInputNotNullOutputVoid() {

    // Arrange
    final String signature = "a\'b\'c";

    // Act, creating object to test constructor
    final VarType varType = new VarType(signature);

    // Assert side effects
    Assert.assertEquals(1, varType.stackSize);
    Assert.assertEquals(8, varType.type);
    Assert.assertEquals(6, varType.typeFamily);
    Assert.assertEquals("a\'b\'c", varType.value);
  }

  // Test written by Diffblue Cover.

  @Test
  public void constructorInputNotNullOutputVoid2() {

    // Arrange
    final String signature = "a\'b\'c";

    // Act, creating object to test constructor
    final VarType varType = new VarType(signature);

    // Assert side effects
    Assert.assertEquals(8, varType.type);
    Assert.assertEquals(6, varType.typeFamily);
    Assert.assertEquals("a\'b\'c", varType.value);
  }

  // Test written by Diffblue Cover.

  @Test
  public void constructorInputNotNullOutputVoid3() {

    // Arrange
    final String signature = "a\'b\'c";

    // Act, creating object to test constructor
    final VarType varType = new VarType(signature);

    // Assert side effects
    Assert.assertEquals(1, varType.stackSize);
    Assert.assertEquals(8, varType.type);
    Assert.assertEquals(6, varType.typeFamily);
    Assert.assertEquals("a\'b\'c", varType.value);
  }

  // Test written by Diffblue Cover.

  @Test
  public void constructorInputNotNullOutputVoid4() {

    // Arrange
    final String signature = "a\'b\'c";

    // Act, creating object to test constructor
    final VarType varType = new VarType(signature);

    // Assert side effects
    Assert.assertEquals(8, varType.type);
    Assert.assertEquals("a\'b\'c", varType.value);
  }

  // Test written by Diffblue Cover.

  @Test
  public void constructorInputNotNullOutputVoid5() {

    // Arrange
    final String signature = "LD";

    // Act, creating object to test constructor
    final VarType varType = new VarType(signature);

    // Assert side effects
    Assert.assertEquals(1, varType.stackSize);
    Assert.assertEquals(8, varType.type);
    Assert.assertEquals(6, varType.typeFamily);
    Assert.assertEquals("LD", varType.value);
  }

  // Test written by Diffblue Cover.

  @Test
  public void constructorInputNotNullOutputVoid6() {

    // Arrange
    final String signature = "L;";

    // Act, creating object to test constructor
    final VarType varType = new VarType(signature);

    // Assert side effects
    Assert.assertEquals(1, varType.stackSize);
    Assert.assertEquals(8, varType.type);
    Assert.assertEquals(6, varType.typeFamily);
    Assert.assertEquals("", varType.value);
  }

  // Test written by Diffblue Cover.

  @Test
  public void constructorInputNotNullOutputVoid7() {

    // Arrange
    final String signature =
        "[\uffff[\ufffe\ufffe\ufffe\ufffe\ufffe\ufffe\ufffe\ufffe\ufffe\ufffe\ufffe\ufffe\ufffe\ufffe\ufffe\ufffe\ufffe\ufffe\ufffe\ufffe\ufffe\ufffe\ufffe\ufffe\ufffe\ufffe\ufffe\ufffe\ufffe\ufffe\ufffe\ufffe\ufffe\ufffe\ufffe\ufffe\ufffe\ufffe\ufffe\ufffe\ufffe\ufffe\ufffe\ufffe\ufffe\ufffe\ufffe\ufffe\ufffe\ufffe\ufffe\ufffe\ufffe\ufffe\ufffe\ufffe\ufffe\ufffe\ufffe\ufffe;";

    // Act, creating object to test constructor
    final VarType varType = new VarType(signature);

    // Assert side effects
    Assert.assertEquals(1, varType.stackSize);
    Assert.assertEquals(1, varType.arrayDim);
    Assert.assertEquals(8, varType.type);
    Assert.assertEquals(6, varType.typeFamily);
    Assert.assertEquals(
        "\uffff[\ufffe\ufffe\ufffe\ufffe\ufffe\ufffe\ufffe\ufffe\ufffe\ufffe\ufffe\ufffe\ufffe\ufffe\ufffe\ufffe\ufffe\ufffe\ufffe\ufffe\ufffe\ufffe\ufffe\ufffe\ufffe\ufffe\ufffe\ufffe\ufffe\ufffe\ufffe\ufffe\ufffe\ufffe\ufffe\ufffe\ufffe\ufffe\ufffe\ufffe\ufffe\ufffe\ufffe\ufffe\ufffe\ufffe\ufffe\ufffe\ufffe\ufffe\ufffe\ufffe\ufffe\ufffe\ufffe\ufffe\ufffe\ufffe\ufffe\ufffe;",
        varType.value);
  }

  // Test written by Diffblue Cover.

  @Test
  public void constructorInputNotNullOutputVoid8() {

    // Arrange
    final String signature = "[[G";

    // Act, creating object to test constructor
    final VarType varType = new VarType(signature);

    // Assert side effects
    Assert.assertEquals(1, varType.stackSize);
    Assert.assertEquals(2, varType.arrayDim);
    Assert.assertEquals(12, varType.type);
    Assert.assertEquals(6, varType.typeFamily);
    Assert.assertEquals("G", varType.value);
  }

  // Test written by Diffblue Cover.

  @Test
  public void constructorInputNotNullOutputVoid9() {

    // Arrange
    final String signature = "[";

    // Act, creating object to test constructor
    final VarType varType = new VarType(signature);

    // Assert side effects
    Assert.assertEquals(1, varType.stackSize);
    Assert.assertEquals(1, varType.arrayDim);
    Assert.assertEquals(6, varType.typeFamily);
    Assert.assertNull(varType.value);
  }

  // Test written by Diffblue Cover.

  @Test
  public void constructorInputNotNullTrueOutputVoid() {

    // Arrange
    final String signature = "/";
    final boolean clType = true;

    // Act, creating object to test constructor
    final VarType varType = new VarType(signature, clType);

    // Assert side effects
    Assert.assertEquals(8, varType.type);
    Assert.assertEquals(6, varType.typeFamily);
    Assert.assertEquals("/", varType.value);
  }

  // Test written by Diffblue Cover.

  @Test
  public void constructorInputNotNullTrueOutputVoid2() {

    // Arrange
    final String signature = "a\'b\'c";
    final boolean clType = true;

    // Act, creating object to test constructor
    final VarType varType = new VarType(signature, clType);

    // Assert side effects
    Assert.assertEquals(1, varType.stackSize);
    Assert.assertEquals(8, varType.type);
    Assert.assertEquals(6, varType.typeFamily);
    Assert.assertEquals("a\'b\'c", varType.value);
  }

  // Test written by Diffblue Cover.

  @Test
  public void constructorInputNotNullTrueOutputVoid3() {

    // Arrange
    final String signature = "a\'b\'c";
    final boolean clType = true;

    // Act, creating object to test constructor
    final VarType varType = new VarType(signature, clType);

    // Assert side effects
    Assert.assertEquals(8, varType.type);
    Assert.assertEquals("a\'b\'c", varType.value);
  }

  // Test written by Diffblue Cover.

  @Test
  public void constructorInputNotNullTrueOutputVoid4() {

    // Arrange
    final String signature = "a\'b\'c";
    final boolean clType = true;

    // Act, creating object to test constructor
    final VarType varType = new VarType(signature, clType);

    // Assert side effects
    Assert.assertEquals(8, varType.type);
    Assert.assertEquals(6, varType.typeFamily);
    Assert.assertEquals("a\'b\'c", varType.value);
  }

  // Test written by Diffblue Cover.

  @Test
  public void constructorInputNotNullTrueOutputVoid5() {

    // Arrange
    final String signature = "L";
    final boolean clType = true;

    // Act, creating object to test constructor
    final VarType varType = new VarType(signature, clType);

    // Assert side effects
    Assert.assertEquals(1, varType.stackSize);
    Assert.assertEquals(8, varType.type);
    Assert.assertEquals(6, varType.typeFamily);
    Assert.assertEquals("L", varType.value);
  }

  // Test written by Diffblue Cover.

  @Test
  public void constructorInputNotNullTrueOutputVoid6() {

    // Arrange
    final String signature = "";
    final boolean clType = true;

    // Act, creating object to test constructor
    final VarType varType = new VarType(signature, clType);

    // Assert side effects
    Assert.assertEquals(1, varType.stackSize);
    Assert.assertEquals(2, varType.typeFamily);
  }

  // Test written by Diffblue Cover.

  @Test
  public void constructorInputNotNullTrueOutputVoid7() {

    // Arrange
    final String signature =
        "[[\uc038\uc038\uc038\uc038\uc038\uc038\uc038\uc038\uc038\uc038\uc038\uc038\uc038\uc038\uc038\uc038\uc038\uc038\uc038\uc038\uc038\uc038\uc038\uc038\uc038\uc038\uc038\uc038\uc038\uc038\uc038\uc038\uc038\uc038\uc038\uc038\uc038\uc038\uc038\uc038\uc038\uc038\uc038\uc038\uc038\uc038\uc038\uc038\uc038\uc038\uc038\uc038\uc038\uc038\uc038\uc038\uc038\uc038\uc038\uc038\uc038\uc038\uc038\uc038\uc038\uc038\uc038\uc038\uc038\uc038\uc038\uc038\uc038\uc038\uc038\uc038\uc038\uc038\uc038\uc038\uc038\uc038\uc038\uc038\uc038\uc038\uc038\uc038\uc038\uc038\uc038\uc038;";
    final boolean clType = true;

    // Act, creating object to test constructor
    final VarType varType = new VarType(signature, clType);

    // Assert side effects
    Assert.assertEquals(1, varType.stackSize);
    Assert.assertEquals(2, varType.arrayDim);
    Assert.assertEquals(8, varType.type);
    Assert.assertEquals(6, varType.typeFamily);
    Assert.assertEquals(
        "\uc038\uc038\uc038\uc038\uc038\uc038\uc038\uc038\uc038\uc038\uc038\uc038\uc038\uc038\uc038\uc038\uc038\uc038\uc038\uc038\uc038\uc038\uc038\uc038\uc038\uc038\uc038\uc038\uc038\uc038\uc038\uc038\uc038\uc038\uc038\uc038\uc038\uc038\uc038\uc038\uc038\uc038\uc038\uc038\uc038\uc038\uc038\uc038\uc038\uc038\uc038\uc038\uc038\uc038\uc038\uc038\uc038\uc038\uc038\uc038\uc038\uc038\uc038\uc038\uc038\uc038\uc038\uc038\uc038\uc038\uc038\uc038\uc038\uc038\uc038\uc038\uc038\uc038\uc038\uc038\uc038\uc038\uc038\uc038\uc038\uc038\uc038\uc038\uc038\uc038\uc038\uc038;",
        varType.value);
  }

  // Test written by Diffblue Cover.

  @Test
  public void constructorInputNotNullTrueOutputVoid8() {

    // Arrange
    final String signature =
        "[[L\uc05c\uc05c\uc05c\uc05c\uc05c\uc05c\uc05c\uc05c\uc05c\uc05c\uc05c\uc05c\uc05c\uc05c\uc05c\uc05c\uc05c\uc05c\uc05c\uc05c\uc05c\uc05c\uc05c\uc05c;";
    final boolean clType = true;

    // Act, creating object to test constructor
    final VarType varType = new VarType(signature, clType);

    // Assert side effects
    Assert.assertEquals(1, varType.stackSize);
    Assert.assertEquals(2, varType.arrayDim);
    Assert.assertEquals(8, varType.type);
    Assert.assertEquals(6, varType.typeFamily);
    Assert.assertEquals(
        "\uc05c\uc05c\uc05c\uc05c\uc05c\uc05c\uc05c\uc05c\uc05c\uc05c\uc05c\uc05c\uc05c\uc05c\uc05c\uc05c\uc05c\uc05c\uc05c\uc05c\uc05c\uc05c\uc05c\uc05c",
        varType.value);
  }

  // Test written by Diffblue Cover.
  @Test
  public void copyInputFalseOutputNotNull() {

    // Arrange
    final VarType varType = new VarType(14);

    // Act
    final VarType actual = varType.copy(false);

    // Assert result
    Assert.assertNotNull(actual);
    Assert.assertEquals(1, actual.stackSize);
    Assert.assertEquals(0, actual.arrayDim);
    Assert.assertFalse(actual.falseBoolean);
    Assert.assertEquals(14, actual.type);
    Assert.assertEquals(0, actual.typeFamily);
    Assert.assertEquals("N", actual.value);
  }

  // Test written by Diffblue Cover.
  @Test
  public void copyInputTrueOutputNotNull() {

    // Arrange
    final VarType varType = new VarType(14);

    // Act
    final VarType actual = varType.copy(true);

    // Assert result
    Assert.assertNotNull(actual);
    Assert.assertEquals(1, actual.stackSize);
    Assert.assertEquals(0, actual.arrayDim);
    Assert.assertTrue(actual.falseBoolean);
    Assert.assertEquals(14, actual.type);
    Assert.assertEquals(0, actual.typeFamily);
    Assert.assertEquals("N", actual.value);
  }

  // Test written by Diffblue Cover.
  @Test
  public void copyInputTrueOutputNotNull2() {

    // Arrange
    final VarType varType = new VarType(13);

    // Act
    final VarType actual = varType.copy(true);

    // Assert result
    Assert.assertNotNull(actual);
    Assert.assertEquals(1, actual.stackSize);
    Assert.assertEquals(0, actual.arrayDim);
    Assert.assertTrue(actual.falseBoolean);
    Assert.assertEquals(13, actual.type);
    Assert.assertEquals(6, actual.typeFamily);
    Assert.assertNull(actual.value);
  }

  // Test written by Diffblue Cover.
  @Test
  public void copyInputTrueOutputNotNull3() {

    // Arrange
    final VarType varType = new VarType(9);

    // Act
    final VarType actual = varType.copy(true);

    // Assert result
    Assert.assertNotNull(actual);
    Assert.assertEquals(1, actual.stackSize);
    Assert.assertEquals(0, actual.arrayDim);
    Assert.assertTrue(actual.falseBoolean);
    Assert.assertEquals(9, actual.type);
    Assert.assertEquals(0, actual.typeFamily);
    Assert.assertEquals("A", actual.value);
  }

  // Test written by Diffblue Cover.
  @Test
  public void copyInputTrueOutputNotNull4() {

    // Arrange
    final VarType varType = new VarType(12);

    // Act
    final VarType actual = varType.copy(true);

    // Assert result
    Assert.assertNotNull(actual);
    Assert.assertEquals(0, actual.stackSize);
    Assert.assertEquals(0, actual.arrayDim);
    Assert.assertTrue(actual.falseBoolean);
    Assert.assertEquals(12, actual.type);
    Assert.assertEquals(0, actual.typeFamily);
    Assert.assertEquals("G", actual.value);
  }

  // Test written by Diffblue Cover.
  @Test
  public void copyOutputNotNull() {

    // Arrange
    final VarType varType = new VarType(13);

    // Act
    final VarType actual = varType.copy();

    // Assert result
    Assert.assertNotNull(actual);
    Assert.assertEquals(1, actual.stackSize);
    Assert.assertEquals(0, actual.arrayDim);
    Assert.assertFalse(actual.falseBoolean);
    Assert.assertEquals(13, actual.type);
    Assert.assertEquals(6, actual.typeFamily);
    Assert.assertNull(actual.value);
  }

  // Test written by Diffblue Cover.
  @Test
  public void copyOutputNotNull2() {

    // Arrange
    final VarType varType = new VarType(9);

    // Act
    final VarType actual = varType.copy();

    // Assert result
    Assert.assertNotNull(actual);
    Assert.assertEquals(1, actual.stackSize);
    Assert.assertEquals(0, actual.arrayDim);
    Assert.assertFalse(actual.falseBoolean);
    Assert.assertEquals(9, actual.type);
    Assert.assertEquals(0, actual.typeFamily);
    Assert.assertEquals("A", actual.value);
  }

  // Test written by Diffblue Cover.
  @Test
  public void copyOutputNotNull3() {

    // Arrange
    final VarType varType = new VarType(14);

    // Act
    final VarType actual = varType.copy();

    // Assert result
    Assert.assertNotNull(actual);
    Assert.assertEquals(1, actual.stackSize);
    Assert.assertEquals(0, actual.arrayDim);
    Assert.assertFalse(actual.falseBoolean);
    Assert.assertEquals(14, actual.type);
    Assert.assertEquals(0, actual.typeFamily);
    Assert.assertEquals("N", actual.value);
  }

  // Test written by Diffblue Cover.
  @Test
  public void copyOutputNotNull4() {

    // Arrange
    final VarType varType = new VarType(12);

    // Act
    final VarType actual = varType.copy();

    // Assert result
    Assert.assertNotNull(actual);
    Assert.assertEquals(0, actual.stackSize);
    Assert.assertEquals(0, actual.arrayDim);
    Assert.assertFalse(actual.falseBoolean);
    Assert.assertEquals(12, actual.type);
    Assert.assertEquals(0, actual.typeFamily);
    Assert.assertEquals("G", actual.value);
  }

  // Test written by Diffblue Cover.
  @Test
  public void decreaseArrayDimOutputNotNull() {

    // Arrange
    final VarType varType = new VarType(12);

    // Act
    final VarType actual = varType.decreaseArrayDim();

    // Assert result
    Assert.assertNotNull(actual);
    Assert.assertEquals(0, actual.stackSize);
    Assert.assertEquals(0, actual.arrayDim);
    Assert.assertFalse(actual.falseBoolean);
    Assert.assertEquals(12, actual.type);
    Assert.assertEquals(0, actual.typeFamily);
    Assert.assertEquals("G", actual.value);
  }

  // Test written by Diffblue Cover.
  @Test
  public void decreaseArrayDimOutputNotNull2() {

    // Arrange
    final VarType varType = new VarType(9);

    // Act
    final VarType actual = varType.decreaseArrayDim();

    // Assert result
    Assert.assertNotNull(actual);
    Assert.assertEquals(1, actual.stackSize);
    Assert.assertEquals(0, actual.arrayDim);
    Assert.assertFalse(actual.falseBoolean);
    Assert.assertEquals(9, actual.type);
    Assert.assertEquals(0, actual.typeFamily);
    Assert.assertEquals("A", actual.value);
  }

  // Test written by Diffblue Cover.
  @Test
  public void decreaseArrayDimOutputNotNull3() {

    // Arrange
    final VarType varType = new VarType(13);

    // Act
    final VarType actual = varType.decreaseArrayDim();

    // Assert result
    Assert.assertNotNull(actual);
    Assert.assertEquals(1, actual.stackSize);
    Assert.assertEquals(0, actual.arrayDim);
    Assert.assertFalse(actual.falseBoolean);
    Assert.assertEquals(13, actual.type);
    Assert.assertEquals(6, actual.typeFamily);
    Assert.assertNull(actual.value);
  }

  // Test written by Diffblue Cover.
  @Test
  public void decreaseArrayDimOutputNotNull4() {

    // Arrange
    final VarType varType = new VarType(14);

    // Act
    final VarType actual = varType.decreaseArrayDim();

    // Assert result
    Assert.assertNotNull(actual);
    Assert.assertEquals(1, actual.stackSize);
    Assert.assertEquals(0, actual.arrayDim);
    Assert.assertFalse(actual.falseBoolean);
    Assert.assertEquals(14, actual.type);
    Assert.assertEquals(0, actual.typeFamily);
    Assert.assertEquals("N", actual.value);
  }

  // Test written by Diffblue Cover.
  @Test
  public void equalsInputNotNullOutputFalse() {

    // Arrange
    final VarType varType = new VarType(12);
    final VarType o = new VarType(8);

    // Act and Assert result
    Assert.assertFalse(varType.equals(o));
  }

  // Test written by Diffblue Cover.
  @Test
  public void equalsInputNotNullOutputFalse2() {

    // Arrange
    final VarType varType = new VarType(9);
    final VarType o = new VarType(8);

    // Act and Assert result
    Assert.assertFalse(varType.equals(o));
  }

  // Test written by Diffblue Cover.
  @Test
  public void equalsInputNotNullOutputFalse3() {

    // Arrange
    final VarType varType = new VarType(9);
    final VarType o = new VarType(14);

    // Act and Assert result
    Assert.assertFalse(varType.equals(o));
  }

  // Test written by Diffblue Cover.
  @Test
  public void getCommonMinTypeInputNotNullNotNullOutputNotNull6() {

    // Arrange
    final VarType type1 = new VarType(16);
    final VarType type2 = new VarType(4);

    // Act
    final VarType actual = VarType.getCommonMinType(type1, type2);

    // Assert result
    Assert.assertNotNull(actual);
    Assert.assertEquals(1, actual.stackSize);
    Assert.assertEquals(0, actual.arrayDim);
    Assert.assertFalse(actual.falseBoolean);
    Assert.assertEquals(16, actual.type);
    Assert.assertEquals(2, actual.typeFamily);
    Assert.assertEquals("Y", actual.value);
  }

  // Test written by Diffblue Cover.
  @Test
  public void getCommonMinTypeInputNotNullNotNullOutputNotNull7() {

    // Arrange
    final VarType type1 = new VarType(16);
    final VarType type2 = new VarType(15);

    // Act
    final VarType actual = VarType.getCommonMinType(type1, type2);

    // Assert result
    Assert.assertNotNull(actual);
    Assert.assertEquals(1, actual.stackSize);
    Assert.assertEquals(0, actual.arrayDim);
    Assert.assertFalse(actual.falseBoolean);
    Assert.assertEquals(15, actual.type);
    Assert.assertEquals(2, actual.typeFamily);
    Assert.assertEquals("X", actual.value);
  }

  // Test written by Diffblue Cover.
  @Test
  public void getCommonMinTypeInputNotNullNotNullOutputNotNull8() {

    // Arrange
    final VarType type1 = new VarType(16);
    final VarType type2 = new VarType(7);

    // Act
    final VarType actual = VarType.getCommonMinType(type1, type2);

    // Assert result
    Assert.assertNotNull(actual);
    Assert.assertEquals(1, actual.stackSize);
    Assert.assertEquals(0, actual.arrayDim);
    Assert.assertFalse(actual.falseBoolean);
    Assert.assertEquals(7, actual.type);
    Assert.assertEquals(1, actual.typeFamily);
    Assert.assertEquals("Z", actual.value);
  }

  // Test written by Diffblue Cover.
  @Test
  public void getCommonMinTypeInputNotNullNotNullOutputNotNull9() {

    // Arrange
    final VarType type1 = new VarType(6);
    final VarType type2 = new VarType(4);

    // Act
    final VarType actual = VarType.getCommonMinType(type1, type2);

    // Assert result
    Assert.assertNotNull(actual);
    Assert.assertEquals(1, actual.stackSize);
    Assert.assertEquals(0, actual.arrayDim);
    Assert.assertFalse(actual.falseBoolean);
    Assert.assertEquals(6, actual.type);
    Assert.assertEquals(2, actual.typeFamily);
    Assert.assertEquals("S", actual.value);
  }

  // Test written by Diffblue Cover.
  @Test
  public void getCommonMinTypeInputNotNullNotNullOutputNotNull11() {

    // Arrange
    final VarType type1 = new VarType(7);
    final VarType type2 = new VarType(15);

    // Act
    final VarType actual = VarType.getCommonMinType(type1, type2);

    // Assert result
    Assert.assertNotNull(actual);
    Assert.assertEquals(1, actual.stackSize);
    Assert.assertEquals(0, actual.arrayDim);
    Assert.assertFalse(actual.falseBoolean);
    Assert.assertEquals(7, actual.type);
    Assert.assertEquals(1, actual.typeFamily);
    Assert.assertEquals("Z", actual.value);
  }

  // Test written by Diffblue Cover.
  @Test
  public void getCommonMinTypeInputNotNullNotNullOutputNotNull12() {

    // Arrange
    final VarType type1 = new VarType(17);
    final VarType type2 = new VarType(15);

    // Act
    final VarType actual = VarType.getCommonMinType(type1, type2);

    // Assert result
    Assert.assertNotNull(actual);
    Assert.assertEquals(1, actual.stackSize);
    Assert.assertEquals(0, actual.arrayDim);
    Assert.assertFalse(actual.falseBoolean);
    Assert.assertEquals(17, actual.type);
    Assert.assertEquals(0, actual.typeFamily);
    Assert.assertEquals("U", actual.value);
  }

  // Test written by Diffblue Cover.
  @Test
  public void getCommonMinTypeInputNotNullNotNullOutputNotNull13() {

    // Arrange
    final VarType type1 = new VarType(7);
    final VarType type2 = new VarType(7);

    // Act
    final VarType actual = VarType.getCommonMinType(type1, type2);

    // Assert result
    Assert.assertNotNull(actual);
    Assert.assertEquals(1, actual.stackSize);
    Assert.assertEquals(0, actual.arrayDim);
    Assert.assertFalse(actual.falseBoolean);
    Assert.assertEquals(7, actual.type);
    Assert.assertEquals(1, actual.typeFamily);
    Assert.assertEquals("Z", actual.value);
  }

  // Test written by Diffblue Cover.
  @Test
  public void getCommonMinTypeInputNotNullNotNullOutputNotNull14() {

    // Arrange
    final VarType type1 = new VarType(13);
    final VarType type2 = new VarType(8);

    // Act
    final VarType actual = VarType.getCommonMinType(type1, type2);

    // Assert result
    Assert.assertNotNull(actual);
    Assert.assertEquals(1, actual.stackSize);
    Assert.assertEquals(0, actual.arrayDim);
    Assert.assertFalse(actual.falseBoolean);
    Assert.assertEquals(13, actual.type);
    Assert.assertEquals(6, actual.typeFamily);
    Assert.assertNull(actual.value);
  }

  // Test written by Diffblue Cover.
  @Test
  public void getCommonMinTypeInputNotNullNotNullOutputNotNull16() {

    // Arrange
    final VarType type1 = new VarType(7);
    final VarType type2 = new VarType(15);

    // Act
    final VarType actual = VarType.getCommonMinType(type1, type2);

    // Assert result
    Assert.assertNotNull(actual);
    Assert.assertEquals(1, actual.stackSize);
    Assert.assertEquals(0, actual.arrayDim);
    Assert.assertFalse(actual.falseBoolean);
    Assert.assertEquals(7, actual.type);
    Assert.assertEquals(1, actual.typeFamily);
    Assert.assertEquals("Z", actual.value);
  }

  // Test written by Diffblue Cover.
  @Test
  public void getCommonMinTypeInputNotNullNotNullOutputNotNull18() {

    // Arrange
    final VarType type1 = new VarType(7);
    final VarType type2 = new VarType(7);

    // Act
    final VarType actual = VarType.getCommonMinType(type1, type2);

    // Assert result
    Assert.assertNotNull(actual);
    Assert.assertEquals(1, actual.stackSize);
    Assert.assertEquals(0, actual.arrayDim);
    Assert.assertFalse(actual.falseBoolean);
    Assert.assertEquals(7, actual.type);
    Assert.assertEquals(1, actual.typeFamily);
    Assert.assertEquals("Z", actual.value);
  }

  // Test written by Diffblue Cover.
  @Test
  public void getCommonMinTypeInputNotNullNotNullOutputNull000a5669df0d10d2064() {

    // Arrange
    final VarType type1 = new VarType(12);
    final VarType type2 = new VarType(10);

    // Act and Assert result
    Assert.assertNull(VarType.getCommonMinType(type1, type2));
  }

  // Test written by Diffblue Cover.
  @Test
  public void getCommonMinTypeInputNotNullNotNullOutputNull2() {

    // Arrange
    final VarType type1 = new VarType(10);
    final VarType type2 = new VarType(6);

    // Act and Assert result
    Assert.assertNull(VarType.getCommonMinType(type1, type2));
  }

  // Test written by Diffblue Cover.
  @Test
  public void getCommonMinTypeInputNotNullNotNullOutputNull3() {

    // Arrange
    final VarType type1 = new VarType(14);
    final VarType type2 = new VarType(6);

    // Act and Assert result
    Assert.assertNull(VarType.getCommonMinType(type1, type2));
  }

  // Test written by Diffblue Cover.
  @Test
  public void getCommonMinTypeInputNotNullNotNullOutputNull4() {

    // Arrange
    final VarType type1 = new VarType(14);
    final VarType type2 = new VarType(4);

    // Act and Assert result
    Assert.assertNull(VarType.getCommonMinType(type1, type2));
  }

  // Test written by Diffblue Cover.
  @Test
  public void getCommonSupertypeInputNotNullNotNullOutputNotNull3() {

    // Arrange
    final VarType type1 = new VarType(4);
    final VarType type2 = new VarType(7);

    // Act
    final VarType actual = VarType.getCommonSupertype(type1, type2);

    // Assert result
    Assert.assertNotNull(actual);
    Assert.assertEquals(1, actual.stackSize);
    Assert.assertEquals(0, actual.arrayDim);
    Assert.assertFalse(actual.falseBoolean);
    Assert.assertEquals(4, actual.type);
    Assert.assertEquals(2, actual.typeFamily);
    Assert.assertEquals("I", actual.value);
  }

  // Test written by Diffblue Cover.
  @Test
  public void getCommonSupertypeInputNotNullNotNullOutputNotNull4() {

    // Arrange
    final VarType type1 = new VarType(4);
    final VarType type2 = new VarType(6);

    // Act
    final VarType actual = VarType.getCommonSupertype(type1, type2);

    // Assert result
    Assert.assertNotNull(actual);
    Assert.assertEquals(1, actual.stackSize);
    Assert.assertEquals(0, actual.arrayDim);
    Assert.assertFalse(actual.falseBoolean);
    Assert.assertEquals(4, actual.type);
    Assert.assertEquals(2, actual.typeFamily);
    Assert.assertEquals("I", actual.value);
  }

  // Test written by Diffblue Cover.
  @Test
  public void getCommonSupertypeInputNotNullNotNullOutputNotNull5() {

    // Arrange
    final VarType type1 = new VarType(15);
    final VarType type2 = new VarType(6);

    // Act
    final VarType actual = VarType.getCommonSupertype(type1, type2);

    // Assert result
    Assert.assertNotNull(actual);
    Assert.assertEquals(1, actual.stackSize);
    Assert.assertEquals(0, actual.arrayDim);
    Assert.assertFalse(actual.falseBoolean);
    Assert.assertEquals(6, actual.type);
    Assert.assertEquals(2, actual.typeFamily);
    Assert.assertEquals("S", actual.value);
  }

  // Test written by Diffblue Cover.
  @Test
  public void getCommonSupertypeInputNotNullNotNullOutputNotNull7() {

    // Arrange
    final VarType type1 = new VarType(5);
    final VarType type2 = new VarType(17);

    // Act
    final VarType actual = VarType.getCommonSupertype(type1, type2);

    // Assert result
    Assert.assertNotNull(actual);
    Assert.assertEquals(2, actual.stackSize);
    Assert.assertEquals(0, actual.arrayDim);
    Assert.assertFalse(actual.falseBoolean);
    Assert.assertEquals(5, actual.type);
    Assert.assertEquals(4, actual.typeFamily);
    Assert.assertEquals("J", actual.value);
  }

  // Test written by Diffblue Cover.
  @Test
  public void getCommonSupertypeInputNotNullNotNullOutputNotNull8() {

    // Arrange
    final VarType type1 = new VarType(13);
    final VarType type2 = new VarType(17);

    // Act
    final VarType actual = VarType.getCommonSupertype(type1, type2);

    // Assert result
    Assert.assertNotNull(actual);
    Assert.assertEquals(1, actual.stackSize);
    Assert.assertEquals(0, actual.arrayDim);
    Assert.assertFalse(actual.falseBoolean);
    Assert.assertEquals(13, actual.type);
    Assert.assertEquals(6, actual.typeFamily);
    Assert.assertNull(actual.value);
  }

  // Test written by Diffblue Cover.
  @Test
  public void getCommonSupertypeInputNotNullNotNullOutputNotNull12() {

    // Arrange
    final VarType type1 = new VarType(13);
    final VarType type2 = new VarType(8);

    // Act
    final VarType actual = VarType.getCommonSupertype(type1, type2);

    // Assert result
    Assert.assertNotNull(actual);
    Assert.assertEquals(1, actual.stackSize);
    Assert.assertEquals(0, actual.arrayDim);
    Assert.assertFalse(actual.falseBoolean);
    Assert.assertEquals(8, actual.type);
    Assert.assertEquals(6, actual.typeFamily);
    Assert.assertNull(actual.value);
  }

  // Test written by Diffblue Cover.
  @Test
  public void getCommonSupertypeInputNotNullNotNullOutputNotNull14() {

    // Arrange
    final VarType type1 = new VarType(7);
    final VarType type2 = new VarType(7);

    // Act
    final VarType actual = VarType.getCommonSupertype(type1, type2);

    // Assert result
    Assert.assertNotNull(actual);
    Assert.assertEquals(1, actual.stackSize);
    Assert.assertEquals(0, actual.arrayDim);
    Assert.assertFalse(actual.falseBoolean);
    Assert.assertEquals(7, actual.type);
    Assert.assertEquals(1, actual.typeFamily);
    Assert.assertEquals("Z", actual.value);
  }

  // Test written by Diffblue Cover.
  @Test
  public void getCommonSupertypeInputNotNullNotNullOutputNotNull15() {

    // Arrange
    final VarType type1 = new VarType(17);
    final VarType type2 = new VarType(10);

    // Act
    final VarType actual = VarType.getCommonSupertype(type1, type2);

    // Assert result
    Assert.assertNotNull(actual);
    Assert.assertEquals(0, actual.stackSize);
    Assert.assertEquals(0, actual.arrayDim);
    Assert.assertFalse(actual.falseBoolean);
    Assert.assertEquals(10, actual.type);
    Assert.assertEquals(0, actual.typeFamily);
    Assert.assertEquals("V", actual.value);
  }

  // Test written by Diffblue Cover.
  @Test
  public void getCommonSupertypeInputNotNullNotNullOutputNotNull16() {

    // Arrange
    final VarType type1 = new VarType(16);
    final VarType type2 = new VarType(15);

    // Act
    final VarType actual = VarType.getCommonSupertype(type1, type2);

    // Assert result
    Assert.assertNotNull(actual);
    Assert.assertEquals(1, actual.stackSize);
    Assert.assertEquals(0, actual.arrayDim);
    Assert.assertFalse(actual.falseBoolean);
    Assert.assertEquals(16, actual.type);
    Assert.assertEquals(2, actual.typeFamily);
    Assert.assertEquals("Y", actual.value);
  }

  // Test written by Diffblue Cover.
  @Test
  public void getCommonSupertypeInputNotNullNotNullOutputNotNull17() {

    // Arrange
    final VarType type1 = new VarType(16);
    final VarType type2 = new VarType(4);

    // Act
    final VarType actual = VarType.getCommonSupertype(type1, type2);

    // Assert result
    Assert.assertNotNull(actual);
    Assert.assertEquals(1, actual.stackSize);
    Assert.assertEquals(0, actual.arrayDim);
    Assert.assertFalse(actual.falseBoolean);
    Assert.assertEquals(4, actual.type);
    Assert.assertEquals(2, actual.typeFamily);
    Assert.assertEquals("I", actual.value);
  }

  // Test written by Diffblue Cover.
  @Test
  public void getCommonSupertypeInputNotNullNotNullOutputNotNull19() {

    // Arrange
    final VarType type1 = new VarType(7);
    final VarType type2 = new VarType(16);

    // Act
    final VarType actual = VarType.getCommonSupertype(type1, type2);

    // Assert result
    Assert.assertNotNull(actual);
    Assert.assertEquals(1, actual.stackSize);
    Assert.assertEquals(0, actual.arrayDim);
    Assert.assertFalse(actual.falseBoolean);
    Assert.assertEquals(16, actual.type);
    Assert.assertEquals(2, actual.typeFamily);
    Assert.assertEquals("Y", actual.value);
  }

  // Test written by Diffblue Cover.
  @Test
  public void getCommonSupertypeInputNotNullNotNullOutputNotNull20() {

    // Arrange
    final VarType type1 = new VarType(7);
    final VarType type2 = new VarType(7);

    // Act
    final VarType actual = VarType.getCommonSupertype(type1, type2);

    // Assert result
    Assert.assertNotNull(actual);
    Assert.assertEquals(1, actual.stackSize);
    Assert.assertEquals(0, actual.arrayDim);
    Assert.assertFalse(actual.falseBoolean);
    Assert.assertEquals(7, actual.type);
    Assert.assertEquals(1, actual.typeFamily);
    Assert.assertEquals("Z", actual.value);
  }

  // Test written by Diffblue Cover.
  @Test
  public void getCommonSupertypeInputNotNullNotNullOutputNull00075d64b959c7adb48() {

    // Arrange
    final VarType type1 = new VarType(12);
    final VarType type2 = new VarType(10);

    // Act and Assert result
    Assert.assertNull(VarType.getCommonSupertype(type1, type2));
  }

  // Test written by Diffblue Cover.
  @Test
  public void getCommonSupertypeInputNotNullNotNullOutputNull2() {

    // Arrange
    final VarType type1 = new VarType(10);
    final VarType type2 = new VarType(6);

    // Act and Assert result
    Assert.assertNull(VarType.getCommonSupertype(type1, type2));
  }

  // Test written by Diffblue Cover.
  @Test
  public void getCommonSupertypeInputNotNullNotNullOutputNull3() {

    // Arrange
    final VarType type1 = new VarType(14);
    final VarType type2 = new VarType(6);

    // Act and Assert result
    Assert.assertNull(VarType.getCommonSupertype(type1, type2));
  }

  // Test written by Diffblue Cover.
  @Test
  public void getCommonSupertypeInputNotNullNotNullOutputNull4() {

    // Arrange
    final VarType type1 = new VarType(7);
    final VarType type2 = new VarType(3);

    // Act and Assert result
    Assert.assertNull(VarType.getCommonSupertype(type1, type2));
  }

  // Test written by Diffblue Cover.
  @Test
  public void getCommonSupertypeInputNotNullNotNullOutputNull5() {

    // Arrange
    final VarType type1 = new VarType(4);
    final VarType type2 = new VarType(3);

    // Act and Assert result
    Assert.assertNull(VarType.getCommonSupertype(type1, type2));
  }

  // Test written by Diffblue Cover.
  @Test
  public void getCommonSupertypeInputNotNullNotNullOutputNull6() {

    // Arrange
    final VarType type1 = new VarType(2);
    final VarType type2 = new VarType(9);

    // Act and Assert result
    Assert.assertNull(VarType.getCommonSupertype(type1, type2));
  }

  // Test written by Diffblue Cover.
  @Test
  public void getMinTypeInFamilyInputPositiveOutputIllegalArgumentException() {

    // Act
    thrown.expect(IllegalArgumentException.class);
    VarType.getMinTypeInFamily(65_541);

    // The method is not expected to return due to exception thrown
  }

  // Test written by Diffblue Cover.
  @Test
  public void getMinTypeInFamilyInputPositiveOutputNotNull() {

    // Act
    final VarType actual = VarType.getMinTypeInFamily(5);

    // Assert result
    Assert.assertNotNull(actual);
    Assert.assertEquals(2, actual.stackSize);
    Assert.assertEquals(0, actual.arrayDim);
    Assert.assertFalse(actual.falseBoolean);
    Assert.assertEquals(2, actual.type);
    Assert.assertEquals(5, actual.typeFamily);
    Assert.assertEquals("D", actual.value);
  }

  // Test written by Diffblue Cover.
  @Test
  public void getMinTypeInFamilyInputPositiveOutputNotNull2() {

    // Act
    final VarType actual = VarType.getMinTypeInFamily(4);

    // Assert result
    Assert.assertNotNull(actual);
    Assert.assertEquals(2, actual.stackSize);
    Assert.assertEquals(0, actual.arrayDim);
    Assert.assertFalse(actual.falseBoolean);
    Assert.assertEquals(5, actual.type);
    Assert.assertEquals(4, actual.typeFamily);
    Assert.assertEquals("J", actual.value);
  }

  // Test written by Diffblue Cover.
  @Test
  public void getMinTypeInFamilyInputPositiveOutputNotNull3() {

    // Act
    final VarType actual = VarType.getMinTypeInFamily(6);

    // Assert result
    Assert.assertNotNull(actual);
    Assert.assertEquals(1, actual.stackSize);
    Assert.assertEquals(0, actual.arrayDim);
    Assert.assertFalse(actual.falseBoolean);
    Assert.assertEquals(13, actual.type);
    Assert.assertEquals(6, actual.typeFamily);
    Assert.assertNull(actual.value);
  }

  // Test written by Diffblue Cover.
  @Test
  public void getMinTypeInFamilyInputPositiveOutputNotNull4() {

    // Act
    final VarType actual = VarType.getMinTypeInFamily(2);

    // Assert result
    Assert.assertNotNull(actual);
    Assert.assertEquals(1, actual.stackSize);
    Assert.assertEquals(0, actual.arrayDim);
    Assert.assertFalse(actual.falseBoolean);
    Assert.assertEquals(15, actual.type);
    Assert.assertEquals(2, actual.typeFamily);
    Assert.assertEquals("X", actual.value);
  }

  // Test written by Diffblue Cover.
  @Test
  public void getMinTypeInFamilyInputPositiveOutputNotNull5() {

    // Act
    final VarType actual = VarType.getMinTypeInFamily(3);

    // Assert result
    Assert.assertNotNull(actual);
    Assert.assertEquals(1, actual.stackSize);
    Assert.assertEquals(0, actual.arrayDim);
    Assert.assertFalse(actual.falseBoolean);
    Assert.assertEquals(3, actual.type);
    Assert.assertEquals(3, actual.typeFamily);
    Assert.assertEquals("F", actual.value);
  }

  // Test written by Diffblue Cover.
  @Test
  public void getMinTypeInFamilyInputPositiveOutputNotNull6() {

    // Act
    final VarType actual = VarType.getMinTypeInFamily(1);

    // Assert result
    Assert.assertNotNull(actual);
    Assert.assertEquals(1, actual.stackSize);
    Assert.assertEquals(0, actual.arrayDim);
    Assert.assertFalse(actual.falseBoolean);
    Assert.assertEquals(7, actual.type);
    Assert.assertEquals(1, actual.typeFamily);
    Assert.assertEquals("Z", actual.value);
  }

  // Test written by Diffblue Cover.
  @Test
  public void getMinTypeInFamilyInputZeroOutputNotNull() {

    // Act
    final VarType actual = VarType.getMinTypeInFamily(0);

    // Assert result
    Assert.assertNotNull(actual);
    Assert.assertEquals(1, actual.stackSize);
    Assert.assertEquals(0, actual.arrayDim);
    Assert.assertFalse(actual.falseBoolean);
    Assert.assertEquals(17, actual.type);
    Assert.assertEquals(0, actual.typeFamily);
    Assert.assertEquals("U", actual.value);
  }

  // Test written by Diffblue Cover.
  @Test
  public void getTypeInputAOutputPositive() {

    // Act and Assert result
    Assert.assertEquals(9, VarType.getType('A'));
  }

  // Test written by Diffblue Cover.
  @Test
  public void getTypeInputBOutputZero() {

    // Act and Assert result
    Assert.assertEquals(0, VarType.getType('B'));
  }

  // Test written by Diffblue Cover.
  @Test
  public void getTypeInputCOutputPositive() {

    // Act and Assert result
    Assert.assertEquals(1, VarType.getType('C'));
  }

  // Test written by Diffblue Cover.
  @Test
  public void getTypeInputDOutputPositive() {

    // Act and Assert result
    Assert.assertEquals(2, VarType.getType('D'));
  }

  // Test written by Diffblue Cover.
  @Test
  public void getTypeInputFOutputPositive() {

    // Act and Assert result
    Assert.assertEquals(3, VarType.getType('F'));
  }

  // Test written by Diffblue Cover.
  @Test
  public void getTypeInputGOutputPositive() {

    // Act and Assert result
    Assert.assertEquals(12, VarType.getType('G'));
  }

  // Test written by Diffblue Cover.
  @Test
  public void getTypeInputIOutputPositive() {

    // Act and Assert result
    Assert.assertEquals(4, VarType.getType('I'));
  }

  // Test written by Diffblue Cover.
  @Test
  public void getTypeInputJOutputPositive() {

    // Act and Assert result
    Assert.assertEquals(5, VarType.getType('J'));
  }

  // Test written by Diffblue Cover.
  @Test
  public void getTypeInputNOutputPositive() {

    // Act and Assert result
    Assert.assertEquals(14, VarType.getType('N'));
  }

  // Test written by Diffblue Cover.
  @Test
  public void getTypeInputPOutputIllegalArgumentException() {

    // Act
    thrown.expect(IllegalArgumentException.class);
    VarType.getType('P');

    // The method is not expected to return due to exception thrown
  }

  // Test written by Diffblue Cover.
  @Test
  public void getTypeInputSOutputPositive() {

    // Act and Assert result
    Assert.assertEquals(6, VarType.getType('S'));
  }

  // Test written by Diffblue Cover.
  @Test
  public void getTypeInputUOutputPositive() {

    // Act and Assert result
    Assert.assertEquals(17, VarType.getType('U'));
  }

  // Test written by Diffblue Cover.
  @Test
  public void getTypeInputVOutputPositive() {

    // Act and Assert result
    Assert.assertEquals(10, VarType.getType('V'));
  }

  // Test written by Diffblue Cover.
  @Test
  public void getTypeInputXOutputPositive() {

    // Act and Assert result
    Assert.assertEquals(15, VarType.getType('X'));
  }

  // Test written by Diffblue Cover.
  @Test
  public void getTypeInputYOutputPositive() {

    // Act and Assert result
    Assert.assertEquals(16, VarType.getType('Y'));
  }

  // Test written by Diffblue Cover.
  @Test
  public void getTypeInputZOutputPositive() {

    // Act and Assert result
    Assert.assertEquals(7, VarType.getType('Z'));
  }

  // Test written by Diffblue Cover.
  @Test
  public void isFalseBooleanOutputFalse() {

    // Arrange
    final VarType varType = new VarType(12);

    // Act and Assert result
    Assert.assertFalse(varType.isFalseBoolean());
  }

  // Test written by Diffblue Cover.
  @Test
  public void isFalseBooleanOutputFalse2() {

    // Arrange
    final VarType varType = new VarType(9);

    // Act and Assert result
    Assert.assertFalse(varType.isFalseBoolean());
  }

  // Test written by Diffblue Cover.
  @Test
  public void isFalseBooleanOutputFalse3() {

    // Arrange
    final VarType varType = new VarType(13);

    // Act and Assert result
    Assert.assertFalse(varType.isFalseBoolean());
  }

  // Test written by Diffblue Cover.
  @Test
  public void isFalseBooleanOutputFalse4() {

    // Arrange
    final VarType varType = new VarType(14);

    // Act and Assert result
    Assert.assertFalse(varType.isFalseBoolean());
  }

  // Test written by Diffblue Cover.
  @Test
  public void isStrictSupersetInputNotNullOutputFalse() {

    // Arrange
    final VarType varType = new VarType(14);
    final VarType val = new VarType(5);

    // Act and Assert result
    Assert.assertFalse(varType.isStrictSuperset(val));
  }

  // Test written by Diffblue Cover.
  @Test
  public void isStrictSupersetInputNotNullOutputFalse2() {

    // Arrange
    final VarType varType = new VarType(12);
    final VarType val = new VarType(5);

    // Act and Assert result
    Assert.assertFalse(varType.isStrictSuperset(val));
  }

  // Test written by Diffblue Cover.
  @Test
  public void isStrictSupersetInputNotNullOutputFalse3() {

    // Arrange
    final VarType varType = new VarType(12);
    final VarType val = new VarType(13);

    // Act and Assert result
    Assert.assertFalse(varType.isStrictSuperset(val));
  }

  // Test written by Diffblue Cover.
  @Test
  public void isStrictSupersetInputNotNullOutputFalse4() {

    // Arrange
    final VarType varType = new VarType(9);
    final VarType val = new VarType(13);

    // Act and Assert result
    Assert.assertFalse(varType.isStrictSuperset(val));
  }

  // Test written by Diffblue Cover.
  @Test
  public void isStrictSupersetInputNotNullOutputFalse5() {

    // Arrange
    final VarType varType = new VarType(15);
    final VarType val = new VarType(13);

    // Act and Assert result
    Assert.assertFalse(varType.isStrictSuperset(val));
  }

  // Test written by Diffblue Cover.
  @Test
  public void isStrictSupersetInputNotNullOutputFalse6() {

    // Arrange
    final VarType varType = new VarType(6);
    final VarType val = new VarType(5);

    // Act and Assert result
    Assert.assertFalse(varType.isStrictSuperset(val));
  }

  // Test written by Diffblue Cover.
  @Test
  public void isStrictSupersetInputNotNullOutputFalse7() {

    // Arrange
    final VarType varType = new VarType(4);
    final VarType val = new VarType(5);

    // Act and Assert result
    Assert.assertFalse(varType.isStrictSuperset(val));
  }

  // Test written by Diffblue Cover.
  @Test
  public void isStrictSupersetInputNotNullOutputFalse11() {

    // Arrange
    final VarType varType = new VarType(17);
    final VarType val = new VarType(17);

    // Act and Assert result
    Assert.assertFalse(varType.isStrictSuperset(val));
  }

  // Test written by Diffblue Cover.
  @Test
  public void isStrictSupersetInputNotNullOutputTrue() {

    // Arrange
    final VarType varType = new VarType(4);
    final VarType val = new VarType(7);

    // Act and Assert result
    Assert.assertTrue(varType.isStrictSuperset(val));
  }

  // Test written by Diffblue Cover.
  @Test
  public void isStrictSupersetInputNotNullOutputTrue2() {

    // Arrange
    final VarType varType = new VarType(4);
    final VarType val = new VarType(16);

    // Act and Assert result
    Assert.assertTrue(varType.isStrictSuperset(val));
  }

  // Test written by Diffblue Cover.
  @Test
  public void isStrictSupersetInputNotNullOutputTrue3() {

    // Arrange
    final VarType varType = new VarType(4);
    final VarType val = new VarType(15);

    // Act and Assert result
    Assert.assertTrue(varType.isStrictSuperset(val));
  }

  // Test written by Diffblue Cover.
  @Test
  public void isStrictSupersetInputNotNullOutputTrue4() {

    // Arrange
    final VarType varType = new VarType(4);
    final VarType val = new VarType(6);

    // Act and Assert result
    Assert.assertTrue(varType.isStrictSuperset(val));
  }

  // Test written by Diffblue Cover.
  @Test
  public void isStrictSupersetInputNotNullOutputTrue5() {

    // Arrange
    final VarType varType = new VarType(8);
    final VarType val = new VarType(13);

    // Act and Assert result
    Assert.assertTrue(varType.isStrictSuperset(val));
  }

  // Test written by Diffblue Cover.
  @Test
  public void isStrictSupersetInputNotNullOutputTrue7() {

    // Arrange
    final VarType varType = new VarType(9);
    final VarType val = new VarType(17);

    // Act and Assert result
    Assert.assertTrue(varType.isStrictSuperset(val));
  }

  // Test written by Diffblue Cover.
  @Test
  public void isSupersetInputNotNullOutputFalse() {

    // Arrange
    final VarType varType = new VarType(14);
    final VarType val = new VarType(2);

    // Act and Assert result
    Assert.assertFalse(varType.isSuperset(val));
  }

  // Test written by Diffblue Cover.
  @Test
  public void isSupersetInputNotNullOutputFalse2() {

    // Arrange
    final VarType varType = new VarType(4);
    final VarType val = new VarType(2);

    // Act and Assert result
    Assert.assertFalse(varType.isSuperset(val));
  }

  // Test written by Diffblue Cover.
  @Test
  public void isSupersetInputNotNullOutputFalse4() {

    // Arrange
    final VarType varType = new VarType(12);
    final VarType val = new VarType(2);

    // Act and Assert result
    Assert.assertFalse(varType.isSuperset(val));
  }

  // Test written by Diffblue Cover.
  @Test
  public void isSupersetInputNotNullOutputFalse5() {

    // Arrange
    final VarType varType = new VarType(12);
    final VarType val = new VarType(9);

    // Act and Assert result
    Assert.assertFalse(varType.isSuperset(val));
  }

  // Test written by Diffblue Cover.
  @Test
  public void isSupersetInputNotNullOutputFalse6() {

    // Arrange
    final VarType varType = new VarType(13);
    final VarType val = new VarType(9);

    // Act and Assert result
    Assert.assertFalse(varType.isSuperset(val));
  }

  // Test written by Diffblue Cover.
  @Test
  public void isSupersetInputNotNullOutputTrue2() {

    // Arrange
    final VarType varType = new VarType(13);
    final VarType val = new VarType(17);

    // Act and Assert result
    Assert.assertTrue(varType.isSuperset(val));
  }

  // Test written by Diffblue Cover.
  @Test
  public void isSupersetInputNotNullOutputTrue3() {

    // Arrange
    final VarType varType = new VarType(8);
    final VarType val = new VarType(13);

    // Act and Assert result
    Assert.assertTrue(varType.isSuperset(val));
  }

  // Test written by Diffblue Cover.
  @Test
  public void isSupersetInputNotNullOutputTrue4() {

    // Arrange
    final VarType varType = new VarType(16);
    final VarType val = new VarType(7);

    // Act and Assert result
    Assert.assertTrue(varType.isSuperset(val));
  }

  // Test written by Diffblue Cover.
  @Test
  public void isSupersetInputNotNullOutputTrue5() {

    // Arrange
    final VarType varType = new VarType(16);
    final VarType val = new VarType(15);

    // Act and Assert result
    Assert.assertTrue(varType.isSuperset(val));
  }

  // Test written by Diffblue Cover.
  @Test
  public void isSupersetInputNotNullOutputTrue6() {

    // Arrange
    final VarType varType = new VarType(6);
    final VarType val = new VarType(16);

    // Act and Assert result
    Assert.assertTrue(varType.isSuperset(val));
  }

  // Test written by Diffblue Cover.
  @Test
  public void isSupersetInputNotNullOutputTrue8() {

    // Arrange
    final VarType varType = new VarType(4);
    final VarType val = new VarType(6);

    // Act and Assert result
    Assert.assertTrue(varType.isSuperset(val));
  }

  // Test written by Diffblue Cover.
  @Test
  public void resizeArrayDimInputPositiveOutputNotNull() {

    // Arrange
    final VarType varType = new VarType(13);

    // Act
    final VarType actual = varType.resizeArrayDim(2);

    // Assert result
    Assert.assertNotNull(actual);
    Assert.assertEquals(1, actual.stackSize);
    Assert.assertEquals(2, actual.arrayDim);
    Assert.assertFalse(actual.falseBoolean);
    Assert.assertEquals(13, actual.type);
    Assert.assertEquals(6, actual.typeFamily);
    Assert.assertNull(actual.value);
  }

  // Test written by Diffblue Cover.
  @Test
  public void resizeArrayDimInputPositiveOutputNotNull2() {

    // Arrange
    final VarType varType = new VarType(9);

    // Act
    final VarType actual = varType.resizeArrayDim(2);

    // Assert result
    Assert.assertNotNull(actual);
    Assert.assertEquals(1, actual.stackSize);
    Assert.assertEquals(2, actual.arrayDim);
    Assert.assertFalse(actual.falseBoolean);
    Assert.assertEquals(9, actual.type);
    Assert.assertEquals(0, actual.typeFamily);
    Assert.assertEquals("A", actual.value);
  }

  // Test written by Diffblue Cover.
  @Test
  public void resizeArrayDimInputPositiveOutputNotNull3() {

    // Arrange
    final VarType varType = new VarType(14);

    // Act
    final VarType actual = varType.resizeArrayDim(2);

    // Assert result
    Assert.assertNotNull(actual);
    Assert.assertEquals(1, actual.stackSize);
    Assert.assertEquals(2, actual.arrayDim);
    Assert.assertFalse(actual.falseBoolean);
    Assert.assertEquals(14, actual.type);
    Assert.assertEquals(0, actual.typeFamily);
    Assert.assertEquals("N", actual.value);
  }

  // Test written by Diffblue Cover.
  @Test
  public void resizeArrayDimInputPositiveOutputNotNull4() {

    // Arrange
    final VarType varType = new VarType(12);

    // Act
    final VarType actual = varType.resizeArrayDim(2);

    // Assert result
    Assert.assertNotNull(actual);
    Assert.assertEquals(0, actual.stackSize);
    Assert.assertEquals(2, actual.arrayDim);
    Assert.assertFalse(actual.falseBoolean);
    Assert.assertEquals(12, actual.type);
    Assert.assertEquals(0, actual.typeFamily);
    Assert.assertEquals("G", actual.value);
  }

  // Test written by Diffblue Cover.
  @Test
  public void toStringOutputNotNull() {

    // Arrange
    final VarType varType = new VarType(3);

    // Act and Assert result
    Assert.assertEquals("F", varType.toString());
  }

  // Test written by Diffblue Cover.
  @Test
  public void toStringOutputNotNull2() {

    // Arrange
    final VarType varType = new VarType(14);

    // Act and Assert result
    Assert.assertEquals("N", varType.toString());
  }

  // Test written by Diffblue Cover.
  @Test
  public void toStringOutputNotNull3() {

    // Arrange
    final VarType varType = new VarType(9);

    // Act and Assert result
    Assert.assertEquals("A", varType.toString());
  }

  // Test written by Diffblue Cover.
  @Test
  public void toStringOutputNotNull4() {

    // Arrange
    final VarType varType = new VarType(12);

    // Act and Assert result
    Assert.assertEquals("G", varType.toString());
  }
}
