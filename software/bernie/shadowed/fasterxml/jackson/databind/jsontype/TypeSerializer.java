package software.bernie.shadowed.fasterxml.jackson.databind.jsontype;

import java.io.IOException;
import software.bernie.shadowed.fasterxml.jackson.annotation.JsonTypeInfo;
import software.bernie.shadowed.fasterxml.jackson.core.JsonGenerator;
import software.bernie.shadowed.fasterxml.jackson.core.JsonToken;
import software.bernie.shadowed.fasterxml.jackson.core.type.WritableTypeId;
import software.bernie.shadowed.fasterxml.jackson.core.util.VersionUtil;
import software.bernie.shadowed.fasterxml.jackson.databind.BeanProperty;

public abstract class TypeSerializer {
   public abstract TypeSerializer forProperty(BeanProperty var1);

   public abstract JsonTypeInfo.As getTypeInclusion();

   public abstract String getPropertyName();

   public abstract TypeIdResolver getTypeIdResolver();

   public WritableTypeId typeId(Object value, JsonToken valueShape) {
      WritableTypeId typeIdDef = new WritableTypeId(value, valueShape);
      switch(this.getTypeInclusion()) {
      case EXISTING_PROPERTY:
         typeIdDef.include = WritableTypeId.Inclusion.PAYLOAD_PROPERTY;
         typeIdDef.asProperty = this.getPropertyName();
         break;
      case EXTERNAL_PROPERTY:
         typeIdDef.include = WritableTypeId.Inclusion.PARENT_PROPERTY;
         typeIdDef.asProperty = this.getPropertyName();
         break;
      case PROPERTY:
         typeIdDef.include = WritableTypeId.Inclusion.METADATA_PROPERTY;
         typeIdDef.asProperty = this.getPropertyName();
         break;
      case WRAPPER_ARRAY:
         typeIdDef.include = WritableTypeId.Inclusion.WRAPPER_ARRAY;
         break;
      case WRAPPER_OBJECT:
         typeIdDef.include = WritableTypeId.Inclusion.WRAPPER_OBJECT;
         break;
      default:
         VersionUtil.throwInternal();
      }

      return typeIdDef;
   }

   public WritableTypeId typeId(Object value, JsonToken valueShape, Object id) {
      WritableTypeId typeId = this.typeId(value, valueShape);
      typeId.id = id;
      return typeId;
   }

   public WritableTypeId typeId(Object value, Class<?> typeForId, JsonToken valueShape) {
      WritableTypeId typeId = this.typeId(value, valueShape);
      typeId.forValueType = typeForId;
      return typeId;
   }

   public abstract WritableTypeId writeTypePrefix(JsonGenerator var1, WritableTypeId var2) throws IOException;

   public abstract WritableTypeId writeTypeSuffix(JsonGenerator var1, WritableTypeId var2) throws IOException;

   /** @deprecated */
   @Deprecated
   public void writeTypePrefixForScalar(Object value, JsonGenerator g) throws IOException {
      this.writeTypePrefix(g, this.typeId(value, JsonToken.VALUE_STRING));
   }

   /** @deprecated */
   @Deprecated
   public void writeTypePrefixForObject(Object value, JsonGenerator g) throws IOException {
      this.writeTypePrefix(g, this.typeId(value, JsonToken.START_OBJECT));
   }

   /** @deprecated */
   @Deprecated
   public void writeTypePrefixForArray(Object value, JsonGenerator g) throws IOException {
      this.writeTypePrefix(g, this.typeId(value, JsonToken.START_ARRAY));
   }

   /** @deprecated */
   @Deprecated
   public void writeTypeSuffixForScalar(Object value, JsonGenerator g) throws IOException {
      this._writeLegacySuffix(g, this.typeId(value, JsonToken.VALUE_STRING));
   }

   /** @deprecated */
   @Deprecated
   public void writeTypeSuffixForObject(Object value, JsonGenerator g) throws IOException {
      this._writeLegacySuffix(g, this.typeId(value, JsonToken.START_OBJECT));
   }

   /** @deprecated */
   @Deprecated
   public void writeTypeSuffixForArray(Object value, JsonGenerator g) throws IOException {
      this._writeLegacySuffix(g, this.typeId(value, JsonToken.START_ARRAY));
   }

   /** @deprecated */
   @Deprecated
   public void writeTypePrefixForScalar(Object value, JsonGenerator g, Class<?> type) throws IOException {
      this.writeTypePrefix(g, this.typeId(value, type, JsonToken.VALUE_STRING));
   }

   /** @deprecated */
   @Deprecated
   public void writeTypePrefixForObject(Object value, JsonGenerator g, Class<?> type) throws IOException {
      this.writeTypePrefix(g, this.typeId(value, type, JsonToken.START_OBJECT));
   }

   /** @deprecated */
   @Deprecated
   public void writeTypePrefixForArray(Object value, JsonGenerator g, Class<?> type) throws IOException {
      this.writeTypePrefix(g, this.typeId(value, type, JsonToken.START_ARRAY));
   }

   /** @deprecated */
   @Deprecated
   public void writeCustomTypePrefixForScalar(Object value, JsonGenerator g, String typeId) throws IOException {
      this.writeTypePrefix(g, this.typeId(value, (JsonToken)JsonToken.VALUE_STRING, (Object)typeId));
   }

   /** @deprecated */
   @Deprecated
   public void writeCustomTypePrefixForObject(Object value, JsonGenerator g, String typeId) throws IOException {
      this.writeTypePrefix(g, this.typeId(value, (JsonToken)JsonToken.START_OBJECT, (Object)typeId));
   }

   /** @deprecated */
   @Deprecated
   public void writeCustomTypePrefixForArray(Object value, JsonGenerator g, String typeId) throws IOException {
      this.writeTypePrefix(g, this.typeId(value, (JsonToken)JsonToken.START_ARRAY, (Object)typeId));
   }

   /** @deprecated */
   @Deprecated
   public void writeCustomTypeSuffixForScalar(Object value, JsonGenerator g, String typeId) throws IOException {
      this._writeLegacySuffix(g, this.typeId(value, (JsonToken)JsonToken.VALUE_STRING, (Object)typeId));
   }

   /** @deprecated */
   @Deprecated
   public void writeCustomTypeSuffixForObject(Object value, JsonGenerator g, String typeId) throws IOException {
      this._writeLegacySuffix(g, this.typeId(value, (JsonToken)JsonToken.START_OBJECT, (Object)typeId));
   }

   /** @deprecated */
   @Deprecated
   public void writeCustomTypeSuffixForArray(Object value, JsonGenerator g, String typeId) throws IOException {
      this._writeLegacySuffix(g, this.typeId(value, (JsonToken)JsonToken.START_ARRAY, (Object)typeId));
   }

   protected final void _writeLegacySuffix(JsonGenerator g, WritableTypeId typeId) throws IOException {
      typeId.wrapperWritten = !g.canWriteTypeId();
      this.writeTypeSuffix(g, typeId);
   }
}
