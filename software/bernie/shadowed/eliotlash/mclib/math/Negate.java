package software.bernie.shadowed.eliotlash.mclib.math;

public class Negate implements IValue {
   public IValue value;

   public Negate(IValue value) {
      this.value = value;
   }

   public double get() {
      return this.value.get() == 0.0D ? 1.0D : 0.0D;
   }

   public String toString() {
      return "!" + this.value.toString();
   }
}
