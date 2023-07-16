package software.bernie.shadowed.eliotlash.molang.functions;

import software.bernie.shadowed.eliotlash.mclib.math.IValue;
import software.bernie.shadowed.eliotlash.mclib.math.functions.Function;

public class CosDegrees extends Function {
   public CosDegrees(IValue[] values, String name) throws Exception {
      super(values, name);
   }

   public int getRequiredArguments() {
      return 1;
   }

   public double get() {
      return Math.cos(this.getArg(0) / 180.0D * 3.141592653589793D);
   }
}
