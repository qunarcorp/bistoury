package qunar.tc.bistoury.instrument.client.spring.el;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.Timeout;
import qunar.tc.bistoury.instrument.client.spring.el.ObjectUtils;

import java.lang.reflect.Array;

public class ObjectUtilsTest {

  @Rule public final ExpectedException thrown = ExpectedException.none();

  @Rule public final Timeout globalTimeout = new Timeout(10000);

  // Test written by Diffblue Cover.
  @Test
  public void containsConstantInput0NotNullFalseOutputFalse() {

    // Arrange
    final Enum[] enumValues = {};

    // Act and Assert result
    Assert.assertFalse(ObjectUtils.containsConstant(enumValues, "/", false));
  }

  // Test written by Diffblue Cover.
  @Test
  public void containsConstantInput0NotNullOutputFalse() {

    // Arrange
    final Enum[] enumValues = {};

    // Act and Assert result
    Assert.assertFalse(ObjectUtils.containsConstant(enumValues, "/"));
  }

  // Test written by Diffblue Cover.

  @Test
  public void containsElementInput0NegativeOutputFalse() {

    // Arrange
    final Object[] array = {};
    final Object element = -999_998;

    // Act
    final boolean actual = ObjectUtils.containsElement(array, element);

    // Assert result
    Assert.assertFalse(actual);
  }

  // Test written by Diffblue Cover.

  @Test
  public void containsElementInput1ZeroOutputFalse() {

    // Arrange
    final Object[] array = {null};
    final Object element = 0;

    // Act
    final boolean actual = ObjectUtils.containsElement(array, element);

    // Assert result
    Assert.assertFalse(actual);
  }

  // Test written by Diffblue Cover.

  @Test
  public void containsElementInput2ZeroOutputFalse() {

    // Arrange
    final Object[] array = {null, 1};
    final Object element = 0;

    // Act
    final boolean actual = ObjectUtils.containsElement(array, element);

    // Assert result
    Assert.assertFalse(actual);
  }

  // Test written by Diffblue Cover.

  @Test
  public void containsElementInput2ZeroOutputTrue() {

    // Arrange
    final Object[] array = {-2_147_483_648, 0};
    final Object element = 0;

    // Act
    final boolean actual = ObjectUtils.containsElement(array, element);

    // Assert result
    Assert.assertTrue(actual);
  }

  // Test written by Diffblue Cover.
  @Test
  public void containsElementInputNullZeroOutputFalse() {

    // Act and Assert result
    Assert.assertFalse(ObjectUtils.containsElement(null, 0));
  }

  // Test written by Diffblue Cover.
  @Test
  public void getDisplayStringInputNegativeOutputNotNull() {

    // Act and Assert result
    Assert.assertEquals("-8256", ObjectUtils.getDisplayString(-8256));
  }

  // Test written by Diffblue Cover.
  @Test
  public void getDisplayStringInputNullOutputNotNull() {

    // Act and Assert result
    Assert.assertEquals("", ObjectUtils.getDisplayString(null));
  }

  // Test written by Diffblue Cover.
  @Test
  public void hashCodeInputPositiveOutputPositive3() {

    // Act and Assert result
    Assert.assertEquals(2, ObjectUtils.hashCode(2L));
  }

  // Test written by Diffblue Cover.
  @Test
  public void hashCodeInputTrueOutputPositive() {

    // Act and Assert result
    Assert.assertEquals(1231, ObjectUtils.hashCode(true));
  }

  // Test written by Diffblue Cover.
  @Test
  public void hashCodeInputZeroOutputZero() {

    // Act and Assert result
    Assert.assertEquals(0, ObjectUtils.hashCode(0.0f));
  }

  // Test written by Diffblue Cover.
  @Test
  public void identityToStringInputNullOutputNotNull() {

    // Act and Assert result
    Assert.assertEquals("", ObjectUtils.identityToString(null));
  }

  // Test written by Diffblue Cover.
  @Test
  public void isArrayInputNegativeOutputFalse() {

    // Act and Assert result
    Assert.assertFalse(ObjectUtils.isArray(-999_998));
  }

  // Test written by Diffblue Cover.
  @Test
  public void isCompatibleWithThrowsClauseInputNull1OutputNullPointerException() {

    // Arrange
    final Class[] declaredExceptions = {null};

    // Act
    thrown.expect(NullPointerException.class);
    ObjectUtils.isCompatibleWithThrowsClause(null, declaredExceptions);

    // The method is not expected to return due to exception thrown
  }

  // Test written by Diffblue Cover.

  @Test
  public void isEmptyInput0OutputTrue() {

    // Arrange
    final Object[] array = {};

    // Act
    final boolean actual = ObjectUtils.isEmpty(array);

    // Assert result
    Assert.assertTrue(actual);
  }

  // Test written by Diffblue Cover.
  @Test
  public void nullSafeClassNameInputNegativeOutputNotNull() {

    // Act and Assert result
    Assert.assertEquals("java.lang.Integer", ObjectUtils.nullSafeClassName(-999_998));
  }

  // Test written by Diffblue Cover.
  @Test
  public void nullSafeEqualsInputNullNullOutputTrue() {

    // Act and Assert result
    Assert.assertTrue(ObjectUtils.nullSafeEquals(null, null));
  }

  // Test written by Diffblue Cover.

  @Test
  public void nullSafeHashCodeInput0OutputPositive() {

    // Arrange
    final Object[] array = {};

    // Act
    final int actual = ObjectUtils.nullSafeHashCode(array);

    // Assert result
    Assert.assertEquals(7, actual);
  }

  // Test written by Diffblue Cover.
  @Test
  public void nullSafeHashCodeInput0OutputPositive2() {

    // Arrange
    final byte[] array = {};

    // Act and Assert result
    Assert.assertEquals(7, ObjectUtils.nullSafeHashCode(array));
  }

  // Test written by Diffblue Cover.
  @Test
  public void nullSafeHashCodeInput0OutputPositive3() {

    // Arrange
    final boolean[] array = {};

    // Act and Assert result
    Assert.assertEquals(7, ObjectUtils.nullSafeHashCode(array));
  }

  // Test written by Diffblue Cover.
  @Test
  public void nullSafeHashCodeInput0OutputPositive4() {

    // Arrange
    final double[] array = {};

    // Act and Assert result
    Assert.assertEquals(7, ObjectUtils.nullSafeHashCode(array));
  }

  // Test written by Diffblue Cover.
  @Test
  public void nullSafeHashCodeInput0OutputPositive5() {

    // Arrange
    final char[] array = {};

    // Act and Assert result
    Assert.assertEquals(7, ObjectUtils.nullSafeHashCode(array));
  }

  // Test written by Diffblue Cover.
  @Test
  public void nullSafeHashCodeInput0OutputPositive6() {

    // Arrange
    final int[] array = {};

    // Act and Assert result
    Assert.assertEquals(7, ObjectUtils.nullSafeHashCode(array));
  }

  // Test written by Diffblue Cover.
  @Test
  public void nullSafeHashCodeInput0OutputPositive7() {

    // Arrange
    final float[] array = {};

    // Act and Assert result
    Assert.assertEquals(7, ObjectUtils.nullSafeHashCode(array));
  }

  // Test written by Diffblue Cover.
  @Test
  public void nullSafeHashCodeInput0OutputPositive8() {

    // Arrange
    final long[] array = {};

    // Act and Assert result
    Assert.assertEquals(7, ObjectUtils.nullSafeHashCode(array));
  }

  // Test written by Diffblue Cover.
  @Test
  public void nullSafeHashCodeInput0OutputPositive9() {

    // Arrange
    final short[] array = {};

    // Act and Assert result
    Assert.assertEquals(7, ObjectUtils.nullSafeHashCode(array));
  }

  // Test written by Diffblue Cover.
  @Test
  public void nullSafeHashCodeInput1OutputPositive() {

    // Arrange
    final double[] array = {0.0};

    // Act and Assert result
    Assert.assertEquals(217, ObjectUtils.nullSafeHashCode(array));
  }

  // Test written by Diffblue Cover.

  @Test
  public void nullSafeHashCodeInput1OutputPositive2() {

    // Arrange
    final Object[] array = {null};

    // Act
    final int actual = ObjectUtils.nullSafeHashCode(array);

    // Assert result
    Assert.assertEquals(217, actual);
  }

  // Test written by Diffblue Cover.
  @Test
  public void nullSafeHashCodeInput1OutputPositive3() {

    // Arrange
    final char[] array = {'\u0744'};

    // Act and Assert result
    Assert.assertEquals(2077, ObjectUtils.nullSafeHashCode(array));
  }

  // Test written by Diffblue Cover.

  @Test
  public void nullSafeHashCodeInput1OutputPositive4() {

    // Arrange
    final Object[] array = {0};

    // Act
    final int actual = ObjectUtils.nullSafeHashCode(array);

    // Assert result
    Assert.assertEquals(217, actual);
  }

  // Test written by Diffblue Cover.
  @Test
  public void nullSafeHashCodeInput1OutputPositive6() {

    // Arrange
    final long[] array = {0L};

    // Act and Assert result
    Assert.assertEquals(217, ObjectUtils.nullSafeHashCode(array));
  }

  // Test written by Diffblue Cover.
  @Test
  public void nullSafeHashCodeInput1OutputZero() {

    // Arrange
    final short[] array = {(short)-217};

    // Act and Assert result
    Assert.assertEquals(0, ObjectUtils.nullSafeHashCode(array));
  }

  // Test written by Diffblue Cover.
  @Test
  public void nullSafeHashCodeInput2OutputPositive() {

    // Arrange
    final byte[] array = {(byte)-125, (byte)-116};

    // Act and Assert result
    Assert.assertEquals(2736, ObjectUtils.nullSafeHashCode(array));
  }

  // Test written by Diffblue Cover.
  @Test
  public void nullSafeHashCodeInput2OutputPositive2() {

    // Arrange
    final boolean[] array = {false, false};

    // Act and Assert result
    Assert.assertEquals(46_311, ObjectUtils.nullSafeHashCode(array));
  }

  // Test written by Diffblue Cover.
  @Test
  public void nullSafeHashCodeInput2OutputZero() {

    // Arrange
    final int[] array = {-1_246_757_081, -5_242_880};

    // Act and Assert result
    Assert.assertEquals(0, ObjectUtils.nullSafeHashCode(array));
  }

  // Test written by Diffblue Cover.

  @Test
  public void nullSafeToStringInput0OutputNotNull() {

    // Arrange
    final Object[] array = {};

    // Act
    final String actual = ObjectUtils.nullSafeToString(array);

    // Assert result
    Assert.assertEquals("{}", actual);
  }

  // Test written by Diffblue Cover.
  @Test
  public void nullSafeToStringInput0OutputNotNull2() {

    // Arrange
    final boolean[] array = {};

    // Act and Assert result
    Assert.assertEquals("{}", ObjectUtils.nullSafeToString(array));
  }

  // Test written by Diffblue Cover.
  @Test
  public void nullSafeToStringInput0OutputNotNull3() {

    // Arrange
    final byte[] array = {};

    // Act and Assert result
    Assert.assertEquals("{}", ObjectUtils.nullSafeToString(array));
  }

  // Test written by Diffblue Cover.
  @Test
  public void nullSafeToStringInput0OutputNotNull4() {

    // Arrange
    final char[] array = {};

    // Act and Assert result
    Assert.assertEquals("{}", ObjectUtils.nullSafeToString(array));
  }

  // Test written by Diffblue Cover.
  @Test
  public void nullSafeToStringInput0OutputNotNull5() {

    // Arrange
    final int[] array = {};

    // Act and Assert result
    Assert.assertEquals("{}", ObjectUtils.nullSafeToString(array));
  }

  // Test written by Diffblue Cover.
  @Test
  public void nullSafeToStringInput0OutputNotNull6() {

    // Arrange
    final short[] array = {};

    // Act and Assert result
    Assert.assertEquals("{}", ObjectUtils.nullSafeToString(array));
  }

  // Test written by Diffblue Cover.
  @Test
  public void nullSafeToStringInput0OutputNotNull7() {

    // Arrange
    final long[] array = {};

    // Act and Assert result
    Assert.assertEquals("{}", ObjectUtils.nullSafeToString(array));
  }

  // Test written by Diffblue Cover.
  @Test
  public void nullSafeToStringInput0OutputNotNull8() {

    // Arrange
    final float[] array = {};

    // Act and Assert result
    Assert.assertEquals("{}", ObjectUtils.nullSafeToString(array));
  }

  // Test written by Diffblue Cover.
  @Test
  public void nullSafeToStringInput0OutputNotNull000f962abf4f492b190() {

    // Arrange
    final double[] array = {};

    // Act and Assert result
    Assert.assertEquals("{}", ObjectUtils.nullSafeToString(array));
  }

  // Test written by Diffblue Cover.
  @Test
  public void nullSafeToStringInput1OutputNotNull2() {

    // Arrange
    final byte[] array = {(byte)43};

    // Act and Assert result
    Assert.assertEquals("{43}", ObjectUtils.nullSafeToString(array));
  }

  // Test written by Diffblue Cover.
  @Test
  public void nullSafeToStringInput1OutputNotNull3() {

    // Arrange
    final int[] array = {-67_289_088};

    // Act and Assert result
    Assert.assertEquals("{-67289088}", ObjectUtils.nullSafeToString(array));
  }

  // Test written by Diffblue Cover.
  @Test
  public void nullSafeToStringInput1OutputNotNull4() {

    // Arrange
    final char[] array = {'\u0001'};

    // Act and Assert result
    Assert.assertEquals("{\'\u0001\'}", ObjectUtils.nullSafeToString(array));
  }

  // Test written by Diffblue Cover.
  @Test
  public void nullSafeToStringInput1OutputNotNull5() {

    // Arrange
    final long[] array = {0L};

    // Act and Assert result
    Assert.assertEquals("{0}", ObjectUtils.nullSafeToString(array));
  }

  // Test written by Diffblue Cover.
  @Test
  public void nullSafeToStringInput2OutputNotNull() {

    // Arrange
    final boolean[] array = {true, true};

    // Act and Assert result
    Assert.assertEquals("{true, true}", ObjectUtils.nullSafeToString(array));
  }

  // Test written by Diffblue Cover.
  @Test
  public void nullSafeToStringInput2OutputNotNull2() {

    // Arrange
    final byte[] array = {(byte)43, (byte)-8};

    // Act and Assert result
    Assert.assertEquals("{43, -8}", ObjectUtils.nullSafeToString(array));
  }

  // Test written by Diffblue Cover.
  @Test
  public void nullSafeToStringInput2OutputNotNull4() {

    // Arrange
    final int[] array = {-67_289_088, 43};

    // Act and Assert result
    Assert.assertEquals("{-67289088, 43}", ObjectUtils.nullSafeToString(array));
  }

  // Test written by Diffblue Cover.
  @Test
  public void nullSafeToStringInput2OutputNotNull5() {

    // Arrange
    final char[] array = {'\u0001', '\u0001'};

    // Act and Assert result
    Assert.assertEquals("{\'\u0001\', \'\u0001\'}", ObjectUtils.nullSafeToString(array));
  }

  // Test written by Diffblue Cover.
  @Test
  public void nullSafeToStringInput2OutputNotNull6() {

    // Arrange
    final short[] array = {(short)43, (short)-16_384};

    // Act and Assert result
    Assert.assertEquals("{43, -16384}", ObjectUtils.nullSafeToString(array));
  }

  // Test written by Diffblue Cover.
  @Test
  public void nullSafeToStringInput2OutputNotNull7() {

    // Arrange
    final long[] array = {0L, 0L};

    // Act and Assert result
    Assert.assertEquals("{0, 0}", ObjectUtils.nullSafeToString(array));
  }

  // Test written by Diffblue Cover.

  @Test
  public void toObjectArrayInputNullOutput0() {

    // Arrange
    final Object source = null;

    // Act
    final Object[] actual = ObjectUtils.toObjectArray(source);

    // Assert result
    Assert.assertArrayEquals(new Object[] {}, actual);
  }
}
