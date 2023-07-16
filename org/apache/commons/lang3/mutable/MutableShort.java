package org.apache.commons.lang3.mutable;

import org.apache.commons.lang3.math.NumberUtils;

public class MutableShort extends Number implements Comparable<MutableShort>, Mutable<Number> {
   private static final long serialVersionUID = -2135791679L;
   private short value;

   public MutableShort() {
   }

   public MutableShort(short value) {
      this.value = value;
   }

   public MutableShort(Number value) {
      this.value = value.shortValue();
   }

   public MutableShort(String value) {
      this.value = Short.parseShort(value);
   }

   public Short getValue() {
      return this.value;
   }

   public void setValue(short value) {
      this.value = value;
   }

   public void setValue(Number value) {
      this.value = value.shortValue();
   }

   public void increment() {
      ++this.value;
   }

   public short getAndIncrement() {
      short last = this.value++;
      return last;
   }

   public short incrementAndGet() {
      ++this.value;
      return this.value;
   }

   public void decrement() {
      --this.value;
   }

   public short getAndDecrement() {
      short last = this.value--;
      return last;
   }

   public short decrementAndGet() {
      --this.value;
      return this.value;
   }

   public void add(short operand) {
      this.value += operand;
   }

   public void add(Number operand) {
      this.value += operand.shortValue();
   }

   public void subtract(short operand) {
      this.value -= operand;
   }

   public void subtract(Number operand) {
      this.value -= operand.shortValue();
   }

   public short addAndGet(short operand) {
      this.value += operand;
      return this.value;
   }

   public short addAndGet(Number operand) {
      this.value += operand.shortValue();
      return this.value;
   }

   public short getAndAdd(short operand) {
      short last = this.value;
      this.value += operand;
      return last;
   }

   public short getAndAdd(Number operand) {
      short last = this.value;
      this.value += operand.shortValue();
      return last;
   }

   public short shortValue() {
      return this.value;
   }

   public int intValue() {
      return this.value;
   }

   public long longValue() {
      return (long)this.value;
   }

   public float floatValue() {
      return (float)this.value;
   }

   public double doubleValue() {
      return (double)this.value;
   }

   public Short toShort() {
      return this.shortValue();
   }

   public boolean equals(Object obj) {
      if (obj instanceof MutableShort) {
         return this.value == ((MutableShort)obj).shortValue();
      } else {
         return false;
      }
   }

   public int hashCode() {
      return this.value;
   }

   public int compareTo(MutableShort other) {
      return NumberUtils.compare(this.value, other.value);
   }

   public String toString() {
      return String.valueOf(this.value);
   }
}
