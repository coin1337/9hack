package org.apache.commons.lang3.concurrent;

import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

public class Memoizer<I, O> implements Computable<I, O> {
   private final ConcurrentMap<I, Future<O>> cache;
   private final Computable<I, O> computable;
   private final boolean recalculate;

   public Memoizer(Computable<I, O> computable) {
      this(computable, false);
   }

   public Memoizer(Computable<I, O> computable, boolean recalculate) {
      this.cache = new ConcurrentHashMap();
      this.computable = computable;
      this.recalculate = recalculate;
   }

   public O compute(I arg) throws InterruptedException {
      while(true) {
         Future<O> future = (Future)this.cache.get(arg);
         if (future == null) {
            Callable<O> eval = () -> {
               return this.computable.compute(arg);
            };
            FutureTask<O> futureTask = new FutureTask(eval);
            future = (Future)this.cache.putIfAbsent(arg, futureTask);
            if (future == null) {
               future = futureTask;
               futureTask.run();
            }
         }

         try {
            return ((Future)future).get();
         } catch (CancellationException var5) {
            this.cache.remove(arg, future);
         } catch (ExecutionException var6) {
            if (this.recalculate) {
               this.cache.remove(arg, future);
            }

            throw this.launderException(var6.getCause());
         }
      }
   }

   private RuntimeException launderException(Throwable throwable) {
      if (throwable instanceof RuntimeException) {
         return (RuntimeException)throwable;
      } else if (throwable instanceof Error) {
         throw (Error)throwable;
      } else {
         throw new IllegalStateException("Unchecked exception", throwable);
      }
   }
}
