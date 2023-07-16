package software.bernie.shadowed.fasterxml.jackson.databind.util;

import java.lang.annotation.Annotation;

public interface Annotations {
   <A extends Annotation> A get(Class<A> var1);

   boolean has(Class<?> var1);

   boolean hasOneOf(Class<? extends Annotation>[] var1);

   int size();
}
