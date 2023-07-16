package org.apache.commons.lang3;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.text.Normalizer;
import java.text.Normalizer.Form;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;
import java.util.regex.Pattern;

public class StringUtils {
   private static final int STRING_BUILDER_SIZE = 256;
   public static final String SPACE = " ";
   public static final String EMPTY = "";
   public static final String LF = "\n";
   public static final String CR = "\r";
   public static final int INDEX_NOT_FOUND = -1;
   private static final int PAD_LIMIT = 8192;
   private static final Pattern STRIP_ACCENTS_PATTERN = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");

   public static String abbreviate(String str, int maxWidth) {
      return abbreviate(str, "...", 0, maxWidth);
   }

   public static String abbreviate(String str, int offset, int maxWidth) {
      return abbreviate(str, "...", offset, maxWidth);
   }

   public static String abbreviate(String str, String abbrevMarker, int maxWidth) {
      return abbreviate(str, abbrevMarker, 0, maxWidth);
   }

   public static String abbreviate(String str, String abbrevMarker, int offset, int maxWidth) {
      if (isNotEmpty(str) && "".equals(abbrevMarker) && maxWidth > 0) {
         return substring(str, 0, maxWidth);
      } else if (isAnyEmpty(str, abbrevMarker)) {
         return str;
      } else {
         int abbrevMarkerLength = abbrevMarker.length();
         int minAbbrevWidth = abbrevMarkerLength + 1;
         int minAbbrevWidthOffset = abbrevMarkerLength + abbrevMarkerLength + 1;
         if (maxWidth < minAbbrevWidth) {
            throw new IllegalArgumentException(String.format("Minimum abbreviation width is %d", minAbbrevWidth));
         } else if (str.length() <= maxWidth) {
            return str;
         } else {
            if (offset > str.length()) {
               offset = str.length();
            }

            if (str.length() - offset < maxWidth - abbrevMarkerLength) {
               offset = str.length() - (maxWidth - abbrevMarkerLength);
            }

            if (offset <= abbrevMarkerLength + 1) {
               return str.substring(0, maxWidth - abbrevMarkerLength) + abbrevMarker;
            } else if (maxWidth < minAbbrevWidthOffset) {
               throw new IllegalArgumentException(String.format("Minimum abbreviation width with offset is %d", minAbbrevWidthOffset));
            } else {
               return offset + maxWidth - abbrevMarkerLength < str.length() ? abbrevMarker + abbreviate(str.substring(offset), abbrevMarker, maxWidth - abbrevMarkerLength) : abbrevMarker + str.substring(str.length() - (maxWidth - abbrevMarkerLength));
            }
         }
      }
   }

   public static String abbreviateMiddle(String str, String middle, int length) {
      if (!isAnyEmpty(str, middle) && length < str.length() && length >= middle.length() + 2) {
         int targetSting = length - middle.length();
         int startOffset = targetSting / 2 + targetSting % 2;
         int endOffset = str.length() - targetSting / 2;
         return str.substring(0, startOffset) + middle + str.substring(endOffset);
      } else {
         return str;
      }
   }

   private static String appendIfMissing(String str, CharSequence suffix, boolean ignoreCase, CharSequence... suffixes) {
      if (str != null && !isEmpty(suffix) && !endsWith(str, suffix, ignoreCase)) {
         if (ArrayUtils.isNotEmpty((Object[])suffixes)) {
            CharSequence[] var4 = suffixes;
            int var5 = suffixes.length;

            for(int var6 = 0; var6 < var5; ++var6) {
               CharSequence s = var4[var6];
               if (endsWith(str, s, ignoreCase)) {
                  return str;
               }
            }
         }

         return str + suffix.toString();
      } else {
         return str;
      }
   }

   public static String appendIfMissing(String str, CharSequence suffix, CharSequence... suffixes) {
      return appendIfMissing(str, suffix, false, suffixes);
   }

   public static String appendIfMissingIgnoreCase(String str, CharSequence suffix, CharSequence... suffixes) {
      return appendIfMissing(str, suffix, true, suffixes);
   }

   public static String capitalize(String str) {
      int strLen = length(str);
      if (strLen == 0) {
         return str;
      } else {
         int firstCodepoint = str.codePointAt(0);
         int newCodePoint = Character.toTitleCase(firstCodepoint);
         if (firstCodepoint == newCodePoint) {
            return str;
         } else {
            int[] newCodePoints = new int[strLen];
            int outOffset = 0;
            int outOffset = outOffset + 1;
            newCodePoints[outOffset] = newCodePoint;

            int codepoint;
            for(int inOffset = Character.charCount(firstCodepoint); inOffset < strLen; inOffset += Character.charCount(codepoint)) {
               codepoint = str.codePointAt(inOffset);
               newCodePoints[outOffset++] = codepoint;
            }

            return new String(newCodePoints, 0, outOffset);
         }
      }
   }

   public static String center(String str, int size) {
      return center(str, size, ' ');
   }

   public static String center(String str, int size, char padChar) {
      if (str != null && size > 0) {
         int strLen = str.length();
         int pads = size - strLen;
         if (pads <= 0) {
            return str;
         } else {
            str = leftPad(str, strLen + pads / 2, padChar);
            str = rightPad(str, size, padChar);
            return str;
         }
      } else {
         return str;
      }
   }

   public static String center(String str, int size, String padStr) {
      if (str != null && size > 0) {
         if (isEmpty(padStr)) {
            padStr = " ";
         }

         int strLen = str.length();
         int pads = size - strLen;
         if (pads <= 0) {
            return str;
         } else {
            str = leftPad(str, strLen + pads / 2, padStr);
            str = rightPad(str, size, padStr);
            return str;
         }
      } else {
         return str;
      }
   }

   public static String chomp(String str) {
      if (isEmpty(str)) {
         return str;
      } else if (str.length() == 1) {
         char ch = str.charAt(0);
         return ch != '\r' && ch != '\n' ? str : "";
      } else {
         int lastIdx = str.length() - 1;
         char last = str.charAt(lastIdx);
         if (last == '\n') {
            if (str.charAt(lastIdx - 1) == '\r') {
               --lastIdx;
            }
         } else if (last != '\r') {
            ++lastIdx;
         }

         return str.substring(0, lastIdx);
      }
   }

   /** @deprecated */
   @Deprecated
   public static String chomp(String str, String separator) {
      return removeEnd(str, separator);
   }

   public static String chop(String str) {
      if (str == null) {
         return null;
      } else {
         int strLen = str.length();
         if (strLen < 2) {
            return "";
         } else {
            int lastIdx = strLen - 1;
            String ret = str.substring(0, lastIdx);
            char last = str.charAt(lastIdx);
            return last == '\n' && ret.charAt(lastIdx - 1) == '\r' ? ret.substring(0, lastIdx - 1) : ret;
         }
      }
   }

   public static int compare(String str1, String str2) {
      return compare(str1, str2, true);
   }

   public static int compare(String str1, String str2, boolean nullIsLess) {
      if (str1 == str2) {
         return 0;
      } else if (str1 == null) {
         return nullIsLess ? -1 : 1;
      } else if (str2 == null) {
         return nullIsLess ? 1 : -1;
      } else {
         return str1.compareTo(str2);
      }
   }

   public static int compareIgnoreCase(String str1, String str2) {
      return compareIgnoreCase(str1, str2, true);
   }

   public static int compareIgnoreCase(String str1, String str2, boolean nullIsLess) {
      if (str1 == str2) {
         return 0;
      } else if (str1 == null) {
         return nullIsLess ? -1 : 1;
      } else if (str2 == null) {
         return nullIsLess ? 1 : -1;
      } else {
         return str1.compareToIgnoreCase(str2);
      }
   }

   public static boolean contains(CharSequence seq, CharSequence searchSeq) {
      if (seq != null && searchSeq != null) {
         return CharSequenceUtils.indexOf(seq, searchSeq, 0) >= 0;
      } else {
         return false;
      }
   }

   public static boolean contains(CharSequence seq, int searchChar) {
      if (isEmpty(seq)) {
         return false;
      } else {
         return CharSequenceUtils.indexOf(seq, searchChar, 0) >= 0;
      }
   }

   public static boolean containsAny(CharSequence cs, char... searchChars) {
      if (!isEmpty(cs) && !ArrayUtils.isEmpty(searchChars)) {
         int csLength = cs.length();
         int searchLength = searchChars.length;
         int csLast = csLength - 1;
         int searchLast = searchLength - 1;

         for(int i = 0; i < csLength; ++i) {
            char ch = cs.charAt(i);

            for(int j = 0; j < searchLength; ++j) {
               if (searchChars[j] == ch) {
                  if (!Character.isHighSurrogate(ch)) {
                     return true;
                  }

                  if (j == searchLast) {
                     return true;
                  }

                  if (i < csLast && searchChars[j + 1] == cs.charAt(i + 1)) {
                     return true;
                  }
               }
            }
         }

         return false;
      } else {
         return false;
      }
   }

   public static boolean containsAny(CharSequence cs, CharSequence searchChars) {
      return searchChars == null ? false : containsAny(cs, CharSequenceUtils.toCharArray(searchChars));
   }

   public static boolean containsAny(CharSequence cs, CharSequence... searchCharSequences) {
      if (!isEmpty(cs) && !ArrayUtils.isEmpty((Object[])searchCharSequences)) {
         CharSequence[] var2 = searchCharSequences;
         int var3 = searchCharSequences.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            CharSequence searchCharSequence = var2[var4];
            if (contains(cs, searchCharSequence)) {
               return true;
            }
         }

         return false;
      } else {
         return false;
      }
   }

   public static boolean containsIgnoreCase(CharSequence str, CharSequence searchStr) {
      if (str != null && searchStr != null) {
         int len = searchStr.length();
         int max = str.length() - len;

         for(int i = 0; i <= max; ++i) {
            if (CharSequenceUtils.regionMatches(str, true, i, searchStr, 0, len)) {
               return true;
            }
         }

         return false;
      } else {
         return false;
      }
   }

   public static boolean containsNone(CharSequence cs, char... searchChars) {
      if (cs != null && searchChars != null) {
         int csLen = cs.length();
         int csLast = csLen - 1;
         int searchLen = searchChars.length;
         int searchLast = searchLen - 1;

         for(int i = 0; i < csLen; ++i) {
            char ch = cs.charAt(i);

            for(int j = 0; j < searchLen; ++j) {
               if (searchChars[j] == ch) {
                  if (!Character.isHighSurrogate(ch)) {
                     return false;
                  }

                  if (j == searchLast) {
                     return false;
                  }

                  if (i < csLast && searchChars[j + 1] == cs.charAt(i + 1)) {
                     return false;
                  }
               }
            }
         }

         return true;
      } else {
         return true;
      }
   }

   public static boolean containsNone(CharSequence cs, String invalidChars) {
      return cs != null && invalidChars != null ? containsNone(cs, invalidChars.toCharArray()) : true;
   }

   public static boolean containsOnly(CharSequence cs, char... valid) {
      if (valid != null && cs != null) {
         if (cs.length() == 0) {
            return true;
         } else if (valid.length == 0) {
            return false;
         } else {
            return indexOfAnyBut(cs, valid) == -1;
         }
      } else {
         return false;
      }
   }

   public static boolean containsOnly(CharSequence cs, String validChars) {
      return cs != null && validChars != null ? containsOnly(cs, validChars.toCharArray()) : false;
   }

   public static boolean containsWhitespace(CharSequence seq) {
      if (isEmpty(seq)) {
         return false;
      } else {
         int strLen = seq.length();

         for(int i = 0; i < strLen; ++i) {
            if (Character.isWhitespace(seq.charAt(i))) {
               return true;
            }
         }

         return false;
      }
   }

   private static void convertRemainingAccentCharacters(StringBuilder decomposed) {
      for(int i = 0; i < decomposed.length(); ++i) {
         if (decomposed.charAt(i) == 321) {
            decomposed.deleteCharAt(i);
            decomposed.insert(i, 'L');
         } else if (decomposed.charAt(i) == 322) {
            decomposed.deleteCharAt(i);
            decomposed.insert(i, 'l');
         }
      }

   }

   public static int countMatches(CharSequence str, char ch) {
      if (isEmpty(str)) {
         return 0;
      } else {
         int count = 0;

         for(int i = 0; i < str.length(); ++i) {
            if (ch == str.charAt(i)) {
               ++count;
            }
         }

         return count;
      }
   }

   public static int countMatches(CharSequence str, CharSequence sub) {
      if (!isEmpty(str) && !isEmpty(sub)) {
         int count = 0;

         for(int idx = 0; (idx = CharSequenceUtils.indexOf(str, sub, idx)) != -1; idx += sub.length()) {
            ++count;
         }

         return count;
      } else {
         return 0;
      }
   }

   public static <T extends CharSequence> T defaultIfBlank(T str, T defaultStr) {
      return isBlank(str) ? defaultStr : str;
   }

   public static <T extends CharSequence> T defaultIfEmpty(T str, T defaultStr) {
      return isEmpty(str) ? defaultStr : str;
   }

   public static String defaultString(String str) {
      return defaultString(str, "");
   }

   public static String defaultString(String str, String defaultStr) {
      return str == null ? defaultStr : str;
   }

   public static String deleteWhitespace(String str) {
      if (isEmpty(str)) {
         return str;
      } else {
         int sz = str.length();
         char[] chs = new char[sz];
         int count = 0;

         for(int i = 0; i < sz; ++i) {
            if (!Character.isWhitespace(str.charAt(i))) {
               chs[count++] = str.charAt(i);
            }
         }

         if (count == sz) {
            return str;
         } else {
            return new String(chs, 0, count);
         }
      }
   }

   public static String difference(String str1, String str2) {
      if (str1 == null) {
         return str2;
      } else if (str2 == null) {
         return str1;
      } else {
         int at = indexOfDifference(str1, str2);
         return at == -1 ? "" : str2.substring(at);
      }
   }

   public static boolean endsWith(CharSequence str, CharSequence suffix) {
      return endsWith(str, suffix, false);
   }

   private static boolean endsWith(CharSequence str, CharSequence suffix, boolean ignoreCase) {
      if (str != null && suffix != null) {
         if (suffix.length() > str.length()) {
            return false;
         } else {
            int strOffset = str.length() - suffix.length();
            return CharSequenceUtils.regionMatches(str, ignoreCase, strOffset, suffix, 0, suffix.length());
         }
      } else {
         return str == suffix;
      }
   }

   public static boolean endsWithAny(CharSequence sequence, CharSequence... searchStrings) {
      if (!isEmpty(sequence) && !ArrayUtils.isEmpty((Object[])searchStrings)) {
         CharSequence[] var2 = searchStrings;
         int var3 = searchStrings.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            CharSequence searchString = var2[var4];
            if (endsWith(sequence, searchString)) {
               return true;
            }
         }

         return false;
      } else {
         return false;
      }
   }

   public static boolean endsWithIgnoreCase(CharSequence str, CharSequence suffix) {
      return endsWith(str, suffix, true);
   }

   public static boolean equals(CharSequence cs1, CharSequence cs2) {
      if (cs1 == cs2) {
         return true;
      } else if (cs1 != null && cs2 != null) {
         if (cs1.length() != cs2.length()) {
            return false;
         } else if (cs1 instanceof String && cs2 instanceof String) {
            return cs1.equals(cs2);
         } else {
            int length = cs1.length();

            for(int i = 0; i < length; ++i) {
               if (cs1.charAt(i) != cs2.charAt(i)) {
                  return false;
               }
            }

            return true;
         }
      } else {
         return false;
      }
   }

   public static boolean equalsAny(CharSequence string, CharSequence... searchStrings) {
      if (ArrayUtils.isNotEmpty((Object[])searchStrings)) {
         CharSequence[] var2 = searchStrings;
         int var3 = searchStrings.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            CharSequence next = var2[var4];
            if (equals(string, next)) {
               return true;
            }
         }
      }

      return false;
   }

   public static boolean equalsAnyIgnoreCase(CharSequence string, CharSequence... searchStrings) {
      if (ArrayUtils.isNotEmpty((Object[])searchStrings)) {
         CharSequence[] var2 = searchStrings;
         int var3 = searchStrings.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            CharSequence next = var2[var4];
            if (equalsIgnoreCase(string, next)) {
               return true;
            }
         }
      }

      return false;
   }

   public static boolean equalsIgnoreCase(CharSequence cs1, CharSequence cs2) {
      if (cs1 == cs2) {
         return true;
      } else if (cs1 != null && cs2 != null) {
         return cs1.length() != cs2.length() ? false : CharSequenceUtils.regionMatches(cs1, true, 0, cs2, 0, cs1.length());
      } else {
         return false;
      }
   }

   @SafeVarargs
   public static <T extends CharSequence> T firstNonBlank(T... values) {
      if (values != null) {
         CharSequence[] var1 = values;
         int var2 = values.length;

         for(int var3 = 0; var3 < var2; ++var3) {
            T val = var1[var3];
            if (isNotBlank(val)) {
               return val;
            }
         }
      }

      return null;
   }

   @SafeVarargs
   public static <T extends CharSequence> T firstNonEmpty(T... values) {
      if (values != null) {
         CharSequence[] var1 = values;
         int var2 = values.length;

         for(int var3 = 0; var3 < var2; ++var3) {
            T val = var1[var3];
            if (isNotEmpty(val)) {
               return val;
            }
         }
      }

      return null;
   }

   public static byte[] getBytes(String string, Charset charset) {
      return string == null ? ArrayUtils.EMPTY_BYTE_ARRAY : string.getBytes(Charsets.toCharset(charset));
   }

   public static byte[] getBytes(String string, String charset) throws UnsupportedEncodingException {
      return string == null ? ArrayUtils.EMPTY_BYTE_ARRAY : string.getBytes(Charsets.toCharsetName(charset));
   }

   public static String getCommonPrefix(String... strs) {
      if (ArrayUtils.isEmpty((Object[])strs)) {
         return "";
      } else {
         int smallestIndexOfDiff = indexOfDifference(strs);
         if (smallestIndexOfDiff == -1) {
            return strs[0] == null ? "" : strs[0];
         } else {
            return smallestIndexOfDiff == 0 ? "" : strs[0].substring(0, smallestIndexOfDiff);
         }
      }
   }

   public static String getDigits(String str) {
      if (isEmpty(str)) {
         return str;
      } else {
         int sz = str.length();
         StringBuilder strDigits = new StringBuilder(sz);

         for(int i = 0; i < sz; ++i) {
            char tempChar = str.charAt(i);
            if (Character.isDigit(tempChar)) {
               strDigits.append(tempChar);
            }
         }

         return strDigits.toString();
      }
   }

   /** @deprecated */
   @Deprecated
   public static int getFuzzyDistance(CharSequence term, CharSequence query, Locale locale) {
      if (term != null && query != null) {
         if (locale == null) {
            throw new IllegalArgumentException("Locale must not be null");
         } else {
            String termLowerCase = term.toString().toLowerCase(locale);
            String queryLowerCase = query.toString().toLowerCase(locale);
            int score = 0;
            int termIndex = 0;
            int previousMatchingCharacterIndex = Integer.MIN_VALUE;

            for(int queryIndex = 0; queryIndex < queryLowerCase.length(); ++queryIndex) {
               char queryChar = queryLowerCase.charAt(queryIndex);

               for(boolean termCharacterMatchFound = false; termIndex < termLowerCase.length() && !termCharacterMatchFound; ++termIndex) {
                  char termChar = termLowerCase.charAt(termIndex);
                  if (queryChar == termChar) {
                     ++score;
                     if (previousMatchingCharacterIndex + 1 == termIndex) {
                        score += 2;
                     }

                     previousMatchingCharacterIndex = termIndex;
                     termCharacterMatchFound = true;
                  }
               }
            }

            return score;
         }
      } else {
         throw new IllegalArgumentException("Strings must not be null");
      }
   }

   public static <T extends CharSequence> T getIfBlank(T str, Supplier<T> defaultSupplier) {
      return isBlank(str) ? (defaultSupplier == null ? null : (CharSequence)defaultSupplier.get()) : str;
   }

   public static <T extends CharSequence> T getIfEmpty(T str, Supplier<T> defaultSupplier) {
      return isEmpty(str) ? (defaultSupplier == null ? null : (CharSequence)defaultSupplier.get()) : str;
   }

   /** @deprecated */
   @Deprecated
   public static double getJaroWinklerDistance(CharSequence first, CharSequence second) {
      double DEFAULT_SCALING_FACTOR = 0.1D;
      if (first != null && second != null) {
         int[] mtp = matches(first, second);
         double m = (double)mtp[0];
         if (m == 0.0D) {
            return 0.0D;
         } else {
            double j = (m / (double)first.length() + m / (double)second.length() + (m - (double)mtp[1]) / m) / 3.0D;
            double jw = j < 0.7D ? j : j + Math.min(0.1D, 1.0D / (double)mtp[3]) * (double)mtp[2] * (1.0D - j);
            return (double)Math.round(jw * 100.0D) / 100.0D;
         }
      } else {
         throw new IllegalArgumentException("Strings must not be null");
      }
   }

   /** @deprecated */
   @Deprecated
   public static int getLevenshteinDistance(CharSequence s, CharSequence t) {
      if (s != null && t != null) {
         int n = s.length();
         int m = t.length();
         if (n == 0) {
            return m;
         } else if (m == 0) {
            return n;
         } else {
            if (n > m) {
               CharSequence tmp = s;
               s = t;
               t = tmp;
               n = m;
               m = tmp.length();
            }

            int[] p = new int[n + 1];

            int i;
            for(i = 0; i <= n; p[i] = i++) {
            }

            for(int j = 1; j <= m; ++j) {
               int upper_left = p[0];
               char t_j = t.charAt(j - 1);
               p[0] = j;

               for(i = 1; i <= n; ++i) {
                  int upper = p[i];
                  int cost = s.charAt(i - 1) == t_j ? 0 : 1;
                  p[i] = Math.min(Math.min(p[i - 1] + 1, p[i] + 1), upper_left + cost);
                  upper_left = upper;
               }
            }

            return p[n];
         }
      } else {
         throw new IllegalArgumentException("Strings must not be null");
      }
   }

   /** @deprecated */
   @Deprecated
   public static int getLevenshteinDistance(CharSequence s, CharSequence t, int threshold) {
      if (s != null && t != null) {
         if (threshold < 0) {
            throw new IllegalArgumentException("Threshold must not be negative");
         } else {
            int n = s.length();
            int m = t.length();
            if (n == 0) {
               return m <= threshold ? m : -1;
            } else if (m == 0) {
               return n <= threshold ? n : -1;
            } else if (Math.abs(n - m) > threshold) {
               return -1;
            } else {
               if (n > m) {
                  CharSequence tmp = s;
                  s = t;
                  t = tmp;
                  n = m;
                  m = tmp.length();
               }

               int[] p = new int[n + 1];
               int[] d = new int[n + 1];
               int boundary = Math.min(n, threshold) + 1;

               int j;
               for(j = 0; j < boundary; p[j] = j++) {
               }

               Arrays.fill(p, boundary, p.length, Integer.MAX_VALUE);
               Arrays.fill(d, Integer.MAX_VALUE);

               for(j = 1; j <= m; ++j) {
                  char t_j = t.charAt(j - 1);
                  d[0] = j;
                  int min = Math.max(1, j - threshold);
                  int max = j > Integer.MAX_VALUE - threshold ? n : Math.min(n, j + threshold);
                  if (min > max) {
                     return -1;
                  }

                  if (min > 1) {
                     d[min - 1] = Integer.MAX_VALUE;
                  }

                  for(int i = min; i <= max; ++i) {
                     if (s.charAt(i - 1) == t_j) {
                        d[i] = p[i - 1];
                     } else {
                        d[i] = 1 + Math.min(Math.min(d[i - 1], p[i]), p[i - 1]);
                     }
                  }

                  int[] _d = p;
                  p = d;
                  d = _d;
               }

               if (p[n] <= threshold) {
                  return p[n];
               } else {
                  return -1;
               }
            }
         }
      } else {
         throw new IllegalArgumentException("Strings must not be null");
      }
   }

   public static int indexOf(CharSequence seq, CharSequence searchSeq) {
      return seq != null && searchSeq != null ? CharSequenceUtils.indexOf(seq, searchSeq, 0) : -1;
   }

   public static int indexOf(CharSequence seq, CharSequence searchSeq, int startPos) {
      return seq != null && searchSeq != null ? CharSequenceUtils.indexOf(seq, searchSeq, startPos) : -1;
   }

   public static int indexOf(CharSequence seq, int searchChar) {
      return isEmpty(seq) ? -1 : CharSequenceUtils.indexOf(seq, searchChar, 0);
   }

   public static int indexOf(CharSequence seq, int searchChar, int startPos) {
      return isEmpty(seq) ? -1 : CharSequenceUtils.indexOf(seq, searchChar, startPos);
   }

   public static int indexOfAny(CharSequence cs, char... searchChars) {
      if (!isEmpty(cs) && !ArrayUtils.isEmpty(searchChars)) {
         int csLen = cs.length();
         int csLast = csLen - 1;
         int searchLen = searchChars.length;
         int searchLast = searchLen - 1;

         for(int i = 0; i < csLen; ++i) {
            char ch = cs.charAt(i);

            for(int j = 0; j < searchLen; ++j) {
               if (searchChars[j] == ch) {
                  if (i >= csLast || j >= searchLast || !Character.isHighSurrogate(ch)) {
                     return i;
                  }

                  if (searchChars[j + 1] == cs.charAt(i + 1)) {
                     return i;
                  }
               }
            }
         }

         return -1;
      } else {
         return -1;
      }
   }

   public static int indexOfAny(CharSequence str, CharSequence... searchStrs) {
      if (str != null && searchStrs != null) {
         int ret = Integer.MAX_VALUE;
         int tmp = false;
         CharSequence[] var4 = searchStrs;
         int var5 = searchStrs.length;

         for(int var6 = 0; var6 < var5; ++var6) {
            CharSequence search = var4[var6];
            if (search != null) {
               int tmp = CharSequenceUtils.indexOf(str, search, 0);
               if (tmp != -1 && tmp < ret) {
                  ret = tmp;
               }
            }
         }

         return ret == Integer.MAX_VALUE ? -1 : ret;
      } else {
         return -1;
      }
   }

   public static int indexOfAny(CharSequence cs, String searchChars) {
      return !isEmpty(cs) && !isEmpty(searchChars) ? indexOfAny(cs, searchChars.toCharArray()) : -1;
   }

   public static int indexOfAnyBut(CharSequence cs, char... searchChars) {
      if (!isEmpty(cs) && !ArrayUtils.isEmpty(searchChars)) {
         int csLen = cs.length();
         int csLast = csLen - 1;
         int searchLen = searchChars.length;
         int searchLast = searchLen - 1;

         label38:
         for(int i = 0; i < csLen; ++i) {
            char ch = cs.charAt(i);

            for(int j = 0; j < searchLen; ++j) {
               if (searchChars[j] == ch && (i >= csLast || j >= searchLast || !Character.isHighSurrogate(ch) || searchChars[j + 1] == cs.charAt(i + 1))) {
                  continue label38;
               }
            }

            return i;
         }

         return -1;
      } else {
         return -1;
      }
   }

   public static int indexOfAnyBut(CharSequence seq, CharSequence searchChars) {
      if (!isEmpty(seq) && !isEmpty(searchChars)) {
         int strLen = seq.length();

         for(int i = 0; i < strLen; ++i) {
            char ch = seq.charAt(i);
            boolean chFound = CharSequenceUtils.indexOf(searchChars, ch, 0) >= 0;
            if (i + 1 < strLen && Character.isHighSurrogate(ch)) {
               char ch2 = seq.charAt(i + 1);
               if (chFound && CharSequenceUtils.indexOf(searchChars, ch2, 0) < 0) {
                  return i;
               }
            } else if (!chFound) {
               return i;
            }
         }

         return -1;
      } else {
         return -1;
      }
   }

   public static int indexOfDifference(CharSequence... css) {
      if (ArrayUtils.getLength(css) <= 1) {
         return -1;
      } else {
         boolean anyStringNull = false;
         boolean allStringsNull = true;
         int arrayLen = css.length;
         int shortestStrLen = Integer.MAX_VALUE;
         int longestStrLen = 0;
         CharSequence[] var6 = css;
         int stringPos = css.length;

         for(int var8 = 0; var8 < stringPos; ++var8) {
            CharSequence cs = var6[var8];
            if (cs == null) {
               anyStringNull = true;
               shortestStrLen = 0;
            } else {
               allStringsNull = false;
               shortestStrLen = Math.min(cs.length(), shortestStrLen);
               longestStrLen = Math.max(cs.length(), longestStrLen);
            }
         }

         if (allStringsNull || longestStrLen == 0 && !anyStringNull) {
            return -1;
         } else if (shortestStrLen == 0) {
            return 0;
         } else {
            int firstDiff = -1;

            for(stringPos = 0; stringPos < shortestStrLen; ++stringPos) {
               char comparisonChar = css[0].charAt(stringPos);

               for(int arrayPos = 1; arrayPos < arrayLen; ++arrayPos) {
                  if (css[arrayPos].charAt(stringPos) != comparisonChar) {
                     firstDiff = stringPos;
                     break;
                  }
               }

               if (firstDiff != -1) {
                  break;
               }
            }

            return firstDiff == -1 && shortestStrLen != longestStrLen ? shortestStrLen : firstDiff;
         }
      }
   }

   public static int indexOfDifference(CharSequence cs1, CharSequence cs2) {
      if (cs1 == cs2) {
         return -1;
      } else if (cs1 != null && cs2 != null) {
         int i;
         for(i = 0; i < cs1.length() && i < cs2.length() && cs1.charAt(i) == cs2.charAt(i); ++i) {
         }

         return i >= cs2.length() && i >= cs1.length() ? -1 : i;
      } else {
         return 0;
      }
   }

   public static int indexOfIgnoreCase(CharSequence str, CharSequence searchStr) {
      return indexOfIgnoreCase(str, searchStr, 0);
   }

   public static int indexOfIgnoreCase(CharSequence str, CharSequence searchStr, int startPos) {
      if (str != null && searchStr != null) {
         if (startPos < 0) {
            startPos = 0;
         }

         int endLimit = str.length() - searchStr.length() + 1;
         if (startPos > endLimit) {
            return -1;
         } else if (searchStr.length() == 0) {
            return startPos;
         } else {
            for(int i = startPos; i < endLimit; ++i) {
               if (CharSequenceUtils.regionMatches(str, true, i, searchStr, 0, searchStr.length())) {
                  return i;
               }
            }

            return -1;
         }
      } else {
         return -1;
      }
   }

   public static boolean isAllBlank(CharSequence... css) {
      if (ArrayUtils.isEmpty((Object[])css)) {
         return true;
      } else {
         CharSequence[] var1 = css;
         int var2 = css.length;

         for(int var3 = 0; var3 < var2; ++var3) {
            CharSequence cs = var1[var3];
            if (isNotBlank(cs)) {
               return false;
            }
         }

         return true;
      }
   }

   public static boolean isAllEmpty(CharSequence... css) {
      if (ArrayUtils.isEmpty((Object[])css)) {
         return true;
      } else {
         CharSequence[] var1 = css;
         int var2 = css.length;

         for(int var3 = 0; var3 < var2; ++var3) {
            CharSequence cs = var1[var3];
            if (isNotEmpty(cs)) {
               return false;
            }
         }

         return true;
      }
   }

   public static boolean isAllLowerCase(CharSequence cs) {
      if (isEmpty(cs)) {
         return false;
      } else {
         int sz = cs.length();

         for(int i = 0; i < sz; ++i) {
            if (!Character.isLowerCase(cs.charAt(i))) {
               return false;
            }
         }

         return true;
      }
   }

   public static boolean isAllUpperCase(CharSequence cs) {
      if (isEmpty(cs)) {
         return false;
      } else {
         int sz = cs.length();

         for(int i = 0; i < sz; ++i) {
            if (!Character.isUpperCase(cs.charAt(i))) {
               return false;
            }
         }

         return true;
      }
   }

   public static boolean isAlpha(CharSequence cs) {
      if (isEmpty(cs)) {
         return false;
      } else {
         int sz = cs.length();

         for(int i = 0; i < sz; ++i) {
            if (!Character.isLetter(cs.charAt(i))) {
               return false;
            }
         }

         return true;
      }
   }

   public static boolean isAlphanumeric(CharSequence cs) {
      if (isEmpty(cs)) {
         return false;
      } else {
         int sz = cs.length();

         for(int i = 0; i < sz; ++i) {
            if (!Character.isLetterOrDigit(cs.charAt(i))) {
               return false;
            }
         }

         return true;
      }
   }

   public static boolean isAlphanumericSpace(CharSequence cs) {
      if (cs == null) {
         return false;
      } else {
         int sz = cs.length();

         for(int i = 0; i < sz; ++i) {
            if (!Character.isLetterOrDigit(cs.charAt(i)) && cs.charAt(i) != ' ') {
               return false;
            }
         }

         return true;
      }
   }

   public static boolean isAlphaSpace(CharSequence cs) {
      if (cs == null) {
         return false;
      } else {
         int sz = cs.length();

         for(int i = 0; i < sz; ++i) {
            if (!Character.isLetter(cs.charAt(i)) && cs.charAt(i) != ' ') {
               return false;
            }
         }

         return true;
      }
   }

   public static boolean isAnyBlank(CharSequence... css) {
      if (ArrayUtils.isEmpty((Object[])css)) {
         return false;
      } else {
         CharSequence[] var1 = css;
         int var2 = css.length;

         for(int var3 = 0; var3 < var2; ++var3) {
            CharSequence cs = var1[var3];
            if (isBlank(cs)) {
               return true;
            }
         }

         return false;
      }
   }

   public static boolean isAnyEmpty(CharSequence... css) {
      if (ArrayUtils.isEmpty((Object[])css)) {
         return false;
      } else {
         CharSequence[] var1 = css;
         int var2 = css.length;

         for(int var3 = 0; var3 < var2; ++var3) {
            CharSequence cs = var1[var3];
            if (isEmpty(cs)) {
               return true;
            }
         }

         return false;
      }
   }

   public static boolean isAsciiPrintable(CharSequence cs) {
      if (cs == null) {
         return false;
      } else {
         int sz = cs.length();

         for(int i = 0; i < sz; ++i) {
            if (!CharUtils.isAsciiPrintable(cs.charAt(i))) {
               return false;
            }
         }

         return true;
      }
   }

   public static boolean isBlank(CharSequence cs) {
      int strLen = length(cs);
      if (strLen == 0) {
         return true;
      } else {
         for(int i = 0; i < strLen; ++i) {
            if (!Character.isWhitespace(cs.charAt(i))) {
               return false;
            }
         }

         return true;
      }
   }

   public static boolean isEmpty(CharSequence cs) {
      return cs == null || cs.length() == 0;
   }

   public static boolean isMixedCase(CharSequence cs) {
      if (!isEmpty(cs) && cs.length() != 1) {
         boolean containsUppercase = false;
         boolean containsLowercase = false;
         int sz = cs.length();

         for(int i = 0; i < sz; ++i) {
            if (containsUppercase && containsLowercase) {
               return true;
            }

            if (Character.isUpperCase(cs.charAt(i))) {
               containsUppercase = true;
            } else if (Character.isLowerCase(cs.charAt(i))) {
               containsLowercase = true;
            }
         }

         return containsUppercase && containsLowercase;
      } else {
         return false;
      }
   }

   public static boolean isNoneBlank(CharSequence... css) {
      return !isAnyBlank(css);
   }

   public static boolean isNoneEmpty(CharSequence... css) {
      return !isAnyEmpty(css);
   }

   public static boolean isNotBlank(CharSequence cs) {
      return !isBlank(cs);
   }

   public static boolean isNotEmpty(CharSequence cs) {
      return !isEmpty(cs);
   }

   public static boolean isNumeric(CharSequence cs) {
      if (isEmpty(cs)) {
         return false;
      } else {
         int sz = cs.length();

         for(int i = 0; i < sz; ++i) {
            if (!Character.isDigit(cs.charAt(i))) {
               return false;
            }
         }

         return true;
      }
   }

   public static boolean isNumericSpace(CharSequence cs) {
      if (cs == null) {
         return false;
      } else {
         int sz = cs.length();

         for(int i = 0; i < sz; ++i) {
            if (!Character.isDigit(cs.charAt(i)) && cs.charAt(i) != ' ') {
               return false;
            }
         }

         return true;
      }
   }

   public static boolean isWhitespace(CharSequence cs) {
      if (cs == null) {
         return false;
      } else {
         int sz = cs.length();

         for(int i = 0; i < sz; ++i) {
            if (!Character.isWhitespace(cs.charAt(i))) {
               return false;
            }
         }

         return true;
      }
   }

   public static String join(byte[] array, char separator) {
      return array == null ? null : join((byte[])array, separator, 0, array.length);
   }

   public static String join(byte[] array, char separator, int startIndex, int endIndex) {
      if (array == null) {
         return null;
      } else {
         int noOfItems = endIndex - startIndex;
         if (noOfItems <= 0) {
            return "";
         } else {
            StringBuilder buf = newStringBuilder(noOfItems);
            buf.append(array[startIndex]);

            for(int i = startIndex + 1; i < endIndex; ++i) {
               buf.append(separator);
               buf.append(array[i]);
            }

            return buf.toString();
         }
      }
   }

   public static String join(char[] array, char separator) {
      return array == null ? null : join((char[])array, separator, 0, array.length);
   }

   public static String join(char[] array, char separator, int startIndex, int endIndex) {
      if (array == null) {
         return null;
      } else {
         int noOfItems = endIndex - startIndex;
         if (noOfItems <= 0) {
            return "";
         } else {
            StringBuilder buf = newStringBuilder(noOfItems);
            buf.append(array[startIndex]);

            for(int i = startIndex + 1; i < endIndex; ++i) {
               buf.append(separator);
               buf.append(array[i]);
            }

            return buf.toString();
         }
      }
   }

   public static String join(double[] array, char separator) {
      return array == null ? null : join((double[])array, separator, 0, array.length);
   }

   public static String join(double[] array, char separator, int startIndex, int endIndex) {
      if (array == null) {
         return null;
      } else {
         int noOfItems = endIndex - startIndex;
         if (noOfItems <= 0) {
            return "";
         } else {
            StringBuilder buf = newStringBuilder(noOfItems);
            buf.append(array[startIndex]);

            for(int i = startIndex + 1; i < endIndex; ++i) {
               buf.append(separator);
               buf.append(array[i]);
            }

            return buf.toString();
         }
      }
   }

   public static String join(float[] array, char separator) {
      return array == null ? null : join((float[])array, separator, 0, array.length);
   }

   public static String join(float[] array, char separator, int startIndex, int endIndex) {
      if (array == null) {
         return null;
      } else {
         int noOfItems = endIndex - startIndex;
         if (noOfItems <= 0) {
            return "";
         } else {
            StringBuilder buf = newStringBuilder(noOfItems);
            buf.append(array[startIndex]);

            for(int i = startIndex + 1; i < endIndex; ++i) {
               buf.append(separator);
               buf.append(array[i]);
            }

            return buf.toString();
         }
      }
   }

   public static String join(int[] array, char separator) {
      return array == null ? null : join((int[])array, separator, 0, array.length);
   }

   public static String join(int[] array, char separator, int startIndex, int endIndex) {
      if (array == null) {
         return null;
      } else {
         int noOfItems = endIndex - startIndex;
         if (noOfItems <= 0) {
            return "";
         } else {
            StringBuilder buf = newStringBuilder(noOfItems);
            buf.append(array[startIndex]);

            for(int i = startIndex + 1; i < endIndex; ++i) {
               buf.append(separator);
               buf.append(array[i]);
            }

            return buf.toString();
         }
      }
   }

   public static String join(Iterable<?> iterable, char separator) {
      return iterable == null ? null : join(iterable.iterator(), separator);
   }

   public static String join(Iterable<?> iterable, String separator) {
      return iterable == null ? null : join(iterable.iterator(), separator);
   }

   public static String join(Iterator<?> iterator, char separator) {
      if (iterator == null) {
         return null;
      } else if (!iterator.hasNext()) {
         return "";
      } else {
         Object first = iterator.next();
         if (!iterator.hasNext()) {
            return Objects.toString(first, "");
         } else {
            StringBuilder buf = new StringBuilder(256);
            if (first != null) {
               buf.append(first);
            }

            while(iterator.hasNext()) {
               buf.append(separator);
               Object obj = iterator.next();
               if (obj != null) {
                  buf.append(obj);
               }
            }

            return buf.toString();
         }
      }
   }

   public static String join(Iterator<?> iterator, String separator) {
      if (iterator == null) {
         return null;
      } else if (!iterator.hasNext()) {
         return "";
      } else {
         Object first = iterator.next();
         if (!iterator.hasNext()) {
            return Objects.toString(first, "");
         } else {
            StringBuilder buf = new StringBuilder(256);
            if (first != null) {
               buf.append(first);
            }

            while(iterator.hasNext()) {
               if (separator != null) {
                  buf.append(separator);
               }

               Object obj = iterator.next();
               if (obj != null) {
                  buf.append(obj);
               }
            }

            return buf.toString();
         }
      }
   }

   public static String join(List<?> list, char separator, int startIndex, int endIndex) {
      if (list == null) {
         return null;
      } else {
         int noOfItems = endIndex - startIndex;
         if (noOfItems <= 0) {
            return "";
         } else {
            List<?> subList = list.subList(startIndex, endIndex);
            return join(subList.iterator(), separator);
         }
      }
   }

   public static String join(List<?> list, String separator, int startIndex, int endIndex) {
      if (list == null) {
         return null;
      } else {
         int noOfItems = endIndex - startIndex;
         if (noOfItems <= 0) {
            return "";
         } else {
            List<?> subList = list.subList(startIndex, endIndex);
            return join(subList.iterator(), separator);
         }
      }
   }

   public static String join(long[] array, char separator) {
      return array == null ? null : join((long[])array, separator, 0, array.length);
   }

   public static String join(long[] array, char separator, int startIndex, int endIndex) {
      if (array == null) {
         return null;
      } else {
         int noOfItems = endIndex - startIndex;
         if (noOfItems <= 0) {
            return "";
         } else {
            StringBuilder buf = newStringBuilder(noOfItems);
            buf.append(array[startIndex]);

            for(int i = startIndex + 1; i < endIndex; ++i) {
               buf.append(separator);
               buf.append(array[i]);
            }

            return buf.toString();
         }
      }
   }

   public static String join(Object[] array, char separator) {
      return array == null ? null : join((Object[])array, separator, 0, array.length);
   }

   public static String join(Object[] array, char separator, int startIndex, int endIndex) {
      if (array == null) {
         return null;
      } else {
         int noOfItems = endIndex - startIndex;
         if (noOfItems <= 0) {
            return "";
         } else {
            StringBuilder buf = newStringBuilder(noOfItems);
            if (array[startIndex] != null) {
               buf.append(array[startIndex]);
            }

            for(int i = startIndex + 1; i < endIndex; ++i) {
               buf.append(separator);
               if (array[i] != null) {
                  buf.append(array[i]);
               }
            }

            return buf.toString();
         }
      }
   }

   public static String join(Object[] array, String separator) {
      return array == null ? null : join((Object[])array, separator, 0, array.length);
   }

   public static String join(Object[] array, String separator, int startIndex, int endIndex) {
      if (array == null) {
         return null;
      } else {
         if (separator == null) {
            separator = "";
         }

         int noOfItems = endIndex - startIndex;
         if (noOfItems <= 0) {
            return "";
         } else {
            StringBuilder buf = newStringBuilder(noOfItems);
            if (array[startIndex] != null) {
               buf.append(array[startIndex]);
            }

            for(int i = startIndex + 1; i < endIndex; ++i) {
               buf.append(separator);
               if (array[i] != null) {
                  buf.append(array[i]);
               }
            }

            return buf.toString();
         }
      }
   }

   public static String join(short[] array, char separator) {
      return array == null ? null : join((short[])array, separator, 0, array.length);
   }

   public static String join(short[] array, char separator, int startIndex, int endIndex) {
      if (array == null) {
         return null;
      } else {
         int noOfItems = endIndex - startIndex;
         if (noOfItems <= 0) {
            return "";
         } else {
            StringBuilder buf = newStringBuilder(noOfItems);
            buf.append(array[startIndex]);

            for(int i = startIndex + 1; i < endIndex; ++i) {
               buf.append(separator);
               buf.append(array[i]);
            }

            return buf.toString();
         }
      }
   }

   @SafeVarargs
   public static <T> String join(T... elements) {
      return join((Object[])elements, (String)null);
   }

   public static String joinWith(String separator, Object... objects) {
      if (objects == null) {
         throw new IllegalArgumentException("Object varargs must not be null");
      } else {
         String sanitizedSeparator = defaultString(separator);
         StringBuilder result = new StringBuilder();
         Iterator iterator = Arrays.asList(objects).iterator();

         while(iterator.hasNext()) {
            String value = Objects.toString(iterator.next(), "");
            result.append(value);
            if (iterator.hasNext()) {
               result.append(sanitizedSeparator);
            }
         }

         return result.toString();
      }
   }

   public static int lastIndexOf(CharSequence seq, CharSequence searchSeq) {
      return seq != null && searchSeq != null ? CharSequenceUtils.lastIndexOf(seq, searchSeq, seq.length()) : -1;
   }

   public static int lastIndexOf(CharSequence seq, CharSequence searchSeq, int startPos) {
      return seq != null && searchSeq != null ? CharSequenceUtils.lastIndexOf(seq, searchSeq, startPos) : -1;
   }

   public static int lastIndexOf(CharSequence seq, int searchChar) {
      return isEmpty(seq) ? -1 : CharSequenceUtils.lastIndexOf(seq, searchChar, seq.length());
   }

   public static int lastIndexOf(CharSequence seq, int searchChar, int startPos) {
      return isEmpty(seq) ? -1 : CharSequenceUtils.lastIndexOf(seq, searchChar, startPos);
   }

   public static int lastIndexOfAny(CharSequence str, CharSequence... searchStrs) {
      if (str != null && searchStrs != null) {
         int ret = -1;
         int tmp = false;
         CharSequence[] var4 = searchStrs;
         int var5 = searchStrs.length;

         for(int var6 = 0; var6 < var5; ++var6) {
            CharSequence search = var4[var6];
            if (search != null) {
               int tmp = CharSequenceUtils.lastIndexOf(str, search, str.length());
               if (tmp > ret) {
                  ret = tmp;
               }
            }
         }

         return ret;
      } else {
         return -1;
      }
   }

   public static int lastIndexOfIgnoreCase(CharSequence str, CharSequence searchStr) {
      return str != null && searchStr != null ? lastIndexOfIgnoreCase(str, searchStr, str.length()) : -1;
   }

   public static int lastIndexOfIgnoreCase(CharSequence str, CharSequence searchStr, int startPos) {
      if (str != null && searchStr != null) {
         if (startPos > str.length() - searchStr.length()) {
            startPos = str.length() - searchStr.length();
         }

         if (startPos < 0) {
            return -1;
         } else if (searchStr.length() == 0) {
            return startPos;
         } else {
            for(int i = startPos; i >= 0; --i) {
               if (CharSequenceUtils.regionMatches(str, true, i, searchStr, 0, searchStr.length())) {
                  return i;
               }
            }

            return -1;
         }
      } else {
         return -1;
      }
   }

   public static int lastOrdinalIndexOf(CharSequence str, CharSequence searchStr, int ordinal) {
      return ordinalIndexOf(str, searchStr, ordinal, true);
   }

   public static String left(String str, int len) {
      if (str == null) {
         return null;
      } else if (len < 0) {
         return "";
      } else {
         return str.length() <= len ? str : str.substring(0, len);
      }
   }

   public static String leftPad(String str, int size) {
      return leftPad(str, size, ' ');
   }

   public static String leftPad(String str, int size, char padChar) {
      if (str == null) {
         return null;
      } else {
         int pads = size - str.length();
         if (pads <= 0) {
            return str;
         } else {
            return pads > 8192 ? leftPad(str, size, String.valueOf(padChar)) : repeat(padChar, pads).concat(str);
         }
      }
   }

   public static String leftPad(String str, int size, String padStr) {
      if (str == null) {
         return null;
      } else {
         if (isEmpty(padStr)) {
            padStr = " ";
         }

         int padLen = padStr.length();
         int strLen = str.length();
         int pads = size - strLen;
         if (pads <= 0) {
            return str;
         } else if (padLen == 1 && pads <= 8192) {
            return leftPad(str, size, padStr.charAt(0));
         } else if (pads == padLen) {
            return padStr.concat(str);
         } else if (pads < padLen) {
            return padStr.substring(0, pads).concat(str);
         } else {
            char[] padding = new char[pads];
            char[] padChars = padStr.toCharArray();

            for(int i = 0; i < pads; ++i) {
               padding[i] = padChars[i % padLen];
            }

            return (new String(padding)).concat(str);
         }
      }
   }

   public static int length(CharSequence cs) {
      return cs == null ? 0 : cs.length();
   }

   public static String lowerCase(String str) {
      return str == null ? null : str.toLowerCase();
   }

   public static String lowerCase(String str, Locale locale) {
      return str == null ? null : str.toLowerCase(locale);
   }

   private static int[] matches(CharSequence first, CharSequence second) {
      CharSequence max;
      CharSequence min;
      if (first.length() > second.length()) {
         max = first;
         min = second;
      } else {
         max = second;
         min = first;
      }

      int range = Math.max(max.length() / 2 - 1, 0);
      int[] matchIndexes = new int[min.length()];
      Arrays.fill(matchIndexes, -1);
      boolean[] matchFlags = new boolean[max.length()];
      int matches = 0;

      int transpositions;
      int prefix;
      for(int mi = 0; mi < min.length(); ++mi) {
         char c1 = min.charAt(mi);
         transpositions = Math.max(mi - range, 0);

         for(prefix = Math.min(mi + range + 1, max.length()); transpositions < prefix; ++transpositions) {
            if (!matchFlags[transpositions] && c1 == max.charAt(transpositions)) {
               matchIndexes[mi] = transpositions;
               matchFlags[transpositions] = true;
               ++matches;
               break;
            }
         }
      }

      char[] ms1 = new char[matches];
      char[] ms2 = new char[matches];
      transpositions = 0;

      for(prefix = 0; transpositions < min.length(); ++transpositions) {
         if (matchIndexes[transpositions] != -1) {
            ms1[prefix] = min.charAt(transpositions);
            ++prefix;
         }
      }

      transpositions = 0;

      for(prefix = 0; transpositions < max.length(); ++transpositions) {
         if (matchFlags[transpositions]) {
            ms2[prefix] = max.charAt(transpositions);
            ++prefix;
         }
      }

      transpositions = 0;

      for(prefix = 0; prefix < ms1.length; ++prefix) {
         if (ms1[prefix] != ms2[prefix]) {
            ++transpositions;
         }
      }

      prefix = 0;

      for(int mi = 0; mi < min.length() && first.charAt(mi) == second.charAt(mi); ++mi) {
         ++prefix;
      }

      return new int[]{matches, transpositions / 2, prefix, max.length()};
   }

   public static String mid(String str, int pos, int len) {
      if (str == null) {
         return null;
      } else if (len >= 0 && pos <= str.length()) {
         if (pos < 0) {
            pos = 0;
         }

         return str.length() <= pos + len ? str.substring(pos) : str.substring(pos, pos + len);
      } else {
         return "";
      }
   }

   private static StringBuilder newStringBuilder(int noOfItems) {
      return new StringBuilder(noOfItems * 16);
   }

   public static String normalizeSpace(String str) {
      if (isEmpty(str)) {
         return str;
      } else {
         int size = str.length();
         char[] newChars = new char[size];
         int count = 0;
         int whitespacesCount = 0;
         boolean startWhitespaces = true;

         for(int i = 0; i < size; ++i) {
            char actualChar = str.charAt(i);
            boolean isWhitespace = Character.isWhitespace(actualChar);
            if (isWhitespace) {
               if (whitespacesCount == 0 && !startWhitespaces) {
                  newChars[count++] = " ".charAt(0);
               }

               ++whitespacesCount;
            } else {
               startWhitespaces = false;
               newChars[count++] = actualChar == 160 ? 32 : actualChar;
               whitespacesCount = 0;
            }
         }

         if (startWhitespaces) {
            return "";
         } else {
            return (new String(newChars, 0, count - (whitespacesCount > 0 ? 1 : 0))).trim();
         }
      }
   }

   public static int ordinalIndexOf(CharSequence str, CharSequence searchStr, int ordinal) {
      return ordinalIndexOf(str, searchStr, ordinal, false);
   }

   private static int ordinalIndexOf(CharSequence str, CharSequence searchStr, int ordinal, boolean lastIndex) {
      if (str != null && searchStr != null && ordinal > 0) {
         if (searchStr.length() == 0) {
            return lastIndex ? str.length() : 0;
         } else {
            int found = 0;
            int index = lastIndex ? str.length() : -1;

            do {
               if (lastIndex) {
                  index = CharSequenceUtils.lastIndexOf(str, searchStr, index - 1);
               } else {
                  index = CharSequenceUtils.indexOf(str, searchStr, index + 1);
               }

               if (index < 0) {
                  return index;
               }

               ++found;
            } while(found < ordinal);

            return index;
         }
      } else {
         return -1;
      }
   }

   public static String overlay(String str, String overlay, int start, int end) {
      if (str == null) {
         return null;
      } else {
         if (overlay == null) {
            overlay = "";
         }

         int len = str.length();
         if (start < 0) {
            start = 0;
         }

         if (start > len) {
            start = len;
         }

         if (end < 0) {
            end = 0;
         }

         if (end > len) {
            end = len;
         }

         if (start > end) {
            int temp = start;
            start = end;
            end = temp;
         }

         return str.substring(0, start) + overlay + str.substring(end);
      }
   }

   private static String prependIfMissing(String str, CharSequence prefix, boolean ignoreCase, CharSequence... prefixes) {
      if (str != null && !isEmpty(prefix) && !startsWith(str, prefix, ignoreCase)) {
         if (ArrayUtils.isNotEmpty((Object[])prefixes)) {
            CharSequence[] var4 = prefixes;
            int var5 = prefixes.length;

            for(int var6 = 0; var6 < var5; ++var6) {
               CharSequence p = var4[var6];
               if (startsWith(str, p, ignoreCase)) {
                  return str;
               }
            }
         }

         return prefix.toString() + str;
      } else {
         return str;
      }
   }

   public static String prependIfMissing(String str, CharSequence prefix, CharSequence... prefixes) {
      return prependIfMissing(str, prefix, false, prefixes);
   }

   public static String prependIfMissingIgnoreCase(String str, CharSequence prefix, CharSequence... prefixes) {
      return prependIfMissing(str, prefix, true, prefixes);
   }

   public static String remove(String str, char remove) {
      if (!isEmpty(str) && str.indexOf(remove) != -1) {
         char[] chars = str.toCharArray();
         int pos = 0;

         for(int i = 0; i < chars.length; ++i) {
            if (chars[i] != remove) {
               chars[pos++] = chars[i];
            }
         }

         return new String(chars, 0, pos);
      } else {
         return str;
      }
   }

   public static String remove(String str, String remove) {
      return !isEmpty(str) && !isEmpty(remove) ? replace(str, remove, "", -1) : str;
   }

   /** @deprecated */
   @Deprecated
   public static String removeAll(String text, String regex) {
      return RegExUtils.removeAll(text, regex);
   }

   public static String removeEnd(String str, String remove) {
      if (!isEmpty(str) && !isEmpty(remove)) {
         return str.endsWith(remove) ? str.substring(0, str.length() - remove.length()) : str;
      } else {
         return str;
      }
   }

   public static String removeEndIgnoreCase(String str, String remove) {
      if (!isEmpty(str) && !isEmpty(remove)) {
         return endsWithIgnoreCase(str, remove) ? str.substring(0, str.length() - remove.length()) : str;
      } else {
         return str;
      }
   }

   /** @deprecated */
   @Deprecated
   public static String removeFirst(String text, String regex) {
      return replaceFirst(text, regex, "");
   }

   public static String removeIgnoreCase(String str, String remove) {
      return !isEmpty(str) && !isEmpty(remove) ? replaceIgnoreCase(str, remove, "", -1) : str;
   }

   /** @deprecated */
   @Deprecated
   public static String removePattern(String source, String regex) {
      return RegExUtils.removePattern(source, regex);
   }

   public static String removeStart(String str, String remove) {
      if (!isEmpty(str) && !isEmpty(remove)) {
         return str.startsWith(remove) ? str.substring(remove.length()) : str;
      } else {
         return str;
      }
   }

   public static String removeStartIgnoreCase(String str, String remove) {
      if (!isEmpty(str) && !isEmpty(remove)) {
         return startsWithIgnoreCase(str, remove) ? str.substring(remove.length()) : str;
      } else {
         return str;
      }
   }

   public static String repeat(char ch, int repeat) {
      if (repeat <= 0) {
         return "";
      } else {
         char[] buf = new char[repeat];

         for(int i = repeat - 1; i >= 0; --i) {
            buf[i] = ch;
         }

         return new String(buf);
      }
   }

   public static String repeat(String str, int repeat) {
      if (str == null) {
         return null;
      } else if (repeat <= 0) {
         return "";
      } else {
         int inputLength = str.length();
         if (repeat != 1 && inputLength != 0) {
            if (inputLength == 1 && repeat <= 8192) {
               return repeat(str.charAt(0), repeat);
            } else {
               int outputLength = inputLength * repeat;
               switch(inputLength) {
               case 1:
                  return repeat(str.charAt(0), repeat);
               case 2:
                  char ch0 = str.charAt(0);
                  char ch1 = str.charAt(1);
                  char[] output2 = new char[outputLength];

                  for(int i = repeat * 2 - 2; i >= 0; --i) {
                     output2[i] = ch0;
                     output2[i + 1] = ch1;
                     --i;
                  }

                  return new String(output2);
               default:
                  StringBuilder buf = new StringBuilder(outputLength);

                  for(int i = 0; i < repeat; ++i) {
                     buf.append(str);
                  }

                  return buf.toString();
               }
            }
         } else {
            return str;
         }
      }
   }

   public static String repeat(String str, String separator, int repeat) {
      if (str != null && separator != null) {
         String result = repeat(str + separator, repeat);
         return removeEnd(result, separator);
      } else {
         return repeat(str, repeat);
      }
   }

   public static String replace(String text, String searchString, String replacement) {
      return replace(text, searchString, replacement, -1);
   }

   public static String replace(String text, String searchString, String replacement, int max) {
      return replace(text, searchString, replacement, max, false);
   }

   private static String replace(String text, String searchString, String replacement, int max, boolean ignoreCase) {
      if (!isEmpty(text) && !isEmpty(searchString) && replacement != null && max != 0) {
         if (ignoreCase) {
            searchString = searchString.toLowerCase();
         }

         int start = 0;
         int end = ignoreCase ? indexOfIgnoreCase(text, searchString, start) : indexOf(text, searchString, start);
         if (end == -1) {
            return text;
         } else {
            int replLength = searchString.length();
            int increase = Math.max(replacement.length() - replLength, 0);
            increase *= max < 0 ? 16 : Math.min(max, 64);

            StringBuilder buf;
            for(buf = new StringBuilder(text.length() + increase); end != -1; end = ignoreCase ? indexOfIgnoreCase(text, searchString, start) : indexOf(text, searchString, start)) {
               buf.append(text, start, end).append(replacement);
               start = end + replLength;
               --max;
               if (max == 0) {
                  break;
               }
            }

            buf.append(text, start, text.length());
            return buf.toString();
         }
      } else {
         return text;
      }
   }

   /** @deprecated */
   @Deprecated
   public static String replaceAll(String text, String regex, String replacement) {
      return RegExUtils.replaceAll(text, regex, replacement);
   }

   public static String replaceChars(String str, char searchChar, char replaceChar) {
      return str == null ? null : str.replace(searchChar, replaceChar);
   }

   public static String replaceChars(String str, String searchChars, String replaceChars) {
      if (!isEmpty(str) && !isEmpty(searchChars)) {
         if (replaceChars == null) {
            replaceChars = "";
         }

         boolean modified = false;
         int replaceCharsLength = replaceChars.length();
         int strLength = str.length();
         StringBuilder buf = new StringBuilder(strLength);

         for(int i = 0; i < strLength; ++i) {
            char ch = str.charAt(i);
            int index = searchChars.indexOf(ch);
            if (index >= 0) {
               modified = true;
               if (index < replaceCharsLength) {
                  buf.append(replaceChars.charAt(index));
               }
            } else {
               buf.append(ch);
            }
         }

         if (modified) {
            return buf.toString();
         } else {
            return str;
         }
      } else {
         return str;
      }
   }

   public static String replaceEach(String text, String[] searchList, String[] replacementList) {
      return replaceEach(text, searchList, replacementList, false, 0);
   }

   private static String replaceEach(String text, String[] searchList, String[] replacementList, boolean repeat, int timeToLive) {
      if (timeToLive < 0) {
         Set<String> searchSet = new HashSet(Arrays.asList(searchList));
         Set<String> replacementSet = new HashSet(Arrays.asList(replacementList));
         searchSet.retainAll(replacementSet);
         if (searchSet.size() > 0) {
            throw new IllegalStateException("Aborting to protect against StackOverflowError - output of one loop is the input of another");
         }
      }

      if (isEmpty(text) || ArrayUtils.isEmpty((Object[])searchList) || ArrayUtils.isEmpty((Object[])replacementList) || ArrayUtils.isNotEmpty((Object[])searchList) && timeToLive == -1) {
         return text;
      } else {
         int searchLength = searchList.length;
         int replacementLength = replacementList.length;
         if (searchLength != replacementLength) {
            throw new IllegalArgumentException("Search and Replace array lengths don't match: " + searchLength + " vs " + replacementLength);
         } else {
            boolean[] noMoreMatchesForReplIndex = new boolean[searchLength];
            int textIndex = -1;
            int replaceIndex = -1;
            int tempIndex = true;

            int start;
            int tempIndex;
            for(start = 0; start < searchLength; ++start) {
               if (!noMoreMatchesForReplIndex[start] && !isEmpty(searchList[start]) && replacementList[start] != null) {
                  tempIndex = text.indexOf(searchList[start]);
                  if (tempIndex == -1) {
                     noMoreMatchesForReplIndex[start] = true;
                  } else if (textIndex == -1 || tempIndex < textIndex) {
                     textIndex = tempIndex;
                     replaceIndex = start;
                  }
               }
            }

            if (textIndex == -1) {
               return text;
            } else {
               start = 0;
               int increase = 0;

               int i;
               for(int i = 0; i < searchList.length; ++i) {
                  if (searchList[i] != null && replacementList[i] != null) {
                     i = replacementList[i].length() - searchList[i].length();
                     if (i > 0) {
                        increase += 3 * i;
                     }
                  }
               }

               increase = Math.min(increase, text.length() / 5);
               StringBuilder buf = new StringBuilder(text.length() + increase);

               while(textIndex != -1) {
                  for(i = start; i < textIndex; ++i) {
                     buf.append(text.charAt(i));
                  }

                  buf.append(replacementList[replaceIndex]);
                  start = textIndex + searchList[replaceIndex].length();
                  textIndex = -1;
                  replaceIndex = -1;
                  tempIndex = true;

                  for(i = 0; i < searchLength; ++i) {
                     if (!noMoreMatchesForReplIndex[i] && searchList[i] != null && !searchList[i].isEmpty() && replacementList[i] != null) {
                        tempIndex = text.indexOf(searchList[i], start);
                        if (tempIndex == -1) {
                           noMoreMatchesForReplIndex[i] = true;
                        } else if (textIndex == -1 || tempIndex < textIndex) {
                           textIndex = tempIndex;
                           replaceIndex = i;
                        }
                     }
                  }
               }

               i = text.length();

               for(int i = start; i < i; ++i) {
                  buf.append(text.charAt(i));
               }

               String result = buf.toString();
               if (!repeat) {
                  return result;
               } else {
                  return replaceEach(result, searchList, replacementList, repeat, timeToLive - 1);
               }
            }
         }
      }
   }

   public static String replaceEachRepeatedly(String text, String[] searchList, String[] replacementList) {
      int timeToLive = searchList == null ? 0 : searchList.length;
      return replaceEach(text, searchList, replacementList, true, timeToLive);
   }

   /** @deprecated */
   @Deprecated
   public static String replaceFirst(String text, String regex, String replacement) {
      return RegExUtils.replaceFirst(text, regex, replacement);
   }

   public static String replaceIgnoreCase(String text, String searchString, String replacement) {
      return replaceIgnoreCase(text, searchString, replacement, -1);
   }

   public static String replaceIgnoreCase(String text, String searchString, String replacement, int max) {
      return replace(text, searchString, replacement, max, true);
   }

   public static String replaceOnce(String text, String searchString, String replacement) {
      return replace(text, searchString, replacement, 1);
   }

   public static String replaceOnceIgnoreCase(String text, String searchString, String replacement) {
      return replaceIgnoreCase(text, searchString, replacement, 1);
   }

   /** @deprecated */
   @Deprecated
   public static String replacePattern(String source, String regex, String replacement) {
      return RegExUtils.replacePattern(source, regex, replacement);
   }

   public static String reverse(String str) {
      return str == null ? null : (new StringBuilder(str)).reverse().toString();
   }

   public static String reverseDelimited(String str, char separatorChar) {
      if (str == null) {
         return null;
      } else {
         String[] strs = split(str, separatorChar);
         ArrayUtils.reverse((Object[])strs);
         return join((Object[])strs, separatorChar);
      }
   }

   public static String right(String str, int len) {
      if (str == null) {
         return null;
      } else if (len < 0) {
         return "";
      } else {
         return str.length() <= len ? str : str.substring(str.length() - len);
      }
   }

   public static String rightPad(String str, int size) {
      return rightPad(str, size, ' ');
   }

   public static String rightPad(String str, int size, char padChar) {
      if (str == null) {
         return null;
      } else {
         int pads = size - str.length();
         if (pads <= 0) {
            return str;
         } else {
            return pads > 8192 ? rightPad(str, size, String.valueOf(padChar)) : str.concat(repeat(padChar, pads));
         }
      }
   }

   public static String rightPad(String str, int size, String padStr) {
      if (str == null) {
         return null;
      } else {
         if (isEmpty(padStr)) {
            padStr = " ";
         }

         int padLen = padStr.length();
         int strLen = str.length();
         int pads = size - strLen;
         if (pads <= 0) {
            return str;
         } else if (padLen == 1 && pads <= 8192) {
            return rightPad(str, size, padStr.charAt(0));
         } else if (pads == padLen) {
            return str.concat(padStr);
         } else if (pads < padLen) {
            return str.concat(padStr.substring(0, pads));
         } else {
            char[] padding = new char[pads];
            char[] padChars = padStr.toCharArray();

            for(int i = 0; i < pads; ++i) {
               padding[i] = padChars[i % padLen];
            }

            return str.concat(new String(padding));
         }
      }
   }

   public static String rotate(String str, int shift) {
      if (str == null) {
         return null;
      } else {
         int strLen = str.length();
         if (shift != 0 && strLen != 0 && shift % strLen != 0) {
            StringBuilder builder = new StringBuilder(strLen);
            int offset = -(shift % strLen);
            builder.append(substring(str, offset));
            builder.append(substring(str, 0, offset));
            return builder.toString();
         } else {
            return str;
         }
      }
   }

   public static String[] split(String str) {
      return split(str, (String)null, -1);
   }

   public static String[] split(String str, char separatorChar) {
      return splitWorker(str, separatorChar, false);
   }

   public static String[] split(String str, String separatorChars) {
      return splitWorker(str, separatorChars, -1, false);
   }

   public static String[] split(String str, String separatorChars, int max) {
      return splitWorker(str, separatorChars, max, false);
   }

   public static String[] splitByCharacterType(String str) {
      return splitByCharacterType(str, false);
   }

   private static String[] splitByCharacterType(String str, boolean camelCase) {
      if (str == null) {
         return null;
      } else if (str.isEmpty()) {
         return ArrayUtils.EMPTY_STRING_ARRAY;
      } else {
         char[] c = str.toCharArray();
         List<String> list = new ArrayList();
         int tokenStart = 0;
         int currentType = Character.getType(c[tokenStart]);

         for(int pos = tokenStart + 1; pos < c.length; ++pos) {
            int type = Character.getType(c[pos]);
            if (type != currentType) {
               if (camelCase && type == 2 && currentType == 1) {
                  int newTokenStart = pos - 1;
                  if (newTokenStart != tokenStart) {
                     list.add(new String(c, tokenStart, newTokenStart - tokenStart));
                     tokenStart = newTokenStart;
                  }
               } else {
                  list.add(new String(c, tokenStart, pos - tokenStart));
                  tokenStart = pos;
               }

               currentType = type;
            }
         }

         list.add(new String(c, tokenStart, c.length - tokenStart));
         return (String[])list.toArray(ArrayUtils.EMPTY_STRING_ARRAY);
      }
   }

   public static String[] splitByCharacterTypeCamelCase(String str) {
      return splitByCharacterType(str, true);
   }

   public static String[] splitByWholeSeparator(String str, String separator) {
      return splitByWholeSeparatorWorker(str, separator, -1, false);
   }

   public static String[] splitByWholeSeparator(String str, String separator, int max) {
      return splitByWholeSeparatorWorker(str, separator, max, false);
   }

   public static String[] splitByWholeSeparatorPreserveAllTokens(String str, String separator) {
      return splitByWholeSeparatorWorker(str, separator, -1, true);
   }

   public static String[] splitByWholeSeparatorPreserveAllTokens(String str, String separator, int max) {
      return splitByWholeSeparatorWorker(str, separator, max, true);
   }

   private static String[] splitByWholeSeparatorWorker(String str, String separator, int max, boolean preserveAllTokens) {
      if (str == null) {
         return null;
      } else {
         int len = str.length();
         if (len == 0) {
            return ArrayUtils.EMPTY_STRING_ARRAY;
         } else if (separator != null && !"".equals(separator)) {
            int separatorLength = separator.length();
            ArrayList<String> substrings = new ArrayList();
            int numberOfSubstrings = 0;
            int beg = 0;
            int end = 0;

            while(end < len) {
               end = str.indexOf(separator, beg);
               if (end > -1) {
                  if (end > beg) {
                     ++numberOfSubstrings;
                     if (numberOfSubstrings == max) {
                        end = len;
                        substrings.add(str.substring(beg));
                     } else {
                        substrings.add(str.substring(beg, end));
                        beg = end + separatorLength;
                     }
                  } else {
                     if (preserveAllTokens) {
                        ++numberOfSubstrings;
                        if (numberOfSubstrings == max) {
                           end = len;
                           substrings.add(str.substring(beg));
                        } else {
                           substrings.add("");
                        }
                     }

                     beg = end + separatorLength;
                  }
               } else {
                  substrings.add(str.substring(beg));
                  end = len;
               }
            }

            return (String[])substrings.toArray(ArrayUtils.EMPTY_STRING_ARRAY);
         } else {
            return splitWorker(str, (String)null, max, preserveAllTokens);
         }
      }
   }

   public static String[] splitPreserveAllTokens(String str) {
      return splitWorker(str, (String)null, -1, true);
   }

   public static String[] splitPreserveAllTokens(String str, char separatorChar) {
      return splitWorker(str, separatorChar, true);
   }

   public static String[] splitPreserveAllTokens(String str, String separatorChars) {
      return splitWorker(str, separatorChars, -1, true);
   }

   public static String[] splitPreserveAllTokens(String str, String separatorChars, int max) {
      return splitWorker(str, separatorChars, max, true);
   }

   private static String[] splitWorker(String str, char separatorChar, boolean preserveAllTokens) {
      if (str == null) {
         return null;
      } else {
         int len = str.length();
         if (len == 0) {
            return ArrayUtils.EMPTY_STRING_ARRAY;
         } else {
            List<String> list = new ArrayList();
            int i = 0;
            int start = 0;
            boolean match = false;
            boolean lastMatch = false;

            while(true) {
               while(i < len) {
                  if (str.charAt(i) == separatorChar) {
                     if (match || preserveAllTokens) {
                        list.add(str.substring(start, i));
                        match = false;
                        lastMatch = true;
                     }

                     ++i;
                     start = i;
                  } else {
                     lastMatch = false;
                     match = true;
                     ++i;
                  }
               }

               if (match || preserveAllTokens && lastMatch) {
                  list.add(str.substring(start, i));
               }

               return (String[])list.toArray(ArrayUtils.EMPTY_STRING_ARRAY);
            }
         }
      }
   }

   private static String[] splitWorker(String str, String separatorChars, int max, boolean preserveAllTokens) {
      if (str == null) {
         return null;
      } else {
         int len = str.length();
         if (len == 0) {
            return ArrayUtils.EMPTY_STRING_ARRAY;
         } else {
            List<String> list = new ArrayList();
            int sizePlus1 = 1;
            int i = 0;
            int start = 0;
            boolean match = false;
            boolean lastMatch = false;
            if (separatorChars != null) {
               if (separatorChars.length() != 1) {
                  label87:
                  while(true) {
                     while(true) {
                        if (i >= len) {
                           break label87;
                        }

                        if (separatorChars.indexOf(str.charAt(i)) >= 0) {
                           if (match || preserveAllTokens) {
                              lastMatch = true;
                              if (sizePlus1++ == max) {
                                 i = len;
                                 lastMatch = false;
                              }

                              list.add(str.substring(start, i));
                              match = false;
                           }

                           ++i;
                           start = i;
                        } else {
                           lastMatch = false;
                           match = true;
                           ++i;
                        }
                     }
                  }
               } else {
                  char sep = separatorChars.charAt(0);

                  label71:
                  while(true) {
                     while(true) {
                        if (i >= len) {
                           break label71;
                        }

                        if (str.charAt(i) == sep) {
                           if (match || preserveAllTokens) {
                              lastMatch = true;
                              if (sizePlus1++ == max) {
                                 i = len;
                                 lastMatch = false;
                              }

                              list.add(str.substring(start, i));
                              match = false;
                           }

                           ++i;
                           start = i;
                        } else {
                           lastMatch = false;
                           match = true;
                           ++i;
                        }
                     }
                  }
               }
            } else {
               label103:
               while(true) {
                  while(true) {
                     if (i >= len) {
                        break label103;
                     }

                     if (Character.isWhitespace(str.charAt(i))) {
                        if (match || preserveAllTokens) {
                           lastMatch = true;
                           if (sizePlus1++ == max) {
                              i = len;
                              lastMatch = false;
                           }

                           list.add(str.substring(start, i));
                           match = false;
                        }

                        ++i;
                        start = i;
                     } else {
                        lastMatch = false;
                        match = true;
                        ++i;
                     }
                  }
               }
            }

            if (match || preserveAllTokens && lastMatch) {
               list.add(str.substring(start, i));
            }

            return (String[])list.toArray(ArrayUtils.EMPTY_STRING_ARRAY);
         }
      }
   }

   public static boolean startsWith(CharSequence str, CharSequence prefix) {
      return startsWith(str, prefix, false);
   }

   private static boolean startsWith(CharSequence str, CharSequence prefix, boolean ignoreCase) {
      if (str != null && prefix != null) {
         return prefix.length() > str.length() ? false : CharSequenceUtils.regionMatches(str, ignoreCase, 0, prefix, 0, prefix.length());
      } else {
         return str == prefix;
      }
   }

   public static boolean startsWithAny(CharSequence sequence, CharSequence... searchStrings) {
      if (!isEmpty(sequence) && !ArrayUtils.isEmpty((Object[])searchStrings)) {
         CharSequence[] var2 = searchStrings;
         int var3 = searchStrings.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            CharSequence searchString = var2[var4];
            if (startsWith(sequence, searchString)) {
               return true;
            }
         }

         return false;
      } else {
         return false;
      }
   }

   public static boolean startsWithIgnoreCase(CharSequence str, CharSequence prefix) {
      return startsWith(str, prefix, true);
   }

   public static String strip(String str) {
      return strip(str, (String)null);
   }

   public static String strip(String str, String stripChars) {
      if (isEmpty(str)) {
         return str;
      } else {
         str = stripStart(str, stripChars);
         return stripEnd(str, stripChars);
      }
   }

   public static String stripAccents(String input) {
      if (input == null) {
         return null;
      } else {
         StringBuilder decomposed = new StringBuilder(Normalizer.normalize(input, Form.NFD));
         convertRemainingAccentCharacters(decomposed);
         return STRIP_ACCENTS_PATTERN.matcher(decomposed).replaceAll("");
      }
   }

   public static String[] stripAll(String... strs) {
      return stripAll(strs, (String)null);
   }

   public static String[] stripAll(String[] strs, String stripChars) {
      int strsLen = ArrayUtils.getLength(strs);
      if (strsLen == 0) {
         return strs;
      } else {
         String[] newArr = new String[strsLen];

         for(int i = 0; i < strsLen; ++i) {
            newArr[i] = strip(strs[i], stripChars);
         }

         return newArr;
      }
   }

   public static String stripEnd(String str, String stripChars) {
      int end = length(str);
      if (end == 0) {
         return str;
      } else {
         if (stripChars == null) {
            while(end != 0 && Character.isWhitespace(str.charAt(end - 1))) {
               --end;
            }
         } else {
            if (stripChars.isEmpty()) {
               return str;
            }

            while(end != 0 && stripChars.indexOf(str.charAt(end - 1)) != -1) {
               --end;
            }
         }

         return str.substring(0, end);
      }
   }

   public static String stripStart(String str, String stripChars) {
      int strLen = length(str);
      if (strLen == 0) {
         return str;
      } else {
         int start = 0;
         if (stripChars == null) {
            while(start != strLen && Character.isWhitespace(str.charAt(start))) {
               ++start;
            }
         } else {
            if (stripChars.isEmpty()) {
               return str;
            }

            while(start != strLen && stripChars.indexOf(str.charAt(start)) != -1) {
               ++start;
            }
         }

         return str.substring(start);
      }
   }

   public static String stripToEmpty(String str) {
      return str == null ? "" : strip(str, (String)null);
   }

   public static String stripToNull(String str) {
      if (str == null) {
         return null;
      } else {
         str = strip(str, (String)null);
         return str.isEmpty() ? null : str;
      }
   }

   public static String substring(String str, int start) {
      if (str == null) {
         return null;
      } else {
         if (start < 0) {
            start += str.length();
         }

         if (start < 0) {
            start = 0;
         }

         return start > str.length() ? "" : str.substring(start);
      }
   }

   public static String substring(String str, int start, int end) {
      if (str == null) {
         return null;
      } else {
         if (end < 0) {
            end += str.length();
         }

         if (start < 0) {
            start += str.length();
         }

         if (end > str.length()) {
            end = str.length();
         }

         if (start > end) {
            return "";
         } else {
            if (start < 0) {
               start = 0;
            }

            if (end < 0) {
               end = 0;
            }

            return str.substring(start, end);
         }
      }
   }

   public static String substringAfter(String str, int separator) {
      if (isEmpty(str)) {
         return str;
      } else {
         int pos = str.indexOf(separator);
         return pos == -1 ? "" : str.substring(pos + 1);
      }
   }

   public static String substringAfter(String str, String separator) {
      if (isEmpty(str)) {
         return str;
      } else if (separator == null) {
         return "";
      } else {
         int pos = str.indexOf(separator);
         return pos == -1 ? "" : str.substring(pos + separator.length());
      }
   }

   public static String substringAfterLast(String str, int separator) {
      if (isEmpty(str)) {
         return str;
      } else {
         int pos = str.lastIndexOf(separator);
         return pos != -1 && pos != str.length() - 1 ? str.substring(pos + 1) : "";
      }
   }

   public static String substringAfterLast(String str, String separator) {
      if (isEmpty(str)) {
         return str;
      } else if (isEmpty(separator)) {
         return "";
      } else {
         int pos = str.lastIndexOf(separator);
         return pos != -1 && pos != str.length() - separator.length() ? str.substring(pos + separator.length()) : "";
      }
   }

   public static String substringBefore(String str, String separator) {
      if (!isEmpty(str) && separator != null) {
         if (separator.isEmpty()) {
            return "";
         } else {
            int pos = str.indexOf(separator);
            return pos == -1 ? str : str.substring(0, pos);
         }
      } else {
         return str;
      }
   }

   public static String substringBeforeLast(String str, String separator) {
      if (!isEmpty(str) && !isEmpty(separator)) {
         int pos = str.lastIndexOf(separator);
         return pos == -1 ? str : str.substring(0, pos);
      } else {
         return str;
      }
   }

   public static String substringBetween(String str, String tag) {
      return substringBetween(str, tag, tag);
   }

   public static String substringBetween(String str, String open, String close) {
      if (!ObjectUtils.allNotNull(str, open, close)) {
         return null;
      } else {
         int start = str.indexOf(open);
         if (start != -1) {
            int end = str.indexOf(close, start + open.length());
            if (end != -1) {
               return str.substring(start + open.length(), end);
            }
         }

         return null;
      }
   }

   public static String[] substringsBetween(String str, String open, String close) {
      if (str != null && !isEmpty(open) && !isEmpty(close)) {
         int strLen = str.length();
         if (strLen == 0) {
            return ArrayUtils.EMPTY_STRING_ARRAY;
         } else {
            int closeLen = close.length();
            int openLen = open.length();
            List<String> list = new ArrayList();

            int end;
            for(int pos = 0; pos < strLen - closeLen; pos = end + closeLen) {
               int start = str.indexOf(open, pos);
               if (start < 0) {
                  break;
               }

               start += openLen;
               end = str.indexOf(close, start);
               if (end < 0) {
                  break;
               }

               list.add(str.substring(start, end));
            }

            return list.isEmpty() ? null : (String[])list.toArray(ArrayUtils.EMPTY_STRING_ARRAY);
         }
      } else {
         return null;
      }
   }

   public static String swapCase(String str) {
      if (isEmpty(str)) {
         return str;
      } else {
         int strLen = str.length();
         int[] newCodePoints = new int[strLen];
         int outOffset = 0;

         int newCodePoint;
         for(int i = 0; i < strLen; i += Character.charCount(newCodePoint)) {
            int oldCodepoint = str.codePointAt(i);
            if (!Character.isUpperCase(oldCodepoint) && !Character.isTitleCase(oldCodepoint)) {
               if (Character.isLowerCase(oldCodepoint)) {
                  newCodePoint = Character.toUpperCase(oldCodepoint);
               } else {
                  newCodePoint = oldCodepoint;
               }
            } else {
               newCodePoint = Character.toLowerCase(oldCodepoint);
            }

            newCodePoints[outOffset++] = newCodePoint;
         }

         return new String(newCodePoints, 0, outOffset);
      }
   }

   public static int[] toCodePoints(CharSequence str) {
      if (str == null) {
         return null;
      } else if (str.length() == 0) {
         return ArrayUtils.EMPTY_INT_ARRAY;
      } else {
         String s = str.toString();
         int[] result = new int[s.codePointCount(0, s.length())];
         int index = 0;

         for(int i = 0; i < result.length; ++i) {
            result[i] = s.codePointAt(index);
            index += Character.charCount(result[i]);
         }

         return result;
      }
   }

   public static String toEncodedString(byte[] bytes, Charset charset) {
      return new String(bytes, Charsets.toCharset(charset));
   }

   public static String toRootLowerCase(String source) {
      return source == null ? null : source.toLowerCase(Locale.ROOT);
   }

   public static String toRootUpperCase(String source) {
      return source == null ? null : source.toUpperCase(Locale.ROOT);
   }

   /** @deprecated */
   @Deprecated
   public static String toString(byte[] bytes, String charsetName) throws UnsupportedEncodingException {
      return charsetName != null ? new String(bytes, charsetName) : new String(bytes, Charset.defaultCharset());
   }

   public static String trim(String str) {
      return str == null ? null : str.trim();
   }

   public static String trimToEmpty(String str) {
      return str == null ? "" : str.trim();
   }

   public static String trimToNull(String str) {
      String ts = trim(str);
      return isEmpty(ts) ? null : ts;
   }

   public static String truncate(String str, int maxWidth) {
      return truncate(str, 0, maxWidth);
   }

   public static String truncate(String str, int offset, int maxWidth) {
      if (offset < 0) {
         throw new IllegalArgumentException("offset cannot be negative");
      } else if (maxWidth < 0) {
         throw new IllegalArgumentException("maxWith cannot be negative");
      } else if (str == null) {
         return null;
      } else if (offset > str.length()) {
         return "";
      } else if (str.length() > maxWidth) {
         int ix = Math.min(offset + maxWidth, str.length());
         return str.substring(offset, ix);
      } else {
         return str.substring(offset);
      }
   }

   public static String uncapitalize(String str) {
      int strLen = length(str);
      if (strLen == 0) {
         return str;
      } else {
         int firstCodepoint = str.codePointAt(0);
         int newCodePoint = Character.toLowerCase(firstCodepoint);
         if (firstCodepoint == newCodePoint) {
            return str;
         } else {
            int[] newCodePoints = new int[strLen];
            int outOffset = 0;
            int outOffset = outOffset + 1;
            newCodePoints[outOffset] = newCodePoint;

            int codepoint;
            for(int inOffset = Character.charCount(firstCodepoint); inOffset < strLen; inOffset += Character.charCount(codepoint)) {
               codepoint = str.codePointAt(inOffset);
               newCodePoints[outOffset++] = codepoint;
            }

            return new String(newCodePoints, 0, outOffset);
         }
      }
   }

   public static String unwrap(String str, char wrapChar) {
      if (!isEmpty(str) && wrapChar != 0 && str.length() != 1) {
         if (str.charAt(0) == wrapChar && str.charAt(str.length() - 1) == wrapChar) {
            int startIndex = false;
            int endIndex = str.length() - 1;
            return str.substring(1, endIndex);
         } else {
            return str;
         }
      } else {
         return str;
      }
   }

   public static String unwrap(String str, String wrapToken) {
      if (!isEmpty(str) && !isEmpty(wrapToken) && str.length() != 1) {
         if (startsWith(str, wrapToken) && endsWith(str, wrapToken)) {
            int startIndex = str.indexOf(wrapToken);
            int endIndex = str.lastIndexOf(wrapToken);
            int wrapLength = wrapToken.length();
            if (startIndex != -1 && endIndex != -1) {
               return str.substring(startIndex + wrapLength, endIndex);
            }
         }

         return str;
      } else {
         return str;
      }
   }

   public static String upperCase(String str) {
      return str == null ? null : str.toUpperCase();
   }

   public static String upperCase(String str, Locale locale) {
      return str == null ? null : str.toUpperCase(locale);
   }

   public static String valueOf(char[] value) {
      return value == null ? null : String.valueOf(value);
   }

   public static String wrap(String str, char wrapWith) {
      return !isEmpty(str) && wrapWith != 0 ? wrapWith + str + wrapWith : str;
   }

   public static String wrap(String str, String wrapWith) {
      return !isEmpty(str) && !isEmpty(wrapWith) ? wrapWith.concat(str).concat(wrapWith) : str;
   }

   public static String wrapIfMissing(String str, char wrapWith) {
      if (!isEmpty(str) && wrapWith != 0) {
         boolean wrapStart = str.charAt(0) != wrapWith;
         boolean wrapEnd = str.charAt(str.length() - 1) != wrapWith;
         if (!wrapStart && !wrapEnd) {
            return str;
         } else {
            StringBuilder builder = new StringBuilder(str.length() + 2);
            if (wrapStart) {
               builder.append(wrapWith);
            }

            builder.append(str);
            if (wrapEnd) {
               builder.append(wrapWith);
            }

            return builder.toString();
         }
      } else {
         return str;
      }
   }

   public static String wrapIfMissing(String str, String wrapWith) {
      if (!isEmpty(str) && !isEmpty(wrapWith)) {
         boolean wrapStart = !str.startsWith(wrapWith);
         boolean wrapEnd = !str.endsWith(wrapWith);
         if (!wrapStart && !wrapEnd) {
            return str;
         } else {
            StringBuilder builder = new StringBuilder(str.length() + wrapWith.length() + wrapWith.length());
            if (wrapStart) {
               builder.append(wrapWith);
            }

            builder.append(str);
            if (wrapEnd) {
               builder.append(wrapWith);
            }

            return builder.toString();
         }
      } else {
         return str;
      }
   }
}
