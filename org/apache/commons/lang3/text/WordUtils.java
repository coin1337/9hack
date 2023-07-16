package org.apache.commons.lang3.text;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

/** @deprecated */
@Deprecated
public class WordUtils {
   public static String wrap(String str, int wrapLength) {
      return wrap(str, wrapLength, (String)null, false);
   }

   public static String wrap(String str, int wrapLength, String newLineStr, boolean wrapLongWords) {
      return wrap(str, wrapLength, newLineStr, wrapLongWords, " ");
   }

   public static String wrap(String str, int wrapLength, String newLineStr, boolean wrapLongWords, String wrapOn) {
      if (str == null) {
         return null;
      } else {
         if (newLineStr == null) {
            newLineStr = System.lineSeparator();
         }

         if (wrapLength < 1) {
            wrapLength = 1;
         }

         if (StringUtils.isBlank(wrapOn)) {
            wrapOn = " ";
         }

         Pattern patternToWrapOn = Pattern.compile(wrapOn);
         int inputLineLength = str.length();
         int offset = 0;
         StringBuilder wrappedLine = new StringBuilder(inputLineLength + 32);

         while(offset < inputLineLength) {
            int spaceToWrapAt = -1;
            Matcher matcher = patternToWrapOn.matcher(str.substring(offset, Math.min((int)Math.min(2147483647L, (long)(offset + wrapLength) + 1L), inputLineLength)));
            if (matcher.find()) {
               if (matcher.start() == 0) {
                  offset += matcher.end();
                  continue;
               }

               spaceToWrapAt = matcher.start() + offset;
            }

            if (inputLineLength - offset <= wrapLength) {
               break;
            }

            while(matcher.find()) {
               spaceToWrapAt = matcher.start() + offset;
            }

            if (spaceToWrapAt >= offset) {
               wrappedLine.append(str, offset, spaceToWrapAt);
               wrappedLine.append(newLineStr);
               offset = spaceToWrapAt + 1;
            } else if (wrapLongWords) {
               wrappedLine.append(str, offset, wrapLength + offset);
               wrappedLine.append(newLineStr);
               offset += wrapLength;
            } else {
               matcher = patternToWrapOn.matcher(str.substring(offset + wrapLength));
               if (matcher.find()) {
                  spaceToWrapAt = matcher.start() + offset + wrapLength;
               }

               if (spaceToWrapAt >= 0) {
                  wrappedLine.append(str, offset, spaceToWrapAt);
                  wrappedLine.append(newLineStr);
                  offset = spaceToWrapAt + 1;
               } else {
                  wrappedLine.append(str, offset, str.length());
                  offset = inputLineLength;
               }
            }
         }

         wrappedLine.append(str, offset, str.length());
         return wrappedLine.toString();
      }
   }

   public static String capitalize(String str) {
      return capitalize(str, (char[])null);
   }

   public static String capitalize(String str, char... delimiters) {
      int delimLen = delimiters == null ? -1 : delimiters.length;
      if (!StringUtils.isEmpty(str) && delimLen != 0) {
         char[] buffer = str.toCharArray();
         boolean capitalizeNext = true;

         for(int i = 0; i < buffer.length; ++i) {
            char ch = buffer[i];
            if (isDelimiter(ch, delimiters)) {
               capitalizeNext = true;
            } else if (capitalizeNext) {
               buffer[i] = Character.toTitleCase(ch);
               capitalizeNext = false;
            }
         }

         return new String(buffer);
      } else {
         return str;
      }
   }

   public static String capitalizeFully(String str) {
      return capitalizeFully(str, (char[])null);
   }

   public static String capitalizeFully(String str, char... delimiters) {
      int delimLen = delimiters == null ? -1 : delimiters.length;
      if (!StringUtils.isEmpty(str) && delimLen != 0) {
         str = str.toLowerCase();
         return capitalize(str, delimiters);
      } else {
         return str;
      }
   }

   public static String uncapitalize(String str) {
      return uncapitalize(str, (char[])null);
   }

   public static String uncapitalize(String str, char... delimiters) {
      int delimLen = delimiters == null ? -1 : delimiters.length;
      if (!StringUtils.isEmpty(str) && delimLen != 0) {
         char[] buffer = str.toCharArray();
         boolean uncapitalizeNext = true;

         for(int i = 0; i < buffer.length; ++i) {
            char ch = buffer[i];
            if (isDelimiter(ch, delimiters)) {
               uncapitalizeNext = true;
            } else if (uncapitalizeNext) {
               buffer[i] = Character.toLowerCase(ch);
               uncapitalizeNext = false;
            }
         }

         return new String(buffer);
      } else {
         return str;
      }
   }

   public static String swapCase(String str) {
      if (StringUtils.isEmpty(str)) {
         return str;
      } else {
         char[] buffer = str.toCharArray();
         boolean whitespace = true;

         for(int i = 0; i < buffer.length; ++i) {
            char ch = buffer[i];
            if (!Character.isUpperCase(ch) && !Character.isTitleCase(ch)) {
               if (Character.isLowerCase(ch)) {
                  if (whitespace) {
                     buffer[i] = Character.toTitleCase(ch);
                     whitespace = false;
                  } else {
                     buffer[i] = Character.toUpperCase(ch);
                  }
               } else {
                  whitespace = Character.isWhitespace(ch);
               }
            } else {
               buffer[i] = Character.toLowerCase(ch);
               whitespace = false;
            }
         }

         return new String(buffer);
      }
   }

   public static String initials(String str) {
      return initials(str, (char[])null);
   }

   public static String initials(String str, char... delimiters) {
      if (StringUtils.isEmpty(str)) {
         return str;
      } else if (delimiters != null && delimiters.length == 0) {
         return "";
      } else {
         int strLen = str.length();
         char[] buf = new char[strLen / 2 + 1];
         int count = 0;
         boolean lastWasGap = true;

         for(int i = 0; i < strLen; ++i) {
            char ch = str.charAt(i);
            if (isDelimiter(ch, delimiters)) {
               lastWasGap = true;
            } else if (lastWasGap) {
               buf[count++] = ch;
               lastWasGap = false;
            }
         }

         return new String(buf, 0, count);
      }
   }

   public static boolean containsAllWords(CharSequence word, CharSequence... words) {
      if (!StringUtils.isEmpty(word) && !ArrayUtils.isEmpty((Object[])words)) {
         CharSequence[] var2 = words;
         int var3 = words.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            CharSequence w = var2[var4];
            if (StringUtils.isBlank(w)) {
               return false;
            }

            Pattern p = Pattern.compile(".*\\b" + w + "\\b.*");
            if (!p.matcher(word).matches()) {
               return false;
            }
         }

         return true;
      } else {
         return false;
      }
   }

   private static boolean isDelimiter(char ch, char[] delimiters) {
      if (delimiters == null) {
         return Character.isWhitespace(ch);
      } else {
         char[] var2 = delimiters;
         int var3 = delimiters.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            char delimiter = var2[var4];
            if (ch == delimiter) {
               return true;
            }
         }

         return false;
      }
   }
}
