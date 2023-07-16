package software.bernie.shadowed.fasterxml.jackson.databind.deser.impl;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import software.bernie.shadowed.fasterxml.jackson.core.JsonParser;
import software.bernie.shadowed.fasterxml.jackson.databind.DeserializationConfig;
import software.bernie.shadowed.fasterxml.jackson.databind.DeserializationContext;
import software.bernie.shadowed.fasterxml.jackson.databind.JsonMappingException;
import software.bernie.shadowed.fasterxml.jackson.databind.MapperFeature;
import software.bernie.shadowed.fasterxml.jackson.databind.PropertyName;
import software.bernie.shadowed.fasterxml.jackson.databind.deser.SettableBeanProperty;
import software.bernie.shadowed.fasterxml.jackson.databind.deser.ValueInstantiator;

public final class PropertyBasedCreator {
   protected final int _propertyCount;
   protected final ValueInstantiator _valueInstantiator;
   protected final HashMap<String, SettableBeanProperty> _propertyLookup;
   protected final SettableBeanProperty[] _allProperties;

   protected PropertyBasedCreator(DeserializationContext ctxt, ValueInstantiator valueInstantiator, SettableBeanProperty[] creatorProps, boolean caseInsensitive, boolean addAliases) {
      this._valueInstantiator = valueInstantiator;
      if (caseInsensitive) {
         this._propertyLookup = new PropertyBasedCreator.CaseInsensitiveMap();
      } else {
         this._propertyLookup = new HashMap();
      }

      int len = creatorProps.length;
      this._propertyCount = len;
      this._allProperties = new SettableBeanProperty[len];
      if (addAliases) {
         DeserializationConfig config = ctxt.getConfig();
         SettableBeanProperty[] arr$ = creatorProps;
         int len$ = creatorProps.length;

         for(int i$ = 0; i$ < len$; ++i$) {
            SettableBeanProperty prop = arr$[i$];
            List<PropertyName> aliases = prop.findAliases(config);
            if (!aliases.isEmpty()) {
               Iterator i$ = aliases.iterator();

               while(i$.hasNext()) {
                  PropertyName pn = (PropertyName)i$.next();
                  this._propertyLookup.put(pn.getSimpleName(), prop);
               }
            }
         }
      }

      for(int i = 0; i < len; ++i) {
         SettableBeanProperty prop = creatorProps[i];
         this._allProperties[i] = prop;
         this._propertyLookup.put(prop.getName(), prop);
      }

   }

   public static PropertyBasedCreator construct(DeserializationContext ctxt, ValueInstantiator valueInstantiator, SettableBeanProperty[] srcCreatorProps, BeanPropertyMap allProperties) throws JsonMappingException {
      int len = srcCreatorProps.length;
      SettableBeanProperty[] creatorProps = new SettableBeanProperty[len];

      for(int i = 0; i < len; ++i) {
         SettableBeanProperty prop = srcCreatorProps[i];
         if (!prop.hasValueDeserializer()) {
            prop = prop.withValueDeserializer(ctxt.findContextualValueDeserializer(prop.getType(), prop));
         }

         creatorProps[i] = prop;
      }

      return new PropertyBasedCreator(ctxt, valueInstantiator, creatorProps, allProperties.isCaseInsensitive(), allProperties.hasAliases());
   }

   public static PropertyBasedCreator construct(DeserializationContext ctxt, ValueInstantiator valueInstantiator, SettableBeanProperty[] srcCreatorProps, boolean caseInsensitive) throws JsonMappingException {
      int len = srcCreatorProps.length;
      SettableBeanProperty[] creatorProps = new SettableBeanProperty[len];

      for(int i = 0; i < len; ++i) {
         SettableBeanProperty prop = srcCreatorProps[i];
         if (!prop.hasValueDeserializer()) {
            prop = prop.withValueDeserializer(ctxt.findContextualValueDeserializer(prop.getType(), prop));
         }

         creatorProps[i] = prop;
      }

      return new PropertyBasedCreator(ctxt, valueInstantiator, creatorProps, caseInsensitive, false);
   }

   /** @deprecated */
   @Deprecated
   public static PropertyBasedCreator construct(DeserializationContext ctxt, ValueInstantiator valueInstantiator, SettableBeanProperty[] srcCreatorProps) throws JsonMappingException {
      return construct(ctxt, valueInstantiator, srcCreatorProps, ctxt.isEnabled(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES));
   }

   public Collection<SettableBeanProperty> properties() {
      return this._propertyLookup.values();
   }

   public SettableBeanProperty findCreatorProperty(String name) {
      return (SettableBeanProperty)this._propertyLookup.get(name);
   }

   public SettableBeanProperty findCreatorProperty(int propertyIndex) {
      Iterator i$ = this._propertyLookup.values().iterator();

      SettableBeanProperty prop;
      do {
         if (!i$.hasNext()) {
            return null;
         }

         prop = (SettableBeanProperty)i$.next();
      } while(prop.getPropertyIndex() != propertyIndex);

      return prop;
   }

   public PropertyValueBuffer startBuilding(JsonParser p, DeserializationContext ctxt, ObjectIdReader oir) {
      return new PropertyValueBuffer(p, ctxt, this._propertyCount, oir);
   }

   public Object build(DeserializationContext ctxt, PropertyValueBuffer buffer) throws IOException {
      Object bean = this._valueInstantiator.createFromObjectWith(ctxt, this._allProperties, buffer);
      if (bean != null) {
         bean = buffer.handleIdValue(ctxt, bean);

         for(PropertyValue pv = buffer.buffered(); pv != null; pv = pv.next) {
            pv.assign(bean);
         }
      }

      return bean;
   }

   static class CaseInsensitiveMap extends HashMap<String, SettableBeanProperty> {
      private static final long serialVersionUID = 1L;

      public SettableBeanProperty get(Object key0) {
         return (SettableBeanProperty)super.get(((String)key0).toLowerCase());
      }

      public SettableBeanProperty put(String key, SettableBeanProperty value) {
         key = key.toLowerCase();
         return (SettableBeanProperty)super.put(key, value);
      }
   }
}
