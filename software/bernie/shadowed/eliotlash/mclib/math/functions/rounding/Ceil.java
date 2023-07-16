package software.bernie.shadowed.eliotlash.mclib.math.functions.rounding;

import software.bernie.shadowed.eliotlash.mclib.math.IValue;
import software.bernie.shadowed.eliotlash.mclib.math.functions.Function;

public class Ceil extends Function {
   public Ceil(IValue[] values, String name) throws Exception {
      super(values, name);
   }

   public int getRequiredArguments() {
      return 1;
   }

   public double get() {
      return Math.ceil(this.getArg(0));
   }
}
