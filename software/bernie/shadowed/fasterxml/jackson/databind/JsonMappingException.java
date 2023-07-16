package software.bernie.shadowed.fasterxml.jackson.databind;

import java.io.Closeable;
import java.io.IOException;
import java.io.Serializable;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import software.bernie.shadowed.fasterxml.jackson.annotation.JsonIgnore;
import software.bernie.shadowed.fasterxml.jackson.core.JsonGenerator;
import software.bernie.shadowed.fasterxml.jackson.core.JsonLocation;
import software.bernie.shadowed.fasterxml.jackson.core.JsonParser;
import software.bernie.shadowed.fasterxml.jackson.core.JsonProcessingException;

public class JsonMappingException extends JsonProcessingException {
   private static final long serialVersionUID = 1L;
   static final int MAX_REFS_TO_LIST = 1000;
   protected LinkedList<JsonMappingException.Reference> _path;
   protected transient Closeable _processor;

   /** @deprecated */
   @Deprecated
   public JsonMappingException(String msg) {
      super(msg);
   }

   /** @deprecated */
   @Deprecated
   public JsonMappingException(String msg, Throwable rootCause) {
      super(msg, rootCause);
   }

   /** @deprecated */
   @Deprecated
   public JsonMappingException(String msg, JsonLocation loc) {
      super(msg, loc);
   }

   /** @deprecated */
   @Deprecated
   public JsonMappingException(String msg, JsonLocation loc, Throwable rootCause) {
      super(msg, loc, rootCause);
   }

   public JsonMappingException(Closeable processor, String msg) {
      super(msg);
      this._processor = processor;
      if (processor instanceof JsonParser) {
         this._location = ((JsonParser)processor).getTokenLocation();
      }

   }

   public JsonMappingException(Closeable processor, String msg, Throwable problem) {
      super(msg, problem);
      this._processor = processor;
      if (processor instanceof JsonParser) {
         this._location = ((JsonParser)processor).getTokenLocation();
      }

   }

   public JsonMappingException(Closeable processor, String msg, JsonLocation loc) {
      super(msg, loc);
      this._processor = processor;
   }

   public static JsonMappingException from(JsonParser p, String msg) {
      return new JsonMappingException(p, msg);
   }

   public static JsonMappingException from(JsonParser p, String msg, Throwable problem) {
      return new JsonMappingException(p, msg, problem);
   }

   public static JsonMappingException from(JsonGenerator g, String msg) {
      return new JsonMappingException(g, msg, (Throwable)null);
   }

   public static JsonMappingException from(JsonGenerator g, String msg, Throwable problem) {
      return new JsonMappingException(g, msg, problem);
   }

   public static JsonMappingException from(DeserializationContext ctxt, String msg) {
      return new JsonMappingException(ctxt.getParser(), msg);
   }

   public static JsonMappingException from(DeserializationContext ctxt, String msg, Throwable t) {
      return new JsonMappingException(ctxt.getParser(), msg, t);
   }

   public static JsonMappingException from(SerializerProvider ctxt, String msg) {
      return new JsonMappingException(ctxt.getGenerator(), msg);
   }

   public static JsonMappingException from(SerializerProvider ctxt, String msg, Throwable problem) {
      return new JsonMappingException(ctxt.getGenerator(), msg, problem);
   }

   public static JsonMappingException fromUnexpectedIOE(IOException src) {
      return new JsonMappingException((Closeable)null, String.format("Unexpected IOException (of type %s): %s", src.getClass().getName(), src.getMessage()));
   }

   public static JsonMappingException wrapWithPath(Throwable src, Object refFrom, String refFieldName) {
      return wrapWithPath(src, new JsonMappingException.Reference(refFrom, refFieldName));
   }

   public static JsonMappingException wrapWithPath(Throwable src, Object refFrom, int index) {
      return wrapWithPath(src, new JsonMappingException.Reference(refFrom, index));
   }

   public static JsonMappingException wrapWithPath(Throwable src, JsonMappingException.Reference ref) {
      JsonMappingException jme;
      if (src instanceof JsonMappingException) {
         jme = (JsonMappingException)src;
      } else {
         String msg = src.getMessage();
         if (msg == null || msg.length() == 0) {
            msg = "(was " + src.getClass().getName() + ")";
         }

         Closeable proc = null;
         if (src instanceof JsonProcessingException) {
            Object proc0 = ((JsonProcessingException)src).getProcessor();
            if (proc0 instanceof Closeable) {
               proc = (Closeable)proc0;
            }
         }

         jme = new JsonMappingException(proc, msg, src);
      }

      jme.prependPath(ref);
      return jme;
   }

   public List<JsonMappingException.Reference> getPath() {
      return this._path == null ? Collections.emptyList() : Collections.unmodifiableList(this._path);
   }

   public String getPathReference() {
      return this.getPathReference(new StringBuilder()).toString();
   }

   public StringBuilder getPathReference(StringBuilder sb) {
      this._appendPathDesc(sb);
      return sb;
   }

   public void prependPath(Object referrer, String fieldName) {
      JsonMappingException.Reference ref = new JsonMappingException.Reference(referrer, fieldName);
      this.prependPath(ref);
   }

   public void prependPath(Object referrer, int index) {
      JsonMappingException.Reference ref = new JsonMappingException.Reference(referrer, index);
      this.prependPath(ref);
   }

   public void prependPath(JsonMappingException.Reference r) {
      if (this._path == null) {
         this._path = new LinkedList();
      }

      if (this._path.size() < 1000) {
         this._path.addFirst(r);
      }

   }

   @JsonIgnore
   public Object getProcessor() {
      return this._processor;
   }

   public String getLocalizedMessage() {
      return this._buildMessage();
   }

   public String getMessage() {
      return this._buildMessage();
   }

   protected String _buildMessage() {
      String msg = super.getMessage();
      if (this._path == null) {
         return msg;
      } else {
         StringBuilder sb = msg == null ? new StringBuilder() : new StringBuilder(msg);
         sb.append(" (through reference chain: ");
         sb = this.getPathReference(sb);
         sb.append(')');
         return sb.toString();
      }
   }

   public String toString() {
      return this.getClass().getName() + ": " + this.getMessage();
   }

   protected void _appendPathDesc(StringBuilder sb) {
      if (this._path != null) {
         Iterator it = this._path.iterator();

         while(it.hasNext()) {
            sb.append(((JsonMappingException.Reference)it.next()).toString());
            if (it.hasNext()) {
               sb.append("->");
            }
         }

      }
   }

   public static class Reference implements Serializable {
      private static final long serialVersionUID = 2L;
      protected transient Object _from;
      protected String _fieldName;
      protected int _index = -1;
      protected String _desc;

      protected Reference() {
      }

      public Reference(Object from) {
         this._from = from;
      }

      public Reference(Object from, String fieldName) {
         this._from = from;
         if (fieldName == null) {
            throw new NullPointerException("Cannot pass null fieldName");
         } else {
            this._fieldName = fieldName;
         }
      }

      public Reference(Object from, int index) {
         this._from = from;
         this._index = index;
      }

      void setFieldName(String n) {
         this._fieldName = n;
      }

      void setIndex(int ix) {
         this._index = ix;
      }

      void setDescription(String d) {
         this._desc = d;
      }

      @JsonIgnore
      public Object getFrom() {
         return this._from;
      }

      public String getFieldName() {
         return this._fieldName;
      }

      public int getIndex() {
         return this._index;
      }

      public String getDescription() {
         if (this._desc == null) {
            StringBuilder sb = new StringBuilder();
            if (this._from == null) {
               sb.append("UNKNOWN");
            } else {
               Class<?> cls = this._from instanceof Class ? (Class)this._from : this._from.getClass();

               int arrays;
               for(arrays = 0; cls.isArray(); ++arrays) {
                  cls = cls.getComponentType();
               }

               sb.append(cls.getName());

               while(true) {
                  --arrays;
                  if (arrays < 0) {
                     break;
                  }

                  sb.append("[]");
               }
            }

            sb.append('[');
            if (this._fieldName != null) {
               sb.append('"');
               sb.append(this._fieldName);
               sb.append('"');
            } else if (this._index >= 0) {
               sb.append(this._index);
            } else {
               sb.append('?');
            }

            sb.append(']');
            this._desc = sb.toString();
         }

         return this._desc;
      }

      public String toString() {
         return this.getDescription();
      }

      Object writeReplace() {
         this.getDescription();
         return this;
      }
   }
}
