package qunar.tc.decompiler.struct.attr;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.Timeout;
import qunar.tc.decompiler.struct.attr.StructEnclosingMethodAttribute;

import java.lang.reflect.Method;

public class StructEnclosingMethodAttributeTest {

  @Rule public final ExpectedException thrown = ExpectedException.none();

  @Rule public final Timeout globalTimeout = new Timeout(10000);

  // Test written by Diffblue Cover.
  @Test
  public void getClassNameOutputNull() {

    // Arrange
    final StructEnclosingMethodAttribute structEnclosingMethodAttribute =
        new StructEnclosingMethodAttribute();

    // Act and Assert result
    Assert.assertNull(structEnclosingMethodAttribute.getClassName());
  }

  // Test written by Diffblue Cover.
  @Test
  public void getMethodDescriptorOutputNull() {

    // Arrange
    final StructEnclosingMethodAttribute structEnclosingMethodAttribute =
        new StructEnclosingMethodAttribute();

    // Act and Assert result
    Assert.assertNull(structEnclosingMethodAttribute.getMethodDescriptor());
  }

  // Test written by Diffblue Cover.
  @Test
  public void getMethodNameOutputNull() {

    // Arrange
    final StructEnclosingMethodAttribute structEnclosingMethodAttribute =
        new StructEnclosingMethodAttribute();

    // Act and Assert result
    Assert.assertNull(structEnclosingMethodAttribute.getMethodName());
  }
}
