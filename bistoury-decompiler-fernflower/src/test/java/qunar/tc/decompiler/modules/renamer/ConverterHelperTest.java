package qunar.tc.decompiler.modules.renamer;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.Timeout;
import qunar.tc.decompiler.modules.renamer.ConverterHelper;

public class ConverterHelperTest {

  @Rule public final ExpectedException thrown = ExpectedException.none();

  @Rule public final Timeout globalTimeout = new Timeout(10000);

  // Test written by Diffblue Cover.
  @Test
  public void getSimpleClassNameInputNotNullOutputNotNull() {

    // Act and Assert result
    Assert.assertEquals("a\'b\'c", ConverterHelper.getSimpleClassName("a\'b\'c"));
  }

  // Test written by Diffblue Cover.
  @Test
  public void replaceSimpleClassNameInputNotNullNotNullOutputNotNull() {

    // Act and Assert result
    Assert.assertEquals("a/b//", ConverterHelper.replaceSimpleClassName("a/b/c", "/"));
  }
}
