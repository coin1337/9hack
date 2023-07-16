package org.apache.commons.lang3.mutable;

import org.apache.commons.lang3.math.NumberUtils;

public class MutableByte extends Number implements Comparable<MutableByte>, Mutable<Number> {
   private static final long serialVersionUID = -1585823265L;
   private byte value;

   public MutableByte() {
   }

   public MutableByte(byte value) {
      this.value = value;
   }

   public MutableByte(Number value) {
      this.value = value.byteValue();
   }

   public MutableByte(String value) {
      this.value = Byte.parseByte(value);
   }

   public Byte getValue() {
      return this.value;
   }

   public void setValue(byte value) {
      this.value = value;
   }

   public void setValue(Number value) {
      this.value = value.byteValue();
   }

   public void increment() {
      ++this.value;
   }

   public byte getAndIncrement() {
      byte last = this.value++;
      return last;
   }

   public byte incrementAndGet() {
      ++this.value;
      return this.value;
   }

   public void decrement() {
      --this.value;
   }

   public byte getAndDecrement() {
      byte last = this.value--;
      return last;
   }

   public byte decrementAndGet() {
      --this.value;
      return this.value;
   }

   public void add(byte operand) {
      this.value += operand;
   }

   public void add(Number operand) {
      this.value += operand.byteValue();
   }

   public void subtract(byte operand) {
      this.value -= operand;
   }

   public void subtract(Number operand) {
      this.value -= operand.byteValue();
   }

   public byte addAndGet(byte operand) {
      this.value += operand;
      return this.value;
   }

   public byte addAndGet(Number operand) {
      this.value += operand.byteValue();
      return this.value;
   }

   public byte getAndAdd(byte operand) {
      byte last = this.value;
      this.value += operand;
      return last;
   }

   public byte getAndAdd(Number operand) {
      byte last = this.value;
      this.value += operand.byteValue();
      return last;
   }

   public byte byteValue() {
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

   public Byte toByte() {
      return this.byteValue();
   }

   public boolean equals(Object obj) {
      if (obj instanceof MutableByte) {
         return this.value == ((MutableByte)obj).byteValue();
      } else {
         return false;
      }
   }

   public int hashCode() {
      return this.value;
   }

   public int compareTo(MutableByte other) {
      return NumberUtils.compare(this.value, other.value);
   }

   public String toString() {
      return String.valueOf(this.value);
   }
}
