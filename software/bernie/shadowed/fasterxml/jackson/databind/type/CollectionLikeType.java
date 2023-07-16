package software.bernie.shadowed.fasterxml.jackson.databind.type;

import java.lang.reflect.TypeVariable;
import java.util.Collection;
import software.bernie.shadowed.fasterxml.jackson.databind.JavaType;

public class CollectionLikeType extends TypeBase {
   private static final long serialVersionUID = 1L;
   protected final JavaType _elementType;

   protected CollectionLikeType(Class<?> collT, TypeBindings bindings, JavaType superClass, JavaType[] superInts, JavaType elemT, Object valueHandler, Object typeHandler, boolean asStatic) {
      super(collT, bindings, superClass, superInts, elemT.hashCode(), valueHandler, typeHandler, asStatic);
      this._elementType = elemT;
   }

   protected CollectionLikeType(TypeBase base, JavaType elemT) {
      super(base);
      this._elementType = elemT;
   }

   public static CollectionLikeType construct(Class<?> rawType, TypeBindings bindings, JavaType superClass, JavaType[] superInts, JavaType elemT) {
      return new CollectionLikeType(rawType, bindings, superClass, superInts, elemT, (Object)null, (Object)null, false);
   }

   /** @deprecated */
   @Deprecated
   public static CollectionLikeType construct(Class<?> rawType, JavaType elemT) {
      TypeVariable<?>[] vars = rawType.getTypeParameters();
      TypeBindings bindings;
      if (vars != null && vars.length == 1) {
         bindings = TypeBindings.create(rawType, elemT);
      } else {
         bindings = TypeBindings.emptyBindings();
      }

      return new CollectionLikeType(rawType, bindings, _bogusSuperClass(rawType), (JavaType[])null, elemT, (Object)null, (Object)null, false);
   }

   public static CollectionLikeType upgradeFrom(JavaType baseType, JavaType elementType) {
      if (baseType instanceof TypeBase) {
         return new CollectionLikeType((TypeBase)baseType, elementType);
      } else {
         throw new IllegalArgumentException("Cannot upgrade from an instance of " + baseType.getClass());
      }
   }

   /** @deprecated */
   @Deprecated
   protected JavaType _narrow(Class<?> subclass) {
      return new CollectionLikeType(subclass, this._bindings, this._superClass, this._superInterfaces, this._elementType, this._valueHandler, this._typeHandler, this._asStatic);
   }

   public JavaType withContentType(JavaType contentType) {
      return this._elementType == contentType ? this : new CollectionLikeType(this._class, this._bindings, this._superClass, this._superInterfaces, contentType, this._valueHandler, this._typeHandler, this._asStatic);
   }

   public CollectionLikeType withTypeHandler(Object h) {
      return new CollectionLikeType(this._class, this._bindings, this._superClass, this._superInterfaces, this._elementType, this._valueHandler, h, this._asStatic);
   }

   public CollectionLikeType withContentTypeHandler(Object h) {
      return new CollectionLikeType(this._class, this._bindings, this._superClass, this._superInterfaces, this._elementType.withTypeHandler(h), this._valueHandler, this._typeHandler, this._asStatic);
   }

   public CollectionLikeType withValueHandler(Object h) {
      return new CollectionLikeType(this._class, this._bindings, this._superClass, this._superInterfaces, this._elementType, h, this._typeHandler, this._asStatic);
   }

   public CollectionLikeType withContentValueHandler(Object h) {
      return new CollectionLikeType(this._class, this._bindings, this._superClass, this._superInterfaces, this._elementType.withValueHandler(h), this._valueHandler, this._typeHandler, this._asStatic);
   }

   public JavaType withHandlersFrom(JavaType src) {
      JavaType type = super.withHandlersFrom(src);
      JavaType srcCt = src.getContentType();
      if (srcCt != null) {
         JavaType ct = this._elementType.withHandlersFrom(srcCt);
         if (ct != this._elementType) {
            type = type.withContentType(ct);
         }
      }

      return type;
   }

   public CollectionLikeType withStaticTyping() {
      return this._asStatic ? this : new CollectionLikeType(this._class, this._bindings, this._superClass, this._superInterfaces, this._elementType.withStaticTyping(), this._valueHandler, this._typeHandler, true);
   }

   public JavaType refine(Class<?> rawType, TypeBindings bindings, JavaType superClass, JavaType[] superInterfaces) {
      return new CollectionLikeType(rawType, bindings, superClass, superInterfaces, this._elementType, this._valueHandler, this._typeHandler, this._asStatic);
   }

   public boolean isContainerType() {
      return true;
   }

   public boolean isCollectionLikeType() {
      return true;
   }

   public JavaType getContentType() {
      return this._elementType;
   }

   public Object getContentValueHandler() {
      return this._elementType.getValueHandler();
   }

   public Object getContentTypeHandler() {
      return this._elementType.getTypeHandler();
   }

   public boolean hasHandlers() {
      return super.hasHandlers() || this._elementType.hasHandlers();
   }

   public StringBuilder getErasedSignature(StringBuilder sb) {
      return _classSignature(this._class, sb, true);
   }

   public StringBuilder getGenericSignature(StringBuilder sb) {
      _classSignature(this._class, sb, false);
      sb.append('<');
      this._elementType.getGenericSignature(sb);
      sb.append(">;");
      return sb;
   }

   protected String buildCanonicalName() {
      StringBuilder sb = new StringBuilder();
      sb.append(this._class.getName());
      if (this._elementType != null) {
         sb.append('<');
         sb.append(this._elementType.toCanonical());
         sb.append('>');
      }

      return sb.toString();
   }

   public boolean isTrueCollectionType() {
      return Collection.class.isAssignableFrom(this._class);
   }

   public boolean equals(Object o) {
      if (o == this) {
         return true;
      } else if (o == null) {
         return false;
      } else if (o.getClass() != this.getClass()) {
         return false;
      } else {
         CollectionLikeType other = (CollectionLikeType)o;
         return this._class == other._class && this._elementType.equals(other._elementType);
      }
   }

   public String toString() {
      return "[collection-like type; class " + this._class.getName() + ", contains " + this._elementType + "]";
   }
}
