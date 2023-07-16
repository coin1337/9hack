package software.bernie.shadowed.fasterxml.jackson.databind.ser.std;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import software.bernie.shadowed.fasterxml.jackson.annotation.JsonFormat;
import software.bernie.shadowed.fasterxml.jackson.annotation.JsonIgnoreProperties;
import software.bernie.shadowed.fasterxml.jackson.annotation.ObjectIdGenerator;
import software.bernie.shadowed.fasterxml.jackson.annotation.ObjectIdGenerators;
import software.bernie.shadowed.fasterxml.jackson.core.JsonGenerationException;
import software.bernie.shadowed.fasterxml.jackson.core.JsonGenerator;
import software.bernie.shadowed.fasterxml.jackson.core.JsonToken;
import software.bernie.shadowed.fasterxml.jackson.core.type.WritableTypeId;
import software.bernie.shadowed.fasterxml.jackson.databind.AnnotationIntrospector;
import software.bernie.shadowed.fasterxml.jackson.databind.BeanDescription;
import software.bernie.shadowed.fasterxml.jackson.databind.BeanProperty;
import software.bernie.shadowed.fasterxml.jackson.databind.JavaType;
import software.bernie.shadowed.fasterxml.jackson.databind.JsonMappingException;
import software.bernie.shadowed.fasterxml.jackson.databind.JsonNode;
import software.bernie.shadowed.fasterxml.jackson.databind.JsonSerializer;
import software.bernie.shadowed.fasterxml.jackson.databind.PropertyName;
import software.bernie.shadowed.fasterxml.jackson.databind.SerializationConfig;
import software.bernie.shadowed.fasterxml.jackson.databind.SerializerProvider;
import software.bernie.shadowed.fasterxml.jackson.databind.introspect.AnnotatedMember;
import software.bernie.shadowed.fasterxml.jackson.databind.introspect.ObjectIdInfo;
import software.bernie.shadowed.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitable;
import software.bernie.shadowed.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper;
import software.bernie.shadowed.fasterxml.jackson.databind.jsonFormatVisitors.JsonObjectFormatVisitor;
import software.bernie.shadowed.fasterxml.jackson.databind.jsonschema.JsonSerializableSchema;
import software.bernie.shadowed.fasterxml.jackson.databind.jsonschema.SchemaAware;
import software.bernie.shadowed.fasterxml.jackson.databind.jsontype.TypeSerializer;
import software.bernie.shadowed.fasterxml.jackson.databind.node.ObjectNode;
import software.bernie.shadowed.fasterxml.jackson.databind.ser.AnyGetterWriter;
import software.bernie.shadowed.fasterxml.jackson.databind.ser.BeanPropertyWriter;
import software.bernie.shadowed.fasterxml.jackson.databind.ser.BeanSerializerBuilder;
import software.bernie.shadowed.fasterxml.jackson.databind.ser.ContainerSerializer;
import software.bernie.shadowed.fasterxml.jackson.databind.ser.ContextualSerializer;
import software.bernie.shadowed.fasterxml.jackson.databind.ser.PropertyFilter;
import software.bernie.shadowed.fasterxml.jackson.databind.ser.PropertyWriter;
import software.bernie.shadowed.fasterxml.jackson.databind.ser.ResolvableSerializer;
import software.bernie.shadowed.fasterxml.jackson.databind.ser.impl.MapEntrySerializer;
import software.bernie.shadowed.fasterxml.jackson.databind.ser.impl.ObjectIdWriter;
import software.bernie.shadowed.fasterxml.jackson.databind.ser.impl.PropertyBasedObjectIdGenerator;
import software.bernie.shadowed.fasterxml.jackson.databind.ser.impl.WritableObjectId;
import software.bernie.shadowed.fasterxml.jackson.databind.util.ArrayBuilders;
import software.bernie.shadowed.fasterxml.jackson.databind.util.Converter;
import software.bernie.shadowed.fasterxml.jackson.databind.util.NameTransformer;

public abstract class BeanSerializerBase extends StdSerializer<Object> implements ContextualSerializer, ResolvableSerializer, JsonFormatVisitable, SchemaAware {
   protected static final PropertyName NAME_FOR_OBJECT_REF = new PropertyName("#object-ref");
   protected static final BeanPropertyWriter[] NO_PROPS = new BeanPropertyWriter[0];
   protected final JavaType _beanType;
   protected final BeanPropertyWriter[] _props;
   protected final BeanPropertyWriter[] _filteredProps;
   protected final AnyGetterWriter _anyGetterWriter;
   protected final Object _propertyFilterId;
   protected final AnnotatedMember _typeId;
   protected final ObjectIdWriter _objectIdWriter;
   protected final JsonFormat.Shape _serializationShape;

   protected BeanSerializerBase(JavaType type, BeanSerializerBuilder builder, BeanPropertyWriter[] properties, BeanPropertyWriter[] filteredProperties) {
      super(type);
      this._beanType = type;
      this._props = properties;
      this._filteredProps = filteredProperties;
      if (builder == null) {
         this._typeId = null;
         this._anyGetterWriter = null;
         this._propertyFilterId = null;
         this._objectIdWriter = null;
         this._serializationShape = null;
      } else {
         this._typeId = builder.getTypeId();
         this._anyGetterWriter = builder.getAnyGetter();
         this._propertyFilterId = builder.getFilterId();
         this._objectIdWriter = builder.getObjectIdWriter();
         JsonFormat.Value format = builder.getBeanDescription().findExpectedFormat((JsonFormat.Value)null);
         this._serializationShape = format == null ? null : format.getShape();
      }

   }

   public BeanSerializerBase(BeanSerializerBase src, BeanPropertyWriter[] properties, BeanPropertyWriter[] filteredProperties) {
      super(src._handledType);
      this._beanType = src._beanType;
      this._props = properties;
      this._filteredProps = filteredProperties;
      this._typeId = src._typeId;
      this._anyGetterWriter = src._anyGetterWriter;
      this._objectIdWriter = src._objectIdWriter;
      this._propertyFilterId = src._propertyFilterId;
      this._serializationShape = src._serializationShape;
   }

   protected BeanSerializerBase(BeanSerializerBase src, ObjectIdWriter objectIdWriter) {
      this(src, objectIdWriter, src._propertyFilterId);
   }

   protected BeanSerializerBase(BeanSerializerBase src, ObjectIdWriter objectIdWriter, Object filterId) {
      super(src._handledType);
      this._beanType = src._beanType;
      this._props = src._props;
      this._filteredProps = src._filteredProps;
      this._typeId = src._typeId;
      this._anyGetterWriter = src._anyGetterWriter;
      this._objectIdWriter = objectIdWriter;
      this._propertyFilterId = filterId;
      this._serializationShape = src._serializationShape;
   }

   /** @deprecated */
   @Deprecated
   protected BeanSerializerBase(BeanSerializerBase src, String[] toIgnore) {
      this(src, (Set)ArrayBuilders.arrayToSet(toIgnore));
   }

   protected BeanSerializerBase(BeanSerializerBase src, Set<String> toIgnore) {
      super(src._handledType);
      this._beanType = src._beanType;
      BeanPropertyWriter[] propsIn = src._props;
      BeanPropertyWriter[] fpropsIn = src._filteredProps;
      int len = propsIn.length;
      ArrayList<BeanPropertyWriter> propsOut = new ArrayList(len);
      ArrayList<BeanPropertyWriter> fpropsOut = fpropsIn == null ? null : new ArrayList(len);

      for(int i = 0; i < len; ++i) {
         BeanPropertyWriter bpw = propsIn[i];
         if (toIgnore == null || !toIgnore.contains(bpw.getName())) {
            propsOut.add(bpw);
            if (fpropsIn != null) {
               fpropsOut.add(fpropsIn[i]);
            }
         }
      }

      this._props = (BeanPropertyWriter[])propsOut.toArray(new BeanPropertyWriter[propsOut.size()]);
      this._filteredProps = fpropsOut == null ? null : (BeanPropertyWriter[])fpropsOut.toArray(new BeanPropertyWriter[fpropsOut.size()]);
      this._typeId = src._typeId;
      this._anyGetterWriter = src._anyGetterWriter;
      this._objectIdWriter = src._objectIdWriter;
      this._propertyFilterId = src._propertyFilterId;
      this._serializationShape = src._serializationShape;
   }

   public abstract BeanSerializerBase withObjectIdWriter(ObjectIdWriter var1);

   protected abstract BeanSerializerBase withIgnorals(Set<String> var1);

   /** @deprecated */
   @Deprecated
   protected BeanSerializerBase withIgnorals(String[] toIgnore) {
      return this.withIgnorals((Set)ArrayBuilders.arrayToSet(toIgnore));
   }

   protected abstract BeanSerializerBase asArraySerializer();

   public abstract BeanSerializerBase withFilterId(Object var1);

   protected BeanSerializerBase(BeanSerializerBase src) {
      this(src, src._props, src._filteredProps);
   }

   protected BeanSerializerBase(BeanSerializerBase src, NameTransformer unwrapper) {
      this(src, rename(src._props, unwrapper), rename(src._filteredProps, unwrapper));
   }

   private static final BeanPropertyWriter[] rename(BeanPropertyWriter[] props, NameTransformer transformer) {
      if (props != null && props.length != 0 && transformer != null && transformer != NameTransformer.NOP) {
         int len = props.length;
         BeanPropertyWriter[] result = new BeanPropertyWriter[len];

         for(int i = 0; i < len; ++i) {
            BeanPropertyWriter bpw = props[i];
            if (bpw != null) {
               result[i] = bpw.rename(transformer);
            }
         }

         return result;
      } else {
         return props;
      }
   }

   public void resolve(SerializerProvider provider) throws JsonMappingException {
      int filteredCount = this._filteredProps == null ? 0 : this._filteredProps.length;
      int i = 0;

      for(int len = this._props.length; i < len; ++i) {
         BeanPropertyWriter prop = this._props[i];
         BeanPropertyWriter w2;
         if (!prop.willSuppressNulls() && !prop.hasNullSerializer()) {
            JsonSerializer<Object> nullSer = provider.findNullValueSerializer(prop);
            if (nullSer != null) {
               prop.assignNullSerializer(nullSer);
               if (i < filteredCount) {
                  w2 = this._filteredProps[i];
                  if (w2 != null) {
                     w2.assignNullSerializer(nullSer);
                  }
               }
            }
         }

         if (!prop.hasSerializer()) {
            JsonSerializer<Object> ser = this.findConvertingSerializer(provider, prop);
            if (ser == null) {
               JavaType type = prop.getSerializationType();
               if (type == null) {
                  type = prop.getType();
                  if (!type.isFinal()) {
                     if (type.isContainerType() || type.containedTypeCount() > 0) {
                        prop.setNonTrivialBaseType(type);
                     }
                     continue;
                  }
               }

               ser = provider.findValueSerializer((JavaType)type, prop);
               if (type.isContainerType()) {
                  TypeSerializer typeSer = (TypeSerializer)type.getContentType().getTypeHandler();
                  if (typeSer != null && ser instanceof ContainerSerializer) {
                     JsonSerializer<Object> ser2 = ((ContainerSerializer)ser).withValueTypeSerializer(typeSer);
                     ser = ser2;
                  }
               }
            }

            if (i < filteredCount) {
               w2 = this._filteredProps[i];
               if (w2 != null) {
                  w2.assignSerializer((JsonSerializer)ser);
                  continue;
               }
            }

            prop.assignSerializer((JsonSerializer)ser);
         }
      }

      if (this._anyGetterWriter != null) {
         this._anyGetterWriter.resolve(provider);
      }

   }

   protected JsonSerializer<Object> findConvertingSerializer(SerializerProvider provider, BeanPropertyWriter prop) throws JsonMappingException {
      AnnotationIntrospector intr = provider.getAnnotationIntrospector();
      if (intr != null) {
         AnnotatedMember m = prop.getMember();
         if (m != null) {
            Object convDef = intr.findSerializationConverter(m);
            if (convDef != null) {
               Converter<Object, Object> conv = provider.converterInstance(prop.getMember(), convDef);
               JavaType delegateType = conv.getOutputType(provider.getTypeFactory());
               JsonSerializer<?> ser = delegateType.isJavaLangObject() ? null : provider.findValueSerializer((JavaType)delegateType, prop);
               return new StdDelegatingSerializer(conv, delegateType, ser);
            }
         }
      }

      return null;
   }

   public JsonSerializer<?> createContextual(SerializerProvider provider, BeanProperty property) throws JsonMappingException {
      AnnotationIntrospector intr = provider.getAnnotationIntrospector();
      AnnotatedMember accessor = property != null && intr != null ? property.getMember() : null;
      SerializationConfig config = provider.getConfig();
      JsonFormat.Value format = this.findFormatOverrides(provider, property, this.handledType());
      JsonFormat.Shape shape = null;
      if (format != null && format.hasShape()) {
         shape = format.getShape();
         if (shape != JsonFormat.Shape.ANY && shape != this._serializationShape) {
            if (this._handledType.isEnum()) {
               switch(shape) {
               case STRING:
               case NUMBER:
               case NUMBER_INT:
                  BeanDescription desc = config.introspectClassAnnotations(this._beanType);
                  JsonSerializer<?> ser = EnumSerializer.construct(this._beanType.getRawClass(), provider.getConfig(), desc, format);
                  return provider.handlePrimaryContextualization(ser, property);
               }
            } else if (shape == JsonFormat.Shape.NATURAL && (!this._beanType.isMapLikeType() || !Map.class.isAssignableFrom(this._handledType)) && Entry.class.isAssignableFrom(this._handledType)) {
               JavaType mapEntryType = this._beanType.findSuperType(Entry.class);
               JavaType kt = mapEntryType.containedTypeOrUnknown(0);
               JavaType vt = mapEntryType.containedTypeOrUnknown(1);
               JsonSerializer<?> ser = new MapEntrySerializer(this._beanType, kt, vt, false, (TypeSerializer)null, property);
               return provider.handlePrimaryContextualization(ser, property);
            }
         }
      }

      ObjectIdWriter oiw = this._objectIdWriter;
      Set<String> ignoredProps = null;
      Object newFilterId = null;
      if (accessor != null) {
         JsonIgnoreProperties.Value ignorals = intr.findPropertyIgnorals(accessor);
         if (ignorals != null) {
            ignoredProps = ignorals.findIgnoredForSerialization();
         }

         ObjectIdInfo objectIdInfo = intr.findObjectIdInfo(accessor);
         if (objectIdInfo == null) {
            if (oiw != null) {
               objectIdInfo = intr.findObjectReferenceInfo(accessor, (ObjectIdInfo)null);
               if (objectIdInfo != null) {
                  oiw = this._objectIdWriter.withAlwaysAsId(objectIdInfo.getAlwaysAsId());
               }
            }
         } else {
            objectIdInfo = intr.findObjectReferenceInfo(accessor, objectIdInfo);
            Class<?> implClass = objectIdInfo.getGeneratorType();
            JavaType type = provider.constructType(implClass);
            JavaType idType = provider.getTypeFactory().findTypeParameters(type, ObjectIdGenerator.class)[0];
            if (implClass == ObjectIdGenerators.PropertyGenerator.class) {
               String propName = objectIdInfo.getPropertyName().getSimpleName();
               BeanPropertyWriter idProp = null;
               int i = 0;
               int len = this._props.length;

               while(true) {
                  if (i == len) {
                     provider.reportBadDefinition(this._beanType, String.format("Invalid Object Id definition for %s: cannot find property with name '%s'", this.handledType().getName(), propName));
                  }

                  BeanPropertyWriter prop = this._props[i];
                  if (propName.equals(prop.getName())) {
                     if (i > 0) {
                        System.arraycopy(this._props, 0, this._props, 1, i);
                        this._props[0] = prop;
                        if (this._filteredProps != null) {
                           BeanPropertyWriter fp = this._filteredProps[i];
                           System.arraycopy(this._filteredProps, 0, this._filteredProps, 1, i);
                           this._filteredProps[0] = fp;
                        }
                     }

                     idType = prop.getType();
                     ObjectIdGenerator<?> gen = new PropertyBasedObjectIdGenerator(objectIdInfo, prop);
                     oiw = ObjectIdWriter.construct(idType, (PropertyName)null, gen, objectIdInfo.getAlwaysAsId());
                     break;
                  }

                  ++i;
               }
            } else {
               ObjectIdGenerator<?> gen = provider.objectIdGeneratorInstance(accessor, objectIdInfo);
               oiw = ObjectIdWriter.construct(idType, objectIdInfo.getPropertyName(), gen, objectIdInfo.getAlwaysAsId());
            }
         }

         Object filterId = intr.findFilterId(accessor);
         if (filterId != null && (this._propertyFilterId == null || !filterId.equals(this._propertyFilterId))) {
            newFilterId = filterId;
         }
      }

      BeanSerializerBase contextual = this;
      if (oiw != null) {
         JsonSerializer<?> ser = provider.findValueSerializer(oiw.idType, property);
         oiw = oiw.withSerializer(ser);
         if (oiw != this._objectIdWriter) {
            contextual = this.withObjectIdWriter(oiw);
         }
      }

      if (ignoredProps != null && !ignoredProps.isEmpty()) {
         contextual = contextual.withIgnorals(ignoredProps);
      }

      if (newFilterId != null) {
         contextual = contextual.withFilterId(newFilterId);
      }

      if (shape == null) {
         shape = this._serializationShape;
      }

      return shape == JsonFormat.Shape.ARRAY ? contextual.asArraySerializer() : contextual;
   }

   public Iterator<PropertyWriter> properties() {
      return Arrays.asList(this._props).iterator();
   }

   public boolean usesObjectId() {
      return this._objectIdWriter != null;
   }

   public abstract void serialize(Object var1, JsonGenerator var2, SerializerProvider var3) throws IOException;

   public void serializeWithType(Object bean, JsonGenerator gen, SerializerProvider provider, TypeSerializer typeSer) throws IOException {
      if (this._objectIdWriter != null) {
         gen.setCurrentValue(bean);
         this._serializeWithObjectId(bean, gen, provider, typeSer);
      } else {
         gen.setCurrentValue(bean);
         WritableTypeId typeIdDef = this._typeIdDef(typeSer, bean, JsonToken.START_OBJECT);
         typeSer.writeTypePrefix(gen, typeIdDef);
         if (this._propertyFilterId != null) {
            this.serializeFieldsFiltered(bean, gen, provider);
         } else {
            this.serializeFields(bean, gen, provider);
         }

         typeSer.writeTypeSuffix(gen, typeIdDef);
      }
   }

   protected final void _serializeWithObjectId(Object bean, JsonGenerator gen, SerializerProvider provider, boolean startEndObject) throws IOException {
      ObjectIdWriter w = this._objectIdWriter;
      WritableObjectId objectId = provider.findObjectId(bean, w.generator);
      if (!objectId.writeAsId(gen, provider, w)) {
         Object id = objectId.generateId(bean);
         if (w.alwaysAsId) {
            w.serializer.serialize(id, gen, provider);
         } else {
            if (startEndObject) {
               gen.writeStartObject(bean);
            }

            objectId.writeAsField(gen, provider, w);
            if (this._propertyFilterId != null) {
               this.serializeFieldsFiltered(bean, gen, provider);
            } else {
               this.serializeFields(bean, gen, provider);
            }

            if (startEndObject) {
               gen.writeEndObject();
            }

         }
      }
   }

   protected final void _serializeWithObjectId(Object bean, JsonGenerator gen, SerializerProvider provider, TypeSerializer typeSer) throws IOException {
      ObjectIdWriter w = this._objectIdWriter;
      WritableObjectId objectId = provider.findObjectId(bean, w.generator);
      if (!objectId.writeAsId(gen, provider, w)) {
         Object id = objectId.generateId(bean);
         if (w.alwaysAsId) {
            w.serializer.serialize(id, gen, provider);
         } else {
            this._serializeObjectId(bean, gen, provider, typeSer, objectId);
         }
      }
   }

   protected void _serializeObjectId(Object bean, JsonGenerator g, SerializerProvider provider, TypeSerializer typeSer, WritableObjectId objectId) throws IOException {
      ObjectIdWriter w = this._objectIdWriter;
      WritableTypeId typeIdDef = this._typeIdDef(typeSer, bean, JsonToken.START_OBJECT);
      typeSer.writeTypePrefix(g, typeIdDef);
      objectId.writeAsField(g, provider, w);
      if (this._propertyFilterId != null) {
         this.serializeFieldsFiltered(bean, g, provider);
      } else {
         this.serializeFields(bean, g, provider);
      }

      typeSer.writeTypeSuffix(g, typeIdDef);
   }

   protected final WritableTypeId _typeIdDef(TypeSerializer typeSer, Object bean, JsonToken valueShape) {
      if (this._typeId == null) {
         return typeSer.typeId(bean, valueShape);
      } else {
         Object typeId = this._typeId.getValue(bean);
         if (typeId == null) {
            typeId = "";
         }

         return typeSer.typeId(bean, valueShape, typeId);
      }
   }

   /** @deprecated */
   @Deprecated
   protected final String _customTypeId(Object bean) {
      Object typeId = this._typeId.getValue(bean);
      if (typeId == null) {
         return "";
      } else {
         return typeId instanceof String ? (String)typeId : typeId.toString();
      }
   }

   protected void serializeFields(Object bean, JsonGenerator gen, SerializerProvider provider) throws IOException {
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
            if (prop != null) {
               prop.serializeAsField(bean, gen, provider);
            }
         }

         if (this._anyGetterWriter != null) {
            this._anyGetterWriter.getAndSerialize(bean, gen, provider);
         }
      } catch (Exception var9) {
         String name = i == props.length ? "[anySetter]" : props[i].getName();
         this.wrapAndThrow(provider, var9, bean, name);
      } catch (StackOverflowError var10) {
         JsonMappingException mapE = new JsonMappingException(gen, "Infinite recursion (StackOverflowError)", var10);
         String name = i == props.length ? "[anySetter]" : props[i].getName();
         mapE.prependPath(new JsonMappingException.Reference(bean, name));
         throw mapE;
      }

   }

   protected void serializeFieldsFiltered(Object bean, JsonGenerator gen, SerializerProvider provider) throws IOException, JsonGenerationException {
      BeanPropertyWriter[] props;
      if (this._filteredProps != null && provider.getActiveView() != null) {
         props = this._filteredProps;
      } else {
         props = this._props;
      }

      PropertyFilter filter = this.findPropertyFilter(provider, this._propertyFilterId, bean);
      if (filter == null) {
         this.serializeFields(bean, gen, provider);
      } else {
         int i = 0;

         try {
            for(int len = props.length; i < len; ++i) {
               BeanPropertyWriter prop = props[i];
               if (prop != null) {
                  filter.serializeAsField(bean, gen, provider, prop);
               }
            }

            if (this._anyGetterWriter != null) {
               this._anyGetterWriter.getAndFilter(bean, gen, provider, filter);
            }
         } catch (Exception var10) {
            String name = i == props.length ? "[anySetter]" : props[i].getName();
            this.wrapAndThrow(provider, var10, bean, name);
         } catch (StackOverflowError var11) {
            JsonMappingException mapE = new JsonMappingException(gen, "Infinite recursion (StackOverflowError)", var11);
            String name = i == props.length ? "[anySetter]" : props[i].getName();
            mapE.prependPath(new JsonMappingException.Reference(bean, name));
            throw mapE;
         }

      }
   }

   /** @deprecated */
   @Deprecated
   public JsonNode getSchema(SerializerProvider provider, Type typeHint) throws JsonMappingException {
      ObjectNode o = this.createSchemaNode("object", true);
      JsonSerializableSchema ann = (JsonSerializableSchema)this._handledType.getAnnotation(JsonSerializableSchema.class);
      if (ann != null) {
         String id = ann.id();
         if (id != null && id.length() > 0) {
            o.put("id", id);
         }
      }

      ObjectNode propertiesNode = o.objectNode();
      PropertyFilter filter;
      if (this._propertyFilterId != null) {
         filter = this.findPropertyFilter(provider, this._propertyFilterId, (Object)null);
      } else {
         filter = null;
      }

      for(int i = 0; i < this._props.length; ++i) {
         BeanPropertyWriter prop = this._props[i];
         if (filter == null) {
            prop.depositSchemaProperty(propertiesNode, provider);
         } else {
            filter.depositSchemaProperty(prop, (ObjectNode)propertiesNode, provider);
         }
      }

      o.set("properties", propertiesNode);
      return o;
   }

   public void acceptJsonFormatVisitor(JsonFormatVisitorWrapper visitor, JavaType typeHint) throws JsonMappingException {
      if (visitor != null) {
         JsonObjectFormatVisitor objectVisitor = visitor.expectObjectFormat(typeHint);
         if (objectVisitor != null) {
            SerializerProvider provider = visitor.getProvider();
            int i;
            if (this._propertyFilterId != null) {
               PropertyFilter filter = this.findPropertyFilter(visitor.getProvider(), this._propertyFilterId, (Object)null);
               int i = 0;

               for(i = this._props.length; i < i; ++i) {
                  filter.depositSchemaProperty(this._props[i], (JsonObjectFormatVisitor)objectVisitor, provider);
               }
            } else {
               Class<?> view = this._filteredProps != null && provider != null ? provider.getActiveView() : null;
               BeanPropertyWriter[] props;
               if (view != null) {
                  props = this._filteredProps;
               } else {
                  props = this._props;
               }

               i = 0;

               for(int end = props.length; i < end; ++i) {
                  BeanPropertyWriter prop = props[i];
                  if (prop != null) {
                     prop.depositSchemaProperty(objectVisitor, provider);
                  }
               }
            }

         }
      }
   }
}
