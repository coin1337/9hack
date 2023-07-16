package software.bernie.shadowed.fasterxml.jackson.databind.deser.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import software.bernie.shadowed.fasterxml.jackson.core.JsonParser;
import software.bernie.shadowed.fasterxml.jackson.core.JsonToken;
import software.bernie.shadowed.fasterxml.jackson.databind.DeserializationContext;
import software.bernie.shadowed.fasterxml.jackson.databind.DeserializationFeature;
import software.bernie.shadowed.fasterxml.jackson.databind.JavaType;
import software.bernie.shadowed.fasterxml.jackson.databind.deser.SettableBeanProperty;
import software.bernie.shadowed.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import software.bernie.shadowed.fasterxml.jackson.databind.util.TokenBuffer;

public class ExternalTypeHandler {
   private final JavaType _beanType;
   private final ExternalTypeHandler.ExtTypedProperty[] _properties;
   private final Map<String, Object> _nameToPropertyIndex;
   private final String[] _typeIds;
   private final TokenBuffer[] _tokens;

   protected ExternalTypeHandler(JavaType beanType, ExternalTypeHandler.ExtTypedProperty[] properties, Map<String, Object> nameToPropertyIndex, String[] typeIds, TokenBuffer[] tokens) {
      this._beanType = beanType;
      this._properties = properties;
      this._nameToPropertyIndex = nameToPropertyIndex;
      this._typeIds = typeIds;
      this._tokens = tokens;
   }

   protected ExternalTypeHandler(ExternalTypeHandler h) {
      this._beanType = h._beanType;
      this._properties = h._properties;
      this._nameToPropertyIndex = h._nameToPropertyIndex;
      int len = this._properties.length;
      this._typeIds = new String[len];
      this._tokens = new TokenBuffer[len];
   }

   public static ExternalTypeHandler.Builder builder(JavaType beanType) {
      return new ExternalTypeHandler.Builder(beanType);
   }

   public ExternalTypeHandler start() {
      return new ExternalTypeHandler(this);
   }

   public boolean handleTypePropertyValue(JsonParser p, DeserializationContext ctxt, String propName, Object bean) throws IOException {
      Object ob = this._nameToPropertyIndex.get(propName);
      if (ob == null) {
         return false;
      } else {
         String typeId = p.getText();
         if (ob instanceof List) {
            boolean result = false;
            Iterator i$ = ((List)ob).iterator();

            while(i$.hasNext()) {
               Integer index = (Integer)i$.next();
               if (this._handleTypePropertyValue(p, ctxt, propName, bean, typeId, index)) {
                  result = true;
               }
            }

            return result;
         } else {
            return this._handleTypePropertyValue(p, ctxt, propName, bean, typeId, (Integer)ob);
         }
      }
   }

   private final boolean _handleTypePropertyValue(JsonParser p, DeserializationContext ctxt, String propName, Object bean, String typeId, int index) throws IOException {
      ExternalTypeHandler.ExtTypedProperty prop = this._properties[index];
      if (!prop.hasTypePropertyName(propName)) {
         return false;
      } else {
         boolean canDeserialize = bean != null && this._tokens[index] != null;
         if (canDeserialize) {
            this._deserializeAndSet(p, ctxt, bean, index, typeId);
            this._tokens[index] = null;
         } else {
            this._typeIds[index] = typeId;
         }

         return true;
      }
   }

   public boolean handlePropertyValue(JsonParser p, DeserializationContext ctxt, String propName, Object bean) throws IOException {
      Object ob = this._nameToPropertyIndex.get(propName);
      if (ob == null) {
         return false;
      } else {
         TokenBuffer tokens;
         String typeId;
         if (ob instanceof List) {
            Iterator<Integer> it = ((List)ob).iterator();
            Integer index = (Integer)it.next();
            ExternalTypeHandler.ExtTypedProperty prop = this._properties[index];
            if (prop.hasTypePropertyName(propName)) {
               typeId = p.getText();
               p.skipChildren();

               for(this._typeIds[index] = typeId; it.hasNext(); this._typeIds[(Integer)it.next()] = typeId) {
               }
            } else {
               tokens = new TokenBuffer(p, ctxt);
               tokens.copyCurrentStructure(p);

               for(this._tokens[index] = tokens; it.hasNext(); this._tokens[(Integer)it.next()] = tokens) {
               }
            }

            return true;
         } else {
            int index = (Integer)ob;
            ExternalTypeHandler.ExtTypedProperty prop = this._properties[index];
            boolean canDeserialize;
            if (prop.hasTypePropertyName(propName)) {
               this._typeIds[index] = p.getText();
               p.skipChildren();
               canDeserialize = bean != null && this._tokens[index] != null;
            } else {
               tokens = new TokenBuffer(p, ctxt);
               tokens.copyCurrentStructure(p);
               this._tokens[index] = tokens;
               canDeserialize = bean != null && this._typeIds[index] != null;
            }

            if (canDeserialize) {
               typeId = this._typeIds[index];
               this._typeIds[index] = null;
               this._deserializeAndSet(p, ctxt, bean, index, typeId);
               this._tokens[index] = null;
            }

            return true;
         }
      }
   }

   public Object complete(JsonParser p, DeserializationContext ctxt, Object bean) throws IOException {
      int i = 0;

      for(int len = this._properties.length; i < len; ++i) {
         String typeId = this._typeIds[i];
         if (typeId == null) {
            TokenBuffer tokens = this._tokens[i];
            if (tokens == null) {
               continue;
            }

            JsonToken t = tokens.firstToken();
            if (t.isScalarValue()) {
               JsonParser buffered = tokens.asParser(p);
               buffered.nextToken();
               SettableBeanProperty extProp = this._properties[i].getProperty();
               Object result = TypeDeserializer.deserializeIfNatural(buffered, ctxt, extProp.getType());
               if (result != null) {
                  extProp.set(bean, result);
                  continue;
               }

               if (!this._properties[i].hasDefaultType()) {
                  ctxt.reportInputMismatch(bean.getClass(), "Missing external type id property '%s'", this._properties[i].getTypePropertyName());
               } else {
                  typeId = this._properties[i].getDefaultTypeId();
               }
            }
         } else if (this._tokens[i] == null) {
            SettableBeanProperty prop = this._properties[i].getProperty();
            if (prop.isRequired() || ctxt.isEnabled(DeserializationFeature.FAIL_ON_MISSING_EXTERNAL_TYPE_ID_PROPERTY)) {
               ctxt.reportInputMismatch(bean.getClass(), "Missing property '%s' for external type id '%s'", prop.getName(), this._properties[i].getTypePropertyName());
            }

            return bean;
         }

         this._deserializeAndSet(p, ctxt, bean, i, typeId);
      }

      return bean;
   }

   public Object complete(JsonParser p, DeserializationContext ctxt, PropertyValueBuffer buffer, PropertyBasedCreator creator) throws IOException {
      int len = this._properties.length;
      Object[] values = new Object[len];

      for(int i = 0; i < len; ++i) {
         String typeId = this._typeIds[i];
         ExternalTypeHandler.ExtTypedProperty extProp = this._properties[i];
         SettableBeanProperty prop;
         if (typeId == null) {
            if (this._tokens[i] == null) {
               continue;
            }

            if (!extProp.hasDefaultType()) {
               ctxt.reportInputMismatch(this._beanType, "Missing external type id property '%s'", extProp.getTypePropertyName());
            } else {
               typeId = extProp.getDefaultTypeId();
            }
         } else if (this._tokens[i] == null) {
            prop = extProp.getProperty();
            ctxt.reportInputMismatch(this._beanType, "Missing property '%s' for external type id '%s'", prop.getName(), this._properties[i].getTypePropertyName());
         }

         values[i] = this._deserialize(p, ctxt, i, typeId);
         prop = extProp.getProperty();
         if (prop.getCreatorIndex() >= 0) {
            buffer.assignParameter(prop, values[i]);
            SettableBeanProperty typeProp = extProp.getTypeProperty();
            if (typeProp != null && typeProp.getCreatorIndex() >= 0) {
               buffer.assignParameter(typeProp, typeId);
            }
         }
      }

      Object bean = creator.build(ctxt, buffer);

      for(int i = 0; i < len; ++i) {
         SettableBeanProperty prop = this._properties[i].getProperty();
         if (prop.getCreatorIndex() < 0) {
            prop.set(bean, values[i]);
         }
      }

      return bean;
   }

   protected final Object _deserialize(JsonParser p, DeserializationContext ctxt, int index, String typeId) throws IOException {
      JsonParser p2 = this._tokens[index].asParser(p);
      JsonToken t = p2.nextToken();
      if (t == JsonToken.VALUE_NULL) {
         return null;
      } else {
         TokenBuffer merged = new TokenBuffer(p, ctxt);
         merged.writeStartArray();
         merged.writeString(typeId);
         merged.copyCurrentStructure(p2);
         merged.writeEndArray();
         JsonParser mp = merged.asParser(p);
         mp.nextToken();
         return this._properties[index].getProperty().deserialize(mp, ctxt);
      }
   }

   protected final void _deserializeAndSet(JsonParser p, DeserializationContext ctxt, Object bean, int index, String typeId) throws IOException {
      JsonParser p2 = this._tokens[index].asParser(p);
      JsonToken t = p2.nextToken();
      if (t == JsonToken.VALUE_NULL) {
         this._properties[index].getProperty().set(bean, (Object)null);
      } else {
         TokenBuffer merged = new TokenBuffer(p, ctxt);
         merged.writeStartArray();
         merged.writeString(typeId);
         merged.copyCurrentStructure(p2);
         merged.writeEndArray();
         JsonParser mp = merged.asParser(p);
         mp.nextToken();
         this._properties[index].getProperty().deserializeAndSet(mp, ctxt, bean);
      }
   }

   private static final class ExtTypedProperty {
      private final SettableBeanProperty _property;
      private final TypeDeserializer _typeDeserializer;
      private final String _typePropertyName;
      private SettableBeanProperty _typeProperty;

      public ExtTypedProperty(SettableBeanProperty property, TypeDeserializer typeDeser) {
         this._property = property;
         this._typeDeserializer = typeDeser;
         this._typePropertyName = typeDeser.getPropertyName();
      }

      public void linkTypeProperty(SettableBeanProperty p) {
         this._typeProperty = p;
      }

      public boolean hasTypePropertyName(String n) {
         return n.equals(this._typePropertyName);
      }

      public boolean hasDefaultType() {
         return this._typeDeserializer.getDefaultImpl() != null;
      }

      public String getDefaultTypeId() {
         Class<?> defaultType = this._typeDeserializer.getDefaultImpl();
         return defaultType == null ? null : this._typeDeserializer.getTypeIdResolver().idFromValueAndType((Object)null, defaultType);
      }

      public String getTypePropertyName() {
         return this._typePropertyName;
      }

      public SettableBeanProperty getProperty() {
         return this._property;
      }

      public SettableBeanProperty getTypeProperty() {
         return this._typeProperty;
      }
   }

   public static class Builder {
      private final JavaType _beanType;
      private final List<ExternalTypeHandler.ExtTypedProperty> _properties = new ArrayList();
      private final Map<String, Object> _nameToPropertyIndex = new HashMap();

      protected Builder(JavaType t) {
         this._beanType = t;
      }

      public void addExternal(SettableBeanProperty property, TypeDeserializer typeDeser) {
         Integer index = this._properties.size();
         this._properties.add(new ExternalTypeHandler.ExtTypedProperty(property, typeDeser));
         this._addPropertyIndex(property.getName(), index);
         this._addPropertyIndex(typeDeser.getPropertyName(), index);
      }

      private void _addPropertyIndex(String name, Integer index) {
         Object ob = this._nameToPropertyIndex.get(name);
         if (ob == null) {
            this._nameToPropertyIndex.put(name, index);
         } else if (ob instanceof List) {
            List<Object> list = (List)ob;
            list.add(index);
         } else {
            List<Object> list = new LinkedList();
            list.add(ob);
            list.add(index);
            this._nameToPropertyIndex.put(name, list);
         }

      }

      public ExternalTypeHandler build(BeanPropertyMap otherProps) {
         int len = this._properties.size();
         ExternalTypeHandler.ExtTypedProperty[] extProps = new ExternalTypeHandler.ExtTypedProperty[len];

         for(int i = 0; i < len; ++i) {
            ExternalTypeHandler.ExtTypedProperty extProp = (ExternalTypeHandler.ExtTypedProperty)this._properties.get(i);
            String typePropId = extProp.getTypePropertyName();
            SettableBeanProperty typeProp = otherProps.find(typePropId);
            if (typeProp != null) {
               extProp.linkTypeProperty(typeProp);
            }

            extProps[i] = extProp;
         }

         return new ExternalTypeHandler(this._beanType, extProps, this._nameToPropertyIndex, (String[])null, (TokenBuffer[])null);
      }
   }
}
