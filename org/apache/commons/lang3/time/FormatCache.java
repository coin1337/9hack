package org.apache.commons.lang3.time;

import java.text.DateFormat;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.apache.commons.lang3.Validate;

abstract class FormatCache<F extends Format> {
   static final int NONE = -1;
   private final ConcurrentMap<FormatCache.MultipartKey, F> cInstanceCache = new ConcurrentHashMap(7);
   private static final ConcurrentMap<FormatCache.MultipartKey, String> cDateTimeInstanceCache = new ConcurrentHashMap(7);

   public F getInstance() {
      return this.getDateTimeInstance(3, 3, TimeZone.getDefault(), Locale.getDefault());
   }

   public F getInstance(String pattern, TimeZone timeZone, Locale locale) {
      Validate.notNull(pattern, "pattern must not be null");
      if (timeZone == null) {
         timeZone = TimeZone.getDefault();
      }

      if (locale == null) {
         locale = Locale.getDefault();
      }

      FormatCache.MultipartKey key = new FormatCache.MultipartKey(new Object[]{pattern, timeZone, locale});
      F format = (Format)this.cInstanceCache.get(key);
      if (format == null) {
         format = this.createInstance(pattern, timeZone, locale);
         F previousValue = (Format)this.cInstanceCache.putIfAbsent(key, format);
         if (previousValue != null) {
            format = previousValue;
         }
      }

      return format;
   }

   protected abstract F createInstance(String var1, TimeZone var2, Locale var3);

   private F getDateTimeInstance(Integer dateStyle, Integer timeStyle, TimeZone timeZone, Locale locale) {
      if (locale == null) {
         locale = Locale.getDefault();
      }

      String pattern = getPatternForStyle(dateStyle, timeStyle, locale);
      return this.getInstance(pattern, timeZone, locale);
   }

   F getDateTimeInstance(int dateStyle, int timeStyle, TimeZone timeZone, Locale locale) {
      return this.getDateTimeInstance(dateStyle, timeStyle, timeZone, locale);
   }

   F getDateInstance(int dateStyle, TimeZone timeZone, Locale locale) {
      return this.getDateTimeInstance(dateStyle, (Integer)null, timeZone, locale);
   }

   F getTimeInstance(int timeStyle, TimeZone timeZone, Locale locale) {
      return this.getDateTimeInstance((Integer)null, timeStyle, timeZone, locale);
   }

   static String getPatternForStyle(Integer dateStyle, Integer timeStyle, Locale locale) {
      FormatCache.MultipartKey key = new FormatCache.MultipartKey(new Object[]{dateStyle, timeStyle, locale});
      String pattern = (String)cDateTimeInstanceCache.get(key);
      if (pattern == null) {
         try {
            DateFormat formatter;
            if (dateStyle == null) {
               formatter = DateFormat.getTimeInstance(timeStyle, locale);
            } else if (timeStyle == null) {
               formatter = DateFormat.getDateInstance(dateStyle, locale);
            } else {
               formatter = DateFormat.getDateTimeInstance(dateStyle, timeStyle, locale);
            }

            pattern = ((SimpleDateFormat)formatter).toPattern();
            String previous = (String)cDateTimeInstanceCache.putIfAbsent(key, pattern);
            if (previous != null) {
               pattern = previous;
            }
         } catch (ClassCastException var7) {
            throw new IllegalArgumentException("No date time pattern for locale: " + locale);
         }
      }

      return pattern;
   }

   private static class MultipartKey {
      private final Object[] keys;
      private int hashCode;

      MultipartKey(Object... keys) {
         this.keys = keys;
      }

      public boolean equals(Object obj) {
         return Arrays.equals(this.keys, ((FormatCache.MultipartKey)obj).keys);
      }

      public int hashCode() {
         if (this.hashCode == 0) {
            int rc = 0;
            Object[] var2 = this.keys;
            int var3 = var2.length;

            for(int var4 = 0; var4 < var3; ++var4) {
               Object key = var2[var4];
               if (key != null) {
                  rc = rc * 7 + key.hashCode();
               }
            }

            this.hashCode = rc;
         }

         return this.hashCode;
      }
   }
}
