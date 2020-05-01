package qunar.tc.bistoury.instrument.client.spring.el;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.Timeout;
import qunar.tc.bistoury.instrument.client.spring.el.NestedExceptionUtils;

public class NestedExceptionUtilsTest {

  @Rule public final ExpectedException thrown = ExpectedException.none();

  @Rule public final Timeout globalTimeout = new Timeout(10000);

  // Test written by Diffblue Cover.
  @Test
  public void buildMessageInputNotNullNullOutputNotNull() {

    // Act and Assert result
    Assert.assertEquals("?????????????", NestedExceptionUtils.buildMessage("?????????????", null));
  }
}
