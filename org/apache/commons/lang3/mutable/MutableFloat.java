package org.apache.commons.lang3.mutable;

public class MutableFloat extends Number implements Comparable<MutableFloat>, Mutable<Number> {
   private static final long serialVersionUID = 5787169186L;
   private float value;

   public MutableFloat() {
   }

   public MutableFloat(float value) {
      this.value = value;
   }

   public MutableFloat(Number value) {
      this.value = value.floatValue();
   }

   public MutableFloat(String value) {
      this.value = Float.parseFloat(value);
   }

   public Float getValue() {
      return this.value;
   }

   public void setValue(float value) {
      this.value = value;
   }

   public void setValue(Number value) {
      this.value = value.floatValue();
   }

   public boolean isNaN() {
      return Float.isNaN(this.value);
   }

   public boolean isInfinite() {
      return Float.isInfinite(this.value);
   }

   public void increment() {
      ++this.value;
   }

   public float getAndIncrement() {
      float last = (float)(this.value++);
      return last;
   }

   public float incrementAndGet() {
      ++this.value;
      return this.value;
   }

   public void decrement() {
      --this.value;
   }

   public float getAndDecrement() {
      float last = (float)(this.value--);
      return last;
   }

   public float decrementAndGet() {
      --this.value;
      return this.value;
   }

   public void add(float operand) {
      this.value += operand;
   }

   public void add(Number operand) {
      this.value += operand.floatValue();
   }

   public void subtract(float operand) {
      this.value -= operand;
   }

   public void subtract(Number operand) {
      this.value -= operand.floatValue();
   }

   public float addAndGet(float operand) {
      this.value += operand;
      return this.value;
   }

   public float addAndGet(Number operand) {
      this.value += operand.floatValue();
      return this.value;
   }

   public float getAndAdd(float operand) {
      float last = this.value;
      this.value += operand;
      return last;
   }

   public float getAndAdd(Number operand) {
      float last = this.value;
      this.value += operand.floatValue();
      return last;
   }

   public int intValue() {
      return (int)this.value;
   }

   public long longValue() {
      return (long)this.value;
   }

   public float floatValue() {
      return this.value;
   }

   public double doubleValue() {
      return (double)this.value;
   }

   public Float toFloat() {
      return this.floatValue();
   }

   public boolean equals(Object obj) {
      return obj instanceof MutableFloat && Float.floatToIntBits(((MutableFloat)obj).value) == Float.floatToIntBits(this.value);
   }

   public int hashCode() {
      return Float.floatToIntBits(this.value);
   }

   public int compareTo(MutableFloat other) {
      return Float.compare(this.value, other.value);
   }

   public String toString() {
      return String.valueOf(this.value);
   }
}
