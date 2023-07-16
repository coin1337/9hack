package software.bernie.shadowed.eliotlash.mclib.math.functions.rounding;

import software.bernie.shadowed.eliotlash.mclib.math.IValue;
import software.bernie.shadowed.eliotlash.mclib.math.functions.Function;

public class Floor extends Function {
   public Floor(IValue[] values, String name) throws Exception {
      super(values, name);
   }

   public int getRequiredArguments() {
      return 1;
   }

   public double get() {
      return Math.floor(this.getArg(0));
   }
}
