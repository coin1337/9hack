package software.bernie.shadowed.fasterxml.jackson.databind.util;

import java.text.DateFormat;
import java.text.FieldPosition;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import software.bernie.shadowed.fasterxml.jackson.core.io.NumberInput;

public class StdDateFormat extends DateFormat {
   protected static final String PATTERN_PLAIN_STR = "\\d\\d\\d\\d[-]\\d\\d[-]\\d\\d";
   protected static final Pattern PATTERN_PLAIN = Pattern.compile("\\d\\d\\d\\d[-]\\d\\d[-]\\d\\d");
   protected static final Pattern PATTERN_ISO8601;
   public static final String DATE_FORMAT_STR_ISO8601 = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";
   protected static final String DATE_FORMAT_STR_PLAIN = "yyyy-MM-dd";
   protected static final String DATE_FORMAT_STR_RFC1123 = "EEE, dd MMM yyyy HH:mm:ss zzz";
   protected static final String[] ALL_FORMATS;
   protected static final TimeZone DEFAULT_TIMEZONE;
   protected static final Locale DEFAULT_LOCALE;
   protected static final DateFormat DATE_FORMAT_RFC1123;
   protected static final DateFormat DATE_FORMAT_ISO8601;
   public static final StdDateFormat instance;
   protected transient TimeZone _timezone;
   protected final Locale _locale;
   protected Boolean _lenient;
   private transient DateFormat _formatRFC1123;

   public StdDateFormat() {
      this._locale = DEFAULT_LOCALE;
   }

   /** @deprecated */
   @Deprecated
   public StdDateFormat(TimeZone tz, Locale loc) {
      this._timezone = tz;
      this._locale = loc;
   }

   protected StdDateFormat(TimeZone tz, Locale loc, Boolean lenient) {
      this._timezone = tz;
      this._locale = loc;
      this._lenient = lenient;
   }

   public static TimeZone getDefaultTimeZone() {
      return DEFAULT_TIMEZONE;
   }

   public StdDateFormat withTimeZone(TimeZone tz) {
      if (tz == null) {
         tz = DEFAULT_TIMEZONE;
      }

      return tz != this._timezone && !tz.equals(this._timezone) ? new StdDateFormat(tz, this._locale, this._lenient) : this;
   }

   public StdDateFormat withLocale(Locale loc) {
      return loc.equals(this._locale) ? this : new StdDateFormat(this._timezone, loc, this._lenient);
   }

   public StdDateFormat withLenient(Boolean b) {
      return _equals(b, this._lenient) ? this : new StdDateFormat(this._timezone, this._locale, b);
   }

   public StdDateFormat clone() {
      return new StdDateFormat(this._timezone, this._locale, this._lenient);
   }

   /** @deprecated */
   @Deprecated
   public static DateFormat getISO8601Format(TimeZone tz, Locale loc) {
      return _cloneFormat(DATE_FORMAT_ISO8601, "yyyy-MM-dd'T'HH:mm:ss.SSSZ", tz, loc, (Boolean)null);
   }

   /** @deprecated */
   @Deprecated
   public static DateFormat getRFC1123Format(TimeZone tz, Locale loc) {
      return _cloneFormat(DATE_FORMAT_RFC1123, "EEE, dd MMM yyyy HH:mm:ss zzz", tz, loc, (Boolean)null);
   }

   public TimeZone getTimeZone() {
      return this._timezone;
   }

   public void setTimeZone(TimeZone tz) {
      if (!tz.equals(this._timezone)) {
         this._clearFormats();
         this._timezone = tz;
      }

   }

   public void setLenient(boolean enabled) {
      Boolean newValue = enabled;
      if (!_equals(newValue, this._lenient)) {
         this._lenient = newValue;
         this._clearFormats();
      }

   }

   public boolean isLenient() {
      return this._lenient == null || this._lenient;
   }

   public Date parse(String dateStr) throws ParseException {
      dateStr = dateStr.trim();
      ParsePosition pos = new ParsePosition(0);
      Date dt = this._parseDate(dateStr, pos);
      if (dt != null) {
         return dt;
      } else {
         StringBuilder sb = new StringBuilder();
         String[] arr$ = ALL_FORMATS;
         int len$ = arr$.length;

         for(int i$ = 0; i$ < len$; ++i$) {
            String f = arr$[i$];
            if (sb.length() > 0) {
               sb.append("\", \"");
            } else {
               sb.append('"');
            }

            sb.append(f);
         }

         sb.append('"');
         throw new ParseException(String.format("Cannot parse date \"%s\": not compatible with any of standard forms (%s)", dateStr, sb.toString()), pos.getErrorIndex());
      }
   }

   public Date parse(String dateStr, ParsePosition pos) {
      try {
         return this._parseDate(dateStr, pos);
      } catch (ParseException var4) {
         return null;
      }
   }

   protected Date _parseDate(String dateStr, ParsePosition pos) throws ParseException {
      if (this.looksLikeISO8601(dateStr)) {
         return this.parseAsISO8601(dateStr, pos);
      } else {
         int i = dateStr.length();

         char ch;
         do {
            --i;
            if (i < 0) {
               break;
            }

            ch = dateStr.charAt(i);
         } while(ch >= '0' && ch <= '9' || i <= 0 && ch == '-');

         return i >= 0 || dateStr.charAt(0) != '-' && !NumberInput.inLongRange(dateStr, false) ? this.parseAsRFC1123(dateStr, pos) : this._parseDateFromLong(dateStr, pos);
      }
   }

   public StringBuffer format(Date date, StringBuffer toAppendTo, FieldPosition fieldPosition) {
      TimeZone tz = this._timezone;
      if (tz == null) {
         tz = DEFAULT_TIMEZONE;
      }

      _format(tz, this._locale, date, toAppendTo);
      return toAppendTo;
   }

   protected static void _format(TimeZone tz, Locale loc, Date date, StringBuffer buffer) {
      Calendar calendar = new GregorianCalendar(tz, loc);
      calendar.setTime(date);
      pad4(buffer, calendar.get(1));
      buffer.append('-');
      pad2(buffer, calendar.get(2) + 1);
      buffer.append('-');
      pad2(buffer, calendar.get(5));
      buffer.append('T');
      pad2(buffer, calendar.get(11));
      buffer.append(':');
      pad2(buffer, calendar.get(12));
      buffer.append(':');
      pad2(buffer, calendar.get(13));
      buffer.append('.');
      pad3(buffer, calendar.get(14));
      int offset = tz.getOffset(calendar.getTimeInMillis());
      if (offset != 0) {
         int hours = Math.abs(offset / '\uea60' / 60);
         int minutes = Math.abs(offset / '\uea60' % 60);
         buffer.append((char)(offset < 0 ? '-' : '+'));
         pad2(buffer, hours);
         pad2(buffer, minutes);
      } else {
         buffer.append("+0000");
      }

   }

   private static void pad2(StringBuffer buffer, int value) {
      int tens = value / 10;
      if (tens == 0) {
         buffer.append('0');
      } else {
         buffer.append((char)(48 + tens));
         value -= 10 * tens;
      }

      buffer.append((char)(48 + value));
   }

   private static void pad3(StringBuffer buffer, int value) {
      int h = value / 100;
      if (h == 0) {
         buffer.append('0');
      } else {
         buffer.append((char)(48 + h));
         value -= h * 100;
      }

      pad2(buffer, value);
   }

   private static void pad4(StringBuffer buffer, int value) {
      int h = value / 100;
      if (h == 0) {
         buffer.append('0').append('0');
      } else {
         pad2(buffer, h);
         value -= 100 * h;
      }

      pad2(buffer, value);
   }

   public String toString() {
      return String.format("DateFormat %s: (timezone: %s, locale: %s, lenient: %s)", this.getClass().getName(), this._timezone, this._locale, this._lenient);
   }

   public String toPattern() {
      StringBuilder sb = new StringBuilder(100);
      sb.append("[one of: '").append("yyyy-MM-dd'T'HH:mm:ss.SSSZ").append("', '").append("EEE, dd MMM yyyy HH:mm:ss zzz").append("' (");
      sb.append(Boolean.FALSE.equals(this._lenient) ? "strict" : "lenient").append(")]");
      return sb.toString();
   }

   public boolean equals(Object o) {
      return o == this;
   }

   public int hashCode() {
      return System.identityHashCode(this);
   }

   protected boolean looksLikeISO8601(String dateStr) {
      return dateStr.length() >= 7 && Character.isDigit(dateStr.charAt(0)) && Character.isDigit(dateStr.charAt(3)) && dateStr.charAt(4) == '-' && Character.isDigit(dateStr.charAt(5));
   }

   private Date _parseDateFromLong(String longStr, ParsePosition pos) throws ParseException {
      long ts;
      try {
         ts = NumberInput.parseLong(longStr);
      } catch (NumberFormatException var6) {
         throw new ParseException(String.format("Timestamp value %s out of 64-bit value range", longStr), pos.getErrorIndex());
      }

      return new Date(ts);
   }

   protected Date parseAsISO8601(String dateStr, ParsePosition pos) throws ParseException {
      try {
         return this._parseAsISO8601(dateStr, pos);
      } catch (IllegalArgumentException var4) {
         throw new ParseException(String.format("Cannot parse date \"%s\", problem: %s", dateStr, var4.getMessage()), pos.getErrorIndex());
      }
   }

   protected Date _parseAsISO8601(String dateStr, ParsePosition pos) throws IllegalArgumentException, ParseException {
      int totalLen = dateStr.length();
      TimeZone tz = DEFAULT_TIMEZONE;
      if (this._timezone != null && 'Z' != dateStr.charAt(totalLen - 1)) {
         tz = this._timezone;
      }

      Calendar cal = new GregorianCalendar(tz, this._locale);
      if (this._lenient != null) {
         cal.setLenient(this._lenient);
      }

      String formatStr;
      Matcher m;
      int start;
      int end;
      int len;
      if (totalLen <= 10) {
         m = PATTERN_PLAIN.matcher(dateStr);
         if (m.matches()) {
            start = _parse4D(dateStr, 0);
            end = _parse2D(dateStr, 5) - 1;
            len = _parse2D(dateStr, 8);
            cal.set(start, end, len, 0, 0, 0);
            cal.set(14, 0);
            return cal.getTime();
         }

         formatStr = "yyyy-MM-dd";
      } else {
         m = PATTERN_ISO8601.matcher(dateStr);
         if (m.matches()) {
            start = m.start(2);
            end = m.end(2);
            len = end - start;
            int offsetSecs;
            if (len > 1) {
               offsetSecs = _parse2D(dateStr, start + 1) * 3600;
               if (len >= 5) {
                  offsetSecs += _parse2D(dateStr, end - 2);
               }

               if (dateStr.charAt(start) == '-') {
                  offsetSecs *= -1000;
               } else {
                  offsetSecs *= 1000;
               }

               cal.set(15, offsetSecs);
               cal.set(16, 0);
            }

            offsetSecs = _parse4D(dateStr, 0);
            int month = _parse2D(dateStr, 5) - 1;
            int day = _parse2D(dateStr, 8);
            int hour = _parse2D(dateStr, 11);
            int minute = _parse2D(dateStr, 14);
            int seconds;
            if (totalLen > 16 && dateStr.charAt(16) == ':') {
               seconds = _parse2D(dateStr, 17);
            } else {
               seconds = 0;
            }

            cal.set(offsetSecs, month, day, hour, minute, seconds);
            start = m.start(1) + 1;
            end = m.end(1);
            int msecs = false;
            if (start >= end) {
               cal.set(14, 0);
            } else {
               int msecs = 0;
               switch(end - start) {
               case 3:
                  msecs += dateStr.charAt(start + 2) - 48;
               case 2:
                  msecs += 10 * (dateStr.charAt(start + 1) - 48);
               case 1:
                  msecs += 100 * (dateStr.charAt(start) - 48);
                  cal.set(14, msecs);
                  break;
               default:
                  throw new ParseException(String.format("Cannot parse date \"%s\": invalid fractional seconds '%s'; can use at most 3 digits", dateStr, m.group(1).substring(1)), pos.getErrorIndex());
               }
            }

            return cal.getTime();
         }

         formatStr = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";
      }

      throw new ParseException(String.format("Cannot parse date \"%s\": while it seems to fit format '%s', parsing fails (leniency? %s)", dateStr, formatStr, this._lenient), pos.getErrorIndex());
   }

   private static int _parse4D(String str, int index) {
      return 1000 * (str.charAt(index) - 48) + 100 * (str.charAt(index + 1) - 48) + 10 * (str.charAt(index + 2) - 48) + (str.charAt(index + 3) - 48);
   }

   private static int _parse2D(String str, int index) {
      return 10 * (str.charAt(index) - 48) + (str.charAt(index + 1) - 48);
   }

   protected Date parseAsRFC1123(String dateStr, ParsePosition pos) {
      if (this._formatRFC1123 == null) {
         this._formatRFC1123 = _cloneFormat(DATE_FORMAT_RFC1123, "EEE, dd MMM yyyy HH:mm:ss zzz", this._timezone, this._locale, this._lenient);
      }

      return this._formatRFC1123.parse(dateStr, pos);
   }

   private static final DateFormat _cloneFormat(DateFormat df, String format, TimeZone tz, Locale loc, Boolean lenient) {
      Object df;
      if (!loc.equals(DEFAULT_LOCALE)) {
         df = new SimpleDateFormat(format, loc);
         ((DateFormat)df).setTimeZone(tz == null ? DEFAULT_TIMEZONE : tz);
      } else {
         df = (DateFormat)df.clone();
         if (tz != null) {
            ((DateFormat)df).setTimeZone(tz);
         }
      }

      if (lenient != null) {
         ((DateFormat)df).setLenient(lenient);
      }

      return (DateFormat)df;
   }

   protected void _clearFormats() {
      this._formatRFC1123 = null;
   }

   protected static <T> boolean _equals(T value1, T value2) {
      if (value1 == value2) {
         return true;
      } else {
         return value1 != null && value1.equals(value2);
      }
   }

   static {
      Pattern p = null;

      try {
         p = Pattern.compile("\\d\\d\\d\\d[-]\\d\\d[-]\\d\\d[T]\\d\\d[:]\\d\\d(?:[:]\\d\\d)?(\\.\\d+)?(Z|[+-]\\d\\d(?:[:]?\\d\\d)?)?");
      } catch (Throwable var2) {
         throw new RuntimeException(var2);
      }

      PATTERN_ISO8601 = p;
      ALL_FORMATS = new String[]{"yyyy-MM-dd'T'HH:mm:ss.SSSZ", "yyyy-MM-dd'T'HH:mm:ss.SSS", "EEE, dd MMM yyyy HH:mm:ss zzz", "yyyy-MM-dd"};
      DEFAULT_TIMEZONE = TimeZone.getTimeZone("UTC");
      DEFAULT_LOCALE = Locale.US;
      DATE_FORMAT_RFC1123 = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz", DEFAULT_LOCALE);
      DATE_FORMAT_RFC1123.setTimeZone(DEFAULT_TIMEZONE);
      DATE_FORMAT_ISO8601 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", DEFAULT_LOCALE);
      DATE_FORMAT_ISO8601.setTimeZone(DEFAULT_TIMEZONE);
      instance = new StdDateFormat();
   }
}
