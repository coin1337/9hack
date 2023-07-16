package software.bernie.shadowed.eliotlash.mclib.math.functions.classic;

import software.bernie.shadowed.eliotlash.mclib.math.IValue;
import software.bernie.shadowed.eliotlash.mclib.math.functions.Function;

public class Abs extends Function {
   public Abs(IValue[] values, String name) throws Exception {
      super(values, name);
   }

   public int getRequiredArguments() {
      return 1;
   }

   public double get() {
      return Math.abs(this.getArg(0));
   }
}
