package software.bernie.shadowed.fasterxml.jackson.databind.ser.impl;

import java.io.IOException;
import java.util.List;
import software.bernie.shadowed.fasterxml.jackson.core.JsonGenerator;
import software.bernie.shadowed.fasterxml.jackson.core.JsonToken;
import software.bernie.shadowed.fasterxml.jackson.core.type.WritableTypeId;
import software.bernie.shadowed.fasterxml.jackson.databind.BeanProperty;
import software.bernie.shadowed.fasterxml.jackson.databind.JsonMappingException;
import software.bernie.shadowed.fasterxml.jackson.databind.JsonNode;
import software.bernie.shadowed.fasterxml.jackson.databind.JsonSerializer;
import software.bernie.shadowed.fasterxml.jackson.databind.SerializationFeature;
import software.bernie.shadowed.fasterxml.jackson.databind.SerializerProvider;
import software.bernie.shadowed.fasterxml.jackson.databind.annotation.JacksonStdImpl;
import software.bernie.shadowed.fasterxml.jackson.databind.jsonFormatVisitors.JsonArrayFormatVisitor;
import software.bernie.shadowed.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatTypes;
import software.bernie.shadowed.fasterxml.jackson.databind.jsontype.TypeSerializer;
import software.bernie.shadowed.fasterxml.jackson.databind.ser.std.StaticListSerializerBase;

@JacksonStdImpl
public final class IndexedStringListSerializer extends StaticListSerializerBase<List<String>> {
   private static final long serialVersionUID = 1L;
   public static final IndexedStringListSerializer instance = new IndexedStringListSerializer();

   protected IndexedStringListSerializer() {
      super(List.class);
   }

   public IndexedStringListSerializer(IndexedStringListSerializer src, Boolean unwrapSingle) {
      super(src, unwrapSingle);
   }

   public JsonSerializer<?> _withResolved(BeanProperty prop, Boolean unwrapSingle) {
      return new IndexedStringListSerializer(this, unwrapSingle);
   }

   protected JsonNode contentSchema() {
      return this.createSchemaNode("string", true);
   }

   protected void acceptContentVisitor(JsonArrayFormatVisitor visitor) throws JsonMappingException {
      visitor.itemsFormat(JsonFormatTypes.STRING);
   }

   public void serialize(List<String> value, JsonGenerator g, SerializerProvider provider) throws IOException {
      int len = value.size();
      if (len != 1 || (this._unwrapSingle != null || !provider.isEnabled(SerializationFeature.WRITE_SINGLE_ELEM_ARRAYS_UNWRAPPED)) && this._unwrapSingle != Boolean.TRUE) {
         g.writeStartArray(len);
         this.serializeContents(value, g, provider, len);
         g.writeEndArray();
      } else {
         this.serializeContents(value, g, provider, 1);
      }
   }

   public void serializeWithType(List<String> value, JsonGenerator g, SerializerProvider provider, TypeSerializer typeSer) throws IOException {
      WritableTypeId typeIdDef = typeSer.writeTypePrefix(g, typeSer.typeId(value, JsonToken.START_ARRAY));
      this.serializeContents(value, g, provider, value.size());
      typeSer.writeTypeSuffix(g, typeIdDef);
   }

   private final void serializeContents(List<String> value, JsonGenerator g, SerializerProvider provider, int len) throws IOException {
      g.setCurrentValue(value);
      int i = 0;

      try {
         for(; i < len; ++i) {
            String str = (String)value.get(i);
            if (str == null) {
               provider.defaultSerializeNull(g);
            } else {
               g.writeString(str);
            }
         }
      } catch (Exception var7) {
         this.wrapAndThrow(provider, var7, value, i);
      }

   }
}
