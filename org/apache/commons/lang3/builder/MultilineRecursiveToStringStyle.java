package org.apache.commons.lang3.builder;

import org.apache.commons.lang3.ClassUtils;

public class MultilineRecursiveToStringStyle extends RecursiveToStringStyle {
   private static final long serialVersionUID = 1L;
   private static final int INDENT = 2;
   private int spaces = 2;

   public MultilineRecursiveToStringStyle() {
      this.resetIndent();
   }

   private void resetIndent() {
      this.setArrayStart("{" + System.lineSeparator() + this.spacer(this.spaces));
      this.setArraySeparator("," + System.lineSeparator() + this.spacer(this.spaces));
      this.setArrayEnd(System.lineSeparator() + this.spacer(this.spaces - 2) + "}");
      this.setContentStart("[" + System.lineSeparator() + this.spacer(this.spaces));
      this.setFieldSeparator("," + System.lineSeparator() + this.spacer(this.spaces));
      this.setContentEnd(System.lineSeparator() + this.spacer(this.spaces - 2) + "]");
   }

   private StringBuilder spacer(int spaces) {
      StringBuilder sb = new StringBuilder();

      for(int i = 0; i < spaces; ++i) {
         sb.append(" ");
      }

      return sb;
   }

   public void appendDetail(StringBuffer buffer, String fieldName, Object value) {
      if (!ClassUtils.isPrimitiveWrapper(value.getClass()) && !String.class.equals(value.getClass()) && this.accept(value.getClass())) {
         this.spaces += 2;
         this.resetIndent();
         buffer.append(ReflectionToStringBuilder.toString(value, this));
         this.spaces -= 2;
         this.resetIndent();
      } else {
         super.appendDetail(buffer, fieldName, value);
      }

   }

   protected void appendDetail(StringBuffer buffer, String fieldName, Object[] array) {
      this.spaces += 2;
      this.resetIndent();
      super.appendDetail(buffer, fieldName, (Object[])array);
      this.spaces -= 2;
      this.resetIndent();
   }

   protected void reflectionAppendArrayDetail(StringBuffer buffer, String fieldName, Object array) {
      this.spaces += 2;
      this.resetIndent();
      super.reflectionAppendArrayDetail(buffer, fieldName, array);
      this.spaces -= 2;
      this.resetIndent();
   }

   protected void appendDetail(StringBuffer buffer, String fieldName, long[] array) {
      this.spaces += 2;
      this.resetIndent();
      super.appendDetail(buffer, fieldName, (long[])array);
      this.spaces -= 2;
      this.resetIndent();
   }

   protected void appendDetail(StringBuffer buffer, String fieldName, int[] array) {
      this.spaces += 2;
      this.resetIndent();
      super.appendDetail(buffer, fieldName, (int[])array);
      this.spaces -= 2;
      this.resetIndent();
   }

   protected void appendDetail(StringBuffer buffer, String fieldName, short[] array) {
      this.spaces += 2;
      this.resetIndent();
      super.appendDetail(buffer, fieldName, (short[])array);
      this.spaces -= 2;
      this.resetIndent();
   }

   protected void appendDetail(StringBuffer buffer, String fieldName, byte[] array) {
      this.spaces += 2;
      this.resetIndent();
      super.appendDetail(buffer, fieldName, (byte[])array);
      this.spaces -= 2;
      this.resetIndent();
   }

   protected void appendDetail(StringBuffer buffer, String fieldName, char[] array) {
      this.spaces += 2;
      this.resetIndent();
      super.appendDetail(buffer, fieldName, (char[])array);
      this.spaces -= 2;
      this.resetIndent();
   }

   protected void appendDetail(StringBuffer buffer, String fieldName, double[] array) {
      this.spaces += 2;
      this.resetIndent();
      super.appendDetail(buffer, fieldName, (double[])array);
      this.spaces -= 2;
      this.resetIndent();
   }

   protected void appendDetail(StringBuffer buffer, String fieldName, float[] array) {
      this.spaces += 2;
      this.resetIndent();
      super.appendDetail(buffer, fieldName, (float[])array);
      this.spaces -= 2;
      this.resetIndent();
   }

   protected void appendDetail(StringBuffer buffer, String fieldName, boolean[] array) {
      this.spaces += 2;
      this.resetIndent();
      super.appendDetail(buffer, fieldName, (boolean[])array);
      this.spaces -= 2;
      this.resetIndent();
   }
}
