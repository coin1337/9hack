package software.bernie.shadowed.fasterxml.jackson.databind.type;

import software.bernie.shadowed.fasterxml.jackson.databind.JavaType;

public class ReferenceType extends SimpleType {
   private static final long serialVersionUID = 1L;
   protected final JavaType _referencedType;
   protected final JavaType _anchorType;

   protected ReferenceType(Class<?> cls, TypeBindings bindings, JavaType superClass, JavaType[] superInts, JavaType refType, JavaType anchorType, Object valueHandler, Object typeHandler, boolean asStatic) {
      super(cls, bindings, superClass, superInts, refType.hashCode(), valueHandler, typeHandler, asStatic);
      this._referencedType = refType;
      this._anchorType = (JavaType)(anchorType == null ? this : anchorType);
   }

   protected ReferenceType(TypeBase base, JavaType refType) {
      super(base);
      this._referencedType = refType;
      this._anchorType = this;
   }

   public static ReferenceType upgradeFrom(JavaType baseType, JavaType refdType) {
      if (refdType == null) {
         throw new IllegalArgumentException("Missing referencedType");
      } else if (baseType instanceof TypeBase) {
         return new ReferenceType((TypeBase)baseType, refdType);
      } else {
         throw new IllegalArgumentException("Cannot upgrade from an instance of " + baseType.getClass());
      }
   }

   public static ReferenceType construct(Class<?> cls, TypeBindings bindings, JavaType superClass, JavaType[] superInts, JavaType refType) {
      return new ReferenceType(cls, bindings, superClass, superInts, refType, (JavaType)null, (Object)null, (Object)null, false);
   }

   /** @deprecated */
   @Deprecated
   public static ReferenceType construct(Class<?> cls, JavaType refType) {
      return new ReferenceType(cls, TypeBindings.emptyBindings(), (JavaType)null, (JavaType[])null, (JavaType)null, refType, (Object)null, (Object)null, false);
   }

   public JavaType withContentType(JavaType contentType) {
      return this._referencedType == contentType ? this : new ReferenceType(this._class, this._bindings, this._superClass, this._superInterfaces, contentType, this._anchorType, this._valueHandler, this._typeHandler, this._asStatic);
   }

   public ReferenceType withTypeHandler(Object h) {
      return h == this._typeHandler ? this : new ReferenceType(this._class, this._bindings, this._superClass, this._superInterfaces, this._referencedType, this._anchorType, this._valueHandler, h, this._asStatic);
   }

   public ReferenceType withContentTypeHandler(Object h) {
      return h == this._referencedType.getTypeHandler() ? this : new ReferenceType(this._class, this._bindings, this._superClass, this._superInterfaces, this._referencedType.withTypeHandler(h), this._anchorType, this._valueHandler, this._typeHandler, this._asStatic);
   }

   public ReferenceType withValueHandler(Object h) {
      return h == this._valueHandler ? this : new ReferenceType(this._class, this._bindings, this._superClass, this._superInterfaces, this._referencedType, this._anchorType, h, this._typeHandler, this._asStatic);
   }

   public ReferenceType withContentValueHandler(Object h) {
      if (h == this._referencedType.getValueHandler()) {
         return this;
      } else {
         JavaType refdType = this._referencedType.withValueHandler(h);
         return new ReferenceType(this._class, this._bindings, this._superClass, this._superInterfaces, refdType, this._anchorType, this._valueHandler, this._typeHandler, this._asStatic);
      }
   }

   public ReferenceType withStaticTyping() {
      return this._asStatic ? this : new ReferenceType(this._class, this._bindings, this._superClass, this._superInterfaces, this._referencedType.withStaticTyping(), this._anchorType, this._valueHandler, this._typeHandler, true);
   }

   public JavaType refine(Class<?> rawType, TypeBindings bindings, JavaType superClass, JavaType[] superInterfaces) {
      return new ReferenceType(rawType, this._bindings, superClass, superInterfaces, this._referencedType, this._anchorType, this._valueHandler, this._typeHandler, this._asStatic);
   }

   protected String buildCanonicalName() {
      StringBuilder sb = new StringBuilder();
      sb.append(this._class.getName());
      sb.append('<');
      sb.append(this._referencedType.toCanonical());
      return sb.toString();
   }

   /** @deprecated */
   @Deprecated
   protected JavaType _narrow(Class<?> subclass) {
      return new ReferenceType(subclass, this._bindings, this._superClass, this._superInterfaces, this._referencedType, this._anchorType, this._valueHandler, this._typeHandler, this._asStatic);
   }

   public JavaType getContentType() {
      return this._referencedType;
   }

   public JavaType getReferencedType() {
      return this._referencedType;
   }

   public boolean hasContentType() {
      return true;
   }

   public boolean isReferenceType() {
      return true;
   }

   public StringBuilder getErasedSignature(StringBuilder sb) {
      return _classSignature(this._class, sb, true);
   }

   public StringBuilder getGenericSignature(StringBuilder sb) {
      _classSignature(this._class, sb, false);
      sb.append('<');
      sb = this._referencedType.getGenericSignature(sb);
      sb.append(">;");
      return sb;
   }

   public JavaType getAnchorType() {
      return this._anchorType;
   }

   public boolean isAnchorType() {
      return this._anchorType == this;
   }

   public String toString() {
      return (new StringBuilder(40)).append("[reference type, class ").append(this.buildCanonicalName()).append('<').append(this._referencedType).append('>').append(']').toString();
   }

   public boolean equals(Object o) {
      if (o == this) {
         return true;
      } else if (o == null) {
         return false;
      } else if (o.getClass() != this.getClass()) {
         return false;
      } else {
         ReferenceType other = (ReferenceType)o;
         return other._class != this._class ? false : this._referencedType.equals(other._referencedType);
      }
   }
}
