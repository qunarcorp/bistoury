package qunar.tc.decompiler.code;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.Timeout;
import qunar.tc.decompiler.code.SwitchInstruction;

import java.lang.reflect.Array;

public class SwitchInstructionTest {

  @Rule public final ExpectedException thrown = ExpectedException.none();

  @Rule public final Timeout globalTimeout = new Timeout(10000);

  // Test written by Diffblue Cover.
  @Test
  public void getDefaultDestinationOutputZero() {

    // Arrange
    final int[] myIntArray = {};
    final SwitchInstruction switchInstruction = new SwitchInstruction(8, 2, false, 2, myIntArray);

    // Act and Assert result
    Assert.assertEquals(0, switchInstruction.getDefaultDestination());
  }

  // Test written by Diffblue Cover.
  @Test
  public void getDestinationsOutputNull() {

    // Arrange
    final int[] myIntArray = {};
    final SwitchInstruction switchInstruction = new SwitchInstruction(8, 2, false, 2, myIntArray);

    // Act and Assert result
    Assert.assertNull(switchInstruction.getDestinations());
  }

  // Test written by Diffblue Cover.
  @Test
  public void getValuesOutputNull() {

    // Arrange
    final int[] myIntArray = {};
    final SwitchInstruction switchInstruction = new SwitchInstruction(8, 2, false, 2, myIntArray);

    // Act and Assert result
    Assert.assertNull(switchInstruction.getValues());
  }
}
