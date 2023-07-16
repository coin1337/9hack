package org.apache.commons.lang3.concurrent;

public interface Computable<I, O> {
   O compute(I var1) throws InterruptedException;
}
