package software.bernie.shadowed.fasterxml.jackson.databind.introspect;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import software.bernie.shadowed.fasterxml.jackson.databind.JavaType;
import software.bernie.shadowed.fasterxml.jackson.databind.util.ClassUtil;

public final class AnnotatedMethod extends AnnotatedWithParams implements Serializable {
   private static final long serialVersionUID = 1L;
   protected final transient Method _method;
   protected Class<?>[] _paramClasses;
   protected AnnotatedMethod.Serialization _serialization;

   public AnnotatedMethod(TypeResolutionContext ctxt, Method method, AnnotationMap classAnn, AnnotationMap[] paramAnnotations) {
      super(ctxt, classAnn, paramAnnotations);
      if (method == null) {
         throw new IllegalArgumentException("Cannot construct AnnotatedMethod with null Method");
      } else {
         this._method = method;
      }
   }

   protected AnnotatedMethod(AnnotatedMethod.Serialization ser) {
      super((TypeResolutionContext)null, (AnnotationMap)null, (AnnotationMap[])null);
      this._method = null;
      this._serialization = ser;
   }

   public AnnotatedMethod withAnnotations(AnnotationMap ann) {
      return new AnnotatedMethod(this._typeContext, this._method, ann, this._paramAnnotations);
   }

   public Method getAnnotated() {
      return this._method;
   }

   public int getModifiers() {
      return this._method.getModifiers();
   }

   public String getName() {
      return this._method.getName();
   }

   public JavaType getType() {
      return this._typeContext.resolveType(this._method.getGenericReturnType());
   }

   public Class<?> getRawType() {
      return this._method.getReturnType();
   }

   /** @deprecated */
   @Deprecated
   public Type getGenericType() {
      return this._method.getGenericReturnType();
   }

   public final Object call() throws Exception {
      return this._method.invoke((Object)null);
   }

   public final Object call(Object[] args) throws Exception {
      return this._method.invoke((Object)null, args);
   }

   public final Object call1(Object arg) throws Exception {
      return this._method.invoke((Object)null, arg);
   }

   public final Object callOn(Object pojo) throws Exception {
      return this._method.invoke(pojo, (Object[])null);
   }

   public final Object callOnWith(Object pojo, Object... args) throws Exception {
      return this._method.invoke(pojo, args);
   }

   public int getParameterCount() {
      return this.getRawParameterTypes().length;
   }

   public Class<?> getRawParameterType(int index) {
      Class<?>[] types = this.getRawParameterTypes();
      return index >= types.length ? null : types[index];
   }

   public JavaType getParameterType(int index) {
      Type[] types = this._method.getGenericParameterTypes();
      return index >= types.length ? null : this._typeContext.resolveType(types[index]);
   }

   /** @deprecated */
   @Deprecated
   public Type getGenericParameterType(int index) {
      Type[] types = this.getGenericParameterTypes();
      return index >= types.length ? null : types[index];
   }

   public Class<?> getDeclaringClass() {
      return this._method.getDeclaringClass();
   }

   public Method getMember() {
      return this._method;
   }

   public void setValue(Object pojo, Object value) throws IllegalArgumentException {
      try {
         this._method.invoke(pojo, value);
      } catch (InvocationTargetException | IllegalAccessException var4) {
         throw new IllegalArgumentException("Failed to setValue() with method " + this.getFullName() + ": " + var4.getMessage(), var4);
      }
   }

   public Object getValue(Object pojo) throws IllegalArgumentException {
      try {
         return this._method.invoke(pojo, (Object[])null);
      } catch (InvocationTargetException | IllegalAccessException var3) {
         throw new IllegalArgumentException("Failed to getValue() with method " + this.getFullName() + ": " + var3.getMessage(), var3);
      }
   }

   public String getFullName() {
      return String.format("%s(%d params)", super.getFullName(), this.getParameterCount());
   }

   public Class<?>[] getRawParameterTypes() {
      if (this._paramClasses == null) {
         this._paramClasses = this._method.getParameterTypes();
      }

      return this._paramClasses;
   }

   /** @deprecated */
   @Deprecated
   public Type[] getGenericParameterTypes() {
      return this._method.getGenericParameterTypes();
   }

   public Class<?> getRawReturnType() {
      return this._method.getReturnType();
   }

   public boolean hasReturnType() {
      Class<?> rt = this.getRawReturnType();
      return rt != Void.TYPE && rt != Void.class;
   }

   public String toString() {
      return "[method " + this.getFullName() + "]";
   }

   public int hashCode() {
      return this._method.getName().hashCode();
   }

   public boolean equals(Object o) {
      if (o == this) {
         return true;
      } else {
         return ClassUtil.hasClass(o, this.getClass()) && ((AnnotatedMethod)o)._method == this._method;
      }
   }

   Object writeReplace() {
      return new AnnotatedMethod(new AnnotatedMethod.Serialization(this._method));
   }

   Object readResolve() {
      Class clazz = this._serialization.clazz;

      try {
         Method m = clazz.getDeclaredMethod(this._serialization.name, this._serialization.args);
         if (!m.isAccessible()) {
            ClassUtil.checkAndFixAccess(m, false);
         }

         return new AnnotatedMethod((TypeResolutionContext)null, m, (AnnotationMap)null, (AnnotationMap[])null);
      } catch (Exception var3) {
         throw new IllegalArgumentException("Could not find method '" + this._serialization.name + "' from Class '" + clazz.getName());
      }
   }

   private static final class Serialization implements Serializable {
      private static final long serialVersionUID = 1L;
      protected Class<?> clazz;
      protected String name;
      protected Class<?>[] args;

      public Serialization(Method setter) {
         this.clazz = setter.getDeclaringClass();
         this.name = setter.getName();
         this.args = setter.getParameterTypes();
      }
   }
}
