package org.apache.commons.lang3.compare;

import java.io.Serializable;
import java.util.Comparator;

public final class ObjectToStringComparator implements Comparator<Object>, Serializable {
   public static final ObjectToStringComparator INSTANCE = new ObjectToStringComparator();
   private static final long serialVersionUID = 1L;

   public int compare(Object o1, Object o2) {
      if (o1 == null && o2 == null) {
         return 0;
      } else if (o1 == null) {
         return 1;
      } else if (o2 == null) {
         return -1;
      } else {
         String string1 = o1.toString();
         String string2 = o2.toString();
         if (string1 == null && string2 == null) {
            return 0;
         } else if (string1 == null) {
            return 1;
         } else {
            return string2 == null ? -1 : string1.compareTo(string2);
         }
      }
   }
}
