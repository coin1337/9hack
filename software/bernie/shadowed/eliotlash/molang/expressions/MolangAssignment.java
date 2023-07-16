package software.bernie.shadowed.eliotlash.molang.expressions;

import software.bernie.shadowed.eliotlash.mclib.math.IValue;
import software.bernie.shadowed.eliotlash.mclib.math.Variable;
import software.bernie.shadowed.eliotlash.molang.MolangParser;

public class MolangAssignment extends MolangExpression {
   public Variable variable;
   public IValue expression;

   public MolangAssignment(MolangParser context, Variable variable, IValue expression) {
      super(context);
      this.variable = variable;
      this.expression = expression;
   }

   public double get() {
      double value = this.expression.get();
      this.variable.set(value);
      return value;
   }

   public String toString() {
      return this.variable.getName() + " = " + this.expression.toString();
   }
}
