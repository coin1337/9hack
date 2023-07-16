package software.bernie.shadowed.fasterxml.jackson.databind.type;

import software.bernie.shadowed.fasterxml.jackson.databind.JavaType;

public class ResolvedRecursiveType extends TypeBase {
   private static final long serialVersionUID = 1L;
   protected JavaType _referencedType;

   public ResolvedRecursiveType(Class<?> erasedType, TypeBindings bindings) {
      super(erasedType, bindings, (JavaType)null, (JavaType[])null, 0, (Object)null, (Object)null, false);
   }

   public void setReference(JavaType ref) {
      if (this._referencedType != null) {
         throw new IllegalStateException("Trying to re-set self reference; old value = " + this._referencedType + ", new = " + ref);
      } else {
         this._referencedType = ref;
      }
   }

   public JavaType getSuperClass() {
      return this._referencedType != null ? this._referencedType.getSuperClass() : super.getSuperClass();
   }

   public JavaType getSelfReferencedType() {
      return this._referencedType;
   }

   public StringBuilder getGenericSignature(StringBuilder sb) {
      return this._referencedType.getGenericSignature(sb);
   }

   public StringBuilder getErasedSignature(StringBuilder sb) {
      return this._referencedType.getErasedSignature(sb);
   }

   public JavaType withContentType(JavaType contentType) {
      return this;
   }

   public JavaType withTypeHandler(Object h) {
      return this;
   }

   public JavaType withContentTypeHandler(Object h) {
      return this;
   }

   public JavaType withValueHandler(Object h) {
      return this;
   }

   public JavaType withContentValueHandler(Object h) {
      return this;
   }

   public JavaType withStaticTyping() {
      return this;
   }

   /** @deprecated */
   @Deprecated
   protected JavaType _narrow(Class<?> subclass) {
      return this;
   }

   public JavaType refine(Class<?> rawType, TypeBindings bindings, JavaType superClass, JavaType[] superInterfaces) {
      return null;
   }

   public boolean isContainerType() {
      return false;
   }

   public String toString() {
      StringBuilder sb = (new StringBuilder(40)).append("[recursive type; ");
      if (this._referencedType == null) {
         sb.append("UNRESOLVED");
      } else {
         sb.append(this._referencedType.getRawClass().getName());
      }

      return sb.toString();
   }

   public boolean equals(Object o) {
      if (o == this) {
         return true;
      } else if (o == null) {
         return false;
      } else {
         return o.getClass() == this.getClass() ? false : false;
      }
   }
}
