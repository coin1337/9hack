package software.bernie.shadowed.fasterxml.jackson.databind.deser.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import software.bernie.shadowed.fasterxml.jackson.core.JsonParser;
import software.bernie.shadowed.fasterxml.jackson.databind.DeserializationContext;
import software.bernie.shadowed.fasterxml.jackson.databind.JsonDeserializer;
import software.bernie.shadowed.fasterxml.jackson.databind.deser.SettableBeanProperty;
import software.bernie.shadowed.fasterxml.jackson.databind.util.NameTransformer;
import software.bernie.shadowed.fasterxml.jackson.databind.util.TokenBuffer;

public class UnwrappedPropertyHandler {
   protected final List<SettableBeanProperty> _properties;

   public UnwrappedPropertyHandler() {
      this._properties = new ArrayList();
   }

   protected UnwrappedPropertyHandler(List<SettableBeanProperty> props) {
      this._properties = props;
   }

   public void addProperty(SettableBeanProperty property) {
      this._properties.add(property);
   }

   public UnwrappedPropertyHandler renameAll(NameTransformer transformer) {
      ArrayList<SettableBeanProperty> newProps = new ArrayList(this._properties.size());

      SettableBeanProperty prop;
      for(Iterator i$ = this._properties.iterator(); i$.hasNext(); newProps.add(prop)) {
         prop = (SettableBeanProperty)i$.next();
         String newName = transformer.transform(prop.getName());
         prop = prop.withSimpleName(newName);
         JsonDeserializer<?> deser = prop.getValueDeserializer();
         if (deser != null) {
            JsonDeserializer<Object> newDeser = deser.unwrappingDeserializer(transformer);
            if (newDeser != deser) {
               prop = prop.withValueDeserializer(newDeser);
            }
         }
      }

      return new UnwrappedPropertyHandler(newProps);
   }

   public Object processUnwrapped(JsonParser originalParser, DeserializationContext ctxt, Object bean, TokenBuffer buffered) throws IOException {
      int i = 0;

      for(int len = this._properties.size(); i < len; ++i) {
         SettableBeanProperty prop = (SettableBeanProperty)this._properties.get(i);
         JsonParser p = buffered.asParser();
         p.nextToken();
         prop.deserializeAndSet(p, ctxt, bean);
      }

      return bean;
   }
}
