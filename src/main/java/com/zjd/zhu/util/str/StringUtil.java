package com.zjd.zhu.util.str;

import java.util.Random;

public class StringUtil {
  private static volatile Random randGen = null;
  private static char[] numbersAndLetters = null;
  private static Object initLock = new Object();

  public static boolean isEmpty(String str) {
    return str == null || str.length() == 0;
  }

  public static final String randomString(int length) {

    if (length < 1) {
      return null;
    }
    if (randGen == null) {
      synchronized (initLock) {
        if (randGen == null) {
          randGen = new Random();
          numbersAndLetters =
              ("0123456789abcdefghijklmnopqrstuvwxyz" + "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ")
                  .toCharArray();
          // numbersAndLetters =
          // ("0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ").toCharArray();
        }
      }
    }
    char[] randBuffer = new char[length];
    for (int i = 0; i < randBuffer.length; i++) {
      randBuffer[i] = numbersAndLetters[randGen.nextInt(71)];
      // randBuffer[i] = numbersAndLetters[randGen.nextInt(35)];
    }
    return new String(randBuffer);
  }

  public static final boolean isNotBlank(String string) {
    return !(isBlank(string));
  }

  /**
   * Determine Whether the Sting is <code>null</code> or <code>""</code> or only containing blank
   * character
   * <p>
   * Examples: <blockquote>
   * 
   * <pre>
   * StringUtil.isBlank(null) == true
   * StringUtil.isBlank("") == true
   * StringUtil.isBlank("  ") == true
   * StringUtil.isBlank("Best") == false
   * StringUtil.isBlank("  Best  ") == false
   * </pre>
   * 
   * </blockquote>
   * 
   * @param string the string for testing
   * 
   * @return If it is blank, return true
   */
  public static final boolean isBlank(String string) {
    if (string == null) {
      return true;
    }
    if (string.length() == 0) {
      return true;
    }
    for (int i = 0; i < string.length(); i++) {
      if (!Character.isWhitespace(string.charAt(i))) {
        return false;
      }
    }
    return true;
  }

  public static final String capitalizeFirstLetter(String sentence) {
    if (sentence == null) {
      return null;
    }
    StringBuffer result = new StringBuffer();
    String[] words = sentence.split(" ");
    for (String word : words) {
      result.append(word.substring(0, 1).toUpperCase());
      result.append(word.substring(1));
      result.append(" ");
    }
    return result.toString();
  }
}
