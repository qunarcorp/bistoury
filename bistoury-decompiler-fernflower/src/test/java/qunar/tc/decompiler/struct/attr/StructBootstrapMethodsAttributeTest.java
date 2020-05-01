package qunar.tc.decompiler.struct.attr;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.Timeout;
import qunar.tc.decompiler.struct.attr.StructBootstrapMethodsAttribute;

import java.lang.reflect.Method;

public class StructBootstrapMethodsAttributeTest {

  @Rule public final ExpectedException thrown = ExpectedException.none();

  @Rule public final Timeout globalTimeout = new Timeout(10000);

  // Test written by Diffblue Cover.
  @Test
  public void getMethodArgumentsInputPositiveOutputIndexOutOfBoundsException() {

    // Arrange
    final StructBootstrapMethodsAttribute structBootstrapMethodsAttribute =
        new StructBootstrapMethodsAttribute();

    // Act
    thrown.expect(IndexOutOfBoundsException.class);
    structBootstrapMethodsAttribute.getMethodArguments(2);

    // The method is not expected to return due to exception thrown
  }

  // Test written by Diffblue Cover.
  @Test
  public void getMethodReferenceInputPositiveOutputIndexOutOfBoundsException() {

    // Arrange
    final StructBootstrapMethodsAttribute structBootstrapMethodsAttribute =
        new StructBootstrapMethodsAttribute();

    // Act
    thrown.expect(IndexOutOfBoundsException.class);
    structBootstrapMethodsAttribute.getMethodReference(2);

    // The method is not expected to return due to exception thrown
  }

  // Test written by Diffblue Cover.
  @Test
  public void getMethodsNumberOutputZero() {

    // Arrange
    final StructBootstrapMethodsAttribute structBootstrapMethodsAttribute =
        new StructBootstrapMethodsAttribute();

    // Act and Assert result
    Assert.assertEquals(0, structBootstrapMethodsAttribute.getMethodsNumber());
  }
}
