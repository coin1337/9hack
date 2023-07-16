package software.bernie.geckolib3.core;

import software.bernie.shadowed.eliotlash.mclib.math.IValue;

public class ConstantValue implements IValue {
   private final double value;

   public ConstantValue(double value) {
      this.value = value;
   }

   public double get() {
      return this.value;
   }

   public static ConstantValue fromDouble(double d) {
      return new ConstantValue(d);
   }

   public static ConstantValue fromFloat(float d) {
      return new ConstantValue((double)d);
   }

   public static ConstantValue parseDouble(String s) {
      return new ConstantValue(Double.parseDouble(s));
   }

   public static ConstantValue parseFloat(String s) {
      return new ConstantValue((double)Float.parseFloat(s));
   }

   public static ConstantValue subtract(IValue first, IValue second) {
      return fromDouble(first.get() - second.get());
   }
}
