package software.bernie.shadowed.fasterxml.jackson.databind.introspect;

import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import software.bernie.shadowed.fasterxml.jackson.databind.util.Annotations;

public final class AnnotationMap implements Annotations {
   protected HashMap<Class<?>, Annotation> _annotations;

   public AnnotationMap() {
   }

   public static AnnotationMap of(Class<?> type, Annotation value) {
      HashMap<Class<?>, Annotation> ann = new HashMap(4);
      ann.put(type, value);
      return new AnnotationMap(ann);
   }

   AnnotationMap(HashMap<Class<?>, Annotation> a) {
      this._annotations = a;
   }

   public <A extends Annotation> A get(Class<A> cls) {
      return this._annotations == null ? null : (Annotation)this._annotations.get(cls);
   }

   public boolean has(Class<?> cls) {
      return this._annotations == null ? false : this._annotations.containsKey(cls);
   }

   public boolean hasOneOf(Class<? extends Annotation>[] annoClasses) {
      if (this._annotations != null) {
         int i = 0;

         for(int end = annoClasses.length; i < end; ++i) {
            if (this._annotations.containsKey(annoClasses[i])) {
               return true;
            }
         }
      }

      return false;
   }

   public Iterable<Annotation> annotations() {
      return (Iterable)(this._annotations != null && this._annotations.size() != 0 ? this._annotations.values() : Collections.emptyList());
   }

   public static AnnotationMap merge(AnnotationMap primary, AnnotationMap secondary) {
      if (primary != null && primary._annotations != null && !primary._annotations.isEmpty()) {
         if (secondary != null && secondary._annotations != null && !secondary._annotations.isEmpty()) {
            HashMap<Class<?>, Annotation> annotations = new HashMap();
            Iterator i$ = secondary._annotations.values().iterator();

            Annotation ann;
            while(i$.hasNext()) {
               ann = (Annotation)i$.next();
               annotations.put(ann.annotationType(), ann);
            }

            i$ = primary._annotations.values().iterator();

            while(i$.hasNext()) {
               ann = (Annotation)i$.next();
               annotations.put(ann.annotationType(), ann);
            }

            return new AnnotationMap(annotations);
         } else {
            return primary;
         }
      } else {
         return secondary;
      }
   }

   public int size() {
      return this._annotations == null ? 0 : this._annotations.size();
   }

   public boolean addIfNotPresent(Annotation ann) {
      if (this._annotations != null && this._annotations.containsKey(ann.annotationType())) {
         return false;
      } else {
         this._add(ann);
         return true;
      }
   }

   public boolean add(Annotation ann) {
      return this._add(ann);
   }

   public String toString() {
      return this._annotations == null ? "[null]" : this._annotations.toString();
   }

   protected final boolean _add(Annotation ann) {
      if (this._annotations == null) {
         this._annotations = new HashMap();
      }

      Annotation previous = (Annotation)this._annotations.put(ann.annotationType(), ann);
      return previous == null || !previous.equals(ann);
   }
}
