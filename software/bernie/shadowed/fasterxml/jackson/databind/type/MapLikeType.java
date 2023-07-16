package software.bernie.shadowed.fasterxml.jackson.databind.type;

import java.lang.reflect.TypeVariable;
import java.util.Map;
import software.bernie.shadowed.fasterxml.jackson.databind.JavaType;

public class MapLikeType extends TypeBase {
   private static final long serialVersionUID = 1L;
   protected final JavaType _keyType;
   protected final JavaType _valueType;

   protected MapLikeType(Class<?> mapType, TypeBindings bindings, JavaType superClass, JavaType[] superInts, JavaType keyT, JavaType valueT, Object valueHandler, Object typeHandler, boolean asStatic) {
      super(mapType, bindings, superClass, superInts, keyT.hashCode() ^ valueT.hashCode(), valueHandler, typeHandler, asStatic);
      this._keyType = keyT;
      this._valueType = valueT;
   }

   protected MapLikeType(TypeBase base, JavaType keyT, JavaType valueT) {
      super(base);
      this._keyType = keyT;
      this._valueType = valueT;
   }

   public static MapLikeType upgradeFrom(JavaType baseType, JavaType keyT, JavaType valueT) {
      if (baseType instanceof TypeBase) {
         return new MapLikeType((TypeBase)baseType, keyT, valueT);
      } else {
         throw new IllegalArgumentException("Cannot upgrade from an instance of " + baseType.getClass());
      }
   }

   /** @deprecated */
   @Deprecated
   public static MapLikeType construct(Class<?> rawType, JavaType keyT, JavaType valueT) {
      TypeVariable<?>[] vars = rawType.getTypeParameters();
      TypeBindings bindings;
      if (vars != null && vars.length == 2) {
         bindings = TypeBindings.create(rawType, keyT, valueT);
      } else {
         bindings = TypeBindings.emptyBindings();
      }

      return new MapLikeType(rawType, bindings, _bogusSuperClass(rawType), (JavaType[])null, keyT, valueT, (Object)null, (Object)null, false);
   }

   /** @deprecated */
   @Deprecated
   protected JavaType _narrow(Class<?> subclass) {
      return new MapLikeType(subclass, this._bindings, this._superClass, this._superInterfaces, this._keyType, this._valueType, this._valueHandler, this._typeHandler, this._asStatic);
   }

   public MapLikeType withKeyType(JavaType keyType) {
      return keyType == this._keyType ? this : new MapLikeType(this._class, this._bindings, this._superClass, this._superInterfaces, keyType, this._valueType, this._valueHandler, this._typeHandler, this._asStatic);
   }

   public JavaType withContentType(JavaType contentType) {
      return this._valueType == contentType ? this : new MapLikeType(this._class, this._bindings, this._superClass, this._superInterfaces, this._keyType, contentType, this._valueHandler, this._typeHandler, this._asStatic);
   }

   public MapLikeType withTypeHandler(Object h) {
      return new MapLikeType(this._class, this._bindings, this._superClass, this._superInterfaces, this._keyType, this._valueType, this._valueHandler, h, this._asStatic);
   }

   public MapLikeType withContentTypeHandler(Object h) {
      return new MapLikeType(this._class, this._bindings, this._superClass, this._superInterfaces, this._keyType, this._valueType.withTypeHandler(h), this._valueHandler, this._typeHandler, this._asStatic);
   }

   public MapLikeType withValueHandler(Object h) {
      return new MapLikeType(this._class, this._bindings, this._superClass, this._superInterfaces, this._keyType, this._valueType, h, this._typeHandler, this._asStatic);
   }

   public MapLikeType withContentValueHandler(Object h) {
      return new MapLikeType(this._class, this._bindings, this._superClass, this._superInterfaces, this._keyType, this._valueType.withValueHandler(h), this._valueHandler, this._typeHandler, this._asStatic);
   }

   public JavaType withHandlersFrom(JavaType src) {
      JavaType type = super.withHandlersFrom(src);
      JavaType srcKeyType = src.getKeyType();
      JavaType srcCt;
      if (type instanceof MapLikeType && srcKeyType != null) {
         srcCt = this._keyType.withHandlersFrom(srcKeyType);
         if (srcCt != this._keyType) {
            type = ((MapLikeType)type).withKeyType(srcCt);
         }
      }

      srcCt = src.getContentType();
      if (srcCt != null) {
         JavaType ct = this._valueType.withHandlersFrom(srcCt);
         if (ct != this._valueType) {
            type = ((JavaType)type).withContentType(ct);
         }
      }

      return (JavaType)type;
   }

   public MapLikeType withStaticTyping() {
      return this._asStatic ? this : new MapLikeType(this._class, this._bindings, this._superClass, this._superInterfaces, this._keyType, this._valueType.withStaticTyping(), this._valueHandler, this._typeHandler, true);
   }

   public JavaType refine(Class<?> rawType, TypeBindings bindings, JavaType superClass, JavaType[] superInterfaces) {
      return new MapLikeType(rawType, bindings, superClass, superInterfaces, this._keyType, this._valueType, this._valueHandler, this._typeHandler, this._asStatic);
   }

   protected String buildCanonicalName() {
      StringBuilder sb = new StringBuilder();
      sb.append(this._class.getName());
      if (this._keyType != null) {
         sb.append('<');
         sb.append(this._keyType.toCanonical());
         sb.append(',');
         sb.append(this._valueType.toCanonical());
         sb.append('>');
      }

      return sb.toString();
   }

   public boolean isContainerType() {
      return true;
   }

   public boolean isMapLikeType() {
      return true;
   }

   public JavaType getKeyType() {
      return this._keyType;
   }

   public JavaType getContentType() {
      return this._valueType;
   }

   public Object getContentValueHandler() {
      return this._valueType.getValueHandler();
   }

   public Object getContentTypeHandler() {
      return this._valueType.getTypeHandler();
   }

   public boolean hasHandlers() {
      return super.hasHandlers() || this._valueType.hasHandlers() || this._keyType.hasHandlers();
   }

   public StringBuilder getErasedSignature(StringBuilder sb) {
      return _classSignature(this._class, sb, true);
   }

   public StringBuilder getGenericSignature(StringBuilder sb) {
      _classSignature(this._class, sb, false);
      sb.append('<');
      this._keyType.getGenericSignature(sb);
      this._valueType.getGenericSignature(sb);
      sb.append(">;");
      return sb;
   }

   public MapLikeType withKeyTypeHandler(Object h) {
      return new MapLikeType(this._class, this._bindings, this._superClass, this._superInterfaces, this._keyType.withTypeHandler(h), this._valueType, this._valueHandler, this._typeHandler, this._asStatic);
   }

   public MapLikeType withKeyValueHandler(Object h) {
      return new MapLikeType(this._class, this._bindings, this._superClass, this._superInterfaces, this._keyType.withValueHandler(h), this._valueType, this._valueHandler, this._typeHandler, this._asStatic);
   }

   public boolean isTrueMapType() {
      return Map.class.isAssignableFrom(this._class);
   }

   public String toString() {
      return String.format("[map-like type; class %s, %s -> %s]", this._class.getName(), this._keyType, this._valueType);
   }

   public boolean equals(Object o) {
      if (o == this) {
         return true;
      } else if (o == null) {
         return false;
      } else if (o.getClass() != this.getClass()) {
         return false;
      } else {
         MapLikeType other = (MapLikeType)o;
         return this._class == other._class && this._keyType.equals(other._keyType) && this._valueType.equals(other._valueType);
      }
   }
}
