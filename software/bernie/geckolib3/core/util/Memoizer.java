package software.bernie.geckolib3.core.util;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public class Memoizer<T, U> {
   private final Map<T, U> cache = new ConcurrentHashMap();

   private Memoizer() {
   }

   private Function<T, U> doMemoize(Function<T, U> function) {
      return (input) -> {
         Map var10000 = this.cache;
         function.getClass();
         return var10000.computeIfAbsent(input, function::apply);
      };
   }

   public static <T, U> Function<T, U> memoize(Function<T, U> function) {
      return (new Memoizer()).doMemoize(function);
   }
}
