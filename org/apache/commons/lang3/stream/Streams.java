package org.apache.commons.lang3.stream;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Stream;
import java.util.stream.Collector.Characteristics;
import org.apache.commons.lang3.function.Failable;
import org.apache.commons.lang3.function.FailableConsumer;
import org.apache.commons.lang3.function.FailableFunction;
import org.apache.commons.lang3.function.FailablePredicate;

public class Streams {
   public static <O> Streams.FailableStream<O> stream(Collection<O> stream) {
      return stream(stream.stream());
   }

   public static <O> Streams.FailableStream<O> stream(Stream<O> stream) {
      return new Streams.FailableStream(stream);
   }

   public static <O> Collector<O, ?, O[]> toArray(Class<O> pElementType) {
      return new Streams.ArrayCollector(pElementType);
   }

   public static class FailableStream<O> {
      private Stream<O> stream;
      private boolean terminated;

      public FailableStream(Stream<O> stream) {
         this.stream = stream;
      }

      public boolean allMatch(FailablePredicate<O, ?> predicate) {
         this.assertNotTerminated();
         return this.stream().allMatch(Failable.asPredicate(predicate));
      }

      public boolean anyMatch(FailablePredicate<O, ?> predicate) {
         this.assertNotTerminated();
         return this.stream().anyMatch(Failable.asPredicate(predicate));
      }

      protected void assertNotTerminated() {
         if (this.terminated) {
            throw new IllegalStateException("This stream is already terminated.");
         }
      }

      public <A, R> R collect(Collector<? super O, A, R> collector) {
         this.makeTerminated();
         return this.stream().collect(collector);
      }

      public <A, R> R collect(Supplier<R> pupplier, BiConsumer<R, ? super O> accumulator, BiConsumer<R, R> combiner) {
         this.makeTerminated();
         return this.stream().collect(pupplier, accumulator, combiner);
      }

      public Streams.FailableStream<O> filter(FailablePredicate<O, ?> predicate) {
         this.assertNotTerminated();
         this.stream = this.stream.filter(Failable.asPredicate(predicate));
         return this;
      }

      public void forEach(FailableConsumer<O, ?> action) {
         this.makeTerminated();
         this.stream().forEach(Failable.asConsumer(action));
      }

      protected void makeTerminated() {
         this.assertNotTerminated();
         this.terminated = true;
      }

      public <R> Streams.FailableStream<R> map(FailableFunction<O, R, ?> mapper) {
         this.assertNotTerminated();
         return new Streams.FailableStream(this.stream.map(Failable.asFunction(mapper)));
      }

      public O reduce(O identity, BinaryOperator<O> accumulator) {
         this.makeTerminated();
         return this.stream().reduce(identity, accumulator);
      }

      public Stream<O> stream() {
         return this.stream;
      }
   }

   public static class ArrayCollector<O> implements Collector<O, List<O>, O[]> {
      private static final Set<Characteristics> characteristics = Collections.emptySet();
      private final Class<O> elementType;

      public ArrayCollector(Class<O> elementType) {
         this.elementType = elementType;
      }

      public BiConsumer<List<O>, O> accumulator() {
         return List::add;
      }

      public Set<Characteristics> characteristics() {
         return characteristics;
      }

      public BinaryOperator<List<O>> combiner() {
         return (left, right) -> {
            left.addAll(right);
            return left;
         };
      }

      public Function<List<O>, O[]> finisher() {
         return (list) -> {
            O[] array = (Object[])((Object[])Array.newInstance(this.elementType, list.size()));
            return list.toArray(array);
         };
      }

      public Supplier<List<O>> supplier() {
         return ArrayList::new;
      }
   }
}
