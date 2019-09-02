package qunar.tc.decompiler.struct.gen.generics;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.Timeout;
import qunar.tc.decompiler.struct.gen.generics.GenericType;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class GenericTypeTest {

  @Rule public final ExpectedException thrown = ExpectedException.none();

  @Rule public final Timeout globalTimeout = new Timeout(10000);

  // Test written by Diffblue Cover.

  @Test
  public void constructorInputNegativeNegativeNotNullOutputVoid() {

    // Arrange
    final int type = -999_998;
    final int arrayDim = -999_998;
    final String value = "foo";

    // Act, creating object to test constructor
    final GenericType genericType = new GenericType(type, arrayDim, value);

    // Assert side effects
    final ArrayList<GenericType> arrayList = new ArrayList<GenericType>();
    Assert.assertEquals(arrayList, genericType.getArguments());
    final ArrayList<Integer> arrayList1 = new ArrayList<Integer>();
    Assert.assertEquals(arrayList1, genericType.getWildcards());
    final ArrayList<GenericType> arrayList2 = new ArrayList<GenericType>();
    Assert.assertEquals(arrayList2, genericType.getEnclosingClasses());
    Assert.assertEquals("foo", genericType.value);
  }

  // Test written by Diffblue Cover.

  @Test
  public void constructorInputNotNullOutputIllegalArgumentException() {

    // Arrange
    final String signature = "foo";

    // Act, creating object to test constructor
    thrown.expect(IllegalArgumentException.class);
    final GenericType genericType = new GenericType(signature);
  }

  // Test written by Diffblue Cover.

  @Test
  public void constructorInputNotNullOutputStringIndexOutOfBoundsException() {

    // Arrange
    final String signature = "L";

    // Act, creating object to test constructor
    thrown.expect(StringIndexOutOfBoundsException.class);
    final GenericType genericType = new GenericType(signature);
  }

  // Test written by Diffblue Cover.

  @Test
  public void constructorInputNotNullOutputVoid() {

    // Arrange
    final String signature = "";

    // Act, creating object to test constructor
    final GenericType genericType = new GenericType(signature);

    // Assert side effects
    final ArrayList<GenericType> arrayList = new ArrayList<GenericType>();
    Assert.assertEquals(arrayList, genericType.getArguments());
    final ArrayList<Integer> arrayList1 = new ArrayList<Integer>();
    Assert.assertEquals(arrayList1, genericType.getWildcards());
    final ArrayList<GenericType> arrayList2 = new ArrayList<GenericType>();
    Assert.assertEquals(arrayList2, genericType.getEnclosingClasses());
  }

  // Test written by Diffblue Cover.
  @Test
  public void getNextTypeInputNotNullOutputNotNull() {

    // Act and Assert result
    Assert.assertEquals("\'", GenericType.getNextType("\'"));
  }

  // Test written by Diffblue Cover.
  @Test
  public void getNextTypeInputNotNullOutputNotNull2() {

    // Act and Assert result
    Assert.assertEquals("*",
                        GenericType.getNextType(
                            "*++?????????????????????????????????????????????????????????????"));
  }

  // Test written by Diffblue Cover.
  @Test
  public void getNextTypeInputNotNullOutputNotNull3() {

    // Act and Assert result
    Assert.assertEquals(
        ";",
        GenericType.getNextType(
            ";\u0000\u0000?????????????????????????????????????????????????????????????"));
  }

  // Test written by Diffblue Cover.
  @Test
  public void getNextTypeInputNotNullOutputStringIndexOutOfBoundsException() {

    // Act
    thrown.expect(StringIndexOutOfBoundsException.class);
    GenericType.getNextType("");

    // The method is not expected to return due to exception thrown
  }

  // Test written by Diffblue Cover.
  @Test
  public void getNextTypeInputNotNullOutputStringIndexOutOfBoundsException2() {

    // Act
    thrown.expect(StringIndexOutOfBoundsException.class);
    GenericType.getNextType("L");

    // The method is not expected to return due to exception thrown
  }

  // Test written by Diffblue Cover.
  @Test
  public void getNextTypeInputNotNullOutputStringIndexOutOfBoundsException3() {

    // Act
    thrown.expect(StringIndexOutOfBoundsException.class);
    GenericType.getNextType(">");

    // The method is not expected to return due to exception thrown
  }

  // Test written by Diffblue Cover.
  @Test
  public void getNextTypeInputNotNullOutputStringIndexOutOfBoundsException4() {

    // Act
    thrown.expect(StringIndexOutOfBoundsException.class);
    GenericType.getNextType("<");

    // The method is not expected to return due to exception thrown
  }
}
