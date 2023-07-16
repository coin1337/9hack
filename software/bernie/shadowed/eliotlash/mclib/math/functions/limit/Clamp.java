package software.bernie.shadowed.eliotlash.mclib.math.functions.limit;

import software.bernie.shadowed.eliotlash.mclib.math.IValue;
import software.bernie.shadowed.eliotlash.mclib.math.functions.Function;
import software.bernie.shadowed.eliotlash.mclib.utils.MathUtils;

public class Clamp extends Function {
   public Clamp(IValue[] values, String name) throws Exception {
      super(values, name);
   }

   public int getRequiredArguments() {
      return 3;
   }

   public double get() {
      return MathUtils.clamp(this.getArg(0), this.getArg(1), this.getArg(2));
   }
}
