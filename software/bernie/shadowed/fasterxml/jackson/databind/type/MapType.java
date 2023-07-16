package software.bernie.shadowed.fasterxml.jackson.databind.type;

import java.lang.reflect.TypeVariable;
import software.bernie.shadowed.fasterxml.jackson.databind.JavaType;

public final class MapType extends MapLikeType {
   private static final long serialVersionUID = 1L;

   private MapType(Class<?> mapType, TypeBindings bindings, JavaType superClass, JavaType[] superInts, JavaType keyT, JavaType valueT, Object valueHandler, Object typeHandler, boolean asStatic) {
      super(mapType, bindings, superClass, superInts, keyT, valueT, valueHandler, typeHandler, asStatic);
   }

   protected MapType(TypeBase base, JavaType keyT, JavaType valueT) {
      super(base, keyT, valueT);
   }

   public static MapType construct(Class<?> rawType, TypeBindings bindings, JavaType superClass, JavaType[] superInts, JavaType keyT, JavaType valueT) {
      return new MapType(rawType, bindings, superClass, superInts, keyT, valueT, (Object)null, (Object)null, false);
   }

   /** @deprecated */
   @Deprecated
   public static MapType construct(Class<?> rawType, JavaType keyT, JavaType valueT) {
      TypeVariable<?>[] vars = rawType.getTypeParameters();
      TypeBindings bindings;
      if (vars != null && vars.length == 2) {
         bindings = TypeBindings.create(rawType, keyT, valueT);
      } else {
         bindings = TypeBindings.emptyBindings();
      }

      return new MapType(rawType, bindings, _bogusSuperClass(rawType), (JavaType[])null, keyT, valueT, (Object)null, (Object)null, false);
   }

   /** @deprecated */
   @Deprecated
   protected JavaType _narrow(Class<?> subclass) {
      return new MapType(subclass, this._bindings, this._superClass, this._superInterfaces, this._keyType, this._valueType, this._valueHandler, this._typeHandler, this._asStatic);
   }

   public MapType withTypeHandler(Object h) {
      return new MapType(this._class, this._bindings, this._superClass, this._superInterfaces, this._keyType, this._valueType, this._valueHandler, h, this._asStatic);
   }

   public MapType withContentTypeHandler(Object h) {
      return new MapType(this._class, this._bindings, this._superClass, this._superInterfaces, this._keyType, this._valueType.withTypeHandler(h), this._valueHandler, this._typeHandler, this._asStatic);
   }

   public MapType withValueHandler(Object h) {
      return new MapType(this._class, this._bindings, this._superClass, this._superInterfaces, this._keyType, this._valueType, h, this._typeHandler, this._asStatic);
   }

   public MapType withContentValueHandler(Object h) {
      return new MapType(this._class, this._bindings, this._superClass, this._superInterfaces, this._keyType, this._valueType.withValueHandler(h), this._valueHandler, this._typeHandler, this._asStatic);
   }

   public MapType withStaticTyping() {
      return this._asStatic ? this : new MapType(this._class, this._bindings, this._superClass, this._superInterfaces, this._keyType.withStaticTyping(), this._valueType.withStaticTyping(), this._valueHandler, this._typeHandler, true);
   }

   public JavaType withContentType(JavaType contentType) {
      return this._valueType == contentType ? this : new MapType(this._class, this._bindings, this._superClass, this._superInterfaces, this._keyType, contentType, this._valueHandler, this._typeHandler, this._asStatic);
   }

   public MapType withKeyType(JavaType keyType) {
      return keyType == this._keyType ? this : new MapType(this._class, this._bindings, this._superClass, this._superInterfaces, keyType, this._valueType, this._valueHandler, this._typeHandler, this._asStatic);
   }

   public JavaType refine(Class<?> rawType, TypeBindings bindings, JavaType superClass, JavaType[] superInterfaces) {
      return new MapType(rawType, bindings, superClass, superInterfaces, this._keyType, this._valueType, this._valueHandler, this._typeHandler, this._asStatic);
   }

   public MapType withKeyTypeHandler(Object h) {
      return new MapType(this._class, this._bindings, this._superClass, this._superInterfaces, this._keyType.withTypeHandler(h), this._valueType, this._valueHandler, this._typeHandler, this._asStatic);
   }

   public MapType withKeyValueHandler(Object h) {
      return new MapType(this._class, this._bindings, this._superClass, this._superInterfaces, this._keyType.withValueHandler(h), this._valueType, this._valueHandler, this._typeHandler, this._asStatic);
   }

   public String toString() {
      return "[map type; class " + this._class.getName() + ", " + this._keyType + " -> " + this._valueType + "]";
   }
}
