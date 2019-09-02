package qunar.tc.decompiler.struct.attr;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.Timeout;
import qunar.tc.decompiler.struct.attr.StructMethodParametersAttribute;

import java.lang.reflect.Method;

public class StructMethodParametersAttributeTest {

  @Rule public final ExpectedException thrown = ExpectedException.none();

  @Rule public final Timeout globalTimeout = new Timeout(10000);

  // Test written by Diffblue Cover.
  @Test
  public void getEntriesOutputNull() {

    // Arrange
    final StructMethodParametersAttribute structMethodParametersAttribute =
        new StructMethodParametersAttribute();

    // Act and Assert result
    Assert.assertNull(structMethodParametersAttribute.getEntries());
  }
}
