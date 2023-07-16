package software.bernie.shadowed.fasterxml.jackson.databind.ser.impl;

import java.io.IOException;
import java.io.Serializable;
import java.util.Set;
import software.bernie.shadowed.fasterxml.jackson.core.JsonGenerator;
import software.bernie.shadowed.fasterxml.jackson.databind.JsonSerializer;
import software.bernie.shadowed.fasterxml.jackson.databind.SerializationFeature;
import software.bernie.shadowed.fasterxml.jackson.databind.SerializerProvider;
import software.bernie.shadowed.fasterxml.jackson.databind.jsontype.TypeSerializer;
import software.bernie.shadowed.fasterxml.jackson.databind.ser.std.BeanSerializerBase;
import software.bernie.shadowed.fasterxml.jackson.databind.util.NameTransformer;

public class UnwrappingBeanSerializer extends BeanSerializerBase implements Serializable {
   private static final long serialVersionUID = 1L;
   protected final NameTransformer _nameTransformer;

   public UnwrappingBeanSerializer(BeanSerializerBase src, NameTransformer transformer) {
      super(src, transformer);
      this._nameTransformer = transformer;
   }

   public UnwrappingBeanSerializer(UnwrappingBeanSerializer src, ObjectIdWriter objectIdWriter) {
      super(src, (ObjectIdWriter)objectIdWriter);
      this._nameTransformer = src._nameTransformer;
   }

   public UnwrappingBeanSerializer(UnwrappingBeanSerializer src, ObjectIdWriter objectIdWriter, Object filterId) {
      super(src, (ObjectIdWriter)objectIdWriter, (Object)filterId);
      this._nameTransformer = src._nameTransformer;
   }

   protected UnwrappingBeanSerializer(UnwrappingBeanSerializer src, Set<String> toIgnore) {
      super(src, (Set)toIgnore);
      this._nameTransformer = src._nameTransformer;
   }

   public JsonSerializer<Object> unwrappingSerializer(NameTransformer transformer) {
      return new UnwrappingBeanSerializer(this, transformer);
   }

   public boolean isUnwrappingSerializer() {
      return true;
   }

   public BeanSerializerBase withObjectIdWriter(ObjectIdWriter objectIdWriter) {
      return new UnwrappingBeanSerializer(this, objectIdWriter);
   }

   public BeanSerializerBase withFilterId(Object filterId) {
      return new UnwrappingBeanSerializer(this, this._objectIdWriter, filterId);
   }

   protected BeanSerializerBase withIgnorals(Set<String> toIgnore) {
      return new UnwrappingBeanSerializer(this, toIgnore);
   }

   protected BeanSerializerBase asArraySerializer() {
      return this;
   }

   public final void serialize(Object bean, JsonGenerator gen, SerializerProvider provider) throws IOException {
      gen.setCurrentValue(bean);
      if (this._objectIdWriter != null) {
         this._serializeWithObjectId(bean, gen, provider, false);
      } else {
         if (this._propertyFilterId != null) {
            this.serializeFieldsFiltered(bean, gen, provider);
         } else {
            this.serializeFields(bean, gen, provider);
         }

      }
   }

   public void serializeWithType(Object bean, JsonGenerator gen, SerializerProvider provider, TypeSerializer typeSer) throws IOException {
      if (provider.isEnabled(SerializationFeature.FAIL_ON_UNWRAPPED_TYPE_IDENTIFIERS)) {
         provider.reportBadDefinition(this.handledType(), "Unwrapped property requires use of type information: cannot serialize without disabling `SerializationFeature.FAIL_ON_UNWRAPPED_TYPE_IDENTIFIERS`");
      }

      gen.setCurrentValue(bean);
      if (this._objectIdWriter != null) {
         this._serializeWithObjectId(bean, gen, provider, typeSer);
      } else {
         if (this._propertyFilterId != null) {
            this.serializeFieldsFiltered(bean, gen, provider);
         } else {
            this.serializeFields(bean, gen, provider);
         }

      }
   }

   public String toString() {
      return "UnwrappingBeanSerializer for " + this.handledType().getName();
   }
}
