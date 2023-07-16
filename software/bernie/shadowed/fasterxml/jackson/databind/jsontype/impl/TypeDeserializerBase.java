package software.bernie.shadowed.fasterxml.jackson.databind.jsontype.impl;

import java.io.IOException;
import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import software.bernie.shadowed.fasterxml.jackson.annotation.JsonTypeInfo;
import software.bernie.shadowed.fasterxml.jackson.core.JsonParser;
import software.bernie.shadowed.fasterxml.jackson.databind.BeanProperty;
import software.bernie.shadowed.fasterxml.jackson.databind.DeserializationContext;
import software.bernie.shadowed.fasterxml.jackson.databind.DeserializationFeature;
import software.bernie.shadowed.fasterxml.jackson.databind.JavaType;
import software.bernie.shadowed.fasterxml.jackson.databind.JsonDeserializer;
import software.bernie.shadowed.fasterxml.jackson.databind.deser.std.NullifyingDeserializer;
import software.bernie.shadowed.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import software.bernie.shadowed.fasterxml.jackson.databind.jsontype.TypeIdResolver;
import software.bernie.shadowed.fasterxml.jackson.databind.util.ClassUtil;

public abstract class TypeDeserializerBase extends TypeDeserializer implements Serializable {
   private static final long serialVersionUID = 1L;
   protected final TypeIdResolver _idResolver;
   protected final JavaType _baseType;
   protected final BeanProperty _property;
   protected final JavaType _defaultImpl;
   protected final String _typePropertyName;
   protected final boolean _typeIdVisible;
   protected final Map<String, JsonDeserializer<Object>> _deserializers;
   protected JsonDeserializer<Object> _defaultImplDeserializer;

   protected TypeDeserializerBase(JavaType baseType, TypeIdResolver idRes, String typePropertyName, boolean typeIdVisible, JavaType defaultImpl) {
      this._baseType = baseType;
      this._idResolver = idRes;
      this._typePropertyName = ClassUtil.nonNullString(typePropertyName);
      this._typeIdVisible = typeIdVisible;
      this._deserializers = new ConcurrentHashMap(16, 0.75F, 2);
      this._defaultImpl = defaultImpl;
      this._property = null;
   }

   protected TypeDeserializerBase(TypeDeserializerBase src, BeanProperty property) {
      this._baseType = src._baseType;
      this._idResolver = src._idResolver;
      this._typePropertyName = src._typePropertyName;
      this._typeIdVisible = src._typeIdVisible;
      this._deserializers = src._deserializers;
      this._defaultImpl = src._defaultImpl;
      this._defaultImplDeserializer = src._defaultImplDeserializer;
      this._property = property;
   }

   public abstract TypeDeserializer forProperty(BeanProperty var1);

   public abstract JsonTypeInfo.As getTypeInclusion();

   public String baseTypeName() {
      return this._baseType.getRawClass().getName();
   }

   public final String getPropertyName() {
      return this._typePropertyName;
   }

   public TypeIdResolver getTypeIdResolver() {
      return this._idResolver;
   }

   public Class<?> getDefaultImpl() {
      return ClassUtil.rawClass(this._defaultImpl);
   }

   public JavaType baseType() {
      return this._baseType;
   }

   public String toString() {
      StringBuilder sb = new StringBuilder();
      sb.append('[').append(this.getClass().getName());
      sb.append("; base-type:").append(this._baseType);
      sb.append("; id-resolver: ").append(this._idResolver);
      sb.append(']');
      return sb.toString();
   }

   protected final JsonDeserializer<Object> _findDeserializer(DeserializationContext ctxt, String typeId) throws IOException {
      JsonDeserializer<Object> deser = (JsonDeserializer)this._deserializers.get(typeId);
      if (deser == null) {
         JavaType type = this._idResolver.typeFromId(ctxt, typeId);
         if (type == null) {
            deser = this._findDefaultImplDeserializer(ctxt);
            if (deser == null) {
               JavaType actual = this._handleUnknownTypeId(ctxt, typeId);
               if (actual == null) {
                  return null;
               }

               deser = ctxt.findContextualValueDeserializer(actual, this._property);
            }
         } else {
            if (this._baseType != null && this._baseType.getClass() == type.getClass() && !type.hasGenericTypes()) {
               type = ctxt.getTypeFactory().constructSpecializedType(this._baseType, type.getRawClass());
            }

            deser = ctxt.findContextualValueDeserializer(type, this._property);
         }

         this._deserializers.put(typeId, deser);
      }

      return deser;
   }

   protected final JsonDeserializer<Object> _findDefaultImplDeserializer(DeserializationContext ctxt) throws IOException {
      if (this._defaultImpl == null) {
         return !ctxt.isEnabled(DeserializationFeature.FAIL_ON_INVALID_SUBTYPE) ? NullifyingDeserializer.instance : null;
      } else {
         Class<?> raw = this._defaultImpl.getRawClass();
         if (ClassUtil.isBogusClass(raw)) {
            return NullifyingDeserializer.instance;
         } else {
            synchronized(this._defaultImpl) {
               if (this._defaultImplDeserializer == null) {
                  this._defaultImplDeserializer = ctxt.findContextualValueDeserializer(this._defaultImpl, this._property);
               }

               return this._defaultImplDeserializer;
            }
         }
      }
   }

   /** @deprecated */
   @Deprecated
   protected Object _deserializeWithNativeTypeId(JsonParser jp, DeserializationContext ctxt) throws IOException {
      return this._deserializeWithNativeTypeId(jp, ctxt, jp.getTypeId());
   }

   protected Object _deserializeWithNativeTypeId(JsonParser jp, DeserializationContext ctxt, Object typeId) throws IOException {
      JsonDeserializer deser;
      if (typeId == null) {
         deser = this._findDefaultImplDeserializer(ctxt);
         if (deser == null) {
            return ctxt.reportInputMismatch(this.baseType(), "No (native) type id found when one was expected for polymorphic type handling");
         }
      } else {
         String typeIdStr = typeId instanceof String ? (String)typeId : String.valueOf(typeId);
         deser = this._findDeserializer(ctxt, typeIdStr);
      }

      return deser.deserialize(jp, ctxt);
   }

   protected JavaType _handleUnknownTypeId(DeserializationContext ctxt, String typeId) throws IOException {
      String extraDesc = this._idResolver.getDescForKnownTypeIds();
      if (extraDesc == null) {
         extraDesc = "type ids are not statically known";
      } else {
         extraDesc = "known type ids = " + extraDesc;
      }

      if (this._property != null) {
         extraDesc = String.format("%s (for POJO property '%s')", extraDesc, this._property.getName());
      }

      return ctxt.handleUnknownTypeId(this._baseType, typeId, this._idResolver, extraDesc);
   }

   protected JavaType _handleMissingTypeId(DeserializationContext ctxt, String extraDesc) throws IOException {
      return ctxt.handleMissingTypeId(this._baseType, this._idResolver, extraDesc);
   }
}
