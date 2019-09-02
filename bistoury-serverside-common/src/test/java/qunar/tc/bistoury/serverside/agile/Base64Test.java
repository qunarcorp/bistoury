package qunar.tc.bistoury.serverside.agile;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.Timeout;
import qunar.tc.bistoury.serverside.agile.Base64;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;

public class Base64Test {

  @Rule public final ExpectedException thrown = ExpectedException.none();

  @Rule public final Timeout globalTimeout = new Timeout(10000);

  // Test written by Diffblue Cover.

  @Test
  public void decodeInput1OutputUnsupportedEncodingException() throws UnsupportedEncodingException {

    // Arrange
    final byte[] source = {(byte)-67};

    // Act
    thrown.expect(UnsupportedEncodingException.class);
    Base64.decode(source);

    // The method is not expected to return due to exception thrown
  }

  // Test written by Diffblue Cover.

  @Test
  public void decodeInput3OutputUnsupportedEncodingException() throws UnsupportedEncodingException {

    // Arrange
    final byte[] source = {(byte)51, (byte)61, (byte)53};

    // Act
    thrown.expect(UnsupportedEncodingException.class);
    Base64.decode(source);

    // The method is not expected to return due to exception thrown
  }

  // Test written by Diffblue Cover.

  @Test
  public void decodeInput3PositivePositive1OutputUnsupportedEncodingException()
      throws UnsupportedEncodingException {

    // Arrange
    final byte[] source = {(byte)-127, (byte)-127, (byte)-128};
    final int off = 2;
    final int len = 131_072;
    final byte[] decodabet = {(byte)-124};

    // Act
    thrown.expect(UnsupportedEncodingException.class);
    Base64.decode(source, off, len, decodabet);

    // The method is not expected to return due to exception thrown
  }

  // Test written by Diffblue Cover.

  @Test
  public void decodeInput9PositivePositiveOutputUnsupportedEncodingException()
      throws UnsupportedEncodingException {

    // Arrange
    final byte[] source = {(byte)93, (byte)93, (byte)92, (byte)93, (byte)93,
                           (byte)93, (byte)93, (byte)93, (byte)92};
    final int off = 8;
    final int len = 24_569;

    // Act
    thrown.expect(UnsupportedEncodingException.class);
    Base64.decode(source, off, len);

    // The method is not expected to return due to exception thrown
  }

  // Test written by Diffblue Cover.

  @Test
  public void decodeInput10PositivePositiveOutputUnsupportedEncodingException()
      throws UnsupportedEncodingException {

    // Arrange
    final byte[] source = {(byte)60, (byte)60, (byte)61, (byte)61, (byte)61,
                           (byte)60, (byte)60, (byte)60, (byte)61, (byte)60};
    final int off = 2;
    final int len = 8;

    // Act
    thrown.expect(UnsupportedEncodingException.class);
    Base64.decode(source, off, len);

    // The method is not expected to return due to exception thrown
  }

  // Test written by Diffblue Cover.

  @Test
  public void decodeInput12OutputUnsupportedEncodingException()
      throws UnsupportedEncodingException {

    // Arrange
    final byte[] source = {(byte)51, (byte)55, (byte)61, (byte)125, (byte)60, (byte)60,
                           (byte)60, (byte)60, (byte)60, (byte)60,  (byte)60, (byte)125};

    // Act
    thrown.expect(UnsupportedEncodingException.class);
    Base64.decode(source);

    // The method is not expected to return due to exception thrown
  }

  // Test written by Diffblue Cover.
  @Test
  public void decodeInput23PositivePositive27OutputUnsupportedEncodingException()
      throws UnsupportedEncodingException {

    // Arrange
    final byte[] source = {(byte)0,  (byte)69,   (byte)0, (byte)7, (byte)2, (byte)3,
                           (byte)0,  (byte)7,    (byte)0, (byte)0, (byte)0, (byte)0,
                           (byte)32, (byte)0,    (byte)4, (byte)5, (byte)1, (byte)7,
                           (byte)9,  (byte)-123, (byte)9, (byte)5, (byte)0};
    final byte[] decodabet = {(byte)17,  (byte)113, (byte)16, (byte)16,  (byte)-3, (byte)-11,
                              (byte)-67, (byte)-37, (byte)89, (byte)-11, (byte)16, (byte)16,
                              (byte)17,  (byte)25,  (byte)21, (byte)21,  (byte)80, (byte)16,
                              (byte)16,  (byte)19,  (byte)1,  (byte)16,  (byte)25, (byte)21,
                              (byte)25,  (byte)16,  (byte)16};

    // Act
    thrown.expect(UnsupportedEncodingException.class);
    Base64.decode(source, 14, 2, decodabet);

    // The method is not expected to return due to exception thrown
  }

  // Test written by Diffblue Cover.
  @Test
  public void decodeInput23PositivePositive27OutputUnsupportedEncodingException2()
      throws UnsupportedEncodingException {

    // Arrange
    final byte[] source = {(byte)0,  (byte)69,   (byte)0, (byte)7, (byte)2, (byte)3,
                           (byte)0,  (byte)7,    (byte)0, (byte)0, (byte)0, (byte)0,
                           (byte)32, (byte)0,    (byte)4, (byte)5, (byte)1, (byte)7,
                           (byte)9,  (byte)-123, (byte)9, (byte)5, (byte)0};
    final byte[] decodabet = {(byte)65,  (byte)93,  (byte)100, (byte)64,  (byte)-3, (byte)69,
                              (byte)-91, (byte)-37, (byte)9,   (byte)-91, (byte)68, (byte)76,
                              (byte)93,  (byte)73,  (byte)77,  (byte)69,  (byte)0,  (byte)36,
                              (byte)64,  (byte)67,  (byte)81,  (byte)64,  (byte)73, (byte)69,
                              (byte)73,  (byte)0,   (byte)0};

    // Act
    thrown.expect(UnsupportedEncodingException.class);
    Base64.decode(source, 14, 2, decodabet);

    // The method is not expected to return due to exception thrown
  }

  // Test written by Diffblue Cover.

  @Test
  public void decodeInput30PositivePositiveOutputUnsupportedEncodingException()
      throws UnsupportedEncodingException {

    // Arrange
    final byte[] source = {(byte)-3,  (byte)-14, (byte)-118, (byte)-117, (byte)61,   (byte)60,
                           (byte)-67, (byte)-14, (byte)115,  (byte)61,   (byte)10,   (byte)60,
                           (byte)58,  (byte)-67, (byte)62,   (byte)-119, (byte)60,   (byte)10,
                           (byte)-3,  (byte)63,  (byte)-120, (byte)62,   (byte)50,   (byte)-13,
                           (byte)-6,  (byte)61,  (byte)60,   (byte)45,   (byte)-117, (byte)51};
    final int off = 23;
    final int len = 6;

    // Act
    thrown.expect(UnsupportedEncodingException.class);
    Base64.decode(source, off, len);

    // The method is not expected to return due to exception thrown
  }

  // Test written by Diffblue Cover.
  @Test
  public void decodeInputNotNullOutputUnsupportedEncodingException()
      throws UnsupportedEncodingException {

    // Act
    thrown.expect(UnsupportedEncodingException.class);
    Base64.decode("#{");

    // The method is not expected to return due to exception thrown
  }

  // Test written by Diffblue Cover.
  @Test
  public void decodeInputNotNullOutputUnsupportedEncodingException2()
      throws UnsupportedEncodingException {

    // Act
    thrown.expect(UnsupportedEncodingException.class);
    Base64.decode("\n\u0000");

    // The method is not expected to return due to exception thrown
  }

  // Test written by Diffblue Cover.
  @Test
  public void decodeInputNotNullOutputUnsupportedEncodingException3()
      throws UnsupportedEncodingException {

    // Act
    thrown.expect(UnsupportedEncodingException.class);
    Base64.decode("\n=");

    // The method is not expected to return due to exception thrown
  }

  // Test written by Diffblue Cover.
  @Test
  public void decodeInputNotNullOutputUnsupportedEncodingException4()
      throws UnsupportedEncodingException {

    // Act
    thrown.expect(UnsupportedEncodingException.class);
    Base64.decode("5=");

    // The method is not expected to return due to exception thrown
  }

  // Test written by Diffblue Cover.
  @Test
  public void decodeInputNotNullOutputUnsupportedEncodingException5()
      throws UnsupportedEncodingException {

    // Act
    thrown.expect(UnsupportedEncodingException.class);
    Base64.decode("5\t");

    // The method is not expected to return due to exception thrown
  }

  // Test written by Diffblue Cover.

  @Test
  public void decodeWebSafeInput1OutputUnsupportedEncodingException()
      throws UnsupportedEncodingException {

    // Arrange
    final byte[] source = {(byte)-67};

    // Act
    thrown.expect(UnsupportedEncodingException.class);
    Base64.decodeWebSafe(source);

    // The method is not expected to return due to exception thrown
  }

  // Test written by Diffblue Cover.

  @Test
  public void decodeWebSafeInput3OutputUnsupportedEncodingException()
      throws UnsupportedEncodingException {

    // Arrange
    final byte[] source = {(byte)68, (byte)10, (byte)61};

    // Act
    thrown.expect(UnsupportedEncodingException.class);
    Base64.decodeWebSafe(source);

    // The method is not expected to return due to exception thrown
  }

  // Test written by Diffblue Cover.

  @Test
  public void decodeWebSafeInput9PositivePositiveOutputUnsupportedEncodingException()
      throws UnsupportedEncodingException {

    // Arrange
    final byte[] source = {(byte)127, (byte)127, (byte)126, (byte)127, (byte)127,
                           (byte)127, (byte)127, (byte)127, (byte)126};
    final int off = 8;
    final int len = 8193;

    // Act
    thrown.expect(UnsupportedEncodingException.class);
    Base64.decodeWebSafe(source, off, len);

    // The method is not expected to return due to exception thrown
  }

  // Test written by Diffblue Cover.

  @Test
  public void decodeWebSafeInput9PositivePositiveOutputUnsupportedEncodingException2()
      throws UnsupportedEncodingException {

    // Arrange
    final byte[] source = {(byte)60, (byte)60, (byte)61, (byte)60, (byte)61,
                           (byte)60, (byte)60, (byte)60, (byte)61};
    final int off = 4;
    final int len = 4;

    // Act
    thrown.expect(UnsupportedEncodingException.class);
    Base64.decodeWebSafe(source, off, len);

    // The method is not expected to return due to exception thrown
  }

  // Test written by Diffblue Cover.

  @Test
  public void decodeWebSafeInput19OutputUnsupportedEncodingException()
      throws UnsupportedEncodingException {

    // Arrange
    final byte[] source = {(byte)98,  (byte)51, (byte)61,  (byte)-4, (byte)60, (byte)60,  (byte)60,
                           (byte)-19, (byte)60, (byte)127, (byte)60, (byte)60, (byte)-67, (byte)62,
                           (byte)-11, (byte)60, (byte)60,  (byte)-3, (byte)-3};

    // Act
    thrown.expect(UnsupportedEncodingException.class);
    Base64.decodeWebSafe(source);

    // The method is not expected to return due to exception thrown
  }

  // Test written by Diffblue Cover.

  @Test
  public void decodeWebSafeInput30PositivePositiveOutputUnsupportedEncodingException()
      throws UnsupportedEncodingException {

    // Arrange
    final byte[] source = {(byte)-72, (byte)-68, (byte)56,  (byte)88,  (byte)-68, (byte)88,
                           (byte)-68, (byte)-71, (byte)28,  (byte)-68, (byte)-68, (byte)-68,
                           (byte)-87, (byte)-68, (byte)-68, (byte)56,  (byte)-68, (byte)88,
                           (byte)32,  (byte)-67, (byte)-67, (byte)-65, (byte)57,  (byte)-67,
                           (byte)-68, (byte)56,  (byte)57,  (byte)33,  (byte)56,  (byte)57};
    final int off = 17;
    final int len = 10;

    // Act
    thrown.expect(UnsupportedEncodingException.class);
    Base64.decodeWebSafe(source, off, len);

    // The method is not expected to return due to exception thrown
  }

  // Test written by Diffblue Cover.

  @Test
  public void decodeWebSafeInput30PositivePositiveOutputUnsupportedEncodingException2()
      throws UnsupportedEncodingException {

    // Arrange
    final byte[] source = {(byte)-72, (byte)-68, (byte)56,  (byte)88,  (byte)-68, (byte)88,
                           (byte)-68, (byte)-71, (byte)28,  (byte)-68, (byte)-68, (byte)-68,
                           (byte)-87, (byte)-68, (byte)-68, (byte)56,  (byte)-68, (byte)88,
                           (byte)57,  (byte)-67, (byte)-67, (byte)-65, (byte)57,  (byte)-67,
                           (byte)-68, (byte)56,  (byte)57,  (byte)33,  (byte)56,  (byte)57};
    final int off = 17;
    final int len = 10;

    // Act
    thrown.expect(UnsupportedEncodingException.class);
    Base64.decodeWebSafe(source, off, len);

    // The method is not expected to return due to exception thrown
  }

  // Test written by Diffblue Cover.
  @Test
  public void decodeWebSafeInputNotNullOutputUnsupportedEncodingException()
      throws UnsupportedEncodingException {

    // Act
    thrown.expect(UnsupportedEncodingException.class);
    Base64.decodeWebSafe("#{");

    // The method is not expected to return due to exception thrown
  }

  // Test written by Diffblue Cover.

  @Test
  public void decodeWebSafeInputNotNullOutputUnsupportedEncodingException3()
      throws UnsupportedEncodingException {

    // Arrange
    final String s = "\u2064=";

    // Act
    thrown.expect(UnsupportedEncodingException.class);
    Base64.decodeWebSafe(s);

    // The method is not expected to return due to exception thrown
  }

  // Test written by Diffblue Cover.

  @Test
  public void decodeWebSafeInputNotNullOutputUnsupportedEncodingException4()
      throws UnsupportedEncodingException {

    // Arrange
    final String s = "\u202d";

    // Act
    thrown.expect(UnsupportedEncodingException.class);
    Base64.decodeWebSafe(s);

    // The method is not expected to return due to exception thrown
  }

  // Test written by Diffblue Cover.

  @Test
  public void encodeInput0OutputNotNull() {

    // Arrange
    final byte[] source = {};

    // Act
    final String actual = Base64.encode(source);

    // Assert result
    Assert.assertEquals("", actual);
  }

  // Test written by Diffblue Cover.

  @Test
  public void encodeInput7PositivePositive9PositiveOutput4() {

    // Arrange
    final byte[] source = {(byte)1, (byte)77, (byte)8, (byte)16, (byte)24, (byte)24, (byte)1};
    final int off = 5;
    final int len = 2;
    final byte[] alphabet = {(byte)10, (byte)11, (byte)11, (byte)11, (byte)0,
                             (byte)10, (byte)0,  (byte)11, (byte)1};
    final int maxLineLength = 6;

    // Act
    final byte[] actual = Base64.encode(source, off, len, alphabet, maxLineLength);

    // Assert result
    Assert.assertArrayEquals(new byte[] {(byte)0, (byte)10, (byte)0, (byte)61}, actual);
  }

  // Test written by Diffblue Cover.
  @Test
  public void encodeInput7PositivePositive9PositiveOutput5() {

    // Arrange
    final byte[] source = {(byte)1, (byte)77, (byte)8, (byte)16, (byte)24, (byte)24, (byte)1};
    final byte[] alphabet = {(byte)42, (byte)43, (byte)11, (byte)43, (byte)0,
                             (byte)74, (byte)0,  (byte)43, (byte)1};

    // Act
    final byte[] actual = Base64.encode(source, 5, 2, alphabet, 4);

    // Assert result
    Assert.assertArrayEquals(new byte[] {(byte)0, (byte)42, (byte)0, (byte)61, (byte)10}, actual);
  }

  // Test written by Diffblue Cover.

  @Test
  public void encodeInput17PositivePositive25PositiveOutput13() {

    // Arrange
    final byte[] source = {(byte)119, (byte)74,  (byte)114, (byte)-30, (byte)99, (byte)65,
                           (byte)-80, (byte)-55, (byte)34,  (byte)66,  (byte)24, (byte)32,
                           (byte)72,  (byte)40,  (byte)115, (byte)75,  (byte)81};
    final int off = 10;
    final int len = 7;
    final byte[] alphabet = {(byte)0,    (byte)1,    (byte)-125, (byte)2,    (byte)0,
                             (byte)-128, (byte)1,    (byte)-128, (byte)1,    (byte)1,
                             (byte)0,    (byte)-128, (byte)16,   (byte)2,    (byte)1,
                             (byte)1,    (byte)-125, (byte)1,    (byte)-127, (byte)1,
                             (byte)0,    (byte)3,    (byte)1,    (byte)3,    (byte)32};
    final int maxLineLength = 8;

    // Act
    final byte[] actual = Base64.encode(source, off, len, alphabet, maxLineLength);

    // Assert result
    Assert.assertArrayEquals(new byte[] {(byte)1, (byte)-125, (byte)1, (byte)1, (byte)0, (byte)-128,
                                         (byte)2, (byte)-128, (byte)10, (byte)0, (byte)-125,
                                         (byte)61, (byte)61},
                             actual);
  }

  // Test written by Diffblue Cover.

  @Test
  public void encodeInput28PositivePositive29FalseOutputNotNull() {

    // Arrange
    final byte[] source = {(byte)79, (byte)51,  (byte)63, (byte)37, (byte)49, (byte)17, (byte)37,
                           (byte)81, (byte)119, (byte)36, (byte)21, (byte)39, (byte)50, (byte)49,
                           (byte)-9, (byte)39,  (byte)13, (byte)2,  (byte)98, (byte)35, (byte)87,
                           (byte)77, (byte)35,  (byte)6,  (byte)97, (byte)52, (byte)97, (byte)39};
    final int off = 21;
    final int len = 3;
    final byte[] alphabet = {(byte)-71,  (byte)-90, (byte)44,  (byte)-83,  (byte)32,   (byte)44,
                             (byte)61,   (byte)13,  (byte)-80, (byte)-127, (byte)-68,  (byte)-83,
                             (byte)61,   (byte)-96, (byte)-77, (byte)-104, (byte)57,   (byte)44,
                             (byte)24,   (byte)0,   (byte)13,  (byte)-119, (byte)-103, (byte)-88,
                             (byte)-127, (byte)-88, (byte)-72, (byte)-96,  (byte)-102};
    final boolean doPadding = false;

    // Act
    final String actual = Base64.encode(source, off, len, alphabet, doPadding);

    // Assert result
    Assert.assertEquals("\u0000\u0018", actual);
  }

  // Test written by Diffblue Cover.

  @Test
  public void encodeWebSafeInput0FalseOutputNotNull() {

    // Arrange
    final byte[] source = {};
    final boolean doPadding = false;

    // Act
    final String actual = Base64.encodeWebSafe(source, doPadding);

    // Assert result
    Assert.assertEquals("", actual);
  }
}
