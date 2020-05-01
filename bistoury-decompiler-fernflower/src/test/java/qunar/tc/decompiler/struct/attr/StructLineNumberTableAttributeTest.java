package qunar.tc.decompiler.struct.attr;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.Timeout;
import qunar.tc.decompiler.struct.attr.StructLineNumberTableAttribute;

public class StructLineNumberTableAttributeTest {

  @Rule public final ExpectedException thrown = ExpectedException.none();

  @Rule public final Timeout globalTimeout = new Timeout(10000);

  // Test written by Diffblue Cover.
  @Test
  public void findLineNumberInputPositiveOutputNegative2() {

    // Arrange
    final StructLineNumberTableAttribute structLineNumberTableAttribute =
        new StructLineNumberTableAttribute();

    // Act and Assert result
    Assert.assertEquals(-1, structLineNumberTableAttribute.findLineNumber(2));
  }
}
