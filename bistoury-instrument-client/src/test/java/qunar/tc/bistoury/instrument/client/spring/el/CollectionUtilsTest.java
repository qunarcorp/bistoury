package qunar.tc.bistoury.instrument.client.spring.el;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.Timeout;
import qunar.tc.bistoury.instrument.client.spring.el.CollectionUtils;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CollectionUtilsTest {

  @Rule public final ExpectedException thrown = ExpectedException.none();

  @Rule public final Timeout globalTimeout = new Timeout(10000);

  // Test written by Diffblue Cover.
  @Test
  public void arrayToListInputNegativeOutputIllegalArgumentException() {

    // Act
    thrown.expect(IllegalArgumentException.class);
    CollectionUtils.arrayToList(-21);

    // The method is not expected to return due to exception thrown
  }

  // Test written by Diffblue Cover.

  @Test
  public void arrayToListInputNullOutput0() {

    // Arrange
    final Object source = null;

    // Act
    final List actual = CollectionUtils.arrayToList(source);

    // Assert result
    final ArrayList arrayList = new ArrayList();
    Assert.assertEquals(arrayList, actual);
  }

  // Test written by Diffblue Cover.
  @Test
  public void containsAnyInput00OutputFalse() {

    // Arrange
    final ArrayList source = new ArrayList();
    final ArrayList candidates = new ArrayList();

    // Act and Assert result
    Assert.assertFalse(CollectionUtils.containsAny(source, candidates));
  }

  // Test written by Diffblue Cover.
  @Test
  public void containsAnyInput10OutputFalse() {

    // Arrange
    final ArrayList source = new ArrayList();
    source.add(null);
    final ArrayList candidates = new ArrayList();

    // Act and Assert result
    Assert.assertFalse(CollectionUtils.containsAny(source, candidates));
  }

  // Test written by Diffblue Cover.
  @Test
  public void containsAnyInput11OutputTrue() {

    // Arrange
    final ArrayList source = new ArrayList();
    source.add(null);
    final ArrayList candidates = new ArrayList();
    candidates.add(null);

    // Act and Assert result
    Assert.assertTrue(CollectionUtils.containsAny(source, candidates));
  }

  // Test written by Diffblue Cover.

  @Test
  public void containsAnyInput12OutputFalse() {

    // Arrange
    final ArrayList source = new ArrayList();
    source.add(1_103_904_774);
    final ArrayList candidates = new ArrayList();
    candidates.add(1_103_908_870);
    candidates.add(1_099_710_470);

    // Act
    final boolean actual = CollectionUtils.containsAny(source, candidates);

    // Assert result
    Assert.assertFalse(actual);
  }

  // Test written by Diffblue Cover.
  @Test
  public void containsInstanceInput0NegativeOutputFalse() {

    // Arrange
    final ArrayList collection = new ArrayList();

    // Act and Assert result
    Assert.assertFalse(CollectionUtils.containsInstance(collection, -999_998));
  }

  // Test written by Diffblue Cover.

  @Test
  public void containsInstanceInput2NullOutputTrue() {

    // Arrange
    final ArrayList collection = new ArrayList();
    collection.add(null);
    collection.add(null);
    final Object element = null;

    // Act
    final boolean actual = CollectionUtils.containsInstance(collection, element);

    // Assert result
    Assert.assertTrue(actual);
  }

  // Test written by Diffblue Cover.

  @Test
  public void containsInstanceInput2NullOutputTrue2() {

    // Arrange
    final ArrayList collection = new ArrayList();
    collection.add(0);
    collection.add(null);
    final Object element = null;

    // Act
    final boolean actual = CollectionUtils.containsInstance(collection, element);

    // Assert result
    Assert.assertTrue(actual);
  }

  // Test written by Diffblue Cover.
  @Test
  public void findCommonElementTypeInput0OutputNull() {

    // Arrange
    final ArrayList collection = new ArrayList();

    // Act and Assert result
    Assert.assertNull(CollectionUtils.findCommonElementType(collection));
  }

  // Test written by Diffblue Cover.

  @Test
  public void findCommonElementTypeInput2OutputNull() {

    // Arrange
    final ArrayList collection = new ArrayList();
    collection.add(null);
    collection.add(null);

    // Act
    final Class actual = CollectionUtils.findCommonElementType(collection);

    // Assert result
    Assert.assertNull(actual);
  }

  // Test written by Diffblue Cover.
  @Test
  public void findFirstMatchInput01OutputNull() {

    // Arrange
    final ArrayList source = new ArrayList();
    final ArrayList candidates = new ArrayList();
    candidates.add(-999_999);

    // Act and Assert result
    Assert.assertNull(CollectionUtils.findFirstMatch(source, candidates));
  }

  // Test written by Diffblue Cover.
  @Test
  public void findFirstMatchInput11OutputNull() {

    // Arrange
    final ArrayList source = new ArrayList();
    source.add(null);
    final ArrayList candidates = new ArrayList();
    candidates.add(null);

    // Act and Assert result
    Assert.assertNull(CollectionUtils.findFirstMatch(source, candidates));
  }

  // Test written by Diffblue Cover.

  @Test
  public void findFirstMatchInput11OutputNull2() {

    // Arrange
    final ArrayList source = new ArrayList();
    source.add(null);
    final ArrayList candidates = new ArrayList();
    candidates.add(1);

    // Act
    final Object actual = CollectionUtils.findFirstMatch(source, candidates);

    // Assert result
    Assert.assertNull(actual);
  }

  // Test written by Diffblue Cover.
  @Test
  public void findValueOfTypeInput00OutputNull() {

    // Arrange
    final ArrayList collection = new ArrayList();
    final Class[] types = {};

    // Act and Assert result
    Assert.assertNull(CollectionUtils.findValueOfType(collection, types));
  }

  // Test written by Diffblue Cover.

  @Test
  public void findValueOfTypeInput1NullOutputZero() {

    // Arrange
    final ArrayList collection = new ArrayList();
    collection.add(0);
    final Class type = null;

    // Act
    final Object actual = CollectionUtils.findValueOfType(collection, type);

    // Assert result
    Assert.assertEquals(0, actual);
  }

  // Test written by Diffblue Cover.

  @Test
  public void findValueOfTypeInput2NullOutputNull() {

    // Arrange
    final ArrayList collection = new ArrayList();
    collection.add(0);
    collection.add(null);
    final Class type = null;

    // Act
    final Object actual = CollectionUtils.findValueOfType(collection, type);

    // Assert result
    Assert.assertNull(actual);
  }

  // Test written by Diffblue Cover.
  @Test
  public void findValueOfTypeInput10OutputNull() {

    // Arrange
    final ArrayList collection = new ArrayList();
    collection.add(null);
    final Class[] types = {};

    // Act and Assert result
    Assert.assertNull(CollectionUtils.findValueOfType(collection, types));
  }

  // Test written by Diffblue Cover.
  @Test
  public void findValueOfTypeInput11OutputNull2() {

    // Arrange
    final ArrayList collection = new ArrayList();
    collection.add(null);
    final Class[] types = {null};

    // Act and Assert result
    Assert.assertNull(CollectionUtils.findValueOfType(collection, types));
  }

  // Test written by Diffblue Cover.

  @Test
  public void findValueOfTypeInput11OutputZero() {

    // Arrange
    final ArrayList collection = new ArrayList();
    collection.add(0);
    final Class[] types = {null};

    // Act
    final Object actual = CollectionUtils.findValueOfType(collection, types);

    // Assert result
    Assert.assertEquals(0, actual);
  }

  // Test written by Diffblue Cover.
  @Test
  public void hasUniqueObjectInput0OutputFalse() {

    // Arrange
    final ArrayList collection = new ArrayList();

    // Act and Assert result
    Assert.assertFalse(CollectionUtils.hasUniqueObject(collection));
  }

  // Test written by Diffblue Cover.

  @Test
  public void hasUniqueObjectInput1OutputTrue() {

    // Arrange
    final ArrayList collection = new ArrayList();
    collection.add(null);

    // Act
    final boolean actual = CollectionUtils.hasUniqueObject(collection);

    // Assert result
    Assert.assertTrue(actual);
  }

  // Test written by Diffblue Cover.

  @Test
  public void hasUniqueObjectInput2OutputFalse() {

    // Arrange
    final ArrayList collection = new ArrayList();
    collection.add(null);
    collection.add(0);

    // Act
    final boolean actual = CollectionUtils.hasUniqueObject(collection);

    // Assert result
    Assert.assertFalse(actual);
  }

  // Test written by Diffblue Cover.

  @Test
  public void hasUniqueObjectInput2OutputTrue() {

    // Arrange
    final ArrayList collection = new ArrayList();
    collection.add(null);
    collection.add(null);

    // Act
    final boolean actual = CollectionUtils.hasUniqueObject(collection);

    // Assert result
    Assert.assertTrue(actual);
  }

  // Test written by Diffblue Cover.
  @Test
  public void isEmptyInput0OutputTrue() {

    // Arrange
    final ArrayList collection = new ArrayList();

    // Act and Assert result
    Assert.assertTrue(CollectionUtils.isEmpty(collection));
  }

  // Test written by Diffblue Cover.
  @Test
  public void isEmptyInput0OutputTrue2() {

    // Arrange
    final HashMap map = new HashMap();

    // Act and Assert result
    Assert.assertTrue(CollectionUtils.isEmpty(map));
  }

  // Test written by Diffblue Cover.
  @Test
  public void isEmptyInput1OutputFalse() {

    // Arrange
    final ArrayList collection = new ArrayList();
    collection.add(null);

    // Act and Assert result
    Assert.assertFalse(CollectionUtils.isEmpty(collection));
  }

  // Test written by Diffblue Cover.
  @Test
  public void isEmptyInput1OutputFalse2() {

    // Arrange
    final HashMap map = new HashMap();
    map.put(null, null);

    // Act and Assert result
    Assert.assertFalse(CollectionUtils.isEmpty(map));
  }

  // Test written by Diffblue Cover.
  @Test
  public void mergeArrayIntoCollectionInputNegative1OutputIllegalArgumentException() {

    // Arrange
    final ArrayList collection = new ArrayList();
    collection.add(null);

    // Act
    thrown.expect(IllegalArgumentException.class);
    CollectionUtils.mergeArrayIntoCollection(-21, collection);

    // The method is not expected to return due to exception thrown
  }

  // Test written by Diffblue Cover.
  @Test
  public void mergeArrayIntoCollectionInputNullNullOutputIllegalArgumentException() {

    // Act
    thrown.expect(IllegalArgumentException.class);
    CollectionUtils.mergeArrayIntoCollection(null, null);

    // The method is not expected to return due to exception thrown
  }

  // Test written by Diffblue Cover.
  @Test
  public void mergePropertiesIntoMapInputNullNullOutputIllegalArgumentException() {

    // Act
    thrown.expect(IllegalArgumentException.class);
    CollectionUtils.mergePropertiesIntoMap(null, null);

    // The method is not expected to return due to exception thrown
  }
}
