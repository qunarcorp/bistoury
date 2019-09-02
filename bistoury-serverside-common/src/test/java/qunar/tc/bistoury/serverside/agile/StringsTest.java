package qunar.tc.bistoury.serverside.agile;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.Timeout;
import qunar.tc.bistoury.serverside.agile.Strings;

public class StringsTest {

  @Rule public final ExpectedException thrown = ExpectedException.none();

  @Rule public final Timeout globalTimeout = new Timeout(10000);

  // Test written by Diffblue Cover.
  @Test
  public void getBooleanInputNotNullFalseOutputFalse() {

    // Act and Assert result
    Assert.assertFalse(Strings.getBoolean(",", false));
  }

  // Test written by Diffblue Cover.
  @Test
  public void getBooleanInputNotNullFalseOutputTrue() {

    // Act and Assert result
    Assert.assertTrue(Strings.getBoolean(
        "                                                                     true  ", false));
  }

  // Test written by Diffblue Cover.
  @Test
  public void getBooleanInputNullFalseOutputFalse() {

    // Act and Assert result
    Assert.assertFalse(Strings.getBoolean(null, false));
  }

  // Test written by Diffblue Cover.
  @Test
  public void isEmptyInputNotNullOutputFalse() {

    // Act and Assert result
    Assert.assertFalse(Strings.isEmpty("/"));
  }

  // Test written by Diffblue Cover.
  @Test
  public void isEmptyInputNullOutputTrue() {

    // Act and Assert result
    Assert.assertTrue(Strings.isEmpty(null));
  }
}
