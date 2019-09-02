package qunar.tc.bistoury.instrument.client.spring.el;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.Timeout;
import qunar.tc.bistoury.instrument.client.spring.el.Operator.DescriptorComparison;

public class Operator_DescriptorComparisonTest {

  @Rule public final ExpectedException thrown = ExpectedException.none();

  @Rule public final Timeout globalTimeout = new Timeout(10000);

  // Test written by Diffblue Cover.
  @Test
  public void checkNumericCompatibilityInputNotNullNotNullNotNullNotNullOutputNotNull() {

    // Act
    final DescriptorComparison actual =
        DescriptorComparison.checkNumericCompatibility("foo", "foo", "foo", "foo");

    // Assert result
    Assert.assertNotNull(actual);
    Assert.assertEquals(' ', actual.compatibleType);
    Assert.assertFalse(actual.areNumbers);
    Assert.assertFalse(actual.areCompatible);
  }
}
