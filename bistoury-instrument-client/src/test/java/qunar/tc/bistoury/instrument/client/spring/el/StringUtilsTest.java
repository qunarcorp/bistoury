package qunar.tc.bistoury.instrument.client.spring.el;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.Timeout;
import qunar.tc.bistoury.instrument.client.spring.el.StringUtils;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Properties;

public class StringUtilsTest {

  @Rule public final ExpectedException thrown = ExpectedException.none();

  @Rule public final Timeout globalTimeout = new Timeout(10000);

  // Test written by Diffblue Cover.

  @Test
  public void addStringToArrayInput0NotNullOutput1() {

    // Arrange
    final String[] array = {};
    final String str = "/";

    // Act
    final String[] actual = StringUtils.addStringToArray(array, str);

    // Assert result
    Assert.assertArrayEquals(new String[] {"/"}, actual);
  }

  // Test written by Diffblue Cover.
  @Test
  public void applyRelativePathInputNotNullNotNullOutputNotNull() {

    // Act and Assert result
    Assert.assertEquals("/", StringUtils.applyRelativePath("/", "/"));
  }

  // Test written by Diffblue Cover.
  @Test
  public void applyRelativePathInputNotNullNotNullOutputNotNull2() {

    // Act and Assert result
    Assert.assertEquals("foo", StringUtils.applyRelativePath("foo", "foo"));
  }

  // Test written by Diffblue Cover.
  @Test
  public void applyRelativePathInputNotNullNotNullOutputNotNull3() {

    // Act and Assert result
    Assert.assertEquals(
        "/\u0000?",
        StringUtils.applyRelativePath(
            "/???????????????????????????????????????????????????????????????", "\u0000?"));
  }

  // Test written by Diffblue Cover.

  @Test
  public void arrayToCommaDelimitedStringInput0OutputNotNull() {

    // Arrange
    final Object[] arr = {};

    // Act
    final String actual = StringUtils.arrayToCommaDelimitedString(arr);

    // Assert result
    Assert.assertEquals("", actual);
  }

  // Test written by Diffblue Cover.

  @Test
  public void arrayToDelimitedStringInput0NotNullOutputNotNull() {

    // Arrange
    final Object[] arr = {};
    final String delim = "\'";

    // Act
    final String actual = StringUtils.arrayToDelimitedString(arr, delim);

    // Assert result
    Assert.assertEquals("", actual);
  }

  // Test written by Diffblue Cover.

  @Test
  public void arrayToDelimitedStringInput1NullOutputNotNull() {

    // Arrange
    final Object[] arr = {-491_519};
    final String delim = null;

    // Act
    final String actual = StringUtils.arrayToDelimitedString(arr, delim);

    // Assert result
    Assert.assertEquals("-491519", actual);
  }

  // Test written by Diffblue Cover.

  @Test
  public void arrayToDelimitedStringInput2NotNullOutputNotNull() {

    // Arrange
    final Object[] arr = {-491_519, null};
    final String delim = "?";

    // Act
    final String actual = StringUtils.arrayToDelimitedString(arr, delim);

    // Assert result
    Assert.assertEquals("-491519?null", actual);
  }

  // Test written by Diffblue Cover.
  @Test
  public void capitalizeInputNotNullOutputNotNull6() {

    // Act and Assert result
    Assert.assertEquals("", StringUtils.capitalize(""));
  }

  // Test written by Diffblue Cover.
  @Test
  public void capitalizeInputNullOutputNull() {

    // Act and Assert result
    Assert.assertNull(StringUtils.capitalize(null));
  }

  // Test written by Diffblue Cover.

  @Test
  public void cleanPathInputNotNullOutputNotNull() {

    // Arrange
    final String path = "/";

    // Act
    final String actual = StringUtils.cleanPath(path);

    // Assert result
    Assert.assertEquals("/", actual);
  }

  // Test written by Diffblue Cover.
  @Test
  public void cleanPathInputNullOutputNull() {

    // Act and Assert result
    Assert.assertNull(StringUtils.cleanPath(null));
  }

  // Test written by Diffblue Cover.
  @Test
  public void collectionToCommaDelimitedStringInput0OutputNotNull() {

    // Arrange
    final ArrayList coll = new ArrayList();

    // Act and Assert result
    Assert.assertEquals("", StringUtils.collectionToCommaDelimitedString(coll));
  }

  // Test written by Diffblue Cover.

  @Test
  public void collectionToDelimitedStringInput1NullOutputNotNull() {

    // Arrange
    final ArrayList coll = new ArrayList();
    coll.add(1_413_480_705);
    final String delim = null;

    // Act
    final String actual = StringUtils.collectionToDelimitedString(coll, delim);

    // Assert result
    Assert.assertEquals("1413480705", actual);
  }

  // Test written by Diffblue Cover.

  @Test
  public void collectionToDelimitedStringInput2NotNullOutputNotNull() {

    // Arrange
    final ArrayList coll = new ArrayList();
    coll.add(null);
    coll.add(null);
    final String delim =
        "???????????????????????????????????????????????????????????????????????????????????????????????";

    // Act
    final String actual = StringUtils.collectionToDelimitedString(coll, delim);

    // Assert result
    Assert.assertEquals(
        "null???????????????????????????????????????????????????????????????????????????????????????????????null",
        actual);
  }

  // Test written by Diffblue Cover.

  @Test
  public void commaDelimitedListToStringArrayInputNotNullOutput1() {

    // Arrange
    final String str = "foo";

    // Act
    final String[] actual = StringUtils.commaDelimitedListToStringArray(str);

    // Assert result
    Assert.assertArrayEquals(new String[] {"foo"}, actual);
  }

  // Test written by Diffblue Cover.

  @Test
  public void commaDelimitedListToStringArrayInputNotNullOutput2() {

    // Arrange
    final String str = "-\u802c,nn)!\u802e.M\u9d1c\u9d1c\u6d5em-";

    // Act
    final String[] actual = StringUtils.commaDelimitedListToStringArray(str);

    // Assert result
    Assert.assertArrayEquals(new String[] {"-\u802c", "nn)!\u802e.M\u9d1c\u9d1c\u6d5em-"}, actual);
  }

  // Test written by Diffblue Cover.

  @Test
  public void commaDelimitedListToStringArrayInputNullOutput0() {

    // Arrange
    final String str = null;

    // Act
    final String[] actual = StringUtils.commaDelimitedListToStringArray(str);

    // Assert result
    Assert.assertArrayEquals(new String[] {}, actual);
  }

  // Test written by Diffblue Cover.
  @Test
  public void concatenateStringArraysInput00Output0() {

    // Arrange
    final String[] array1 = {};
    final String[] array2 = {};

    // Act
    final String[] actual = StringUtils.concatenateStringArrays(array1, array2);

    // Assert result
    Assert.assertArrayEquals(new String[] {}, actual);
  }

  // Test written by Diffblue Cover.
  @Test
  public void concatenateStringArraysInput1NullOutput1() {

    // Arrange
    final String[] array1 = {null};

    // Act
    final String[] actual = StringUtils.concatenateStringArrays(array1, null);

    // Assert result
    Assert.assertArrayEquals(new String[] {null}, actual);
  }

  // Test written by Diffblue Cover.
  @Test
  public void containsWhitespaceInputNotNullOutputFalse() {

    // Arrange
    final CharSequence str = "\u0000";

    // Act and Assert result
    Assert.assertFalse(StringUtils.containsWhitespace(str));
  }

  // Test written by Diffblue Cover.
  @Test
  public void containsWhitespaceInputNotNullOutputFalse2() {

    // Arrange
    final CharSequence str = "";

    // Act and Assert result
    Assert.assertFalse(StringUtils.containsWhitespace(str));
  }

  // Test written by Diffblue Cover.
  @Test
  public void containsWhitespaceInputNotNullOutputTrue() {

    // Arrange
    final CharSequence str = "\u0000\u0000\n";

    // Act and Assert result
    Assert.assertTrue(StringUtils.containsWhitespace(str));
  }

  // Test written by Diffblue Cover.
  @Test
  public void containsWhitespaceInputNullOutputFalse() {

    // Act and Assert result
    Assert.assertFalse(StringUtils.containsWhitespace(null));
  }

  // Test written by Diffblue Cover.
  @Test
  public void containsWhitespaceInputNullOutputFalse2() {

    // Act and Assert result
    Assert.assertFalse(StringUtils.containsWhitespace(null));
  }

  // Test written by Diffblue Cover.
  @Test
  public void countOccurrencesOfInputNotNullNotNullOutputPositive() {

    // Act and Assert result
    Assert.assertEquals(1,
                        StringUtils.countOccurrencesOf("=<???????????????????????????????", "=<"));
  }

  // Test written by Diffblue Cover.
  @Test
  public void countOccurrencesOfInputNotNullNotNullOutputZero() {

    // Act and Assert result
    Assert.assertEquals(0, StringUtils.countOccurrencesOf("/", "foo"));
  }

  // Test written by Diffblue Cover.
  @Test
  public void countOccurrencesOfInputNotNullNotNullOutputZero2() {

    // Act and Assert result
    Assert.assertEquals(0, StringUtils.countOccurrencesOf("!!!!!", ""));
  }

  // Test written by Diffblue Cover.
  @Test
  public void countOccurrencesOfInputNotNullNotNullOutputZero3() {

    // Act and Assert result
    Assert.assertEquals(0, StringUtils.countOccurrencesOf("", "!"));
  }

  // Test written by Diffblue Cover.
  @Test
  public void deleteAnyInputNotNullNotNullOutputNotNull() {

    // Act and Assert result
    Assert.assertEquals("\u7fe2", StringUtils.deleteAny("\u7fe0\u7fe2", "\u7fe1\u7fe0"));
  }

  // Test written by Diffblue Cover.
  @Test
  public void deleteAnyInputNullNotNullOutputNull() {

    // Act and Assert result
    Assert.assertNull(StringUtils.deleteAny(null, "\u7fe1\u7fe0"));
  }

  // Test written by Diffblue Cover.
  @Test
  public void deleteInputNotNullNotNullOutputNotNull() {

    // Act and Assert result
    Assert.assertEquals("", StringUtils.delete("", ""));
  }

  // Test written by Diffblue Cover.

  @Test
  public void delimitedListToStringArrayInputNotNullNotNullOutput0() {

    // Arrange
    final String str = "";
    final String delimiter = "";

    // Act
    final String[] actual = StringUtils.delimitedListToStringArray(str, delimiter);

    // Assert result
    Assert.assertArrayEquals(new String[] {}, actual);
  }

  // Test written by Diffblue Cover.

  @Test
  public void delimitedListToStringArrayInputNotNullNotNullOutput1() {

    // Arrange
    final String str = "/";
    final String delimiter = "foo";

    // Act
    final String[] actual = StringUtils.delimitedListToStringArray(str, delimiter);

    // Assert result
    Assert.assertArrayEquals(new String[] {"/"}, actual);
  }

  // Test written by Diffblue Cover.

  @Test
  public void delimitedListToStringArrayInputNotNullNotNullOutput12() {

    // Arrange
    final String str = "\u0000";
    final String delimiter = "";

    // Act
    final String[] actual = StringUtils.delimitedListToStringArray(str, delimiter);

    // Assert result
    Assert.assertArrayEquals(new String[] {"\u0000"}, actual);
  }

  // Test written by Diffblue Cover.

  @Test
  public void delimitedListToStringArrayInputNotNullNullNullOutput1() {

    // Arrange
    final String str = "+77_\'+776";
    final String delimiter = null;
    final String charsToDelete = null;

    // Act
    final String[] actual = StringUtils.delimitedListToStringArray(str, delimiter, charsToDelete);

    // Assert result
    Assert.assertArrayEquals(new String[] {"+77_\'+776"}, actual);
  }

  // Test written by Diffblue Cover.

  @Test
  public void delimitedListToStringArrayInputNotNullNullOutput1() {

    // Arrange
    final String str = "?";
    final String delimiter = null;

    // Act
    final String[] actual = StringUtils.delimitedListToStringArray(str, delimiter);

    // Assert result
    Assert.assertArrayEquals(new String[] {"?"}, actual);
  }

  // Test written by Diffblue Cover.
  @Test
  public void endsWithIgnoreCaseInputNotNullNotNullOutputFalse() {

    // Act and Assert result
    Assert.assertFalse(StringUtils.endsWithIgnoreCase("/", "\'"));
  }

  // Test written by Diffblue Cover.
  @Test
  public void endsWithIgnoreCaseInputNotNullNotNullOutputFalse2() {

    // Act and Assert result
    Assert.assertFalse(StringUtils.endsWithIgnoreCase("foo", "a\'b\'c"));
  }

  // Test written by Diffblue Cover.
  @Test
  public void endsWithIgnoreCaseInputNotNullNotNullOutputTrue() {

    // Act and Assert result
    Assert.assertTrue(StringUtils.endsWithIgnoreCase("/", "/"));
  }

  // Test written by Diffblue Cover.
  @Test
  public void endsWithIgnoreCaseInputNullNullOutputFalse() {

    // Act and Assert result
    Assert.assertFalse(StringUtils.endsWithIgnoreCase(null, null));
  }

  // Test written by Diffblue Cover.
  @Test
  public void getFilenameExtensionInputNotNullOutputNotNull() {

    // Act and Assert result
    Assert.assertEquals(
        "\uffd1\uffd1\uffd1\uffd1\uffd1\uffd1\uffd1\uffd1\uffd1\uffd1\uffd1\uffd1\uffd1\uffd1\uffd1\uffd1\uffd1\uffd1\uffd1\uffd1\uffd1\uffd1\uffd1\uffd1\uffd1\uffd1\uffd1\uffd1\uffd1\uffd1\uffd1\uffd1\uffd1\uffd1\uffd1\uffd1\uffd1\uffd1\uffd1\uffd1\uffd1\uffd1\uffd1\uffd1\uffd1\uffd1\uffd1\uffd1\uffd1\uffd1\uffd1\uffd1\uffd1\uffd1\uffd1\uffd1\uffd1\uffd1\uffd1\uffd1\uffd1\uffd1\uffd1\uffd1\uffd1\uffd1\uffd1\uffd1\uffd1\uffd1\uffd1\uffd1\uffd1\uffd1\uffd1\uffd1\uffd1\uffd1\uffd1\uffd1\uffd1\uffd1\uffd1\uffd1\uffd1\uffd1\uffd1",
        StringUtils.getFilenameExtension(
            "...\uffd1\uffd1\uffd1\uffd1\uffd1\uffd1\uffd1\uffd1\uffd1\uffd1\uffd1\uffd1\uffd1\uffd1\uffd1\uffd1\uffd1\uffd1\uffd1\uffd1\uffd1\uffd1\uffd1\uffd1\uffd1\uffd1\uffd1\uffd1\uffd1\uffd1\uffd1\uffd1\uffd1\uffd1\uffd1\uffd1\uffd1\uffd1\uffd1\uffd1\uffd1\uffd1\uffd1\uffd1\uffd1\uffd1\uffd1\uffd1\uffd1\uffd1\uffd1\uffd1\uffd1\uffd1\uffd1\uffd1\uffd1\uffd1\uffd1\uffd1\uffd1\uffd1\uffd1\uffd1\uffd1\uffd1\uffd1\uffd1\uffd1\uffd1\uffd1\uffd1\uffd1\uffd1\uffd1\uffd1\uffd1\uffd1\uffd1\uffd1\uffd1\uffd1\uffd1\uffd1\uffd1\uffd1\uffd1"));
  }

  // Test written by Diffblue Cover.
  @Test
  public void getFilenameExtensionInputNotNullOutputNull() {

    // Act and Assert result
    Assert.assertNull(StringUtils.getFilenameExtension("foo"));
  }

  // Test written by Diffblue Cover.
  @Test
  public void getFilenameExtensionInputNotNullOutputNull2() {

    // Act and Assert result
    Assert.assertNull(StringUtils.getFilenameExtension(
        "...................////\uffd1\uffd1\uffd1\uffd1\uffd1\uffd1\uffd1\uffd1\uffd1\uffd1\uffd1\uffd1\uffd1\uffd1\uffd1\uffd1\uffd1\uffd1\uffd1\uffd1\uffd1\uffd1\uffd1\uffd1\uffd1\uffd1\uffd1\uffd1\uffd1\uffd1\uffd1\uffd1\uffd1\uffd1\uffd1\uffd1\uffd1\uffd1\uffd1\uffd1\uffd1\uffd1\uffd1\uffd1\uffd1\uffd1\uffd1\uffd1\uffd1\uffd1\uffd1\uffd1\uffd1\uffd1\uffd1\uffd1\uffd1\uffd1\uffd1\uffd1\uffd1\uffd1\uffd1\uffd1\uffd1\uffd1\uffd1"));
  }

  // Test written by Diffblue Cover.
  @Test
  public void getFilenameExtensionInputNullOutputNull() {

    // Act and Assert result
    Assert.assertNull(StringUtils.getFilenameExtension(null));
  }

  // Test written by Diffblue Cover.
  @Test
  public void getFilenameInputNotNullOutputNotNull() {

    // Act and Assert result
    Assert.assertEquals("c", StringUtils.getFilename("a/b/c"));
  }

  // Test written by Diffblue Cover.
  @Test
  public void getFilenameInputNotNullOutputNotNull2() {

    // Act and Assert result
    Assert.assertEquals("foo", StringUtils.getFilename("foo"));
  }

  // Test written by Diffblue Cover.
  @Test
  public void getFilenameInputNullOutputNull() {

    // Act and Assert result
    Assert.assertNull(StringUtils.getFilename(null));
  }

  // Test written by Diffblue Cover.
  @Test
  public void hasLengthInputNotNullOutputFalse() {

    // Arrange
    final CharSequence str = "";

    // Act and Assert result
    Assert.assertFalse(StringUtils.hasLength(str));
  }

  // Test written by Diffblue Cover.
  @Test
  public void hasLengthInputNotNullOutputTrue() {

    // Act and Assert result
    Assert.assertTrue(StringUtils.hasLength("/"));
  }

  // Test written by Diffblue Cover.
  @Test
  public void hasTextInputNotNullOutputFalse() {

    // Arrange
    final CharSequence str = "";

    // Act and Assert result
    Assert.assertFalse(StringUtils.hasText(str));
  }

  // Test written by Diffblue Cover.
  @Test
  public void hasTextInputNotNullOutputFalse2() {

    // Arrange
    final CharSequence str = "\n";

    // Act and Assert result
    Assert.assertFalse(StringUtils.hasText(str));
  }

  // Test written by Diffblue Cover.
  @Test
  public void hasTextInputNotNullOutputTrue() {

    // Act and Assert result
    Assert.assertTrue(StringUtils.hasText("foo"));
  }

  // Test written by Diffblue Cover.
  @Test
  public void hasTextInputNotNullOutputTrue2() {

    // Arrange
    final CharSequence str = "\n\u0000\u0000";

    // Act and Assert result
    Assert.assertTrue(StringUtils.hasText(str));
  }

  // Test written by Diffblue Cover.
  @Test
  public void hasTextInputNullOutputFalse() {

    // Act and Assert result
    Assert.assertFalse(StringUtils.hasText(null));
  }

  // Test written by Diffblue Cover.
  @Test
  public void isEmptyInputNegativeOutputFalse() {

    // Act and Assert result
    Assert.assertFalse(StringUtils.isEmpty(-999_998));
  }

  // Test written by Diffblue Cover.
  @Test
  public void isEmptyInputNullOutputTrue() {

    // Act and Assert result
    Assert.assertTrue(StringUtils.isEmpty(null));
  }

  // Test written by Diffblue Cover.
  @Test
  public void mergeStringArraysInput00Output0() {

    // Arrange
    final String[] array1 = {};
    final String[] array2 = {};

    // Act
    final String[] actual = StringUtils.mergeStringArrays(array1, array2);

    // Assert result
    Assert.assertArrayEquals(new String[] {}, actual);
  }

  // Test written by Diffblue Cover.
  @Test
  public void mergeStringArraysInput1NullOutput1() {

    // Arrange
    final String[] array1 = {null};

    // Act
    final String[] actual = StringUtils.mergeStringArrays(array1, null);

    // Assert result
    Assert.assertArrayEquals(new String[] {null}, actual);
  }

  // Test written by Diffblue Cover.

  @Test
  public void mergeStringArraysInput11Output2() {

    // Arrange
    final String[] array1 = {
        "\u0002\ufffe\ufffe\ufffe\ufffe\ufffe\ufffe\ufffe\ufffe\ufffe\ufffe\ufffe\ufffe\ufffe\ufffe\ufffe\ufffe\ufffe\ufffe\ufffe\ufffe\ufffe\ufffe\ufffe\ufffe\ufffe\ufffe\ufffe\ufffe\ufffe\ufffe\ufffe\ufffe\ufffe\ufffe\ufffe\ufffe\ufffe\ufffe\ufffe"};
    final String[] array2 = {null};

    // Act
    final String[] actual = StringUtils.mergeStringArrays(array1, array2);

    // Assert result
    Assert.assertArrayEquals(
        new String[] {
            "\u0002\ufffe\ufffe\ufffe\ufffe\ufffe\ufffe\ufffe\ufffe\ufffe\ufffe\ufffe\ufffe\ufffe\ufffe\ufffe\ufffe\ufffe\ufffe\ufffe\ufffe\ufffe\ufffe\ufffe\ufffe\ufffe\ufffe\ufffe\ufffe\ufffe\ufffe\ufffe\ufffe\ufffe\ufffe\ufffe\ufffe\ufffe\ufffe\ufffe",
            null},
        actual);
  }

  // Test written by Diffblue Cover.

  @Test
  public void mergeStringArraysInput22Output2() {

    // Arrange
    final String[] array1 = {null, "\u7232"};
    final String[] array2 = {null, "\u7232"};

    // Act
    final String[] actual = StringUtils.mergeStringArrays(array1, array2);

    // Assert result
    Assert.assertArrayEquals(new String[] {null, "\u7232"}, actual);
  }

  // Test written by Diffblue Cover.
  @Test
  public void quoteIfStringInputNotNullOutputNotNull() {

    // Act and Assert result
    Assert.assertEquals("\'/\'", StringUtils.quoteIfString("/"));
  }

  // Test written by Diffblue Cover.
  @Test
  public void quoteInputNotNullOutputNotNull() {

    // Act and Assert result
    Assert.assertEquals("\'/\'", StringUtils.quote("/"));
  }

  // Test written by Diffblue Cover.
  @Test
  public void removeDuplicateStringsInput0Output0() {

    // Arrange
    final String[] array = {};

    // Act
    final String[] actual = StringUtils.removeDuplicateStrings(array);

    // Assert result
    Assert.assertArrayEquals(new String[] {}, actual);
  }

  // Test written by Diffblue Cover.

  @Test
  public void removeDuplicateStringsInput2OutputNullPointerException() {

    // Arrange
    final String[] array = {null, ""};

    // Act
    thrown.expect(NullPointerException.class);
    StringUtils.removeDuplicateStrings(array);

    // The method is not expected to return due to exception thrown
  }

  // Test written by Diffblue Cover.
  @Test
  public void replaceInputNotNullNotNullNotNullOutputNotNull() {

    // Act and Assert result
    Assert.assertEquals("", StringUtils.replace("", "", "00"));
  }

  // Test written by Diffblue Cover.
  @Test
  public void replaceInputNotNullNotNullNotNullOutputNotNull2() {

    // Act and Assert result
    Assert.assertEquals(
        "\u680a\u680a\u680a\u680a\u680a\u680a\u680a\u680a\u680a\u680a\u680a\u680a\u680a\u680a\u680a\u680a\u680a\u680a\u680a\u680a\u680a\u680a\u680a\u680a\u680a\u680a\u680a\u680a\u680a???\u10dc\u10dc\u10dc\u10dc\u10dc\u10dc\u10dc",
        StringUtils.replace(
            "\u680a\u680a\u680a\u680a\u680a\u680a\u680a\u680a\u680a\u680a\u680a\u680a\u680a\u680a\u680a\u680a\u680a\u680a\u680a\u680a\u680a\u680a\u680a\u680a\u680a\u680a\u680a\u680a\u680a\u68fc\u68fc\u68fc\u68fc\u68fc\u68fc\u68fc\u68fc\u68fc\u68fc\u68fc\u68fc\u68fc\u68fc\u68fc\u68fc\u68fc\u68fc\u68fc\u68fc\u68fc\u68fc\u68fc\u68fc\u68fc\u68fc\u68fc\u68fc\u68fc\u68fc\u685c\u685c\u685c\u685c\u685c\u685c\u685c\u685c\u685c\u685c\u685c\u685c\u685c\u685c\u685c\u685c\u685c\u685c\u685c\u685c\u685c\u685c\u685c\u685c\u685c\u685c\u685c\u685c\u685c\u685c\u10dc\u10dc\u10dc\u10dc\u10dc\u10dc\u10dc",
            "\u68fc\u68fc\u68fc\u68fc\u68fc\u68fc\u68fc\u68fc\u68fc\u68fc\u68fc\u68fc\u68fc\u68fc\u68fc\u68fc\u68fc\u68fc\u68fc\u68fc\u68fc\u68fc\u68fc\u68fc\u68fc\u68fc\u68fc\u68fc\u68fc\u68fc\u685c\u685c\u685c\u685c\u685c\u685c\u685c\u685c\u685c\u685c\u685c\u685c\u685c\u685c\u685c\u685c\u685c\u685c\u685c\u685c\u685c\u685c\u685c\u685c\u685c\u685c\u685c\u685c\u685c\u685c",
            "???"));
  }

  // Test written by Diffblue Cover.

  @Test
  public void sortStringArrayInput0Output0() {

    // Arrange
    final String[] array = {};

    // Act
    final String[] actual = StringUtils.sortStringArray(array);

    // Assert result
    Assert.assertArrayEquals(new String[] {}, actual);
  }

  // Test written by Diffblue Cover.

  @Test
  public void sortStringArrayInput1Output1() {

    // Arrange
    final String[] array = {null};

    // Act
    final String[] actual = StringUtils.sortStringArray(array);

    // Assert result
    Assert.assertArrayEquals(new String[] {null}, actual);
  }

  // Test written by Diffblue Cover.
  @Test
  public void splitArrayElementsIntoPropertiesInput0NotNullOutputNull() {

    // Arrange
    final String[] array = {};

    // Act and Assert result
    Assert.assertNull(StringUtils.splitArrayElementsIntoProperties(array, "/"));
  }

  // Test written by Diffblue Cover.
  @Test
  public void splitArrayElementsIntoPropertiesInput1NullNotNullOutput0() {

    // Arrange
    final String[] array = {null};

    // Act
    final Properties actual = StringUtils.splitArrayElementsIntoProperties(array, null, "?");

    // Assert result
    final Properties properties = new Properties();
    Assert.assertEquals(properties, actual);
  }

  // Test written by Diffblue Cover.
  @Test
  public void splitArrayElementsIntoPropertiesInput1NullNullOutput0() {

    // Arrange
    final String[] array = {null};

    // Act
    final Properties actual = StringUtils.splitArrayElementsIntoProperties(array, null, null);

    // Assert result
    final Properties properties = new Properties();
    Assert.assertEquals(properties, actual);
  }

  // Test written by Diffblue Cover.
  @Test
  public void splitArrayElementsIntoPropertiesInput1NullOutput0() {

    // Arrange
    final String[] array = {null};

    // Act
    final Properties actual = StringUtils.splitArrayElementsIntoProperties(array, null);

    // Assert result
    final Properties properties = new Properties();
    Assert.assertEquals(properties, actual);
  }

  // Test written by Diffblue Cover.

  @Test
  public void splitInputNotNullNotNullOutput2() {

    // Arrange
    final String toSplit = "foo";
    final String delimiter = "foo";

    // Act
    final String[] actual = StringUtils.split(toSplit, delimiter);

    // Assert result
    Assert.assertArrayEquals(new String[] {"", ""}, actual);
  }

  // Test written by Diffblue Cover.
  @Test
  public void splitInputNotNullNotNullOutputNull() {

    // Act and Assert result
    Assert.assertNull(StringUtils.split("!!", "0"));
  }

  // Test written by Diffblue Cover.
  @Test
  public void splitInputNotNullNotNullOutputNull2() {

    // Act and Assert result
    Assert.assertNull(StringUtils.split("!!!!", ""));
  }

  // Test written by Diffblue Cover.
  @Test
  public void splitInputNotNullNotNullOutputNull3() {

    // Act and Assert result
    Assert.assertNull(StringUtils.split("", "000000000"));
  }

  // Test written by Diffblue Cover.

  @Test
  public void startsWithIgnoreCaseInputNotNullNotNullOutputFalse() {

    // Arrange
    final String str = "foo";
    final String prefix = "a\'b\'c";

    // Act
    final boolean actual = StringUtils.startsWithIgnoreCase(str, prefix);

    // Assert result
    Assert.assertFalse(actual);
  }

  // Test written by Diffblue Cover.

  @Test
  public void startsWithIgnoreCaseInputNotNullNotNullOutputFalse2() {

    // Arrange
    final String str = "a\'b\'c";
    final String prefix = "foo";

    // Act
    final boolean actual = StringUtils.startsWithIgnoreCase(str, prefix);

    // Assert result
    Assert.assertFalse(actual);
  }

  // Test written by Diffblue Cover.

  @Test
  public void startsWithIgnoreCaseInputNotNullNotNullOutputTrue() {

    // Arrange
    final String str = "\'";
    final String prefix = "\'";

    // Act
    final boolean actual = StringUtils.startsWithIgnoreCase(str, prefix);

    // Assert result
    Assert.assertTrue(actual);
  }

  // Test written by Diffblue Cover.
  @Test
  public void startsWithIgnoreCaseInputNullNullOutputFalse() {

    // Act and Assert result
    Assert.assertFalse(StringUtils.startsWithIgnoreCase(null, null));
  }

  // Test written by Diffblue Cover.
  @Test
  public void stripFilenameExtensionInputNotNullOutputNotNull() {

    // Act and Assert result
    Assert.assertEquals("foo", StringUtils.stripFilenameExtension("foo"));
  }

  // Test written by Diffblue Cover.
  @Test
  public void stripFilenameExtensionInputNotNullOutputNotNull2() {

    // Act and Assert result
    Assert.assertEquals("./", StringUtils.stripFilenameExtension("./"));
  }

  // Test written by Diffblue Cover.
  @Test
  public void stripFilenameExtensionInputNotNullOutputNotNull3() {

    // Act and Assert result
    Assert.assertEquals(
        "//////////////////+,****+++++++++++++++++++++++++++++++++++++++++++++++",
        StringUtils.stripFilenameExtension(
            "//////////////////+,****+++++++++++++++++++++++++++++++++++++++++++++++.,,,,,,,,,,,,,"));
  }

  // Test written by Diffblue Cover.
  @Test
  public void stripFilenameExtensionInputNullOutputNull() {

    // Act and Assert result
    Assert.assertNull(StringUtils.stripFilenameExtension(null));
  }

  // Test written by Diffblue Cover.
  @Test
  public void substringMatchInputNotNullPositiveNotNullOutputFalse() {

    // Arrange
    final CharSequence str = "\uffe0";
    final CharSequence substring = "\u0000";

    // Act and Assert result
    Assert.assertFalse(StringUtils.substringMatch(str, 2, substring));
  }

  // Test written by Diffblue Cover.
  @Test
  public void substringMatchInputNotNullPositiveNotNullOutputFalse2() {

    // Arrange
    final CharSequence str =
        "\uffe0\uffe0\uffe0\uffe0\uffe0\uffe0\uffe0\uffe0\uffe0\uffe0\uffe0\uffe0\uffe0\uffe0\uffe0\uffe0\uffe0\uffe0\uffe0\uffe0\uffe0\uffe0\uffe0\uffe0\uffe0\uffe0\uffe0\uffe0\uffe0\uffe0\uffe0\uffe0\uffe0\uffe0\uffe0\uffe0\uffe0\uffe0";
    final CharSequence substring = "\u0000";

    // Act and Assert result
    Assert.assertFalse(StringUtils.substringMatch(str, 37, substring));
  }

  // Test written by Diffblue Cover.
  @Test
  public void substringMatchInputNotNullPositiveNotNullOutputTrue() {

    // Arrange
    final CharSequence str = "\uffe0";
    final CharSequence substring = "";

    // Act and Assert result
    Assert.assertTrue(StringUtils.substringMatch(str, 2, substring));
  }

  // Test written by Diffblue Cover.
  @Test
  public void substringMatchInputNotNullPositiveNotNullOutputTrue2() {

    // Arrange
    final CharSequence str =
        "\uffe0\uffe0\uffe0\uffe0\uffe0\uffe0\uffe0\uffe0\uffe0\uffe0\uffe0\uffe0\uffe0\uffe0\uffe0\uffe0\uffe0\uffe0\uffe0\uffe0\uffe0\uffe0\uffe0\uffe0\uffe0\uffe0\uffe0\uffe0\uffe0\uffe0\uffe0\uffe0\uffe0\uffe0\uffe0\uffe0\uffe0\uffe0";
    final CharSequence substring = "\uffe0";

    // Act and Assert result
    Assert.assertTrue(StringUtils.substringMatch(str, 37, substring));
  }

  // Test written by Diffblue Cover.
  @Test
  public void tokenizeToStringArrayInputNullNotNullFalseTrueOutputNull() {

    // Act and Assert result
    Assert.assertNull(StringUtils.tokenizeToStringArray(null, "", false, true));
  }

  // Test written by Diffblue Cover.
  @Test
  public void tokenizeToStringArrayInputNullNullOutputNull() {

    // Act and Assert result
    Assert.assertNull(StringUtils.tokenizeToStringArray(null, null));
  }

  // Test written by Diffblue Cover.
  @Test
  public void trimAllWhitespaceInputNotNullOutputNotNull() {

    // Act and Assert result
    Assert.assertEquals("", StringUtils.trimAllWhitespace(""));
  }

  // Test written by Diffblue Cover.
  @Test
  public void trimAllWhitespaceInputNotNullOutputNotNull2() {

    // Act and Assert result
    Assert.assertEquals("\"", StringUtils.trimAllWhitespace("\" "));
  }

  // Test written by Diffblue Cover.

  @Test
  public void trimArrayElementsInput0Output0() {

    // Arrange
    final String[] array = {};

    // Act
    final String[] actual = StringUtils.trimArrayElements(array);

    // Assert result
    Assert.assertArrayEquals(new String[] {}, actual);
  }

  // Test written by Diffblue Cover.

  @Test
  public void trimArrayElementsInput2Output2() {

    // Arrange
    final String[] array = {
        null,
        "\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u000e\u000b\u000b\u000b\u000b\u000b\u000b\u000b\u000b\u000b\u000b\u000b\u000b\u000b\u000b\u000b\u000b\u000b\u000b\u000b\u000b\u000b\u000b\u000b\u000b\u000b\u000b\u000b\u000b\u000b\u000b\u000b"};

    // Act
    final String[] actual = StringUtils.trimArrayElements(array);

    // Assert result
    Assert.assertArrayEquals(new String[] {null, ""}, actual);
  }

  // Test written by Diffblue Cover.
  @Test
  public void trimLeadingCharacterInputNotNullNotNullOutputNotNull() {

    // Act and Assert result
    Assert.assertEquals("foo", StringUtils.trimLeadingCharacter("foo", '!'));
  }

  // Test written by Diffblue Cover.
  @Test
  public void trimLeadingCharacterInputNotNullNotNullOutputNotNull2() {

    // Act and Assert result
    Assert.assertEquals("", StringUtils.trimLeadingCharacter("\u0000", '\u0000'));
  }

  // Test written by Diffblue Cover.
  @Test
  public void trimLeadingCharacterInputNullNotNullOutputNull() {

    // Act and Assert result
    Assert.assertNull(StringUtils.trimLeadingCharacter(null, '\u0000'));
  }

  // Test written by Diffblue Cover.
  @Test
  public void trimLeadingWhitespaceInputNotNullOutputNotNull() {

    // Act and Assert result
    Assert.assertEquals("foo", StringUtils.trimLeadingWhitespace("foo"));
  }

  // Test written by Diffblue Cover.
  @Test
  public void trimLeadingWhitespaceInputNotNullOutputNotNull2() {

    // Act and Assert result
    Assert.assertEquals("", StringUtils.trimLeadingWhitespace(""));
  }

  // Test written by Diffblue Cover.
  @Test
  public void trimLeadingWhitespaceInputNotNullOutputNotNull3() {

    // Act and Assert result
    Assert.assertEquals("", StringUtils.trimLeadingWhitespace("\n"));
  }

  // Test written by Diffblue Cover.
  @Test
  public void trimTrailingCharacterInputNotNullAOutputNotNull() {

    // Act and Assert result
    Assert.assertEquals("AAAAAAAAAAAAAAAAAAA@",
                        StringUtils.trimTrailingCharacter("AAAAAAAAAAAAAAAAAAA@A", 'A'));
  }

  // Test written by Diffblue Cover.
  @Test
  public void trimTrailingCharacterInputNotNullAOutputNotNull2() {

    // Act and Assert result
    Assert.assertEquals("", StringUtils.trimTrailingCharacter("A", 'A'));
  }

  // Test written by Diffblue Cover.
  @Test
  public void trimTrailingCharacterInputNotNullNotNullOutputNotNull() {

    // Act and Assert result
    Assert.assertEquals("foo", StringUtils.trimTrailingCharacter("foo", '!'));
  }

  // Test written by Diffblue Cover.
  @Test
  public void trimTrailingCharacterInputNullNotNullOutputNull() {

    // Act and Assert result
    Assert.assertNull(StringUtils.trimTrailingCharacter(null, '\uffe1'));
  }

  // Test written by Diffblue Cover.
  @Test
  public void trimTrailingWhitespaceInputNotNullOutputNotNull() {

    // Act and Assert result
    Assert.assertEquals("\'", StringUtils.trimTrailingWhitespace("\'"));
  }

  // Test written by Diffblue Cover.
  @Test
  public void trimTrailingWhitespaceInputNotNullOutputNotNull2() {

    // Act and Assert result
    Assert.assertEquals("", StringUtils.trimTrailingWhitespace(" "));
  }

  // Test written by Diffblue Cover.
  @Test
  public void trimTrailingWhitespaceInputNullOutputNull() {

    // Act and Assert result
    Assert.assertNull(StringUtils.trimTrailingWhitespace(null));
  }

  // Test written by Diffblue Cover.

  @Test
  public void trimWhitespaceInputNotNullOutputNotNull() {

    // Arrange
    final String str = "/";

    // Act
    final String actual = StringUtils.trimWhitespace(str);

    // Assert result
    Assert.assertEquals("/", actual);
  }

  // Test written by Diffblue Cover.
  @Test
  public void trimWhitespaceInputNotNullOutputNotNull2() {

    // Act and Assert result
    Assert.assertEquals("", StringUtils.trimWhitespace("\u2029"));
  }

  // Test written by Diffblue Cover.
  @Test
  public void trimWhitespaceInputNotNullOutputNotNull3() {

    // Act and Assert result
    Assert.assertEquals("\b", StringUtils.trimWhitespace("\b\n"));
  }

  // Test written by Diffblue Cover.
  @Test
  public void trimWhitespaceInputNullOutputNull() {

    // Act and Assert result
    Assert.assertNull(StringUtils.trimWhitespace(null));
  }

  // Test written by Diffblue Cover.
  @Test
  public void uncapitalizeInputNotNullOutputNotNull6() {

    // Act and Assert result
    Assert.assertEquals("", StringUtils.uncapitalize(""));
  }

  // Test written by Diffblue Cover.
  @Test
  public void uncapitalizeInputNullOutputNull() {

    // Act and Assert result
    Assert.assertNull(StringUtils.uncapitalize(null));
  }

  // Test written by Diffblue Cover.
  @Test
  public void unqualifyInputNotNullNotNullOutputNotNull() {

    // Act and Assert result
    Assert.assertEquals("", StringUtils.unqualify("/", '/'));
  }

  // Test written by Diffblue Cover.
  @Test
  public void unqualifyInputNotNullOutputNotNull() {

    // Act and Assert result
    Assert.assertEquals("\'", StringUtils.unqualify("\'"));
  }
}
