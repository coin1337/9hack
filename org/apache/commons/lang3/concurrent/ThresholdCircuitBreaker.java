package org.apache.commons.lang3.concurrent;

import java.util.concurrent.atomic.AtomicLong;

public class ThresholdCircuitBreaker extends AbstractCircuitBreaker<Long> {
   private static final long INITIAL_COUNT = 0L;
   private final long threshold;
   private final AtomicLong used = new AtomicLong(0L);

   public ThresholdCircuitBreaker(long threshold) {
      this.threshold = threshold;
   }

   public long getThreshold() {
      return this.threshold;
   }

   public boolean checkState() {
      return this.isOpen();
   }

   public void close() {
      super.close();
      this.used.set(0L);
   }

   public boolean incrementAndCheckState(Long increment) {
      if (this.threshold == 0L) {
         this.open();
      }

      long used = this.used.addAndGet(increment);
      if (used > this.threshold) {
         this.open();
      }

      return this.checkState();
   }
}
