package org.apache.commons.lang3;

public class CharSetUtils {
   public static boolean containsAny(String str, String... set) {
      if (!StringUtils.isEmpty(str) && !deepEmpty(set)) {
         CharSet chars = CharSet.getInstance(set);
         char[] var3 = str.toCharArray();
         int var4 = var3.length;

         for(int var5 = 0; var5 < var4; ++var5) {
            char c = var3[var5];
            if (chars.contains(c)) {
               return true;
            }
         }

         return false;
      } else {
         return false;
      }
   }

   public static int count(String str, String... set) {
      if (!StringUtils.isEmpty(str) && !deepEmpty(set)) {
         CharSet chars = CharSet.getInstance(set);
         int count = 0;
         char[] var4 = str.toCharArray();
         int var5 = var4.length;

         for(int var6 = 0; var6 < var5; ++var6) {
            char c = var4[var6];
            if (chars.contains(c)) {
               ++count;
            }
         }

         return count;
      } else {
         return 0;
      }
   }

   private static boolean deepEmpty(String[] strings) {
      if (strings != null) {
         String[] var1 = strings;
         int var2 = strings.length;

         for(int var3 = 0; var3 < var2; ++var3) {
            String s = var1[var3];
            if (StringUtils.isNotEmpty(s)) {
               return false;
            }
         }
      }

      return true;
   }

   public static String delete(String str, String... set) {
      return !StringUtils.isEmpty(str) && !deepEmpty(set) ? modify(str, set, false) : str;
   }

   public static String keep(String str, String... set) {
      if (str == null) {
         return null;
      } else {
         return !str.isEmpty() && !deepEmpty(set) ? modify(str, set, true) : "";
      }
   }

   private static String modify(String str, String[] set, boolean expect) {
      CharSet chars = CharSet.getInstance(set);
      StringBuilder buffer = new StringBuilder(str.length());
      char[] chrs = str.toCharArray();
      char[] var6 = chrs;
      int var7 = chrs.length;

      for(int var8 = 0; var8 < var7; ++var8) {
         char chr = var6[var8];
         if (chars.contains(chr) == expect) {
            buffer.append(chr);
         }
      }

      return buffer.toString();
   }

   public static String squeeze(String str, String... set) {
      if (!StringUtils.isEmpty(str) && !deepEmpty(set)) {
         CharSet chars = CharSet.getInstance(set);
         StringBuilder buffer = new StringBuilder(str.length());
         char[] chrs = str.toCharArray();
         int sz = chrs.length;
         char lastChar = chrs[0];
         char ch = true;
         Character inChars = null;
         Character notInChars = null;
         buffer.append(lastChar);

         for(int i = 1; i < sz; ++i) {
            char ch = chrs[i];
            if (ch == lastChar) {
               if (inChars != null && ch == inChars) {
                  continue;
               }

               if (notInChars == null || ch != notInChars) {
                  if (chars.contains(ch)) {
                     inChars = ch;
                     continue;
                  }

                  notInChars = ch;
               }
            }

            buffer.append(ch);
            lastChar = ch;
         }

         return buffer.toString();
      } else {
         return str;
      }
   }
}
