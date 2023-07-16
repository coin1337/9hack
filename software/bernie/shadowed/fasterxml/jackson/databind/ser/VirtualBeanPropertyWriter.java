package software.bernie.shadowed.fasterxml.jackson.databind.ser;

import java.io.Serializable;
import software.bernie.shadowed.fasterxml.jackson.annotation.JsonInclude;
import software.bernie.shadowed.fasterxml.jackson.core.JsonGenerator;
import software.bernie.shadowed.fasterxml.jackson.core.SerializableString;
import software.bernie.shadowed.fasterxml.jackson.databind.JavaType;
import software.bernie.shadowed.fasterxml.jackson.databind.JsonSerializer;
import software.bernie.shadowed.fasterxml.jackson.databind.PropertyName;
import software.bernie.shadowed.fasterxml.jackson.databind.SerializerProvider;
import software.bernie.shadowed.fasterxml.jackson.databind.cfg.MapperConfig;
import software.bernie.shadowed.fasterxml.jackson.databind.introspect.AnnotatedClass;
import software.bernie.shadowed.fasterxml.jackson.databind.introspect.BeanPropertyDefinition;
import software.bernie.shadowed.fasterxml.jackson.databind.jsontype.TypeSerializer;
import software.bernie.shadowed.fasterxml.jackson.databind.ser.impl.PropertySerializerMap;
import software.bernie.shadowed.fasterxml.jackson.databind.util.Annotations;

public abstract class VirtualBeanPropertyWriter extends BeanPropertyWriter implements Serializable {
   private static final long serialVersionUID = 1L;

   protected VirtualBeanPropertyWriter(BeanPropertyDefinition propDef, Annotations contextAnnotations, JavaType declaredType) {
      this(propDef, contextAnnotations, declaredType, (JsonSerializer)null, (TypeSerializer)null, (JavaType)null, propDef.findInclusion());
   }

   protected VirtualBeanPropertyWriter() {
   }

   protected VirtualBeanPropertyWriter(BeanPropertyDefinition propDef, Annotations contextAnnotations, JavaType declaredType, JsonSerializer<?> ser, TypeSerializer typeSer, JavaType serType, JsonInclude.Value inclusion, Class<?>[] includeInViews) {
      super(propDef, propDef.getPrimaryMember(), contextAnnotations, declaredType, ser, typeSer, serType, _suppressNulls(inclusion), _suppressableValue(inclusion), includeInViews);
   }

   /** @deprecated */
   @Deprecated
   protected VirtualBeanPropertyWriter(BeanPropertyDefinition propDef, Annotations contextAnnotations, JavaType declaredType, JsonSerializer<?> ser, TypeSerializer typeSer, JavaType serType, JsonInclude.Value inclusion) {
      this(propDef, contextAnnotations, declaredType, ser, typeSer, serType, inclusion, (Class[])null);
   }

   protected VirtualBeanPropertyWriter(VirtualBeanPropertyWriter base) {
      super(base);
   }

   protected VirtualBeanPropertyWriter(VirtualBeanPropertyWriter base, PropertyName name) {
      super(base, (PropertyName)name);
   }

   protected static boolean _suppressNulls(JsonInclude.Value inclusion) {
      if (inclusion == null) {
         return false;
      } else {
         JsonInclude.Include incl = inclusion.getValueInclusion();
         return incl != JsonInclude.Include.ALWAYS && incl != JsonInclude.Include.USE_DEFAULTS;
      }
   }

   protected static Object _suppressableValue(JsonInclude.Value inclusion) {
      if (inclusion == null) {
         return false;
      } else {
         JsonInclude.Include incl = inclusion.getValueInclusion();
         return incl != JsonInclude.Include.ALWAYS && incl != JsonInclude.Include.NON_NULL && incl != JsonInclude.Include.USE_DEFAULTS ? MARKER_FOR_EMPTY : null;
      }
   }

   public boolean isVirtual() {
      return true;
   }

   protected abstract Object value(Object var1, JsonGenerator var2, SerializerProvider var3) throws Exception;

   public abstract VirtualBeanPropertyWriter withConfig(MapperConfig<?> var1, AnnotatedClass var2, BeanPropertyDefinition var3, JavaType var4);

   public void serializeAsField(Object bean, JsonGenerator gen, SerializerProvider prov) throws Exception {
      Object value = this.value(bean, gen, prov);
      if (value == null) {
         if (this._nullSerializer != null) {
            gen.writeFieldName((SerializableString)this._name);
            this._nullSerializer.serialize((Object)null, gen, prov);
         }

      } else {
         JsonSerializer<Object> ser = this._serializer;
         if (ser == null) {
            Class<?> cls = value.getClass();
            PropertySerializerMap m = this._dynamicSerializers;
            ser = m.serializerFor(cls);
            if (ser == null) {
               ser = this._findAndAddDynamic(m, cls, prov);
            }
         }

         if (this._suppressableValue != null) {
            if (MARKER_FOR_EMPTY == this._suppressableValue) {
               if (ser.isEmpty(prov, value)) {
                  return;
               }
            } else if (this._suppressableValue.equals(value)) {
               return;
            }
         }

         if (value != bean || !this._handleSelfReference(bean, gen, prov, ser)) {
            gen.writeFieldName((SerializableString)this._name);
            if (this._typeSerializer == null) {
               ser.serialize(value, gen, prov);
            } else {
               ser.serializeWithType(value, gen, prov, this._typeSerializer);
            }

         }
      }
   }

   public void serializeAsElement(Object bean, JsonGenerator gen, SerializerProvider prov) throws Exception {
      Object value = this.value(bean, gen, prov);
      if (value == null) {
         if (this._nullSerializer != null) {
            this._nullSerializer.serialize((Object)null, gen, prov);
         } else {
            gen.writeNull();
         }

      } else {
         JsonSerializer<Object> ser = this._serializer;
         if (ser == null) {
            Class<?> cls = value.getClass();
            PropertySerializerMap map = this._dynamicSerializers;
            ser = map.serializerFor(cls);
            if (ser == null) {
               ser = this._findAndAddDynamic(map, cls, prov);
            }
         }

         if (this._suppressableValue != null) {
            if (MARKER_FOR_EMPTY == this._suppressableValue) {
               if (ser.isEmpty(prov, value)) {
                  this.serializeAsPlaceholder(bean, gen, prov);
                  return;
               }
            } else if (this._suppressableValue.equals(value)) {
               this.serializeAsPlaceholder(bean, gen, prov);
               return;
            }
         }

         if (value != bean || !this._handleSelfReference(bean, gen, prov, ser)) {
            if (this._typeSerializer == null) {
               ser.serialize(value, gen, prov);
            } else {
               ser.serializeWithType(value, gen, prov, this._typeSerializer);
            }

         }
      }
   }
}
