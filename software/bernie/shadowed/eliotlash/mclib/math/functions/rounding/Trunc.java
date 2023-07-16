package software.bernie.shadowed.eliotlash.mclib.math.functions.rounding;

import software.bernie.shadowed.eliotlash.mclib.math.IValue;
import software.bernie.shadowed.eliotlash.mclib.math.functions.Function;

public class Trunc extends Function {
   public Trunc(IValue[] values, String name) throws Exception {
      super(values, name);
   }

   public int getRequiredArguments() {
      return 1;
   }

   public double get() {
      double value = this.getArg(0);
      return value < 0.0D ? Math.ceil(value) : Math.floor(value);
   }
}
