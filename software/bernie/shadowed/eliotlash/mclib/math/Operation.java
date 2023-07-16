package software.bernie.shadowed.eliotlash.mclib.math;

import java.util.HashSet;
import java.util.Set;

public enum Operation {
   ADD("+", 1) {
      public double calculate(double a, double b) {
         return a + b;
      }
   },
   SUB("-", 1) {
      public double calculate(double a, double b) {
         return a - b;
      }
   },
   MUL("*", 2) {
      public double calculate(double a, double b) {
         return a * b;
      }
   },
   DIV("/", 2) {
      public double calculate(double a, double b) {
         return a / (b == 0.0D ? 1.0D : b);
      }
   },
   MOD("%", 2) {
      public double calculate(double a, double b) {
         return a % b;
      }
   },
   POW("^", 3) {
      public double calculate(double a, double b) {
         return Math.pow(a, b);
      }
   },
   AND("&&", 5) {
      public double calculate(double a, double b) {
         return a != 0.0D && b != 0.0D ? 1.0D : 0.0D;
      }
   },
   OR("||", 5) {
      public double calculate(double a, double b) {
         return a == 0.0D && b == 0.0D ? 0.0D : 1.0D;
      }
   },
   LESS("<", 5) {
      public double calculate(double a, double b) {
         return a < b ? 1.0D : 0.0D;
      }
   },
   LESS_THAN("<=", 5) {
      public double calculate(double a, double b) {
         return a <= b ? 1.0D : 0.0D;
      }
   },
   GREATER_THAN(">=", 5) {
      public double calculate(double a, double b) {
         return a >= b ? 1.0D : 0.0D;
      }
   },
   GREATER(">", 5) {
      public double calculate(double a, double b) {
         return a > b ? 1.0D : 0.0D;
      }
   },
   EQUALS("==", 5) {
      public double calculate(double a, double b) {
         return equals(a, b) ? 1.0D : 0.0D;
      }
   },
   NOT_EQUALS("!=", 5) {
      public double calculate(double a, double b) {
         return !equals(a, b) ? 1.0D : 0.0D;
      }
   };

   public static final Set<String> OPERATORS = new HashSet();
   public final String sign;
   public final int value;

   public static boolean equals(double a, double b) {
      return Math.abs(a - b) < 1.0E-5D;
   }

   private Operation(String sign, int value) {
      this.sign = sign;
      this.value = value;
   }

   public abstract double calculate(double var1, double var3);

   // $FF: synthetic method
   Operation(String x2, int x3, Object x4) {
      this(x2, x3);
   }

   static {
      Operation[] var0 = values();
      int var1 = var0.length;

      for(int var2 = 0; var2 < var1; ++var2) {
         Operation op = var0[var2];
         OPERATORS.add(op.sign);
      }

   }
}
