package qunar.tc.bistoury.clientside.common.meta;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.Timeout;
import qunar.tc.bistoury.clientside.common.meta.Numbers;

public class NumbersTest {

  @Rule public final ExpectedException thrown = ExpectedException.none();

  @Rule public final Timeout globalTimeout = new Timeout(10000);

  // Test written by Diffblue Cover.

  @Test
  public void ipNiceNumberInputNotNullOutputPositive2() {

    // Arrange
    final String ip = "7";

    // Act
    final long actual = Numbers.ipNiceNumber(ip);

    // Assert result
    Assert.assertEquals(7L, actual);
  }

  // Test written by Diffblue Cover.

  @Test
  public void ipNiceNumberInputNotNullOutputPositive3() {

    // Arrange
    final String ip = ".1";

    // Act
    final long actual = Numbers.ipNiceNumber(ip);

    // Assert result
    Assert.assertEquals(1L, actual);
  }

  // Test written by Diffblue Cover.

  @Test
  public void ipNiceNumberInputNotNullOutputZero() {

    // Arrange
    final String ip = "";

    // Act
    final long actual = Numbers.ipNiceNumber(ip);

    // Assert result
    Assert.assertEquals(0L, actual);
  }

  // Test written by Diffblue Cover.

  @Test
  public void ipNiceNumberInputNotNullOutputZero3() {

    // Arrange
    final String ip = ".";

    // Act
    final long actual = Numbers.ipNiceNumber(ip);

    // Assert result
    Assert.assertEquals(0L, actual);
  }

  // Test written by Diffblue Cover.
  @Test
  public void toDoubleInputNullOutputZero() {

    // Act and Assert result
    Assert.assertEquals(0.0, Numbers.toDouble(null), 0.0);
  }

  // Test written by Diffblue Cover.
  @Test
  public void toDoubleInputNullZeroOutputZero() {

    // Act and Assert result
    Assert.assertEquals(0.0, Numbers.toDouble(null, 0.0), 0.0);
  }

  // Test written by Diffblue Cover.
  @Test
  public void toFloatInputNotNullPositiveOutputPositive() {

    // Act and Assert result
    Assert.assertEquals(2.0f, Numbers.toFloat(",", 2.0f), 0.0f);
  }

  // Test written by Diffblue Cover.
  @Test
  public void toFloatInputNullOutputZero() {

    // Act and Assert result
    Assert.assertEquals(0.0f, Numbers.toFloat(null), 0.0f);
  }

  // Test written by Diffblue Cover.
  @Test
  public void toFloatInputNullZeroOutputZero() {

    // Act and Assert result
    Assert.assertEquals(0.0f, Numbers.toFloat(null, 0.0f), 0.0f);
  }

  // Test written by Diffblue Cover.
  @Test
  public void toIntInputNotNullOutputPositive() {

    // Act and Assert result
    Assert.assertEquals(1, Numbers.toInt("1"));
  }

  // Test written by Diffblue Cover.
  @Test
  public void toIntInputNotNullZeroOutputNegative() {

    // Act and Assert result
    Assert.assertEquals(-7, Numbers.toInt("-7", 0));
  }

  // Test written by Diffblue Cover.
  @Test
  public void toIntInputNullZeroOutputZero() {

    // Act and Assert result
    Assert.assertEquals(0, Numbers.toInt(null, 0));
  }

  // Test written by Diffblue Cover.
  @Test
  public void toLongInputNotNullZeroOutputPositive() {

    // Act and Assert result
    Assert.assertEquals(3L, Numbers.toLong("3", 0L));
  }

  // Test written by Diffblue Cover.
  @Test
  public void toLongInputNullOutputZero() {

    // Act and Assert result
    Assert.assertEquals(0L, Numbers.toLong(null));
  }

  // Test written by Diffblue Cover.
  @Test
  public void toLongInputNullZeroOutputZero() {

    // Act and Assert result
    Assert.assertEquals(0L, Numbers.toLong(null, 0L));
  }
}
