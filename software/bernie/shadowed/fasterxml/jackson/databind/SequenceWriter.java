package software.bernie.shadowed.fasterxml.jackson.databind;

import java.io.Closeable;
import java.io.Flushable;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import software.bernie.shadowed.fasterxml.jackson.core.JsonGenerator;
import software.bernie.shadowed.fasterxml.jackson.core.Version;
import software.bernie.shadowed.fasterxml.jackson.core.Versioned;
import software.bernie.shadowed.fasterxml.jackson.databind.cfg.PackageVersion;
import software.bernie.shadowed.fasterxml.jackson.databind.jsontype.TypeSerializer;
import software.bernie.shadowed.fasterxml.jackson.databind.ser.DefaultSerializerProvider;
import software.bernie.shadowed.fasterxml.jackson.databind.ser.impl.PropertySerializerMap;
import software.bernie.shadowed.fasterxml.jackson.databind.ser.impl.TypeWrappedSerializer;

public class SequenceWriter implements Versioned, Closeable, Flushable {
   protected final DefaultSerializerProvider _provider;
   protected final SerializationConfig _config;
   protected final JsonGenerator _generator;
   protected final JsonSerializer<Object> _rootSerializer;
   protected final TypeSerializer _typeSerializer;
   protected final boolean _closeGenerator;
   protected final boolean _cfgFlush;
   protected final boolean _cfgCloseCloseable;
   protected PropertySerializerMap _dynamicSerializers;
   protected boolean _openArray;
   protected boolean _closed;

   public SequenceWriter(DefaultSerializerProvider prov, JsonGenerator gen, boolean closeGenerator, ObjectWriter.Prefetch prefetch) throws IOException {
      this._provider = prov;
      this._generator = gen;
      this._closeGenerator = closeGenerator;
      this._rootSerializer = prefetch.getValueSerializer();
      this._typeSerializer = prefetch.getTypeSerializer();
      this._config = prov.getConfig();
      this._cfgFlush = this._config.isEnabled(SerializationFeature.FLUSH_AFTER_WRITE_VALUE);
      this._cfgCloseCloseable = this._config.isEnabled(SerializationFeature.CLOSE_CLOSEABLE);
      this._dynamicSerializers = PropertySerializerMap.emptyForRootValues();
   }

   public SequenceWriter init(boolean wrapInArray) throws IOException {
      if (wrapInArray) {
         this._generator.writeStartArray();
         this._openArray = true;
      }

      return this;
   }

   public Version version() {
      return PackageVersion.VERSION;
   }

   public SequenceWriter write(Object value) throws IOException {
      if (value == null) {
         this._provider.serializeValue(this._generator, (Object)null);
         return this;
      } else if (this._cfgCloseCloseable && value instanceof Closeable) {
         return this._writeCloseableValue(value);
      } else {
         JsonSerializer<Object> ser = this._rootSerializer;
         if (ser == null) {
            Class<?> type = value.getClass();
            ser = this._dynamicSerializers.serializerFor(type);
            if (ser == null) {
               ser = this._findAndAddDynamic(type);
            }
         }

         this._provider.serializeValue(this._generator, value, (JavaType)null, ser);
         if (this._cfgFlush) {
            this._generator.flush();
         }

         return this;
      }
   }

   public SequenceWriter write(Object value, JavaType type) throws IOException {
      if (value == null) {
         this._provider.serializeValue(this._generator, (Object)null);
         return this;
      } else if (this._cfgCloseCloseable && value instanceof Closeable) {
         return this._writeCloseableValue(value, type);
      } else {
         JsonSerializer<Object> ser = this._dynamicSerializers.serializerFor(type.getRawClass());
         if (ser == null) {
            ser = this._findAndAddDynamic(type);
         }

         this._provider.serializeValue(this._generator, value, type, ser);
         if (this._cfgFlush) {
            this._generator.flush();
         }

         return this;
      }
   }

   public SequenceWriter writeAll(Object[] value) throws IOException {
      int i = 0;

      for(int len = value.length; i < len; ++i) {
         this.write(value[i]);
      }

      return this;
   }

   public <C extends Collection<?>> SequenceWriter writeAll(C container) throws IOException {
      Iterator i$ = container.iterator();

      while(i$.hasNext()) {
         Object value = i$.next();
         this.write(value);
      }

      return this;
   }

   public SequenceWriter writeAll(Iterable<?> iterable) throws IOException {
      Iterator i$ = iterable.iterator();

      while(i$.hasNext()) {
         Object value = i$.next();
         this.write(value);
      }

      return this;
   }

   public void flush() throws IOException {
      if (!this._closed) {
         this._generator.flush();
      }

   }

   public void close() throws IOException {
      if (!this._closed) {
         this._closed = true;
         if (this._openArray) {
            this._openArray = false;
            this._generator.writeEndArray();
         }

         if (this._closeGenerator) {
            this._generator.close();
         }
      }

   }

   protected SequenceWriter _writeCloseableValue(Object value) throws IOException {
      Closeable toClose = (Closeable)value;

      try {
         JsonSerializer<Object> ser = this._rootSerializer;
         if (ser == null) {
            Class<?> type = value.getClass();
            ser = this._dynamicSerializers.serializerFor(type);
            if (ser == null) {
               ser = this._findAndAddDynamic(type);
            }
         }

         this._provider.serializeValue(this._generator, value, (JavaType)null, ser);
         if (this._cfgFlush) {
            this._generator.flush();
         }

         Closeable tmpToClose = toClose;
         toClose = null;
         tmpToClose.close();
      } finally {
         if (toClose != null) {
            try {
               toClose.close();
            } catch (IOException var10) {
            }
         }

      }

      return this;
   }

   protected SequenceWriter _writeCloseableValue(Object value, JavaType type) throws IOException {
      Closeable toClose = (Closeable)value;

      try {
         JsonSerializer<Object> ser = this._dynamicSerializers.serializerFor(type.getRawClass());
         if (ser == null) {
            ser = this._findAndAddDynamic(type);
         }

         this._provider.serializeValue(this._generator, value, type, ser);
         if (this._cfgFlush) {
            this._generator.flush();
         }

         Closeable tmpToClose = toClose;
         toClose = null;
         tmpToClose.close();
      } finally {
         if (toClose != null) {
            try {
               toClose.close();
            } catch (IOException var11) {
            }
         }

      }

      return this;
   }

   private final JsonSerializer<Object> _findAndAddDynamic(Class<?> type) throws JsonMappingException {
      PropertySerializerMap.SerializerAndMapResult result;
      if (this._typeSerializer == null) {
         result = this._dynamicSerializers.findAndAddRootValueSerializer((Class)type, this._provider);
      } else {
         result = this._dynamicSerializers.addSerializer((Class)type, new TypeWrappedSerializer(this._typeSerializer, this._provider.findValueSerializer(type, (BeanProperty)null)));
      }

      this._dynamicSerializers = result.map;
      return result.serializer;
   }

   private final JsonSerializer<Object> _findAndAddDynamic(JavaType type) throws JsonMappingException {
      PropertySerializerMap.SerializerAndMapResult result;
      if (this._typeSerializer == null) {
         result = this._dynamicSerializers.findAndAddRootValueSerializer((JavaType)type, this._provider);
      } else {
         result = this._dynamicSerializers.addSerializer((JavaType)type, new TypeWrappedSerializer(this._typeSerializer, this._provider.findValueSerializer(type, (BeanProperty)null)));
      }

      this._dynamicSerializers = result.map;
      return result.serializer;
   }
}
