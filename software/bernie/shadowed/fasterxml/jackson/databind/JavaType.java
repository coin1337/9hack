package software.bernie.shadowed.fasterxml.jackson.databind;

import java.io.Serializable;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.List;
import software.bernie.shadowed.fasterxml.jackson.core.type.ResolvedType;
import software.bernie.shadowed.fasterxml.jackson.databind.type.TypeBindings;
import software.bernie.shadowed.fasterxml.jackson.databind.type.TypeFactory;

public abstract class JavaType extends ResolvedType implements Serializable, Type {
   private static final long serialVersionUID = 1L;
   protected final Class<?> _class;
   protected final int _hash;
   protected final Object _valueHandler;
   protected final Object _typeHandler;
   protected final boolean _asStatic;

   protected JavaType(Class<?> raw, int additionalHash, Object valueHandler, Object typeHandler, boolean asStatic) {
      this._class = raw;
      this._hash = raw.getName().hashCode() + additionalHash;
      this._valueHandler = valueHandler;
      this._typeHandler = typeHandler;
      this._asStatic = asStatic;
   }

   protected JavaType(JavaType base) {
      this._class = base._class;
      this._hash = base._hash;
      this._valueHandler = base._valueHandler;
      this._typeHandler = base._typeHandler;
      this._asStatic = base._asStatic;
   }

   public abstract JavaType withTypeHandler(Object var1);

   public abstract JavaType withContentTypeHandler(Object var1);

   public abstract JavaType withValueHandler(Object var1);

   public abstract JavaType withContentValueHandler(Object var1);

   public JavaType withHandlersFrom(JavaType src) {
      JavaType type = this;
      Object h = src.getTypeHandler();
      if (h != this._typeHandler) {
         type = this.withTypeHandler(h);
      }

      h = src.getValueHandler();
      if (h != this._valueHandler) {
         type = type.withValueHandler(h);
      }

      return type;
   }

   public abstract JavaType withContentType(JavaType var1);

   public abstract JavaType withStaticTyping();

   public abstract JavaType refine(Class<?> var1, TypeBindings var2, JavaType var3, JavaType[] var4);

   /** @deprecated */
   @Deprecated
   public JavaType forcedNarrowBy(Class<?> subclass) {
      return subclass == this._class ? this : this._narrow(subclass);
   }

   /** @deprecated */
   @Deprecated
   protected abstract JavaType _narrow(Class<?> var1);

   public final Class<?> getRawClass() {
      return this._class;
   }

   public final boolean hasRawClass(Class<?> clz) {
      return this._class == clz;
   }

   public boolean hasContentType() {
      return true;
   }

   public final boolean isTypeOrSubTypeOf(Class<?> clz) {
      return this._class == clz || clz.isAssignableFrom(this._class);
   }

   public final boolean isTypeOrSuperTypeOf(Class<?> clz) {
      return this._class == clz || this._class.isAssignableFrom(clz);
   }

   public boolean isAbstract() {
      return Modifier.isAbstract(this._class.getModifiers());
   }

   public boolean isConcrete() {
      int mod = this._class.getModifiers();
      return (mod & 1536) == 0 ? true : this._class.isPrimitive();
   }

   public boolean isThrowable() {
      return Throwable.class.isAssignableFrom(this._class);
   }

   public boolean isArrayType() {
      return false;
   }

   public final boolean isEnumType() {
      return this._class.isEnum();
   }

   public final boolean isInterface() {
      return this._class.isInterface();
   }

   public final boolean isPrimitive() {
      return this._class.isPrimitive();
   }

   public final boolean isFinal() {
      return Modifier.isFinal(this._class.getModifiers());
   }

   public abstract boolean isContainerType();

   public boolean isCollectionLikeType() {
      return false;
   }

   public boolean isMapLikeType() {
      return false;
   }

   public final boolean isJavaLangObject() {
      return this._class == Object.class;
   }

   public final boolean useStaticType() {
      return this._asStatic;
   }

   public boolean hasGenericTypes() {
      return this.containedTypeCount() > 0;
   }

   public JavaType getKeyType() {
      return null;
   }

   public JavaType getContentType() {
      return null;
   }

   public JavaType getReferencedType() {
      return null;
   }

   public abstract int containedTypeCount();

   public abstract JavaType containedType(int var1);

   /** @deprecated */
   @Deprecated
   public abstract String containedTypeName(int var1);

   /** @deprecated */
   @Deprecated
   public Class<?> getParameterSource() {
      return null;
   }

   public JavaType containedTypeOrUnknown(int index) {
      JavaType t = this.containedType(index);
      return t == null ? TypeFactory.unknownType() : t;
   }

   public abstract TypeBindings getBindings();

   public abstract JavaType findSuperType(Class<?> var1);

   public abstract JavaType getSuperClass();

   public abstract List<JavaType> getInterfaces();

   public abstract JavaType[] findTypeParameters(Class<?> var1);

   public <T> T getValueHandler() {
      return this._valueHandler;
   }

   public <T> T getTypeHandler() {
      return this._typeHandler;
   }

   public Object getContentValueHandler() {
      return null;
   }

   public Object getContentTypeHandler() {
      return null;
   }

   public boolean hasValueHandler() {
      return this._valueHandler != null;
   }

   public boolean hasHandlers() {
      return this._typeHandler != null || this._valueHandler != null;
   }

   public String getGenericSignature() {
      StringBuilder sb = new StringBuilder(40);
      this.getGenericSignature(sb);
      return sb.toString();
   }

   public abstract StringBuilder getGenericSignature(StringBuilder var1);

   public String getErasedSignature() {
      StringBuilder sb = new StringBuilder(40);
      this.getErasedSignature(sb);
      return sb.toString();
   }

   public abstract StringBuilder getErasedSignature(StringBuilder var1);

   public abstract String toString();

   public abstract boolean equals(Object var1);

   public final int hashCode() {
      return this._hash;
   }
}
