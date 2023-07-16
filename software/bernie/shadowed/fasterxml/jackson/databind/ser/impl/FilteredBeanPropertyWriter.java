package software.bernie.shadowed.fasterxml.jackson.databind.ser.impl;

import java.io.Serializable;
import software.bernie.shadowed.fasterxml.jackson.core.JsonGenerator;
import software.bernie.shadowed.fasterxml.jackson.databind.JsonMappingException;
import software.bernie.shadowed.fasterxml.jackson.databind.JsonSerializer;
import software.bernie.shadowed.fasterxml.jackson.databind.SerializerProvider;
import software.bernie.shadowed.fasterxml.jackson.databind.jsonFormatVisitors.JsonObjectFormatVisitor;
import software.bernie.shadowed.fasterxml.jackson.databind.ser.BeanPropertyWriter;
import software.bernie.shadowed.fasterxml.jackson.databind.util.NameTransformer;

public abstract class FilteredBeanPropertyWriter {
   public static BeanPropertyWriter constructViewBased(BeanPropertyWriter base, Class<?>[] viewsToIncludeIn) {
      return (BeanPropertyWriter)(viewsToIncludeIn.length == 1 ? new FilteredBeanPropertyWriter.SingleView(base, viewsToIncludeIn[0]) : new FilteredBeanPropertyWriter.MultiView(base, viewsToIncludeIn));
   }

   private static final class MultiView extends BeanPropertyWriter implements Serializable {
      private static final long serialVersionUID = 1L;
      protected final BeanPropertyWriter _delegate;
      protected final Class<?>[] _views;

      protected MultiView(BeanPropertyWriter delegate, Class<?>[] views) {
         super(delegate);
         this._delegate = delegate;
         this._views = views;
      }

      public FilteredBeanPropertyWriter.MultiView rename(NameTransformer transformer) {
         return new FilteredBeanPropertyWriter.MultiView(this._delegate.rename(transformer), this._views);
      }

      public void assignSerializer(JsonSerializer<Object> ser) {
         this._delegate.assignSerializer(ser);
      }

      public void assignNullSerializer(JsonSerializer<Object> nullSer) {
         this._delegate.assignNullSerializer(nullSer);
      }

      public void serializeAsField(Object bean, JsonGenerator gen, SerializerProvider prov) throws Exception {
         if (this._inView(prov.getActiveView())) {
            this._delegate.serializeAsField(bean, gen, prov);
         } else {
            this._delegate.serializeAsOmittedField(bean, gen, prov);
         }
      }

      public void serializeAsElement(Object bean, JsonGenerator gen, SerializerProvider prov) throws Exception {
         if (this._inView(prov.getActiveView())) {
            this._delegate.serializeAsElement(bean, gen, prov);
         } else {
            this._delegate.serializeAsPlaceholder(bean, gen, prov);
         }
      }

      public void depositSchemaProperty(JsonObjectFormatVisitor v, SerializerProvider provider) throws JsonMappingException {
         if (this._inView(provider.getActiveView())) {
            super.depositSchemaProperty(v, provider);
         }

      }

      private final boolean _inView(Class<?> activeView) {
         if (activeView == null) {
            return true;
         } else {
            int len = this._views.length;

            for(int i = 0; i < len; ++i) {
               if (this._views[i].isAssignableFrom(activeView)) {
                  return true;
               }
            }

            return false;
         }
      }
   }

   private static final class SingleView extends BeanPropertyWriter implements Serializable {
      private static final long serialVersionUID = 1L;
      protected final BeanPropertyWriter _delegate;
      protected final Class<?> _view;

      protected SingleView(BeanPropertyWriter delegate, Class<?> view) {
         super(delegate);
         this._delegate = delegate;
         this._view = view;
      }

      public FilteredBeanPropertyWriter.SingleView rename(NameTransformer transformer) {
         return new FilteredBeanPropertyWriter.SingleView(this._delegate.rename(transformer), this._view);
      }

      public void assignSerializer(JsonSerializer<Object> ser) {
         this._delegate.assignSerializer(ser);
      }

      public void assignNullSerializer(JsonSerializer<Object> nullSer) {
         this._delegate.assignNullSerializer(nullSer);
      }

      public void serializeAsField(Object bean, JsonGenerator gen, SerializerProvider prov) throws Exception {
         Class<?> activeView = prov.getActiveView();
         if (activeView != null && !this._view.isAssignableFrom(activeView)) {
            this._delegate.serializeAsOmittedField(bean, gen, prov);
         } else {
            this._delegate.serializeAsField(bean, gen, prov);
         }

      }

      public void serializeAsElement(Object bean, JsonGenerator gen, SerializerProvider prov) throws Exception {
         Class<?> activeView = prov.getActiveView();
         if (activeView != null && !this._view.isAssignableFrom(activeView)) {
            this._delegate.serializeAsPlaceholder(bean, gen, prov);
         } else {
            this._delegate.serializeAsElement(bean, gen, prov);
         }

      }

      public void depositSchemaProperty(JsonObjectFormatVisitor v, SerializerProvider provider) throws JsonMappingException {
         Class<?> activeView = provider.getActiveView();
         if (activeView == null || this._view.isAssignableFrom(activeView)) {
            super.depositSchemaProperty(v, provider);
         }

      }
   }
}
