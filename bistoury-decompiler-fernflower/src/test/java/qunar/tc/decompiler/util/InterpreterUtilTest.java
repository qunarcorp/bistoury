package qunar.tc.decompiler.util;

import static org.mockito.AdditionalMatchers.or;
import static org.mockito.Matchers.isA;
import static org.mockito.Matchers.isNull;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.Timeout;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import qunar.tc.decompiler.util.InterpreterUtil;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

@RunWith(PowerMockRunner.class)
public class InterpreterUtilTest {

  @Rule public final ExpectedException thrown = ExpectedException.none();

  @Rule public final Timeout globalTimeout = new Timeout(10000);

  // Test written by Diffblue Cover.
  @Test
  public void equalListsInput00OutputTrue() {

    // Arrange
    final ArrayList first = new ArrayList();
    final ArrayList second = new ArrayList();

    // Act and Assert result
    Assert.assertTrue(InterpreterUtil.equalLists(first, second));
  }

  // Test written by Diffblue Cover.

  @Test
  public void equalListsInput1NullOutputFalse() {

    // Arrange
    final ArrayList first = new ArrayList();
    first.add(null);
    final List second = null;

    // Act
    final boolean actual = InterpreterUtil.equalLists(first, second);

    // Assert result
    Assert.assertFalse(actual);
  }

  // Test written by Diffblue Cover.
  @Test
  public void equalListsInput10OutputFalse() {

    // Arrange
    final ArrayList first = new ArrayList();
    first.add(null);
    final ArrayList second = new ArrayList();

    // Act and Assert result
    Assert.assertFalse(InterpreterUtil.equalLists(first, second));
  }

  // Test written by Diffblue Cover.
  @Test
  public void equalListsInputNullNullOutputTrue() {

    // Act and Assert result
    Assert.assertTrue(InterpreterUtil.equalLists(null, null));
  }

  // Test written by Diffblue Cover.
  @Test
  public void equalObjectsInputNegativeNegativeOutputTrue() {

    // Act and Assert result
    Assert.assertTrue(InterpreterUtil.equalObjects(-999_998, -999_998));
  }

  // Test written by Diffblue Cover.
  @Test
  public void equalObjectsInputNullNullOutputTrue() {

    // Act and Assert result
    Assert.assertTrue(InterpreterUtil.equalObjects(null, null));
  }

  // Test written by Diffblue Cover.

  @Test
  public void equalSetsInput0NullOutputFalse() {

    // Arrange
    final ArrayList c1 = new ArrayList();
    final Collection c2 = null;

    // Act
    final boolean actual = InterpreterUtil.equalSets(c1, c2);

    // Assert result
    Assert.assertFalse(actual);
  }

  // Test written by Diffblue Cover.
  @PrepareForTest({HashSet.class, InterpreterUtil.class, AbstractSet.class})
  @Test
  public void equalSetsInput00OutputTrue() throws Exception {

    // Arrange
    final ArrayList c1 = new ArrayList();
    final ArrayList c2 = new ArrayList();
    final HashSet hashSet = new HashSet();
    PowerMockito.whenNew(HashSet.class)
        .withParameterTypes(Collection.class)
        .withArguments(or(isA(Collection.class), isNull(Collection.class)))
        .thenReturn(hashSet);

    // Act
    final boolean actual = InterpreterUtil.equalSets(c1, c2);

    // Assert result
    Assert.assertTrue(actual);
  }

  // Test written by Diffblue Cover.
  @Test
  public void equalSetsInput01OutputFalse() {

    // Arrange
    final ArrayList c1 = new ArrayList();
    final ArrayList c2 = new ArrayList();
    c2.add(null);

    // Act and Assert result
    Assert.assertFalse(InterpreterUtil.equalSets(c1, c2));
  }

  // Test written by Diffblue Cover.
  @PrepareForTest({HashSet.class, InterpreterUtil.class, AbstractSet.class})
  @Test
  public void equalSetsInput22OutputFalse() throws Exception {

    // Arrange
    final ArrayList c1 = new ArrayList();
    c1.add(2_228_416);
    c1.add(2_228_416);
    final ArrayList c2 = new ArrayList();
    c2.add(0);
    c2.add(0);
    final HashSet hashSet = new HashSet();
    hashSet.add(2228416);
    PowerMockito.whenNew(HashSet.class)
        .withParameterTypes(Collection.class)
        .withArguments(or(isA(Collection.class), isNull(Collection.class)))
        .thenReturn(hashSet);

    // Act
    final boolean actual = InterpreterUtil.equalSets(c1, c2);

    // Assert result
    Assert.assertFalse(actual);
  }

  // Test written by Diffblue Cover.
  @Test
  public void equalSetsInputNullNullOutputTrue() {

    // Act and Assert result
    Assert.assertTrue(InterpreterUtil.equalSets(null, null));
  }

  // Test written by Diffblue Cover.
  @Test
  public void makeUniqueKeyInputNotNullNotNullNotNullOutputNotNull() {

    // Act and Assert result
    Assert.assertEquals("/ foo /", InterpreterUtil.makeUniqueKey("/", "foo", "/"));
  }

  // Test written by Diffblue Cover.
  @Test
  public void makeUniqueKeyInputNotNullNotNullOutputNotNull() {

    // Act and Assert result
    Assert.assertEquals("/ /", InterpreterUtil.makeUniqueKey("/", "/"));
  }

  // Test written by Diffblue Cover.
  @Test
  public void readBytesInputNullZeroOutput0() throws IOException {

    // Act
    final byte[] actual = InterpreterUtil.readBytes(null, 0);

    // Assert result
    Assert.assertArrayEquals(new byte[] {}, actual);
  }
}
