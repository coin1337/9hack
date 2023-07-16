package software.bernie.shadowed.eliotlash.mclib.math;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import software.bernie.shadowed.eliotlash.mclib.math.functions.Function;
import software.bernie.shadowed.eliotlash.mclib.math.functions.classic.Abs;
import software.bernie.shadowed.eliotlash.mclib.math.functions.classic.Cos;
import software.bernie.shadowed.eliotlash.mclib.math.functions.classic.Exp;
import software.bernie.shadowed.eliotlash.mclib.math.functions.classic.Ln;
import software.bernie.shadowed.eliotlash.mclib.math.functions.classic.Mod;
import software.bernie.shadowed.eliotlash.mclib.math.functions.classic.Pow;
import software.bernie.shadowed.eliotlash.mclib.math.functions.classic.Sin;
import software.bernie.shadowed.eliotlash.mclib.math.functions.classic.Sqrt;
import software.bernie.shadowed.eliotlash.mclib.math.functions.limit.Clamp;
import software.bernie.shadowed.eliotlash.mclib.math.functions.limit.Max;
import software.bernie.shadowed.eliotlash.mclib.math.functions.limit.Min;
import software.bernie.shadowed.eliotlash.mclib.math.functions.rounding.Ceil;
import software.bernie.shadowed.eliotlash.mclib.math.functions.rounding.Floor;
import software.bernie.shadowed.eliotlash.mclib.math.functions.rounding.Round;
import software.bernie.shadowed.eliotlash.mclib.math.functions.rounding.Trunc;
import software.bernie.shadowed.eliotlash.mclib.math.functions.utility.Lerp;
import software.bernie.shadowed.eliotlash.mclib.math.functions.utility.LerpRotate;
import software.bernie.shadowed.eliotlash.mclib.math.functions.utility.Random;

public class MathBuilder {
   public Map<String, Variable> variables = new HashMap();
   public Map<String, Class<? extends Function>> functions = new HashMap();

   public MathBuilder() {
      this.register(new Variable("PI", 3.141592653589793D));
      this.register(new Variable("E", 2.718281828459045D));
      this.functions.put("floor", Floor.class);
      this.functions.put("round", Round.class);
      this.functions.put("ceil", Ceil.class);
      this.functions.put("trunc", Trunc.class);
      this.functions.put("clamp", Clamp.class);
      this.functions.put("max", Max.class);
      this.functions.put("min", Min.class);
      this.functions.put("abs", Abs.class);
      this.functions.put("cos", Cos.class);
      this.functions.put("sin", Sin.class);
      this.functions.put("exp", Exp.class);
      this.functions.put("ln", Ln.class);
      this.functions.put("sqrt", Sqrt.class);
      this.functions.put("mod", Mod.class);
      this.functions.put("pow", Pow.class);
      this.functions.put("lerp", Lerp.class);
      this.functions.put("lerprotate", LerpRotate.class);
      this.functions.put("random", Random.class);
   }

   public void register(Variable variable) {
      this.variables.put(variable.getName(), variable);
   }

   public IValue parse(String expression) throws Exception {
      return this.parseSymbols(this.breakdownChars(this.breakdown(expression)));
   }

   public String[] breakdown(String expression) throws Exception {
      if (!expression.matches("^[\\w\\d\\s_+-/*%^&|<>=!?:.,()]+$")) {
         throw new Exception("Given expression '" + expression + "' contains illegal characters!");
      } else {
         expression = expression.replaceAll("\\s+", "");
         String[] chars = expression.split("(?!^)");
         int left = 0;
         int right = 0;
         String[] var5 = chars;
         int var6 = chars.length;

         for(int var7 = 0; var7 < var6; ++var7) {
            String s = var5[var7];
            if (s.equals("(")) {
               ++left;
            } else if (s.equals(")")) {
               ++right;
            }
         }

         if (left != right) {
            throw new Exception("Given expression '" + expression + "' has more uneven amount of parenthesis, there are " + left + " open and " + right + " closed!");
         } else {
            return chars;
         }
      }
   }

   public List<Object> breakdownChars(String[] chars) {
      List<Object> symbols = new ArrayList();
      String buffer = "";
      int len = chars.length;

      for(int i = 0; i < len; ++i) {
         String s = chars[i];
         boolean longOperator = i > 0 && this.isOperator(chars[i - 1] + s);
         int counter;
         if (!this.isOperator(s) && !longOperator && !s.equals(",")) {
            if (s.equals("(")) {
               if (!buffer.isEmpty()) {
                  symbols.add(buffer);
                  buffer = "";
               }

               counter = 1;

               for(int j = i + 1; j < len; ++j) {
                  String c = chars[j];
                  if (c.equals("(")) {
                     ++counter;
                  } else if (c.equals(")")) {
                     --counter;
                  }

                  if (counter == 0) {
                     symbols.add(this.breakdownChars(buffer.split("(?!^)")));
                     i = j;
                     buffer = "";
                     break;
                  }

                  buffer = buffer + c;
               }
            } else {
               buffer = buffer + s;
            }
         } else {
            if (s.equals("-")) {
               counter = symbols.size();
               boolean isFirst = counter == 0 && buffer.isEmpty();
               boolean isOperatorBehind = counter > 0 && (this.isOperator(symbols.get(counter - 1)) || symbols.get(counter - 1).equals(",")) && buffer.isEmpty();
               if (isFirst || isOperatorBehind) {
                  buffer = buffer + s;
                  continue;
               }
            }

            if (longOperator) {
               s = chars[i - 1] + s;
               buffer = buffer.substring(0, buffer.length() - 1);
            }

            if (!buffer.isEmpty()) {
               symbols.add(buffer);
               buffer = "";
            }

            symbols.add(s);
         }
      }

      if (!buffer.isEmpty()) {
         symbols.add(buffer);
      }

      return symbols;
   }

   public IValue parseSymbols(List<Object> symbols) throws Exception {
      IValue ternary = this.tryTernary(symbols);
      if (ternary != null) {
         return ternary;
      } else {
         int size = symbols.size();
         if (size == 1) {
            return this.valueFromObject(symbols.get(0));
         } else {
            if (size == 2) {
               Object first = symbols.get(0);
               Object second = symbols.get(1);
               if ((this.isVariable(first) || first.equals("-")) && second instanceof List) {
                  return this.createFunction((String)first, (List)second);
               }
            }

            int lastOp = this.seekLastOperator(symbols);

            int leftOp;
            for(int op = lastOp; op != -1; op = leftOp) {
               leftOp = this.seekLastOperator(symbols, op - 1);
               if (leftOp != -1) {
                  Operation left = this.operationForOperator((String)symbols.get(leftOp));
                  Operation right = this.operationForOperator((String)symbols.get(op));
                  IValue leftValue;
                  if (right.value > left.value) {
                     IValue leftValue = this.parseSymbols(symbols.subList(0, leftOp));
                     leftValue = this.parseSymbols(symbols.subList(leftOp + 1, size));
                     return new Operator(left, leftValue, leftValue);
                  }

                  if (left.value > right.value) {
                     Operation initial = this.operationForOperator((String)symbols.get(lastOp));
                     IValue rightValue;
                     if (initial.value < left.value) {
                        leftValue = this.parseSymbols(symbols.subList(0, lastOp));
                        rightValue = this.parseSymbols(symbols.subList(lastOp + 1, size));
                        return new Operator(initial, leftValue, rightValue);
                     }

                     leftValue = this.parseSymbols(symbols.subList(0, op));
                     rightValue = this.parseSymbols(symbols.subList(op + 1, size));
                     return new Operator(right, leftValue, rightValue);
                  }
               }
            }

            Operation operation = this.operationForOperator((String)symbols.get(lastOp));
            return new Operator(operation, this.parseSymbols(symbols.subList(0, lastOp)), this.parseSymbols(symbols.subList(lastOp + 1, size)));
         }
      }
   }

   protected int seekLastOperator(List<Object> symbols) {
      return this.seekLastOperator(symbols, symbols.size() - 1);
   }

   protected int seekLastOperator(List<Object> symbols, int offset) {
      for(int i = offset; i >= 0; --i) {
         Object o = symbols.get(i);
         if (this.isOperator(o)) {
            return i;
         }
      }

      return -1;
   }

   protected int seekFirstOperator(List<Object> symbols) {
      return this.seekFirstOperator(symbols, 0);
   }

   protected int seekFirstOperator(List<Object> symbols, int offset) {
      int i = offset;

      for(int size = symbols.size(); i < size; ++i) {
         Object o = symbols.get(i);
         if (this.isOperator(o)) {
            return i;
         }
      }

      return -1;
   }

   protected IValue tryTernary(List<Object> symbols) throws Exception {
      int question = -1;
      int questions = 0;
      int colon = -1;
      int colons = 0;
      int size = symbols.size();

      for(int i = 0; i < size; ++i) {
         Object object = symbols.get(i);
         if (object instanceof String) {
            if (object.equals("?")) {
               if (question == -1) {
                  question = i;
               }

               ++questions;
            } else if (object.equals(":")) {
               if (colons + 1 == questions && colon == -1) {
                  colon = i;
               }

               ++colons;
            }
         }
      }

      if (questions == colons && question > 0 && question + 1 < colon && colon < size - 1) {
         return new Ternary(this.parseSymbols(symbols.subList(0, question)), this.parseSymbols(symbols.subList(question + 1, colon)), this.parseSymbols(symbols.subList(colon + 1, size)));
      } else {
         return null;
      }
   }

   protected IValue createFunction(String first, List<Object> args) throws Exception {
      if (first.equals("!")) {
         return new Negate(this.parseSymbols(args));
      } else if (first.startsWith("!") && first.length() > 1) {
         return new Negate(this.createFunction(first.substring(1), args));
      } else if (first.equals("-")) {
         return new Negative(this.parseSymbols(args));
      } else if (first.startsWith("-") && first.length() > 1) {
         return new Negative(this.createFunction(first.substring(1), args));
      } else if (!this.functions.containsKey(first)) {
         throw new Exception("Function '" + first + "' couldn't be found!");
      } else {
         List<IValue> values = new ArrayList();
         List<Object> buffer = new ArrayList();
         Iterator var5 = args.iterator();

         while(var5.hasNext()) {
            Object o = var5.next();
            if (o.equals(",")) {
               values.add(this.parseSymbols(buffer));
               buffer.clear();
            } else {
               buffer.add(o);
            }
         }

         if (!buffer.isEmpty()) {
            values.add(this.parseSymbols(buffer));
         }

         Class<? extends Function> function = (Class)this.functions.get(first);
         Constructor<? extends Function> ctor = function.getConstructor(IValue[].class, String.class);
         Function func = (Function)ctor.newInstance(values.toArray(new IValue[values.size()]), first);
         return func;
      }
   }

   public IValue valueFromObject(Object object) throws Exception {
      if (object instanceof String) {
         String symbol = (String)object;
         if (symbol.startsWith("!")) {
            return new Negate(this.valueFromObject(symbol.substring(1)));
         }

         if (this.isDecimal(symbol)) {
            return new Constant(Double.parseDouble(symbol));
         }

         if (this.isVariable(symbol)) {
            Variable value;
            if (symbol.startsWith("-")) {
               symbol = symbol.substring(1);
               value = this.getVariable(symbol);
               if (value != null) {
                  return new Negative(value);
               }
            } else {
               value = this.getVariable(symbol);
               if (value != null) {
                  return value;
               }
            }
         }
      } else if (object instanceof List) {
         return new Group(this.parseSymbols((List)object));
      }

      throw new Exception("Given object couldn't be converted to value! " + object);
   }

   protected Variable getVariable(String name) {
      return (Variable)this.variables.get(name);
   }

   protected Operation operationForOperator(String op) throws Exception {
      Operation[] var2 = Operation.values();
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         Operation operation = var2[var4];
         if (operation.sign.equals(op)) {
            return operation;
         }
      }

      throw new Exception("There is no such operator '" + op + "'!");
   }

   protected boolean isVariable(Object o) {
      return o instanceof String && !this.isDecimal((String)o) && !this.isOperator((String)o);
   }

   protected boolean isOperator(Object o) {
      return o instanceof String && this.isOperator((String)o);
   }

   protected boolean isOperator(String s) {
      return Operation.OPERATORS.contains(s) || s.equals("?") || s.equals(":");
   }

   protected boolean isDecimal(String s) {
      return s.matches("^-?\\d+(\\.\\d+)?$");
   }
}
