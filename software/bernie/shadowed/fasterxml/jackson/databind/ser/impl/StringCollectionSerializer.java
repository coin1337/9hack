package software.bernie.shadowed.fasterxml.jackson.databind.ser.impl;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
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
public class StringCollectionSerializer extends StaticListSerializerBase<Collection<String>> {
   public static final StringCollectionSerializer instance = new StringCollectionSerializer();

   protected StringCollectionSerializer() {
      super(Collection.class);
   }

   protected StringCollectionSerializer(StringCollectionSerializer src, Boolean unwrapSingle) {
      super(src, unwrapSingle);
   }

   public JsonSerializer<?> _withResolved(BeanProperty prop, Boolean unwrapSingle) {
      return new StringCollectionSerializer(this, unwrapSingle);
   }

   protected JsonNode contentSchema() {
      return this.createSchemaNode("string", true);
   }

   protected void acceptContentVisitor(JsonArrayFormatVisitor visitor) throws JsonMappingException {
      visitor.itemsFormat(JsonFormatTypes.STRING);
   }

   public void serialize(Collection<String> value, JsonGenerator g, SerializerProvider provider) throws IOException {
      g.setCurrentValue(value);
      int len = value.size();
      if (len != 1 || (this._unwrapSingle != null || !provider.isEnabled(SerializationFeature.WRITE_SINGLE_ELEM_ARRAYS_UNWRAPPED)) && this._unwrapSingle != Boolean.TRUE) {
         g.writeStartArray(len);
         this.serializeContents(value, g, provider);
         g.writeEndArray();
      } else {
         this.serializeContents(value, g, provider);
      }
   }

   public void serializeWithType(Collection<String> value, JsonGenerator g, SerializerProvider provider, TypeSerializer typeSer) throws IOException {
      g.setCurrentValue(value);
      WritableTypeId typeIdDef = typeSer.writeTypePrefix(g, typeSer.typeId(value, JsonToken.START_ARRAY));
      this.serializeContents(value, g, provider);
      typeSer.writeTypeSuffix(g, typeIdDef);
   }

   private final void serializeContents(Collection<String> value, JsonGenerator g, SerializerProvider provider) throws IOException {
      int i = 0;

      try {
         for(Iterator i$ = value.iterator(); i$.hasNext(); ++i) {
            String str = (String)i$.next();
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
