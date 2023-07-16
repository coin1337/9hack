package software.bernie.shadowed.fasterxml.jackson.databind.introspect;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Member;
import java.lang.reflect.Type;
import software.bernie.shadowed.fasterxml.jackson.databind.JavaType;
import software.bernie.shadowed.fasterxml.jackson.databind.util.ClassUtil;

public final class AnnotatedParameter extends AnnotatedMember {
   private static final long serialVersionUID = 1L;
   protected final AnnotatedWithParams _owner;
   protected final JavaType _type;
   protected final int _index;

   public AnnotatedParameter(AnnotatedWithParams owner, JavaType type, TypeResolutionContext typeContext, AnnotationMap annotations, int index) {
      super(typeContext, annotations);
      this._owner = owner;
      this._type = type;
      this._index = index;
   }

   public AnnotatedParameter withAnnotations(AnnotationMap ann) {
      return ann == this._annotations ? this : this._owner.replaceParameterAnnotations(this._index, ann);
   }

   public AnnotatedElement getAnnotated() {
      return null;
   }

   public int getModifiers() {
      return this._owner.getModifiers();
   }

   public String getName() {
      return "";
   }

   public Class<?> getRawType() {
      return this._type.getRawClass();
   }

   public JavaType getType() {
      return this._type;
   }

   /** @deprecated */
   @Deprecated
   public Type getGenericType() {
      return this._owner.getGenericParameterType(this._index);
   }

   public Class<?> getDeclaringClass() {
      return this._owner.getDeclaringClass();
   }

   public Member getMember() {
      return this._owner.getMember();
   }

   public void setValue(Object pojo, Object value) throws UnsupportedOperationException {
      throw new UnsupportedOperationException("Cannot call setValue() on constructor parameter of " + this.getDeclaringClass().getName());
   }

   public Object getValue(Object pojo) throws UnsupportedOperationException {
      throw new UnsupportedOperationException("Cannot call getValue() on constructor parameter of " + this.getDeclaringClass().getName());
   }

   public Type getParameterType() {
      return this._type;
   }

   public AnnotatedWithParams getOwner() {
      return this._owner;
   }

   public int getIndex() {
      return this._index;
   }

   public int hashCode() {
      return this._owner.hashCode() + this._index;
   }

   public boolean equals(Object o) {
      if (o == this) {
         return true;
      } else if (!ClassUtil.hasClass(o, this.getClass())) {
         return false;
      } else {
         AnnotatedParameter other = (AnnotatedParameter)o;
         return other._owner.equals(this._owner) && other._index == this._index;
      }
   }

   public String toString() {
      return "[parameter #" + this.getIndex() + ", annotations: " + this._annotations + "]";
   }
}
