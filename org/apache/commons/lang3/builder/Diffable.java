package org.apache.commons.lang3.builder;

@FunctionalInterface
public interface Diffable<T> {
   DiffResult<T> diff(T var1);
}
