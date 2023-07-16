package software.bernie.shadowed.fasterxml.jackson.databind.util;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.FieldPosition;
import java.text.ParseException;
import java.text.ParsePosition;
import java.util.Date;
import java.util.GregorianCalendar;

/** @deprecated */
@Deprecated
public class ISO8601DateFormat extends DateFormat {
   private static final long serialVersionUID = 1L;

   public ISO8601DateFormat() {
      this.numberFormat = new DecimalFormat();
      this.calendar = new GregorianCalendar();
   }

   public StringBuffer format(Date date, StringBuffer toAppendTo, FieldPosition fieldPosition) {
      toAppendTo.append(ISO8601Utils.format(date));
      return toAppendTo;
   }

   public Date parse(String source, ParsePosition pos) {
      try {
         return ISO8601Utils.parse(source, pos);
      } catch (ParseException var4) {
         return null;
      }
   }

   public Date parse(String source) throws ParseException {
      return ISO8601Utils.parse(source, new ParsePosition(0));
   }

   public Object clone() {
      return this;
   }
}
