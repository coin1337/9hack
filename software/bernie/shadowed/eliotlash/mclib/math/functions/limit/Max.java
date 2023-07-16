package software.bernie.shadowed.eliotlash.mclib.math.functions.limit;

import software.bernie.shadowed.eliotlash.mclib.math.IValue;
import software.bernie.shadowed.eliotlash.mclib.math.functions.Function;

public class Max extends Function {
   public Max(IValue[] values, String name) throws Exception {
      super(values, name);
   }

   public int getRequiredArguments() {
      return 2;
   }

   public double get() {
      return Math.max(this.getArg(0), this.getArg(1));
   }
}
