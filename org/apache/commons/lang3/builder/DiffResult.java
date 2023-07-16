package org.apache.commons.lang3.builder;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.apache.commons.lang3.Validate;

public class DiffResult<T> implements Iterable<Diff<?>> {
   public static final String OBJECTS_SAME_STRING = "";
   private static final String DIFFERS_STRING = "differs from";
   private final List<Diff<?>> diffs;
   private final T lhs;
   private final T rhs;
   private final ToStringStyle style;

   DiffResult(T lhs, T rhs, List<Diff<?>> diffs, ToStringStyle style) {
      Validate.notNull(lhs, "Left hand object cannot be null");
      Validate.notNull(rhs, "Right hand object cannot be null");
      Validate.notNull(diffs, "List of differences cannot be null");
      this.diffs = diffs;
      this.lhs = lhs;
      this.rhs = rhs;
      if (style == null) {
         this.style = ToStringStyle.DEFAULT_STYLE;
      } else {
         this.style = style;
      }

   }

   public T getLeft() {
      return this.lhs;
   }

   public T getRight() {
      return this.rhs;
   }

   public List<Diff<?>> getDiffs() {
      return Collections.unmodifiableList(this.diffs);
   }

   public int getNumberOfDiffs() {
      return this.diffs.size();
   }

   public ToStringStyle getToStringStyle() {
      return this.style;
   }

   public String toString() {
      return this.toString(this.style);
   }

   public String toString(ToStringStyle style) {
      if (this.diffs.isEmpty()) {
         return "";
      } else {
         ToStringBuilder lhsBuilder = new ToStringBuilder(this.lhs, style);
         ToStringBuilder rhsBuilder = new ToStringBuilder(this.rhs, style);
         Iterator var4 = this.diffs.iterator();

         while(var4.hasNext()) {
            Diff<?> diff = (Diff)var4.next();
            lhsBuilder.append(diff.getFieldName(), diff.getLeft());
            rhsBuilder.append(diff.getFieldName(), diff.getRight());
         }

         return String.format("%s %s %s", lhsBuilder.build(), "differs from", rhsBuilder.build());
      }
   }

   public Iterator<Diff<?>> iterator() {
      return this.diffs.iterator();
   }
}
