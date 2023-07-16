package software.bernie.shadowed.fasterxml.jackson.databind.introspect;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import software.bernie.shadowed.fasterxml.jackson.databind.util.Annotations;

public abstract class AnnotationCollector {
   protected static final Annotations NO_ANNOTATIONS = new AnnotationCollector.NoAnnotations();
   protected final Object _data;

   protected AnnotationCollector(Object d) {
      this._data = d;
   }

   public static Annotations emptyAnnotations() {
      return NO_ANNOTATIONS;
   }

   public static AnnotationCollector emptyCollector() {
      return AnnotationCollector.EmptyCollector.instance;
   }

   public static AnnotationCollector emptyCollector(Object data) {
      return new AnnotationCollector.EmptyCollector(data);
   }

   public abstract Annotations asAnnotations();

   public abstract AnnotationMap asAnnotationMap();

   public Object getData() {
      return this._data;
   }

   public abstract boolean isPresent(Annotation var1);

   public abstract AnnotationCollector addOrOverride(Annotation var1);

   public static class TwoAnnotations implements Annotations, Serializable {
      private static final long serialVersionUID = 1L;
      private final Class<?> _type1;
      private final Class<?> _type2;
      private final Annotation _value1;
      private final Annotation _value2;

      public TwoAnnotations(Class<?> type1, Annotation value1, Class<?> type2, Annotation value2) {
         this._type1 = type1;
         this._value1 = value1;
         this._type2 = type2;
         this._value2 = value2;
      }

      public <A extends Annotation> A get(Class<A> cls) {
         if (this._type1 == cls) {
            return this._value1;
         } else {
            return this._type2 == cls ? this._value2 : null;
         }
      }

      public boolean has(Class<?> cls) {
         return this._type1 == cls || this._type2 == cls;
      }

      public boolean hasOneOf(Class<? extends Annotation>[] annoClasses) {
         Class[] arr$ = annoClasses;
         int len$ = annoClasses.length;

         for(int i$ = 0; i$ < len$; ++i$) {
            Class<?> cls = arr$[i$];
            if (cls == this._type1 || cls == this._type2) {
               return true;
            }
         }

         return false;
      }

      public int size() {
         return 2;
      }
   }

   public static class OneAnnotation implements Annotations, Serializable {
      private static final long serialVersionUID = 1L;
      private final Class<?> _type;
      private final Annotation _value;

      public OneAnnotation(Class<?> type, Annotation value) {
         this._type = type;
         this._value = value;
      }

      public <A extends Annotation> A get(Class<A> cls) {
         return this._type == cls ? this._value : null;
      }

      public boolean has(Class<?> cls) {
         return this._type == cls;
      }

      public boolean hasOneOf(Class<? extends Annotation>[] annoClasses) {
         Class[] arr$ = annoClasses;
         int len$ = annoClasses.length;

         for(int i$ = 0; i$ < len$; ++i$) {
            Class<?> cls = arr$[i$];
            if (cls == this._type) {
               return true;
            }
         }

         return false;
      }

      public int size() {
         return 1;
      }
   }

   public static class NoAnnotations implements Annotations, Serializable {
      private static final long serialVersionUID = 1L;

      NoAnnotations() {
      }

      public <A extends Annotation> A get(Class<A> cls) {
         return null;
      }

      public boolean has(Class<?> cls) {
         return false;
      }

      public boolean hasOneOf(Class<? extends Annotation>[] annoClasses) {
         return false;
      }

      public int size() {
         return 0;
      }
   }

   static class NCollector extends AnnotationCollector {
      protected final HashMap<Class<?>, Annotation> _annotations = new HashMap();

      public NCollector(Object data, Class<?> type1, Annotation value1, Class<?> type2, Annotation value2) {
         super(data);
         this._annotations.put(type1, value1);
         this._annotations.put(type2, value2);
      }

      public Annotations asAnnotations() {
         if (this._annotations.size() == 2) {
            Iterator<Entry<Class<?>, Annotation>> it = this._annotations.entrySet().iterator();
            Entry<Class<?>, Annotation> en1 = (Entry)it.next();
            Entry<Class<?>, Annotation> en2 = (Entry)it.next();
            return new AnnotationCollector.TwoAnnotations((Class)en1.getKey(), (Annotation)en1.getValue(), (Class)en2.getKey(), (Annotation)en2.getValue());
         } else {
            return new AnnotationMap(this._annotations);
         }
      }

      public AnnotationMap asAnnotationMap() {
         AnnotationMap result = new AnnotationMap();
         Iterator i$ = this._annotations.values().iterator();

         while(i$.hasNext()) {
            Annotation ann = (Annotation)i$.next();
            result.add(ann);
         }

         return result;
      }

      public boolean isPresent(Annotation ann) {
         return this._annotations.containsKey(ann.annotationType());
      }

      public AnnotationCollector addOrOverride(Annotation ann) {
         this._annotations.put(ann.annotationType(), ann);
         return this;
      }
   }

   static class OneCollector extends AnnotationCollector {
      private Class<?> _type;
      private Annotation _value;

      public OneCollector(Object data, Class<?> type, Annotation value) {
         super(data);
         this._type = type;
         this._value = value;
      }

      public Annotations asAnnotations() {
         return new AnnotationCollector.OneAnnotation(this._type, this._value);
      }

      public AnnotationMap asAnnotationMap() {
         return AnnotationMap.of(this._type, this._value);
      }

      public boolean isPresent(Annotation ann) {
         return ann.annotationType() == this._type;
      }

      public AnnotationCollector addOrOverride(Annotation ann) {
         Class<?> type = ann.annotationType();
         if (this._type == type) {
            this._value = ann;
            return this;
         } else {
            return new AnnotationCollector.NCollector(this._data, this._type, this._value, type, ann);
         }
      }
   }

   static class EmptyCollector extends AnnotationCollector {
      public static final AnnotationCollector.EmptyCollector instance = new AnnotationCollector.EmptyCollector((Object)null);

      EmptyCollector(Object data) {
         super(data);
      }

      public Annotations asAnnotations() {
         return NO_ANNOTATIONS;
      }

      public AnnotationMap asAnnotationMap() {
         return new AnnotationMap();
      }

      public boolean isPresent(Annotation ann) {
         return false;
      }

      public AnnotationCollector addOrOverride(Annotation ann) {
         return new AnnotationCollector.OneCollector(this._data, ann.annotationType(), ann);
      }
   }
}
