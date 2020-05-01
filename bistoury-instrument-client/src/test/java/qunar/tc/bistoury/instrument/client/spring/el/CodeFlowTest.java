package qunar.tc.bistoury.instrument.client.spring.el;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.Timeout;
import qunar.tc.bistoury.instrument.client.spring.el.CodeFlow;

import java.lang.reflect.Array;

public class CodeFlowTest {

  @Rule public final ExpectedException thrown = ExpectedException.none();

  @Rule public final Timeout globalTimeout = new Timeout(10000);

  // Test written by Diffblue Cover.
  @Test
  public void areBoxingCompatibleInputNotNullNotNullOutputFalse() {

    // Act and Assert result
    Assert.assertFalse(CodeFlow.areBoxingCompatible("foo", "\'"));
  }

  // Test written by Diffblue Cover.
  @Test
  public void areBoxingCompatibleInputNotNullNotNullOutputFalse2() {

    // Act and Assert result
    Assert.assertFalse(CodeFlow.areBoxingCompatible("\'", "/"));
  }

  // Test written by Diffblue Cover.
  @Test
  public void areBoxingCompatibleInputNotNullNotNullOutputFalse3() {

    // Act and Assert result
    Assert.assertFalse(CodeFlow.areBoxingCompatible("J", ""));
  }

  // Test written by Diffblue Cover.
  @Test
  public void areBoxingCompatibleInputNotNullNotNullOutputFalse4() {

    // Act and Assert result
    Assert.assertFalse(CodeFlow.areBoxingCompatible("", "F"));
  }

  // Test written by Diffblue Cover.
  @Test
  public void areBoxingCompatibleInputNotNullNotNullOutputFalse5() {

    // Act and Assert result
    Assert.assertFalse(CodeFlow.areBoxingCompatible("", "D"));
  }

  // Test written by Diffblue Cover.
  @Test
  public void areBoxingCompatibleInputNotNullNotNullOutputFalse6() {

    // Act and Assert result
    Assert.assertFalse(CodeFlow.areBoxingCompatible("", "J"));
  }

  // Test written by Diffblue Cover.
  @Test
  public void areBoxingCompatibleInputNotNullNotNullOutputFalse7() {

    // Act and Assert result
    Assert.assertFalse(CodeFlow.areBoxingCompatible("I", ""));
  }

  // Test written by Diffblue Cover.
  @Test
  public void areBoxingCompatibleInputNotNullNotNullOutputFalse8() {

    // Act and Assert result
    Assert.assertFalse(CodeFlow.areBoxingCompatible("", "I"));
  }

  // Test written by Diffblue Cover.
  @Test
  public void areBoxingCompatibleInputNotNullNotNullOutputFalse9() {

    // Act and Assert result
    Assert.assertFalse(CodeFlow.areBoxingCompatible("F", ""));
  }

  // Test written by Diffblue Cover.
  @Test
  public void areBoxingCompatibleInputNotNullNotNullOutputFalse10() {

    // Act and Assert result
    Assert.assertFalse(CodeFlow.areBoxingCompatible("", "Z"));
  }

  // Test written by Diffblue Cover.
  @Test
  public void areBoxingCompatibleInputNotNullNotNullOutputFalse11() {

    // Act and Assert result
    Assert.assertFalse(CodeFlow.areBoxingCompatible("Z", ""));
  }

  // Test written by Diffblue Cover.
  @Test
  public void areBoxingCompatibleInputNotNullNotNullOutputFalse12() {

    // Act and Assert result
    Assert.assertFalse(CodeFlow.areBoxingCompatible("D", ""));
  }

  // Test written by Diffblue Cover.
  @Test
  public void areBoxingCompatibleInputNotNullNotNullOutputTrue() {

    // Act and Assert result
    Assert.assertTrue(CodeFlow.areBoxingCompatible(",", ","));
  }

  // Test written by Diffblue Cover.
  @Test
  public void arrayCodeForInputNotNullOutputIllegalArgumentException() {

    // Act
    thrown.expect(IllegalArgumentException.class);
    CodeFlow.arrayCodeFor("foo");

    // The method is not expected to return due to exception thrown
  }

  // Test written by Diffblue Cover.
  @Test
  public void arrayCodeForInputNotNullOutputPositive() {

    // Act and Assert result
    Assert.assertEquals(6, CodeFlow.arrayCodeFor("F"));
  }

  // Test written by Diffblue Cover.
  @Test
  public void arrayCodeForInputNotNullOutputPositive2() {

    // Act and Assert result
    Assert.assertEquals(9, CodeFlow.arrayCodeFor("S"));
  }

  // Test written by Diffblue Cover.
  @Test
  public void arrayCodeForInputNotNullOutputPositive3() {

    // Act and Assert result
    Assert.assertEquals(4, CodeFlow.arrayCodeFor("Z"));
  }

  // Test written by Diffblue Cover.
  @Test
  public void arrayCodeForInputNotNullOutputPositive4() {

    // Act and Assert result
    Assert.assertEquals(8, CodeFlow.arrayCodeFor("B"));
  }

  // Test written by Diffblue Cover.
  @Test
  public void arrayCodeForInputNotNullOutputPositive5() {

    // Act and Assert result
    Assert.assertEquals(7, CodeFlow.arrayCodeFor("D"));
  }

  // Test written by Diffblue Cover.
  @Test
  public void arrayCodeForInputNotNullOutputPositive6() {

    // Act and Assert result
    Assert.assertEquals(5, CodeFlow.arrayCodeFor("C"));
  }

  // Test written by Diffblue Cover.
  @Test
  public void arrayCodeForInputNotNullOutputPositive7() {

    // Act and Assert result
    Assert.assertEquals(10, CodeFlow.arrayCodeFor("I"));
  }

  // Test written by Diffblue Cover.
  @Test
  public void arrayCodeForInputNotNullOutputPositive8() {

    // Act and Assert result
    Assert.assertEquals(11, CodeFlow.arrayCodeFor("J"));
  }

  // Test written by Diffblue Cover.
  @Test
  public void
  insertAnyNecessaryTypeConversionBytecodesInputNullENotNullOutputIllegalStateException() {

    // Act
    thrown.expect(IllegalStateException.class);
    CodeFlow.insertAnyNecessaryTypeConversionBytecodes(null, 'E', "D");

    // The method is not expected to return due to exception thrown
  }

  // Test written by Diffblue Cover.
  @Test
  public void insertArrayStoreInputNullNotNullOutputIllegalArgumentException() {

    // Act
    thrown.expect(IllegalArgumentException.class);
    CodeFlow.insertArrayStore(null, "\u0000");

    // The method is not expected to return due to exception thrown
  }

  // Test written by Diffblue Cover.
  @Test
  public void insertBoxIfNecessaryInputNullNotNullOutputIllegalArgumentException() {

    // Act
    thrown.expect(IllegalArgumentException.class);
    CodeFlow.insertBoxIfNecessary(null, "\u0000");

    // The method is not expected to return due to exception thrown
  }

  // Test written by Diffblue Cover.
  @Test
  public void
  insertNumericUnboxOrPrimitiveTypeCoercionInputNullNotNullNotNullOutputIllegalStateException() {

    // Act
    thrown.expect(IllegalStateException.class);
    CodeFlow.insertNumericUnboxOrPrimitiveTypeCoercion(null, "F", '\u00c6');

    // The method is not expected to return due to exception thrown
  }

  // Test written by Diffblue Cover.
  @Test
  public void isBooleanCompatibleInputNotNullOutputFalse() {

    // Act and Assert result
    Assert.assertFalse(CodeFlow.isBooleanCompatible(","));
  }

  // Test written by Diffblue Cover.
  @Test
  public void isBooleanCompatibleInputNotNullOutputTrue() {

    // Act and Assert result
    Assert.assertTrue(CodeFlow.isBooleanCompatible("Z"));
  }

  // Test written by Diffblue Cover.
  @Test
  public void isIntegerForNumericOpInputNegativeOutputTrue() {

    // Act and Assert result
    Assert.assertTrue(CodeFlow.isIntegerForNumericOp(-999_998));
  }

  // Test written by Diffblue Cover.
  @Test
  public void isIntegerForNumericOpInputNegativeOutputTrue2() {

    // Act and Assert result
    Assert.assertTrue(CodeFlow.isIntegerForNumericOp((byte)-63));
  }

  // Test written by Diffblue Cover.
  @Test
  public void isPrimitiveArrayInputNotNullOutputTrue() {

    // Act and Assert result
    Assert.assertTrue(CodeFlow.isPrimitiveArray("\'"));
  }

  // Test written by Diffblue Cover.
  @Test
  public void isPrimitiveArrayInputNotNullOutputTrue2() {

    // Act and Assert result
    Assert.assertTrue(CodeFlow.isPrimitiveArray(""));
  }

  // Test written by Diffblue Cover.
  @Test
  public void isPrimitiveArrayInputNotNullOutputTrue3() {

    // Act and Assert result
    Assert.assertTrue(CodeFlow.isPrimitiveArray(
        "[\u0000\u0000?????????????????????????????????????????????????????????????"));
  }

  // Test written by Diffblue Cover.
  @Test
  public void isPrimitiveInputNotNullOutputFalse() {

    // Act and Assert result
    Assert.assertFalse(CodeFlow.isPrimitive("a\'b\'c"));
  }

  // Test written by Diffblue Cover.
  @Test
  public void isPrimitiveInputNotNullOutputTrue() {

    // Act and Assert result
    Assert.assertTrue(CodeFlow.isPrimitive("/"));
  }

  // Test written by Diffblue Cover.
  @Test
  public void isPrimitiveOrUnboxableSupportedNumberInputNotNullOutputFalse() {

    // Act and Assert result
    Assert.assertFalse(CodeFlow.isPrimitiveOrUnboxableSupportedNumber(","));
  }

  // Test written by Diffblue Cover.
  @Test
  public void isPrimitiveOrUnboxableSupportedNumberInputNotNullOutputFalse2() {

    // Act and Assert result
    Assert.assertFalse(CodeFlow.isPrimitiveOrUnboxableSupportedNumber("a/b/c"));
  }

  // Test written by Diffblue Cover.
  @Test
  public void isPrimitiveOrUnboxableSupportedNumberInputNotNullOutputFalse3() {

    // Act and Assert result
    Assert.assertFalse(CodeFlow.isPrimitiveOrUnboxableSupportedNumber("Ljava/lang/"));
  }

  // Test written by Diffblue Cover.
  @Test
  public void isPrimitiveOrUnboxableSupportedNumberInputNotNullOutputTrue() {

    // Act and Assert result
    Assert.assertTrue(CodeFlow.isPrimitiveOrUnboxableSupportedNumber("Ljava/lang/Double"));
  }

  // Test written by Diffblue Cover.
  @Test
  public void isPrimitiveOrUnboxableSupportedNumberInputNullOutputFalse() {

    // Act and Assert result
    Assert.assertFalse(CodeFlow.isPrimitiveOrUnboxableSupportedNumber(null));
  }

  // Test written by Diffblue Cover.
  @Test
  public void isPrimitiveOrUnboxableSupportedNumberOrBooleanInputNotNullOutputFalse() {

    // Act and Assert result
    Assert.assertFalse(CodeFlow.isPrimitiveOrUnboxableSupportedNumberOrBoolean("a/b/c"));
  }

  // Test written by Diffblue Cover.
  @Test
  public void isPrimitiveOrUnboxableSupportedNumberOrBooleanInputNotNullOutputTrue() {

    // Act and Assert result
    Assert.assertTrue(
        CodeFlow.isPrimitiveOrUnboxableSupportedNumberOrBoolean("Ljava/lang/Boolean"));
  }

  // Test written by Diffblue Cover.
  @Test
  public void isPrimitiveOrUnboxableSupportedNumberOrBooleanInputNotNullOutputTrue2() {

    // Act and Assert result
    Assert.assertTrue(
        CodeFlow.isPrimitiveOrUnboxableSupportedNumberOrBoolean("Ljava/lang/Integer"));
  }

  // Test written by Diffblue Cover.
  @Test
  public void isPrimitiveOrUnboxableSupportedNumberOrBooleanInputNotNullOutputTrue3() {

    // Act and Assert result
    Assert.assertTrue(CodeFlow.isPrimitiveOrUnboxableSupportedNumberOrBoolean("Z"));
  }

  // Test written by Diffblue Cover.
  @Test
  public void isPrimitiveOrUnboxableSupportedNumberOrBooleanInputNullOutputFalse() {

    // Act and Assert result
    Assert.assertFalse(CodeFlow.isPrimitiveOrUnboxableSupportedNumberOrBoolean(null));
  }

  // Test written by Diffblue Cover.
  @Test
  public void isReferenceTypeArrayInputNotNullOutputFalse() {

    // Act and Assert result
    Assert.assertFalse(CodeFlow.isReferenceTypeArray("\'"));
  }

  // Test written by Diffblue Cover.
  @Test
  public void isReferenceTypeArrayInputNotNullOutputFalse2() {

    // Act and Assert result
    Assert.assertFalse(CodeFlow.isReferenceTypeArray(""));
  }

  // Test written by Diffblue Cover.
  @Test
  public void isReferenceTypeArrayInputNotNullOutputFalse3() {

    // Act and Assert result
    Assert.assertFalse(CodeFlow.isReferenceTypeArray(
        "[\u0000\u0000?????????????????????????????????????????????????????????????"));
  }

  // Test written by Diffblue Cover.
  @Test
  public void isReferenceTypeArrayInputNotNullOutputTrue() {

    // Act and Assert result
    Assert.assertTrue(CodeFlow.isReferenceTypeArray(
        "[L\u0000?????????????????????????????????????????????????????????????"));
  }

  // Test written by Diffblue Cover.
  @Test
  public void toDescriptorFromObjectInputNegativeOutputNotNull() {

    // Act and Assert result
    Assert.assertEquals("Ljava/lang/Integer", CodeFlow.toDescriptorFromObject(-999_998));
  }

  // Test written by Diffblue Cover.
  @Test
  public void toDescriptorFromObjectInputNullOutputNotNull() {

    // Act and Assert result
    Assert.assertEquals("Ljava/lang/Object", CodeFlow.toDescriptorFromObject(null));
  }

  // Test written by Diffblue Cover.

  @Test
  public void toDescriptorsInput0Output0() {

    // Arrange
    final Class[] types = {};

    // Act
    final String[] actual = CodeFlow.toDescriptors(types);

    // Assert result
    Assert.assertArrayEquals(new String[] {}, actual);
  }

  // Test written by Diffblue Cover.

  @Test
  public void toDescriptorsInput2OutputNullPointerException2() {

    // Arrange
    final Class[] types = {null, null};

    // Act
    thrown.expect(NullPointerException.class);
    CodeFlow.toDescriptors(types);

    // The method is not expected to return due to exception thrown
  }

  // Test written by Diffblue Cover.
  @Test
  public void toPrimitiveTargetDescInputNotNullOutputB() {

    // Act and Assert result
    Assert.assertEquals('B', CodeFlow.toPrimitiveTargetDesc("Ljava/lang/Byte"));
  }

  // Test written by Diffblue Cover.
  @Test
  public void toPrimitiveTargetDescInputNotNullOutputC() {

    // Act and Assert result
    Assert.assertEquals('C', CodeFlow.toPrimitiveTargetDesc("Ljava/lang/Character"));
  }

  // Test written by Diffblue Cover.
  @Test
  public void toPrimitiveTargetDescInputNotNullOutputD() {

    // Act and Assert result
    Assert.assertEquals('D', CodeFlow.toPrimitiveTargetDesc("Ljava/lang/Double"));
  }

  // Test written by Diffblue Cover.
  @Test
  public void toPrimitiveTargetDescInputNotNullOutputF() {

    // Act and Assert result
    Assert.assertEquals('F', CodeFlow.toPrimitiveTargetDesc("Ljava/lang/Float"));
  }

  // Test written by Diffblue Cover.
  @Test
  public void toPrimitiveTargetDescInputNotNullOutputI() {

    // Act and Assert result
    Assert.assertEquals('I', CodeFlow.toPrimitiveTargetDesc("Ljava/lang/Integer"));
  }

  // Test written by Diffblue Cover.
  @Test
  public void toPrimitiveTargetDescInputNotNullOutputIllegalStateException() {

    // Act
    thrown.expect(IllegalStateException.class);
    CodeFlow.toPrimitiveTargetDesc("foo");

    // The method is not expected to return due to exception thrown
  }

  // Test written by Diffblue Cover.
  @Test
  public void toPrimitiveTargetDescInputNotNullOutputJ() {

    // Act and Assert result
    Assert.assertEquals('J', CodeFlow.toPrimitiveTargetDesc("Ljava/lang/Long"));
  }

  // Test written by Diffblue Cover.
  @Test
  public void toPrimitiveTargetDescInputNotNullOutputNotNull() {

    // Act and Assert result
    Assert.assertEquals(',', CodeFlow.toPrimitiveTargetDesc(","));
  }

  // Test written by Diffblue Cover.
  @Test
  public void toPrimitiveTargetDescInputNotNullOutputS() {

    // Act and Assert result
    Assert.assertEquals('S', CodeFlow.toPrimitiveTargetDesc("Ljava/lang/Short"));
  }

  // Test written by Diffblue Cover.
  @Test
  public void toPrimitiveTargetDescInputNotNullOutputZ() {

    // Act and Assert result
    Assert.assertEquals('Z', CodeFlow.toPrimitiveTargetDesc("Ljava/lang/Boolean"));
  }
}
