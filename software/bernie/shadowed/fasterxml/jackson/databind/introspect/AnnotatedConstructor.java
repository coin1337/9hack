package software.bernie.shadowed.fasterxml.jackson.databind.introspect;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.Member;
import java.lang.reflect.Type;
import software.bernie.shadowed.fasterxml.jackson.databind.JavaType;
import software.bernie.shadowed.fasterxml.jackson.databind.util.ClassUtil;

public final class AnnotatedConstructor extends AnnotatedWithParams {
   private static final long serialVersionUID = 1L;
   protected final Constructor<?> _constructor;
   protected AnnotatedConstructor.Serialization _serialization;

   public AnnotatedConstructor(TypeResolutionContext ctxt, Constructor<?> constructor, AnnotationMap classAnn, AnnotationMap[] paramAnn) {
      super(ctxt, classAnn, paramAnn);
      if (constructor == null) {
         throw new IllegalArgumentException("Null constructor not allowed");
      } else {
         this._constructor = constructor;
      }
   }

   protected AnnotatedConstructor(AnnotatedConstructor.Serialization ser) {
      super((TypeResolutionContext)null, (AnnotationMap)null, (AnnotationMap[])null);
      this._constructor = null;
      this._serialization = ser;
   }

   public AnnotatedConstructor withAnnotations(AnnotationMap ann) {
      return new AnnotatedConstructor(this._typeContext, this._constructor, ann, this._paramAnnotations);
   }

   public Constructor<?> getAnnotated() {
      return this._constructor;
   }

   public int getModifiers() {
      return this._constructor.getModifiers();
   }

   public String getName() {
      return this._constructor.getName();
   }

   public JavaType getType() {
      return this._typeContext.resolveType(this.getRawType());
   }

   public Class<?> getRawType() {
      return this._constructor.getDeclaringClass();
   }

   public int getParameterCount() {
      return this._constructor.getParameterTypes().length;
   }

   public Class<?> getRawParameterType(int index) {
      Class<?>[] types = this._constructor.getParameterTypes();
      return index >= types.length ? null : types[index];
   }

   public JavaType getParameterType(int index) {
      Type[] types = this._constructor.getGenericParameterTypes();
      return index >= types.length ? null : this._typeContext.resolveType(types[index]);
   }

   /** @deprecated */
   @Deprecated
   public Type getGenericParameterType(int index) {
      Type[] types = this._constructor.getGenericParameterTypes();
      return index >= types.length ? null : types[index];
   }

   public final Object call() throws Exception {
      return this._constructor.newInstance();
   }

   public final Object call(Object[] args) throws Exception {
      return this._constructor.newInstance(args);
   }

   public final Object call1(Object arg) throws Exception {
      return this._constructor.newInstance(arg);
   }

   public Class<?> getDeclaringClass() {
      return this._constructor.getDeclaringClass();
   }

   public Member getMember() {
      return this._constructor;
   }

   public void setValue(Object pojo, Object value) throws UnsupportedOperationException {
      throw new UnsupportedOperationException("Cannot call setValue() on constructor of " + this.getDeclaringClass().getName());
   }

   public Object getValue(Object pojo) throws UnsupportedOperationException {
      throw new UnsupportedOperationException("Cannot call getValue() on constructor of " + this.getDeclaringClass().getName());
   }

   public String toString() {
      return "[constructor for " + this.getName() + ", annotations: " + this._annotations + "]";
   }

   public int hashCode() {
      return this._constructor.getName().hashCode();
   }

   public boolean equals(Object o) {
      if (o == this) {
         return true;
      } else {
         return ClassUtil.hasClass(o, this.getClass()) && ((AnnotatedConstructor)o)._constructor == this._constructor;
      }
   }

   Object writeReplace() {
      return new AnnotatedConstructor(new AnnotatedConstructor.Serialization(this._constructor));
   }

   Object readResolve() {
      Class clazz = this._serialization.clazz;

      try {
         Constructor<?> ctor = clazz.getDeclaredConstructor(this._serialization.args);
         if (!ctor.isAccessible()) {
            ClassUtil.checkAndFixAccess(ctor, false);
         }

         return new AnnotatedConstructor((TypeResolutionContext)null, ctor, (AnnotationMap)null, (AnnotationMap[])null);
      } catch (Exception var3) {
         throw new IllegalArgumentException("Could not find constructor with " + this._serialization.args.length + " args from Class '" + clazz.getName());
      }
   }

   private static final class Serialization implements Serializable {
      private static final long serialVersionUID = 1L;
      protected Class<?> clazz;
      protected Class<?>[] args;

      public Serialization(Constructor<?> ctor) {
         this.clazz = ctor.getDeclaringClass();
         this.args = ctor.getParameterTypes();
      }
   }
}
