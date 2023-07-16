package software.bernie.shadowed.fasterxml.jackson.databind;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import software.bernie.shadowed.fasterxml.jackson.core.FormatSchema;
import software.bernie.shadowed.fasterxml.jackson.core.JsonLocation;
import software.bernie.shadowed.fasterxml.jackson.core.JsonParser;
import software.bernie.shadowed.fasterxml.jackson.core.JsonStreamContext;
import software.bernie.shadowed.fasterxml.jackson.core.JsonToken;

public class MappingIterator<T> implements Iterator<T>, Closeable {
   protected static final MappingIterator<?> EMPTY_ITERATOR = new MappingIterator((JavaType)null, (JsonParser)null, (DeserializationContext)null, (JsonDeserializer)null, false, (Object)null);
   protected static final int STATE_CLOSED = 0;
   protected static final int STATE_NEED_RESYNC = 1;
   protected static final int STATE_MAY_HAVE_VALUE = 2;
   protected static final int STATE_HAS_VALUE = 3;
   protected final JavaType _type;
   protected final DeserializationContext _context;
   protected final JsonDeserializer<T> _deserializer;
   protected final JsonParser _parser;
   protected final JsonStreamContext _seqContext;
   protected final T _updatedValue;
   protected final boolean _closeParser;
   protected int _state;

   protected MappingIterator(JavaType type, JsonParser p, DeserializationContext ctxt, JsonDeserializer<?> deser, boolean managedParser, Object valueToUpdate) {
      this._type = type;
      this._parser = p;
      this._context = ctxt;
      this._deserializer = deser;
      this._closeParser = managedParser;
      if (valueToUpdate == null) {
         this._updatedValue = null;
      } else {
         this._updatedValue = valueToUpdate;
      }

      if (p == null) {
         this._seqContext = null;
         this._state = 0;
      } else {
         JsonStreamContext sctxt = p.getParsingContext();
         if (managedParser && p.isExpectedStartArrayToken()) {
            p.clearCurrentToken();
         } else {
            JsonToken t = p.getCurrentToken();
            if (t == JsonToken.START_OBJECT || t == JsonToken.START_ARRAY) {
               sctxt = sctxt.getParent();
            }
         }

         this._seqContext = sctxt;
         this._state = 2;
      }

   }

   protected static <T> MappingIterator<T> emptyIterator() {
      return EMPTY_ITERATOR;
   }

   public boolean hasNext() {
      try {
         return this.hasNextValue();
      } catch (JsonMappingException var2) {
         return (Boolean)this._handleMappingException(var2);
      } catch (IOException var3) {
         return (Boolean)this._handleIOException(var3);
      }
   }

   public T next() {
      try {
         return this.nextValue();
      } catch (JsonMappingException var2) {
         throw new RuntimeJsonMappingException(var2.getMessage(), var2);
      } catch (IOException var3) {
         throw new RuntimeException(var3.getMessage(), var3);
      }
   }

   public void remove() {
      throw new UnsupportedOperationException();
   }

   public void close() throws IOException {
      if (this._state != 0) {
         this._state = 0;
         if (this._parser != null) {
            this._parser.close();
         }
      }

   }

   public boolean hasNextValue() throws IOException {
      switch(this._state) {
      case 0:
         return false;
      case 1:
         this._resync();
      case 2:
         break;
      case 3:
      default:
         return true;
      }

      JsonToken t = this._parser.getCurrentToken();
      if (t == null) {
         t = this._parser.nextToken();
         if (t == null || t == JsonToken.END_ARRAY) {
            this._state = 0;
            if (this._closeParser && this._parser != null) {
               this._parser.close();
            }

            return false;
         }
      }

      this._state = 3;
      return true;
   }

   public T nextValue() throws IOException {
      switch(this._state) {
      case 0:
         return this._throwNoSuchElement();
      case 1:
      case 2:
         if (!this.hasNextValue()) {
            return this._throwNoSuchElement();
         }
      case 3:
      default:
         byte nextState = 1;

         Object var3;
         try {
            Object value;
            if (this._updatedValue == null) {
               value = this._deserializer.deserialize(this._parser, this._context);
            } else {
               this._deserializer.deserialize(this._parser, this._context, this._updatedValue);
               value = this._updatedValue;
            }

            nextState = 2;
            var3 = value;
         } finally {
            this._state = nextState;
            this._parser.clearCurrentToken();
         }

         return var3;
      }
   }

   public List<T> readAll() throws IOException {
      return this.readAll((List)(new ArrayList()));
   }

   public <L extends List<? super T>> L readAll(L resultList) throws IOException {
      while(this.hasNextValue()) {
         resultList.add(this.nextValue());
      }

      return resultList;
   }

   public <C extends Collection<? super T>> C readAll(C results) throws IOException {
      while(this.hasNextValue()) {
         results.add(this.nextValue());
      }

      return results;
   }

   public JsonParser getParser() {
      return this._parser;
   }

   public FormatSchema getParserSchema() {
      return this._parser.getSchema();
   }

   public JsonLocation getCurrentLocation() {
      return this._parser.getCurrentLocation();
   }

   protected void _resync() throws IOException {
      JsonParser p = this._parser;
      if (p.getParsingContext() != this._seqContext) {
         JsonToken t;
         label30:
         do {
            while(true) {
               while(true) {
                  t = p.nextToken();
                  if (t != JsonToken.END_ARRAY && t != JsonToken.END_OBJECT) {
                     if (t != JsonToken.START_ARRAY && t != JsonToken.START_OBJECT) {
                        continue label30;
                     }

                     p.skipChildren();
                  } else if (p.getParsingContext() == this._seqContext) {
                     p.clearCurrentToken();
                     return;
                  }
               }
            }
         } while(t != null);

      }
   }

   protected <R> R _throwNoSuchElement() {
      throw new NoSuchElementException();
   }

   protected <R> R _handleMappingException(JsonMappingException e) {
      throw new RuntimeJsonMappingException(e.getMessage(), e);
   }

   protected <R> R _handleIOException(IOException e) {
      throw new RuntimeException(e.getMessage(), e);
   }
}
