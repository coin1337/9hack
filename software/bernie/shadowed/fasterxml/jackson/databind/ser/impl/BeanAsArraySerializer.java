package software.bernie.shadowed.fasterxml.jackson.databind.ser.impl;

import java.io.IOException;
import java.util.Set;
import software.bernie.shadowed.fasterxml.jackson.core.JsonGenerator;
import software.bernie.shadowed.fasterxml.jackson.core.JsonToken;
import software.bernie.shadowed.fasterxml.jackson.core.type.WritableTypeId;
import software.bernie.shadowed.fasterxml.jackson.databind.JsonMappingException;
import software.bernie.shadowed.fasterxml.jackson.databind.JsonSerializer;
import software.bernie.shadowed.fasterxml.jackson.databind.SerializationFeature;
import software.bernie.shadowed.fasterxml.jackson.databind.SerializerProvider;
import software.bernie.shadowed.fasterxml.jackson.databind.jsontype.TypeSerializer;
import software.bernie.shadowed.fasterxml.jackson.databind.ser.BeanPropertyWriter;
import software.bernie.shadowed.fasterxml.jackson.databind.ser.std.BeanSerializerBase;
import software.bernie.shadowed.fasterxml.jackson.databind.util.NameTransformer;

public class BeanAsArraySerializer extends BeanSerializerBase {
   private static final long serialVersionUID = 1L;
   protected final BeanSerializerBase _defaultSerializer;

   public BeanAsArraySerializer(BeanSerializerBase src) {
      super(src, (ObjectIdWriter)null);
      this._defaultSerializer = src;
   }

   protected BeanAsArraySerializer(BeanSerializerBase src, Set<String> toIgnore) {
      super(src, toIgnore);
      this._defaultSerializer = src;
   }

   protected BeanAsArraySerializer(BeanSerializerBase src, ObjectIdWriter oiw, Object filterId) {
      super(src, oiw, filterId);
      this._defaultSerializer = src;
   }

   public JsonSerializer<Object> unwrappingSerializer(NameTransformer transformer) {
      return this._defaultSerializer.unwrappingSerializer(transformer);
   }

   public boolean isUnwrappingSerializer() {
      return false;
   }

   public BeanSerializerBase withObjectIdWriter(ObjectIdWriter objectIdWriter) {
      return this._defaultSerializer.withObjectIdWriter(objectIdWriter);
   }

   public BeanSerializerBase withFilterId(Object filterId) {
      return new BeanAsArraySerializer(this, this._objectIdWriter, filterId);
   }

   protected BeanAsArraySerializer withIgnorals(Set<String> toIgnore) {
      return new BeanAsArraySerializer(this, toIgnore);
   }

   protected BeanSerializerBase asArraySerializer() {
      return this;
   }

   public void serializeWithType(Object bean, JsonGenerator gen, SerializerProvider provider, TypeSerializer typeSer) throws IOException {
      if (this._objectIdWriter != null) {
         this._serializeWithObjectId(bean, gen, provider, typeSer);
      } else {
         gen.setCurrentValue(bean);
         WritableTypeId typeIdDef = this._typeIdDef(typeSer, bean, JsonToken.START_ARRAY);
         typeSer.writeTypePrefix(gen, typeIdDef);
         this.serializeAsArray(bean, gen, provider);
         typeSer.writeTypeSuffix(gen, typeIdDef);
      }
   }

   public final void serialize(Object bean, JsonGenerator gen, SerializerProvider provider) throws IOException {
      if (provider.isEnabled(SerializationFeature.WRITE_SINGLE_ELEM_ARRAYS_UNWRAPPED) && this.hasSingleElement(provider)) {
         this.serializeAsArray(bean, gen, provider);
      } else {
         gen.writeStartArray();
         gen.setCurrentValue(bean);
         this.serializeAsArray(bean, gen, provider);
         gen.writeEndArray();
      }
   }

   private boolean hasSingleElement(SerializerProvider provider) {
      BeanPropertyWriter[] props;
      if (this._filteredProps != null && provider.getActiveView() != null) {
         props = this._filteredProps;
      } else {
         props = this._props;
      }

      return props.length == 1;
   }

   protected final void serializeAsArray(Object bean, JsonGenerator gen, SerializerProvider provider) throws IOException {
      BeanPropertyWriter[] props;
      if (this._filteredProps != null && provider.getActiveView() != null) {
         props = this._filteredProps;
      } else {
         props = this._props;
      }

      int i = 0;

      try {
         for(int len = props.length; i < len; ++i) {
            BeanPropertyWriter prop = props[i];
            if (prop == null) {
               gen.writeNull();
            } else {
               prop.serializeAsElement(bean, gen, provider);
            }
         }
      } catch (Exception var9) {
         String name = i == props.length ? "[anySetter]" : props[i].getName();
         this.wrapAndThrow(provider, var9, bean, name);
      } catch (StackOverflowError var10) {
         JsonMappingException mapE = JsonMappingException.from((JsonGenerator)gen, "Infinite recursion (StackOverflowError)", var10);
         String name = i == props.length ? "[anySetter]" : props[i].getName();
         mapE.prependPath(new JsonMappingException.Reference(bean, name));
         throw mapE;
      }

   }

   public String toString() {
      return "BeanAsArraySerializer for " + this.handledType().getName();
   }
}
