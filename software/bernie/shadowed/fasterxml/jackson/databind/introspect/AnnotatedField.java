package software.bernie.shadowed.fasterxml.jackson.databind.introspect;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import software.bernie.shadowed.fasterxml.jackson.databind.JavaType;
import software.bernie.shadowed.fasterxml.jackson.databind.util.ClassUtil;

public final class AnnotatedField extends AnnotatedMember implements Serializable {
   private static final long serialVersionUID = 1L;
   protected final transient Field _field;
   protected AnnotatedField.Serialization _serialization;

   public AnnotatedField(TypeResolutionContext contextClass, Field field, AnnotationMap annMap) {
      super(contextClass, annMap);
      this._field = field;
   }

   public AnnotatedField withAnnotations(AnnotationMap ann) {
      return new AnnotatedField(this._typeContext, this._field, ann);
   }

   protected AnnotatedField(AnnotatedField.Serialization ser) {
      super((TypeResolutionContext)null, (AnnotationMap)null);
      this._field = null;
      this._serialization = ser;
   }

   public Field getAnnotated() {
      return this._field;
   }

   public int getModifiers() {
      return this._field.getModifiers();
   }

   public String getName() {
      return this._field.getName();
   }

   public Class<?> getRawType() {
      return this._field.getType();
   }

   /** @deprecated */
   @Deprecated
   public Type getGenericType() {
      return this._field.getGenericType();
   }

   public JavaType getType() {
      return this._typeContext.resolveType(this._field.getGenericType());
   }

   public Class<?> getDeclaringClass() {
      return this._field.getDeclaringClass();
   }

   public Member getMember() {
      return this._field;
   }

   public void setValue(Object pojo, Object value) throws IllegalArgumentException {
      try {
         this._field.set(pojo, value);
      } catch (IllegalAccessException var4) {
         throw new IllegalArgumentException("Failed to setValue() for field " + this.getFullName() + ": " + var4.getMessage(), var4);
      }
   }

   public Object getValue(Object pojo) throws IllegalArgumentException {
      try {
         return this._field.get(pojo);
      } catch (IllegalAccessException var3) {
         throw new IllegalArgumentException("Failed to getValue() for field " + this.getFullName() + ": " + var3.getMessage(), var3);
      }
   }

   public int getAnnotationCount() {
      return this._annotations.size();
   }

   public boolean isTransient() {
      return Modifier.isTransient(this.getModifiers());
   }

   public int hashCode() {
      return this._field.getName().hashCode();
   }

   public boolean equals(Object o) {
      if (o == this) {
         return true;
      } else {
         return ClassUtil.hasClass(o, this.getClass()) && ((AnnotatedField)o)._field == this._field;
      }
   }

   public String toString() {
      return "[field " + this.getFullName() + "]";
   }

   Object writeReplace() {
      return new AnnotatedField(new AnnotatedField.Serialization(this._field));
   }

   Object readResolve() {
      Class clazz = this._serialization.clazz;

      try {
         Field f = clazz.getDeclaredField(this._serialization.name);
         if (!f.isAccessible()) {
            ClassUtil.checkAndFixAccess(f, false);
         }

         return new AnnotatedField((TypeResolutionContext)null, f, (AnnotationMap)null);
      } catch (Exception var3) {
         throw new IllegalArgumentException("Could not find method '" + this._serialization.name + "' from Class '" + clazz.getName());
      }
   }

   private static final class Serialization implements Serializable {
      private static final long serialVersionUID = 1L;
      protected Class<?> clazz;
      protected String name;

      public Serialization(Field f) {
         this.clazz = f.getDeclaringClass();
         this.name = f.getName();
      }
   }
}
