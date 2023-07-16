package software.bernie.shadowed.eliotlash.molang.expressions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;
import software.bernie.shadowed.eliotlash.mclib.math.Variable;
import software.bernie.shadowed.eliotlash.molang.MolangParser;

public class MolangMultiStatement extends MolangExpression {
   public List<MolangExpression> expressions = new ArrayList();
   public Map<String, Variable> locals = new HashMap();

   public MolangMultiStatement(MolangParser context) {
      super(context);
   }

   public double get() {
      double value = 0.0D;

      MolangExpression expression;
      for(Iterator var3 = this.expressions.iterator(); var3.hasNext(); value = expression.get()) {
         expression = (MolangExpression)var3.next();
      }

      return value;
   }

   public String toString() {
      StringJoiner builder = new StringJoiner("; ");
      Iterator var2 = this.expressions.iterator();

      while(var2.hasNext()) {
         MolangExpression expression = (MolangExpression)var2.next();
         builder.add(expression.toString());
         if (expression instanceof MolangValue && ((MolangValue)expression).returns) {
            break;
         }
      }

      return builder.toString();
   }
}
