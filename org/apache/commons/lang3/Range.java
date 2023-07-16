package org.apache.commons.lang3;

import java.io.Serializable;
import java.util.Comparator;

public final class Range<T> implements Serializable {
   private static final long serialVersionUID = 1L;
   private final Comparator<T> comparator;
   private transient int hashCode;
   private final T maximum;
   private final T minimum;
   private transient String toString;

   public static <T extends Comparable<T>> Range<T> between(T fromInclusive, T toInclusive) {
      return between(fromInclusive, toInclusive, (Comparator)null);
   }

   public static <T> Range<T> between(T fromInclusive, T toInclusive, Comparator<T> comparator) {
      return new Range(fromInclusive, toInclusive, comparator);
   }

   public static <T extends Comparable<T>> Range<T> is(T element) {
      return between(element, element, (Comparator)null);
   }

   public static <T> Range<T> is(T element, Comparator<T> comparator) {
      return between(element, element, comparator);
   }

   private Range(T element1, T element2, Comparator<T> comp) {
      if (element1 != null && element2 != null) {
         if (comp == null) {
            this.comparator = Range.ComparableComparator.INSTANCE;
         } else {
            this.comparator = comp;
         }

         if (this.comparator.compare(element1, element2) < 1) {
            this.minimum = element1;
            this.maximum = element2;
         } else {
            this.minimum = element2;
            this.maximum = element1;
         }

      } else {
         throw new IllegalArgumentException("Elements in a range must not be null: element1=" + element1 + ", element2=" + element2);
      }
   }

   public boolean contains(T element) {
      if (element == null) {
         return false;
      } else {
         return this.comparator.compare(element, this.minimum) > -1 && this.comparator.compare(element, this.maximum) < 1;
      }
   }

   public boolean containsRange(Range<T> otherRange) {
      if (otherRange == null) {
         return false;
      } else {
         return this.contains(otherRange.minimum) && this.contains(otherRange.maximum);
      }
   }

   public int elementCompareTo(T element) {
      Validate.notNull(element, "Element is null");
      if (this.isAfter(element)) {
         return -1;
      } else {
         return this.isBefore(element) ? 1 : 0;
      }
   }

   public boolean equals(Object obj) {
      if (obj == this) {
         return true;
      } else if (obj != null && obj.getClass() == this.getClass()) {
         Range<T> range = (Range)obj;
         return this.minimum.equals(range.minimum) && this.maximum.equals(range.maximum);
      } else {
         return false;
      }
   }

   public Comparator<T> getComparator() {
      return this.comparator;
   }

   public T getMaximum() {
      return this.maximum;
   }

   public T getMinimum() {
      return this.minimum;
   }

   public int hashCode() {
      int result = this.hashCode;
      if (this.hashCode == 0) {
         int result = 17;
         result = 37 * result + this.getClass().hashCode();
         result = 37 * result + this.minimum.hashCode();
         result = 37 * result + this.maximum.hashCode();
         this.hashCode = result;
      }

      return result;
   }

   public Range<T> intersectionWith(Range<T> other) {
      if (!this.isOverlappedBy(other)) {
         throw new IllegalArgumentException(String.format("Cannot calculate intersection with non-overlapping range %s", other));
      } else if (this.equals(other)) {
         return this;
      } else {
         T min = this.getComparator().compare(this.minimum, other.minimum) < 0 ? other.minimum : this.minimum;
         T max = this.getComparator().compare(this.maximum, other.maximum) < 0 ? this.maximum : other.maximum;
         return between(min, max, this.getComparator());
      }
   }

   public boolean isAfter(T element) {
      if (element == null) {
         return false;
      } else {
         return this.comparator.compare(element, this.minimum) < 0;
      }
   }

   public boolean isAfterRange(Range<T> otherRange) {
      return otherRange == null ? false : this.isAfter(otherRange.maximum);
   }

   public boolean isBefore(T element) {
      if (element == null) {
         return false;
      } else {
         return this.comparator.compare(element, this.maximum) > 0;
      }
   }

   public boolean isBeforeRange(Range<T> otherRange) {
      return otherRange == null ? false : this.isBefore(otherRange.minimum);
   }

   public boolean isEndedBy(T element) {
      if (element == null) {
         return false;
      } else {
         return this.comparator.compare(element, this.maximum) == 0;
      }
   }

   public boolean isNaturalOrdering() {
      return this.comparator == Range.ComparableComparator.INSTANCE;
   }

   public boolean isOverlappedBy(Range<T> otherRange) {
      if (otherRange == null) {
         return false;
      } else {
         return otherRange.contains(this.minimum) || otherRange.contains(this.maximum) || this.contains(otherRange.minimum);
      }
   }

   public boolean isStartedBy(T element) {
      if (element == null) {
         return false;
      } else {
         return this.comparator.compare(element, this.minimum) == 0;
      }
   }

   public T fit(T element) {
      Validate.notNull(element, "element");
      if (this.isAfter(element)) {
         return this.minimum;
      } else {
         return this.isBefore(element) ? this.maximum : element;
      }
   }

   public String toString() {
      if (this.toString == null) {
         this.toString = "[" + this.minimum + ".." + this.maximum + "]";
      }

      return this.toString;
   }

   public String toString(String format) {
      return String.format(format, this.minimum, this.maximum, this.comparator);
   }

   private static enum ComparableComparator implements Comparator {
      INSTANCE;

      public int compare(Object obj1, Object obj2) {
         return ((Comparable)obj1).compareTo(obj2);
      }
   }
}
