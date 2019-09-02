package qunar.tc.decompiler.main.extern;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.Timeout;
import qunar.tc.decompiler.main.extern.IIdentifierRenamer.Type;

public class IIdentifierRenamer_TypeTest {

  @Rule public final ExpectedException thrown = ExpectedException.none();

  @Rule public final Timeout globalTimeout = new Timeout(10000);

  // Test written by Diffblue Cover.

  @Test
  public void valueOfInputNullOutputNullPointerException() {

    // Arrange
    final String name = null;

    // Act
    thrown.expect(NullPointerException.class);
    Type.valueOf(name);

    // The method is not expected to return due to exception thrown
  }
}
