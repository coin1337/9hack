package org.apache.commons.lang3.time;

import java.util.Date;
import java.util.TimeZone;

class GmtTimeZone extends TimeZone {
   private static final int MILLISECONDS_PER_MINUTE = 60000;
   private static final int MINUTES_PER_HOUR = 60;
   private static final int HOURS_PER_DAY = 24;
   static final long serialVersionUID = 1L;
   private final int offset;
   private final String zoneId;

   GmtTimeZone(boolean negate, int hours, int minutes) {
      if (hours >= 24) {
         throw new IllegalArgumentException(hours + " hours out of range");
      } else if (minutes >= 60) {
         throw new IllegalArgumentException(minutes + " minutes out of range");
      } else {
         int milliseconds = (minutes + hours * 60) * '\uea60';
         this.offset = negate ? -milliseconds : milliseconds;
         this.zoneId = twoDigits(twoDigits((new StringBuilder(9)).append("GMT").append((char)(negate ? '-' : '+')), hours).append(':'), minutes).toString();
      }
   }

   private static StringBuilder twoDigits(StringBuilder sb, int n) {
      return sb.append((char)(48 + n / 10)).append((char)(48 + n % 10));
   }

   public int getOffset(int era, int year, int month, int day, int dayOfWeek, int milliseconds) {
      return this.offset;
   }

   public void setRawOffset(int offsetMillis) {
      throw new UnsupportedOperationException();
   }

   public int getRawOffset() {
      return this.offset;
   }

   public String getID() {
      return this.zoneId;
   }

   public boolean useDaylightTime() {
      return false;
   }

   public boolean inDaylightTime(Date date) {
      return false;
   }

   public String toString() {
      return "[GmtTimeZone id=\"" + this.zoneId + "\",offset=" + this.offset + ']';
   }

   public int hashCode() {
      return this.offset;
   }

   public boolean equals(Object other) {
      if (!(other instanceof GmtTimeZone)) {
         return false;
      } else {
         return this.zoneId == ((GmtTimeZone)other).zoneId;
      }
   }
}
