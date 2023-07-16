package software.bernie.shadowed.eliotlash.mclib.math.functions.utility;

import software.bernie.shadowed.eliotlash.mclib.math.IValue;
import software.bernie.shadowed.eliotlash.mclib.math.functions.Function;
import software.bernie.shadowed.eliotlash.mclib.utils.Interpolations;

public class Lerp extends Function {
   public Lerp(IValue[] values, String name) throws Exception {
      super(values, name);
   }

   public int getRequiredArguments() {
      return 3;
   }

   public double get() {
      return Interpolations.lerp(this.getArg(0), this.getArg(1), this.getArg(2));
   }
}
