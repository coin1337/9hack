package org.apache.commons.lang3.time;

import java.util.Calendar;
import java.util.Objects;

public class CalendarUtils {
   public static final CalendarUtils INSTANCE = new CalendarUtils(Calendar.getInstance());
   private final Calendar calendar;

   public CalendarUtils(Calendar calendar) {
      this.calendar = (Calendar)Objects.requireNonNull(calendar, "calendar");
   }

   public int getDayOfMonth() {
      return this.calendar.get(5);
   }

   public int getMonth() {
      return this.calendar.get(2);
   }

   public int getYear() {
      return this.calendar.get(1);
   }
}
