package org.apache.commons.lang3.concurrent.locks;

import java.util.Objects;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.StampedLock;
import java.util.function.Supplier;
import org.apache.commons.lang3.function.Failable;
import org.apache.commons.lang3.function.FailableConsumer;
import org.apache.commons.lang3.function.FailableFunction;

public class LockingVisitors {
   public static <O> LockingVisitors.ReadWriteLockVisitor<O> reentrantReadWriteLockVisitor(O object) {
      return new LockingVisitors.ReadWriteLockVisitor(object, new ReentrantReadWriteLock());
   }

   public static <O> LockingVisitors.StampedLockVisitor<O> stampedLockVisitor(O object) {
      return new LockingVisitors.StampedLockVisitor(object, new StampedLock());
   }

   public static class StampedLockVisitor<O> extends LockingVisitors.LockVisitor<O, StampedLock> {
      protected StampedLockVisitor(O object, StampedLock stampedLock) {
         super(object, stampedLock, stampedLock::asReadLock, stampedLock::asWriteLock);
      }
   }

   public static class ReadWriteLockVisitor<O> extends LockingVisitors.LockVisitor<O, ReadWriteLock> {
      protected ReadWriteLockVisitor(O object, ReadWriteLock readWriteLock) {
         super(object, readWriteLock, readWriteLock::readLock, readWriteLock::writeLock);
      }
   }

   public static class LockVisitor<O, L> {
      private final L lock;
      private final O object;
      private final Supplier<Lock> readLockSupplier;
      private final Supplier<Lock> writeLockSupplier;

      protected LockVisitor(O object, L lock, Supplier<Lock> readLockSupplier, Supplier<Lock> writeLockSupplier) {
         this.object = Objects.requireNonNull(object, "object");
         this.lock = Objects.requireNonNull(lock, "lock");
         this.readLockSupplier = (Supplier)Objects.requireNonNull(readLockSupplier, "readLockSupplier");
         this.writeLockSupplier = (Supplier)Objects.requireNonNull(writeLockSupplier, "writeLockSupplier");
      }

      public void acceptReadLocked(FailableConsumer<O, ?> consumer) {
         this.lockAcceptUnlock(this.readLockSupplier, consumer);
      }

      public void acceptWriteLocked(FailableConsumer<O, ?> consumer) {
         this.lockAcceptUnlock(this.writeLockSupplier, consumer);
      }

      public <T> T applyReadLocked(FailableFunction<O, T, ?> function) {
         return this.lockApplyUnlock(this.readLockSupplier, function);
      }

      public <T> T applyWriteLocked(FailableFunction<O, T, ?> function) {
         return this.lockApplyUnlock(this.writeLockSupplier, function);
      }

      public L getLock() {
         return this.lock;
      }

      public O getObject() {
         return this.object;
      }

      protected void lockAcceptUnlock(Supplier<Lock> lockSupplier, FailableConsumer<O, ?> consumer) {
         Lock lock = (Lock)lockSupplier.get();
         lock.lock();

         try {
            consumer.accept(this.object);
         } catch (Throwable var8) {
            throw Failable.rethrow(var8);
         } finally {
            lock.unlock();
         }

      }

      protected <T> T lockApplyUnlock(Supplier<Lock> lockSupplier, FailableFunction<O, T, ?> function) {
         Lock lock = (Lock)lockSupplier.get();
         lock.lock();

         Object var4;
         try {
            var4 = function.apply(this.object);
         } catch (Throwable var8) {
            throw Failable.rethrow(var8);
         } finally {
            lock.unlock();
         }

         return var4;
      }
   }
}
