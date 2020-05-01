package qunar.tc.decompiler.struct.attr;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.Timeout;
import qunar.tc.decompiler.struct.attr.StructGeneralAttribute;

public class StructGeneralAttributeTest {

  @Rule public final ExpectedException thrown = ExpectedException.none();

  @Rule public final Timeout globalTimeout = new Timeout(10000);

  // Test written by Diffblue Cover.
  @Test
  public void createAttributeInputNotNullOutputNotNull5() {

    // Act
    final StructGeneralAttribute actual = StructGeneralAttribute.createAttribute("Deprecated");

    // Assert result
    Assert.assertNotNull(actual);
    Assert.assertEquals("Deprecated", actual.getName());
  }

  // Test written by Diffblue Cover.
  @Test
  public void createAttributeInputNotNullOutputNotNull6() {

    // Act
    final StructGeneralAttribute actual = StructGeneralAttribute.createAttribute("Synthetic");

    // Assert result
    Assert.assertNotNull(actual);
    Assert.assertEquals("Synthetic", actual.getName());
  }

  // Test written by Diffblue Cover.
  @Test
  public void createAttributeInputNotNullOutputNull() {

    // Act and Assert result
    Assert.assertNull(StructGeneralAttribute.createAttribute("/"));
  }

  // Test written by Diffblue Cover.
  @Test
  public void getNameOutputNull() {

    // Arrange
    final StructGeneralAttribute structGeneralAttribute = new StructGeneralAttribute();

    // Act and Assert result
    Assert.assertNull(structGeneralAttribute.getName());
  }
}
