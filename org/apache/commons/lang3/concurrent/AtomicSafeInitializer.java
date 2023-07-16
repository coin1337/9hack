package org.apache.commons.lang3.concurrent;

import java.util.concurrent.atomic.AtomicReference;

public abstract class AtomicSafeInitializer<T> implements ConcurrentInitializer<T> {
   private final AtomicReference<AtomicSafeInitializer<T>> factory = new AtomicReference();
   private final AtomicReference<T> reference = new AtomicReference();

   public final T get() throws ConcurrentException {
      Object result;
      while((result = this.reference.get()) == null) {
         if (this.factory.compareAndSet((Object)null, this)) {
            this.reference.set(this.initialize());
         }
      }

      return result;
   }

   protected abstract T initialize() throws ConcurrentException;
}
