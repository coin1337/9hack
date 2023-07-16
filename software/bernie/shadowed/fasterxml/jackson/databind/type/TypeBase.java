package software.bernie.shadowed.fasterxml.jackson.databind.type;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import software.bernie.shadowed.fasterxml.jackson.core.JsonGenerator;
import software.bernie.shadowed.fasterxml.jackson.core.JsonProcessingException;
import software.bernie.shadowed.fasterxml.jackson.core.JsonToken;
import software.bernie.shadowed.fasterxml.jackson.core.type.WritableTypeId;
import software.bernie.shadowed.fasterxml.jackson.databind.JavaType;
import software.bernie.shadowed.fasterxml.jackson.databind.JsonSerializable;
import software.bernie.shadowed.fasterxml.jackson.databind.SerializerProvider;
import software.bernie.shadowed.fasterxml.jackson.databind.jsontype.TypeSerializer;

public abstract class TypeBase extends JavaType implements JsonSerializable {
   private static final long serialVersionUID = 1L;
   private static final TypeBindings NO_BINDINGS = TypeBindings.emptyBindings();
   private static final JavaType[] NO_TYPES = new JavaType[0];
   protected final JavaType _superClass;
   protected final JavaType[] _superInterfaces;
   protected final TypeBindings _bindings;
   transient volatile String _canonicalName;

   protected TypeBase(Class<?> raw, TypeBindings bindings, JavaType superClass, JavaType[] superInts, int hash, Object valueHandler, Object typeHandler, boolean asStatic) {
      super(raw, hash, valueHandler, typeHandler, asStatic);
      this._bindings = bindings == null ? NO_BINDINGS : bindings;
      this._superClass = superClass;
      this._superInterfaces = superInts;
   }

   protected TypeBase(TypeBase base) {
      super(base);
      this._superClass = base._superClass;
      this._superInterfaces = base._superInterfaces;
      this._bindings = base._bindings;
   }

   public String toCanonical() {
      String str = this._canonicalName;
      if (str == null) {
         str = this.buildCanonicalName();
      }

      return str;
   }

   protected String buildCanonicalName() {
      return this._class.getName();
   }

   public abstract StringBuilder getGenericSignature(StringBuilder var1);

   public abstract StringBuilder getErasedSignature(StringBuilder var1);

   public TypeBindings getBindings() {
      return this._bindings;
   }

   public int containedTypeCount() {
      return this._bindings.size();
   }

   public JavaType containedType(int index) {
      return this._bindings.getBoundType(index);
   }

   /** @deprecated */
   @Deprecated
   public String containedTypeName(int index) {
      return this._bindings.getBoundName(index);
   }

   public JavaType getSuperClass() {
      return this._superClass;
   }

   public List<JavaType> getInterfaces() {
      if (this._superInterfaces == null) {
         return Collections.emptyList();
      } else {
         switch(this._superInterfaces.length) {
         case 0:
            return Collections.emptyList();
         case 1:
            return Collections.singletonList(this._superInterfaces[0]);
         default:
            return Arrays.asList(this._superInterfaces);
         }
      }
   }

   public final JavaType findSuperType(Class<?> rawTarget) {
      if (rawTarget == this._class) {
         return this;
      } else {
         if (rawTarget.isInterface() && this._superInterfaces != null) {
            int i = 0;

            for(int count = this._superInterfaces.length; i < count; ++i) {
               JavaType type = this._superInterfaces[i].findSuperType(rawTarget);
               if (type != null) {
                  return type;
               }
            }
         }

         if (this._superClass != null) {
            JavaType type = this._superClass.findSuperType(rawTarget);
            if (type != null) {
               return type;
            }
         }

         return null;
      }
   }

   public JavaType[] findTypeParameters(Class<?> expType) {
      JavaType match = this.findSuperType(expType);
      return match == null ? NO_TYPES : match.getBindings().typeParameterArray();
   }

   public void serializeWithType(JsonGenerator g, SerializerProvider provider, TypeSerializer typeSer) throws IOException {
      WritableTypeId typeIdDef = new WritableTypeId(this, JsonToken.VALUE_STRING);
      typeSer.writeTypePrefix(g, typeIdDef);
      this.serialize(g, provider);
      typeSer.writeTypeSuffix(g, typeIdDef);
   }

   public void serialize(JsonGenerator gen, SerializerProvider provider) throws IOException, JsonProcessingException {
      gen.writeString(this.toCanonical());
   }

   protected static StringBuilder _classSignature(Class<?> cls, StringBuilder sb, boolean trailingSemicolon) {
      if (cls.isPrimitive()) {
         if (cls == Boolean.TYPE) {
            sb.append('Z');
         } else if (cls == Byte.TYPE) {
            sb.append('B');
         } else if (cls == Short.TYPE) {
            sb.append('S');
         } else if (cls == Character.TYPE) {
            sb.append('C');
         } else if (cls == Integer.TYPE) {
            sb.append('I');
         } else if (cls == Long.TYPE) {
            sb.append('J');
         } else if (cls == Float.TYPE) {
            sb.append('F');
         } else if (cls == Double.TYPE) {
            sb.append('D');
         } else {
            if (cls != Void.TYPE) {
               throw new IllegalStateException("Unrecognized primitive type: " + cls.getName());
            }

            sb.append('V');
         }
      } else {
         sb.append('L');
         String name = cls.getName();
         int i = 0;

         for(int len = name.length(); i < len; ++i) {
            char c = name.charAt(i);
            if (c == '.') {
               c = '/';
            }

            sb.append(c);
         }

         if (trailingSemicolon) {
            sb.append(';');
         }
      }

      return sb;
   }

   protected static JavaType _bogusSuperClass(Class<?> cls) {
      Class<?> parent = cls.getSuperclass();
      return parent == null ? null : TypeFactory.unknownType();
   }
}
